package com.dhwaniris.dynamicForm.questionTypes;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

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
    public View view;

    public LinearLayout parentLayout;

    public BaseType expandableParentView;

    public boolean isClickable = true;

    public BaseType getExpandableParentView() {
        return expandableParentView;
    }

    public void setExpandableParentView(BaseType expandableParentView) {
        this.expandableParentView = expandableParentView;
    }

    public LinearLayout getParentLayout() {
        return parentLayout;
    }

    public void setParentLayout(LinearLayout parentLayout) {
        this.parentLayout = parentLayout;
    }

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

    public void expandView() {
    }

    public void superSetErrorMsg(String errorMsg) {

    }

    public boolean checkValueForVisibility(QuestionBean questionBean) {
        boolean isMatch;
        if (questionBean.getParent().size() > 0) {
            ParentBean parentBean1 = questionBean.getParent().get(0);
            boolean ignoreSuperParent=questionBean.containsValidation (LibDynamicAppConfig.VAL_IGNORE_SUPER_PARENT);
            isMatch = QuestionsUtils.Companion.isAnswerIsExpected(parentBean1.getOrder(), parentBean1.getValue(), answerBeanHelperList)
            && (QuestionsUtils.Companion.validateSuperParent(parentBean1,questionBeenList,answerBeanHelperList)||ignoreSuperParent);

            if (questionBean.getParent().size() > 1) {
                isMatch = QuestionsUtils.Companion.validateVisibilityWithMultiParent(questionBean, answerBeanHelperList,questionBeenList);
            }
            for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_MULTI_ANS_VISIBILITY_IF_NO_ONE_SELECTED)) {
                    isMatch = QuestionsUtils.Companion.validateMultiAnsRestriction(restrictionsBean, answerBeanHelperList, questionBeenList);
                    break;
                }
            }
            if(questionBean.containsValidation(LibDynamicAppConfig.VAL_REVERSE_VISIBILITY)){
                isMatch = !isMatch;
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
        }, 2000);

    }
}
