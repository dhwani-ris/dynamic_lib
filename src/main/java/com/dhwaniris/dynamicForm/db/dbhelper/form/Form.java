package com.dhwaniris.dynamicForm.db.dbhelper.form;


import java.util.List;

public class Form  {



    private String _id;

    private int formId;

    private String modifiedAt;

    private String createdAt;

    private String version;

    private int fillCount;

    private String formIcon;

    private boolean editable;

    private boolean isActive;

    private List<LanguageBean> languages;

    private boolean location;

    private boolean isMedia;

    private long expiryDate;

    private String minAppVersion;

    private boolean dynamicData;

    private List<ExternalResourceBean> externalResource;

    private ProjectNameBean project;

    private boolean formSynced;

    private boolean dataUpdateStatus;

    private boolean errorManagementStatus;

    private boolean dataCollectionStatus;

    private List<String> duplicateCheckQuestions;

    private List<String> keyInfoOrders;

    private List<GetDynamicOption> getDynamicOptionsList;

    private List<CreateDynamicOption> createDynamicOptionList;


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

    public List<LanguageBean> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageBean> languages) {
        this.languages = languages;
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


    public String getMinAppVersion() {
        return minAppVersion;
    }

    public void setMinAppVersion(String minAppVersion) {
        this.minAppVersion = minAppVersion;
    }


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

    public ProjectNameBean getProject() {
        return project;
    }

    public void setProject(ProjectNameBean project) {
        this.project = project;
    }
}
