package com.dhwaniris.dynamicForm.utils

import androidx.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import com.dhwaniris.dynamicForm.locationservice.LocationUpdatesService


/**
 * Created by ${Sahjad} on 01/29/2019.
 */
class LocationReceiver(val locationData: MutableLiveData<Location>) : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val location = p1?.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
        if (location?.accuracy != 0.0f)
            locationData.value = location
    }

}