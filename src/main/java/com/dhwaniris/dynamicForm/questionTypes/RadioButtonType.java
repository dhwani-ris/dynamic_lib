package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.customViews.RadioRowViewXML;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.LinkedHashMap;
import java.util.List;

import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SYNCED_BUT_EDITABLE;


public class RadioButtonType extends BaseType {

    private QuestionHelperCallback.RadioButtonListener radioButtonListener;
    private RadioRowViewXML dynamicRadioViewXML;

    public RadioButtonType(View view, int formStatus, QuestionBean questionBean,
                           LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled>
                                   answerBeanHelperList, QuestionHelperCallback.RadioButtonListener listener) {
        dynamicRadioViewXML = new RadioRowViewXML(view);
        this.answerBeanHelperList = answerBeanHelperList;
        this.questionBeenList = questionBeenList;
        this.questionBean = questionBean;
        this.formStatus = formStatus;
        radioButtonListener = listener;
        setBasicFunctionality(questionBean, formStatus);
        boolean iniitList = true;
        if (formStatus == SUBMITTED || formStatus == EDITABLE_SUBMITTED) {
            iniitList = false;
        }
        if (iniitList)
            initListener();
    }

    private void initListener() {

        dynamicRadioViewXML.setRadioButtonsCheckChangeListener(new RadioButtonInnerListener() {
            @Override
            public void onRadioButtonCheck(String id, String label) {
                radioButtonListener.onRadioButtonsChecked(questionBean, id, label);
                dynamicRadioViewXML.setAnswerStatus(RadioRowViewXML.ANSWERED);
            }
        });
    }

    @Override
    public void superChangeStatus(int status) {
        dynamicRadioViewXML.setAnswerStatus(status);
    }

    @Override
    public void superResetQuestion() {
        dynamicRadioViewXML.reset();
    }

    @Override
    public void superChangeTitle(String title) {
        dynamicRadioViewXML.setTitle(title);
    }

    @Override
    public void superSetErrorMsg(String errorMsg) {
        dynamicRadioViewXML.setErrorMsg(errorMsg);
    }

    public interface RadioButtonInnerListener {
        void onRadioButtonCheck(String id, String label);
    }

    @Override
    public void superSetAnswer(String answer) {
        dynamicRadioViewXML.checkButtonByIdOrName(answer,null);
    }

    void setBasicFunctionality(QuestionBean questionBean, int formStatus) {
        dynamicRadioViewXML.setTitle(questionBean.getTitle());
        dynamicRadioViewXML.setOrder(questionBean.getLabel());
        dynamicRadioViewXML.setRadioButtonsTextItems(questionBean.getAnswer_options());
        if (questionBean.getInformation() != null && !questionBean.getInformation().trim().equals("")) {

            dynamicRadioViewXML.setInformation(questionBean.getInformation());
        }
        if (formStatus == NEW_FORM) {
            if (questionBean.getParent().size() > 0) {
                dynamicRadioViewXML.setVisibility(View.GONE);
            }
            radioButtonListener.onCreateNewAnswerObject(questionBean, dynamicRadioViewXML.getVisivility() == View.VISIBLE);

        }
        List<ValidationBean> valiList = questionBean.getValidation();
        if (valiList.size() > 0) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(AppConfing.VAL_REQUIRED)) {
                    dynamicRadioViewXML.isRequired(true);
                } else if (validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_GPS)
                        || validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_IMAGE)
                      ) {
                    dynamicRadioViewXML.setAdditionalInfoClick(v -> {
                        radioButtonListener.clickOnAdditionalButton(questionBean);
                    });

                }
            }
        }
        //check if draft and is field filled or not
        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT || formStatus == EDITABLE_SUBMITTED) {


            if (checkValueForVisibility(questionBean)) {
                dynamicRadioViewXML.setVisibility(View.VISIBLE);
            } else {
                dynamicRadioViewXML.setVisibility(View.GONE);
            }


            answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean));

            setData(answerBeanFilled);


            if (formStatus == SUBMITTED || formStatus == AppConfing.EDITABLE_SUBMITTED) {
                dynamicRadioViewXML.setFocusable(false);
                dynamicRadioViewXML.setAnswerStatus(RadioRowViewXML.ANSWERED);

            } else if (formStatus == SYNCED_BUT_EDITABLE || formStatus == EDITABLE_DARFT) {
                if (!questionBean.isEditable()) {
                    dynamicRadioViewXML.setFocusable(false);
                }
                dynamicRadioViewXML.setAnswerStatus(RadioRowViewXML.ANSWERED);
            }
        }

    }

    void setData(QuestionBeanFilled answerBeanFilled){
        if (answerBeanFilled != null) {
            if (answerBeanFilled.getAnswer().size() > 0) {
                String id = answerBeanFilled.getAnswer().get(0).getValue();
                if (!id.equals("")) {
                    dynamicRadioViewXML.checkButtonByIdOrName(id, null);
                    dynamicRadioViewXML.setAnswerStatus(RadioRowViewXML.ANSWERED);
                }
            }

        }
    }


    public void setHint(String string) {
        dynamicRadioViewXML.setTitle(string);
    }

    @Override
    public void superSetEditable(boolean isEditable, String questionType) {
        dynamicRadioViewXML.setFocusable(isEditable);
    }

    @Override
    public void setAdditionalVisibility(int visibility) {
        dynamicRadioViewXML.setAdditionalInfoVisibility(visibility);
    }

    @Override
    public void setAdditionalButtonStatus(int status) {
        dynamicRadioViewXML.setAdditionalStatus(status);
    }

    @Override
    public void superSetAnswer(QuestionBeanFilled questionBeanFilled) {
        setData(questionBeanFilled);
    }
}


