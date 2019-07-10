package com.dhwaniris.dynamicForm.pojo;

import java.util.List;

/**
 * Created by ${Sahjad} on 4/25/2019.
 */
public class FilledFormCard {

    private int upload_status;
    private String transactionId;
    private String mobileCreatedAt;
    private List<String> viewSecquenceList;

    public int getUpload_status() {
        return upload_status;
    }

    public void setUpload_status(int upload_status) {
        this.upload_status = upload_status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMobileCreatedAt() {
        return mobileCreatedAt;
    }

    public void setMobileCreatedAt(String mobileCreatedAt) {
        this.mobileCreatedAt = mobileCreatedAt;
    }

    public List<String> getViewSecquenceList() {
        return viewSecquenceList;
    }

    public void setViewSecquenceList(List<String> viewSecquenceList) {
        this.viewSecquenceList = viewSecquenceList;
    }
}
