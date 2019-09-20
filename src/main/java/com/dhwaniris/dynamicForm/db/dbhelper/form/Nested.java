package com.dhwaniris.dynamicForm.db.dbhelper.form;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Nested  {


    @SerializedName("answerNestedData") private List<QuestionBeanFilled> answerNestedData;

    @SerializedName("forParentValue") private String forParentValue;

    public List<QuestionBeanFilled> getAnswerNestedData() {
        return answerNestedData;
    }

    public void setAnswerNestedData(List<QuestionBeanFilled> answerNestedData) {
        this.answerNestedData = answerNestedData;
    }

    public String getForParentValue() {
        return forParentValue;
    }

    public void setForParentValue(String forParentValue) {
        this.forParentValue = forParentValue;
    }
}
