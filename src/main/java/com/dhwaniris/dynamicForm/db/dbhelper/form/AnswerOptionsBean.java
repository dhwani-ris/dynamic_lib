package com.dhwaniris.dynamicForm.db.dbhelper.form;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnswerOptionsBean  {
    /**
     * _id : 1
     * name : Yes
     */
    
    @SerializedName("_id") private String _id;
    
    @SerializedName("name") private String name;
    
    @SerializedName("did") private List<Did> did;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Did> getDid() {
        return did;
    }

    public void setDid(List<Did> did) {
        this.did = did;
    }
}
