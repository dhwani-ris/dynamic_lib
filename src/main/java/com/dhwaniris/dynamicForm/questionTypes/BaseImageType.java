package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.customViews.ImageRowView;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ValidationBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.List;



import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.NEW_FORM;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.SUBMITTED;


public class BaseImageType extends BaseType {

    private QuestionHelperCallback.ImageViewListener imageViewListener;
    ImageRowView dynamicImageViewRow;

    void createDynamicView(View view) {
        dynamicImageViewRow = view.findViewById(R.id.custom);
    }

    void setBasicFunctionality(QuestionBean questionBean, int formStatus) {
        dynamicImageViewRow.setTag(questionBean.get_id());
        dynamicImageViewRow.setTitle(questionBean.getTitle());
        dynamicImageViewRow.setOrder(questionBean.getLabel());
        if (questionBean.getInformation() != null && !questionBean.getInformation().trim().equals("")) {

            dynamicImageViewRow.setInformation(questionBean.getInformation());
        }
        if (formStatus == SUBMITTED) {
            dynamicImageViewRow.setFocusable(false);
            dynamicImageViewRow.setClickable(false);
            dynamicImageViewRow.setAnswerStatus(ImageRowView.ANSWERED);
            dynamicImageViewRow.hideButtons();
        } else if (formStatus == AppConfing.SYNCED_BUT_EDITABLE || formStatus == AppConfing.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            if (!questionBean.isEditable()) {
                dynamicImageViewRow.setFocusable(false);
                dynamicImageViewRow.setClickable(false);
                dynamicImageViewRow.hideButtons();
            }
            dynamicImageViewRow.setAnswerStatus(ImageRowView.ANSWERED);

        }
        List<ValidationBean> valiList = questionBean.getValidation();
        if (valiList.size() > 0) {
            for (ValidationBean validationBean : valiList) {
                if (validationBean.get_id().equals(AppConfing.VAL_REQUIRED)) {
                    dynamicImageViewRow.isRequired(true);
                    break;
                }
            }
        }
        //check if draft and is field filled or not

        if (formStatus != NEW_FORM) {
            ///this check for if qestion come frist time in draft
            if (checkValueForVisibility(questionBean)) {
                dynamicImageViewRow.setVisibility(View.VISIBLE);
            } else {
                dynamicImageViewRow.setVisibility(View.GONE);
            }
            answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
            setData(answerBeanFilled);

        }

    }

    void setData(QuestionBeanFilled answerBeanFilled) {
        if (answerBeanFilled != null) {
            final List<Answers> ans = answerBeanFilled.getAnswer();
            if (ans.size() > 0) {
                if (ans.get(0).getValue() != null) {
                    if (ans.get(0).getValue().equals("")) {
                        dynamicImageViewRow.setAnswerStatus(ImageRowView.NONE);
                    } else {
                        dynamicImageViewRow.setSrc(ans.get(0).getValue());
                        dynamicImageViewRow.setAnswerStatus(ImageRowView.ANSWERED);
                    }
                }
            }

        }
    }

    void setImageViewListener(QuestionHelperCallback.ImageViewListener imageViewListener) {
        this.imageViewListener = imageViewListener;
    }

    void hideKeyboard() {
        if (imageViewListener != null) {
            imageViewListener.onhidekeyboard();
        }
    }

    void createNewImageObject(QuestionBean questionBean, boolean isVisibleInHideList) {
        if (imageViewListener != null) {
            imageViewListener.createNewAnswerObject(questionBean, isVisibleInHideList);
        }
    }

    void pickImage(QuestionBean questionBean) {
        if (imageViewListener != null) {
            imageViewListener.pickImage(questionBean);
        }
    }

    void clickImage(QuestionBean questionBean) {
        if (imageViewListener != null) {
            imageViewListener.clickImage(questionBean);
        }
    }

    @Override
    public void superChangeStatus(int status) {
        dynamicImageViewRow.setAnswerStatus(status);
    }

    @Override
    public void superResetQuestion() {
        dynamicImageViewRow.setInvalidate();
    }

    @Override
    public void superChangeTitle(String title) {
        dynamicImageViewRow.setTitle(title);
    }

    @Override
    public void superSetEditable(boolean isEditable, String questionType) {
        dynamicImageViewRow.setClickable(isEditable);
        dynamicImageViewRow.setFocusable(isEditable);
        if (isEditable) {
            dynamicImageViewRow.showButtons();
        } else {
            dynamicImageViewRow.hideButtons();
        }
    }

    @Override
    public void superSetErrorMsg(String errorMsg) {
        dynamicImageViewRow.setErrorMsg(errorMsg);
    }

    @Override
    public void superSetAnswer(String answer) {
        dynamicImageViewRow.setSrc(answer);
    }

    @Override
    public void superSetAnswer(QuestionBeanFilled questionBeanFilled) {
        setData(questionBeanFilled);
    }
}
