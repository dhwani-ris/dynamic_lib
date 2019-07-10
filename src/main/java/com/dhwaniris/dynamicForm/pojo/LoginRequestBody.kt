package com.dhwaniris.dynamicForm.pojo

import com.google.gson.annotations.SerializedName

/**
 * Created by ${Sahjad} on 4/18/2019.
 */
data class LoginRequestBody (
        @SerializedName("mobile") var mobile: String = "",
        @SerializedName("password") var password: String = "",

        @SerializedName("oSVersion") var oSVersion: String = "",
        @SerializedName("appVersion") var appVersion: String = "",
        @SerializedName("dbVersion") var dbVersion: String = "",

        @SerializedName("firebaseToken") var firebaseToken: String = "",

        @SerializedName("mobileNo") var mobileNo: String? = "",
        @SerializedName("networkProvider") var networkProvider: String? = "",

        @SerializedName("model") var model: String? = "",
        @SerializedName("manufacturer") var manufacturer: String? = "",
        @SerializedName("imeiSIM1") var imeiSIM1: String? = "",
        @SerializedName("imeiSIM2") var imeiSIM2: String? = "",
        @SerializedName("simSerialNumberSIM1") var simSerialNumberSIM1: String? = "",
        @SerializedName("deviceId") var deviceId: String = "",

        @SerializedName("lng") var lng: String? = "",
        @SerializedName("lat") var lat: String? = ""

    )