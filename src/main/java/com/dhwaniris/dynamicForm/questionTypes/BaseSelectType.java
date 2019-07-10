package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.LinkedHashMap;

import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.NEW_FORM;


class BaseSelectType extends BaseQuestionType {

    BaseSelectType(View view, final QuestionBean questionBean, int formStatus, QuestionHelperCallback.DataListener dataListener,
                   LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled>  answerBeanHelperList) {

        createDynamicView(view);
        this.answerBeanHelperList = answerBeanHelperList;
        this.questionBeenList = questionBeenList;
        setDataListener(dataListener);
        setBasicFunctionality(questionBean, formStatus);
        dynamicEditTextRow.setFocusable(false);

        this.questionBean = questionBean;
        this.formStatus = formStatus;
        if (formStatus == NEW_FORM) {
            if (questionBean.getParent().size() > 0) {
                dynamicEditTextRow.setVisibility(View.GONE);
            }
            createNewAnswerObjRequest(questionBean, dynamicEditTextRow.getVisibility() == View.VISIBLE);
        }

        answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.getQuestionUniqueId(questionBean));
    }


}
