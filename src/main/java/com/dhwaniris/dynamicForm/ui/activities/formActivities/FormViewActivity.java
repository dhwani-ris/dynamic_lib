package com.dhwaniris.tata_trust_delta.ui.activities.formActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dhwaniris.tata_trust_delta.NetworkModule.AppConfig;
import com.dhwaniris.tata_trust_delta.R;
import com.dhwaniris.tata_trust_delta.adapters.UnansweredQusAdapter;
import com.dhwaniris.tata_trust_delta.base.BaseActivity;
import com.dhwaniris.tata_trust_delta.db.FilledForms;
import com.dhwaniris.tata_trust_delta.db.VillageWiseFormCount;
import com.dhwaniris.tata_trust_delta.db.dbhelper.ErrorQuestionsBean;
import com.dhwaniris.tata_trust_delta.db.dbhelper.FormErrorDataBean;
import com.dhwaniris.tata_trust_delta.db.dbhelper.LocationBean;
import com.dhwaniris.tata_trust_delta.db.dbhelper.MessageBeanX;
import com.dhwaniris.tata_trust_delta.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.tata_trust_delta.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.tata_trust_delta.db.dbhelper.form.Form;
import com.dhwaniris.tata_trust_delta.db.dbhelper.form.GetDynamicOption;
import com.dhwaniris.tata_trust_delta.db.dbhelper.form.LanguageBean;
import com.dhwaniris.tata_trust_delta.db.dbhelper.form.QuestionBean;
import com.dhwaniris.tata_trust_delta.db.dbhelper.form.ValidationBean;
import com.dhwaniris.tata_trust_delta.interfaces.UnansweredListener;
import com.dhwaniris.tata_trust_delta.locationservice.LocationUpdatesService;
import com.dhwaniris.tata_trust_delta.persistance.DataRepository;
import com.dhwaniris.tata_trust_delta.questionTypes.BaseType;
import com.dhwaniris.tata_trust_delta.utils.Constant;
import com.dhwaniris.tata_trust_delta.utils.LocationHandler;
import com.dhwaniris.tata_trust_delta.utils.LocationHandlerListener;
import com.dhwaniris.tata_trust_delta.utils.LocationReceiver;
import com.dhwaniris.tata_trust_delta.utils.PermissionHandler;
import com.dhwaniris.tata_trust_delta.utils.PermissionHandlerListener;
import com.dhwaniris.tata_trust_delta.utils.QuestionsUtils;
import com.dhwaniris.tata_trust_delta.utils.Utility;
import com.dhwaniris.tata_trust_delta.utils.ViewUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmList;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;
import static com.dhwaniris.tata_trust_delta.NetworkModule.AppConfig.DRAFT;
import static com.dhwaniris.tata_trust_delta.NetworkModule.AppConfig.EDITABLE_DARFT;
import static com.dhwaniris.tata_trust_delta.NetworkModule.AppConfig.EDITABLE_SUBMITTED;
import static com.dhwaniris.tata_trust_delta.NetworkModule.AppConfig.NEW_FORM;
import static com.dhwaniris.tata_trust_delta.NetworkModule.AppConfig.REJECTED;
import static com.dhwaniris.tata_trust_delta.NetworkModule.AppConfig.SUBMITTED;
import static com.dhwaniris.tata_trust_delta.NetworkModule.AppConfig.SYNCED;
import static com.dhwaniris.tata_trust_delta.NetworkModule.AppConfig.SYNCED_BUT_EDITABLE;


public class FormViewActivity extends BaseFormActivity implements View.OnClickListener
        , PermissionHandlerListener, LocationHandlerListener {

    @BindView(R.id.save_Layout)
    LinearLayout saveLayout;
    private LocationManager locMnagaer;
    private String longitutde = "0.0";
    private String latitude = "0.0";
    private String accuracy = "0.0";
    private long time = 0, lastTimestamp = 0;
    Context ctx;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.submit)
    Button submit;
    protected boolean isErrorViewRequired = false;

    public FilledForms originalFilledFormList;
    private boolean isListLoaded;
    private Form formModel;
    RealmList<QuestionBean> tempQuestionbeanList = new RealmList<>();
    saynList saynList;

    RealmList<String> duplicateCheckQuestions;

    @Inject
    DataRepository dataRepository;

    Disposable disposable;


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        if (!isListLoaded) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setVisibility(View.GONE);
                    layout.setVisibility(View.GONE);
                    // showLoading();
                    showProgress(FormViewActivity.this, getString(R.string.preparing_form));
                }
            });
        }
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);

        saynList.cancel(true);
        hideProgess();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        ButterKnife.bind(this);
        ctx = FormViewActivity.this;
        filledFormList = new FilledForms();
        questionBeenList = new LinkedHashMap<>();
        childQuestionBeenList = new LinkedHashMap<>();
        errorMsgDataList = new RealmList<>();
        BaseActivity.showMessage("oncreate " + LocalTime());

        lastTimestamp = System.currentTimeMillis();

        locationHandler = new LocationHandler(this);
        permissionHandler = new PermissionHandler(this, this);
        locationReceiver = new LocationReceiver(locationDataN);
        locationHandler.setGPSonOffListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        count.setOnClickListener(this);

        Intent i = getIntent();
        if (i.getExtras() != null) {
            tid = i.getStringExtra(Constant.TRANSACTION_ID);
            if (tid.equals("")) {
                Toast.makeText(ctx, "Data synced to server", Toast.LENGTH_SHORT).show();
                hideProgess();
                finishActivityWithResult(NEW_FORM);

            }

            BaseActivity.showMessage("q up" + LocalTime());
            Realm realm = Realm.getDefaultInstance();
            FilledForms mainList = realm.where(FilledForms.class).equalTo("transactionId", tid).findFirst();
            BaseActivity.showMessage("q down" + LocalTime());

            if (mainList != null) {

                filledFormList = realm.copyFromRealm(mainList);
                originalFilledFormList = realm.copyFromRealm(mainList);
                realm.close();
                if (filledFormList.getUpload_status() == SUBMITTED) {
                    formStatus = SUBMITTED;
                    save.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
                } else if (filledFormList.getUpload_status() == SYNCED
                        || filledFormList.getUpload_status() == SYNCED_BUT_EDITABLE || filledFormList.getUpload_status() == EDITABLE_DARFT) {
                    isErrorViewRequired = true;
                    formStatus = filledFormList.getUpload_status();
                    String errorFormLanguage = filledFormList.getLanguage();
                    save.setOnClickListener(this);
                    save.setVisibility(View.VISIBLE);
                    submit.setOnClickListener(this);

                } else if (filledFormList.getUpload_status() == EDITABLE_SUBMITTED) {
                    formStatus = EDITABLE_SUBMITTED;
                    save.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
                } else if (filledFormList.getUpload_status() == REJECTED) {
                    formStatus = SUBMITTED;
                    save.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);

                } else {
                    formStatus = DRAFT;
                    save.setOnClickListener(this);
                    submit.setOnClickListener(this);
                }
                BaseActivity.showMessage("copy complete" + LocalTime());

                //assign all questions to form
                for (QuestionBeanFilled questionBeanFilled : filledFormList.getQuestion()) {
                    answerBeanHelperList.put(QuestionsUtils.Companion.getAnswerUniqueId(questionBeanFilled), questionBeanFilled);
                }

                time = Long.parseLong(filledFormList.getTimeTaken());

                BaseActivity.showMessage("q2 edn" + LocalTime());
                saynList = new saynList();
                saynList.execute();
            } else {
                saved = true;
                BaseActivity.logDatabase(AppConfig.END_POINT, "Invalid Form."
                        , AppConfig.UNEXPECTED_ERROR, "FormViewActivity");
                hideProgess();
                finishActivityWithResult(NEW_FORM);

            }
            realm.close();
        } else {
            saved = true;
            BaseActivity.logDatabase(AppConfig.END_POINT, "Invalid Form."
                    , AppConfig.UNEXPECTED_ERROR, "FormViewActivity");
            hideProgess();
            finishActivityWithResult(NEW_FORM);

        }
    }


    //adding dynamic view in LinearLayout
    private void AddNewObjectView(LinkedHashMap<String, QuestionBean> questionBeanRealmList) {
/*////
        QuestionBean questionBean1 = questionBeanRealmList.get("2");
        ValidationBean validationBean = new ValidationBean();
        validationBean.set_id(AppConfig.VAL_RANDOM_SELECTION);
        validationBean.setError_msg("2");
        questionBean1.getValidation().add(validationBean);
/////*/

        for (QuestionBean questionBean : questionBeanRealmList.values()) {

            if (formStatus == EDITABLE_DARFT) {
                QuestionBeanFilled answer = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                if (!answer.isFilled() && answer.isRequired()
                        && !QuestionsUtils.Companion.isLoopingType(answer)) {
                    questionBean.setEditable(true);
                }
                /*if(!answer.isValidAns())
                {
                    questionBean.setEditable(true);
                }*/
            }

            createViewObject(questionBean, formStatus);
        }
        changeAnswerBeanHelperTitle(questionBeenList, answerBeanHelperList, formStatus);
    }

    private void changeAnswerBeanHelperTitle
            (LinkedHashMap<String, QuestionBean> questionBean, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList,
             int formStatus) {
        if (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT || formStatus == EDITABLE_SUBMITTED) {
            for (QuestionBeanFilled questionBeanFilled : answerBeanHelperList.values()) {
                QuestionBean questionBean1 = questionBean.get(QuestionsUtils.Companion.getAnswerUniqueId(questionBeanFilled));
                if (questionBean1 != null) {
                    questionBeanFilled.setTitle(questionBean1.getTitle());

                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isLocationRequired) {
            if (permissionHandler.checkGpsPermission()) {
                locationHandler.startGpsService();
            }
        }
        alreadyRequest = false;
    }

    @Override
    protected void onPause() {

        if (isLocationRequired) {
            locationHandler.onStop();
        }

        if (!saved && (formStatus == DRAFT || formStatus == EDITABLE_DARFT)) {
            saveData(false, filledFormList, (formStatus == EDITABLE_DARFT) ? EDITABLE_DARFT : DRAFT, false);
        }

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (requirdAlertMap.containsValue(true)) {
            List<String> orderlist = checkRegexHashMap(requirdAlertMap);
            if (!orderlist.isEmpty()) {
                String text = answerBeanHelperList.get(orderlist.get(0)).getAnswer().get(0).getValue();

                checkAlert(text, questionBeenList.get(orderlist.get(0)));
            }
        } else if (focusOnEditext && !notifyOnchangeMap.isEmpty() && !validateOnChangeListeners()) {
            focusOnEditext = false;
            return;
        } else if (formStatus == DRAFT || formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT && filledFormList != originalFilledFormList) {
            new AlertDialog.Builder(ctx)
                    .setTitle(R.string.save_data)
                    .setMessage(R.string.do_you_want_to_save_as_draft)
                    .setNeutralButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //saving changed data in draft
                            saveData(false, filledFormList, (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) ? EDITABLE_DARFT : DRAFT, true);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //saving original data in form if no draft
                            saved = true;
                            finish();
                            //         saveData(true, originalFilledFormList, (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) ? EDITABLE_DARFT : DRAFT, true);
                        }
                    })
                    .setCancelable(true)
                    .show();
        } else {
            finishActivityWithResult(NEW_FORM);

        }
    }

    @Override
    public void onClick(View view) {
        ViewUtil.Companion.preventDoubleClicks(view);
        switch (view.getId()) {

            case R.id.save:
                if (requirdAlertMap.containsValue(true)) {
                    List<String> orderlist = checkRegexHashMap(requirdAlertMap);
                    if (!orderlist.isEmpty()) {
                        String text = answerBeanHelperList.get(orderlist.get(0)).getAnswer().get(0).getValue();

                        checkAlert(text, questionBeenList.get(orderlist.get(0)));
                    }
                } else if (focusOnEditext && !notifyOnchangeMap.isEmpty() && !validateOnChangeListeners()) {
                    focusOnEditext = false;
                    new AlertDialog.Builder(ctx)
                            .setTitle(R.string.save_data)
                            .setMessage(R.string.do_you_want_to_save_as_draft)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    saveData(false, filledFormList, (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) ? EDITABLE_DARFT : DRAFT, true);
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .setCancelable(true)
                            .show();
                    return;
                }
                //draft save action
                else if (getValidAnsCount() > 0) {

                    new AlertDialog.Builder(ctx)
                            .setTitle(R.string.save_data)
                            .setMessage(R.string.do_you_want_to_save_as_draft)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    saveData(false, filledFormList, (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) ? EDITABLE_DARFT : DRAFT, true);
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .setCancelable(true)
                            .show();

                } else {
                    Snackbar.make(submit, R.string.please_fill_at_least_one_field, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.ok, null)
                            .show();
                }

                break;

            case R.id.submit:
                //submit action
                if (isValidLimitOfForm()) {
                    validateData();
                } else {
                    Toast.makeText(ctx, R.string.form_limit_exceed, Toast.LENGTH_SHORT).show();
                    //showCustomToast("Form fill Limit Exceed", 2);
                }
                break;

            case R.id.count:
                //submit action
                showUnansweredQuestions(new ArrayList<>(answerBeanHelperList.values()), true);
                break;
        }

    }

    boolean isValidLimitOfForm() {
        Realm realmx = Realm.getDefaultInstance();
        long submmitedCount = realmx.where(FilledForms.class).equalTo("formId", String.valueOf(form_id))
                .equalTo("upload_status", AppConfig.SUBMITTED).count();
        Form dataBean = realmx.where(Form.class).equalTo("formId", form_id).findFirst();
        VillageWiseFormCount village = realmx.where(VillageWiseFormCount.class).equalTo("formId", form_id).findFirst();
        if (village != null) {
            int formCOunt = village.getCount();
            if (submmitedCount + formCOunt >= dataBean.getFillCount()) {
                realmx.close();
                return false;
            }
        }
        realmx.close();
        return true;
    }

    //Validating data before save
    private void validateData() {

        if (requirdAlertMap.containsValue(true)) {
            List<String> orderlist = checkRegexHashMap(requirdAlertMap);
            if (!orderlist.isEmpty()) {
                String text = answerBeanHelperList.get(orderlist.get(0)).getAnswer().get(0).getValue();

                checkAlert(text, questionBeenList.get(orderlist.get(0)));
            }
        } else if (focusOnEditext && !notifyOnchangeMap.isEmpty() && !validateOnChangeListeners()) {
            focusOnEditext = false;
            refreshLoopingFiltersQuestions();
            List<QuestionBeanFilled> tempList = findInvalidAnswersList(answerBeanHelperList, questionObjectList);

            if (!tempList.isEmpty()) {
                showUnansweredQuestions(tempList, false);
            }
            return;
        } else if (!answerBeanHelperList.isEmpty()) {
            validateOnChangeListeners();
            refreshLoopingFiltersQuestions();
            List<QuestionBeanFilled> tempList = findInvalidAnswersList(answerBeanHelperList, questionObjectList);

            if (!tempList.isEmpty()) {
                showUnansweredQuestions(tempList, false);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                saveData(false, filledFormList, formStatus == EDITABLE_DARFT || formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_SUBMITTED ? EDITABLE_SUBMITTED : SUBMITTED, true);
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();


            }

        } else {
            BaseActivity.logDatabase(AppConfig.END_POINT, "Invalid Form. Line No. 406"
                    , AppConfig.UNEXPECTED_ERROR, "FormViewActivity");
        }
    }

    //show unanswered question
    private void showUnansweredQuestions(List<QuestionBeanFilled> tempList, boolean showAll) {
        //creating dropdown selector
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_selector_layout, null);
        TextView title = view.findViewById(R.id.dTitle);
        ImageView close = view.findViewById(R.id.dClose);

        RecyclerView recyclerView = view.findViewById(R.id.dRecycler);
        dialog.setView(view);
        title.setText(R.string.pending_questions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final android.app.AlertDialog alertDialog = dialog.create();
        UnansweredQusAdapter adapter = new UnansweredQusAdapter(unansweredListener, tempList, alertDialog, showAll, questionBeenList);
        recyclerView.setAdapter(adapter);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    //save data in DataBase
    private void saveData(final boolean isDISCARD, final FilledForms formData,
                          final int status, final boolean isFinish) {

        LocationBean locationBean = new LocationBean();
        Location locationData = locationReceiver.getLocationData().getValue();
        if (locationData != null) {
            longitutde = "" + locationData.getLongitude();
            latitude = "" + locationData.getLatitude();
            accuracy = "" + locationData.getAccuracy();
        }

        locationBean.setAccuracy(accuracy);
        locationBean.setLat(latitude);
        locationBean.setLng(longitutde);
        long timestamp = System.currentTimeMillis();
        time = time + timestamp - lastTimestamp;
        formData.setTimeTaken(String.valueOf(time));
        formData.setVersion(formModel.getVersion());
        formData.setFormId(String.valueOf(formModel.getFormId()));
        formData.setMobileUpdatedAt("" + System.currentTimeMillis());
        formData.setUpload_status(status);


        RealmList<QuestionBeanFilled> answerFilledList = new RealmList<>();
        answerFilledList.addAll(answerBeanHelperList.values());
        QuestionsUtils.Companion.sortAnsList(answerFilledList);
        formData.setQuestion(answerFilledList);

        dataRepository.getLocalRepository().submitForm(formData, status, formModel, answerBeanHelperList).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                showLoading();
                disposable = d;
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                hideLoading();
                saved = true;
                if (isFinish) {
                    if (status == SUBMITTED) {
                        showCustomToast(getString(R.string.form_submitted_successfully), 0);
                    } else if (status == 2 && !isDISCARD) {
                        showCustomToast(getString(R.string.form_saved_draft), 2);
                    }
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {
                hideLoading();
                showToast(R.string.dublicate_record);
            }
        });


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


    //get filled fields count
    private int getValidAnsCount() {
        int ans = 0, total = 0, opt = 0;


        for (QuestionBeanFilled questionBeanFilled : answerBeanHelperList.values()) {
            if (questionBeanFilled.isFilled()) {
                ans++;
            }
        }
        return ans;
    }


    @Override
    protected void onDestroy() {
        unansweredListener = null;
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();

    }


    @Override
    public void acceptedPermission(@NotNull int[] grantResults) {
        saved = false;
        locationHandler.startGpsService();
    }

    @Override
    public void deniedPermission(boolean isNeverAskAgain) {
        if (isNeverAskAgain)
            saveData(true, originalFilledFormList, (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) ? EDITABLE_DARFT : DRAFT, true);
    }

    @Override
    public void acceptedGPS() {

    }

    @Override
    public void deniedGPS() {
        saveData(true, originalFilledFormList, (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) ? EDITABLE_DARFT : DRAFT, true);

    }

    public class saynList extends AsyncTask<Void, Void, Void> {
        private boolean runPost = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            Realm realm1 = Realm.getDefaultInstance();
            try {

                Form dataBean = realm1.where(Form.class).equalTo("_id",
                        filledFormList.getFormUiniqueId()).findFirst();

                if (dataBean != null) {
                    formModel = realm1.copyFromRealm(dataBean);
                    form_id = formModel.getFormId();
                    runPost = true;

                } else {
                    saved = true;
                    BaseActivity.logDatabase(AppConfig.END_POINT, "Invalid Form."
                            , AppConfig.UNEXPECTED_ERROR, "FormViewActivity");
                    hideProgess();
                    finishActivityWithResult(NEW_FORM);
                }
            } finally {
                realm1.close();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (runPost) {
                isLocationRequired = formModel.isLocation() && formStatus != SUBMITTED;
                if (isLocationRequired) {
                    if (permissionHandler.checkGpsPermission()) {
                        locationHandler.startGpsService();
                    } else {
                        saved = true;
                        permissionHandler.requestGpsPermission();
                    }

                }
                RealmList<LanguageBean> lBean = formModel.getLanguages();

                String userLanguage = userLanguage();
                boolean isFoundLanguage = false;
                for (LanguageBean languageBean : formModel.getLanguages()) {
                    if (languageBean.getLng().equals(userLanguage)) {
                        isFoundLanguage = true;
                        break;
                    }
                }
                userLanguage = isFoundLanguage ? userLanguage() : "en";
                for (LanguageBean languageBean : lBean) {
                    if (languageBean.getLng().equals(userLanguage)) {
                        getSupportActionBar().setTitle(languageBean.getTitle());
                        tempQuestionbeanList = languageBean.getQuestion();

                        if (formModel != null && !tempQuestionbeanList.isEmpty()) {
                            checkValidationOnDynamicView();
                        } else {
                            BaseActivity.logDatabase(AppConfig.END_POINT, "Invalid Form."
                                    , AppConfig.UNEXPECTED_ERROR, "FormViewActivity");
                        }
                        break;
                    }

                }
                if (formModel != null) {
                    duplicateCheckQuestions = formModel.getDuplicateCheckQuestions();
                }


            }


            hideLoader();
            if (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) {
                List<QuestionBeanFilled> tempList = findInvalidAnswersList(answerBeanHelperList, questionObjectList);

                if (!tempList.isEmpty()) {
                    showUnansweredQuestions(tempList, false);
                }
            }
        }
    }


    private void hideLoader() {
        if (!isListLoaded) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // hideLoading();
                    hideProgess();
                    layout.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    if (formModel != null)
                        if (Utility.isUpdateNeeded(formModel.getMinAppVersion())) {
                            BaseActivity.versionDialog(ctx);
                        }
                }
            }, 1000);
        }
        isListLoaded = true;
    }

    public void checkValidationOnDynamicView() {

        List<ErrorQuestionsBean> editableQuestionOrderList;
        editableQuestionOrderList = (formStatus == AppConfig.SYNCED_BUT_EDITABLE) || (formStatus == EDITABLE_DARFT)
                ? getEditableOdresList() : new ArrayList<ErrorQuestionsBean>();

        HashMap<String, RealmList<MessageBeanX>> messageHashMap = new HashMap<>();

        for (ErrorQuestionsBean questionsBean : editableQuestionOrderList) {
            messageHashMap.put(QuestionsUtils.Companion.getErrorQuestionsUid(questionsBean), questionsBean.getMessage());

        }

        //    if (filledFormList.getVersion().equals(formModel.getVersion())) {
        validateQuestionAns(tempQuestionbeanList, answerBeanHelperList);
        //  }

        QuestionsUtils.Companion.sortQusList(tempQuestionbeanList);
        for (QuestionBean questionBean : tempQuestionbeanList) {
            questionBean.setEditable(messageHashMap.containsKey(QuestionsUtils.Companion.getQuestionUniqueId(questionBean)));
            QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
            if (questionBeanFilled != null && questionBeanFilled.isRequired() && !questionBeanFilled.isValidAns() && !QuestionsUtils.Companion.isLoopingType(questionBean)) {
                questionBean.setEditable(true);
            }
            if (questionBean.getOrder().contains(".")) {
                childQuestionBeenList.put(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), questionBean);
            } else {
                questionBeenList.put(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), questionBean);
            }
        }

        //validate dynamicOptoin

        if (formStatus == DRAFT && !formModel.getGetDynamicOptionsList().isEmpty()) {
            for (GetDynamicOption getDynamicOption : formModel.getGetDynamicOptionsList()) {
                validateDynamicOption(getDynamicOption, answerBeanHelperList, questionBeenList);
            }
        }

        AddNewObjectView(questionBeenList);

        for (ErrorQuestionsBean questionBean : editableQuestionOrderList) {
            if (messageHashMap.containsKey(QuestionsUtils.Companion.getErrorQuestionsUid(questionBean))) {
                //  questionObjectList.get(Integer.parseInt(questionBean.getOrder()) - 1).superChangeStatus(NOT_ANSWERED);
                questionObjectList.get(QuestionsUtils.Companion.getErrorQuestionsUid(questionBean)).superSetErrorMsg(setErrorMsg(questionBean.getMessage()));
            }

        }

    }


    void validateDynamicOption(GetDynamicOption getDynamicOption, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList, LinkedHashMap<String, QuestionBean> questionBeenList) {
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(getDynamicOption.getOrderToDisplayIn());
        QuestionBean questionBean = questionBeenList.get(getDynamicOption.getOrderToDisplayIn());
        if (questionBeanFilled != null && questionBean != null) {
            RealmList<AnswerOptionsBean> ansOptionFromQuestionAfterFilter = QuestionsUtils.Companion.getAnsOptionFromQuestionAfterFilter(questionBean, questionBeenList, answerBeanHelperList, userLanguage(), form_id);
            if (!QuestionsUtils.Companion.validateAnswerListWithAnswerOptions(questionBeanFilled.getAnswer(), ansOptionFromQuestionAfterFilter)) {
                clearAnswerAndView(questionBean);
            }
        }
    }

    public void validateQuestionAns(RealmList<QuestionBean> tempQuestionbeanList, LinkedHashMap<String, QuestionBeanFilled> beanFilledLinkedHashMap) {
        for (QuestionBean questionBean : tempQuestionbeanList) {
            QuestionBeanFilled questionBeanFilled = beanFilledLinkedHashMap.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
            if (questionBeanFilled == null && !questionBean.getOrder().contains(".")) {
                createOrModifyAnswerBeanObject(questionBean, true);
            }
        }


    }

    public String setErrorMsg(RealmList<MessageBeanX> messageBean) {
        String msg = "";
        String defalutMsg = "";

        for (MessageBeanX message : messageBean) {
            if (userLanguage().equals(message.getLng())) {
                msg = message.getText();
                break;
            }
            if (message.getLng().equals("en")) {
                defalutMsg = message.getText();
            }
        }
        return msg.isEmpty() ? defalutMsg : msg;

    }

    private void finishActivityWithResult(int status) {
        Intent intent = getIntent();
        intent.putExtra(AppConfig.DRAFT_SAVED, status);
        setResult(Constant.REQUEST_FOR_FORM_SAVED, intent);
        finish();
    }

    List<ErrorQuestionsBean> getEditableOdresList() {
        List<ErrorQuestionsBean> editableOrderList = new ArrayList<>();

        Realm realmtemp = Realm.getDefaultInstance();
        FormErrorDataBean responseToBeEditDetails = realmtemp.where(FormErrorDataBean.class).equalTo("transactionId", tid).findFirst();
        if (responseToBeEditDetails != null) {
            for (ErrorQuestionsBean ordersToEdit : responseToBeEditDetails.getQuestions()) {
                if (ordersToEdit.isEdit()) {
                    editableOrderList.add(ordersToEdit);
                    errorMsgDataList.add(ordersToEdit);

                }
            }
        }
        // realmtemp.close();
        return editableOrderList;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return isErrorViewRequired;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_error:
                List<QuestionBeanFilled> tempList = findErrorMsgList(answerBeanHelperList, questionObjectList, errorMsgDataList);

                if (!tempList.isEmpty()) {
                    showUnansweredQuestions(tempList, false);
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.error_view, menu);
        return true;
    }

    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GPS_EVENT_STARTED:
                    break;
                case GPS_EVENT_STOPPED:
                    if (isLocationRequired)
                        locationHandler.startGpsService();
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationHandler.onActivityResult(requestCode, resultCode);

    }

}