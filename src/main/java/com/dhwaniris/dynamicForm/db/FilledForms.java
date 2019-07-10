package com.dhwaniris.dynamicForm.db;

import com.dhwaniris.dynamicForm.db.dbhelper.LocationBean;
import com.dhwaniris.dynamicForm.db.dbhelper.QuestionBeanFilled;
import com.dhwaniris.dynamicForm.utils.QuestionsUtils;


import java.util.LinkedHashMap;
import java.util.List;

public class FilledForms  {



    private String transactionId;

    private String formUiniqueId;

    private String formId;

    private String title;

    private String version;

    private String timeTaken;

    private String mobileCreatedAt;

    private String mobileUpdatedAt;

    private int upload_status;

    private String language;

    private boolean isMedia;

    private LocationBean location;

    private List<QuestionBeanFilled> question;

    private String modifiedAt;

    private String responseId;


    private String uniqueId;

    private List<String> dependentResponse;

    public List<String> getDependentResponse() {
        return dependentResponse;
    }

    public void setDependentResponse(List<String> dependentResponse) {
        this.dependentResponse = dependentResponse;
    }


    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getTransactionId() {
        if (transactionId == null) {
            transactionId = "";
        }
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFormUiniqueId() {
        return formUiniqueId;
    }

    public void setFormUiniqueId(String formUiniqueId) {
        this.formUiniqueId = formUiniqueId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        if (version == null) {
            return "0";
        }
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTimeTaken() {
        if (timeTaken == null) {
            timeTaken = "0";
        }
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getMobileCreatedAt() {
        return mobileCreatedAt;
    }

    public void setMobileCreatedAt(String mobileCreatedAt) {
        this.mobileCreatedAt = mobileCreatedAt;
    }

    public String getMobileUpdatedAt() {
        return mobileUpdatedAt;
    }

    public void setMobileUpdatedAt(String mobileUpdatedAt) {
        this.mobileUpdatedAt = mobileUpdatedAt;
    }

    public int getUpload_status() {
        return upload_status;
    }

    public void setUpload_status(int upload_status) {
        this.upload_status = upload_status;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<QuestionBeanFilled> getQuestion() {
        return question;
    }

    public void setQuestion(List<QuestionBeanFilled> question) {
        this.question = question;
    }

    public boolean isMedia() {
        return isMedia;
    }

    public void setMedia(boolean media) {
        isMedia = media;
    }

    public LinkedHashMap<String, QuestionBeanFilled> getAnswersMap() {
        LinkedHashMap<String, QuestionBeanFilled> answerBeanHelperList= new LinkedHashMap<>();
        for (QuestionBeanFilled questionBeanFilled : question) {
            answerBeanHelperList.put(QuestionsUtils.getAnswerUniqueId(questionBeanFilled), questionBeanFilled);
        }
        return answerBeanHelperList;
    }
}
