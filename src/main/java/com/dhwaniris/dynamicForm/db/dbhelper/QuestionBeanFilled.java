package com.dhwaniris.dynamicForm.db.dbhelper;

import com.dhwaniris.dynamicForm.db.dbhelper.form.Answers;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Nested;


import java.util.List;


public class QuestionBeanFilled implements Comparable<QuestionBeanFilled> {


    private String order;
    private String label;

    private List<Answers> answer;
    private String title;

    private String input_type;
    private String viewSequence;
    private boolean required;
    private boolean optional;
    private boolean validAns;

    private List<Nested> nestedAnswer;
    private boolean isFilled;

    private String uid;

    private LocationBean location;

    private String image;

    private String columnName;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public String getImage() {
        if (image == null) {
            return "";
        }
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Answers> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Answers> answer) {
        this.answer = answer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInput_type() {
        return input_type;
    }

    public void setInput_type(String input_type) {
        this.input_type = input_type;
    }

    public String getViewSequence() {
        return viewSequence;
    }

    public void setViewSequence(String viewSequence) {
        this.viewSequence = viewSequence;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isValidAns() {
        return validAns;
    }

    public void setValidAns(boolean validAns) {
        this.validAns = validAns;
    }

    public List<Nested> getNestedAnswer() {
        return nestedAnswer;
    }

    public void setNestedAnswer(List<Nested> nestedAnswer) {
        this.nestedAnswer = nestedAnswer;
    }


    @Override
    public int compareTo(QuestionBeanFilled o) {
        int currentOrder = Integer.parseInt(viewSequence);
        int secondOrder = Integer.parseInt(o.getViewSequence());
        if (secondOrder < currentOrder) {
            return -1;
        } else if (secondOrder > currentOrder) {
            return 1;
        }

        return 0;
    }
}