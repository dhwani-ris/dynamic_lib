package com.dhwaniris.dynamicForm.db.dbhelper.form;


import com.google.gson.annotations.SerializedName;

public class ChildBean  {

    /**
     * order : 6
     * value : 1
     * _id : 59d4797d96086547dc81b010
     */


    @SerializedName("order") private String order;

    @SerializedName("value") private String value;

    @SerializedName("type") private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getValue() {
        if (value == null) {
            value = "";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
