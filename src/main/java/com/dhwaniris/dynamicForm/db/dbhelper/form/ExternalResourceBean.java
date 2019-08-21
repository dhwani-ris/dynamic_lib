package com.dhwaniris.dynamicForm.db.dbhelper.form;


import com.google.gson.annotations.SerializedName;

public class ExternalResourceBean  {




    @SerializedName("_id") private String _id;

    @SerializedName("url") private String url;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

