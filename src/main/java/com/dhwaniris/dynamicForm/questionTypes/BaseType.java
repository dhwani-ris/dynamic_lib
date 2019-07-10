package com.dhwaniris.dynamicForm.questionTypes;

import android.os.Handler;
import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ParentBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.LinkedHashMap;


public abstract class BaseType {


    public LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList;
    public LinkedHashMap<String, QuestionBean> questionBeenList;
    public QuestionBeanFilled answerBeanFilled;
    public QuestionBean questionBean;
    public int formStatus;
    private int viewIndex;
    public int formId;


    public int getViewIndex() {
        return viewIndex;
    }

    public void setViewIndex(int viewIndex) {
        this.viewIndex = viewIndex;
    }

    public void superChangeStatus(int status) {
    }

    public void superResetQuestion() {
    }

    public void superChangeTitle(String title) {
    }

    public void superSetAnswer(String answer) {
    }

    public void superSetAnswer(QuestionBeanFilled questionBeanFilled) {
    }

    public void superSetEditable(boolean isEditable, String questionType) {

    }

    public void superSetErrorMsg(String errorMsg) {

    }

    public boolean checkValueForVisibility(QuestionBean questionBean) {
        boolean isMatch;
        if (questionBean.getParent().size() > 0) {
            ParentBean parentBean1 = questionBean.getParent().get(0);
            isMatch = QuestionsUtils.isAnswerIsExpected(parentBean1.getOrder(), parentBean1.getValue(), answerBeanHelperList);

            if (questionBean.getParent().size() > 1) {
                isMatch = QuestionsUtils.validateVisibilityWithMultiParent(questionBean, isMatch, answerBeanHelperList);
            }
            for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                if (restrictionsBean.getType().equals(AppConfing.REST_MULTI_ANS_VISIBILITY_IF_NO_ONE_SELECTED)) {
                    isMatch = QuestionsUtils.validateMultiAnsRestriction(restrictionsBean, answerBeanHelperList, questionBeenList);
                    break;
                }
            }

        } else {
            return true;
        }
        return isMatch;
    }

    public void setAdditionalVisibility(int visibility) {
    }

    public void setAdditionalButtonStatus(int status) {
    }

    void preventDoubleClicks(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        new Handler().postDelayed(() -> {
            view.setClickable(true);
            view.setFocusable(true);
        }, 1000);

    }
}
