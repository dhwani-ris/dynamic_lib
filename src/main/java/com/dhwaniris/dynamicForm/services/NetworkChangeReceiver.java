package com.dhwaniris.dynamicForm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dhwaniris.dynamicForm.utils.NetworkUtil;



public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean status = NetworkUtil.getConnectivityStatusString(context);
        //DashBoardActivity.addLogText(status, context);
    }
}
