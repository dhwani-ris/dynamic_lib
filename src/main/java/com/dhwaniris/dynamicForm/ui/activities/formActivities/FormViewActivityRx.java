//package com.dhwaniris.dynamicForm.ui.activities.formActivities;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.Toolbar;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
//import com.dhwaniris.dynamicForm.R;
//import com.dhwaniris.dynamicForm.SingletonForm;
//import com.dhwaniris.dynamicForm.adapters.UnansweredQusAdapter;
//import com.dhwaniris.dynamicForm.base.BaseActivity;
//import com.dhwaniris.dynamicForm.db.FilledForms;
//import com.dhwaniris.dynamicForm.db.dbhelper.LocationBean;
//import com.dhwaniris.dynamicForm.db.dbhelper.MessageBeanX;
//import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
//import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
//import com.dhwaniris.dynamicForm.db.dbhelper.form.Form;
//import com.dhwaniris.dynamicForm.db.dbhelper.form.LanguageBean;
//import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
//import com.dhwaniris.dynamicForm.interfaces.UnansweredListener;
//import com.dhwaniris.dynamicForm.locationservice.LocationUpdatesService;
//import com.dhwaniris.dynamicForm.questionTypes.BaseType;
//import com.dhwaniris.dynamicForm.utils.Constant;
//import com.dhwaniris.dynamicForm.utils.LocationHandler;
//import com.dhwaniris.dynamicForm.utils.LocationHandlerListener;
//import com.dhwaniris.dynamicForm.utils.LocationReceiver;
//import com.dhwaniris.dynamicForm.utils.PermissionHandler;
//import com.dhwaniris.dynamicForm.utils.PermissionHandlerListener;
//import com.dhwaniris.dynamicForm.utils.QuestionsUtils;
//import com.google.android.material.snackbar.Snackbar;
//
//import org.jetbrains.annotations.NotNull;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//
//import io.reactivex.Observable;
//import io.reactivex.Single;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.schedulers.Schedulers;
//
//import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.DRAFT;
//import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_DARFT;
//import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_SUBMITTED;
//import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.REJECTED_DUPLICATE;
//import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;
//import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SYNCED;
//import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SYNCED_BUT_EDITABLE;
//
//public class FormViewActivityRx extends BaseFormActivity implements View.OnClickListener
//        , PermissionHandlerListener, LocationHandlerListener {
//    private volatile boolean isDestroyed = false;
//
//    private LocationManager locMnagaer;
//    private String longitutde = "0.0";
//    private String latitude = "0.0";
//    private String accuracy = "0.0";
//    private long time = 0, lastTimestamp = 0;
//    protected boolean isErrorViewRequired = false;
//
//    private boolean isListLoaded;
//    private Form formModel;
//
//    Context ctx;
//    Toolbar toolbar;
//    Button save;
//    Button submit;
//
//    SingletonForm singletonForm;
//    String titleInLanguage;
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
//                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
//    }
//
//    @Override
//    protected void onStop() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
//
//        hideProgess();
//        super.onStop();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        isDestroyed = false;
//        setContentView(R.layout.activity_form);
//        bindView();
//        superViewBind();
//        ctx = FormViewActivityRx.this;
//        filledFormList = new FilledForms();
//        questionBeenList = new LinkedHashMap<>();
//        childQuestionBeenList = new LinkedHashMap<>();
//
//        lastTimestamp = System.currentTimeMillis();
//
//        locationHandler = new LocationHandler(this);
//        permissionHandler = new PermissionHandler(this, this);
//        locationReceiver = new LocationReceiver(locationDataN);
//        locationHandler.setGPSonOffListener(this);
//        save.setVisibility(View.GONE);
//        try {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        toolbar.setNavigationOnClickListener(view -> onBackPressed());
//
//
//        count.setOnClickListener(this);
//
//
//        singletonForm = SingletonForm.getInstance();
//        if (singletonForm.getForm() == null) {
//            myFinishActivity(true);
//        } else {
//            prepareQuestionView();
//        }
//    }
//
//    void bindView() {
//        toolbar = findViewById(R.id.toolbar);
//        save = findViewById(R.id.save);
//        submit = findViewById(R.id.submit);
//    }
//
//    void checkRequiredStuff(List<QuestionBean> questionBeanList) {
//
//        QuestionsUtils.Companion.sortQusList(questionBeanList);
//        for (QuestionBean questionBean : questionBeanList) {
//            questionBeenList.put(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), questionBean);
//        }
//
//        if (questionBeenList.size() > 0) {
//            if (isLocationRequired) {
//                if (permissionHandler.checkGpsPermission()) {
//                    if (!isDestroyed)
//                        locationHandler.startGpsService();
//                    AddNewObjectView(questionBeenList);
//                    saved = false;
//                } else {
//                    permissionHandler.requestGpsPermission();
//                }
//
//            } else {
//                AddNewObjectView(questionBeenList);
//                saved = false;
//            }
//
//        } else {
//            saved = true;
//            linearLayout.setVisibility(View.GONE);
//            submit.setVisibility(View.GONE);
//            save.setVisibility(View.GONE);
//        }
//
//
//    }
//
//
//    List<Answers> getAnswerFormText(String originalAns, QuestionBean questionBean) {
//        if (originalAns != null && !originalAns.isEmpty()) {
//            return Observable.fromCallable(()-> Arrays.asList(originalAns.split(","))).flatMapIterable((list)->list)
//                    .subscribeOn(Schedulers.computation())
//                    .map((ans)->{
//                        Answers answers = new Answers();
//                        if (QuestionsUtils.Companion.isEditTextType(questionBean.getInput_type())) {
//                            answers.setTextValue(ans);
//                        } else {
//                            answers.setValue(ans);
//                        }
//                        return answers;
//                    }).toList()
//                    .blockingGet();
//
//        }
//        else
//            return new ArrayList<Answers>();
//    }
//
//    private Disposable d ;
//    public void prepareQuestionView() {
//        prepare();
//    }
//    public void prepare(){
//
//        showProgress(ctx, getString(R.string.preparing_form));
//        formModel = singletonForm.getForm();
//        JSONObject jsonObject = singletonForm.getJsonObject();
//
//        formModel = singletonForm.getForm();
//
//        d = Single.fromCallable(() -> {
//            int uploadStatus = 0;
//            try {
//                uploadStatus = jsonObject.getInt(Constant.UPLOAD_STATUS);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            if (uploadStatus == SUBMITTED) {
//                formStatus = SUBMITTED;
//                save.setVisibility(View.GONE);
//                submit.setVisibility(View.GONE);
//            } else if (filledFormList.getUpload_status() == SYNCED
//                    || filledFormList.getUpload_status() == SYNCED_BUT_EDITABLE || filledFormList.getUpload_status() == EDITABLE_DARFT) {
//                isErrorViewRequired = true;
//                formStatus = filledFormList.getUpload_status();
//                submit.setOnClickListener(FormViewActivityRx.this);
//
//            } else if (filledFormList.getUpload_status() == EDITABLE_SUBMITTED) {
//                formStatus = EDITABLE_SUBMITTED;
//                submit.setVisibility(View.GONE);
//            } else if (filledFormList.getUpload_status() == REJECTED_DUPLICATE) {
//                formStatus = SUBMITTED;
//                submit.setVisibility(View.GONE);
//
//            } else {
//                formStatus = DRAFT;
//                submit.setOnClickListener(FormViewActivityRx.this);
//            }
//            time = Long.parseLong(filledFormList.getTimeTaken());
//            return uploadStatus;
//        })
//                .flatMap((uploadStatus) -> Single.just(jsonObject))
//                .subscribeOn(Schedulers.computation())
//                .map(jsonObjectFromSingle -> {
//                    List<QuestionBean> questionBeanList = new ArrayList<>();
//                    JSONObject jsonObject1 = singletonForm.getJsonObject();
//                    if (jsonObject1 != null && formModel != null) {
//                        form_id = formModel.getFormId();
//                        isLocationRequired = formModel.isLocation();
//
//                        String userLanguage = userLanguage();
//                        boolean isFoundLanguage = false;
//
//                        for (LanguageBean languageBean : formModel.getLanguage()) {
//                            if (languageBean.getLng().equals(userLanguage)) {
//                                isFoundLanguage = true;
//                                break;
//                            }
//                        }
//                        userLanguage = isFoundLanguage ? userLanguage() : "en";
//                        final String userLng = userLanguage;
//                        Observable.fromIterable(formModel.getLanguage())
//                                .filter(languageBean -> languageBean.getLng().equals(userLng))
//                                .subscribeOn(Schedulers.computation())
//                                .map((langOpt) -> {
//                                    titleInLanguage = langOpt.getTitle();
//                                    return langOpt.getQuestion();
//                                }).flatMapIterable((opt) -> opt)
//                                .subscribeOn(Schedulers.computation())
//                                .blockingForEach(questionBean -> {
//                                    if (!questionBean.getOrder().contains(".")) {
//                                        questionBeanList.add(questionBean);
//                                    } else {
//                                        childQuestionBeenList.put(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), questionBean);
//                                    }
//                                });
//
//
//                        /*for (LanguageBean languageBean : formModel.getLanguage()) {
//                            if (languageBean.getLng().equals(userLanguage)) {
//                                titleInLanguage = languageBean.getTitle();
//                                for (QuestionBean questionBean : languageBean.getQuestion()) {
//                                    if (!questionBean.getOrder().contains(".")) {
//                                        questionBeanList.add(questionBean);
//                                    } else {
//                                        childQuestionBeenList.put(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), questionBean);
//                                    }
//                                }
//                                break;
//                            }
//                        }*/
//
//                        Disposable disp = Observable.fromIterable(questionBeanList)
//                                .subscribeOn(Schedulers.computation())
//                                .forEach(questionBean -> {
//                                    String columnName = questionBean.getColumnName();
//
//                                    QuestionBeanFilled answerBeanObject = createOrModifyAnswerBeanObject(questionBean, true);
//                                    answerBeanObject.setFilled(true);
//                                    answerBeanObject.setValidAns(true);
//                                    try {
//                                        if (jsonObject1.get(columnName) != null) {
//                                            String string = jsonObject1.getString(columnName);
//                                            answerBeanObject.setAnswer(getAnswerFormText(string, questionBean));
//                                            answerBeanHelperList.put(QuestionsUtils.Companion.getAnswerUniqueId(answerBeanObject), answerBeanObject);
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                });
//
//                        /*for (QuestionBean questionBean : questionBeanList) {
//                            String columnName = questionBean.getColumnName();
//
//                            QuestionBeanFilled answerBeanObject = createOrModifyAnswerBeanObject(questionBean, true);
//                            answerBeanObject.setFilled(true);
//                            answerBeanObject.setValidAns(true);
//                            try {
//                                if (jsonObject1.get(columnName) != null) {
//                                    String string = jsonObject1.getString(columnName);
//                                    answerBeanObject.setAnswer(getAnswerFormText(string, questionBean));
//                                    answerBeanHelperList.put(QuestionsUtils.Companion.getAnswerUniqueId(answerBeanObject), answerBeanObject);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }*/
//
//
//                    }
//
//
//                    return questionBeanList;
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe((questionBeanList) -> {
//                    if(getSupportActionBar()!=null)
//                        getSupportActionBar().setTitle(titleInLanguage);
//                    hideLoader();
//                    if (!questionBeanList.isEmpty()) {
//                        checkRequiredStuff(questionBeanList);
//                    } else {
//                        showToast(R.string.something_went_wrong);
//                        myFinishActivity(true);
//                    }
//                }, (exc) -> {
//                });
//
//
//    }
//
//
//    //adding dynamic view in LinearLayout
//    private void AddNewObjectView(LinkedHashMap<String, QuestionBean> questionBeanRealmList) {
//        Disposable loadFormDynamicViews = Observable.fromIterable(questionBeanRealmList.values())
//                .subscribeOn(Schedulers.computation())
//                .map(questionBean->{
//                    if (formStatus == EDITABLE_DARFT) {
//                        QuestionBeanFilled answer = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
//                        if (!answer.isFilled() && answer.isRequired()
//                                && !QuestionsUtils.Companion.isLoopingType(answer)) {
//                            questionBean.setEditable(true);
//                        }
//                    }
//                    return questionBean;
//                }).observeOn(AndroidSchedulers.mainThread())
//                .flatMap((questionBean)->{
//                    createViewObject(questionBean, formStatus);
//                    return Observable.just(questionBean);
//                })
//                .toList()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe((sols)->{
//                    changeAnswerBeanHelperTitle(questionBeenList, answerBeanHelperList, formStatus);
//                },(e)->{});
//    }
//
//    private void changeAnswerBeanHelperTitle
//            (LinkedHashMap<String, QuestionBean> questionBean, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList,
//             int formStatus) {
//        if (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT || formStatus == EDITABLE_SUBMITTED) {
//            for (QuestionBeanFilled questionBeanFilled : answerBeanHelperList.values()) {
//                QuestionBean questionBean1 = questionBean.get(QuestionsUtils.Companion.getAnswerUniqueId(questionBeanFilled));
//                if (questionBean1 != null) {
//                    questionBeanFilled.setTitle(questionBean1.getTitle());
//                }
//            }
//        }
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (isLocationRequired) {
//            if (permissionHandler.checkGpsPermission()) {
//                locationHandler.startGpsService();
//            }
//        }
//        alreadyRequest = false;
//    }
//
//    @Override
//    protected void onPause() {
//
//        if (isLocationRequired) {
//            locationHandler.onStop();
//        }
//        super.onPause();
//    }
//
//    @Override
//    public void onBackPressed() {
//
//        if (formStatus == SUBMITTED) {
//            new androidx.appcompat.app.AlertDialog.Builder(ctx)
//                    .setTitle(R.string.are_you_sure)
//                    .setMessage(R.string.are_you_sure)
//                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> myFinishActivity(true))
//                    .setNegativeButton(R.string.no, (dialogInterface, i) -> { })
//                    .setCancelable(true)
//                    .show();
//        } else {
//            myFinishActivity(true);
//        }
//
//    }
//
//
//    void myFinishActivity(boolean isCanceled) {
//        if (isCanceled) {
//            setResult(Activity.RESULT_CANCELED);
//        } else {
//            setResult(Activity.RESULT_OK);
//        }
//
//        finish();
//
//    }
//
//
//    @Override
//    public void onClick(View view) {
//
//        int i = view.getId();
//        if (i == R.id.save) {
//            if (requirdAlertMap.containsValue(true)) {
//                List<String> orderlist = checkRegexHashMap(requirdAlertMap);
//                if (!orderlist.isEmpty()) {
//                    String text = answerBeanHelperList.get(orderlist.get(0)).getAnswer().get(0).getValue();
//
//                    checkAlert(text, questionBeenList.get(orderlist.get(0)));
//                }
//            } else if (focusOnEditext && !notifyOnchangeMap.isEmpty() && !validateOnChangeListeners()) {
//                focusOnEditext = false;
//                new AlertDialog.Builder(ctx)
//                        .setTitle(R.string.save_data)
//                        .setMessage(R.string.do_you_want_to_save_as_draft)
//                        .setPositiveButton(R.string.yes, (dialogInterface, i1) -> {
//                        })
//                        .setNegativeButton(R.string.no, null)
//                        .setCancelable(true)
//                        .show();
//                return;
//            }
//            //draft save action
//            else if (getValidAnsCount() > 0) {
//
//                new AlertDialog.Builder(ctx)
//                        .setTitle(R.string.save_data)
//                        .setMessage(R.string.do_you_want_to_save_as_draft)
//                        .setPositiveButton(R.string.yes, (dialogInterface, i12) -> { })
//                        .setNegativeButton(R.string.no, null)
//                        .setCancelable(true)
//                        .show();
//
//            } else {
//                Snackbar.make(submit, R.string.please_fill_at_least_one_field, Snackbar.LENGTH_SHORT)
//                        .setAction(R.string.ok, null)
//                        .show();
//            }
//        } else if (i == R.id.submit) {//submit action
//            validateData();
//        } else if (i == R.id.count) {//submit action
//            showUnansweredQuestions(new ArrayList<>(answerBeanHelperList.values()), true);
//        }
//
//    }
//
//    //Validating data before save
//    private void validateData() {
//
//        if (requirdAlertMap.containsValue(true)) {
//            List<String> orderlist = checkRegexHashMap(requirdAlertMap);
//            if (!orderlist.isEmpty()) {
//                String text = answerBeanHelperList.get(orderlist.get(0)).getAnswer().get(0).getValue();
//
//                checkAlert(text, questionBeenList.get(orderlist.get(0)));
//            }
//        } else if (focusOnEditext && !notifyOnchangeMap.isEmpty() && !validateOnChangeListeners()) {
//            focusOnEditext = false;
//            refreshLoopingFiltersQuestions();
//            List<QuestionBeanFilled> tempList = findInvalidAnswersList(answerBeanHelperList, questionObjectList);
//
//            if (!tempList.isEmpty()) {
//                showUnansweredQuestions(tempList, false);
//            }
//            return;
//        } else if (!answerBeanHelperList.isEmpty()) {
//            validateOnChangeListeners();
//            refreshLoopingFiltersQuestions();
//            List<QuestionBeanFilled> tempList = findInvalidAnswersList(answerBeanHelperList, questionObjectList);
//
//            if (!tempList.isEmpty()) {
//                showUnansweredQuestions(tempList, false);
//            } else {
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.save_data)
//                        .setMessage(R.string.are_you_sure)
//                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
//
//                            uploadtask = new UpdateDataData(SUBMITTED, true);
//                            uploadtask.execute();
//
//                        })
//                        .setNegativeButton(R.string.no, null)
//                        .show();
//
//
//            }
//
//        } else {
//            BaseActivity.logDatabase(AppConfing.END_POINT, "Invalid Form. Line No. 406"
//                    , AppConfing.UNEXPECTED_ERROR, "FormViewActivity");
//        }
//    }
//    private UpdateDataData uploadtask=null;
//
//    //show unanswered question
//    private void showUnansweredQuestions(List<QuestionBeanFilled> tempList, boolean showAll) {
//        //creating dropdown selector
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View view = inflater.inflate(R.layout.dynamic_custom_selector_layout, null);
//        TextView title = view.findViewById(R.id.dTitle);
//        ImageView close = view.findViewById(R.id.dClose);
//
//        RecyclerView recyclerView = view.findViewById(R.id.dRecycler);
//        dialog.setView(view);
//        title.setText(R.string.pending_questions);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        final AlertDialog alertDialog = dialog.create();
//        UnansweredQusAdapter adapter = new UnansweredQusAdapter(unansweredListener, tempList, alertDialog, showAll, questionBeenList);
//        recyclerView.setAdapter(adapter);
//        close.setOnClickListener(v -> alertDialog.dismiss());
//        dialog.setPositiveButton(R.string.ok, (dialogInterface, i) -> alertDialog.dismiss());
//        alertDialog.show();
//    }
//
//
//    class UpdateDataData extends AsyncTask<Void, Void, Boolean> {
//        int status;
//        boolean isFinish;
//
//        UpdateDataData(int status, boolean isFinish) {
//            this.status = status;
//            this.isFinish = isFinish;
//        }
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showLoading();
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... integers) {
//
//            LocationBean locationBean = new LocationBean();
//            Location locationData = locationReceiver.getLocationData().getValue();
//            if (locationData != null) {
//                longitutde = "" + locationData.getLongitude();
//                latitude = "" + locationData.getLatitude();
//                accuracy = "" + locationData.getAccuracy();
//            }
//
//            locationBean.setAccuracy(accuracy);
//            locationBean.setLat(latitude);
//            locationBean.setLng(longitutde);
//            long timestamp = System.currentTimeMillis();
//            time = time + timestamp - lastTimestamp;
//            filledFormList.setTimeTaken(String.valueOf(time));
//            filledFormList.setVersion(formModel.getVersion());
//            filledFormList.setFormId(String.valueOf(formModel.getFormId()));
//            filledFormList.setMobileUpdatedAt("" + System.currentTimeMillis());
//            filledFormList.setUpload_status(status);
//
//
//            List<QuestionBeanFilled> answerFilledList = new ArrayList<>();
//            answerFilledList.addAll(answerBeanHelperList.values());
//            QuestionsUtils.Companion.sortAnsList(answerFilledList);
//            filledFormList.setQuestion(answerFilledList);
//            JSONObject jsonObject = singletonForm.getJsonObject();
//            HashMap<String, Boolean> answerMapper = new HashMap<>();
//            for (QuestionBean questionBean : questionBeenList.values()) {
//                QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
//                if (questionBeanFilled != null) {
//                    String columnName = questionBean.getColumnName();
//                    String value = getAnswerForm(questionBeanFilled);
//                    if (!answerMapper.containsKey(columnName)) {
//                        try {
//                            jsonObject.put(columnName, value);
//                            answerMapper.put(columnName, questionBeanFilled.isFilled());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else if (!answerMapper.get(columnName)) {
//                        try {
//                            jsonObject.put(columnName, value);
//                            answerMapper.put(columnName, questionBeanFilled.isFilled());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//
//                }
//            }
//            try {
//                jsonObject.put(Constant.TIME_TAKKEN, String.valueOf(time));
//                if (formModel.isLocation()) {
//                    JSONObject locationJsonObject = new JSONObject();
//                    locationJsonObject.put("lat", locationBean.getLat());
//                    locationJsonObject.put("lng", locationBean.getLng());
//                    locationJsonObject.put("accuracy", locationBean.getAccuracy());
//                    jsonObject.put(Constant.LOCATION, locationJsonObject);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            singletonForm.setJsonObject(jsonObject);
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            hideLoading();
//            if (isFinish) {
//                if (status == SUBMITTED) {
//                    showCustomToast(getString(R.string.form_submitted_successfully), 0);
//                } else if (status == DRAFT) {
//                    showCustomToast(getString(R.string.form_saved_draft), 2);
//                }
//                saved = true;
//                setResult(Activity.RESULT_OK);
//                myFinishActivity(false);
//            }
//            super.onPostExecute(aBoolean);
//        }
//    }
//
//
//    UnansweredListener unansweredListener = new UnansweredListener() {
//        @Override
//        public void Question(String questionUid) {
//            BaseType baseType = questionObjectList.get(questionUid);
//            if (baseType != null) {
//                int position = baseType.getViewIndex();
//                int parentPosition = linearLayout.getTop() + 1;
//                if (position >= 0 && position < linearLayout.getChildCount()) {
//                    int childPosition = linearLayout.getChildAt(position).getTop();
//                    final int scrollPosition = parentPosition + childPosition;
//                    moveToPosition(scrollPosition);
//                } else {
//                    showToast(R.string.question_still_not_loaded);
//                }
//            } else {
//                showToast(R.string.question_still_not_loaded);
//
//            }
//
//        }
//    };
//
//
//    //get filled fields count
//    private int getValidAnsCount() {
//        int ans = 0, total = 0, opt = 0;
//
//
//        for (QuestionBeanFilled questionBeanFilled : answerBeanHelperList.values()) {
//            if (questionBeanFilled.isFilled()) {
//                ans++;
//            }
//        }
//        return ans;
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        isDestroyed = true;
//        unansweredListener = null;
//        if (d != null && !d.isDisposed())
//            d.dispose();
//        if (uploadtask != null && uploadtask.getStatus()== AsyncTask.Status.RUNNING)
//            uploadtask.cancel(true);
//        super.onDestroy();
//
//    }
//
//
//    @Override
//    public void acceptedPermission(@NotNull int[] grantResults) {
//        saved = false;
//        locationHandler.startGpsService();
//    }
//
//    @Override
//    public void deniedPermission(boolean isNeverAskAgain) {
//        myFinishActivity(true);
//
//    }
//
//    @Override
//    public void acceptedGPS() {
//
//    }
//
//    @Override
//    public void deniedGPS() {
//        myFinishActivity(true);
//
//    }
//
//    private void hideLoader() {
//        if (!isListLoaded) {
//            new Handler().postDelayed(() -> {
//                // hideLoading();
//                hideProgess();
//                layout.setVisibility(View.VISIBLE);
//                linearLayout.setVisibility(View.VISIBLE);
//            }, 1000);
//        }
//        isListLoaded = true;
//    }
//
//    public String setErrorMsg(List<MessageBeanX> messageBean) {
//        String msg = "";
//        String defalutMsg = "";
//
//        for (MessageBeanX message : messageBean) {
//            if (userLanguage().equals(message.getLng())) {
//                msg = message.getText();
//                break;
//            }
//            if (message.getLng().equals("en")) {
//                defalutMsg = message.getText();
//            }
//        }
//        return msg.isEmpty() ? defalutMsg : msg;
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        permissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        locationHandler.onActivityResult(requestCode, resultCode);
//
//    }
//
//}