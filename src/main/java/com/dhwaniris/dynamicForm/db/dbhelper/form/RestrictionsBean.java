package com.dhwaniris.dynamicForm.db.dbhelper.form;


import java.util.List;

public class RestrictionsBean  {



    private String type;

    private List<OrdersBean> orders;

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