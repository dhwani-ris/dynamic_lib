package com.dhwaniris.dynamicForm.db.dbhelper;


import com.google.gson.annotations.SerializedName;

public class HelpDataBean  {


    @SerializedName("title") private String title;

    @SerializedName("name") private String name;

    @SerializedName("number") private String number;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
