package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;

import java.util.LinkedHashMap;
import java.util.List;

import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.NEW_FORM;


class BaseEditTextType extends BaseQuestionType {


    BaseEditTextType(QuestionHelperCallback.DataListener dataListener, View view, final QuestionBean questionBean, int formStatus,
                     LinkedHashMap<String, QuestionBean> questionBeenList,
                     LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {
        createDynamicView(view);

        this.answerBeanHelperList = answerBeanHelperList;
        this.questionBeenList = questionBeenList;
        setBasicFunctionality(questionBean, formStatus);
        setDataListener(dataListener);
        this.questionBean = questionBean;
        this.formStatus = formStatus;

        if (!questionBean.getMax().equals(""))
            dynamicEditTextRow.setMaxLength(Integer.parseInt(questionBean.getMax()));


        if (formStatus == NEW_FORM) {
            if (questionBean.getParent().size() > 0) {
                dynamicEditTextRow.setVisibility(View.GONE);
            }
            createNewAnswerObjRequest(questionBean, dynamicEditTextRow.getVisibility() == View.VISIBLE);

        } else {
            answerBeanFilled = answerBeanHelperList.get(QuestionsUtils.Companion.getQuestionUniqueId(questionBean));
            if (answerBeanFilled != null) {
                if (answerBeanFilled.isRequired()) {
                    dynamicEditTextRow.isRequired(true);
                }
                //setAnswer
                final List<Answers> ans = answerBeanFilled.getAnswer();
                if (!ans.isEmpty()) {
                    Answers answers = ans.get(0);
                    if (answers != null) {
                        if (!QuestionsUtils.Companion.getValueFormTextInputType(answers).isEmpty()) {
                            dynamicEditTextRow.setAnswerStatus(EditTextRowView.ANSWERED);
                        }
                        if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_TEXT) || questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_ADDRESS)) {
                            dynamicEditTextRow.setText(answers.getTextValue());

                        } else {
                            dynamicEditTextRow.setText(answers.getValue());
                        }
                        if (questionBean.getRestrictions().size() > 0) {
                            for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                                if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD)) {
                                    changeTitleRequest(restrictionsBean, answers.getValue());
                                }
                            }
                        }
                    }


                }

            }
        }
// EditText is not editable (i.e. not filled by user)


    }


}
