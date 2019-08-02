package com.dhwaniris.dynamicForm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import static com.dhwaniris.dynamicForm.base.BaseActivity.errorMessgae;


public class NetworkUtil {

    private static int TYPE_WIFI = 1;
    private static int TYPE_MOBILE = 2;
    private static int TYPE_NOT_CONNECTED = 0;

    private static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    //check network connection
    public static boolean checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    public static boolean getConnectivityStatusString(Context context) {

        int conn = NetworkUtil.getConnectivityStatus(context);

        boolean status = false;
        if (conn == NetworkUtil.TYPE_WIFI) {
            //status = "Wifi enabled";
            status = true;
//            status = LibDynamicAppConfig.CONNECT_TO_WIFI;
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            //status = "Mobile data enabled";
            status = getNetworkClass(context);

        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = false;
        }
        return status;
    }

    private static boolean getNetworkClass(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
            return false; //not connected
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return true;
        //      int networkType = info.getSubtype();
        return info.getType() == ConnectivityManager.TYPE_MOBILE;

    }

    // Error Response handle
    public static String parseErrorMessage(String msg) {

        // {"success":false,"status":401,"message":"Authentication Failed"}
        try {
            JSONObject jsonObject = new JSONObject(msg);
            errorMessgae = jsonObject.get("message").toString();
        } catch (JSONException e) {
             Log.e("error",e.getMessage());;
        }
        return errorMessgae;
    }
}
