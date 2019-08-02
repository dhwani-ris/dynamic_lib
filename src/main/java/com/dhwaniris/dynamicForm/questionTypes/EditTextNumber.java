package com.dhwaniris.dynamicForm.questionTypes;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import com.dhwaniris.dynamicForm.NetworkModule.LibDynamicAppConfig;
import com.dhwaniris.dynamicForm.customViews.EditTextRowView;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.db.dbhelper.form.RestrictionsBean;
import com.dhwaniris.dynamicForm.interfaces.EditTextFocusChangeListener;
import com.dhwaniris.dynamicForm.interfaces.QuestionHelperCallback;

import java.util.LinkedHashMap;


public class EditTextNumber extends BaseEditTextType {


    public EditTextNumber(QuestionHelperCallback.DataListener dataListener, View view, QuestionBean questionBean,
                          int formStatus, LinkedHashMap<String, QuestionBean> questionBeenList,
                          LinkedHashMap<String, QuestionBeanFilled>  answerBeanHelperList, EditTextFocusChangeListener focusChangeListener) {
        super(dataListener, view, questionBean, formStatus, questionBeenList, answerBeanHelperList);
        initNumberType(focusChangeListener);
    }

    private void initNumberType(final EditTextFocusChangeListener focusChangeListener) {

        dynamicEditTextRow.getEditTextView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                focusChangeListener.onFocusChange(questionBean, view, b);
            }
        });


        dynamicEditTextRow.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


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
                            if (restrictionsBean.getType().equals(LibDynamicAppConfig.REST_VALUE_AS_TITLE_OF_CHILD)) {
                                changeTitleRequest(restrictionsBean, value);
                            }
                        }
                    }

                }

            }
        });


    }


}
