package com.dhwaniris.dynamicForm.ui.activities.formActivities


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig
import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.*
import com.dhwaniris.dynamicForm.R
import com.dhwaniris.dynamicForm.SingletonForm
import com.dhwaniris.dynamicForm.adapters.UnansweredQusAdapter
import com.dhwaniris.dynamicForm.base.BaseActivity
import com.dhwaniris.dynamicForm.db.FilledForms
import com.dhwaniris.dynamicForm.db.dbhelper.LocationBean
import com.dhwaniris.dynamicForm.db.dbhelper.MessageBeanX
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers
import com.dhwaniris.dynamicForm.db.dbhelper.form.Form
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean
import com.dhwaniris.dynamicForm.interfaces.UnansweredListener
import com.dhwaniris.dynamicForm.locationservice.LocationUpdatesService
import com.dhwaniris.dynamicForm.utils.*
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FormViewActivityRx : BaseFormActivity(), View.OnClickListener, PermissionHandlerListener, LocationHandlerListener {
    @Volatile
    private var isDestroyed2 = false

    private var longitutde = "0.0"
    private var latitude = "0.0"
    private var accuracy = "0.0"
    private var time: Long = 0
    private var lastTimestamp: Long = 0
    private var isErrorViewRequired = false

    private var isListLoaded: Boolean = false
    private var formModel: Form? = null

    private lateinit var ctx: Context
    private lateinit  var toolbar: Toolbar
    private lateinit var save: Button
    private lateinit var submit: Button

    private lateinit var singletonForm: SingletonForm
    private lateinit var titleInLanguage: String

    private var d: Disposable? = null
    private var uploadtask: UpdateDataData? = null


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
    private val validAnsCount: Int
        get() {
            var ans = 0
            val total = 0
            val opt = 0


            for (questionBeanFilled in answerBeanHelperList.values) {
                if (questionBeanFilled.isFilled) {
                    ans++
                }
            }
            return ans
        }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
                IntentFilter(LocationUpdatesService.ACTION_BROADCAST))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver)

        hideProgess()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDestroyed2 = false
        setContentView(R.layout.activity_form)
        bindView()
        superViewBind()
        ctx = this@FormViewActivityRx
        filledFormList = FilledForms()
        questionBeenList = LinkedHashMap()
        childQuestionBeenList = LinkedHashMap()

        lastTimestamp = System.currentTimeMillis()

        locationHandler = LocationHandler(this)
        permissionHandler = PermissionHandler(this, this)
        locationReceiver = LocationReceiver(locationDataN)
        locationHandler.setGPSonOffListener(this)
        save.visibility = View.GONE
        try {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        toolbar.setNavigationOnClickListener { view -> onBackPressed() }


        count.setOnClickListener(this)


        singletonForm = SingletonForm.getInstance()
        if (singletonForm.form == null) {
            myFinishActivity(true)
        } else {
            prepareQuestionView()
        }
    }

    internal fun bindView() {
        toolbar = findViewById(R.id.toolbar)
        save = findViewById(R.id.save)
        submit = findViewById(R.id.submit)
    }

    internal fun checkRequiredStuff(questionBeanList: List<QuestionBean>) {

        QuestionsUtils.sortQusList(questionBeanList)
        for (questionBean in questionBeanList) {
            questionBeenList[QuestionsUtils.getQuestionUniqueId(questionBean)] = questionBean
        }

        if (questionBeenList.size > 0) {
            if (isLocationRequired) {
                if (permissionHandler.checkGpsPermission()) {
                    if (!isDestroyed2)
                        locationHandler.startGpsService()
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
            submit.visibility = View.GONE
            save.visibility = View.GONE
        }


    }


    private fun getAnswerFormText(originalAns: String?, questionBean: QuestionBean): List<Answers> {
        return if (originalAns != null && originalAns.isNotEmpty()) {
             Observable.fromCallable { listOf(*originalAns.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) }.flatMapIterable { list -> list }
                    .subscribeOn(Schedulers.computation())
                    .map { ans ->
                        val answers = Answers()
                        if (QuestionsUtils.isEditTextType(questionBean.input_type)) {
                            answers.textValue = ans
                        } else {
                            answers.value = ans
                        }
                        answers
                    }.toList()
                    .blockingGet()

        } else
            listOf()
    }

    fun prepareQuestionView() {
        prepare()
    }

    fun prepare() {

        showProgress(ctx, getString(R.string.preparing_form))
        formModel = singletonForm.form
        val jsonObject = singletonForm.jsonObject

        formModel = singletonForm.form

        d = Single.fromCallable {
            var uploadStatus = 0
            try {
                uploadStatus = jsonObject.getInt(Constant.UPLOAD_STATUS)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (uploadStatus == SUBMITTED) {
                formStatus = SUBMITTED
                save.visibility = View.GONE
                submit.visibility = View.GONE
            } else if (filledFormList.upload_status == SYNCED
                    || filledFormList.upload_status == SYNCED_BUT_EDITABLE || filledFormList.upload_status == EDITABLE_DARFT) {
                isErrorViewRequired = true
                formStatus = filledFormList.upload_status
                submit.setOnClickListener(this@FormViewActivityRx)

            } else if (filledFormList.upload_status == EDITABLE_SUBMITTED) {
                formStatus = EDITABLE_SUBMITTED
                submit.visibility = View.GONE
            } else if (filledFormList.upload_status == REJECTED_DUPLICATE) {
                formStatus = SUBMITTED
                submit.visibility = View.GONE

            } else {
                formStatus = DRAFT
                submit.setOnClickListener(this@FormViewActivityRx)
            }
            time = java.lang.Long.parseLong(filledFormList.timeTaken)
            uploadStatus
        }
                .flatMap { uploadStatus -> Single.just(jsonObject) }
                .subscribeOn(Schedulers.computation())
                .map<List<QuestionBean>> { jsonObjectFromSingle ->
                    val questionBeanList = ArrayList<QuestionBean>()
                    val jsonObject1 = singletonForm.jsonObject
                    if (jsonObject1 != null && formModel != null) {
                        form_id = formModel!!.formId
                        isLocationRequired = formModel!!.isLocation

                        var userLanguage = userLanguage()
                        var isFoundLanguage = false

                        for (languageBean in formModel!!.language) {
                            if (languageBean.lng == userLanguage) {
                                isFoundLanguage = true
                                break
                            }
                        }
                        userLanguage = if (isFoundLanguage) userLanguage() else "en"
                        val userLng = userLanguage
                        Observable.fromIterable(formModel!!.language)
                                .filter { languageBean -> languageBean.lng == userLng }
                                .subscribeOn(Schedulers.computation())
                                .map { langOpt ->
                                    titleInLanguage = langOpt.title
                                    langOpt.question
                                }.flatMapIterable { opt -> opt }
                                .subscribeOn(Schedulers.computation())
                                .blockingForEach { questionBean ->
                                    if (!questionBean.order.contains(".")) {
                                        questionBeanList.add(questionBean)
                                    } else {
                                        childQuestionBeenList[QuestionsUtils.getQuestionUniqueId(questionBean)] = questionBean
                                    }
                                }


                        /*for (LanguageBean languageBean : formModel.getLanguage()) {
                       if (languageBean.getLng().equals(userLanguage)) {
                           titleInLanguage = languageBean.getTitle();
                           for (QuestionBean questionBean : languageBean.getQuestion()) {
                               if (!questionBean.getOrder().contains(".")) {
                                   questionBeanList.add(questionBean);
                               } else {
                                   childQuestionBeenList.put(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), questionBean);
                               }
                           }
                           break;
                       }
                   }*/

                        val disp = Observable.fromIterable(questionBeanList)
                                .subscribeOn(Schedulers.computation())
                                .forEach { questionBean ->
                                    val columnName = questionBean.columnName

                                    val answerBeanObject = createOrModifyAnswerBeanObject(questionBean, true)
                                    answerBeanObject.isFilled = true
                                    answerBeanObject.isValidAns = true
                                    try {
                                        if (jsonObject1.get(columnName) != null) {
                                            val string = jsonObject1.getString(columnName)
                                            answerBeanObject.answer = getAnswerFormText(string, questionBean)
                                            answerBeanHelperList[QuestionsUtils.getAnswerUniqueId(answerBeanObject)] = answerBeanObject
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }

                        /*for (QuestionBean questionBean : questionBeanList) {
                       String columnName = questionBean.getColumnName();

                       QuestionBeanFilled answerBeanObject = createOrModifyAnswerBeanObject(questionBean, true);
                       answerBeanObject.setFilled(true);
                       answerBeanObject.setValidAns(true);
                       try {
                           if (jsonObject1.get(columnName) != null) {
                               String string = jsonObject1.getString(columnName);
                               answerBeanObject.setAnswer(getAnswerFormText(string, questionBean));
                               answerBeanHelperList.put(QuestionsUtils.Companion.getAnswerUniqueId(answerBeanObject), answerBeanObject);
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }*/


                    }


                    questionBeanList
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ questionBeanList ->
                    if (supportActionBar != null)
                        supportActionBar!!.title = titleInLanguage
                    hideLoader()
                    if (!questionBeanList.isEmpty()) {
                        checkRequiredStuff(questionBeanList)
                    } else {
                        showToast(R.string.something_went_wrong)
                        myFinishActivity(true)
                    }
                }, { exc -> })


    }


    //adding dynamic view in LinearLayout
    private fun AddNewObjectView(questionBeanRealmList: LinkedHashMap<String, QuestionBean>) {
        val loadFormDynamicViews = Observable.fromIterable(questionBeanRealmList.values)
                .subscribeOn(Schedulers.computation())
                .map { questionBean ->
                    if (formStatus == EDITABLE_DARFT) {
                        val answer = answerBeanHelperList[QuestionsUtils.getQuestionUniqueId(questionBean)]
                        if (!answer!!.isFilled && answer.isRequired
                                && !QuestionsUtils.isLoopingType(answer)) {
                            questionBean.isEditable = true
                        }
                    }
                    questionBean
                }.observeOn(AndroidSchedulers.mainThread())
                .flatMap { questionBean ->
                    createViewObject(questionBean, formStatus)
                    Observable.just(questionBean)
                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ sols -> changeAnswerBeanHelperTitle(questionBeenList, answerBeanHelperList, formStatus) }, { e -> })
    }

    private fun changeAnswerBeanHelperTitle(questionBean: LinkedHashMap<String, QuestionBean>, answerBeanHelperList: LinkedHashMap<String, QuestionBeanFilled>,
                                            formStatus: Int) {
        if (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT || formStatus == EDITABLE_SUBMITTED) {
            for (questionBeanFilled in answerBeanHelperList.values) {
                val questionBean1 = questionBean[QuestionsUtils.getAnswerUniqueId(questionBeanFilled)]
                if (questionBean1 != null) {
                    questionBeanFilled.title = questionBean1.title
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (isLocationRequired) {
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

        if (formStatus == SUBMITTED) {
            androidx.appcompat.app.AlertDialog.Builder(ctx)
                    .setTitle(R.string.are_you_sure)
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


    override fun onClick(view: View) {

        val i = view.id
        if (i == R.id.save) {
            if (requirdAlertMap.containsValue(true)) {
                val orderlist = checkRegexHashMap(requirdAlertMap)
                if (orderlist.isNotEmpty()) {
                    val text = answerBeanHelperList[orderlist[0]]!!.answer[0].value

                    checkAlert(text, questionBeenList[orderlist[0]])
                }
            } else if (focusOnEditext && notifyOnchangeMap.isNotEmpty() && !validateOnChangeListeners()) {
                focusOnEditext = false
                AlertDialog.Builder(ctx)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.do_you_want_to_save_as_draft)
                        .setPositiveButton(R.string.yes) { dialogInterface, i1 -> }
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(true)
                        .show()
                return
            } else if (validAnsCount > 0) {

                AlertDialog.Builder(ctx)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.do_you_want_to_save_as_draft)
                        .setPositiveButton(R.string.yes) { dialogInterface, i12 -> }
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(true)
                        .show()

            } else {
                Snackbar.make(submit, R.string.please_fill_at_least_one_field, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.ok, null)
                        .show()
            }//draft save action
        } else if (i == R.id.submit) {//submit action
            validateData()
        } else if (i == R.id.count) {//submit action
            showUnansweredQuestions(ArrayList(answerBeanHelperList.values), true)
        }

    }

    //Validating data before save
    private fun validateData() {

        if (requirdAlertMap.containsValue(true)) {
            val orderlist = checkRegexHashMap(requirdAlertMap)
            if (orderlist.isNotEmpty()) {
                val text = answerBeanHelperList[orderlist[0]]!!.answer[0].value

                checkAlert(text, questionBeenList[orderlist[0]])
            }
        } else if (focusOnEditext && notifyOnchangeMap.isNotEmpty() && !validateOnChangeListeners()) {
            focusOnEditext = false
            refreshLoopingFiltersQuestions()
            val tempList = findInvalidAnswersList(answerBeanHelperList, questionObjectList)

            if (!tempList.isEmpty()) {
                showUnansweredQuestions(tempList, false)
            }
            return
        } else if (!answerBeanHelperList.isEmpty()) {
            validateOnChangeListeners()
            refreshLoopingFiltersQuestions()
            val tempList = findInvalidAnswersList(answerBeanHelperList, questionObjectList)

            if (!tempList.isEmpty()) {
                showUnansweredQuestions(tempList, false)
            } else {
                AlertDialog.Builder(this)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes) { dialogInterface, i ->

                            uploadtask = UpdateDataData(SUBMITTED, true)
                            uploadtask!!.execute()

                        }
                        .setNegativeButton(R.string.no, null)
                        .show()


            }

        } else {
            BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT, "Invalid Form. Line No. 406", LibDynamicAppConfig.UNEXPECTED_ERROR, "FormViewActivity")
        }
    }

    //show unanswered question
    private fun showUnansweredQuestions(tempList: List<QuestionBeanFilled>, showAll: Boolean) {
        //creating dropdown selector
        val dialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dynamic_custom_selector_layout, null)
        val title = view.findViewById<TextView>(R.id.dTitle)
        val close = view.findViewById<ImageView>(R.id.dClose)

        val recyclerView = view.findViewById<RecyclerView>(R.id.dRecycler)
        dialog.setView(view)
        title.setText(R.string.pending_questions)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val alertDialog = dialog.create()
        val adapter = UnansweredQusAdapter(unansweredListener, tempList, alertDialog, showAll, questionBeenList)
        recyclerView.adapter = adapter
        close.setOnClickListener { v -> alertDialog.dismiss() }
        dialog.setPositiveButton(R.string.ok) { dialogInterface, i -> alertDialog.dismiss() }
        alertDialog.show()
    }


    internal inner class UpdateDataData(var status: Int, var isFinish: Boolean) : AsyncTask<Void, Void, Boolean>() {


        override fun onPreExecute() {
            super.onPreExecute()
            showLoading()
        }

        override fun doInBackground(vararg integers: Void): Boolean? {

            val locationBean = LocationBean()
            val locationData = locationReceiver.locationData.value
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
            filledFormList.version = formModel!!.version
            filledFormList.formId = formModel!!.formId.toString()
            filledFormList.mobileUpdatedAt = "" + System.currentTimeMillis()
            filledFormList.upload_status = status


            val answerFilledList = ArrayList<QuestionBeanFilled>()
            answerFilledList.addAll(answerBeanHelperList.values)
            QuestionsUtils.sortAnsList(answerFilledList)
            filledFormList.question = answerFilledList
            val jsonObject = singletonForm.jsonObject
            val answerMapper = HashMap<String, Boolean>()
            for (questionBean in questionBeenList.values) {
                val questionBeanFilled = answerBeanHelperList[QuestionsUtils.getQuestionUniqueId(questionBean)]
                if (questionBeanFilled != null) {
                    val columnName = questionBean.columnName
                    val value = getAnswerForm(questionBeanFilled)
                    if (!answerMapper.containsKey(columnName)) {
                        try {
                            jsonObject.put(columnName, value)
                            answerMapper[columnName] = questionBeanFilled.isFilled
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else if (!answerMapper[columnName]!!) {
                        try {
                            jsonObject.put(columnName, value)
                            answerMapper[columnName] = questionBeanFilled.isFilled
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }


                }
            }
            try {
                jsonObject.put(Constant.TIME_TAKKEN, time.toString())
                if (formModel!!.isLocation) {
                    val locationJsonObject = JSONObject()
                    locationJsonObject.put("lat", locationBean.lat)
                    locationJsonObject.put("lng", locationBean.lng)
                    locationJsonObject.put("accuracy", locationBean.accuracy)
                    jsonObject.put(Constant.LOCATION, locationJsonObject)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            singletonForm.jsonObject = jsonObject
            return true
        }

        override fun onPostExecute(aBoolean: Boolean?) {
            hideLoading()
            if (isFinish) {
                if (status == SUBMITTED) {
                    showCustomToast(getString(R.string.form_submitted_successfully), 0)
                } else if (status == DRAFT) {
                    showCustomToast(getString(R.string.form_saved_draft), 2)
                }
                saved = true
                setResult(Activity.RESULT_OK)
                myFinishActivity(false)
            }
            super.onPostExecute(aBoolean)
        }
    }


    override fun onDestroy() {
        isDestroyed2 = true
        unansweredListener = null
        if (d != null && !d!!.isDisposed)
            d!!.dispose()
        if (uploadtask != null && uploadtask!!.getStatus() == AsyncTask.Status.RUNNING)
            uploadtask!!.cancel(true)
        super.onDestroy()

    }


    override fun acceptedPermission(grantResults: IntArray) {
        saved = false
        locationHandler.startGpsService()
    }

    override fun deniedPermission(isNeverAskAgain: Boolean) {
        myFinishActivity(true)

    }

    override fun acceptedGPS() {

    }

    override fun deniedGPS() {
        myFinishActivity(true)

    }

    private fun hideLoader() {
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

    fun setErrorMsg(messageBean: List<MessageBeanX>): String {
        var msg = ""
        var defalutMsg = ""

        for (message in messageBean) {
            if (userLanguage() == message.lng) {
                msg = message.text
                break
            }
            if (message.lng == "en") {
                defalutMsg = message.text
            }
        }
        return if (msg.isEmpty()) defalutMsg else msg

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