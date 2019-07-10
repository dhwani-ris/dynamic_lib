package com.dhwaniris.dynamicForm.models;






public class FilesToUpload {



    private String mime_type;

    private String file_name;

    private String localUrl;

    private String url;

    private String file_alias;

    private String file_url;

    private String tranId;

    private String pos;

    private String nestedPos;

    private  boolean isDirectImage;

    public boolean isDirectImage() {
        return isDirectImage;
    }

    public void setDirectImage(boolean directImage) {
        isDirectImage = directImage;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile_alias() {
        return file_alias;
    }

    public void setFile_alias(String file_alias) {
        this.file_alias = file_alias;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getNestedPos() {
        return nestedPos;
    }

    public void setNestedPos(String nestedPos) {
        this.nestedPos = nestedPos;
    }
}
