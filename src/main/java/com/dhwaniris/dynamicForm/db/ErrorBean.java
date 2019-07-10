package com.dhwaniris.dynamicForm.db;


import java.util.List;

public class ErrorBean  {


    private String code;

    private List<ErrorMsgLanguageBean> languages;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ErrorMsgLanguageBean> getLanguages() {
        return languages;
    }

    public void setLanguages(List<ErrorMsgLanguageBean> languages) {
        this.languages = languages;
    }
}
