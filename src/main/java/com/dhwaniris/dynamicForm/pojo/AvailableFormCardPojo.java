package com.dhwaniris.dynamicForm.pojo;


public class AvailableFormCardPojo {

    private String _id;
    private int formId;
    private int formFilledByuser;
    private String modifiedAt;
    private String createdAt;
    private String version;
    private int fillCount;
    private String formIcon;
    private boolean editable;
    private boolean isActive;
    private long expiryDate;
    private String minAppVersion;
    private int questionCount;
    private String titleInLanguage;
    private int draftCount;
    private int saveCount;
    private int errorCount;
    private String projectName;
    private int viewType;
    private String userToken;
    private boolean syncaform;
    private int numberOfQuestions;

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public String getTitleInLanguage() {
        return titleInLanguage;
    }

    public void setTitleInLanguage(String titleInLanguage) {
        this.titleInLanguage = titleInLanguage;
    }


    public int getDraftCount() {
        return draftCount;
    }

    public void setDraftCount(int draftCount) {
        this.draftCount = draftCount;
    }

    public int getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(int saveCount) {
        this.saveCount = saveCount;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getFormFilledByuser() {
        return formFilledByuser;
    }

    public void setFormFilledByuser(int formFilledByuser) {
        this.formFilledByuser = formFilledByuser;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getFillCount() {
        return fillCount;
    }

    public void setFillCount(int fillCount) {
        this.fillCount = fillCount;
    }

    public String getFormIcon() {
        return formIcon;
    }

    public void setFormIcon(String formIcon) {
        this.formIcon = formIcon;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMinAppVersion() {
        return minAppVersion;
    }

    public void setMinAppVersion(String minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public boolean isSyncaform() {
        return syncaform;
    }

    public void setSyncaform(boolean syncaform) {
        this.syncaform = syncaform;
    }
}
