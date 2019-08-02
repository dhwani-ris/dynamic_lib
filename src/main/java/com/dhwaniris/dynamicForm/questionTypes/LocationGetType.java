package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.customViews.DynamicLocationViewXML;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.LinkedHashMap;
import java.util.List;



import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;

public class LocationGetType extends BaseType {
    private QuestionHelperCallback.QuestionButtonClickListener locationGetBtn;
    private DynamicLocationViewXML dynamicLocationViewXML;
    QuestionHelperCallback.DataListener dataListener;

    public LocationGetType(View view, int formStatus, QuestionBean questionBean,
                           LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled>
                                   answerBeanHelperList, QuestionHelperCallback.QuestionButtonClickListener listener, QuestionHelperCallback.DataListener dataListener) {


        dynamicLocationViewXML = new DynamicLocationViewXML(view);
        this.answerBeanHelperList = answerBeanHelperList;
        this.questionBeenList = questionBeenList;
        this.questionBean = questionBean;
        this.formStatus = formStatus;
        this.locationGetBtn = listener;
        this.dataListener = dataListener;
        setBasicFunctionality(questionBean, formStatus);
        boolean iniitList = true;
        if (formStatus == SUBMITTED || formStatus == EDITABLE_SUBMITTED) {
            iniitList = false;
        }

        if (iniitList) {
            initListener();
        }
        answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));

        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == LibDynamicAppConfig.SYNCED_BUT_EDITABLE || formStatus == LibDynamicAppConfig.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            setData(answerBeanFilled);
        }

    }

    private void setData(QuestionBeanFilled questionBeanFilled) {
        if (questionBeanFilled != null) {
            final List<Answers> ans = questionBeanFilled.getAnswer();
            String[]  loca = new String[3];
            int i = 0;

            for (Answers location : ans) {

                loca[i] = location.getValue();
                i++;
            }

            dynamicLocationViewXML.setLocation(loca[0], loca[1], loca[2]);
            if (loca[0] != null && !loca[0].equals(""))
                dynamicLocationViewXML.setAnswerStatus(1);
        }
    }

    public void buttonState(boolean isHide) {

        dynamicLocationViewXML.stopBtn(isHide);
    }


    private void setBasicFunctionality(QuestionBean questionBean, int formStatus) {
        dynamicLocationViewXML.setTitle(questionBean.getTitle());
        dynamicLocationViewXML.setOrder(questionBean.getLabel());
        if (questionBean.getInformation() != null && !questionBean.getInformation().trim().equals("")) {

            dynamicLocationViewXML.setInformation(questionBean.getInformation());
        }
        if (formStatus == NEW_FORM) {
            if (questionBean.getParent().size() > 0) {
                dynamicLocationViewXML.setVisibility(View.GONE);
            }
            if (dataListener != null)
                dataListener.createNewAnswerObject(questionBean, dynamicLocationViewXML.getVisivility() == View.VISIBLE);
        }else {
            if (checkValueForVisibility(questionBean)) {
                dynamicLocationViewXML.setVisibility(View.VISIBLE);
            } else {
                dynamicLocationViewXML.setVisibility(View.GONE);
            }
        }

        List<ValidationBean> valiList = questionBean.getValidation();
        if (valiList.size() > 0) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_REQUIRED)) {
                    dynamicLocationViewXML.isRequired(true);
                } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_GPS)
                        || validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_IMAGE)
                        ) {
                    dynamicLocationViewXML.setAdditionalInfoClick(v -> {
                        dataListener.clickOnAdditionalButton(questionBean);
                    });

                }
            }
        }


    }

    private void initListener() {
        dynamicLocationViewXML.setOnCustomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationGetBtn.onclickOnQuestionButton(questionBean);
                // dataListener.createNewAnswerObject(questionBean);

            }
        });
    }

    public void setLocationOnView(String accuracy, String latitude, String longitude) {
        dynamicLocationViewXML.setLocation(accuracy, latitude, longitude);
    }


    @Override
    public void superChangeStatus(int status) {
        dynamicLocationViewXML.setAnswerStatus(status);
    }

    @Override
    public void superResetQuestion() {
        dynamicLocationViewXML.reset();
    }

    @Override
    public void superChangeTitle(String title) {
        dynamicLocationViewXML.setTitle(title);
    }

    @Override
    public void superSetErrorMsg(String errorMsg) {
        dynamicLocationViewXML.setErrorMsg(errorMsg);
    }

    @Override
    public void setAdditionalVisibility(int visibility) {
        dynamicLocationViewXML.setAdditionalInfoVisibility(visibility);
    }

    @Override
    public void setAdditionalButtonStatus(int status) {
        dynamicLocationViewXML.setAdditionalStatus(status);
    }

    @Override
    public void superSetAnswer(QuestionBeanFilled questionBeanFilled) {
        setData(questionBeanFilled);
    }
}
