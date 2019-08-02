package com.dhwaniris.dynamicForm.questionTypes;

import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;

import java.util.LinkedHashMap;
import java.util.List;

import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SYNCED_BUT_EDITABLE;

public class DateSelect extends BaseSelectType {


    public DateSelect(View view, final QuestionBean questionBean, int formStatus,
                      QuestionHelperCallback.DataListener dataListener,
                      LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {
        super(view, questionBean, formStatus, dataListener,
                questionBeenList, answerBeanHelperList);
        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == SYNCED_BUT_EDITABLE || formStatus == LibDynamicAppConfig.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            setData();
        }
        boolean iniitList = true;
        if (formStatus == SUBMITTED || formStatus == EDITABLE_SUBMITTED) {
            iniitList = false;
        }

        if (iniitList && isClickable)
            initListener();

    }


    private void setData() {
        //draft action
        if (answerBeanFilled != null) {
            //setAnswer
            final List<Answers> ans = answerBeanFilled.getAnswer();
            if (!ans.isEmpty()) {
                Answers answers = ans.get(0);
                String text = answers.getValue();
                dynamicEditTextRow.setText(text);
                if (!answers.getValue().equals("")) {
                    dynamicEditTextRow.setAnswerStatus(EditTextRowView.ANSWERED);
                }

                if (questionBean.getRestrictions().size() > 0) {
                    for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                        if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD)) {
                            changeTitleRequest(restrictionsBean, text);
                        }
                    }
                }


            }

        }
    }


    private void initListener() {
        dynamicEditTextRow.setOnCustomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                createDatePicker(questionBean, 0, 0);
            }
        });


    }


}
