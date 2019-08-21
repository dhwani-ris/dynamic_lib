package com.dhwaniris.dynamicForm.db.dbhelper.form;


import com.google.gson.annotations.SerializedName;

public class ValidationBean  {



    @SerializedName("_id") private String _id;

    @SerializedName("error_msg") private String error_msg;


    @SerializedName("value") private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }



    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
