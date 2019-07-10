package com.dhwaniris.dynamicForm.db.helpers;

import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;


public class RestrictionHelper {

    private String qid;
    private String value;
    private String valueId;
    private String type;
    private QuestionBean childQBean;

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public QuestionBean getChildQBean() {
        return childQBean;
    }

    public void setChildQBean(QuestionBean childQBean) {
        this.childQBean = childQBean;
    }
}
