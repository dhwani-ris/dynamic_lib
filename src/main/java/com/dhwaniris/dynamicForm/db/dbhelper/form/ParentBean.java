package com.dhwaniris.dynamicForm.db.dbhelper.form;


import com.google.gson.annotations.SerializedName;

public class ParentBean  {


    @SerializedName("order") private String order;

    @SerializedName("value") private String value;

    @SerializedName("type") private String type;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
