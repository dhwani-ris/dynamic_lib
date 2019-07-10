package com.dhwaniris.dynamicForm.db;

import com.dhwaniris.dynamicForm.db.dbhelper.form.Form;

import java.util.List;


public class FormResponse  {


    private String timestamp;

    private boolean success;

    private String message;

    private List<Form> data;

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

    public List<Form> getData() {
        return data;
    }

    public void setData(List<Form> data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
