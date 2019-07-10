package com.dhwaniris.dynamicForm.interfaces;

import android.view.View;

import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;

/**
 * Created by ${Sahjad} on 2/8/2019.
 */
public interface EditTextFocusChangeListener {
    void onFocusChange(QuestionBean questionBean, View v, boolean hasFocus);
}
