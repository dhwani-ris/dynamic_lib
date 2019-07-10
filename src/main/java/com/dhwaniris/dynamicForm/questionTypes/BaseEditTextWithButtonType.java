package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.customViews.EditTextWIthButtonView;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.LinkedHashMap;
import java.util.List;



import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;


public class BaseEditTextWithButtonType extends BaseType {
    public QuestionHelperCallback.DataListener dataListener;
    EditTextWIthButtonView dynamicLoopingView;

    BaseEditTextWithButtonType(QuestionHelperCallback.DataListener dataListener,
                               View view, LinkedHashMap<String, QuestionBean> questionBeenList,
                               LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList,
                               final QuestionHelperCallback.QuestionButtonClickListener buttonClickListener,
                               QuestionBean questionBean,
                               int formStatus, int form_id) {
        this.formId = form_id;
        this.dataListener = dataListener;
        createDynamicView(view);
        this.answerBeanHelperList = answerBeanHelperList;
        this.questionBeenList = questionBeenList;
        setBasicFunctionality(questionBean, formStatus);
        dynamicLoopingView.setFocusable(false);
        dynamicLoopingView.setButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickListener.onclickOnQuestionButton(BaseEditTextWithButtonType.this.questionBean);
            }
        });
        this.questionBean = questionBean;
        if (formStatus == NEW_FORM) {
            dynamicLoopingView.changebuttonStatus(false, 0);
            if (questionBean.getParent().size() > 0) {
                dynamicLoopingView.setVisibility(View.GONE);
            }
            createNewAnswerObjRequest(questionBean, dynamicLoopingView.getVisibility() == View.VISIBLE);

        } else {
            //draft action
            answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
            if (answerBeanFilled != null && answerBeanFilled.isRequired()) {
                dynamicLoopingView.isRequired(true);
            }
        }
        this.formStatus = formStatus;


    }

    void createDynamicView(View view) {
        dynamicLoopingView = view.findViewById(R.id.custom_looping);

    }

    void setBasicFunctionality(QuestionBean questionBean, int formStatus) {
        dynamicLoopingView.setTag(questionBean.get_id());
        dynamicLoopingView.setEditTextHint(questionBean.getHint());
        dynamicLoopingView.setHint(questionBean.getTitle());
        if (questionBean.getInformation() != null && !questionBean.getInformation().trim().equals("")) {

            dynamicLoopingView.setInformation(questionBean.getInformation());
        }
        /////order to label
        dynamicLoopingView.setOrder(questionBean.getLabel());
        if (formStatus == SUBMITTED) {
            dynamicLoopingView.setFocusable(false);
            dynamicLoopingView.setClickable(false);
            dynamicLoopingView.setAnswerStatus(EditTextWIthButtonView.ANSWERED);
        } else if (formStatus == AppConfing.SYNCED_BUT_EDITABLE || formStatus == AppConfing.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            if (!questionBean.isEditable()) {
                dynamicLoopingView.setFocusable(false);
                dynamicLoopingView.setClickable(false);
                dynamicLoopingView.setFocusableForEditText(1);
            }
            dynamicLoopingView.setAnswerStatus(EditTextWIthButtonView.ANSWERED);

        }

        List<ValidationBean> valiList = questionBean.getValidation();
        if (valiList.size() > 0) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(AppConfing.VAL_REQUIRED)) {
                    dynamicLoopingView.isRequired(true);
                } else if (validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_GPS)
                        || validationBean.get_id().equals(AppConfing.VAL_ADD_INFO_IMAGE)) {
                    dynamicLoopingView.setAdditionalInfoClick(v -> {
                        dataListener.clickOnAdditionalButton(questionBean);
                    });

                }
            }
        }
        //check if draft and is field filled or not
        if (formStatus != NEW_FORM) {

            if (checkValueForVisibility(questionBean)) {
                dynamicLoopingView.setVisibility(View.VISIBLE);
            } else {
                dynamicLoopingView.setVisibility(View.GONE);
            }

        }

    }


    void hideKeyboard() {
        if (dataListener != null) {
            dataListener.onhideKeyboard();
        }
    }


    void createSelector(List<AnswerOptionsBean> answerOptionsBeen,
                        QuestionBean questionBean, String header) {
        if (dataListener != null) {
            dataListener.createSingleSelector(answerOptionsBeen, questionBean, header);
        }
    }

    void createMultiSelector(List<AnswerOptionsBean> ansOpt, List<String> selectedList,
                             QuestionBean questionBean,
                             String header, int checkLimit) {
        if (dataListener != null) {
            dataListener.createMultiSelector(ansOpt, selectedList, questionBean, header, checkLimit);
        }
    }


    void createNewAnswerObjRequest(QuestionBean questionBean, boolean isVisibleInHideList) {
        if (dataListener != null) {
            dataListener.createNewAnswerObject(questionBean, isVisibleInHideList);
        }
    }

    public void changeTitleRequest(RestrictionsBean restrictionsBean, String text) {
        if (dataListener != null) {
            dataListener.changeTitle(restrictionsBean, text);
        }
    }

    @Override
    public void superSetEditable(boolean isEditable, String questionType) {
        dynamicLoopingView.setFocusableForEditText(isEditable);
    }

    @Override
    public void superSetErrorMsg(String errorMsg) {
        dynamicLoopingView.setErrorMsg(errorMsg);
    }

    @Override
    public void setAdditionalVisibility(int visibility) {
        dynamicLoopingView.setAdditionalInfoVisibility(visibility);
    }

    @Override
    public void setAdditionalButtonStatus(int status) {
        dynamicLoopingView.setAdditionalStatus(status);
    }

    @Override
    public void superSetAnswer(String answer) {
        dynamicLoopingView.setText(answer);
    }

    @Override
    public void superChangeStatus(int status) {
        dynamicLoopingView.setAnswerStatus(status);
    }

    @Override
    public void superResetQuestion() {
        dynamicLoopingView.changebuttonStatus(false, 0);
    }

    @Override
    public void superChangeTitle(String title) {
        dynamicLoopingView.setHint(title);
    }

    @Override
    public void superSetAnswer(QuestionBeanFilled questionBeanFilled) {
        String viewableStringFormAns = QuestionsUtils.getViewableStringFormAns(questionBeanFilled);
        dynamicLoopingView.setText(viewableStringFormAns);
    }
}
