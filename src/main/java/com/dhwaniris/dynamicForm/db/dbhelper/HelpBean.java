package com.dhwaniris.dynamicForm.db.dbhelper;


import java.util.List;

public class HelpBean  {



    private int timestamp;

    private boolean success;

    private String message;

    private List<HelpDataBean> data;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HelpDataBean> getData() {
        return data;
    }

    public void setData(List<HelpDataBean> data) {
        this.data = data;
    }
}
