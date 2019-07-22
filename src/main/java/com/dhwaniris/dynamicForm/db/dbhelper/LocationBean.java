package com.dhwaniris.dynamicForm.db.dbhelper;


import com.google.gson.annotations.SerializedName;

public class LocationBean  {



    @SerializedName("lat") private String lat;

    @SerializedName("lng") private String lng;

    @SerializedName("accuracy") private String accuracy;

    public String getLat() {
        if (lat == null) {
            lat = "0";
        }
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        if (lng == null) {
            lng = "0";
        }
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAccuracy() {
        if (accuracy == null) {
            accuracy = "0";
        }
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
