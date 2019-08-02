package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.List;

import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;


public class BaseQuestionType extends BaseType {


    public QuestionHelperCallback.DataListener dataListener;
    EditTextRowView dynamicEditTextRow;


    EditTextRowView createDynamicView(View view) {
        dynamicEditTextRow = view.findViewById(R.id.custom);
        return dynamicEditTextRow;
    }

    void setBasicFunctionality(QuestionBean questionBean, int formStatus) {
        dynamicEditTextRow.setTag(questionBean.get_id());
        dynamicEditTextRow.setEditTextHint(questionBean.getHint());
        dynamicEditTextRow.setHint(questionBean.getTitle());
        /////order to label
        dynamicEditTextRow.setOrder(questionBean.getLabel());
        if (questionBean.getInformation() != null && !questionBean.getInformation().trim().equals("")) {

            dynamicEditTextRow.setInformation(questionBean.getInformation());
        }

        if (formStatus == SUBMITTED) {
            dynamicEditTextRow.setFocusable(false);
            dynamicEditTextRow.setClickable(false);
            dynamicEditTextRow.setAnswerStatus(EditTextRowView.ANSWERED);
        } else if (formStatus == LibDynamicAppConfig.SYNCED_BUT_EDITABLE || formStatus == LibDynamicAppConfig.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            if (!questionBean.isEditable()) {
                dynamicEditTextRow.setFocusable(false);
                dynamicEditTextRow.setFocusableForEditText(1);
                dynamicEditTextRow.setClickable(false);
            }
            dynamicEditTextRow.setAnswerStatus(EditTextRowView.ANSWERED);
        }

        List<ValidationBean> valiList = questionBean.getValidation();
        if (valiList.size() > 0) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_REQUIRED)) {
                    dynamicEditTextRow.isRequired(true);
                } else if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_GPS)
                        || validationBean.get_id().equals(LibDynamicAppConfig.VAL_ADD_INFO_IMAGE)
                ) {
                    dynamicEditTextRow.setAdditionalInfoClick(v -> dataListener.clickOnAdditionalButton(questionBean));

                }
            }
        }
        if (formStatus != NEW_FORM) {
            if (checkValueForVisibility(questionBean)) {
                dynamicEditTextRow.setVisibility(View.VISIBLE);
            } else {
                dynamicEditTextRow.setVisibility(View.GONE);
            }
        }

        for (ValidationBean validationBean : questionBean.getValidation()) {
            if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_NOT_ABLE_TO_FILL)) {
                dynamicEditTextRow.setFocusable(false);
                dynamicEditTextRow.setClickable(false);
                dynamicEditTextRow.setEnabled(false);
                isClickable = false;
                break;
            }
        }

    }

    public void setDataListener(QuestionHelperCallback.DataListener dataListener) {
        this.dataListener = dataListener;
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

    void createTextData(String text, QuestionBean questionBean) {
        if (dataListener != null) {
            dataListener.createTextData(text, questionBean);
        }
    }

    public void showAlert(String text, QuestionBean questionBean) {
        if (dataListener != null) {
            dataListener.showAlert(text, questionBean);
        }
    }


    void createDatePicker(QuestionBean questionBean, long max, long min) {
        if (dataListener != null) {
            dataListener.createDatePicker(questionBean, max, min);
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
    public void superChangeStatus(int status) {
        dynamicEditTextRow.setAnswerStatus(status);
    }

    @Override
    public void superResetQuestion() {
        dynamicEditTextRow.setInvalidate();
    }

    @Override
    public void superChangeTitle(String title) {
        dynamicEditTextRow.setHint(title);
    }

    @Override
    public void superSetAnswer(String answer) {
        dynamicEditTextRow.setAnswerEditText(answer);
    }

    @Override
    public void superSetEditable(boolean isEditable, String questionType) {
        dynamicEditTextRow.setFocusableForEditText(isEditable, questionType);
    }

    @Override
    public void superSetErrorMsg(String errorMsg) {
        dynamicEditTextRow.setErrorMsg(errorMsg);
    }

    @Override
    public void setAdditionalVisibility(int visibility) {
        dynamicEditTextRow.setAdditionalInfoVisibility(visibility);
    }

    @Override
    public void setAdditionalButtonStatus(int status) {
        dynamicEditTextRow.setAdditionalStatus(status);
    }

    @Override
    public void superSetAnswer(QuestionBeanFilled questionBeanFilled) {
        String viewableStringFormAns = QuestionsUtils.Companion.getViewableStringFormAns(questionBeanFilled);
        dynamicEditTextRow.setAnswerEditText(viewableStringFormAns);
    }
}
