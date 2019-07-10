package com.dhwaniris.dynamicForm.db.dbhelper.form;

import android.os.Parcel;
import android.os.Parcelable;




public class OrdersBean  implements Parcelable {


    private String _id;

    private String value;

    private String order;

    public OrdersBean() {
    }

    public OrdersBean(String order) {
        this.order = order;
    }

    protected OrdersBean(Parcel in) {
        _id = in.readString();
        value = in.readString();
        order = in.readString();
    }

    public static final Creator<OrdersBean> CREATOR = new Creator<OrdersBean>() {
        @Override
        public OrdersBean createFromParcel(Parcel in) {
            return new OrdersBean(in);
        }

        @Override
        public OrdersBean[] newArray(int size) {
            return new OrdersBean[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(value);
        dest.writeString(order);
    }
}
