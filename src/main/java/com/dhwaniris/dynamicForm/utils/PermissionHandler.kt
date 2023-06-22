package com.dhwaniris.dynamicForm.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.dhwaniris.dynamicForm.R


/**
 * Created by ${Sahjad} on 01/29/2019.
 */
class PermissionHandler(val context: Context, val permissionHandlerListener: PermissionHandlerListener) {

    fun checkGpsFilePhonePermission(): Boolean {
        if (Build.VERSION_CODES.TIRAMISU > Build.VERSION.SDK_INT) {
            return hasPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
            )
        }else{
            return hasPermission(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
            )
        }
    }

    //ask location permission
    fun requestGpsPermission() {
         requestPermissionRationale(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    //check GPS permission
    fun checkGpsPermission(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun checkPhoneStatePermission(): Boolean {
        return hasPermission(Manifest.permission.READ_PHONE_STATE)
    }


    private fun hasPermission(vararg permission: String): Boolean {
        permission.forEach {
            val result = ContextCompat.checkSelfPermission(context, it)
            if (result == PackageManager.PERMISSION_DENIED)
                return false
        }
        return true
    }

    private fun requestPermissionRationale(permission: Array<String>) {

        for (singlePermission in permission) {
            if (shouldShowRequestPermissionRationale(context as Activity, singlePermission)) {
                showPermissionAlertGpsFilePhone(false, permission)
                return
            }
        }

        requestPermission(permission)
    }

    private fun requestPermission(permission: Array<String>) {

        ActivityCompat.requestPermissions(context as Activity,
                permission,
                Constant.REQUEST_FOR_DYNAMIC_PERMISSIONS)

    }

    //permission listener
    private fun showPermissionAlertGpsFilePhone(isNeverAsk: Boolean, permissions: Array<String>) {
        AlertDialog.Builder(context)
                .setTitle(R.string.permission_required)
                .setMessage(R.string.login_permission)
                .setCancelable(true)
                .setPositiveButton(R.string.ok) { _, _ ->
                    if (!checkGpsFilePhonePermission()) {
                        if (!isNeverAsk) {
                            requestPermission(permissions)
                        } else {
                            openAppSetting(context)
                        }
                    }
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    permissionHandlerListener.deniedPermission(true)
                }
                .show()

    }

    private fun openAppSetting(context: Context) {
        val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.packageName))
        context.startActivity(i)
    }

    @Suppress("UNCHECKED_CAST")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constant.REQUEST_FOR_DYNAMIC_PERMISSIONS) {
            var permissionStatus = true
            if (grantResults.isNotEmpty()) {
                for (res in grantResults) {
                    if (res == PackageManager.PERMISSION_DENIED) {
                        permissionStatus = false
                        break
                    }
                }
            } else {
                permissionStatus = false
            }
            if (permissionStatus) {
                permissionHandlerListener.acceptedPermission(grantResults)
            } else {
                for (singlePermission in permissions) {
                    if (!hasPermission(singlePermission)) {
                        val showRationale = shouldShowRequestPermissionRationale(context as Activity, singlePermission)
                        if (!showRationale) {
                            //never ask again
                            showPermissionAlertGpsFilePhone(true, permissions as Array<String>)
                            return

                        }
                    }
                }
                showPermissionAlertGpsFilePhone(false, permissions as Array<String>)
            }
        }

    }
}
