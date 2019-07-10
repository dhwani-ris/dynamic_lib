package com.dhwaniris.dynamicForm.interfaces;

import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;

import java.util.List;



public interface SelectListener {

    void MultiSelector(QuestionBean question, List<String> value, String text);

    void SingleSelector(QuestionBean question, String value, String id,boolean isSingle);

    void DateSelector(String dd, String mm, String yy, QuestionBean question);
}
