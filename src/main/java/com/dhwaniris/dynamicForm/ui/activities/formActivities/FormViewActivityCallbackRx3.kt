package com.dhwaniris.dynamicForm.ui.activities.formActivities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.widget.Toolbar
import androidx.core.util.Pair
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig
import com.dhwaniris.dynamicForm.R
import com.dhwaniris.dynamicForm.SingletonSubmitForm
import com.dhwaniris.dynamicForm.adapters.UnansweredQusAdapter
import com.dhwaniris.dynamicForm.base.BaseActivity
import com.dhwaniris.dynamicForm.db.FilledForms
import com.dhwaniris.dynamicForm.db.dbhelper.LocationBean
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled
import com.dhwaniris.dynamicForm.db.dbhelper.form.Form
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean
import com.dhwaniris.dynamicForm.interfaces.UnansweredListener
import com.dhwaniris.dynamicForm.locationservice.LocationUpdatesService
import com.dhwaniris.dynamicForm.utils.*
import com.dhwaniris.dynamicForm.utils.QuestionsUtils.Companion.getAnswerUniqueId
import com.dhwaniris.dynamicForm.utils.QuestionsUtils.Companion.getQuestionUniqueId
import com.dhwaniris.dynamicForm.utils.QuestionsUtils.Companion.isItHasAns
import com.dhwaniris.dynamicForm.utils.QuestionsUtils.Companion.isLoopingType
import com.dhwaniris.dynamicForm.utils.QuestionsUtils.Companion.sortAnsList
import com.dhwaniris.dynamicForm.utils.QuestionsUtils.Companion.sortQusList
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Used for location after submitting ok of Dialog (Location is Mandatory in form)
 */
class FormViewActivityCallbackRx3 : BaseFormActivity(), View.OnClickListener
        , PermissionHandlerListener, LocationHandlerListener {
    lateinit var compositeDisposable: CompositeDisposable

    @Volatile
    private var isDestroyedLocal = false
    private var longitutde = "0.0"
    private var latitude = "0.0"
    private var accuracy = "0.0"
    private var time: Long = 0
    private var lastTimestamp: Long = 0
    protected var isErrorViewRequired = false

    private var isListLoaded = false
    private var formModel: Form? = null

    private lateinit var ctx: Context
    private val toolbar: Toolbar? by lazy { findViewById<Toolbar?>(R.id.toolbar) }
    private val save: Button? by lazy { findViewById<Button?>(R.id.save) }
    private val submit: Button? by lazy { findViewById<Button?>(R.id.submit) }

    private var singletonForm: SingletonSubmitForm? = null
    private var titleInLanguage: String? = null

    private var delayedLocation: Boolean = false

    private var isValidateCalled = false
    /**isValidateCalled Variable for delayed location calls*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        isDestroyedLocal = false
        isValidateCalled = false
        setContentView(R.layout.activity_form)
        ctx = this
        superViewBind()

        initVariables()
        initClickListeners()
    }

    private fun initClickListeners() {
        count.setOnClickListener(this)
    }

    private fun initVariables() {
        filledFormList = FilledForms()
        questionBeenList = LinkedHashMap()
        childQuestionBeenList = LinkedHashMap()

        lastTimestamp = System.currentTimeMillis()
        locationHandler = LocationHandler(ctx)
        permissionHandler = PermissionHandler(ctx, this)
        locationReceiver = LocationReceiver(locationDataN)
        locationHandler.setGPSonOffListener(this)
        save?.visibility = View.GONE
        setUpToolbar()

        singletonForm = SingletonSubmitForm.getInstance()
        if (singletonForm?.form == null) {
            myFinishActivity(true)
        } else {
            prepareQuestionView()
        }
    }

    private fun setUpToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener { onBackPressed() }
        }
    }

    override fun onStart() {
        super.onStart()
        locationDataN.observe(this, androidx.lifecycle.Observer {
            if (delayedLocation && isValidateCalled) {
                hideLoader()
                AlertDialog.Builder(this)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes) { dialogInterface, i ->
                            loadUploadData(LibDynamicAppConfig.SUBMITTED, true)
//                            uploadtask = UpdateDataData(LibDynamicAppConfig.SUBMITTED, true)
//                            uploadtask!!.execute()
                        }
                        .setNegativeButton(R.string.no, null)
                        .show()
            }
        })
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
                IntentFilter(LocationUpdatesService.ACTION_BROADCAST))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver)
        hideProgess()
        super.onStop()
    }

    fun prepareQuestionsRx() {
        showProgress(ctx, getString(R.string.preparing_form))
        formModel = singletonForm?.form
        val jsonObject: JSONObject? = singletonForm?.jsonObject
        var uploadStatus = 0
        if (jsonObject != null) {
            try {
                uploadStatus = jsonObject.getInt(Constant.UPLOAD_STATUS)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            when {
                uploadStatus == LibDynamicAppConfig.SUBMITTED -> {
                    formStatus = LibDynamicAppConfig.SUBMITTED
                    save?.visibility = View.GONE
                    submit?.visibility = View.GONE
                }
                filledFormList.upload_status == LibDynamicAppConfig.SYNCED || filledFormList.upload_status == LibDynamicAppConfig.SYNCED_BUT_EDITABLE || filledFormList.upload_status == LibDynamicAppConfig.EDITABLE_DARFT -> {
                    isErrorViewRequired = true
                    formStatus = filledFormList.upload_status
                    submit?.setOnClickListener(this@FormViewActivityCallbackRx3)
                }
                filledFormList.upload_status == LibDynamicAppConfig.EDITABLE_SUBMITTED -> {
                    formStatus = LibDynamicAppConfig.EDITABLE_SUBMITTED
                    submit?.visibility = View.GONE
                }
                filledFormList.upload_status == LibDynamicAppConfig.REJECTED_DUPLICATE -> {
                    formStatus = LibDynamicAppConfig.SUBMITTED
                    submit?.visibility = View.GONE
                }
                else -> {
                    formStatus = LibDynamicAppConfig.DRAFT
                    submit?.setOnClickListener(this@FormViewActivityCallbackRx3)
                }
            }
            time = java.lang.Long.parseLong(filledFormList.timeTaken)
        }
        val questionBeanList: MutableList<QuestionBean> = mutableListOf()
        formModel = singletonForm?.form
        if (jsonObject != null && formModel != null) {
            var userLanguage: String = userLanguage()
            var isFoundLanguage = false
            delayedLocation = formModel?.delayLocation ?: false
            val ss = Single.just(formModel)
                    .subscribeOn(Schedulers.computation())
                    .map {
                        form_id = it.formId
                        isLocationRequired = it.location
                        for (languageBean in it.language) {
                            if (languageBean.lng == userLanguage) {
                                isFoundLanguage = true
                                break
                            }
                        }
                        userLanguage = if (isFoundLanguage) userLanguage() else "en"
                        it.language
                    }.toObservable().flatMapIterable { it }
                    .takeUntil { it.lng == userLanguage }.filter { it.lng == userLanguage }
                    .map {
                        titleInLanguage = it.title
                        it
                    }.flatMapIterable { it.question }
                    .flatMap {
                        if (!it.order.contains(".")) {
                            questionBeanList.add(it)
                        } else {
                            childQuestionBeenList.put(getQuestionUniqueId(it), it)
                        }
                        Observable.just(it)
                    }.toList()
                    .doOnSuccess {
                        convertJsonDataToAnswer(questionBeanList, jsonObject, answerBeanHelperList)
                    }.map {
                        questionBeanList
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        supportActionBar?.title = titleInLanguage
                        hideLoader()
                        if (!questionBeanList.isEmpty()) {
                            checkRequiredStuff(questionBeanList)
                        } else {
                            showToast(R.string.something_went_wrong)
                            myFinishActivity(true)
                        }
                    }, { e -> Log.e("Error Json", "Some Error", e) })
            compositeDisposable.add(ss)
        }
    }

    fun prepareQuestionView() {
        prepareQuestionsRx()
    }

    internal fun checkRequiredStuff(questionBeanList: List<QuestionBean>) {
        sortQusList(questionBeanList)
        for (questionBean in questionBeanList) {
            questionBeenList[getQuestionUniqueId(questionBean)] = questionBean
        }
        if (questionBeenList.size > 0) {
            if (isLocationRequired && !delayedLocation) {
                if (permissionHandler.checkGpsPermission()) {
                    if (!isDestroyedLocal) locationHandler.startGpsService()
                    AddNewObjectView(questionBeenList)
                    saved = false
                } else {
                    permissionHandler.requestGpsPermission()
                }
            } else {
                AddNewObjectView(questionBeenList)
                saved = false
            }
        } else {
            saved = true
            linearLayout.visibility = View.GONE
            submit?.visibility = View.GONE
            save?.visibility = View.GONE
        }
    }

    private fun hideLoader() {
        hideLoading()
        if (!isListLoaded) {
            Handler().postDelayed({
                // hideLoading();
                hideProgess()
                layout.visibility = View.VISIBLE
                linearLayout.visibility = View.VISIBLE
            }, 1000)
        }
        isListLoaded = true
    }

    //adding dynamic view in LinearLayout
    private fun AddNewObjectView(questionBeanRealmList: LinkedHashMap<String?, QuestionBean?>?) {
        for (questionBean in questionBeanRealmList!!.values) {
            val answer = answerBeanHelperList[getQuestionUniqueId(questionBean!!)]
            if (formStatus == LibDynamicAppConfig.EDITABLE_DARFT && answer != null) {
                if (!answer.isFilled && answer.isRequired
                        && !isLoopingType(answer)) {
                    questionBean.isEditable = true
                }
            }
            if (answer != null) {
                if (!isItHasAns(answer.answer)) {
                    answer.isFilled = false
                    answer.isValidAns = false
                }
            }
            createViewObject(questionBean, formStatus)
        }
        changeAnswerBeanHelperTitle(questionBeenList, answerBeanHelperList, formStatus)
    }

    private fun changeAnswerBeanHelperTitle(questionBean: LinkedHashMap<String?, QuestionBean?>?, answerBeanHelperList: LinkedHashMap<String?, QuestionBeanFilled?>?,
                                            formStatus: Int) {
        if (formStatus == LibDynamicAppConfig.SYNCED_BUT_EDITABLE || formStatus == LibDynamicAppConfig.EDITABLE_DARFT || formStatus == LibDynamicAppConfig.EDITABLE_SUBMITTED) {
            for (questionBeanFilled in answerBeanHelperList!!.values) {
                val questionBean1 = questionBean?.get(getAnswerUniqueId(questionBeanFilled!!))
                if (questionBean1 != null) {
                    questionBeanFilled?.title = questionBean1.title
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (isLocationRequired) {
            if (delayedLocation) {
                if (isValidateCalled && permissionHandler.checkGpsPermission()) {
                    locationHandler.startGpsService()
                }
            } else
                if (permissionHandler.checkGpsPermission()) {
                    locationHandler.startGpsService()
                }
        }
        alreadyRequest = false
    }

    override fun onPause() {
        if (isLocationRequired) {
            locationHandler.onStop()
        }
        super.onPause()
    }

    override fun onBackPressed() {
        if (formStatus != LibDynamicAppConfig.SUBMITTED) {
            Builder(ctx)
//                    .setTitle(R.string.are_you_sure)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.yes) { dialogInterface, i -> myFinishActivity(true) }
                    .setNegativeButton(R.string.no) { dialogInterface, i -> }
                    .setCancelable(true)
                    .show()
        } else {
            myFinishActivity(true)
        }
    }


    internal fun myFinishActivity(isCanceled: Boolean) {
        if (isCanceled) {
            setResult(Activity.RESULT_CANCELED)
        } else {
            setResult(Activity.RESULT_OK)
        }
        finish()
    }


    override fun onClick(view: View?) {
        val i = view!!.id
        if (i == R.id.save) {
            if (requirdAlertMap.containsValue(true)) {
                val orderlist: List<String>? = checkRegexHashMap(requirdAlertMap)
                if (!orderlist!!.isEmpty()) {
                    val text: String? = answerBeanHelperList[orderlist[0]]!!.answer[0].value
                    checkAlert(text, questionBeenList[orderlist[0]])
                }
            } else if (focusOnEditext && !notifyOnchangeMap.isEmpty() && !validateOnChangeListeners()) {
                focusOnEditext = false
                AlertDialog.Builder(ctx)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.do_you_want_to_save_as_draft)
                        .setPositiveButton(R.string.yes) { dialogInterface, i -> }
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(true)
                        .show()
                return
            }
            //draft save action
            else if (getValidAnsCount() > 0) {
                AlertDialog.Builder(ctx)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.do_you_want_to_save_as_draft)
                        .setPositiveButton(R.string.yes) { dialogInterface, i -> }
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(true)
                        .show()
            } else {
                Snackbar.make(submit!!, R.string.please_fill_at_least_one_field, BaseTransientBottomBar.LENGTH_SHORT)
                        .setAction(R.string.ok, null)
                        .show()
            }
        } else if (i == R.id.submit) {//submit action

            validateData()
        } else if (i == R.id.count) {//submit action

            showUnansweredQuestions(ArrayList(answerBeanHelperList.values), true)
        }
    }

    //Validating data before save
    private fun validateData() {
        if (requirdAlertMap.containsValue(true)) {
            val orderlist: List<String?>? = checkRegexHashMap(requirdAlertMap)
            if (!orderlist!!.isEmpty()) {
                val text: String? = answerBeanHelperList[orderlist[0]]!!.answer[0].value
                checkAlert(text, questionBeenList[orderlist[0]])
            }
        } else if (focusOnEditext && !notifyOnchangeMap.isEmpty() && !validateOnChangeListeners()) {
            focusOnEditext = false
            refreshLoopingFiltersQuestions()
            val tempList: List<QuestionBeanFilled?>? = findInvalidAnswersList(answerBeanHelperList, questionObjectList)
            if (!tempList!!.isEmpty()) {
                showUnansweredQuestions(tempList, false)
            }
            return
        } else if (!answerBeanHelperList.isEmpty()) {
            isValidateCalled = true
            validateOnChangeListeners()
            refreshLoopingFiltersQuestions()
            val tempList: List<QuestionBeanFilled?>? = findInvalidAnswersList(answerBeanHelperList, questionObjectList)
            if (!tempList!!.isEmpty()) {
                showUnansweredQuestions(tempList, false)
            } else {
                if (delayedLocation) {
                    if (permissionHandler.checkGpsPermission()) {
                        showLoading()
                        locationHandler.startGpsService()
                        saved = false
                    } else {
                        permissionHandler.requestGpsPermission()
                    }
//                    if (!isDestroyedLocal) locationHandler.startGpsService()
                } else {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.save_data)
                            .setMessage(R.string.are_you_sure)
                            .setPositiveButton(R.string.yes) { dialogInterface, i ->
                                loadUploadData(LibDynamicAppConfig.SUBMITTED, true)
//                            uploadtask = UpdateDataData(LibDynamicAppConfig.SUBMITTED, true)
//                            uploadtask!!.execute()
                            }
                            .setNegativeButton(R.string.no, null)
                            .show()
                }
            }
        } else {
            BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT, "Invalid Form. Line No. 406"
                    , LibDynamicAppConfig.UNEXPECTED_ERROR, "FormViewActivity")
        }
    }

    //show unanswered question
    private fun showUnansweredQuestions(tempList: List<QuestionBeanFilled?>?, showAll: Boolean) {
        //creating dropdown selector

        val dialog = AlertDialog.Builder(this)
        val inflater: LayoutInflater? = layoutInflater
        val view: View? = inflater?.inflate(R.layout.dynamic_custom_selector_layout, null)
        val title = view?.findViewById<TextView?>(R.id.dTitle)
        val close = view?.findViewById<ImageView?>(R.id.dClose)
        val recyclerView: RecyclerView? = view?.findViewById(R.id.dRecycler)
        dialog.setView(view)
        title?.setText(R.string.pending_questions)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        val alertDialog: AlertDialog? = dialog.create()
        val adapter = UnansweredQusAdapter(unansweredListener, tempList, alertDialog, showAll, questionBeenList)
        recyclerView?.adapter = adapter
        close?.setOnClickListener { alertDialog!!.dismiss() }
        dialog.setPositiveButton(R.string.ok) { dialogInterface, i -> alertDialog!!.dismiss() }
        alertDialog!!.show()
    }

    fun loadUploadData(status: Int, isFinish: Boolean) {
        showLoading()
        compositeDisposable.add(Single.just(1)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    Single.fromCallable {
                        val locationBean = LocationBean()
                        val locationData: Location? = locationReceiver.locationData.value
                        if (locationData != null) {
                            longitutde = "" + locationData.longitude
                            latitude = "" + locationData.latitude
                            accuracy = "" + locationData.accuracy
                        }
                        locationBean.accuracy = accuracy
                        locationBean.lat = latitude
                        locationBean.lng = longitutde
                        val timestamp = System.currentTimeMillis()
                        time = time + timestamp - lastTimestamp
                        filledFormList.timeTaken = time.toString()
                        filledFormList.version = formModel?.version
                        filledFormList.formId = formModel?.formId.toString()
                        filledFormList.setMobileUpdatedAt("" + System.currentTimeMillis())
                        filledFormList.setUpload_status(status)
                        val answerFilledList: MutableList<QuestionBeanFilled> = mutableListOf()
                        answerFilledList.addAll(answerBeanHelperList.values)
                        sortAnsList(answerFilledList)
                        filledFormList.setQuestion(answerFilledList)
                        val jsonObject: JSONObject = singletonForm?.getJsonObject() ?: JSONObject()
                        val answerMapper = HashMap<String?, Boolean?>()
                        modifyAnswerJson(jsonObject, answerMapper)
                        try {
                            jsonObject.put(Constant.TIME_TAKKEN, time.toString())
                            if (formModel?.isLocation() == true) {
                                val locationJsonObject = JSONObject()
                                locationJsonObject.put("lat", locationBean.lat)
                                locationJsonObject.put("lng", locationBean.lng)
                                locationJsonObject.put("accuracy", locationBean.accuracy)
                                jsonObject.put(Constant.LOCATION, locationJsonObject)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        singletonForm?.jsonObject = jsonObject
                        true
                    }
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (singletonForm?.workOnSubmit != null) {
                        singletonForm?.getWorkOnSubmit()?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
                                ?.subscribe(object : SingleObserver<Pair<Boolean?, String?>?> {
                                    override fun onSubscribe(d: Disposable) {}
                                    override fun onSuccess(isWorkComplete: Pair<Boolean?, String?>) {
                                        hideLoader()
                                        if (isWorkComplete.first == true && isFinish) {
                                            workCompletion(true, status)
                                        } else {
                                            showCustomToast(isWorkComplete.second, 3)
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        hideLoader()
                                        if (e is IOException) {
                                            showCustomToast("Unable to Connect right now please try again later", 3)
                                        } else showCustomToast(e.message, 3)
                                    }
                                })
                    } else {
                        workCompletion(isFinish, status)
                    }
                }, { e -> Log.e("Error", "Error", e) }))
    }

    internal fun workCompletion(isFinish: Boolean, status: Int) {
        hideLoading()
        if (isFinish) {
            if (status == LibDynamicAppConfig.SUBMITTED) {
                showCustomToast(getString(R.string.form_submitted_successfully), 0)
            } else if (status == LibDynamicAppConfig.DRAFT) {
                showCustomToast(getString(R.string.form_saved_draft), 2)
            }
            saved = true
            setResult(Activity.RESULT_OK)
            myFinishActivity(false)
        }
    }


    internal var unansweredListener: UnansweredListener? = UnansweredListener { questionUid ->
        val baseType = questionObjectList[questionUid]
        if (baseType != null) {
            val position = baseType.viewIndex
            val parentPosition = linearLayout.top + 1
            if (position >= 0 && position < linearLayout.childCount) {
                val childPosition = linearLayout.getChildAt(position).top
                val scrollPosition = parentPosition + childPosition
                moveToPosition(scrollPosition)
            } else {
                showToast(R.string.question_still_not_loaded)
            }
        } else {
            showToast(R.string.question_still_not_loaded)
        }
    }

    //get filled fields count
    private fun getValidAnsCount(): Int {
        var ans = 0
        for (questionBeanFilled in answerBeanHelperList.values) {
            if (questionBeanFilled!!.isFilled) {
                ans++
            }
        }
        return ans
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        isDestroyedLocal = true
        unansweredListener = null
        super.onDestroy()
    }

    override fun acceptedPermission(grantResults: IntArray) {
        saved = false
        locationHandler.startGpsService()
    }

    override fun deniedPermission(isNeverAskAgain: Boolean) {
        myFinishActivity(true)
    }

    override fun acceptedGPS() {}

    override fun deniedGPS() {
        myFinishActivity(true)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationHandler.onActivityResult(requestCode, resultCode)
    }
}