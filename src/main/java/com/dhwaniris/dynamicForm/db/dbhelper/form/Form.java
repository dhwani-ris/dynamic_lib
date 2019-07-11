package com.dhwaniris.dynamicForm.db.dbhelper.form;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Form {


    @SerializedName("_id") private String _id;

    @SerializedName("formId") private int formId;

    @SerializedName("modifiedAt") private String modifiedAt;

    @SerializedName("createdAt") private String createdAt;

    @SerializedName("version") private String version;

    @SerializedName("fillCount") private int fillCount;

    @SerializedName("formIcon") private String formIcon;

    @SerializedName("editable") private boolean editable;

    @SerializedName("isActive") private boolean isActive;

    @SerializedName("language") private List<LanguageBean> language;

    @SerializedName("location") private boolean location;

    @SerializedName("isMedia") private boolean isMedia;

    @SerializedName("expiryDate") private long expiryDate;

//@SerializedName("minAppVersion")   private String minAppVersion;

    @SerializedName("dynamicData") private boolean dynamicData;

    @SerializedName("externalResource") private List<ExternalResourceBean> externalResource;

//@SerializedName("project")   private ProjectNameBean project;

    @SerializedName("formSynced") private boolean formSynced;

    @SerializedName("dataUpdateStatus") private boolean dataUpdateStatus;

    @SerializedName("errorManagementStatus") private boolean errorManagementStatus;

    @SerializedName("dataCollectionStatus") private boolean dataCollectionStatus;

    @SerializedName("duplicateCheckQuestions") private List<String> duplicateCheckQuestions;

    @SerializedName("keyInfoOrders") private List<String> keyInfoOrders;

    @SerializedName("getDynamicOptionsList") private List<GetDynamicOption> getDynamicOptionsList;

    @SerializedName("createDynamicOptionList") private List<CreateDynamicOption> createDynamicOptionList;


    public List<GetDynamicOption> getGetDynamicOptionsList() {
        return getDynamicOptionsList;
    }

    public void setGetDynamicOptionsList(List<GetDynamicOption> getDynamicOptionsList) {
        this.getDynamicOptionsList = getDynamicOptionsList;
    }

    public List<CreateDynamicOption> getCreateDynamicOptionList() {
        return createDynamicOptionList;
    }

    public void setCreateDynamicOptionList(List<CreateDynamicOption> createDynamicOptionList) {
        this.createDynamicOptionList = createDynamicOptionList;
    }

    private boolean isMasterSynced;

    public boolean isMasterSynced() {
        return isMasterSynced;
    }

    public void setMasterSynced(boolean masterSynced) {
        isMasterSynced = masterSynced;
    }

    public List<String> getKeyInfoOrders() {
        return keyInfoOrders;
    }

    public void setKeyInfoOrders(List<String> keyInfoOrders) {
        this.keyInfoOrders = keyInfoOrders;
    }

    public List<String> getDuplicateCheckQuestions() {
        return duplicateCheckQuestions;
    }

    public void setDuplicateCheckQuestions(List<String> duplicateCheckQuestions) {
        this.duplicateCheckQuestions = duplicateCheckQuestions;
    }

    public boolean isDataUpdateStatus() {
        return dataUpdateStatus;
    }

    public void setDataUpdateStatus(boolean dataUpdateStatus) {
        this.dataUpdateStatus = dataUpdateStatus;
    }

    public boolean isErrorManagementStatus() {
        return errorManagementStatus;
    }

    public void setErrorManagementStatus(boolean errorManagementStatus) {
        this.errorManagementStatus = errorManagementStatus;
    }

    public boolean isDataCollectionStatus() {
        return dataCollectionStatus;
    }

    public void setDataCollectionStatus(boolean dataCollectionStatus) {
        this.dataCollectionStatus = dataCollectionStatus;
    }

    public boolean isFormSynced() {
        return formSynced;
    }

    public void setFormSynced(boolean formSynced) {
        this.formSynced = formSynced;
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

    public boolean isMedia() {
        return isMedia;
    }

    public void setMedia(boolean media) {
        isMedia = media;
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

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<LanguageBean> getLanguage() {
        return language;
    }

    public void setLanguage(List<LanguageBean> language) {
        this.language = language;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }


//    public String getMinAppVersion() {
//        return minAppVersion;
//    }

//    public void setMinAppVersion(String minAppVersion) {
//        this.minAppVersion = minAppVersion;
//    }


    public boolean isDynamicData() {
        return dynamicData;
    }

    public void setDynamicData(boolean dynamicData) {
        this.dynamicData = dynamicData;
    }

    public List<ExternalResourceBean> getExternalResource() {
        return externalResource;
    }

    public void setExternalResource(List<ExternalResourceBean> externalResource) {
        this.externalResource = externalResource;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

//    public ProjectNameBean getProject() {
//        return project;
//    }

//    public void setProject(ProjectNameBean project) {
//        this.project = project;
//    }
}
