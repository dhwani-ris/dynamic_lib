package com.dhwaniris.dynamicForm.ui.activities.formActivities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.SingletonForm;
import com.dhwaniris.dynamicForm.adapters.UnansweredQusAdapter;
import com.dhwaniris.dynamicForm.base.BaseActivity;
import com.dhwaniris.dynamicForm.db.FilledForms;
import com.dhwaniris.dynamicForm.db.dbhelper.LocationBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Form;
import com.dhwaniris.dynamicForm.db.dbhelper.form.LanguageBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.interfaces.UnansweredListener;
import com.dhwaniris.dynamicForm.locationservice.LocationUpdatesService;
import com.dhwaniris.dynamicForm.questionTypes.BaseType;
import com.dhwaniris.dynamicForm.utils.Constant;
import com.dhwaniris.dynamicForm.utils.LocationHandler;
import com.dhwaniris.dynamicForm.utils.LocationHandlerListener;
import com.dhwaniris.dynamicForm.utils.LocationReceiver;
import com.dhwaniris.dynamicForm.utils.PermissionHandler;
import com.dhwaniris.dynamicForm.utils.PermissionHandlerListener;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;
import com.dhwaniris.dynamicForm.utils.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;


public class FormActivity extends BaseFormActivity implements View.OnClickListener,
        PermissionHandlerListener, LocationHandlerListener {


    Context ctx;
    Toolbar toolbar;
    Button save;
    Button submit;
    String uniqueTransactionId;
    private String longitude = "0.0";
    private String latitude = "0.0";
    private String accuracy = "0.0";
    private boolean isListLoaded;
    private long lastTimestamp = 0;
    String titleInLanguage = "";
    private String mimAppVersion;
    private String formVersion;
    private boolean formIsMedia;

    List<String> duplicateCheckQuestions;
    Form formModel = null;
    SingletonForm singletonForm;

    public FormActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dy_activity_form);
        bindView();
        superViewBind();
        ctx = FormActivity.this;
        filledFormList = new FilledForms();
        lastTimestamp = System.currentTimeMillis();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        formStatus = NEW_FORM;
        //generating unique ID
        uniqueTransactionId = UUID.randomUUID().
                toString();
        questionBeenList = new LinkedHashMap<>();
        questionBeenList = new LinkedHashMap<>();
        childQuestionBeenList = new LinkedHashMap<>();
        save.setOnClickListener(this);
        submit.setOnClickListener(this);
        save.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        count.setOnClickListener(this);
        locationHandler = new LocationHandler(this);
        permissionHandler = new PermissionHandler(this, this);
        locationReceiver = new LocationReceiver(locationDataN);
        locationHandler.setGPSonOffListener(this);
        singletonForm = SingletonForm.getInstance();
        if (singletonForm.getForm() == null) {
            setResult(Activity.RESULT_CANCELED);
            myFinishActivity();
        } else {
            prepareQuestionView();
        }


    }

    void bindView() {
        toolbar = findViewById(R.id.toolbar);
        save = findViewById(R.id.save);
        submit = findViewById(R.id.submit);

    }

    private void hideLoader() {
        if (!isListLoaded) {
            new Handler().postDelayed(() -> {
                // hideLoading();
                hideProgess();
                layout.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                if (mimAppVersion != null)
                    if (Utility.isUpdateNeeded(mimAppVersion)) {
                        BaseActivity.versionDialog(ctx);
                    }
            }, 1000);
        }
        isListLoaded = true;
    }


    //adding dynamic view in LinearLayout
    private void AddNewObjectView(LinkedHashMap<String, QuestionBean> questionBeanList) {
/*////
        QuestionBean questionBean1 = questionBeanList.get("2");
        questionBean1.setInput_type(AppConfing.QUA_UNIT_CONVERSION);
        ValidationBean validationBean = new ValidationBean();
        validationBean.set_id(VAL_UNIT_TEMPERATURE);
        questionBean1.getValidation().add(validationBean);
/////*/
        for (QuestionBean questionBean : questionBeanList.values()) {
            createViewObject(questionBean, formStatus);
        }
        submit.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
        alreadyRequest = false;
        if (isLocationRequired && checkGpsPermission()) {
            locationHandler.startGpsService();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isLocationRequired) {
            locationHandler.onStop();
        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(ctx)
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(Activity.RESULT_CANCELED);
                        myFinishActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setCancelable(true)
                .show();
    }

    //Removing changes in form
    private void RemoveAnyChange() {

    }

    @Override
    public void onClick(View view) {

//        switch (view.getId()) {

        if (view.getId() == R.id.save) {
            save.clearFocus();
        } else if (view.getId() == R.id.submit) {
            //submit action
            validateData();
        } else if (view.getId() == R.id.count) {
            //submit action
            showUnansweredQuestions(new ArrayList<>(answerBeanHelperList.values()), true);
        }
//        }

    }

    //Validating data before save
    private void validateData() {
        if (requirdAlertMap.containsValue(true)) {
            List<String> orderlist = checkRegexHashMap(requirdAlertMap);
            if (orderlist.size() > 0) {
                String text = answerBeanHelperList.get(orderlist.get(0)).getAnswer().get(0).getValue();
                checkAlert(text, questionBeenList.get(orderlist.get(0)));
            }
        } else if (focusOnEditext && notifyOnchangeMap.size() > 0 && !validateOnChangeListeners()) {
            return;
        } else if (answerBeanHelperList.size() > 0) {
            validateOnChangeListeners();
            refreshLoopingFiltersQuestions();
            List<QuestionBeanFilled> tempList = findInvalidAnswersList(answerBeanHelperList, questionObjectList);
            if (tempList.size() > 0) {
                showUnansweredQuestions(tempList, false);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new SaveData(SUBMITTED, true).execute();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();

            }


        } else {
            BaseActivity.logDatabase(AppConfing.END_POINT, "Validation error question size 0",
                    AppConfing.UNEXPECTED_ERROR, "FormActivity");
        }
    }

    //show unanswered question
    private void showUnansweredQuestions(List<QuestionBeanFilled> tempList, boolean showAll) {
        //creating dropdown selector
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dynamic_custom_selector_layout, null);
        TextView title = view.findViewById(R.id.dTitle);
        ImageView close = view.findViewById(R.id.dClose);

        RecyclerView recyclerView = view.findViewById(R.id.dRecycler);
        dialog.setView(view);
        title.setText(R.string.pending_questions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final android.app.AlertDialog alertDialog = dialog.create();

        UnansweredQusAdapter adapter = new UnansweredQusAdapter(unansweredListener,
                tempList, alertDialog, showAll, questionBeenList);
        recyclerView.setAdapter(adapter);
        close.setOnClickListener(v -> alertDialog.dismiss());
        dialog.setPositiveButton(R.string.ok, (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog.show();
    }


    class SaveData extends AsyncTask<Void, Void, Boolean> {
        int status;
        boolean isFinish;

        SaveData(int status, boolean isFinish) {
            this.status = status;
            this.isFinish = isFinish;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... integers) {
            long timestamp = System.currentTimeMillis();
            long time = timestamp - lastTimestamp;
            filledFormList.setTransactionId(uniqueTransactionId);
            filledFormList.setTimeTaken(String.valueOf(time));
            filledFormList.setVersion(formVersion);
            filledFormList.setFormId(String.valueOf(form_id));
            filledFormList.setMobileCreatedAt("" + System.currentTimeMillis());
            filledFormList.setMobileUpdatedAt("" + System.currentTimeMillis());
            LocationBean locationBean = new LocationBean();
            Location locationData = locationReceiver.getLocationData().getValue();
            if (locationData != null) {
                longitude = "" + locationData.getLongitude();
                latitude = "" + locationData.getLatitude();
                accuracy = "" + locationData.getAccuracy();
            }
            locationBean.setAccuracy(accuracy);
            locationBean.setLat(latitude);
            locationBean.setLng(longitude);

            filledFormList.setLocation(locationBean);
            filledFormList.setLanguage(userLanguage());
            List<QuestionBeanFilled> answerFilledList = new ArrayList<>();
            answerFilledList.addAll(answerBeanHelperList.values());
            QuestionsUtils.Companion.sortAnsList(answerFilledList);
            filledFormList.setQuestion(answerFilledList);
            filledFormList.setMedia(formIsMedia);
            JSONObject jsonObject = singletonForm.getJsonObject();
            HashMap<String, Boolean> answerMapper = new HashMap<>();
            for (QuestionBean questionBean : questionBeenList.values()) {
                QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                if (questionBeanFilled != null) {
                    String columnName = questionBean.getColumnName();
                    String value = getAnswerForm(questionBeanFilled);
                    if (!answerMapper.containsKey(columnName)) {
                        try {
                            jsonObject.put(columnName, value);
                            answerMapper.put(columnName, questionBeanFilled.isFilled());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (!answerMapper.get(columnName)) {
                        try {
                            jsonObject.put(columnName, value);
                            answerMapper.put(columnName, questionBeanFilled.isFilled());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }
            }
            try {
                jsonObject.put(Constant.TIME_TAKKEN, String.valueOf(time));
                jsonObject.put(Constant.LOCATION, locationBean);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            singletonForm.setJsonObject(jsonObject);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            hideLoading();
            if (isFinish) {
                if (status == SUBMITTED) {
                    showCustomToast(getString(R.string.form_submitted_successfully), 0);
                } else if (status == DRAFT) {
                    showCustomToast(getString(R.string.form_saved_draft), 2);
                }
                saved = true;
                setResult(Activity.RESULT_OK);
                myFinishActivity();
            }
            super.onPostExecute(aBoolean);
        }
    }

    String getAnswerForm(QuestionBeanFilled questionBeanFilled) {
        StringBuilder answerBuilder = new StringBuilder();
        String prefix = "";
        for (Answers answers : questionBeanFilled.getAnswer()) {
            answerBuilder.append(prefix);
            prefix = ",";
            if (questionBeanFilled.getInput_type().equals(AppConfing.TEMP_QUESTION)
                    || questionBeanFilled.getInput_type().equals(AppConfing.QUS_ADDRESS)) {
                answerBuilder.append(answers.getTextValue());
            } else {
                answerBuilder.append(answers.getValue());
            }
        }
        return answerBuilder.toString();
    }


    UnansweredListener unansweredListener = new UnansweredListener() {
        @Override
        public void Question(String questionUid) {
            BaseType baseType = questionObjectList.get(questionUid);
            if (baseType != null) {
                int position = baseType.getViewIndex();
                int parentPosition = linearLayout.getTop() + 1;
                if (position >= 0 && position < linearLayout.getChildCount()) {
                    int childPosition = linearLayout.getChildAt(position).getTop();
                    final int scrollPosition = parentPosition + childPosition;
                    moveToPosition(scrollPosition);
                } else {
                    showToast(R.string.question_still_not_loaded);
                }
            } else {
                showToast(R.string.question_still_not_loaded);

            }

        }
    };

    void myFinishActivity() {
        finish();

    }


    @Override
    protected void onStop() {

        hideProgess();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
        super.onStop();
    }

    @Override
    protected void onStart() {

        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));


        super.onStart();
    }

    @Override
    protected void onDestroy() {
        unansweredListener = null;
        super.onDestroy();
    }


    class LoadQuestion extends AsyncTask<Void, Void, List<QuestionBean>> {
        @Override
        protected void onPreExecute() {
            showProgress(ctx, getString(R.string.preparing_form));
            super.onPreExecute();
        }

        @Override
        protected List<QuestionBean> doInBackground(Void... voids) {
            List<QuestionBean> questionBeanList = new ArrayList<>();
            formModel = singletonForm.getForm();
            JSONObject jsonObject = singletonForm.getJsonObject();
            try {
                uniqueTransactionId = jsonObject.getString("uniqueId");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (formModel != null) {
                    form_id = formModel.getFormId();
                    formIsMedia = formModel.isMedia();
                    isLocationRequired = formModel.isLocation();
                    formVersion = formModel.getVersion();
                    String userLanguage = userLanguage();
                    boolean isFoundLanguage = false;
                    for (LanguageBean languageBean : formModel.getLanguage()) {
                        if (languageBean.getLng().equals(userLanguage)) {
                            isFoundLanguage = true;
                            break;
                        }
                    }
                    userLanguage = isFoundLanguage ? userLanguage() : "en";
                    for (LanguageBean languageBean : formModel.getLanguage()) {
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
                    }

                    duplicateCheckQuestions = formModel.getDuplicateCheckQuestions();


                }
            } catch (Exception ee) {
                return null;
            }

            return questionBeanList;
        }

        @Override
        protected void onPostExecute(List<QuestionBean> questionBeans) {
            super.onPostExecute(questionBeans);
            getSupportActionBar().setTitle(titleInLanguage);
            checkRequiredStuff(questionBeans);
            hideLoader();

        }
    }


    public void prepareQuestionView() {

        new LoadQuestion().execute();

    }

    void checkRequiredStuff(final List<QuestionBean> questionBeanList) {

        QuestionsUtils.Companion.sortQusList(questionBeanList);
        for (QuestionBean questionBean : questionBeanList) {
            questionBeenList.put(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), questionBean);
        }

        if (questionBeenList.size() > 0) {
            if (isLocationRequired) {
                if (permissionHandler.checkGpsPermission()) {
                    locationHandler.startGpsService();
                    AddNewObjectView(questionBeenList);
                    saved = false;
                } else {
                    permissionHandler.requestGpsPermission();
                }

            } else {
                AddNewObjectView(questionBeenList);
                saved = false;
            }

        } else {
            saved = true;
            linearLayout.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
        }


    }

    public String readJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void acceptedPermission(@NotNull int[] grantResults) {

        locationHandler.startGpsService();
        if (questionBeenList.size() > 0) {
            showProgress(ctx, getString(R.string.preparing_form));
            AddNewObjectView(questionBeenList);
            saved = false;
        }

    }

    @Override
    public void deniedPermission(boolean isNeverAskAgain) {
        if (isNeverAskAgain) {
            RemoveAnyChange();
        }
    }

    @Override
    public void acceptedGPS() {

    }

    @Override
    public void deniedGPS() {
        RemoveAnyChange();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationHandler.onActivityResult(requestCode, resultCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

}
