package com.dhwaniris.dynamicForm.ui.activities.formActivities;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.base.BaseActivity;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.customViews.EditTextWIthButtonView;
import com.dhwaniris.dynamicForm.customViews.ImageRowView;
import com.dhwaniris.dynamicForm.db.FilledForms;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ChildBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Nested;
import com.dhwaniris.dynamicForm.db.dbhelper.form.OrdersBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.EditTextFocusChangeListener;
import com.dhwaniris.dynamicForm.interfaces.ImageSelectListener;
import com.dhwaniris.dynamicForm.interfaces.PermissionListener;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.interfaces.SelectListener;
import com.dhwaniris.dynamicForm.interfaces.UnansweredListener;
import com.dhwaniris.dynamicForm.questionHeplers.DateHelper;
import com.dhwaniris.dynamicForm.questionHeplers.PrefilledData;
import com.dhwaniris.dynamicForm.questionHeplers.PrefilledDefaultData;
import com.dhwaniris.dynamicForm.questionTypes.AudioRecordType;
import com.dhwaniris.dynamicForm.questionTypes.BaseLabelType;
import com.dhwaniris.dynamicForm.questionTypes.BaseType;
import com.dhwaniris.dynamicForm.questionTypes.DateSelect;
import com.dhwaniris.dynamicForm.questionTypes.EditTextAadhar;
import com.dhwaniris.dynamicForm.questionTypes.EditTextNumber;
import com.dhwaniris.dynamicForm.questionTypes.EditTextSimpleText;
import com.dhwaniris.dynamicForm.questionTypes.ImageType;
import com.dhwaniris.dynamicForm.questionTypes.LocationGetType;
import com.dhwaniris.dynamicForm.questionTypes.LoopingMultiSelect;
import com.dhwaniris.dynamicForm.questionTypes.LoopingSingleSelect;
import com.dhwaniris.dynamicForm.questionTypes.MultiSelect;
import com.dhwaniris.dynamicForm.questionTypes.MultiSelectLimit;
import com.dhwaniris.dynamicForm.questionTypes.RadioButtonType;
import com.dhwaniris.dynamicForm.questionTypes.SingleSelect;
import com.dhwaniris.dynamicForm.questionTypes.UnitConversionType;
import com.dhwaniris.dynamicForm.questionTypes.ViewImageWithSingleSelect;
import com.dhwaniris.dynamicForm.ui.activities.FullScreenImageActivity;
import com.dhwaniris.dynamicForm.utils.Constant;
import com.dhwaniris.dynamicForm.utils.DateUtility;
import com.dhwaniris.dynamicForm.utils.DynamicLibUtils;
import com.dhwaniris.dynamicForm.utils.HideQuestionsState;
import com.dhwaniris.dynamicForm.utils.InnerFormData;
import com.dhwaniris.dynamicForm.utils.LocationHandler;
import com.dhwaniris.dynamicForm.utils.LocationReceiver;
import com.dhwaniris.dynamicForm.utils.PermissionHandler;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.REST_CLEAR_DID_CHILD;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.REST_SHOULD_BE_GRATER_THAN;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.REST_SHOULD_BE_LESS_THAN;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.REST_SHOULD_BE_LESS_THAN_EQUAL;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SYNCED_BUT_EDITABLE;
import static java.util.regex.Pattern.matches;


public class BaseFormActivity extends BaseActivity implements SelectListener, ImageSelectListener, UnansweredListener {

    public LinearLayout linearLayout;
    public LinearLayout layout;
    public TextView count;
    public ProgressBar progressBar;
    ScrollView scroll;

    protected LocationReceiver locationReceiver;
    LocationHandler locationHandler;


    public int formStatus = 3;
    public boolean alreadyRequest;
    // public List<QuestionBean> questionsList;
    public FilledForms filledFormList;
    private HashMap<String, String> newTitles;
    public List<HideQuestionsState> hideQuestionsStateList;
    public HashMap<String, String> findNested;
    public HashMap<String, BaseType> questionObjectList;
    private HashMap<String, Integer> twoStepValidation;
    //0 for From 1 for ChildForm
    public static String tid = "";
    protected int FormType = 0;
    protected int form_id;

    AlertDialog.Builder alertDialog;
    AlertDialog ad;
    public HashMap<String, Boolean> requirdAlertMap = new HashMap<>();

    public MutableLiveData<Location> locationDataN = new MutableLiveData<>();

    public boolean focusOnEditext = false;

    public LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList;
    public LinkedHashMap<String, QuestionBean> questionBeenList;
    public LinkedHashMap<String, QuestionBean> childQuestionBeenList;


    PermissionHandler permissionHandler;


    boolean isLocationRequired;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerBeanHelperList = new LinkedHashMap<>();
        hideQuestionsStateList = new ArrayList<>();
        newTitles = new HashMap<>();
        questionObjectList = new HashMap<>();
        twoStepValidation = new HashMap<>();
        findNested = new HashMap<>();
        alertDialog = new AlertDialog.Builder(BaseFormActivity.this);
        ad = alertDialog.show();
        ad.dismiss();

    }

    void superViewBind() {
        linearLayout = findViewById(R.id.linearLayout);
        layout = findViewById(R.id.layout);
        count = findViewById(R.id.count);
        progressBar = findViewById(R.id.progressBar);
        scroll = findViewById(R.id.scroll);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    //hiding keyboard
    public void clearFocus() {
        if (getCurrentFocus() != null) {
            View view = getCurrentFocus();
            view.clearFocus();

        }


    }


    protected void modifyAnswerJson(JSONObject jsonObject, HashMap<String, Boolean> answerMapper) {
        for (QuestionBean questionBean : questionBeenList.values()) {
            QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
            if (questionBeanFilled != null && questionBean.getColumnName() != null && !questionBean.getColumnName().equals("")) {
                String columnName = questionBean.getColumnName();
                String value = getAnswerForm(questionBeanFilled);
                if (!answerMapper.containsKey(columnName)) {
                    try {
                        jsonObject.put(columnName, value);
                        answerMapper.put(columnName, questionBeanFilled.isFilled());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (answerMapper.get(columnName) != null && !answerMapper.get(columnName)) {
                    try {
                        jsonObject.put(columnName, value);
                        answerMapper.put(columnName, questionBeanFilled.isFilled());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LOOPING) ||
                        questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LOOPING_MILTISELECT)) {
                    String nestedColumnName = questionBean.getNestedColumnName();
                    JSONArray nestedJsonAns = new JSONArray();
                    if (questionBeanFilled.getNestedAnswer() != null) {
                        for (Nested nested : questionBeanFilled.getNestedAnswer()) {
                            JSONObject childJsonObject = new JSONObject();
                            try {
                                childJsonObject.put(Constant.FOR_PARENT_VALUE, nested.getForParentValue());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for (QuestionBeanFilled nestedQuestionBeanFilled : nested.getAnswerNestedData()) {
                                try {
                                    childJsonObject.put(nestedQuestionBeanFilled.getColumnName(), getAnswerForm(nestedQuestionBeanFilled));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            nestedJsonAns.put(childJsonObject);
                        }
                    }
                    try {
                        jsonObject.put(nestedColumnName, nestedJsonAns);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    QuestionHelperCallback.DataListener dataListener = new QuestionHelperCallback.DataListener() {
        @Override
        public void onhideKeyboard() {
            hideKeyboard();
        }

        @Override
        public void createSingleSelector(List<AnswerOptionsBean> answerOptionsBeen, QuestionBean questionBean, String header) {
            clearFocus();
            createSelector(BaseFormActivity.this, answerOptionsBeen, questionBean, header);
        }

        @Override
        public void createMultiSelector(List<AnswerOptionsBean> ansOpt, List<String> selectedList, QuestionBean questionBean, String header, int checkLimit) {
            clearFocus();
            showMultiSelectorWithLimit(ansOpt, selectedList, BaseFormActivity.this, questionBean, header, checkLimit);
        }

        @Override
        public void createTextData(String text, QuestionBean questionBean) {
            addDataToSaveList(text, questionBean);
        }

        @Override
        public void showAlert(String text, QuestionBean questionBean) {
            checkAlert(text, questionBean);
        }

        @Override
        public void createDatePicker(QuestionBean questionBean, long max, long min) {
            clearFocus();
            dateChooser(BaseFormActivity.this, questionBean, max, min);
        }

        @Override
        public void createNewAnswerObject(QuestionBean questionBean, boolean isVisibleInHideList) {
            createOrModifyAnswerBeanObject(questionBean, isVisibleInHideList, answerBeanHelperList);
        }

        @Override
        public void changeTitle(RestrictionsBean restrictionsBean, String text) {
            applyTitleChangeRestriction(restrictionsBean, text);
        }


        @Override
        public void clearQuestion(QuestionBean questionBean) {
            clearAnswerAndView(questionBean);
        }

        @Override
        public void clickOnAdditionalButton(QuestionBean questionBean) {
            openAdditionalForm(questionBean);

        }

        @Override
        public String getUserLanguage() {
            return "en";
        }


    };


    QuestionHelperCallback.ImageViewListener imageViewListener = new QuestionHelperCallback.ImageViewListener() {

        @Override
        public void clickImage(final QuestionBean questionBean) {
            if (!checkImagePermission() && !alreadyRequest) {
                permissionListener = new PermissionListener() {
                    @Override
                    public void acceptedPermission() {
                        CaptureImageInternal(BaseFormActivity.this, questionBean);
                    }

                    @Override
                    public void deniedPermission() {
                        //show alert
                        showPermissionAlert();
                    }
                };
                requestImagePermissions();
                alreadyRequest = true;

            } else {
                if (checkImagePermission()) {
                    CaptureImageInternal(BaseFormActivity.this, questionBean);
                }
            }
        }

        @Override
        public void pickImage(final QuestionBean questionBean) {
            // public List<QuestionBeanFilled> answerBeanHelperList;
            hideKeyboard();
            if (!checkImagePermission() && !alreadyRequest) {
                alreadyRequest = true;
                permissionListener = new PermissionListener() {
                    @Override
                    public void acceptedPermission() {
                        PickImage(BaseFormActivity.this, questionBean);
                    }

                    @Override
                    public void deniedPermission() {
                        //show alert
                        showPermissionAlert();
                    }
                };
                requestImagePermissions();

            } else {
                if (checkImagePermission()) {
                    PickImage(BaseFormActivity.this, questionBean);
                }
            }
        }

        @Override
        public void onhidekeyboard() {
            hideKeyboard();
        }

        @Override
        public void createNewAnswerObject(QuestionBean questionBean, boolean isVisibleInHideList) {
            createOrModifyAnswerBeanObject(questionBean, isVisibleInHideList, answerBeanHelperList);

        }
    };


    QuestionHelperCallback.QuestionButtonClickListener onLoopingButtonClickListener = new QuestionHelperCallback.QuestionButtonClickListener() {
        @Override
        public void onclickOnQuestionButton(QuestionBean questionBean) {
            showProgress(BaseFormActivity.this, getString(R.string.loading));
            final String questionUid = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
            int childCount = 0;
            final ArrayList<String> childListString = new ArrayList<>();
            final ArrayList<String> childListValues = new ArrayList<>();
            LinkedHashMap<String, Nested> nestedLinkedHashMap = new LinkedHashMap<>();
            boolean isValidAns = false;
            final QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUid);
            if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LOOPING)) {
                String currentCountOfRepetition = questionBeanFilled.getAnswer().get(0).getValue();
                if (currentCountOfRepetition != null && !currentCountOfRepetition.equals("")) {
                    isValidAns = true;
                    int numberOfCount = Integer.parseInt(currentCountOfRepetition);
                    childCount = numberOfCount;
                    for (int i = 1; i <= numberOfCount; i++) {
                        nestedLinkedHashMap.put(String.valueOf(i), null);
                        childListString.add(String.valueOf(i));
                        childListValues.add(String.valueOf(i));
                    }
                }
            } else if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LOOPING_MILTISELECT)) {
                List<Answers> questionAns = questionBeanFilled.getAnswer();
                if (!questionAns.isEmpty() && !questionAns.get(0).getValue().equals("")) {
                    isValidAns = true;
                    childCount = questionAns.size();
                    for (Answers answers : questionAns) {
                        nestedLinkedHashMap.put(answers.getValue(), null);
                        childListString.add(answers.getLabel());
                        childListValues.add(answers.getValue());
                    }

                }
            }
            int childStatus;
            boolean isChildLocationRequired = false;

            final ArrayList<QuestionBean> childQuestionsList = new ArrayList<>(getNestedQuestion(questionUid));
            for (QuestionBean questionBean1 : childQuestionsList) {
                if (questionBean1.getInput_type().equals(LibDynamicAppConfig.QUS_GET_LOCTION)) {
                    isChildLocationRequired = true;
                    break;
                }
            }

            if (childQuestionsList.size() == 0) {
                showToast(R.string.no_child_question_found);
                hideProgess();
                return;
            }

            if (isValidAns) {
                List<Nested> oldNestedList = questionBeanFilled.getNestedAnswer();
                if (!(formStatus == LibDynamicAppConfig.SUBMITTED || formStatus == EDITABLE_SUBMITTED)) {
                    if (oldNestedList == null || oldNestedList.size() == 0) {

                        //new create new answer for nested
                        ArrayList<Nested> newNestedList = new ArrayList<>();
                        for (Map.Entry<String, Nested> singleMap : nestedLinkedHashMap.entrySet()) {
                            Nested tempNested = getNestedNewItemUpdated(childQuestionsList, singleMap.getKey());
                            newNestedList.add(tempNested);
                        }


                        questionBeanFilled.setNestedAnswer(newNestedList);

                        if (formStatus == SYNCED_BUT_EDITABLE) {
                            childStatus = SYNCED_BUT_EDITABLE;
                        } else {
                            childStatus = NEW_FORM;
                        }


                    } else {

                        childStatus = DRAFT;
                        if (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) {
                            childStatus = formStatus;
                        }

                        //
                        ArrayList<Nested> nestedRealmList = new ArrayList<>();
                        for (Map.Entry<String, Nested> item : nestedLinkedHashMap.entrySet()) {
                            boolean isFound = false;
                            for (Nested nested : oldNestedList) {
                                if (item.getKey().equals(nested.getForParentValue())) {
                                    nestedRealmList.add(modifiedNestedUpdated(nested, item.getKey(), childQuestionsList));
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                Nested tempNested = getNestedNewItemUpdated(childQuestionsList, item.getKey());
                                nestedRealmList.add(tempNested);

                            }
                        }

                        //

                        oldNestedList.clear();
                        oldNestedList.addAll(nestedRealmList);
                        questionBeanFilled.setNestedAnswer(oldNestedList);
                    }

                } else {
                    childStatus = SUBMITTED;
                }

                InnerFormData.Companion.createNew(childQuestionsList, questionBeanFilled);
                Intent intent = new Intent(BaseFormActivity.this, InnerLoopingFormActivity.class);
                intent.putExtra("count", childCount);
                intent.putStringArrayListExtra("countListString", childListString);
                intent.putStringArrayListExtra("countListValue", childListValues);
                intent.putExtra("tvFormStatus", childStatus);
                intent.putExtra("questionOrder", questionUid);
                intent.putExtra("formId", form_id);
                intent.putExtra("locationRequired", isChildLocationRequired);
                startActivityForResult(intent, NESTEDCHILD_CODE);
                overridePendingTransition(R.anim.from_right, R.anim.to_right);
            }


        }
    };


    QuestionHelperCallback.QuestionButtonClickListener onViewImageButtonClickListener = new QuestionHelperCallback.QuestionButtonClickListener() {
        @Override
        public void onclickOnQuestionButton(QuestionBean questionBean) {
            if (questionBean.getResource_urls() != null && questionBean.getResource_urls().size() > 0) {
                String imagePath = questionBean.getResource_urls().get(0).getUrl();
                Intent intent = new Intent(BaseFormActivity.this, FullScreenImageActivity.class);
                intent.putExtra("imageurl", imagePath);
                intent.putExtra("titletext", questionBean.getTitle());
                startActivity(intent);
                overridePendingTransition(R.anim.from_right, R.anim.to_right);
                twoStepValidation.put(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), 1);
            }


        }
    };

    List<QuestionBean> getNestedQuestion(String questionUid) {
        List<QuestionBean> childQuestionsList = new ArrayList<>();
        for (QuestionBean questionBean1 : childQuestionBeenList.values()) {
            String childQuestionOrder = QuestionsUtils.Companion.getQuestionUniqueId(questionBean1);
            String childQuestionGroup = childQuestionOrder.substring(0, childQuestionOrder.indexOf("."));
            if (childQuestionGroup.equals(questionUid)) {
                childQuestionsList.add(questionBean1);
            }
        }
        return childQuestionsList;

    }


    void convertJsonDataToAnswer(List<QuestionBean> questionBeanList, JSONObject jsonObject, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {
        LinkedHashMap<String, QuestionBean> questionBeanMap = QuestionsUtils.Companion.getQuestionBeanMap(questionBeanList);
        for (QuestionBean questionBean : questionBeanList) {
            String columnName = questionBean.getColumnName();

            QuestionBeanFilled answerBeanObject = createOrModifyAnswerBeanObject(questionBean, QuestionsUtils.Companion.checkValueForVisibility(questionBean, answerBeanHelperList, questionBeanMap), answerBeanHelperList);
            answerBeanObject.setFilled(true);
            answerBeanObject.setValidAns(true);
            try {
                if (jsonObject.has(columnName)) {
                    String string = jsonObject.getString(columnName);
                    answerBeanObject.setAnswer(DynamicLibUtils.Companion.getAnswerFormText(string, questionBean));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LOOPING) ||
                    questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LOOPING_MILTISELECT)) {
                List<QuestionBean> nestedQuestion = getNestedQuestion(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                String nestedColumnName = questionBean.getNestedColumnName();
                try {
                    JSONArray nestedJsonArray = (JSONArray) jsonObject.get(nestedColumnName);
                    List<Nested> nestedList = new ArrayList<>();
                    if (nestedJsonArray != null) {
                        for (int i = 0; i < nestedJsonArray.length(); i++) {
                            LinkedHashMap<String, QuestionBeanFilled> childAswer = new LinkedHashMap<>();
                            Nested nested = new Nested();
                            JSONObject nestedJSONObject = nestedJsonArray.getJSONObject(i);
                            if (nestedJSONObject != null) {
                                convertJsonDataToAnswer(nestedQuestion, nestedJSONObject, childAswer);
                                if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LOOPING)) {
                                    nested.setForParentValue(String.valueOf((i + 1)));

                                } else {
                                    nested.setForParentValue(nestedJSONObject.getString(Constant.FOR_PARENT_VALUE));
                                }
                                nested.setAnswerNestedData(new ArrayList<>());
                                nested.getAnswerNestedData().addAll(childAswer.values());
                                nestedList.add(nested);
                            }
                        }
                    }
                    answerBeanObject.setNestedAnswer(nestedList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            answerBeanHelperList.put(QuestionsUtils.Companion.getAnswerUniqueId(answerBeanObject), answerBeanObject);

        }
    }


    QuestionHelperCallback.QuestionButtonClickListener onGetLocationClickListener = new QuestionHelperCallback.QuestionButtonClickListener() {
        @Override
        public void onclickOnQuestionButton(QuestionBean questionBean) {
            gpsOnSetLocation(questionBean, false);
        }


    };

    private void gpsOnSetLocation(QuestionBean questionBean, boolean isButtonHide) {


        LocationGetType locationGetType = null;

        BaseType baseType = questionObjectList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
        if (baseType instanceof LocationGetType) {
            locationGetType = (LocationGetType) baseType;
            if (isButtonHide)
                locationGetType.buttonState(false);
        }
        locationDataN = locationReceiver.getLocationData();
        String[] location = new String[3];
        //    if (locationDataN != null && questionBeanFilled.getAnswer().get(0).getValue().equals("")) {
        if (locationDataN.getValue() != null && locationGetType != null) {
            location[0] = String.valueOf(locationDataN.getValue().getAccuracy());
            location[1] = String.valueOf(locationDataN.getValue().getLatitude());
            location[2] = String.valueOf(locationDataN.getValue().getLongitude());

            questionBeanFilled.setAnswer(getLocationAnswerList(location));
            setFilledAns(questionBeanFilled, true, true);

            locationGetType.setLocationOnView(location[0], location[1], location[2]);
            locationGetType.superChangeStatus(ANSWERED);

        }

    }


    QuestionHelperCallback.RecordAudioResponseListener recordAudioResponseListener = new QuestionHelperCallback.RecordAudioResponseListener() {
        @Override
        public void onCreateNewAnswerObject(QuestionBean questionBean, boolean isVisibleInHideList) {
            createOrModifyAnswerBeanObject(questionBean, isVisibleInHideList, answerBeanHelperList);
        }

        @Override
        public void onRequestMicPermission(final AudioRecordType audioRecordType) {
            if (checkMicPermission()) {
                audioRecordType.isPermission = true;
                audioRecordType.startRecording();
            } else {
                permissionListener = new PermissionListener() {
                    @Override
                    public void acceptedPermission() {
                        audioRecordType.isPermission = true;
                        audioRecordType.startRecording();
                    }

                    @Override
                    public void deniedPermission() {
                        audioRecordType.isPermission = false;
                    }
                };
                requestMicPermission();
            }
        }

        @Override
        public void onhidekeyboard() {
            hideKeyboard();
        }

        @Override
        public void onRequestForNewFilePath(AudioRecordType audioRecordType) {
            audioRecordType.setFilePath(getOutputAudioFile());
        }

        @Override
        public void onVoiceRecorded(QuestionBean questionBean, String path, String length, boolean isRecoded) {
            QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
            if (questionBeanFilled != null) {
                if (isRecoded) {
                    saveDataToAnsList(questionBeanFilled, path, length, "", path);
                    setFilledAns(questionBeanFilled, true, true);
                } else {
                    questionBeanFilled.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
                    setFilledAns(questionBeanFilled, false, false);
                }

            }
        }
    };


    QuestionHelperCallback.RadioButtonListener radioButtonListener = new QuestionHelperCallback.RadioButtonListener() {
        @Override
        public void onRadioButtonsChecked(QuestionBean questionBean, String id, String label) {
            clearFocus();
            saveRadioButtonData(questionBean, id, label);
        }

        @Override
        public void onCreateNewAnswerObject(QuestionBean questionBean, boolean isVisibleInHideList) {
            createOrModifyAnswerBeanObject(questionBean, isVisibleInHideList, answerBeanHelperList);
        }

        @Override
        public void clickOnAdditionalButton(QuestionBean questionBean) {
            openAdditionalForm(questionBean);

        }
    };


    List<String> loopingTypeQuestion = new ArrayList<>();

    QuestionHelperCallback.UnitConversionListener unitConversionListener = new QuestionHelperCallback.UnitConversionListener() {
        @Override
        public void onUnitClick(QuestionBean questionBean) {

        }

        @Override
        public void saveValue(String originalValue, String standardValue, String unit, QuestionBean questionBean) {
            final String questionUid = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
            BaseType baseType = questionObjectList.get(questionUid);
            QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUid);
            if (originalValue != null && baseType != null && questionBeanFilled != null) {
                if (originalValue.isEmpty()) {
                    baseType.superChangeStatus(NONE);
                    setFilledAns(questionBeanFilled, false, false);
                } else {
                    boolean isValidRegex = true;
                    boolean isValidInNumbericRestriction = true;
                    boolean isValidMixMax = true;
                    boolean isRegex = false;
                    boolean isAlertRegex = false;
                    List<String> alertRegexList = new ArrayList<>();
                    boolean isAlertCorrect = true;
                    String alertMsg = null;
                    boolean isBetweenAlert = false;
                    for (ValidationBean validationBean : questionBean.getValidation()) {
                        if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_REGEX)) {
                            isRegex = true;
                        } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ALERT_REGEX)) {
                            isAlertRegex = true;
                            alertRegexList.add(validationBean.getError_msg());
                            alertMsg = "" + getString(R.string.are_you_sure);
                        } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ALERT_MSG)) {
                            if (validationBean.getError_msg() != null && !validationBean.getError_msg().equals("")) {
                                alertMsg = validationBean.getError_msg();
                            } else {
                                alertMsg = "" + getString(R.string.are_you_sure);
                            }
                        } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ALERT_IF_BETWEEN)) {
                            isBetweenAlert = true;

                        }
                    }

                    //alert
                    if (isAlertRegex && alertRegexList.size() > 0) {
                        isAlertCorrect = standardValue.matches(alertRegexList.get(0));
                        requirdAlertMap.put(questionUid, false);
                    }
                    if (isBetweenAlert && isAlertCorrect) {
                        requirdAlertMap.put(questionUid, true);
                    } else if (!isBetweenAlert && !isAlertCorrect) {
                        requirdAlertMap.put(questionUid, true);
                    }

                    if (isRegex && questionBean.getPattern() != null) {
                        isValidRegex = standardValue.matches(questionBean.getPattern());
                    }

                    if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUA_UNIT_CONVERSION) &&
                            questionBean.getRestrictions().size() > 0) {
                        isValidInNumbericRestriction = checkRestrictionOnNumberValues(questionBean, standardValue);
                        callForExpressionsRestriction(questionBean, standardValue);
                    }

                    isValidMixMax = checkMinMaxRange(standardValue, questionBean);


                    if (isValidRegex && isValidInNumbericRestriction && isValidMixMax) {
                        baseType.superChangeStatus(ANSWERED);
                        setFilledAns(questionBeanFilled, true, true);
                    } else {
                        baseType.superChangeStatus(NOT_ANSWERED);
                        setFilledAns(questionBeanFilled, true, false);
                    }
                }
                saveDataToAnsList(questionBeanFilled, standardValue, unit, originalValue, "");
                validateChildVisibility(questionBean, questionBeanFilled, baseType);
                updateCount();

            } else {
                BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT, String.format(Locale.ENGLISH,
                        "Question filled with null. Question ID : %s Line no. 552",
                        questionBean.get_id()), LibDynamicAppConfig.UNEXPECTED_ERROR,
                        "BaseFormActivity");
            }

        }
    };

    boolean checkMinMaxRange(String standardValue, QuestionBean questionBean) {
        if (!questionBean.getMax().isEmpty() || !questionBean.getMin().isEmpty()) {
            double value = Double.parseDouble(standardValue);
            if (!questionBean.getMin().isEmpty() && questionBean.getMax().isEmpty()) {
                double min = Double.parseDouble(questionBean.getMin());
                if (value < min) {
                    return false;
                }
            } else if (questionBean.getMin().isEmpty() && !questionBean.getMax().isEmpty()) {
                double max = Double.parseDouble(questionBean.getMax());
                if (value > max) {
                    return false;
                }
            } else if (!questionBean.getMin().isEmpty() && !questionBean.getMax().isEmpty()) {
                double min = Double.parseDouble(questionBean.getMin());
                double max = Double.parseDouble(questionBean.getMax());
                if (value < min || value > max) {
                    return false;
                }
            }

        }
        return true;
    }


    String getAnswerForm(QuestionBeanFilled questionBeanFilled) {
        StringBuilder answerBuilder = new StringBuilder();
        String prefix = "";
        for (Answers answers : questionBeanFilled.getAnswer()) {
            answerBuilder.append(prefix);
            prefix = ",";
            if (questionBeanFilled.getInput_type().equals(LibDynamicAppConfig.QUS_TEXT)
                    || questionBeanFilled.getInput_type().equals(LibDynamicAppConfig.QUS_ADDRESS)) {
                answerBuilder.append(answers.getTextValue());
            } else {
                answerBuilder.append(answers.getValue());
            }
        }
        return answerBuilder.toString();
    }
    LinearLayout childLayout;
    Boolean childLayoutActive = false;
    Boolean isFirstExpandableLayout = true;
    BaseType expandableParentView;

    protected void createViewObject(QuestionBean questionBean, int formStatus) {
        View view = null;
        String questionUniqueId = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
        childLayoutActive = childLayout != null;

        switch (questionBean.getInput_type()) {
            case LibDynamicAppConfig.QUS_TEXT:
            case LibDynamicAppConfig.QUS_ADDRESS:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                EditTextSimpleText editTextSimpleText = new EditTextSimpleText(dataListener, view, questionBean, formStatus, questionBeenList, answerBeanHelperList);
                questionObjectList.put(questionUniqueId, editTextSimpleText);
                break;
            case LibDynamicAppConfig.QUS_NUMBER:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                EditTextNumber editTextNumber = new EditTextNumber(dataListener, view, questionBean, formStatus, questionBeenList, answerBeanHelperList, editTextFocusChangeListener);
                questionObjectList.put(questionUniqueId, editTextNumber);
                break;
            case LibDynamicAppConfig.QUS_AADHAAR:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                EditTextAadhar editTextAadhar = new EditTextAadhar(dataListener, view, questionBean, formStatus, questionBeenList, answerBeanHelperList);
                questionObjectList.put(questionUniqueId, editTextAadhar);
                break;

            case LibDynamicAppConfig.QUS_DATE:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                DateSelect dateSelect = new DateSelect(view, questionBean, formStatus, dataListener, questionBeenList, answerBeanHelperList);
                questionObjectList.put(questionUniqueId, dateSelect);
                break;

            case LibDynamicAppConfig.QUS_IMAGE:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_img_dynam, linearLayout, false);
                ImageType imageType = new ImageType(view, questionBean, formStatus, imageViewListener,
                        questionBeenList, answerBeanHelperList);

                questionObjectList.put(questionUniqueId, imageType);
                break;
            case LibDynamicAppConfig.QUS_LABEL:
                view = getLayoutInflater().inflate(R.layout.dy_row_label, linearLayout, false);
                BaseLabelType labelType = new BaseLabelType(view);
                labelType.setAnswerAnsQuestionData(answerBeanHelperList, questionBeenList, this);
                labelType.setBasicFunctionality(view, questionBean, formStatus);
                if (formStatus == NEW_FORM) {
                    createOrModifyAnswerBeanObject(questionBean, view.getVisibility() == View.VISIBLE, answerBeanHelperList);
                }
                questionObjectList.put(questionUniqueId, labelType);
                if (labelType.isExpandable) {
                    childLayout = labelType.getChildLayout();
                    childLayoutActive = false;
                    expandableParentView = labelType;
                    if (isFirstExpandableLayout) {
                        labelType.expand(false);
                        isFirstExpandableLayout = false;
                    }
                }
                break;

            case LibDynamicAppConfig.QUS_DROPDOWN:
            case LibDynamicAppConfig.QUS_DROPDOWN_HIDE: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                SingleSelect singleSelect = new SingleSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, form_id);
                questionObjectList.put(questionUniqueId, singleSelect);
            }
            break;

            case LibDynamicAppConfig.QUS_MULTI_SELECT:
            case LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                MultiSelect multiSelect = new MultiSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, form_id);
                questionObjectList.put(questionUniqueId, multiSelect);
            }
            break;
            case LibDynamicAppConfig.QUS_MULTI_SELECT_LIMITED: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                MultiSelectLimit multiSelectLimit = new MultiSelectLimit(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, form_id);
                questionObjectList.put(questionUniqueId, multiSelectLimit);
            }
            break;
            case LibDynamicAppConfig.QUS_LOOPING: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dy_looping, linearLayout, false);
                LoopingSingleSelect loopingSingleSelect = new LoopingSingleSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, onLoopingButtonClickListener
                        , form_id);
                questionObjectList.put(questionUniqueId, loopingSingleSelect);
                loopingTypeQuestion.add(questionUniqueId);

            }
            break;
            case LibDynamicAppConfig.QUS_LOOPING_MILTISELECT: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dy_looping, linearLayout, false);
                LoopingMultiSelect loopingMultiSelect = new LoopingMultiSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, onLoopingButtonClickListener
                        , form_id);
                questionObjectList.put(questionUniqueId, loopingMultiSelect);
                loopingTypeQuestion.add(questionUniqueId);

            }
            break;
            case LibDynamicAppConfig.QUS_VIEW_IMAGE_QUESTION: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dy_looping, linearLayout, false);
                ViewImageWithSingleSelect viewImageWithSingleSelect = new ViewImageWithSingleSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, onViewImageButtonClickListener, form_id);
                questionObjectList.put(questionUniqueId, viewImageWithSingleSelect);
            }
            break;

            case LibDynamicAppConfig.QUS_RECORD_AUDIO:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_audio, linearLayout, false);
                AudioRecordType audioRecordType = new AudioRecordType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, recordAudioResponseListener);
                audioRecordType.isPermission = checkMicPermission();
                questionObjectList.put(questionUniqueId, audioRecordType);
                break;

            case LibDynamicAppConfig.QUS_RADIO_BUTTONS:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_radio_view, linearLayout, false);
                RadioButtonType radioButtonType = new RadioButtonType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, radioButtonListener);
                questionObjectList.put(questionUniqueId, radioButtonType);
                break;

            case LibDynamicAppConfig.QUS_GET_LOCTION:
                view = getLayoutInflater().inflate(R.layout.dy_quest_custom_row_location_xml, linearLayout, false);
                LocationGetType locationGetType = new LocationGetType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, onGetLocationClickListener, dataListener);
                questionObjectList.put(questionUniqueId, locationGetType);
                break;

//            case LibDynamicAppConfig.QUS_CONSENT:
//                view = getLayoutInflater().inflate(R.layout.quest_custom_cosent, linearLayout, false);
//                ConsentType consentType = new ConsentType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, consentListener, dataListener);
//                questionObjectList.put(questionUniqueId, consentType);
//                break;
//
//            case LibDynamicAppConfig.QUS_GEO_TRACE:
//            case LibDynamicAppConfig.QUS_GEO_SHAPE:
//                view = getLayoutInflater().inflate(R.layout.qus_row_custom_geo_trace, linearLayout, false);
//                GeoTraceType geoTraceType = new GeoTraceType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, geoTraceListener, dataListener);
//                questionObjectList.put(questionUniqueId, geoTraceType);
//                break;

            case LibDynamicAppConfig.QUA_UNIT_CONVERSION:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_unit, linearLayout, false);
                UnitConversionType unitConversionType = new UnitConversionType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, unitConversionListener, dataListener);
                questionObjectList.put(questionUniqueId, unitConversionType);
                break;


        }
        BaseType baseType = questionObjectList.get(questionUniqueId);

        if (view != null && baseType != null) {
            int position = 0;
            if(childLayout == null || !childLayoutActive){
                linearLayout.addView(view);
                position = linearLayout.indexOfChild(view);
            } else {
                childLayout.addView(view);
                baseType.parentLayout = childLayout;
                baseType.expandableParentView = expandableParentView;
                childLayout.indexOfChild(view);
            }
            baseType.view = view;
            baseType.setViewIndex(position);;
            if (newTitles.get(questionUniqueId) != null) {
                baseType.superChangeTitle(newTitles.get(questionUniqueId));
                answerBeanHelperList.get(questionUniqueId).setTitle(newTitles.get(questionUniqueId));
            }
            updateCount();
            createNotifyOnChangeList(questionBean);
        }
    }

    protected HashMap<String, HashSet<String>> notifyOnchangeMap = new HashMap<>();

    private void createNotifyOnChangeList(QuestionBean questionBean) {
        for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
            if (QuestionsUtils.Companion.isNotifyRestriction(restrictionsBean)) {
                for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                    HashSet<String> ordersToNotify = notifyOnchangeMap.get(QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean));
                    if (ordersToNotify == null) {
                        ordersToNotify = new HashSet<>();
                    }
                    ordersToNotify.add(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                    notifyOnchangeMap.put(QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean), ordersToNotify);
                }
            }
        }
    }


    //creating new Answer Object
    QuestionBeanFilled createOrModifyAnswerBeanObject(final QuestionBean questionBean, final boolean isVisibleInHideList, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {
        String questionUid = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);

        QuestionBeanFilled answerBeanHelper = answerBeanHelperList.get(questionUid);
        if (FormType == 0 || answerBeanHelper == null) {
            answerBeanHelper = new QuestionBeanFilled();
            answerBeanHelper.setUid(UUID.randomUUID().toString());
            answerBeanHelper.setFilled(false);
            answerBeanHelper.setValidAns(false);
            answerBeanHelper.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
        } else {
            if (answerBeanHelper.getAnswer() == null && answerBeanHelper.getAnswer().size() == 0) {
                answerBeanHelper.setFilled(false);
                answerBeanHelper.setValidAns(false);
                answerBeanHelper.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
            }
        }
        answerBeanHelper.setInput_type(questionBean.getInput_type());
        answerBeanHelper.setOrder(questionUid);
        answerBeanHelper.setColumnName(questionBean.getColumnName());
        answerBeanHelper.setLabel(questionBean.getLabel());
        answerBeanHelper.setTitle(questionBean.getTitle());
        answerBeanHelper.setViewSequence(questionBean.getViewSequence());
        boolean isActive = (isVisibleInHideList || (questionBean.getParent().size() == 0)
                && !questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LABEL));

        List<ValidationBean> valiList = questionBean.getValidation();
        if (valiList.size() > 0) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_REQUIRED)) {
                    answerBeanHelper.setOptional(false);
                    answerBeanHelper.setRequired(isActive);
                    break;
                }
            }
        } else {
            answerBeanHelper.setRequired(false);
            answerBeanHelper.setOptional(isActive);
        }


        answerBeanHelperList.put(questionUid, answerBeanHelper);
        return answerBeanHelper;
    }

    private void addDataToSaveList(final String text, final QuestionBean questionBean) {
        final String questionUid = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
        BaseType baseType = questionObjectList.get(questionUid);
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUid);
        if (text != null && baseType != null && questionBeanFilled != null) {
            boolean isValidRegex = true;
            boolean isValidInNumbericRestriction = true;
            boolean isRegex = false;
            boolean isAlertRegex = false;
            List<String> alertRegexList = new ArrayList<>();
            boolean isAlertCorrect = true;
            String alertMsg = null;
            boolean isBetweenAlert = false;


            for (ValidationBean validationBean : questionBean.getValidation()) {
                if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_REGEX)) {
                    isRegex = true;
                } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ALERT_REGEX)) {
                    isAlertRegex = true;
                    alertRegexList.add(validationBean.getError_msg());
                    alertMsg = "" + this.getString(R.string.are_you_sure);
                } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ALERT_MSG)) {
                    if (validationBean.getError_msg() != null && !validationBean.getError_msg().equals("")) {
                        alertMsg = validationBean.getError_msg();
                    } else {
                        alertMsg = "" + this.getString(R.string.are_you_sure);
                    }
                } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ALERT_IF_BETWEEN)) {
                    isBetweenAlert = true;

                }
            }

            if (text.isEmpty()) {
                baseType.superChangeStatus(NONE);
                setFilledAns(questionBeanFilled, false, false);
            } else {
                //alert
                if (isAlertRegex && alertRegexList.size() > 0) {
                    isAlertCorrect = text.matches(alertRegexList.get(0));
                    requirdAlertMap.put(questionUid, false);
                }
                if (isBetweenAlert && isAlertCorrect) {
                    requirdAlertMap.put(questionUid, true);
                } else if (!isBetweenAlert && !isAlertCorrect) {
                    requirdAlertMap.put(questionUid, true);
                }
                /////////////////////

                if (isRegex && questionBean.getPattern() != null) {
                    isValidRegex = text.matches(questionBean.getPattern());
                }

                if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_NUMBER) &&
                        questionBean.getRestrictions().size() > 0) {
                    isValidInNumbericRestriction = checkRestrictionOnNumberValues(questionBean, text);
                    callForExpressionsRestriction(questionBean, text);
                }


                if (isValidRegex && isValidInNumbericRestriction) {
                    baseType.superChangeStatus(ANSWERED);
                    setFilledAns(questionBeanFilled, true, true);
                } else {
                    baseType.superChangeStatus(NOT_ANSWERED);
                    setFilledAns(questionBeanFilled, true, false);
                }
            }

            if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_ADDRESS) || questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_TEXT)) {
                saveDataToAnsList(questionBeanFilled, "", "", text, "");
            } else {
                saveDataToAnsList(questionBeanFilled, text, "", "", "");

            }


            validateChildVisibility(questionBean, questionBeanFilled, baseType);


            updateCount();


        } else {
            BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT, String.format(Locale.ENGLISH,
                    "Question filled with null. Question ID : %s Line no. 552",
                    questionBean.get_id()), LibDynamicAppConfig.UNEXPECTED_ERROR,
                    "BaseFormActivity");
        }


    }

    private void validateAnsForAdditionalInfo(String text, QuestionBean questionBean, BaseType baseType) {
        for (ValidationBean validationBean : questionBean.getValidation()) {
            if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_GPS)
                    || validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_IMAGE)
            ) {

                String requiredPattern = validationBean.getValue();
                boolean isMatchPatten = matches(requiredPattern, text);
                boolean isMatchValue = text.equals(requiredPattern);
                if (text.equals("")) {
                    isMatchPatten = false;
                    isMatchValue = false;
                } else if (requiredPattern.equals("")) {
                    isMatchPatten = true;
                }

                QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                if (questionBeanFilled != null && (isMatchPatten || isMatchValue)) {
                    questionBeanFilled.setValidAns(false);
                }
                baseType.setAdditionalButtonStatus(NOT_ANSWERED);
                if (isMatchPatten || isMatchValue) {
                    baseType.setAdditionalVisibility(View.VISIBLE);
                    break;
                } else {
                    baseType.setAdditionalVisibility(View.GONE);

                }


                //  break;
            }
        }
    }

    private void validateAnsForAdditionalInfoMultiseltct(List<String> value, QuestionBean questionBean, BaseType baseType) {
        for (ValidationBean validationBean : questionBean.getValidation()) {
            if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_GPS)
                    || validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_IMAGE)
            ) {

                String requiredPattern = validationBean.getValue();
                boolean isMatchPatten = false;
                boolean isMatchValue;

                for (String st : value)
                    if (matches(requiredPattern, st)) {
                        isMatchPatten = true;
                        break;
                    }

                isMatchValue = value.contains(requiredPattern);

                if (requiredPattern.equals("")) {
                    isMatchPatten = true;
                }

                baseType.setAdditionalButtonStatus(NOT_ANSWERED);

                if (isMatchPatten || isMatchValue) {
                    baseType.setAdditionalVisibility(View.VISIBLE);
                } else {
                    baseType.setAdditionalVisibility(View.GONE);
                }

                QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                if (questionBeanFilled != null && (isMatchPatten || isMatchValue)) {
                    questionBeanFilled.setValidAns(false);
                }
                break;
            }
        }
    }

    public void checkAlert(String text, QuestionBean questionBean) {
        String alertMsg = null;
        if (questionBean != null) {
            final String questionUniqueId = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
            BaseType baseType = questionObjectList.get(questionUniqueId);
            if (text != null && baseType != null) {
                View linear = linearLayout.getChildAt(baseType.getViewIndex());
                boolean isAlertRegex = false;
                boolean isBetweemLimit = false;
                List<String> alertRegexList = new ArrayList<>();
                boolean isAlertCorrect = true;

                for (ValidationBean validationBean : questionBean.getValidation()) {
                    if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ALERT_REGEX)) {
                        isAlertRegex = true;
                        alertRegexList.add(validationBean.getError_msg());
                        alertMsg = "" + this.getString(R.string.are_you_sure);
                    } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ALERT_MSG)) {
                        if (validationBean.getError_msg() != null && !validationBean.getError_msg().equals("")) {
                            alertMsg = validationBean.getError_msg();
                        } else {
                            alertMsg = "" + this.getString(R.string.are_you_sure);
                        }
                    } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ALERT_IF_BETWEEN)) {
                        isBetweemLimit = true;

                    }
                }

                if (text.isEmpty()) {
                    baseType.superChangeStatus(NONE);
                    setFilledAns(answerBeanHelperList.get(questionUniqueId), false, false);
                } else {


                    if (isAlertRegex && alertRegexList.size() > 0) {
                        isAlertCorrect = text.matches(alertRegexList.get(0));
                        requirdAlertMap.put(questionUniqueId, false);
                    }


                    if (isBetweemLimit && isAlertCorrect) {
                        showAlertRegexDialog(questionUniqueId, alertDialog, alertMsg, questionBean.getLabel());
                        requirdAlertMap.put(questionUniqueId, false);

                    } else if (!isBetweemLimit && !isAlertCorrect) {
                        showAlertRegexDialog(questionUniqueId, alertDialog, alertMsg, questionBean.getLabel());
                        requirdAlertMap.put(questionUniqueId, false);

                    }

                }


            }
        }

    }

    private void performDateRestrictions(HashSet<String> ordersToNotify, String dd, String mm, String yy) {
        for (String questionUid : ordersToNotify) {
            QuestionBean childQuestion = questionBeenList.get(questionUid);
            BaseType baseType = questionObjectList.get(questionUid);
            QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUid);
            if (childQuestion != null && baseType != null && questionBeanFilled != null) {
                for (RestrictionsBean restrictionsBean : childQuestion.getRestrictions()) {
                    if (LibDynamicAppConfig.REST_CALCULATE_AGE.equals(restrictionsBean.getType())) {
                        String ageFromDob = DateUtility.getAgeFromDob(Integer.parseInt(yy), Integer.parseInt(mm), Integer.parseInt(dd));
                        baseType.superSetAnswer(ageFromDob);
                        baseType.superSetEditable(false, childQuestion.getInput_type());
                    } else if (LibDynamicAppConfig.REST_CALCULATE_AGE_SPLIT_MONTH.equals(restrictionsBean.getType())) {
                        String ageFromDob = DateUtility.getMonthFromDob(Integer.parseInt(yy), Integer.parseInt(mm), Integer.parseInt(dd));
                        baseType.superSetAnswer(ageFromDob);
                        baseType.superSetEditable(false, childQuestion.getInput_type());

                    } else if (LibDynamicAppConfig.REST_CALCULATE_AGE_SPLIT_DAYS.equals(restrictionsBean.getType())) {
                        String ageFromDob = DateUtility.getDaysFromDob(Integer.parseInt(yy), Integer.parseInt(mm), Integer.parseInt(dd));
                        baseType.superSetAnswer(ageFromDob);
                        baseType.superSetEditable(false, childQuestion.getInput_type());

                    } else if (LibDynamicAppConfig.REST_CALCULATE_AGE_IN_DAYS.equals(restrictionsBean.getType())) {
                        String ageInDaysFromDob = DateUtility.getAgeInDaysFromDob(Integer.parseInt(yy), Integer.parseInt(mm), Integer.parseInt(dd));
                        baseType.superSetAnswer(ageInDaysFromDob);
                        baseType.superSetEditable(false, childQuestion.getInput_type());
                    } else if (LibDynamicAppConfig.REST_CALCULATE_AGE_IN_MONTHS.equals(restrictionsBean.getType())) {
                        String ageInDaysFromDob = DateUtility.getAgeInMonthDob(Integer.parseInt(yy), Integer.parseInt(mm), Integer.parseInt(dd));
                        baseType.superSetAnswer(ageInDaysFromDob);
                        baseType.superSetEditable(false, childQuestion.getInput_type());

                    }
                }
            }


        }
    }


    private boolean notifyOrdersOnChange(HashSet<String> ordersToNotify) {
        boolean allValid = true;
        for (String questionUid : ordersToNotify) {
            QuestionBean childQuestion = questionBeenList.get(questionUid);
            boolean isChildIsValid = true;
            if (childQuestion != null) {
                isChildIsValid = verifiyChild(childQuestion);
            }
            if (isChildIsValid) {
                continue;
            }
            allValid = isChildIsValid;
        }
        return allValid;
    }

    protected boolean validateOnChangeListeners() {
        boolean allValid = true;
        for (Map.Entry me : notifyOnchangeMap.entrySet()) {
            boolean isChildIsValid = notifyOrdersOnChange((HashSet<String>) me.getValue());
            if (isChildIsValid) {
                continue;
            }
            allValid = isChildIsValid;
        }
        return allValid;
    }


    EditTextFocusChangeListener editTextFocusChangeListener = new EditTextFocusChangeListener() {
        @Override
        public void onFocusChange(QuestionBean questionBean, View v, boolean hasFocus) {
            focusOnEditext = hasFocus;
            String questionUniqueId = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
            if (!hasFocus) {
                if (notifyOnchangeMap.containsKey(questionUniqueId)) {
                    notifyOrdersOnChange(notifyOnchangeMap.get(questionUniqueId));
                }
                if (requirdAlertMap.get(questionUniqueId) != null && requirdAlertMap.get(questionUniqueId)) {
                    checkAlert(((EditText) v).getText().toString(), questionBean);
                    requirdAlertMap.put(questionUniqueId, false);
                }
            }
        }
    };


    private boolean verifiyChild(QuestionBean childQuestion) {

        QuestionBeanFilled childAnswerBean = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(childQuestion));
        BaseType questionObject = questionObjectList.get(QuestionsUtils.Companion.getQuestionUniqueId(childQuestion));
        List<Answers> childAnswers = childAnswerBean.getAnswer();
        boolean currentlyValid = childAnswerBean.isFilled();
        boolean currentlyValidAns = childAnswerBean.isValidAns();
        boolean isValid = true;
        if (currentlyValid && currentlyValidAns) {
            if (QuestionsUtils.Companion.isSelectType(childQuestion.getInput_type())) {
                List<AnswerOptionsBean> availableOptions = QuestionsUtils.Companion.getAnsOptionFromQuestionAfterFilter(childQuestion, questionBeenList, answerBeanHelperList, dataListener.getUserLanguage(), form_id);
                isValid = QuestionsUtils.Companion.validateAnswerListWithAnswerOptions(childAnswers, availableOptions);
                if (!isValid) {
                    questionObject.superResetQuestion();
                    childAnswerBean.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
                    childAnswerBean.setNestedAnswer(new ArrayList<>());

                }
            } else if (childQuestion.getInput_type().equals(LibDynamicAppConfig.QUS_NUMBER)) {
                if (childAnswers.size() > 0) {
                    isValid = checkRestrictionOnNumberValues(childQuestion, childAnswers.get(0).getValue());
                }
            } else if (childQuestion.getInput_type().equals(LibDynamicAppConfig.QUS_DATE)) {
                if (!childAnswers.isEmpty()) {
                    isValid = checkRestrictionOnDate(childQuestion, childAnswers.get(0).getValue());
                }

            }
            if (!isValid) {
                questionObject.superChangeStatus(NOT_ANSWERED);
                setFilledAns(childAnswerBean, false);
                questionObject.superSetEditable(true, childQuestion.getInput_type());
                questionObject.superResetQuestion();
                childAnswerBean.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
                childAnswerBean.setNestedAnswer(new ArrayList<>());

            }
        } else {
            questionObject.superSetEditable(true, childQuestion.getInput_type());
            questionObject.superResetQuestion();
            childAnswerBean.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
            childAnswerBean.setNestedAnswer(new ArrayList<>());

        }

        return isValid;
    }


    private boolean checkRestrictionOnDate(QuestionBean questionBean, String time) {

        if (time != null && !time.equals("") && time.contains("-")) {

            long currentSelectedDate = DateUtility.getTimeStampFromDate(time);
            for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                if (restrictionsBean.getType().equals(REST_SHOULD_BE_GRATER_THAN)
                        || restrictionsBean.getType().equals(LibDynamicAppConfig.REST_SHOULD_BE_GRATER_THAN_EQUAL)
                        || restrictionsBean.getType().equals(REST_SHOULD_BE_LESS_THAN)
                        || restrictionsBean.getType().equals(LibDynamicAppConfig.REST_SHOULD_BE_EQUAL_TO)
                        || restrictionsBean.getType().equals(REST_SHOULD_BE_LESS_THAN_EQUAL)) {

                    ///  return false only in this portion otherwise always return true
                    List<Float> parentValuesList = new ArrayList<>();
                    for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                        try {
                            List<Answers> answers = QuestionsUtils.Companion.getAnswerListbyOder(QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean), null, answerBeanHelperList);

                            if (!answers.isEmpty()) {
                                String p_ans = answers.get(0).getValue();
                                if (!p_ans.equals("") && p_ans.contains("-")) {
                                    parentValuesList.add((float) DateUtility.getTimeStampFromDate(p_ans));
                                }
                            }

                        } catch (Exception e) {
                            return false;
                        }

                    }
                    boolean isValid = validateValueWithRestriction((float) currentSelectedDate, restrictionsBean, parentValuesList);
                    if (!isValid) {
                        return false;
                    }


                }

            }
        } else {
            return false;
        }


        return true;
    }

    private boolean validateValueWithRestriction(float currentValue, RestrictionsBean restrictionsBean, List<Float> parentValuesList) {
        switch (restrictionsBean.getType()) {
            case LibDynamicAppConfig.REST_SHOULD_BE_GRATER_THAN:
                for (float parentValue : parentValuesList) {
                    if (currentValue <= parentValue) {
                        return false;
                    }
                }
                break;
            case LibDynamicAppConfig.REST_SHOULD_BE_GRATER_THAN_EQUAL:
                for (float parentValue : parentValuesList) {
                    if (currentValue < parentValue) {
                        return false;
                    }
                }
                break;
            case LibDynamicAppConfig.REST_SHOULD_BE_LESS_THAN:
                for (float parentValue : parentValuesList) {
                    if (currentValue >= parentValue) {
                        return false;
                    }
                }
                break;
            case LibDynamicAppConfig.REST_SHOULD_BE_LESS_THAN_EQUAL:
                for (float parentValue : parentValuesList) {
                    if (currentValue > parentValue) {
                        return false;
                    }
                }
                break;
            case LibDynamicAppConfig.REST_SHOULD_BE_EQUAL_TO:
                for (float parentValue : parentValuesList) {
                    if (currentValue != parentValue) {
                        return false;
                    }
                }
                break;
        }
        return true;
    }


    private void showAlertRegexDialog(final String questionUniqueId, final AlertDialog.Builder alertDialog, String alertMsg, String label) {

        if (!ad.isShowing()) {
            alertDialog.setTitle(getString(R.string.alert_msg) + label);
            alertDialog.setMessage("" + alertMsg);
            alertDialog.setIcon(R.drawable.ic_warning);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    BaseType baseType = questionObjectList.get(questionUniqueId);
                    baseType.superSetAnswer("");
                    dialog.cancel();
                }
            });

            ad = alertDialog.show();
        }
    }


    @Override
    public void MultiSelector(final QuestionBean questionBean, final List<String> value, final String text) {
        final String questionUid = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUid);
        BaseType baseType = questionObjectList.get(questionUid);
        if (baseType != null) {
            View linearLayout = (this.linearLayout).getChildAt(baseType.getViewIndex());
            if (linearLayout.getTag().equals(questionBean.get_id())) {
                //updating value in dependent field

                questionBeanFilled.getAnswer().clear();
                List<Answers> answerBeanHelperList = new ArrayList<Answers>();

                String[] data = text.split(",");

                int i = 0;
                for (String val : value) {
                    Answers answers = new Answers();
                    answers.setValue(val);
                    answers.setLabel(data[i]);
                    answerBeanHelperList.add(answers);
                    i++;
                }
                questionBeanFilled.setAnswer(answerBeanHelperList);
                if (linearLayout instanceof EditTextRowView) {
                    baseType.superSetAnswer(text);
                    if (value.size() > 0) {
                        setFilledAns(questionBeanFilled, true, true);

                        baseType.superChangeStatus(ANSWERED);
                    } else {
                        setFilledAns(questionBeanFilled, false, false);
                        baseType.superChangeStatus(NONE);
                    }

                } else if (linearLayout instanceof EditTextWIthButtonView) {


                    EditTextWIthButtonView dynamicLoopingView = (EditTextWIthButtonView) linearLayout;
                    dynamicLoopingView.setText(text);
                    setFilledAns(questionBeanFilled, false, false);
                    dynamicLoopingView.changebuttonStatus(false, 2);

                    if (value.size() == 0) {
                        dynamicLoopingView.setAnswerStatus(EditTextRowView.NONE);
                        dynamicLoopingView.changebuttonStatus(false, 0);

                    }
                    /// handel the default answerOption
                    else if (questionBean.getAnswer_options().size() > 0 && value.size() == 1) {
                        if (value.get(0).equals(questionBean.getAnswer_option().get(0).get_id())) {
                            for (ValidationBean validationBean : questionBean.getValidation()) {
                                if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0)) {
                                    setFilledAns(questionBeanFilled, true, true);
                                    dynamicLoopingView.changebuttonStatus(false, 0);
                                    break;
                                }
                            }
                        }
                    }


                }

            } else {
                BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT, String.format(Locale.ENGLISH,
                        "Wrong Child in linearLayout. Question ID :%s . Line no. 820",
                        questionBean.get_id()), LibDynamicAppConfig.UNEXPECTED_ERROR,
                        "BaseFormActivity");
            }

            if (questionBean.getRestrictions().size() > 0) {
                for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                    if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD)) {
                        applyTitleChangeRestriction(restrictionsBean, text);
                    } else if (restrictionsBean.getType().equals(REST_CLEAR_DID_CHILD)) {
                        for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                            QuestionBean questionBean1 = questionBeenList.get(QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean));
                            if (questionBean1 != null) {
                                clearAnswerAndView(questionBean1);
                            }
                        }
                    }
                }
            }
           /* for (ValidationBean validationBean : questionBean.getValidation()) {
                if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_DYNAMIC_ANSWER_OPTION)) {
                    for (String transactionIds : value)
                        handleDynamicData(questionBean, transactionIds);
                }

            }*/

            validateChildVisibility(questionBean, questionBeanFilled, baseType);

            if (notifyOnchangeMap.containsKey(questionUid)) {
                notifyOrdersOnChange(notifyOnchangeMap.get(questionUid));
            }

            updateCount();


        } else {
            BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT,
                    "Question is Invalid. Line no. 804",
                    LibDynamicAppConfig.UNEXPECTED_ERROR, "BaseFormActivity");
        }
    }

    //listen Single selector here
    @Override
    public void SingleSelector(final QuestionBean questionBean, final String value, final String id, boolean isSingle) {
        String questionUniqueId = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUniqueId);
        BaseType baseType = questionObjectList.get(questionUniqueId);
        if (questionBeanFilled != null && baseType != null) {
            boolean repeat = false;
            if (questionBean.getAnswer_options().size() > 1
                    && questionBeanFilled.getAnswer().size() > 0) {

                if (questionBeanFilled.getAnswer().get(0).getValue().equals(id))
                    repeat = true;
            }
            if (isSingle) {
                repeat = false;
            }

            if (!repeat) {


                View linear = linearLayout.getChildAt(baseType.getViewIndex());
                if (linear != null && linear.getTag().equals(questionBean.get_id())) {
                    //updating value in dependent field

                    setFilledAns(questionBeanFilled, true, true);
                    saveDataToAnsList(questionBeanFilled, id, value, "", "");

                    if (linear instanceof EditTextRowView) {
                        EditTextRowView DynamicEditTextRow = (EditTextRowView) linear;
                        DynamicEditTextRow.setAnswerStatus(com.dhwaniris.dynamicForm.customViews.EditTextRowView.ANSWERED);
                        DynamicEditTextRow.setText(value);

                    } else if (linear instanceof EditTextWIthButtonView) {
                        EditTextWIthButtonView dynamicLoopingView = (EditTextWIthButtonView) linear;
                        dynamicLoopingView.setAnswerStatus(EditTextRowView.ANSWERED);
                        dynamicLoopingView.setText(value);
                        if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LOOPING)) {
                            setFilledAns(questionBeanFilled, false, false);
                            dynamicLoopingView.changebuttonStatus(false, 2);
                        } else if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_VIEW_IMAGE_QUESTION)) {
                            Integer validation = twoStepValidation.get(questionUniqueId);
                            if (validation != null && validation == 1) {
                                dynamicLoopingView.setAnswerStatus(EditTextWIthButtonView.ANSWERED);
                                setFilledAns(questionBeanFilled, true, true);
                            } else {
                                dynamicLoopingView.setAnswerStatus(EditTextWIthButtonView.NOT_ANSWERED);
                                setFilledAns(questionBeanFilled, false, false);
                            }

                        }


                    }
                }

                if (questionBean.getRestrictions().size() > 0) {
                    for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                        if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD)) {
                            applyTitleChangeRestriction(restrictionsBean, value);
                        }
                    }
                }
               /* String optionId = id;
                for (ValidationBean validationBean : questionBean.getValidation()) {
                    if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_VILLAGE_WISE_LIMIT)) {
                        boolean isExceedLimit = checkVillageWiseLimit(questionBean, id);
                        if (isExceedLimit)
                            optionId = "-8898";
                    } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_DYNAMIC_ANSWER_OPTION)) {
                        handleDynamicData(questionBean, id);
                    }

                }*/

                validateChildVisibility(questionBean, questionBeanFilled, baseType);

                if (notifyOnchangeMap.containsKey(questionUniqueId)) {
                    notifyOrdersOnChange(notifyOnchangeMap.get(questionUniqueId));
                }
                updateCount();
            }
        } else {
            BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT,
                    "Question is Invalid. Line no. 873",
                    LibDynamicAppConfig.UNEXPECTED_ERROR, "BaseFormActivity");
        }

    }

    void handleDynamicData(List<QuestionBeanFilled> dynamicData) {
        for (QuestionBeanFilled questionBeanFilled : dynamicData) {
            handleDynamic(questionBeanFilled);
        }
    }

    private boolean handleDynamic(QuestionBeanFilled questionBeanFilled) {
   boolean isValid=false;
        String order = questionBeanFilled.getOrder();
        BaseType baseType = questionObjectList.get(order);
        QuestionBeanFilled originalAnswerBean = answerBeanHelperList.get(order);
        QuestionBean questionBean = questionBeenList.get(order);
        if (QuestionsUtils.Companion.isItHasAns(questionBeanFilled.getAnswer())
                &&
                baseType != null && originalAnswerBean != null && questionBean != null) {
            isValid=true;
            originalAnswerBean.setAnswer(questionBeanFilled.getAnswer());
            originalAnswerBean.setFilled(true);
            originalAnswerBean.setValidAns(true);
            baseType.superSetAnswer(originalAnswerBean);
            baseType.superChangeStatus(ANSWERED);
            validateChildVisibility(questionBean, originalAnswerBean, baseType);
            if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_DATE)) {
                if (notifyOnchangeMap.containsKey(order) && notifyOnchangeMap.get(order) != null) {
                    String ans = QuestionsUtils.Companion.getViewableStringFormAns(originalAnswerBean);
                    if (!ans.isEmpty() && ans.contains("-")) {
                        String[] split = ans.split("-");
                        performDateRestrictions(notifyOnchangeMap.get(order), split[0], split[1], split[2]);
                    }
                }
            }
        }
        return isValid;
    }


    boolean checkVillageWiseLimit(QuestionBean questionBean, String villageId) {
       /* Realm realm = Realm.getDefaultInstance();
        VillageWiseFormCount villageWiseFormCount = realm.where(VillageWiseFormCount.class)
                .equalTo("formId", form_id).findFirst();
        String questionUId = QuestionsUtils.getQuestionUniqueId(questionBean);
        if (villageWiseFormCount != null) {
            for (VillageWiseBean villageWiseBean : villageWiseFormCount.getVillageWise()) {
                if (villageWiseBean.get_id().equals(villageId)) {
                    long submmitedCount = realm.where(FilledForms.class)
                            .equalTo("formId", String.valueOf(form_id))
                            .equalTo("upload_status", LibDynamicAppConfig.SUBMITTED)
                            .equalTo("question.order", questionUId)
                            .equalTo("question.answer.value", villageId).count();
                    int syncCount = villageWiseBean.getCount();
                    int total = syncCount + (int) submmitedCount;
                    if (total >= villageWiseBean.getLimit()) {
                        dataListener.clearQuestion(questionBean);
                        Toast.makeText(this, R.string.option_limit_exceed, Toast.LENGTH_SHORT).show();
                        return true;
                    }


                }
            }
        }
        realm.close();*/
        return false;

    }


    //listen Date change here
    @Override
    public void DateSelector(String dd, String mm, String yy, final QuestionBean questionBean) {

        final String date = dd + "-" + mm + "-" + yy;
        String questionUniqueId = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
        BaseType baseType = questionObjectList.get(questionUniqueId);
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUniqueId);
        if (baseType != null) {
            View linear = linearLayout.getChildAt(baseType.getViewIndex());
            if (linear.getTag().equals(questionBean.get_id())) {
                //updating value in dependent field

                baseType.superSetAnswer(date);
                baseType.superChangeStatus(ANSWERED);


                boolean isValidDate = true;
                boolean isValidDatePattern = true;
                boolean isValidRestriction = true;
                List<ValidationBean> valList = questionBean.getValidation();
                if (valList.size() > 0) {
                    isValidDate = new DateHelper().checkValidationsOnDate(valList, date);
                }
                if (questionBean.getPattern() != null && !questionBean.getPattern().equals("")) {
                    isValidDatePattern = matches(questionBean.getPattern(), date);
                }

                if (!questionBean.getRestrictions().isEmpty()) {
                    isValidRestriction = checkRestrictionOnDate(questionBean, date);
                }

                if (isValidDate && isValidDatePattern && isValidRestriction) {
                    baseType.superChangeStatus(ANSWERED);
                    setFilledAns(questionBeanFilled, true, true);
                } else {
                    baseType.superChangeStatus(NOT_ANSWERED);
                    setFilledAns(questionBeanFilled, true, false);
                }


                saveDataToAnsList(questionBeanFilled, date, "", "", "");


            }

            if (questionBean.getRestrictions().size() > 0) {
                for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                    if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD)) {
                        applyTitleChangeRestriction(restrictionsBean, date);
                    }
                }
            }

            validateChildVisibility(questionBean, questionBeanFilled, baseType);

            if (notifyOnchangeMap.containsKey(questionUniqueId) && notifyOnchangeMap.get(questionUniqueId) != null) {
                performDateRestrictions(notifyOnchangeMap.get(questionUniqueId), dd, mm, yy);
            }

            updateCount();

        } else {
            BaseActivity.logDatabase(LibDynamicAppConfig.END_POINT,
                    "Question is Invalid. Line no. 952",
                    LibDynamicAppConfig.UNEXPECTED_ERROR, "BaseFormActivity");
        }
    }

    //listen image pick
    @Override
    public void imagePath(final String path, final QuestionBean questionBean) {

        String questionUniqueId = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUniqueId);
        BaseType baseType = questionObjectList.get(questionUniqueId);
        if (baseType != null) {

            View linear = linearLayout.getChildAt(baseType.getViewIndex());
            if (linear.getTag().equals(questionBean.get_id())) {
                //updating value in dependent field
                if (linear instanceof ImageRowView) {
                    ImageRowView img = (ImageRowView) linear; // editText is fixed at position 1 in all
                    img.setSrc(path);
                    img.setAnswerStatus(ImageRowView.ANSWERED);
                    setFilledAns(questionBeanFilled, true, true);
                    //check size of answers
                    saveDataToAnsList(questionBeanFilled, path, "", "", path);
                    updateCount();
                }
            }


            List<String> visibleList = new ArrayList<>();
            for (ChildBean childBean : questionBean.getChild()) {
                if (childBean != null) {
                    setChildStatus(childBean, true, visibleList);
                }
            }


            if (questionBean.getChild().size() > 0) {
                String childUniqueId = QuestionsUtils.Companion.getChildUniqueId(questionBean.getChild().get(0));
                QuestionBean questionBean1 = questionBeenList.get(childUniqueId);
                if (questionBean1 != null) {
                    gpsOnSetLocation(questionBean1, true);
                }

                {

                }
            }
        }
    }

    @Override
    public void imageSelectionFailure() {
        // todo null if image is null
    }

    //updating the count of question in UI
    private void updateCount() {
        int ans = 0, total = 0, opt = 0;

        for (QuestionBeanFilled questionBeanFilled : answerBeanHelperList.values()) {
            if (questionBeanFilled.isFilled()
                    && questionBeanFilled.isRequired()) {
                ans++;
            }
            if (questionBeanFilled.isRequired()) {
                total++;
            } else if (questionBeanFilled.isOptional()) {
                opt++;
            }
        }

        int newProg = 0;
        if (total != 0) {
            int prog = ((ans * 100) / (total + opt));
            newProg = Math.round(prog);
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", prog);
            animation.setDuration(300); // 0.5 second
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }
        count.setText(String.format(Locale.getDefault(),
                "%d/%d%s %d%%", ans, total + opt,
                getString(R.string.questions_answered), newProg));
    }


    private void saveRadioButtonData(QuestionBean questionBean, String id, String label) {
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
        setFilledAns(questionBeanFilled, true, true);
        saveDataToAnsList(questionBeanFilled, id, label, "", "");
        if (questionBean.getRestrictions().size() > 0) {
            for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD)) {
                    applyTitleChangeRestriction(restrictionsBean, label);
                }
            }
        }


        BaseType baseType = questionObjectList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
        if (baseType != null) {
            validateChildVisibility(questionBean, questionBeanFilled, baseType);
        }
        updateCount();

    }

    private void validateChildVisibility(QuestionBean questionBean, QuestionBeanFilled questionBeanFilled, BaseType baseType) {
        List<String> values = new ArrayList<>();
        if (QuestionsUtils.Companion.isOneOf(questionBean.getInput_type(),
                LibDynamicAppConfig.QUS_MULTI_SELECT,
                LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE,
                LibDynamicAppConfig.QUS_MULTI_SELECT_LIMITED,
                LibDynamicAppConfig.QUS_LOOPING_MILTISELECT)) {
            for (Answers answers : questionBeanFilled.getAnswer()) {
                values.add(answers.getValue());
            }

        } else if (QuestionsUtils.Companion.isOneOf(questionBean.getInput_type(), LibDynamicAppConfig.QUS_TEXT, LibDynamicAppConfig.QUS_ADDRESS)) {
            List<Answers> answer = questionBeanFilled.getAnswer();
            if (!answer.isEmpty() && answer.get(0) != null) {
                values.add(QuestionsUtils.Companion.getValueFormTextInputType(answer.get(0)));
            }
        } else {
            List<Answers> answer = questionBeanFilled.getAnswer();
            if (!answer.isEmpty() && answer.get(0) != null) {
                values.add(answer.get(0).getValue());
            }

        }


        List<String> visibleList = new ArrayList<>();
        for (ChildBean childBean : questionBean.getChild()) {
            boolean isMatchPatten = false;
            boolean isMatchValue = false;
            boolean isValid = false;

            if (childBean != null && values.size() == 1) {
                String childValueIdPattern = childBean.getValue();
                isMatchPatten = matches(childValueIdPattern, values.get(0));
                isMatchValue = values.contains(childValueIdPattern);
                if (values.get(0).equals("")) {
                    isMatchPatten = false;
                    isMatchValue = false;
                }
                isValid = true;
            } else if (childBean != null && !values.isEmpty()) {
                String childValueIdPattern = childBean.getValue();
                isMatchPatten = false;
                for (String st : values)
                    if (matches(childValueIdPattern, st)) {
                        isMatchPatten = true;
                        break;
                    }
                isMatchValue = values.contains(childValueIdPattern);
                isValid = true;
            }
            if (isValid) {
                setChildStatus(childBean, isMatchPatten || isMatchValue, visibleList);
            }

        }

        if (!QuestionsUtils.Companion.isLoopingType(questionBean)) {
            validateAnsForAdditionalInfoMultiSelect(values, questionBean, baseType);
        }

    }


    private void validateAnsForAdditionalInfoMultiSelect(List<String> value, QuestionBean questionBean, BaseType baseType) {
        for (ValidationBean validationBean : questionBean.getValidation()) {
            if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_GPS)
                    || validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_IMAGE)
            ) {

                String requiredPattern = validationBean.getValue();
                boolean isMatchPatten = false;
                boolean isMatchValue = value.contains(requiredPattern);

                if (value.size() == 1) {
                    isMatchPatten = matches(requiredPattern, value.get(0));
                    if (value.get(0).equals("")) {
                        isMatchPatten = false;
                        isMatchValue = false;
                    }
                } else {
                    for (String st : value)
                        if (matches(requiredPattern, st)) {
                            isMatchPatten = true;
                            break;
                        }

                }


                if (requiredPattern.equals("")) {
                    isMatchPatten = true;
                }

                baseType.setAdditionalButtonStatus(NOT_ANSWERED);

                if (isMatchPatten || isMatchValue) {
                    baseType.setAdditionalVisibility(View.VISIBLE);
                } else {
                    baseType.setAdditionalVisibility(View.GONE);
                }

                QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
                if (questionBeanFilled != null && (isMatchPatten || isMatchValue)) {
                    questionBeanFilled.setValidAns(false);
                }
                break;
            }
        }
    }


    //moving to a postion in linear layout
    public void moveToPosition(final int scrollPosition) {
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.smoothScrollTo(0, scrollPosition);
            }
        });
    }

    private void saveDataToAnsList(QuestionBeanFilled questionBeanFilled, String value, String label, String textValue, String reference) {

        if (questionBeanFilled != null)
            if (questionBeanFilled.getAnswer().size() > 0) {
                // if size is greater then 0(max 1 for drop down) set value and update in Realm
                questionBeanFilled.getAnswer().get(0).setValue(value);
                questionBeanFilled.getAnswer().get(0).setReference(reference);
                questionBeanFilled.getAnswer().get(0).setLabel(label);
                questionBeanFilled.getAnswer().get(0).setTextValue(textValue);
            } else {
                //adding value if there is no exists
                List<Answers> answerBeanHelperList = new ArrayList<Answers>();
                Answers answers = new Answers();
                answers.setValue(value);
                answers.setTextValue(textValue);
                answers.setLabel(label);
                answers.setReference(reference);
                answerBeanHelperList.add(answers);
                questionBeanFilled.setAnswer(answerBeanHelperList);
            }
    }


    //adding in helperList
    private void addingInHelperArray(QuestionBeanFilled questionBeanFilled, boolean isRequired, boolean isValid, boolean isOptional) {
        if (questionBeanFilled != null) {
            questionBeanFilled.setRequired(isRequired);
            questionBeanFilled.setOptional(isOptional);
            questionBeanFilled.setFilled(isValid);
            questionBeanFilled.setValidAns(isValid);
        }
    }

    //adding in helperList
    private void addingInHelperArray(String childUid, boolean isRequired, boolean isValid, boolean isOptional) {
        QuestionBeanFilled helper = answerBeanHelperList.get(childUid);
        helper.setRequired(isRequired);
        helper.setOptional(isOptional);
        helper.setFilled(isValid);
        helper.setValidAns(isValid);
    }


    private List<Answers> getLocationAnswerList(String[] location) {
        List<Answers> answerBeanHelperList = new ArrayList<>();
        Answers accuracy = new Answers();
        Answers latitude = new Answers();
        Answers longitude = new Answers();

        accuracy.setLabel(Constant.ACCURACY);
        accuracy.setValue(location[0]);
        accuracy.setReference("");

        latitude.setLabel(Constant.LATITUDE);
        latitude.setValue(location[1]);
        latitude.setReference("");

        longitude.setLabel(Constant.LONGITUDE);
        longitude.setValue(location[2]);
        longitude.setReference("");


        answerBeanHelperList.add(accuracy);
        answerBeanHelperList.add(latitude);
        answerBeanHelperList.add(longitude);


        return answerBeanHelperList;
    }

    private void checkEditableDependency(QuestionBean questionBean) {
        String questionUniqueId = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
        if (formStatus != SUBMITTED && notifyOnchangeMap.containsKey(questionUniqueId)) {
            HashSet<String> orders = notifyOnchangeMap.get(questionUniqueId);
            if (orders != null) {
                for (String order : orders) {
                    QuestionBean childQuestion = questionBeenList.get(order);
                    BaseType baseType = questionObjectList.get(order);
                    if (childQuestion != null && baseType != null) {
                        if (QuestionsUtils.Companion.isQuestionHasAutoFillRestriction(childQuestion)) {
                            baseType.superSetEditable(true, childQuestion.getInput_type());
                        }
                    }

                }
            }

        }


    }

    //changing child visibility and validation
    private void setChildValidation(String childUid, QuestionBean questionBean, int visibility, View view) {

        view.setVisibility(visibility);
        boolean isActive = visibility == View.VISIBLE;
        boolean isReset = QuestionsUtils.Companion.isQuestionHasValidation(questionBean, LibDynamicAppConfig.VAL_REST_ON_PARENT_CHANGE);
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
        BaseType baseType = questionObjectList.get(childUid);

        if (isActive&&baseType.questionBean.getInput_type ().equals (LibDynamicAppConfig.QUS_DROPDOWN)&&baseType.questionBean.getAnswer_options ().size ()==1){
            AnswerOptionsBean answerOptionsBean = baseType.questionBean.getAnswer_options ().get (0);
            //saveDataToAnsList(questionBeanFilled, answerOptionsBean.get_id (), answerOptionsBean.getName (), "", "");
            //baseType.superSetAnswer (questionBeanFilled);

            this.SingleSelector (baseType.questionBean,answerOptionsBean.getName (),answerOptionsBean.get_id (),false);
        }


        if (questionBeanFilled != null && baseType != null) {
            boolean validAns = questionBeanFilled.isValidAns();
            if (!isActive || isReset) {
                questionBeanFilled.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
                questionBeanFilled.setNestedAnswer(new ArrayList<>());
                baseType.superResetQuestion();
                validAns = false;
                checkEditableDependency(questionBean);
            } else {
                QuestionBeanFilled singleQuestionFilled = PrefilledDefaultData.Companion.getInstance().getSingleQuestionFilled(childUid);
                if (singleQuestionFilled != null) {
                    validAns=handleDynamic(singleQuestionFilled);
                }

            }
            validAns = setStatusOfAnswerObject(questionBeanFilled, questionBean, isActive || isReset, validAns);

            if (!questionBean.getChild().isEmpty()) {
                for (ChildBean childBean : questionBean.getChild()) {
                    String nestedChildUid = QuestionsUtils.Companion.getChildUniqueId(childBean);
                    QuestionBean nestedChildQuestion = questionBeenList.get(nestedChildUid);
                    BaseType baseType1 = questionObjectList.get(nestedChildUid);
                    if (baseType1 != null) {
                        View nestedChildView = (linearLayout).getChildAt(baseType1.getViewIndex());
                        if (nestedChildView.getVisibility() == View.VISIBLE) {
                            if (!validAns) {
                                setChildValidation(nestedChildUid, nestedChildQuestion, View.GONE, nestedChildView);
                            } else {
                                setChildValidation(nestedChildUid, nestedChildQuestion, View.VISIBLE, nestedChildView);
                            }
                        }
                    }
                }
            }
        }


    }


    private boolean setStatusOfAnswerObject(QuestionBeanFilled questionBeanFilled, QuestionBean questionBean, boolean isCurrentlyActive, boolean validAns) {
        if (isCurrentlyActive && !questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LABEL)) {
            if (QuestionsUtils.Companion.isQuestionRequired(questionBean)) {
                addingInHelperArray(questionBeanFilled, true, validAns, false);
            } else {
                addingInHelperArray(questionBeanFilled, false, validAns, true);
            }
        } else {
            addingInHelperArray(questionBeanFilled, false, false, false);
            validAns = false;
        }
        return validAns;
    }

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


    //permission listener
    private void showPermissionAlert() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_required)
                .setMessage(R.string.please_accept_the_both_permissions_camera_and_storage)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!checkImagePermission()) {
                            requestImagePermissions();
                        }
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();

    }

    //AppUpdate alert
    public AlertDialog.Builder showUpdateAppAlert() {

        return new AlertDialog.Builder(this)
                .setTitle(R.string.app_update_is_require)
                //  .setMessage(R.string.latest_app_is_require_to_fill_this_form)
                .setCancelable(false)
                /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                */
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        finish();
                    }
                });
    }


    private boolean checkRestrictionOnNumberValues(QuestionBean questionBean, String text) {
        if (!QuestionsUtils.Companion.isValidFloat(text))
            return false;
        float currentValue = Float.parseFloat(text);

        for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
            if (restrictionsBean.getType().equals(REST_SHOULD_BE_GRATER_THAN)
                    || restrictionsBean.getType().equals(LibDynamicAppConfig.REST_SHOULD_BE_GRATER_THAN_EQUAL)
                    || restrictionsBean.getType().equals(REST_SHOULD_BE_LESS_THAN)
                    || restrictionsBean.getType().equals(LibDynamicAppConfig.REST_SHOULD_BE_EQUAL_TO)
                    || restrictionsBean.getType().equals(REST_SHOULD_BE_LESS_THAN_EQUAL)) {

                ///  return false only in this portion otherwise always return true
                List<Float> parentValuesList = new ArrayList<>();
                for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                    try {
                        List<Answers> answers = QuestionsUtils.Companion.getAnswerListbyOder(QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean), null, answerBeanHelperList);

                        if (!answers.isEmpty()) {
                            String p_ans = answers.get(0).getValue();
                            if (QuestionsUtils.Companion.isValidFloat(p_ans)) {
                                parentValuesList.add(Float.parseFloat(p_ans));
                            } else {
                                return false;
                            }
                        }

                    } catch (Exception e) {
                        return false;
                    }

                }
                boolean isValid = validateValueWithRestriction(currentValue, restrictionsBean, parentValuesList);
                if (!isValid) {
                    return false;
                }

            }

        }
        return true;
    }

    private void callForExpressionsRestriction(QuestionBean questionBean, String text) {
        if (!QuestionsUtils.Companion.isValidFloat(text))
            return;
        for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
            if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_CALL_FOR_EXPRESSION)) {
                List<String> listOfNotifyOrders = new ArrayList<>();
                for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                    String restrictionOrderUniqueId = QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean);
                    if (restrictionOrderUniqueId != null) {
                        listOfNotifyOrders.add(restrictionOrderUniqueId);
                    }
                }
                notifyOrdersForCalculation(listOfNotifyOrders, text, QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
            }
        }
    }


    private boolean notifyOrdersForCalculation(List<String> listOfNotifyOrders, String currentValue, String currentQusUid) {

        List<QuestionBean> listOfQuestionBeanForCalculation = new ArrayList<>();

        if (listOfNotifyOrders.size() > 0) {
            for (String ordersToNotify : listOfNotifyOrders) {
                QuestionBean questionBean = questionBeenList.get(ordersToNotify);
                if (questionBean != null) {
                    listOfQuestionBeanForCalculation.add(questionBean);
                }
            }
        }
        for (QuestionBean questionBean : listOfQuestionBeanForCalculation) {
            for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_CALL_FOR_ADD) ||
                        restrictionsBean.getType().equals(LibDynamicAppConfig.REST_CALL_FOR_SUB) ||
                        restrictionsBean.getType().equals(LibDynamicAppConfig.REST_CALL_FOR_MUL) ||
                        restrictionsBean.getType().equals(LibDynamicAppConfig.REST_CALL_FOR_DIVD)) {
                    solveExpressionAddSubMulDiv(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), restrictionsBean, currentValue, currentQusUid);
                    break;
                }
            }
        }


        return true;
    }

    void solveExpressionAddSubMulDiv(String questionUid, RestrictionsBean
            restrictionsBean, String currentValue, String currentQusUid) {
        List<Float> valuesForEquation = new ArrayList<>();
        float currentValueFloat = 0;
        if (QuestionsUtils.Companion.isValidFloat(currentValue)) {
            currentValueFloat = Float.parseFloat(currentValue);
        } else {
            currentValueFloat = 0;
        }
        for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
            if (QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean) != null && !QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean).equals(currentQusUid)) {
                try {
                    List<Answers> answers = QuestionsUtils.Companion.getAnswerListbyOder(QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean), null, answerBeanHelperList);
                    if (answers.size() > 0) {
                        String p_ans = answers.get(0).getValue();
                        if (QuestionsUtils.Companion.isValidFloat(p_ans)) {
                            valuesForEquation.add(Float.parseFloat(p_ans));
                        } else {
                            valuesForEquation.add(0f);
                        }

                    }
                } catch (Exception e) {
                    valuesForEquation.add(0f);
                }
            } else {
                valuesForEquation.add(currentValueFloat);
            }
        }

        float finalValue = 0;
        switch (restrictionsBean.getType()) {
            case LibDynamicAppConfig.REST_CALL_FOR_ADD:
                for (float pValue : valuesForEquation) {
                    finalValue += pValue;
                }
                break;
            case LibDynamicAppConfig.REST_CALL_FOR_SUB:
                finalValue = valuesForEquation.get(0);
                for (int i = 1; i < valuesForEquation.size(); i++) {
                    finalValue -= valuesForEquation.get(i);
                }
                break;
            case LibDynamicAppConfig.REST_CALL_FOR_MUL:
                finalValue = 1;
                for (float pValue : valuesForEquation) {
                    finalValue += pValue;
                }
                break;
            case LibDynamicAppConfig.REST_CALL_FOR_DIVD:
                break;
        }

        BaseType baseType = questionObjectList.get(questionUid);
        if (baseType != null) {
            baseType.superSetAnswer("");
            baseType.superSetAnswer("" + Math.round(finalValue));

        }

    }

    private void setChildStatus(ChildBean childBean, boolean isMatch, List<String> visibleList) {


        String childUniqueId = QuestionsUtils.Companion.getChildUniqueId(childBean);
        BaseType baseType = questionObjectList.get(childUniqueId);
        View view = (this.linearLayout).getChildAt(baseType.getViewIndex());
        QuestionBean childQuestionBean = questionBeenList.get(childUniqueId);
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(childUniqueId);
        if (questionBeanFilled != null && baseType != null && view != null && childQuestionBean != null) {
            //show child and add validation
            isMatch = getChildVisibilityOnMultiParent(isMatch, childQuestionBean, answerBeanHelperList, questionBeenList);
            QuestionBean childQuestionBeanFound = questionBeenList.get (childUniqueId);
            if (childQuestionBeanFound!=null && childQuestionBeanFound.containsValidation(LibDynamicAppConfig.VAL_REVERSE_VISIBILITY)) {
                isMatch = !isMatch;
            }

            if (isMatch) {
                visibleList.add(childUniqueId);
                setChildValidation(childUniqueId, childQuestionBean, View.VISIBLE, view);
                childQuestionBean.setEditable(!QuestionsUtils.Companion.isQuestionHasValidation(childQuestionBean, LibDynamicAppConfig.VAL_NOT_ABLE_TO_FILL));
                if (formStatus == EDITABLE_DARFT || formStatus == SYNCED_BUT_EDITABLE) {
                    baseType.superSetEditable(true, childQuestionBean.getInput_type());
                    setFilledAns(questionBeanFilled, false, false);
                }/*else if( !questionBeanFilled.getInput_type().equals(LibDynamicAppConfig.QUS_TEXT)
                && questionBeanFilled.getInput_type().equals(LibDynamicAppConfig.QUS_ADDRESS) &&
                childQuestionBean.getAnswer_options ().size () == 1 && !questionBeanFilled.getAnswer ().isEmpty ()) {
//                    List<Answers> ansList = new ArrayList<> ();
//                    ans
                    questionBeanFilled.setAnswer (DynamicLibUtils.Companion.getAnswerFormText(childQuestionBean.getAnswer_options().get (0).get_id (), childQuestionBean));
//                    if (questionBeanFilled.getInput_type().equals(LibDynamicAppConfig.QUS_TEXT)
//                            || questionBeanFilled.getInput_type().equals(LibDynamicAppConfig.QUS_ADDRESS)) {
//                    questionBeanFilled.setAnswer (childQuestionBean.getAnswer_options ().get (0).get_id ());
                    setFilledAns(questionBeanFilled, false, false);
                }*/

                //   childanswerBeanHelperList.get(childPos).setRequired(true);
            } else {
                if (!visibleList.contains(childUniqueId)) {
                    setChildValidation(childUniqueId, childQuestionBean, View.GONE, view);
                    setFilledAns(questionBeanFilled, false, false);

                }
            }


        } else if (childUniqueId.contains(".")) {
            //handle nested child

            QuestionBean childQuestionBeanList = childQuestionBeenList.get(childUniqueId);
            boolean childVisibilityOnMultiParent = getChildVisibilityOnMultiParent(isMatch, childQuestionBeanList, answerBeanHelperList, questionBeenList);
            String parentId = childUniqueId.split("\\.")[0];

            questionBeanFilled = answerBeanHelperList.get(parentId);
            if (questionBeanFilled != null && questionBeanFilled.getNestedAnswer() != null) {
                for (Nested nested : questionBeanFilled.getNestedAnswer()) {
                    for (QuestionBeanFilled childAnswer : nested.getAnswerNestedData()) {
                        if (childAnswer.getOrder().equals(childUniqueId)) {
                            if (childVisibilityOnMultiParent) {
                                setStatusOfAnswerObject(childAnswer, childQuestionBeanList, true, childAnswer.isValidAns());
                            } else {
                                setStatusOfAnswerObject(childAnswer, childQuestionBeanList, false, childAnswer.isValidAns());
                                childAnswer.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
                            }
                        }
                    }
                }
            }
        }

    }


    private boolean getChildVisibilityOnMultiParent(boolean isMatch, QuestionBean childQuestionBean, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList, LinkedHashMap<String, QuestionBean> questionBeenList) {
        if (childQuestionBean.getParent().size() > 1) {
            isMatch = QuestionsUtils.Companion.validateVisibilityWithMultiParent(childQuestionBean, answerBeanHelperList, questionBeenList);
        }
        for (RestrictionsBean restrictionsBean : childQuestionBean.getRestrictions()) {
            if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_MULTI_ANS_VISIBILITY_IF_NO_ONE_SELECTED)) {
                showProgress(this, getString(R.string.loading));
                isMatch = QuestionsUtils.Companion.validateMultiAnsRestriction(restrictionsBean, answerBeanHelperList, questionBeenList);
                hideProgess();
                break;
            }
        }
        return isMatch;
    }

    private void applyTitleChangeRestriction(RestrictionsBean restrictionsBean, String text) {
        switch (restrictionsBean.getType()) {
            case LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD:
                for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                    replaceTitleOfQuestion(QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean), LibDynamicAppConfig.BLANK_TITLE, text);
                }

                break;
        }

    }

    private void replaceTitleOfQuestion(String questionID, String oldText, String newText) {
        QuestionBean questionBean = questionBeenList.get(questionID);
        BaseType baseType = questionObjectList.get(questionID);
        if (baseType != null && questionBean != null) {
            String oldString = questionBean.getTitle();
            if (oldString.contains(oldText)) {
                oldString = oldString.replace(oldText, newText);
                baseType.superChangeTitle(oldString);
                answerBeanHelperList.get(questionID).setTitle(oldString);
            }
        } else if (questionBean != null) {
            String oldString = questionBean.getTitle();
            if (oldString.contains(oldText)) {
                oldString = oldString.replace(oldText, newText);
                newTitles.put(questionID, oldString);
            }
        }

    }


    /*  public Nested getNestedNewItem(int numberOfQuestions, int subIndex, String forParentValue) {

          Nested nested = new Nested();
          nested.setForParentValue(forParentValue);
          List<QuestionBeanFilled> nestedAnswer = new ArrayList<>();
          int questionNumber = 1;
          while (questionNumber <= numberOfQuestions) {
              int questionOrder = ((subIndex - 1) * numberOfQuestions) + questionNumber;
              QuestionBeanFilled questionBeanFilled = getQuestionBeanFilledNewData(questionOrder);
              nestedAnswer.add(questionBeanFilled);
              questionNumber++;
          }

          nested.setAnswerNestedData(nestedAnswer);

          return nested;
      }*/
    public Nested getNestedNewItemUpdated(List<QuestionBean> childQuestionBeenList, String forParentValue) {

        Nested nested = new Nested();
        nested.setForParentValue(forParentValue);
        List<QuestionBeanFilled> nestedAnswer = new ArrayList<>();
        for (QuestionBean questionBean : childQuestionBeenList) {
            QuestionBeanFilled questionBeanFilled = getQuestionBeanFilledNewData(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), questionBean.getColumnName());
            nestedAnswer.add(questionBeanFilled);

        }
        nested.setAnswerNestedData(nestedAnswer);

        return nested;
    }


    public Nested modifiedNestedUpdated(Nested nested, String
            forParentValue, List<QuestionBean> childQuestionsList) {
        nested.setForParentValue(forParentValue);
        // to update to new order for the child
        for (QuestionBeanFilled questionBeanFilled : nested.getAnswerNestedData()) {
            String childKey = QuestionsUtils.Companion.getAnswerUniqueId(questionBeanFilled);
            questionBeanFilled.setOrder(childKey);
        }

        if (childQuestionsList.size() != nested.getAnswerNestedData().size()) {
            for (QuestionBean childQues : childQuestionsList) {
                boolean isFound = false;
                for (QuestionBeanFilled questionBeanFilled : nested.getAnswerNestedData()) {
                    if (QuestionsUtils.Companion.getQuestionUniqueId(childQues).equals(QuestionsUtils.Companion.getAnswerUniqueId(questionBeanFilled))) {
                        isFound = true;
                        break;
                    }
                }

                if (!isFound) {
                    QuestionBeanFilled newAnswerBeanObject = getNewAnswerBeanObject(childQues, childQues.getParent().isEmpty());
                    nested.getAnswerNestedData().add(newAnswerBeanObject);
                }

            }
        }


        return nested;
    }

    //creating new Answer Object
    protected QuestionBeanFilled getNewAnswerBeanObject(final QuestionBean questionBean, final boolean isVisibleInHideList) {
        String questionUid = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);


        QuestionBeanFilled answerBeanHelper = new QuestionBeanFilled();
        answerBeanHelper = new QuestionBeanFilled();
        answerBeanHelper.setUid(UUID.randomUUID().toString());
        answerBeanHelper.setFilled(false);
        answerBeanHelper.setValidAns(false);
        answerBeanHelper.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
        answerBeanHelper.setInput_type(questionBean.getInput_type());
        answerBeanHelper.setOrder(questionUid);
        answerBeanHelper.setColumnName(questionBean.getColumnName());
        answerBeanHelper.setLabel(questionBean.getLabel());
        answerBeanHelper.setTitle(questionBean.getTitle());
        answerBeanHelper.setViewSequence(questionBean.getViewSequence());
        boolean isActive = (isVisibleInHideList || (questionBean.getParent().size() == 0)
                && !questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_LABEL));

        List<ValidationBean> valiList = questionBean.getValidation();
        if (!valiList.isEmpty()) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_REQUIRED)) {
                    answerBeanHelper.setOptional(false);
                    answerBeanHelper.setRequired(isActive);
                    break;
                }
            }
        } else {
            answerBeanHelper.setRequired(false);
            answerBeanHelper.setOptional(isActive);
        }


        return answerBeanHelper;
    }


    private QuestionBeanFilled getQuestionBeanFilledNewData(String childQuestionKeyOrder, String columnName) {
        QuestionBeanFilled questionBeanFilled = new QuestionBeanFilled();
        questionBeanFilled.setUid(UUID.randomUUID().toString());
        questionBeanFilled.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
        questionBeanFilled.setInput_type("");
        questionBeanFilled.setLabel("");
        questionBeanFilled.setOptional(false);
        questionBeanFilled.setOrder(childQuestionKeyOrder);
        questionBeanFilled.setColumnName(columnName);
        questionBeanFilled.setTitle("");
        questionBeanFilled.setRequired(false);
        questionBeanFilled.setFilled(false);
        questionBeanFilled.setValidAns(false);
        questionBeanFilled.setNestedAnswer(new ArrayList<Nested>());
        questionBeanFilled.setViewSequence("");
        return questionBeanFilled;
    }


    public static List<String> getKeysFromMap(Map mp) {
        List<String> oders = new ArrayList<>();
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String qorder = pair.getKey().toString();
            if (qorder != null && !qorder.equals("")) {
                oders.add(qorder);
            }
        }
        return oders;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NESTEDCHILD_CODE && data != null) {
            int childStatus = data.getIntExtra("childState", 0);
            String questionUid = data.getStringExtra("questionOrder");
            BaseType baseType = questionObjectList.get(questionUid);
            if (baseType != null) {
                View linear = linearLayout.getChildAt(baseType.getViewIndex());
                QuestionBeanFilled questionBeanFilled1 = answerBeanHelperList.get(questionUid);
                if (questionBeanFilled1 != null) {
                    if (linear instanceof EditTextWIthButtonView) {
                        EditTextWIthButtonView dynamicLoopingView = (EditTextWIthButtonView) linear;
                        if (childStatus == SUBMITTED) {
                            setFilledAns(questionBeanFilled1, true, true);
                            dynamicLoopingView.changebuttonStatus(true, 1);
                        } else {
                            setFilledAns(questionBeanFilled1, false, false);
                            dynamicLoopingView.changebuttonStatus(false, 3);
                        }

                    }

                   /* if (!findNested.isEmpty()) {
                        String andUid = findNested.get(questionUid);
                        Realm defaultInstance = Realm.getDefaultInstance();
                        if (andUid != null && !andUid.isEmpty()) {
                            QuestionBeanFilled newQuestionBeanFilled = defaultInstance.where(QuestionBeanFilled.class).equalTo("uid", andUid).findFirst();
                            if (newQuestionBeanFilled != null) {
                                List<Nested> newNestedList = new ArrayList<>();
                        //        newNestedList.addAll(defaultInstance.copyFromRealm(newQuestionBeanFilled.getNestedAnswer()));
                                questionBeanFilled1.setNestedAnswer(newNestedList);
                            }
                        }
                        defaultInstance.close();
                    }*/
                }

            }
            refreshLoopingFiltersQuestions();
        }
    }

    @Override
    protected void onDestroy() {

       PrefilledDefaultData.Companion.releaseData();
        super.onDestroy();
        if (linearLayout != null) {
            linearLayout.removeAllViews();
        }
        questionObjectList.clear();
        questionObjectList = null;
        dataListener = null;
        imageViewListener = null;
        onLoopingButtonClickListener = null;
        recordAudioResponseListener = null;
        radioButtonListener = null;
        onGetLocationClickListener = null;


    }

    @Override
    protected void onStop() {
        super.onStop();

    }
/*
    protected void deleteOptionFromForm(String fid, String questionUid, String optionId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Form> dataBean = realm.where(Form.class).equalTo("formId",
                Integer.parseInt(fid))
                .sort("version", Sort.DESCENDING).findAll();
        if (dataBean.size() > 0)
            for (LanguageBean languageBean : dataBean.get(0).getLanguage()) {
                for (QuestionBean questionBean : languageBean.getQuestion()) {
                    if (QuestionsUtils.getQuestionUniqueId(questionBean).equals(questionUid)) {
                        for (AnswerOptionsBean answerOptionsBean : questionBean.getAnswer_options()) {
                            if (answerOptionsBean.get_id().equals(optionId)) {
                                answerOptionsBean.deleteFromRealm();
                                break;
                            }
                        }
                    }
                }
            }

        realm.commitTransaction();
        realm.close();

    }*/

    protected void clearAnswerAndView(QuestionBean questionBean) {
        String questionUniqueId = QuestionsUtils.Companion.getQuestionUniqueId(questionBean);
        BaseType baseType = questionObjectList.get(questionUniqueId);
        if (answerBeanHelperList.containsKey(questionUniqueId)) {
            setFilledAns(answerBeanHelperList.get(questionUniqueId), false, false);
            answerBeanHelperList.get(questionUniqueId).setAnswer(QuestionsUtils.Companion.getNewAnswerList());
        }
        if (baseType != null) {
            baseType.superChangeStatus(NONE);
            baseType.superSetAnswer("");
        }
    }

    public List<QuestionBeanFilled> findInvalidAnswersList
            (LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList,
             HashMap<String, BaseType> questionObjectList) {
        List<QuestionBeanFilled> unansweredList = new ArrayList<>();

        for (QuestionBeanFilled questionBeanFilled : answerBeanHelperList.values()) {
            if (((!questionBeanFilled.isFilled() || !questionBeanFilled.isValidAns()) && questionBeanFilled.isRequired())
                    || (questionBeanFilled.isFilled() && !questionBeanFilled.isValidAns())) {
                unansweredList.add(questionBeanFilled);

                BaseType baseType = questionObjectList.get(QuestionsUtils.Companion.getAnswerUniqueId(questionBeanFilled));
                if (baseType != null) {
                    //updating value in dependent field
                    baseType.superChangeStatus(NOT_ANSWERED);
                }

            }
        }
        return unansweredList;
    }


    public void refreshLoopingFiltersQuestions() {
        if (loopingTypeQuestion.size() > 0) {
            for (String questionUid : loopingTypeQuestion) {
                QuestionBean questionBean = questionBeenList.get(questionUid);
                if (questionBean != null) {
                    inValidAnswerWithLoopingDependent(questionBean);
                }
            }
        }
    }

    List<QuestionBeanFilled> inValidAnswerWithLoopingDependent(QuestionBean loopingTypeQuestion) {
        List<QuestionBeanFilled> ansWithWrongValue = new ArrayList<>();
        for (ChildBean childBean : loopingTypeQuestion.getChild()) {
            String childUniqueID = QuestionsUtils.Companion.getChildUniqueId(childBean);
            QuestionBean childQuestionBean = questionBeenList.get(childUniqueID);

            if (childQuestionBean != null) {
                for (RestrictionsBean restrictionsBean : childQuestionBean.getRestrictions()) {
                    if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_GET_ANS_OPTION_LOOPING)) {
                        List<AnswerOptionsBean> currentAvailableAnswerOption = childQuestionBean.getAnswer_options();
                        currentAvailableAnswerOption = QuestionsUtils.Companion.getAnswerListFormRestriction(currentAvailableAnswerOption, restrictionsBean, childQuestionBean, answerBeanHelperList);
                        List<Answers> childAnswerList = QuestionsUtils.Companion.getAnswerListbyOder(childUniqueID, null, answerBeanHelperList);
                        boolean isValidAnswer = QuestionsUtils.Companion.validateAnswerListWithAnswerOptions(childAnswerList, currentAvailableAnswerOption);
                        if (!isValidAnswer) {
                            QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(childUniqueID);
                            if (questionBeanFilled != null) {
                                setFilledAns(questionBeanFilled, false, false);
                                questionBeanFilled.setAnswer(QuestionsUtils.Companion.getNewAnswerList());
                                questionBeanFilled.setNestedAnswer(new ArrayList<>());

                            }
                            String childAnsId = QuestionsUtils.Companion.getQuestionUniqueId(childQuestionBean);
                            BaseType baseType = questionObjectList.get(childAnsId);
                            if (baseType != null) {
                                //updating value in dependent field
                                baseType.superResetQuestion();
                                baseType.superChangeStatus(NOT_ANSWERED);
                            }
                        }
                        break;
                    } else if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_GET_ANS_SUM_LOOPING)) {
                        List<Answers> answers = new ArrayList<>();
                        float sum = 0;
                        for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                            if (loopingTypeQuestion.getChild().size() > 0)
                                answers.addAll(QuestionsUtils.Companion.getNestedAnswerListFromNestedType(QuestionsUtils.Companion.getQuestionUniqueId(loopingTypeQuestion),
                                        QuestionsUtils.Companion.getRestrictionOrderUniqueId(ordersBean), ordersBean.getValue(), answerBeanHelperList));
                        }
                        if (answers.size() > 0) {

                            for (Answers calc : answers) {
                                if (!calc.getValue().equals(""))
                                    sum = sum + Float.parseFloat(calc.getValue());
                            }
                        }

                        if (!answerBeanHelperList.get(childUniqueID).getAnswer().get(0).getValue().equals("")) {

                            if (Integer.parseInt(answerBeanHelperList.get(childUniqueID).getAnswer().get(0).getValue()) != Math.round(sum)) {
                                String childAnsId = QuestionsUtils.Companion.getQuestionUniqueId(childQuestionBean);
                                BaseType baseType = questionObjectList.get(childAnsId);
                                if (baseType != null) {
                                    //updating value in dependent field
                                    baseType.superSetAnswer(String.valueOf(Math.round(sum)));
                                }
                            }
                        } else {
                            String childAnsId = QuestionsUtils.Companion.getQuestionUniqueId(childQuestionBean);
                            BaseType baseType = questionObjectList.get(childAnsId);
                            if (baseType != null) {
                                //updating value in dependent field
                                baseType.superSetAnswer(String.valueOf(Math.round(sum)));
                            }
                        }
                    }
                }
            }


        }
        return ansWithWrongValue;
    }


    public List<String> checkRegexHashMap(Map mp) {
        List<String> oders = new ArrayList<>();
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Boolean qorder = pair.getValue().equals(true);
            if (qorder == true) {
                oders.clear();
                oders.add(pair.getKey().toString());
            }
        }
        return oders;
    }

    public String get3DecimalOrder(String order, int decimalCount) {
        try {
            return String.format("%." + decimalCount + "f", Float.parseFloat(order));

        } catch (Exception e) {
            return String.format("%." + decimalCount + "f", Float.parseFloat(order));

        }

    }

    public void setFilledAns(QuestionBeanFilled questionBeanFilled, boolean isFilled, boolean isValidAns) {
        questionBeanFilled.setFilled(isFilled);
        questionBeanFilled.setValidAns(isValidAns);
    }

    public void setFilledAns(QuestionBeanFilled questionBeanFilled, boolean isValidAns) {
        questionBeanFilled.setValidAns(isValidAns);
    }

    private void openAdditionalForm(QuestionBean questionBean) {
      /*  Intent intent = new Intent(BaseFormActivity.this, AdditionalFormActivity.class);
        List<Answers> childAnswerList = QuestionsUtils.getAnswerListbyOder(QuestionsUtils.getQuestionUniqueId(questionBean), null, answerBeanHelperList);

        for (ValidationBean validationBean : questionBean.getValidation()) {
            if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_GPS) && (childAnswerList.get(0).getValue().matches(validationBean.getValue()) || validationBean.getValue().equals(""))) {
                intent.putExtra(Constant.IS_LOCATION_REQUIRED, true);
            } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_IMAGE) && (childAnswerList.get(0).getValue().matches(validationBean.getValue()) || validationBean.getValue().equals(""))) {
                intent.putExtra(Constant.IS_IMAGE_REQUIRED, true);
            }
        }


        intent.putExtra(Constant.QUESTION_ORDER, QuestionsUtils.getQuestionUniqueId(questionBean));
        startActivityForResult(intent, ADDITIONAL_FORM_CODE);
        overridePendingTransition(R.anim.from_right, R.anim.to_right);
*/
    }

    @Override
    public void Question(String questionUid) {
        BaseType baseType = questionObjectList.get(questionUid);
        if(baseType!=null){
            if (baseType.expandableParentView != null) {
                baseType.expandableParentView.expandView();
            }
            int position = baseType.getViewIndex();
            int parentPosition = linearLayout.getTop() + 1;

            if(position >= 0){
                int nestedParentPosition = getBaseParentType(baseType);
                int expandableParentView = getExpandableParent(baseType);
                int childPosition = baseType.view.getTop();
                int scrollPosition =
                        parentPosition + childPosition + nestedParentPosition + expandableParentView;
                moveToPosition(scrollPosition);

            } else {
                showToast(R.string.question_still_not_loaded);
            }
        } else {
            showToast(R.string.question_still_not_loaded);
        }

    }

    int getBaseParentType(BaseType baseType){
        if(baseType.getParentLayout() != null) {
            return baseType.getParentLayout().getTop();
        } else {
           return 0;
        }
    }


    int getExpandableParent(BaseType baseType){
        if(baseType.getExpandableParentView() != null) {
            return baseType.getExpandableParentView().view.getTop();
        } else {
           return 0;
        }
    }
}
