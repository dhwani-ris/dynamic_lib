package com.dhwaniris.dynamicForm.db.helpers;

import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;

import java.util.List;


public class PostDataStructure {

    private String order;
    private String input_type;
    private List<Answers> answers;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getInput_type() {
        return input_type;
    }

    public void setInput_type(String input_type) {
        this.input_type = input_type;
    }

    public List<Answers> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answers> answers) {
        this.answers = answers;
    }

}