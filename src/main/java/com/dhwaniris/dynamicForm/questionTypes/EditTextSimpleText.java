package com.dhwaniris.dynamicForm.questionTypes;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;

import java.util.LinkedHashMap;


public class EditTextSimpleText extends BaseEditTextType {
    public EditTextSimpleText(QuestionHelperCallback.DataListener dataListener, View view, QuestionBean questionBean, int formStatus, LinkedHashMap<String, QuestionBean> questionBeenList, LinkedHashMap<String, QuestionBeanFilled>  answerBeanHelperList) {
        super(dataListener, view, questionBean, formStatus, questionBeenList, answerBeanHelperList);
        initAdharType();
    }

    private void initAdharType() {

        dynamicEditTextRow.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        if (questionBean.getInput_type().equals(AppConfing.QUS_ADDRESS)) {
            dynamicEditTextRow.setMaxLines(7);
            dynamicEditTextRow.singleLine(false);

        }


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

                createTextData(editable.toString(), questionBean);
                String value = editable.toString();
                if (editable.toString().equals("")) {
                    dynamicEditTextRow.setAnswerStatus(EditTextRowView.NONE);
                } else {

                    if (questionBean.getRestrictions().size() > 0) {
                        for (RestrictionsBean restrictionsBean : questionBean.getRestrictions()) {
                            if (restrictionsBean.getType().equals(AppConfing.REST_VALUE_AS_TITLE_OF_CHILD)) {
                                changeTitleRequest(restrictionsBean, value);
                            }
                        }
                    }

                }

            }
        });
    }

}
