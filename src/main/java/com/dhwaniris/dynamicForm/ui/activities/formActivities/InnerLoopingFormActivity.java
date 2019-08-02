package com.dhwaniris.dynamicForm.ui.activities.formActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.adapters.UnansweredQusAdapter;
import com.dhwaniris.dynamicForm.base.BaseActivity;
import com.dhwaniris.dynamicForm.db.dbhelper.MessageBeanX;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ChildBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Nested;
import com.dhwaniris.dynamicForm.db.dbhelper.form.OrdersBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ParentBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.PermissionListener;
import com.dhwaniris.dynamicForm.interfaces.UnansweredListener;
import com.dhwaniris.dynamicForm.locationservice.LocationUpdatesService;
import com.dhwaniris.dynamicForm.questionTypes.BaseType;
import com.dhwaniris.dynamicForm.utils.InnerFormData;
import com.dhwaniris.dynamicForm.utils.LocationHandler;
import com.dhwaniris.dynamicForm.utils.LocationHandlerListener;
import com.dhwaniris.dynamicForm.utils.LocationReceiver;
import com.dhwaniris.dynamicForm.utils.PermissionHandler;
import com.dhwaniris.dynamicForm.utils.PermissionHandlerListener;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SYNCED_BUT_EDITABLE;


public class InnerLoopingFormActivity extends BaseFormActivity
        implements View.OnClickListener, PermissionListener, PermissionHandlerListener, LocationHandlerListener {

    Context ctx;
    Toolbar toolbar;
    Button save;
    Button submit;
    EditText loadmore;
    LinearLayout saveLayout;

    protected boolean isErrorViewRequired = false;


    int childCount;
    List<Nested> nestedBean;
    String questionUid;
    ArrayList<String> countListString = new ArrayList<>();
    ArrayList<String> countListValue = new ArrayList<>();
    boolean isFinishSafe = false;
    String originalAnswer;
    HashMap<String, List<MessageBeanX>> messageHashMap = new HashMap<>();
    String formLang = "";

    InnerFormData innerFormData;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        ctx = InnerLoopingFormActivity.this;
        save.setVisibility(View.GONE);
        submit.setText(getString(R.string.ok));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        formStatus = NEW_FORM;
        questionBeenList = new LinkedHashMap<>();
        count.setOnClickListener(this);
        questionObjectList = new HashMap<>();


        locationHandler = new LocationHandler(this);
        permissionHandler = new PermissionHandler(this, this);
        locationReceiver = new LocationReceiver(locationDataN);
        locationHandler.setGPSonOffListener(this);
        FormType = 1;


        String formName = "";
        int formId;
        Intent i = getIntent();
        innerFormData = InnerFormData.Companion.getInstance();
        if (i.getExtras() != null && innerFormData.getQuestionBeanFilled() != null) {
            childCount = i.getIntExtra("count", 0);
            countListString = i.getStringArrayListExtra("countListString");
            countListValue = i.getStringArrayListExtra("countListValue");
            formStatus = i.getIntExtra("tvFormStatus", 0);
            questionUid = i.getStringExtra("questionOrder");
            formId = i.getIntExtra("formId", 0);
            isLocationRequired = i.getBooleanExtra("locationRequired", false);
            formLang = userLanguage();
            if (formStatus == LibDynamicAppConfig.SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) {
                isErrorViewRequired = true;
            }
            getSupportActionBar().setTitle(formName);


            prepareQuestionInBackground(countListString, countListValue);


            if (formStatus != SUBMITTED) {
                save.setOnClickListener(this);
                submit.setOnClickListener(this);
            } else {
                save.setVisibility(View.GONE);
                submit.setVisibility(View.GONE);
            }


        } else {
            saved = true;
            BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT, "CHILD FORM  is null",
                    LibDynamicAppConfig.UNEXPECTED_ERROR, "CHILDFormActivity");
            finish();
        }
    }


    void bindView() {
        toolbar = findViewById(R.id.toolbar);
        save = findViewById(R.id.save);
        submit = findViewById(R.id.submit);
        loadmore = findViewById(R.id.loadmore);
        saveLayout = findViewById(R.id.save_Layout);

    }


    private void showProcessing() {
        linearLayout.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        // showLoading();
        showProgress(InnerLoopingFormActivity.this, getString(R.string.preparing_form));
    }

    private void hideProcessing() {
        layout.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgess();
            }
        }, 100);
    }

    //adding dynamic view in LinearLayout

    private void AddDynamicView(LinkedHashMap<String, QuestionBean> questionBeanRealmList) {
        for (QuestionBean questionBean : questionBeanRealmList.values()) {
            if (formStatus == EDITABLE_DARFT || formStatus == SYNCED_BUT_EDITABLE) {
                QuestionBeanFilled answer = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                if (!answer.isFilled() && answer.isRequired() && !QuestionsUtils.Companion.isLoopingType(answer)) {
                    questionBean.setEditable(true);
                }
            }

            createViewObject(questionBean, formStatus);

            if (formStatus != NEW_FORM && !(formStatus == EDITABLE_DARFT || formStatus == SYNCED_BUT_EDITABLE)) {
                String questionUniqueId = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
                QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUniqueId);
                if (questionBeanFilled != null && (questionBeanFilled.getTitle() == null || questionBeanFilled.getTitle().equals(""))) {
                    createOrModifyAnswerBeanObject(questionBean, questionBean.getParent().size() == 0);
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
        super.onPause();
        if (isLocationRequired) {
            locationHandler.onStop();
        }
    }


    @Override
    public void onBackPressed() {
        if (requirdAlertMap.containsValue(true)) {
            List<String> orderlist = checkRegexHashMap(requirdAlertMap);
            if (!orderlist.isEmpty()) {
                String text = answerBeanHelperList.get(orderlist.get(0)).getAnswer().get(0).getValue();

                checkAlert(text, questionBeenList.get(orderlist.get(0)));

            }
        } else if (!originalAnswer.equals(new Gson().toJson(answerBeanHelperList))) {
            new AlertDialog.Builder(ctx)
                    .setTitle(R.string.save_data)
                    .setMessage(R.string.do_you_want_to_save_as_draft)
                    .setNeutralButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isFinishSafe = true;
                            saveData(DRAFT, true);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RemoveAnyChange();
                        }
                    })
                    .setCancelable(true)
                    .show();

        } else {
            finish();
            overridePendingTransition(R.anim.from_left, R.anim.to_left);
        }

    }

    //Removing changes in form
    private void RemoveAnyChange() {
        isFinishSafe = true;
        Intent intent = new Intent();
        intent.putExtra("childState", DRAFT);
        intent.putExtra("isDiscard", true);
        intent.putExtra("questionOrder", questionUid);
        setResult(NESTEDCHILD_CODE, intent);
        finish();
        overridePendingTransition(R.anim.from_left, R.anim.to_left);


    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.save) {//draft save action
            if (requirdAlertMap.containsValue(true)) {
                List<String> orderlist = checkRegexHashMap(requirdAlertMap);
                if (!orderlist.isEmpty()) {
                    String text = answerBeanHelperList.get(orderlist.get(0)).getAnswer().get(0).getValue();

                    checkAlert(text, questionBeenList.get(orderlist.get(0)));

                }
            } else if (getValidAnsCount() > 0) {
                new AlertDialog.Builder(ctx)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.do_you_want_to_save_as_draft)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isFinishSafe = true;
                                saveData(DRAFT, true);
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
        } else if (i == R.id.submit) {//submit action
            validateAnswers();
        } else if (i == R.id.count) {//submit action
            showUnansweredQuestions(new ArrayList<>(answerBeanHelperList.values()), true);
        }

    }

    //Validating data before save
    private void validateAnswers() {
        if (!answerBeanHelperList.isEmpty()) {
            List<QuestionBeanFilled> tempList = findInvalidAnswersList(answerBeanHelperList, questionObjectList);

            if (!tempList.isEmpty()) {
                showUnansweredQuestions(tempList, false);
            } else if (requirdAlertMap.containsValue(true)) {
                List<String> orderlist = checkRegexHashMap(requirdAlertMap);
                if (!orderlist.isEmpty()) {
                    String text = answerBeanHelperList.get(orderlist.get(0)).getAnswer().get(0).getValue();

                    checkAlert(text, questionBeenList.get(orderlist.get(0)));
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.save_data)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isFinishSafe = true;
                                saveData(SUBMITTED, true);
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }


        } else {
            BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT, "Validation error question size 0",
                    LibDynamicAppConfig.UNEXPECTED_ERROR, "FormActivity");
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
    private void saveData(final int status, final boolean isFinish) {


        if (isFinish) {
            for (Nested nested : nestedBean) {
                for (QuestionBeanFilled questionBeanFilled : nested.getAnswerNestedData()) {
                    String uniqueOrder = QuestionsUtils.Companion.getAnswerUniqueId(questionBeanFilled);
                    String orderKey = uniqueOrder.split("_")[1];
                    questionBeanFilled.setOrder(orderKey);
                }
            }
            InnerFormData.Companion.saveNestedAns(nestedBean);
            Intent intent = new Intent();
            intent.putExtra("childState", status);
            intent.putExtra("questionOrder", questionUid);
            intent.putExtra("isDiscard", false);
            setResult(NESTEDCHILD_CODE, intent);
            finish();
            overridePendingTransition(R.anim.from_left, R.anim.to_left);
        } else {
            List<Nested> tempNestedList = new ArrayList<>();
            Gson gson = new Gson();
            for (Nested nested : nestedBean) {
                Nested newTempNested = gson.fromJson(gson.toJson(nested), Nested.class);
                for (QuestionBeanFilled questionBeanFilled : newTempNested.getAnswerNestedData()) {
                    String uniqueOrder = QuestionsUtils.Companion.getAnswerUniqueId(questionBeanFilled);
                    String orderKey = uniqueOrder.split("_")[1];
                    questionBeanFilled.setOrder(orderKey);
                }
                tempNestedList.add(newTempNested);
            }
            InnerFormData.Companion.saveNestedAns(tempNestedList);

        }
    }


    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);

        hideProgess();

        if (!isFinishSafe)
            saveData(NEW_FORM, false);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));


    }

    @Override
    protected void onDestroy() {
        unansweredListener = null;
        super.onDestroy();
    }

    UnansweredListener unansweredListener = questionUid -> {
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

    };


    private void showGPSPermissionAlert() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_required)
                .setMessage(R.string.to_fill_this_form_please_accept_the_gps_permission)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!checkGpsPermission()) {
                            saved = true;
                            requestGpsPermission();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saved = true;
                        finish();
                    }
                })
                .show();

    }

    //get filled fields count
    private int getValidAnsCount() {
        int ans = 0;
        for (QuestionBeanFilled questionBeanFilled : answerBeanHelperList.values()) {
            if (questionBeanFilled.isFilled()) {
                ans++;
            }
        }
        return ans;
    }


    @Override
    public void acceptedPermission() {

    }

    @Override
    public void deniedPermission() {
        RemoveAnyChange();
    }


    class LoadView extends AsyncTask<Void, Void, List<QuestionBean>> {

        List<String> childListString;

        public LoadView(List<String> childListString, List<String> childListValu) {
            this.childListString = childListString;
            this.childListValu = childListValu;
        }

        List<String> childListValu;

        @Override
        protected List<QuestionBean> doInBackground(Void... voids) {

            ArrayList<QuestionBean> questionBeanRealmList = new ArrayList<>();

            Gson gson = new Gson();
            QuestionBeanFilled questionBeanFilled1 = innerFormData.getQuestionBeanFilled();
            QuestionBeanFilled questionBeanFilled2 = gson.fromJson(gson.toJson(questionBeanFilled1), QuestionBeanFilled.class);

            nestedBean = questionBeanFilled2.getNestedAnswer();
            for (Nested nested : nestedBean) {
                String forParentValue = nested.getForParentValue();
                for (QuestionBeanFilled questionBeanFilled : nested.getAnswerNestedData()) {
                    questionBeanFilled.setOrder(QuestionsUtils.Companion.getUpdatedChildOrder(forParentValue, questionBeanFilled.getOrder()));
                    answerBeanHelperList.put(QuestionsUtils.Companion.getAnswerUniqueId(questionBeanFilled), questionBeanFilled);
                }
            }

            if (innerFormData.getQuestionBeanRealmList() != null && !innerFormData.getQuestionBeanRealmList().isEmpty()) {
                questionBeanRealmList.addAll(createQuestionNumberofTime(innerFormData.getQuestionBeanRealmList(), childListString, childListValu));
            } else {
                submit.setVisibility(View.GONE);
            }
            return questionBeanRealmList;
        }

        @Override
        protected void onPostExecute(List<QuestionBean> questionBeans) {

            if (innerFormData.getQuestionBeanRealmList() != null && innerFormData.getQuestionBeanRealmList().isEmpty()) {
                submit.setVisibility(View.GONE);
            }
            super.onPostExecute(questionBeans);
            hideProcessing();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProcessing();
        }
    }


    void prepareQuestionInBackground(final ArrayList<String> childListString, final ArrayList<String> childListValue) {
        new LoadView(childListString, childListValue);
    }

    public List<QuestionBean> createQuestionNumberofTime(List<QuestionBean> groupQuestion,
                                                         List<String> childListString, List<String> childListValue) {
        ArrayList<QuestionBean> createdQuestions = new ArrayList<>();
        int viewSequence = 1;
        int index = 0;
        Gson gson = new Gson();
        for (String forParentValue : childListValue) {
            ArrayList<QuestionBean> tempQuestionGroup = new ArrayList<>();
            for (QuestionBean questionBean : groupQuestion) {
                tempQuestionGroup.add(gson.fromJson(gson.toJson(questionBean), QuestionBean.class));
            }
            String firstTitle = tempQuestionGroup.get(0).getTitle();
            firstTitle = firstTitle + " " + childListString.get(index++);
            tempQuestionGroup.get(0).setTitle(firstTitle);
            for (QuestionBean tempQuestionBean : tempQuestionGroup) {
                QuestionBean questionBean1;
                questionBean1 = tempQuestionBean;
                String order = QuestionsUtils.Companion.getQuestionUniqueId(questionBean1);
                String newOrder = QuestionsUtils.Companion.getUpdatedChildOrder(forParentValue, order);
                questionBean1.setOrder(newOrder);
                questionBean1.setViewSequence(String.valueOf(viewSequence++));
                for (ChildBean childBean : questionBean1.getChild()) {
                    String childOrder = childBean.getOrder();
                    childOrder = QuestionsUtils.Companion.getUpdatedChildOrder(forParentValue, childOrder);
                    childBean.setOrder(childOrder);
                }
                for (ParentBean parentBean : questionBean1.getParent()) {
                    String parentOrder = parentBean.getOrder();
                    parentOrder = QuestionsUtils.Companion.getUpdatedChildOrder(forParentValue, parentOrder);
                    parentBean.setOrder(parentOrder);
                }

                boolean isDynamicOrderCreation = false;
                for (ValidationBean validationBean : questionBean1.getValidation()) {
                    if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_DYNAMIC_ORDER_CREATION_FOR_NESTED)) {
                        isDynamicOrderCreation = true;
                        break;
                    }
                }


                for (RestrictionsBean restrictionsBean : questionBean1.getRestrictions()) {
                    if (isDynamicOrderCreation && (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_GET_ANS_OPTION) ||
                            restrictionsBean.getType().equals(LibDynamicAppConfig.REST_GET_ANS_OPTION_FILTER))) {
                        OrdersBean originalOrderBean = restrictionsBean.getOrders().get(0);
                        restrictionsBean.getOrders().clear();
                        if (originalOrderBean != null) {
                            for (String forParentValue2 : childListValue) {
                                OrdersBean ordersBean = new OrdersBean();
                                ordersBean.set_id(originalOrderBean.get_id());
                                ordersBean.setValue(originalOrderBean.getValue());
                                ordersBean.setOrder(QuestionsUtils.Companion.getUpdatedChildOrder(forParentValue2, originalOrderBean.getOrder()));
                                restrictionsBean.getOrders().add(ordersBean);
                            }
                        }
                    } else {
                        for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                            String resOrder = ordersBean.getOrder();
                            resOrder = QuestionsUtils.Companion.getUpdatedChildOrder(forParentValue, resOrder);
                            ordersBean.setOrder(resOrder);
                        }

                    }


                }
                createdQuestions.add(questionBean1);
            }

        }

        return createdQuestions;
    }

    public String setErrorMsg(List<MessageBeanX> messageBean) {
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


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return isErrorViewRequired;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.error_view, menu);
        return true;
    }

    //creating new Answer Object
    private void modifyAnswerBeanObject(QuestionBeanFilled answerBeanHelper, QuestionBean questionBean, final boolean isVisibleInHideList) {


        boolean isVisible = (isVisibleInHideList && !questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LABEL));

        answerBeanHelper.setTitle(questionBean.getTitle());
        answerBeanHelper.setOrder(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
        answerBeanHelper.setInput_type(questionBean.getInput_type());
        List<ValidationBean> valiList = questionBean.getValidation();
        if (!valiList.isEmpty()) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_REQUIRED)) {
                    answerBeanHelper.setRequired(isVisible);
                    answerBeanHelper.setOptional(false);
                    break;
                }
            }
        } else {
            answerBeanHelper.setRequired(false);
            answerBeanHelper.setOptional(isVisible);

        }

    }


    @Override
    public void acceptedPermission(@NotNull int[] grantResults) {
        locationHandler.startGpsService();
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

}
