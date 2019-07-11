package com.dhwaniris.dynamicForm.ui.activities.formActivities;

import android.animation.ObjectAnimator;

import androidx.lifecycle.MutableLiveData;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
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
import com.dhwaniris.dynamicForm.utils.HideQuestionsState;
import com.dhwaniris.dynamicForm.utils.LocationHandler;
import com.dhwaniris.dynamicForm.utils.LocationReceiver;
import com.dhwaniris.dynamicForm.utils.PermissionHandler;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.REST_CLEAR_DID_CHILD;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.REST_SHOULD_BE_GRATER_THAN;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.REST_SHOULD_BE_LESS_THAN;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.REST_SHOULD_BE_LESS_THAN_EQUAL;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SYNCED_BUT_EDITABLE;
import static com.dhwaniris.dynamicForm.utils.QuestionsUtils.getNestedAnswerListFromNestedType;
import static com.dhwaniris.dynamicForm.utils.QuestionsUtils.getNewAnswerList;
import static java.util.regex.Pattern.matches;


public class BaseFormActivity extends BaseActivity implements SelectListener, ImageSelectListener {

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
            createOrModifyAnswerBeanObject(questionBean, isVisibleInHideList);
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
            return preferenceHelper.LoadStringPref(AppConfing.LANGUAGE, "en");
        }


    };


    QuestionHelperCallback.ImageViewListener imageViewListener = new QuestionHelperCallback.ImageViewListener() {

        @Override
        public void clickImage(final QuestionBean questionBean) {
            if (!checkImagePermission() && !alreadyRequest) {
                permissionListener = new PermissionListener() {
                    @Override
                    public void acceptedPermission() {
                        CaptureImage(BaseFormActivity.this, questionBean);
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
                    CaptureImage(BaseFormActivity.this, questionBean);
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
            createOrModifyAnswerBeanObject(questionBean, isVisibleInHideList);

        }
    };


    QuestionHelperCallback.QuestionButtonClickListener onLoopingButtonClickListener = new QuestionHelperCallback.QuestionButtonClickListener() {
        @Override
        public void onclickOnQuestionButton(QuestionBean questionBean) {
            showProgress(BaseFormActivity.this, getString(R.string.loading));
            final List<QuestionBean> childQuestionsList = new ArrayList<>();
            final String questionUid = QuestionsUtils.getQuestionUniqueId(questionBean);
            int childCount = 0;
            final ArrayList<String> childListString = new ArrayList<>();
            final ArrayList<String> childListValues = new ArrayList<>();
            LinkedHashMap<String, Nested> nestedLinkedHashMap = new LinkedHashMap<>();
            boolean isvalidAns = false;
            int totalChildQuestionCount;


            final QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(questionUid);
            if (questionBean.getInput_type().equals(AppConfing.QUS_LOOPING)) {
                String currentCountOfRepetition = questionBeanFilled.getAnswer().get(0).getValue();
                if (currentCountOfRepetition != null & !currentCountOfRepetition.equals("")) {
                    isvalidAns = true;
                    int numberofCount = Integer.parseInt(currentCountOfRepetition);
                    childCount = numberofCount;
                    for (int i = 1; i <= numberofCount; i++) {
                        nestedLinkedHashMap.put(String.valueOf(i), null);
                        childListString.add(String.valueOf(i));
                        childListValues.add(String.valueOf(i));
                    }
                }
            } else if (questionBean.getInput_type().equals(AppConfing.QUS_LOOPING_MILTISELECT)) {
                List<Answers> questionAns = questionBeanFilled.getAnswer();
                if (!questionAns.isEmpty() && !questionAns.get(0).getValue().equals("")) {
                    isvalidAns = true;
                    childCount = questionAns.size();
                    for (Answers answers : questionAns) {
                        nestedLinkedHashMap.put(answers.getValue(), null);
                        childListString.add(answers.getLabel());
                        childListValues.add(answers.getValue());
                    }

                }
            }
            String ansUniqueId;
            int childStatus;
            boolean isChildLocationRequired = false;
            for (QuestionBean questionBean1 : childQuestionBeenList.values()) {
                String childQuestionOrder = QuestionsUtils.getQuestionUniqueId(questionBean1);
                String childQuestionGroup = childQuestionOrder.substring(0, childQuestionOrder.indexOf("."));
                if (childQuestionGroup.equals(questionUid)) {
                    childQuestionsList.add(questionBean1);
                    isChildLocationRequired = questionBean1.getInput_type().equals(AppConfing.QUS_GET_LOCTION);

                }
            }

            if (childQuestionsList.size() == 0) {
                showToast(R.string.no_child_question_found);
                hideProgess();
                return;
            }

            if (isvalidAns) {
                ansUniqueId = questionBeanFilled.getUid();
                totalChildQuestionCount = childQuestionsList.size();
                List<Nested> oldNestedList = questionBeanFilled.getNestedAnswer();


                if (!(formStatus == AppConfing.SUBMITTED || formStatus == EDITABLE_SUBMITTED)) {
                    if (oldNestedList == null || oldNestedList.size() == 0) {

                        //new create new answer for nested
                        List<Nested> newNestedList = new ArrayList<>();
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
                        List<Nested> nestedList = new ArrayList<>();
                        for (Map.Entry<String, Nested> item : nestedLinkedHashMap.entrySet()) {
                            boolean isFound = false;
                            for (Nested nested : oldNestedList) {
                                if (item.getKey().equals(nested.getForParentValue())) {
                                    nestedList.add(modifiedNestedUpdated(nested, item.getKey()));
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                Nested tempNested = getNestedNewItemUpdated(childQuestionsList, item.getKey());
                                nestedList.add(tempNested);

                            }
                        }

                        //

                        oldNestedList.clear();
                        oldNestedList.addAll(nestedList);
                        questionBeanFilled.setNestedAnswer(oldNestedList);
                    }

                } else {
                    childStatus = SUBMITTED;
                }
                final String questionUniqueId = UUID.randomUUID().
                        toString();

                final String finalAnsUniqueId = ansUniqueId;
                final int finalChildStatus = childStatus;
                findNested.put(questionUid, finalAnsUniqueId);
                final int finalQuestionCount = childCount;
                final int finalTotalChildQuestionCount = totalChildQuestionCount;
                boolean finalIsChildLocationRequired = isChildLocationRequired;
               /* realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

          *//*              LanguageBean languageBean = new LanguageBean();
                        languageBean.setTitle(questionUniqueId);
                        languageBean.setQuestion(childQuestionsList);
                        realm.insertOrUpdate(languageBean);
                        realm.insertOrUpdate(questionBeanFilled);
*//*

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        hideProgess();
                        Intent intent = new Intent(BaseFormActivity.this, InnerLoopingFormActivity.class);
                        intent.putExtra("count", finalQuestionCount);
                        intent.putStringArrayListExtra("countListString", childListString);
                        intent.putStringArrayListExtra("countListValue", childListValues);
                        intent.putExtra("ansUniqueId", finalAnsUniqueId);
                        intent.putExtra("qusUniqueId", questionUniqueId);
                        intent.putExtra("tvFormStatus", finalChildStatus);
                        intent.putExtra("totalQuestionCount", finalTotalChildQuestionCount);
                        intent.putExtra("questionOrder", questionUid);
                        intent.putExtra("formId", form_id);
                        intent.putExtra("formLang", filledFormList.getLanguage());
                        intent.putExtra("locationRequired", finalIsChildLocationRequired);
                        startActivityForResult(intent, NESTEDCHILD_CODE);
                        overridePendingTransition(R.anim.from_right, R.anim.to_right);


                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(@NonNull Throwable error) {
                        hideProgess();

                    }
                });
                realm.close();*/

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
                twoStepValidation.put(QuestionsUtils.getQuestionUniqueId(questionBean), 1);
            }


        }
    };

    QuestionHelperCallback.QuestionButtonClickListener onGetLocationClickListener = new QuestionHelperCallback.QuestionButtonClickListener() {
        @Override
        public void onclickOnQuestionButton(QuestionBean questionBean) {
            gpsOnSetLocation(questionBean, false);
        }


    };

    private void gpsOnSetLocation(QuestionBean questionBean, boolean isButtonHide) {


        LocationGetType locationGetType = null;

        BaseType baseType = questionObjectList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
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
            createOrModifyAnswerBeanObject(questionBean, isVisibleInHideList);
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
            QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
            if (questionBeanFilled != null) {
                if (isRecoded) {
                    saveDataToAnsList(questionBeanFilled, path, length, "", path);
                    setFilledAns(questionBeanFilled, true, true);
                } else {
                    questionBeanFilled.setAnswer(getNewAnswerList());
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
            createOrModifyAnswerBeanObject(questionBean, isVisibleInHideList);
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
            final String questionUid = QuestionsUtils.getQuestionUniqueId(questionBean);
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
                        if (validationBean.get_id().equals(AppConfing.VAL_REGEX)) {
                            isRegex = true;
                        } else if (validationBean.get_id().equals(AppConfing.VAL_ALERT_REGEX)) {
                            isAlertRegex = true;
                            alertRegexList.add(validationBean.getError_msg());
                            alertMsg = "" + getString(R.string.are_you_sure);
                        } else if (validationBean.get_id().equals(AppConfing.VAL_ALERT_MSG)) {
                            if (validationBean.getError_msg() != null && !validationBean.getError_msg().equals("")) {
                                alertMsg = validationBean.getError_msg();
                            } else {
                                alertMsg = "" + getString(R.string.are_you_sure);
                            }
                        } else if (validationBean.get_id().equals(AppConfing.VAL_ALERT_IF_BETWEEN)) {
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

                    if (questionBean.getInput_type().equals(AppConfing.QUA_UNIT_CONVERSION) &&
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
                validateChildVisibility(questionBean, standardValue, baseType);
                updateCount();

            } else {
                BaseActivity.logDatabase(AppConfing.END_POINT, String.format(Locale.ENGLISH,
                        "Question filled with null. Question ID : %s Line no. 552",
                        questionBean.get_id()), AppConfing.UNEXPECTED_ERROR,
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

    protected void createViewObject(QuestionBean questionBean, int formStatus) {
        View view = null;
        String questionUniqueId = QuestionsUtils.getQuestionUniqueId(questionBean);
        switch (questionBean.getInput_type()) {
            case AppConfing.QUS_TEXT:
            case AppConfing.QUS_ADDRESS:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                EditTextSimpleText editTextSimpleText = new EditTextSimpleText(dataListener, view, questionBean, formStatus, questionBeenList, answerBeanHelperList);
                questionObjectList.put(questionUniqueId, editTextSimpleText);
                break;
            case AppConfing.QUS_NUMBER:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                EditTextNumber editTextNumber = new EditTextNumber(dataListener, view, questionBean, formStatus, questionBeenList, answerBeanHelperList, editTextFocusChangeListener);
                questionObjectList.put(questionUniqueId, editTextNumber);
                break;
            case AppConfing.QUS_AADHAAR:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                EditTextAadhar editTextAadhar = new EditTextAadhar(dataListener, view, questionBean, formStatus, questionBeenList, answerBeanHelperList);
                questionObjectList.put(questionUniqueId, editTextAadhar);
                break;

            case AppConfing.QUS_DATE:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                DateSelect dateSelect = new DateSelect(view, questionBean, formStatus, dataListener, questionBeenList, answerBeanHelperList);
                questionObjectList.put(questionUniqueId, dateSelect);
                break;

            case AppConfing.QUS_IMAGE:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_img_dynam, linearLayout, false);
                ImageType imageType = new ImageType(view, questionBean, formStatus, imageViewListener,
                        questionBeenList, answerBeanHelperList);

                questionObjectList.put(questionUniqueId, imageType);
                break;
            case AppConfing.QUS_LABEL:
                view = getLayoutInflater().inflate(R.layout.dy_row_label, linearLayout, false);
                BaseLabelType labelType = new BaseLabelType(view);
                labelType.setAnswerAnsQuestionData(answerBeanHelperList);
                labelType.setBasicFunctionality(view, questionBean, formStatus);
                if (formStatus == NEW_FORM) {
                    createOrModifyAnswerBeanObject(questionBean, view.getVisibility() == View.VISIBLE);
                }
                questionObjectList.put(questionUniqueId, labelType);
                break;

            case AppConfing.QUS_DROPDOWN:
            case AppConfing.QUS_DROPDOWN_HIDE: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                SingleSelect singleSelect = new SingleSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, form_id);
                questionObjectList.put(questionUniqueId, singleSelect);
            }
            break;

            case AppConfing.QUS_MULTI_SELECT:
            case AppConfing.QUS_MULTI_SELECT_HIDE: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                MultiSelect multiSelect = new MultiSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, form_id);
                questionObjectList.put(questionUniqueId, multiSelect);
            }
            break;
            case AppConfing.QUS_MULTI_SELECT_LIMITED: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dynam, linearLayout, false);
                MultiSelectLimit multiSelectLimit = new MultiSelectLimit(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, form_id);
                questionObjectList.put(questionUniqueId, multiSelectLimit);
            }
            break;
            case AppConfing.QUS_LOOPING: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dy_looping, linearLayout, false);
                LoopingSingleSelect loopingSingleSelect = new LoopingSingleSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, onLoopingButtonClickListener
                        , form_id);
                questionObjectList.put(questionUniqueId, loopingSingleSelect);
                loopingTypeQuestion.add(questionUniqueId);

            }
            break;
            case AppConfing.QUS_LOOPING_MILTISELECT: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dy_looping, linearLayout, false);
                LoopingMultiSelect loopingMultiSelect = new LoopingMultiSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, onLoopingButtonClickListener
                        , form_id);
                questionObjectList.put(questionUniqueId, loopingMultiSelect);
                loopingTypeQuestion.add(questionUniqueId);

            }
            break;
            case AppConfing.QUS_VIEW_IMAGE_QUESTION: {
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_dy_looping, linearLayout, false);
                ViewImageWithSingleSelect viewImageWithSingleSelect = new ViewImageWithSingleSelect(view, questionBean,
                        formStatus, dataListener, questionBeenList, answerBeanHelperList, onViewImageButtonClickListener, form_id);
                questionObjectList.put(questionUniqueId, viewImageWithSingleSelect);
            }
            break;

            case AppConfing.QUS_RECORD_AUDIO:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_audio, linearLayout, false);
                AudioRecordType audioRecordType = new AudioRecordType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, recordAudioResponseListener);
                audioRecordType.isPermission = checkMicPermission();
                questionObjectList.put(questionUniqueId, audioRecordType);
                break;

            case AppConfing.QUS_RADIO_BUTTONS:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_radio_view, linearLayout, false);
                RadioButtonType radioButtonType = new RadioButtonType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, radioButtonListener);
                questionObjectList.put(questionUniqueId, radioButtonType);
                break;

            case AppConfing.QUS_GET_LOCTION:
                view = getLayoutInflater().inflate(R.layout.dy_quest_custom_row_location_xml, linearLayout, false);
                LocationGetType locationGetType = new LocationGetType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, onGetLocationClickListener, dataListener);
                questionObjectList.put(questionUniqueId, locationGetType);
                break;

//            case AppConfing.QUS_CONSENT:
//                view = getLayoutInflater().inflate(R.layout.quest_custom_cosent, linearLayout, false);
//                ConsentType consentType = new ConsentType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, consentListener, dataListener);
//                questionObjectList.put(questionUniqueId, consentType);
//                break;
//
//            case AppConfing.QUS_GEO_TRACE:
//            case AppConfing.QUS_GEO_SHAPE:
//                view = getLayoutInflater().inflate(R.layout.qus_row_custom_geo_trace, linearLayout, false);
//                GeoTraceType geoTraceType = new GeoTraceType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, geoTraceListener, dataListener);
//                questionObjectList.put(questionUniqueId, geoTraceType);
//                break;

            case AppConfing.QUA_UNIT_CONVERSION:
                view = getLayoutInflater().inflate(R.layout.dy_qus_row_custom_unit, linearLayout, false);
                UnitConversionType unitConversionType = new UnitConversionType(view, formStatus, questionBean, questionBeenList, answerBeanHelperList, unitConversionListener, dataListener);
                questionObjectList.put(questionUniqueId, unitConversionType);
                break;


        }
        if (view != null) {
            linearLayout.addView(view);
            int position = linearLayout.indexOfChild(view);
            linearLayout.setTag(position);
            questionObjectList.get(questionUniqueId).setViewIndex(position);

            if (newTitles.get(questionUniqueId) != null) {
                replaceTitleOfQuestion(questionUniqueId, AppConfing.BLANK_TITLE, newTitles.get(questionUniqueId));
                answerBeanHelperList.get(questionUniqueId)
                        .setTitle(newTitles.get(questionUniqueId));
            }
            updateCount();
            createNotifyOnChangeList(questionBean);
        }
    }

    protected HashMap<String, HashSet<String>> notifyOnchangeMap = new HashMap<>();

    private void createNotifyOnChangeList(QuestionBean questionBean) {
        for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
            if (restrictionsBean.getType().equals(AppConfing.REST_DID_RELATION)
                    || restrictionsBean.getType().equals(AppConfing.REST_SHOULD_BE_LESS_THAN)
                    || restrictionsBean.getType().equals(AppConfing.REST_SHOULD_BE_LESS_THAN_EQUAL)
                    || restrictionsBean.getType().equals(AppConfing.REST_SHOULD_BE_GRATER_THAN)
                    || restrictionsBean.getType().equals(AppConfing.REST_SHOULD_BE_GRATER_THAN_EQUAL)
            ) {
                for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                    HashSet<String> ordersToNotify = notifyOnchangeMap.get(QuestionsUtils.getRestrictionOrderUniqueId(ordersBean));
                    if (ordersToNotify == null) {
                        ordersToNotify = new HashSet<>();
                    }
                    ordersToNotify.add(QuestionsUtils.getQuestionUniqueId(questionBean));
                    notifyOnchangeMap.put(QuestionsUtils.getRestrictionOrderUniqueId(ordersBean), ordersToNotify);
                }
            }
        }
    }


    //creating new Answer Object
    protected void createOrModifyAnswerBeanObject(final QuestionBean questionBean, final boolean isVisibleInHideList) {
        String questionUid = QuestionsUtils.getQuestionUniqueId(questionBean);

        QuestionBeanFilled answerBeanHelper = answerBeanHelperList.get(questionUid);
        if (FormType == 0 || answerBeanHelper == null) {
            answerBeanHelper = new QuestionBeanFilled();
            answerBeanHelper.setUid(UUID.randomUUID().toString());
            answerBeanHelper.setFilled(false);
            answerBeanHelper.setValidAns(false);
            answerBeanHelper.setAnswer(getNewAnswerList());
        } else {
            if (answerBeanHelper.getAnswer() == null && answerBeanHelper.getAnswer().size() == 0) {
                answerBeanHelper.setFilled(false);
                answerBeanHelper.setValidAns(false);
                answerBeanHelper.setAnswer(getNewAnswerList());
            }
        }
        answerBeanHelper.setInput_type(questionBean.getInput_type());
        answerBeanHelper.setOrder(questionUid);
        answerBeanHelper.setLabel(questionBean.getLabel());
        answerBeanHelper.setTitle(questionBean.getTitle());
        answerBeanHelper.setViewSequence(questionBean.getViewSequence());
        boolean isActive = (isVisibleInHideList || (questionBean.getParent().size() == 0)
                && !questionBean.getInput_type().equals(AppConfing.QUS_LABEL));

        List<ValidationBean> valiList = questionBean.getValidation();
        if (valiList.size() > 0) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(AppConfing.VAL_REQUIRED)) {
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
    }

    private void addDataToSaveList(final String text, final QuestionBean questionBean) {
        final String questionUid = QuestionsUtils.getQuestionUniqueId(questionBean);
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
                if (validationBean.get_id().equals(AppConfing.VAL_REGEX)) {
                    isRegex = true;
                } else if (validationBean.get_id().equals(AppConfing.VAL_ALERT_REGEX)) {
                    isAlertRegex = true;
                    alertRegexList.add(validationBean.getError_msg());
                    alertMsg = "" + this.getString(R.string.are_you_sure);
                } else if (validationBean.get_id().equals(AppConfing.VAL_ALERT_MSG)) {
                    if (validationBean.getError_msg() != null && !validationBean.getError_msg().equals("")) {
                        alertMsg = validationBean.getError_msg();
                    } else {
                        alertMsg = "" + this.getString(R.string.are_you_sure);
                    }
                } else if (validationBean.get_id().equals(AppConfing.VAL_ALERT_IF_BETWEEN)) {
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

                if (questionBean.getInput_type().equals(AppConfing.QUS_NUMBER) &&
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

            if (questionBean.getInput_type().equals(AppConfing.QUS_ADDRESS) || questionBean.getInput_type().equals(AppConfing.QUS_TEXT)) {
                saveDataToAnsList(questionBeanFilled, "", "", text, "");
            } else {
                saveDataToAnsList(questionBeanFilled, text, "", "", "");

            }


            validateChildVisibility(questionBean, text, baseType);


            updateCount();


        } else {
            BaseActivity.logDatabase(AppConfing.END_POINT, String.format(Locale.ENGLISH,
                    "Question filled with null. Question ID : %s Line no. 552",
                    questionBean.get_id()), AppConfing.UNEXPECTED_ERROR,
                    "BaseFormActivity");
        }


    }

    private void validateAnsForAdditionalInfo(String text, QuestionBean questionBean, BaseType baseType) {
        for (ValidationBean validationBean : questionBean.getValidation()) {
            if (validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_GPS)
                    || validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_IMAGE)
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

                QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
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
            if (validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_GPS)
                    || validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_IMAGE)
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

                QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
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
            final String questionUniqueId = QuestionsUtils.getQuestionUniqueId(questionBean);
            BaseType baseType = questionObjectList.get(questionUniqueId);
            if (text != null && baseType != null) {
                View linear = linearLayout.getChildAt(baseType.getViewIndex());
                boolean isAlertRegex = false;
                boolean isBetweemLimit = false;
                List<String> alertRegexList = new ArrayList<>();
                boolean isAlertCorrect = true;

                for (ValidationBean validationBean : questionBean.getValidation()) {
                    if (validationBean.get_id().equals(AppConfing.VAL_ALERT_REGEX)) {
                        isAlertRegex = true;
                        alertRegexList.add(validationBean.getError_msg());
                        alertMsg = "" + this.getString(R.string.are_you_sure);
                    } else if (validationBean.get_id().equals(AppConfing.VAL_ALERT_MSG)) {
                        if (validationBean.getError_msg() != null && !validationBean.getError_msg().equals("")) {
                            alertMsg = validationBean.getError_msg();
                        } else {
                            alertMsg = "" + this.getString(R.string.are_you_sure);
                        }
                    } else if (validationBean.get_id().equals(AppConfing.VAL_ALERT_IF_BETWEEN)) {
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
            String questionUniqueId = QuestionsUtils.getQuestionUniqueId(questionBean);
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

        QuestionBeanFilled childAnswerBean = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(childQuestion));
        BaseType questionObject = questionObjectList.get(QuestionsUtils.getQuestionUniqueId(childQuestion));
        List<Answers> childAnswers = childAnswerBean.getAnswer();
        boolean currentlyValid = childAnswerBean.isFilled();
        boolean currentlyValidAns = childAnswerBean.isValidAns();
        boolean isValid = true;
        if (currentlyValid && currentlyValidAns) {
            if (QuestionsUtils.isSelectType(childQuestion.getInput_type())) {
                List<AnswerOptionsBean> availableOptions = QuestionsUtils.getAnsOptionFromQuestionAfterFilter(childQuestion, questionBeenList, answerBeanHelperList, dataListener.getUserLanguage(), form_id);
                isValid = QuestionsUtils.validateAnswerListWithAnswerOptions(childAnswers, availableOptions);
                if (!isValid) {
                    questionObject.superResetQuestion();
                    childAnswerBean.setAnswer(getNewAnswerList());
                }
            } else if (childQuestion.getInput_type().equals(AppConfing.QUS_NUMBER)) {
                if (childAnswers.size() > 0) {
                    isValid = checkRestrictionOnNumberValues(childQuestion, childAnswers.get(0).getValue());
                }
            }
            if (!isValid) {
                questionObject.superChangeStatus(NOT_ANSWERED);
                setFilledAns(childAnswerBean, false);
                questionObject.superSetEditable(true, childQuestion.getInput_type());
            }
        } else {
            questionObject.superSetEditable(true, childQuestion.getInput_type());
        }

        return isValid;
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
        final String questionUid = QuestionsUtils.getQuestionUniqueId(questionBean);
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
                                if (validationBean.get_id().equals(AppConfing.VAL_ADD_DEFAULT_OPTION_WHEN_DY_AO_0)) {
                                    setFilledAns(questionBeanFilled, true, true);
                                    dynamicLoopingView.changebuttonStatus(false, 0);
                                    break;
                                }
                            }
                        }
                    }


                }

            } else {
                BaseActivity.logDatabase(AppConfing.END_POINT, String.format(Locale.ENGLISH,
                        "Wrong Child in linearLayout. Question ID :%s . Line no. 820",
                        questionBean.get_id()), AppConfing.UNEXPECTED_ERROR,
                        "BaseFormActivity");
            }

            if (questionBean.getRestrictions().size() > 0) {
                for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                    if (restrictionsBean.getType().equals(AppConfing.REST_VALUE_AS_TITLE_OF_CHILD)) {
                        applyTitleChangeRestriction(restrictionsBean, text);
                    } else if (restrictionsBean.getType().equals(REST_CLEAR_DID_CHILD)) {
                        for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                            QuestionBean questionBean1 = questionBeenList.get(QuestionsUtils.getRestrictionOrderUniqueId(ordersBean));
                            if (questionBean1 != null) {
                                clearAnswerAndView(questionBean1);
                            }
                        }
                    }
                }
            }
            for (ValidationBean validationBean : questionBean.getValidation()) {
                if (validationBean.get_id().equals(AppConfing.VAL_DYNAMIC_ANSWER_OPTION)) {
                    for (String transactionIds : value)
                        handleDynamicData(questionBean, transactionIds);
                }

            }

            List<String> visibleList = new ArrayList<>();
            for (ChildBean childBean : questionBean.getChild()) {
                if (childBean != null) {
                    String childvalueIdPattern = childBean.getValue();
                    boolean isMatchPatten = false;
                    for (String st : value)
                        if (matches(childvalueIdPattern, st)) {
                            isMatchPatten = true;
                            break;
                        }
                    boolean isMatchValue = value.contains(childvalueIdPattern);
                    setChildStatus(childBean, isMatchPatten || isMatchValue, visibleList);
                }
            }
            validateAnsForAdditionalInfoMultiseltct(value, questionBean, baseType);

            if (notifyOnchangeMap.containsKey(questionUid)) {
                notifyOrdersOnChange(notifyOnchangeMap.get(questionUid));
            }

            updateCount();


        } else {
            BaseActivity.logDatabase(AppConfing.END_POINT,
                    "Question is Invalid. Line no. 804",
                    AppConfing.UNEXPECTED_ERROR, "BaseFormActivity");
        }
    }

    //listen Single selector here
    @Override
    public void SingleSelector(final QuestionBean questionBean, final String value, final String id, boolean isSingle) {
        String questionUniqueId = QuestionsUtils.getQuestionUniqueId(questionBean);
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
                        if (questionBean.getInput_type().equals(AppConfing.QUS_LOOPING)) {
                            setFilledAns(questionBeanFilled, false, false);
                            dynamicLoopingView.changebuttonStatus(false, 2);
                        } else if (questionBean.getInput_type().equals(AppConfing.QUS_VIEW_IMAGE_QUESTION)) {
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
                        if (restrictionsBean.getType().equals(AppConfing.REST_VALUE_AS_TITLE_OF_CHILD)) {
                            applyTitleChangeRestriction(restrictionsBean, value);
                        }
                    }
                }
                String optionId = id;
                for (ValidationBean validationBean : questionBean.getValidation()) {
                    if (validationBean.get_id().equals(AppConfing.VAL_VILLAGE_WISE_LIMIT)) {
                        boolean isExceedLimit = checkVillageWiseLimit(questionBean, id);
                        if (isExceedLimit)
                            optionId = "-8898";
                    } else if (validationBean.get_id().equals(AppConfing.VAL_DYNAMIC_ANSWER_OPTION)) {
                        handleDynamicData(questionBean, id);
                    }

                }

                validateChildVisibility(questionBean, optionId, baseType);

                if (notifyOnchangeMap.containsKey(questionUniqueId)) {
                    notifyOrdersOnChange(notifyOnchangeMap.get(questionUniqueId));
                }
                updateCount();
            }
        } else {
            BaseActivity.logDatabase(AppConfing.END_POINT,
                    "Question is Invalid. Line no. 873",
                    AppConfing.UNEXPECTED_ERROR, "BaseFormActivity");
        }

    }

    void handleDynamicData(QuestionBean questionBean, String transactionId) {


     /*   List<QuestionBeanFilled> dynamicData = dataRepository.getLocalRepository().getDynamicData(String.valueOf(form_id), transactionId, questionBean.getOrder());
        for (QuestionBeanFilled questionBeanFilled : dynamicData) {
            String order = questionBeanFilled.getOrder();
            BaseType baseType = questionObjectList.get(order);
            QuestionBeanFilled originalAnswerBean = answerBeanHelperList.get(order);
            if (baseType != null && originalAnswerBean != null) {
                originalAnswerBean.setAnswer(questionBeanFilled.getAnswer());
                originalAnswerBean.setFilled(true);
                originalAnswerBean.setValidAns(true);
                baseType.superSetAnswer(originalAnswerBean);
                baseType.superChangeStatus(ANSWERED);
            }

        }
     */
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
                            .equalTo("upload_status", AppConfing.SUBMITTED)
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
        String questionUniqueId = QuestionsUtils.getQuestionUniqueId(questionBean);
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
                List<ValidationBean> valList = questionBean.getValidation();
                if (valList.size() > 0) {
                    isValidDate = checkValidationsOnDate(valList, date);
                }
                if (questionBean.getPattern() != null && !questionBean.getPattern().equals("")) {
                    isValidDatePattern = matches(questionBean.getPattern(), date);
                }

                if (isValidDate && isValidDatePattern) {
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
                    if (restrictionsBean.getType().equals(AppConfing.REST_VALUE_AS_TITLE_OF_CHILD)) {
                        applyTitleChangeRestriction(restrictionsBean, date);
                    }
                }
            }

            validateChildVisibility(questionBean, date, baseType);

            updateCount();

        } else {
            BaseActivity.logDatabase(AppConfing.END_POINT,
                    "Question is Invalid. Line no. 952",
                    AppConfing.UNEXPECTED_ERROR, "BaseFormActivity");
        }
    }

    //listen image pick
    @Override
    public void imagePath(final String path, final QuestionBean questionBean) {

        String questionUniqueId = QuestionsUtils.getQuestionUniqueId(questionBean);
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
                String childUniqueId = QuestionsUtils.getChildUniqueId(questionBean.getChild().get(0));
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
        QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
        setFilledAns(questionBeanFilled, true, true);
        saveDataToAnsList(questionBeanFilled, id, label, "", "");
        if (questionBean.getRestrictions().size() > 0) {
            for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                if (restrictionsBean.getType().equals(AppConfing.REST_VALUE_AS_TITLE_OF_CHILD)) {
                    applyTitleChangeRestriction(restrictionsBean, label);
                }
            }
        }


        BaseType baseType = questionObjectList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
        if (baseType != null) {
            validateChildVisibility(questionBean, id, baseType);
        }
        updateCount();

    }

    private void validateChildVisibility(QuestionBean questionBean, String id, BaseType baseType) {


        List<String> visibleList = new ArrayList<>();
        for (ChildBean childBean : questionBean.getChild()) {
            if (childBean != null) {

                String ChildvalueIdPattern = childBean.getValue();
                boolean isMatchPatten = matches(ChildvalueIdPattern, id);
                boolean isMatchValue = id.equals(ChildvalueIdPattern);
                if (id.equals("")) {
                    isMatchPatten = false;
                    isMatchValue = false;
                }
                setChildStatus(childBean, isMatchPatten || isMatchValue, visibleList);

            }
        }

        validateAnsForAdditionalInfo(id, questionBean, baseType);
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

    //changing child visibility and validation
    private void setChildValidation(String childUid, QuestionBean questionBean, int visibility, View view) {

        List<ValidationBean> valList = questionBean.getValidation();
        view.setVisibility(visibility);
        boolean isActive = visibility == View.VISIBLE;
        if (!isActive) {
            answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean)).setAnswer(getNewAnswerList());
            answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean)).setNestedAnswer(new ArrayList<>());
        }
        BaseType baseType = questionObjectList.get(childUid);
        baseType.superResetQuestion();
        if (isActive && !questionBean.getInput_type().equals(AppConfing.QUS_LABEL)) {
            if (valList.size() > 0) {
                for (ValidationBean validationBean : valList) {
                    if (validationBean.get_id().equals(AppConfing.VAL_REQUIRED)) {
                        addingInHelperArray(childUid, true, false, false);
                        break;
                    }
                }
            } else {
                addingInHelperArray(childUid, false, false, true);
            }
        } else {

            addingInHelperArray(childUid, false, false, false);
        }
        //hiding nested child here //only one
        if (questionBean.getChild().size() > 0) {
            for (ChildBean childBean : questionBean.getChild()) {
                String nestedChildUid = QuestionsUtils.getChildUniqueId(childBean);
                QuestionBean nestedChildQuestion = questionBeenList.get(nestedChildUid);
                BaseType baseType1 = questionObjectList.get(nestedChildUid);
                if (baseType1 != null) {
                    View nestedChildView = (linearLayout).getChildAt(baseType1.getViewIndex());
                    if (nestedChildView.getVisibility() == View.VISIBLE) {
                        setChildValidation(nestedChildUid, nestedChildQuestion, View.GONE, nestedChildView);
                    }
                }
            }
        }
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
        if (!QuestionsUtils.isValidFloat(text))
            return false;
        float currentValue = Float.parseFloat(text);

        for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
            if (restrictionsBean.getType().equals(REST_SHOULD_BE_GRATER_THAN)
                    || restrictionsBean.getType().equals(AppConfing.REST_SHOULD_BE_GRATER_THAN_EQUAL)
                    || restrictionsBean.getType().equals(REST_SHOULD_BE_LESS_THAN)
                    || restrictionsBean.getType().equals(REST_SHOULD_BE_LESS_THAN_EQUAL)) {

                ///  return false only in this portion otherwise always return true
                List<Float> parentValuesList = new ArrayList<>();
                for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                    try {
                        List<Answers> answers = QuestionsUtils.getAnswerListbyOder(QuestionsUtils.getRestrictionOrderUniqueId(ordersBean), null, answerBeanHelperList);

                        if (answers.size() > 0) {
                            String p_ans = answers.get(0).getValue();
                            if (QuestionsUtils.isValidFloat(p_ans)) {
                                parentValuesList.add(Float.parseFloat(p_ans));
                            } else {
                                return false;
                            }
                        }

                    } catch (Exception e) {
                        return false;
                    }

                }
                switch (restrictionsBean.getType()) {
                    case AppConfing.REST_SHOULD_BE_GRATER_THAN:
                        for (float parentValue : parentValuesList) {
                            if (currentValue <= parentValue) {
                                return false;
                            }
                        }
                        break;
                    case AppConfing.REST_SHOULD_BE_GRATER_THAN_EQUAL:
                        for (float parentValue : parentValuesList) {
                            if (currentValue < parentValue) {
                                return false;
                            }
                        }
                        break;
                    case AppConfing.REST_SHOULD_BE_LESS_THAN:
                        for (float parentValue : parentValuesList) {
                            if (currentValue >= parentValue) {
                                return false;
                            }
                        }
                        break;
                    case AppConfing.REST_SHOULD_BE_LESS_THAN_EQUAL:
                        for (float parentValue : parentValuesList) {
                            if (currentValue > parentValue) {
                                return false;
                            }
                        }
                        break;
                }

            }

        }
        return true;
    }

    private void callForExpressionsRestriction(QuestionBean questionBean, String text) {
        if (!QuestionsUtils.isValidFloat(text))
            return;
        for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
            if (restrictionsBean.getType().equals(AppConfing.REST_CALL_FOR_EXPRESSION)) {
                List<String> listOfNotifyOrders = new ArrayList<>();
                for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                    String restrictionOrderUniqueId = QuestionsUtils.getRestrictionOrderUniqueId(ordersBean);
                    if (restrictionOrderUniqueId != null) {
                        listOfNotifyOrders.add(restrictionOrderUniqueId);
                    }
                }
                notifyOrdersForCalculation(listOfNotifyOrders, text, QuestionsUtils.getQuestionUniqueId(questionBean));
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
                if (restrictionsBean.getType().equals(AppConfing.REST_CALL_FOR_ADD) ||
                        restrictionsBean.getType().equals(AppConfing.REST_CALL_FOR_SUB) ||
                        restrictionsBean.getType().equals(AppConfing.REST_CALL_FOR_MUL) ||
                        restrictionsBean.getType().equals(AppConfing.REST_CALL_FOR_DIVD)) {
                    solveExpressionAddSubMulDiv(QuestionsUtils.getQuestionUniqueId(questionBean), restrictionsBean, currentValue, currentQusUid);
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
        if (QuestionsUtils.isValidFloat(currentValue)) {
            currentValueFloat = Float.parseFloat(currentValue);
        } else {
            currentValueFloat = 0;
        }
        for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
            if (QuestionsUtils.getRestrictionOrderUniqueId(ordersBean) != null && !QuestionsUtils.getRestrictionOrderUniqueId(ordersBean).equals(currentQusUid)) {
                try {
                    List<Answers> answers = QuestionsUtils.getAnswerListbyOder(QuestionsUtils.getRestrictionOrderUniqueId(ordersBean), null, answerBeanHelperList);
                    if (answers.size() > 0) {
                        String p_ans = answers.get(0).getValue();
                        if (QuestionsUtils.isValidFloat(p_ans)) {
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
            case AppConfing.REST_CALL_FOR_ADD:
                for (float pValue : valuesForEquation) {
                    finalValue += pValue;
                }
                break;
            case AppConfing.REST_CALL_FOR_SUB:
                finalValue = valuesForEquation.get(0);
                for (int i = 1; i < valuesForEquation.size(); i++) {
                    finalValue -= valuesForEquation.get(i);
                }
                break;
            case AppConfing.REST_CALL_FOR_MUL:
                finalValue = 1;
                for (float pValue : valuesForEquation) {
                    finalValue += pValue;
                }
                break;
            case AppConfing.REST_CALL_FOR_DIVD:
                break;
        }

        BaseType baseType = questionObjectList.get(questionUid);
        if (baseType != null) {
            baseType.superSetAnswer("");
            baseType.superSetAnswer("" + Math.round(finalValue));

        }

    }

    private boolean checkValidationsOnDate(List<ValidationBean> validationBeanList, String
            selectedDate) {
        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            date = formatter.parse(selectedDate);
            Calendar selDateC = Calendar.getInstance();
            selDateC.setTime(date);

            Calendar temptoday = Calendar.getInstance();
            Date today = temptoday.getTime();
            String s_today = formatter.format(today);
            today = formatter.parse(s_today);
            Calendar todayC = Calendar.getInstance();
            todayC.setTime(today);

            for (ValidationBean validationBean1 : validationBeanList) {
                switch (validationBean1.get_id()) {
                    case AppConfing.VAL_FUTURE_DATE:
                        if (!selDateC.after(todayC)) {
                            return false;
                        }
                        break;
                    case AppConfing.VAL_FUTURE_AND_TODAY:
                        if (selDateC.before(todayC)) {
                            return false;
                        }
                        break;
                    case AppConfing.VAL_PAST_DATE:
                        if (!selDateC.before(todayC)) {
                            return false;
                        }

                        break;
                    case AppConfing.VAL_PAST_AND_TODAY:
                        if (selDateC.after(todayC))
                            return false;
                        break;
                }

            }

        } catch (ParseException e) {
            Log.e("error", e.getMessage());
            ;
        }


        return true;
    }

    private void setChildStatus(ChildBean childBean, boolean isMatch, List<String> visibleList) {


        String childUniqueId = QuestionsUtils.getChildUniqueId(childBean);
        BaseType baseType = questionObjectList.get(childUniqueId);

        if (baseType != null) {

            View view = (this.linearLayout).getChildAt(baseType.getViewIndex());
            QuestionBean childQuestionBean = questionBeenList.get(childUniqueId);
            if (view != null && childQuestionBean != null) {
                //show child and add validation
                if (childQuestionBean.getParent().size() > 1) {
                    isMatch = QuestionsUtils.validateVisibilityWithMultiParent(childQuestionBean, isMatch, answerBeanHelperList);
                }
                for (RestrictionsBean restrictionsBean : childQuestionBean.getRestrictions()) {
                    if (restrictionsBean.getType().equals(AppConfing.REST_MULTI_ANS_VISIBILITY_IF_NO_ONE_SELECTED)) {
                        showProgress(this, getString(R.string.loading));
                        isMatch = QuestionsUtils.validateMultiAnsRestriction(restrictionsBean, answerBeanHelperList, questionBeenList);
                        hideProgess();
                        break;
                    }
                }


                if (isMatch) {
                    visibleList.add(childUniqueId);
                    setChildValidation(childUniqueId, childQuestionBean, View.VISIBLE, view);
                    childQuestionBean.setEditable(true);
                    if (formStatus == EDITABLE_DARFT || formStatus == SYNCED_BUT_EDITABLE) {
                        baseType.superSetEditable(true, childQuestionBean.getInput_type());
                        setFilledAns(answerBeanHelperList.get(childUniqueId), false, false);
                    }

                    //   childanswerBeanHelperList.get(childPos).setRequired(true);
                } else {
                    if (!visibleList.contains(childUniqueId)) {
                        setChildValidation(childUniqueId, childQuestionBean, View.GONE, view);
                        setFilledAns(answerBeanHelperList.get(childUniqueId), false, false);

                    }
                }
            }


        }

    }


    private void applyTitleChangeRestriction(RestrictionsBean restrictionsBean, String text) {
        switch (restrictionsBean.getType()) {
            case AppConfing.REST_VALUE_AS_TITLE_OF_CHILD:
                for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                    replaceTitleOfQuestion(QuestionsUtils.getRestrictionOrderUniqueId(ordersBean), AppConfing.BLANK_TITLE, text);
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
            QuestionBeanFilled questionBeanFilled = getQuestionBeanFilledNewData(QuestionsUtils.getQuestionUniqueId(questionBean));
            nestedAnswer.add(questionBeanFilled);

        }
        nested.setAnswerNestedData(nestedAnswer);

        return nested;
    }


    public Nested modifiedNestedUpdated(Nested nested, String
            forParentValue) {
        nested.setForParentValue(forParentValue);
        // to update to new order for the child
        for (QuestionBeanFilled questionBeanFilled : nested.getAnswerNestedData()) {
            String childKey = QuestionsUtils.getAnswerUniqueId(questionBeanFilled);
            questionBeanFilled.setOrder(childKey);
        }

        return nested;
    }

    private QuestionBeanFilled getQuestionBeanFilledNewData(String childQuestionKeyOrder) {
        QuestionBeanFilled questionBeanFilled = new QuestionBeanFilled();
        questionBeanFilled.setUid(UUID.randomUUID().toString());
        questionBeanFilled.setAnswer(getNewAnswerList());
        questionBeanFilled.setInput_type("");
        questionBeanFilled.setLabel("");
        questionBeanFilled.setOptional(false);
        questionBeanFilled.setOrder(childQuestionKeyOrder);
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
     /*   if (requestCode == NESTEDCHILD_CODE && data != null) {
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

                    if (!findNested.isEmpty()) {
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
                    }
                }

            }
            refreshLoopingFiltersQuestions();
        }*/
    }

    @Override
    protected void onDestroy() {
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
        String questionUniqueId = QuestionsUtils.getQuestionUniqueId(questionBean);
        BaseType baseType = questionObjectList.get(questionUniqueId);
        if (answerBeanHelperList.containsKey(questionUniqueId)) {
            setFilledAns(answerBeanHelperList.get(questionUniqueId), false, false);
            answerBeanHelperList.get(questionUniqueId).setAnswer(getNewAnswerList());
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

                BaseType baseType = questionObjectList.get(QuestionsUtils.getAnswerUniqueId(questionBeanFilled));
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
            String childUniqueID = QuestionsUtils.getChildUniqueId(childBean);
            QuestionBean childQuestionBean = questionBeenList.get(childUniqueID);

            if (childQuestionBean != null) {
                for (RestrictionsBean restrictionsBean : childQuestionBean.getRestrictions()) {
                    if (restrictionsBean.getType().equals(AppConfing.REST_GET_ANS_OPTION_LOOPING)) {
                        List<AnswerOptionsBean> currentAvailableAnswerOption = childQuestionBean.getAnswer_options();
                        currentAvailableAnswerOption = QuestionsUtils.getAnswerListFormRestriction(currentAvailableAnswerOption, restrictionsBean, childQuestionBean, answerBeanHelperList);
                        List<Answers> childAnswerList = QuestionsUtils.getAnswerListbyOder(childUniqueID, null, answerBeanHelperList);
                        boolean isValidAnswer = QuestionsUtils.validateAnswerListWithAnswerOptions(childAnswerList, currentAvailableAnswerOption);
                        if (!isValidAnswer) {
                            QuestionBeanFilled questionBeanFilled = answerBeanHelperList.get(childUniqueID);
                            if (questionBeanFilled != null) {
                                setFilledAns(questionBeanFilled, false, false);
                                questionBeanFilled.setAnswer(getNewAnswerList());
                            }
                            String childAnsId = QuestionsUtils.getQuestionUniqueId(childQuestionBean);
                            BaseType baseType = questionObjectList.get(childAnsId);
                            if (baseType != null) {
                                //updating value in dependent field
                                baseType.superResetQuestion();
                                baseType.superChangeStatus(NOT_ANSWERED);
                            }
                        }
                        break;
                    } else if (restrictionsBean.getType().equals(AppConfing.REST_GET_ANS_SUM_LOOPING)) {
                        List<Answers> answers = new ArrayList<>();
                        float sum = 0;
                        for (OrdersBean ordersBean : restrictionsBean.getOrders()) {
                            if (loopingTypeQuestion.getChild().size() > 0)
                                answers.addAll(getNestedAnswerListFromNestedType(QuestionsUtils.getQuestionUniqueId(loopingTypeQuestion),
                                        QuestionsUtils.getRestrictionOrderUniqueId(ordersBean), ordersBean.getValue(), answerBeanHelperList));
                        }
                        if (answers.size() > 0) {

                            for (Answers calc : answers) {
                                if (!calc.getValue().equals(""))
                                    sum = sum + Float.parseFloat(calc.getValue());
                            }
                        }

                        if (!answerBeanHelperList.get(childUniqueID).getAnswer().get(0).getValue().equals("")) {

                            if (Integer.parseInt(answerBeanHelperList.get(childUniqueID).getAnswer().get(0).getValue()) != Math.round(sum)) {
                                String childAnsId = QuestionsUtils.getQuestionUniqueId(childQuestionBean);
                                BaseType baseType = questionObjectList.get(childAnsId);
                                if (baseType != null) {
                                    //updating value in dependent field
                                    baseType.superSetAnswer(String.valueOf(Math.round(sum)));
                                }
                            }
                        } else {
                            String childAnsId = QuestionsUtils.getQuestionUniqueId(childQuestionBean);
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
            if (validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_GPS) && (childAnswerList.get(0).getValue().matches(validationBean.getValue()) || validationBean.getValue().equals(""))) {
                intent.putExtra(Constant.IS_LOCATION_REQUIRED, true);
            } else if (validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_IMAGE) && (childAnswerList.get(0).getValue().matches(validationBean.getValue()) || validationBean.getValue().equals(""))) {
                intent.putExtra(Constant.IS_IMAGE_REQUIRED, true);
            }
        }


        intent.putExtra(Constant.QUESTION_ORDER, QuestionsUtils.getQuestionUniqueId(questionBean));
        startActivityForResult(intent, ADDITIONAL_FORM_CODE);
        overridePendingTransition(R.anim.from_right, R.anim.to_right);
*/
    }

}
