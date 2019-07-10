package com.dhwaniris.dynamicForm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;



public  class BatteryChangeReceiver extends BroadcastReceiver {
    public static int deviceStatus;
    public static int batteryLevel;
    @Override
    public void onReceive(Context context, Intent intent) {
        batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    }
}
