package com.dhwaniris.dynamicForm.db;


import java.util.List;

public class ErrorMsgLanguageBean  {

    private String lng;


    private List<MessageBean> message;


    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public List<MessageBean> getMessage() {
        return message;
    }

    public void setMessage(List<MessageBean> message) {
        this.message = message;
    }
}
