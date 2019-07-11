package com.dhwaniris.dynamicForm.db.dbhelper.form;

import com.dhwaniris.dynamicForm.utils.QuestionsUtils;
import java.util.LinkedHashMap;
import java.util.List;


public class LanguageBean  {



    private String lng;

    private String title;

    private List<QuestionBean> question;

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QuestionBean> getQuestion() {
        return question;
    }

    public void setQuestion(List<QuestionBean> question) {
        this.question = question;
    }


    public LinkedHashMap<String, QuestionBean> getQuestionMap() {
        LinkedHashMap<String, QuestionBean> linkedHashMap = new LinkedHashMap<>();
        for (QuestionBean questionBean : question) {
            linkedHashMap.put(QuestionsUtils.Companion.getQuestionUniqueId(questionBean), questionBean);
        }
        return linkedHashMap;
    }

}