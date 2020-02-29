package com.dhwaniris.dynamicForm.questionTypes;

import android.os.Handler;
import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.ParentBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
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

    public boolean isClickable = true;


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
        boolean isVisible = QuestionsUtils.Companion.checkValueForVisibility(questionBean, answerBeanHelperList, questionBeenList);
        QuestionBeanFilled answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
        if (!isVisible && answerBeanFilled != null) {
            answerBeanFilled.setRequired(false);
        }
        return isVisible;
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
