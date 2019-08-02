package com.dhwaniris.dynamicForm.questionTypes;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;
import com.dhwaniris.dynamicForm.utils.AadharCardValidation;

import java.util.LinkedHashMap;
import java.util.List;


import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.DRAFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.EDITABLE_DARFT;
import static com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig.SUBMITTED;


public class EditTextAadhar extends BaseEditTextType {
    public EditTextAadhar(QuestionHelperCallback.DataListener dataListener, View view, QuestionBean questionBean, int formStatus, LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList) {
        super(dataListener, view, questionBean, formStatus, questionBeenList, answerBeanHelperList);
        initAdharType();
    }

    private void initAdharType() {

        if (formStatus == DRAFT || formStatus == SUBMITTED || formStatus == LibDynamicAppConfig.SYNCED_BUT_EDITABLE || formStatus == LibDynamicAppConfig.EDITABLE_SUBMITTED || formStatus == EDITABLE_DARFT) {
            final List<Answers> ans = answerBeanFilled.getAnswer();
            if (!ans.isEmpty()) {
                Answers answers = ans.get(0);
                dynamicEditTextRow.setText(answers.getValue());
                if (!answers.getValue().equals("")) {
                    if (AadharCardValidation.validateVerhoeff(answers.getValue())) {
                        dynamicEditTextRow.setAnswerStatus(EditTextRowView.ANSWERED);
                    } else {
                        dynamicEditTextRow.setAnswerStatus(EditTextRowView.NOT_ANSWERED);
                    }
                }
            }
        }
        dynamicEditTextRow.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        dynamicEditTextRow.setMaxLength(12);

        // save data in saving list
        dynamicEditTextRow.attachTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().contains(".")) {

                    if (!editable.toString().equals("")) {
                        if (AadharCardValidation.validateVerhoeff(editable.toString()) &&
                                editable.toString().trim().length() == 12) {

                            dynamicEditTextRow.setAnswerStatus(EditTextRowView.ANSWERED);
                            createTextData(editable.toString(), questionBean);

                        } else {
                            dynamicEditTextRow.setAnswerStatus(EditTextRowView.NOT_ANSWERED);
                        }
                    } else {
                        dynamicEditTextRow.setAnswerStatus(EditTextRowView.NONE);
                        createTextData(editable.toString(), questionBean);
                    }
                } else {
                    String txt = editable.toString().replace(".", "");
                    dynamicEditTextRow.setText(txt);
                }
            }
        });
    }

}
