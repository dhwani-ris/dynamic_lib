package com.dhwaniris.dynamicForm.db.dbhelper.form;


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Did {


    @SerializedName("parent_option") private String parent_option;

    @SerializedName("parentOrder") private String parentOrder;

    public String getParentOrder() {
        return parentOrder;
    }

    public void setParentOrder(String parentOrder) {
        this.parentOrder = parentOrder;
    }

    public String getParent_option() {
        return parent_option;
    }

    public void setParent_option(String parent_option) {
        this.parent_option = parent_option;
    }

    @NonNull
    @Override
    public String toString() {
        return parent_option+parentOrder;
    }

    @Override
    public int hashCode() {
        return (parent_option+parentOrder).hashCode();
    }
}
