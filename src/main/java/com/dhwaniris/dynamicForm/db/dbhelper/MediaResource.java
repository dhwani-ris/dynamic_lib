package com.dhwaniris.dynamicForm.db.dbhelper;

public class MediaResource  {

    private String uniqueId;
    private String webUrl;
    private String localUri;
    private String formId;
    private boolean isDownloadCompleted;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public boolean isDownloadCompleted() {
        return isDownloadCompleted;
    }

    public void setDownloadCompleted(boolean downloadCompleted) {
        isDownloadCompleted = downloadCompleted;
    }
}
