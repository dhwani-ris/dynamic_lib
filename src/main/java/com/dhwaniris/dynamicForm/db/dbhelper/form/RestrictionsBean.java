package com.dhwaniris.dynamicForm.db.dbhelper.form;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestrictionsBean  {



    @SerializedName("type") private String type;

    @SerializedName("orders") private List<OrdersBean> orders;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<OrdersBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrdersBean> orders) {
        this.orders = orders;
    }
}