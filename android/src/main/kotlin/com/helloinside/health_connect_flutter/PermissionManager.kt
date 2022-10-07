package com.helloinside.health_connect_flutter

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.PluginRegistry
import timber.log.Timber

class PermissionManager(
    private val activityPluginBinding: ActivityPluginBinding,
) : PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {

    interface Callback {
        fun onSuccess(permissionResult: Pigeon.PermissionResult)
        fun onError(exception: Exception)
    }

    var callback: Callback? = null

    init {
        activityPluginBinding.addRequestPermissionsResultListener(this)
        activityPluginBinding.addActivityResultListener(this)
    }

    fun cleanUp() {
        activityPluginBinding.removeRequestPermissionsResultListener(this)
        activityPluginBinding.removeActivityResultListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        Timber.tag(TAG)
            .d("onRequestPermissionsResult: requestCode $requestCode permissions: ${permissions.map { it }} grantResults: ${grantResults.map { it.toString() }}")

        when (requestCode) {
            Permission.Code.ACTIVITY_RECOGNITION -> {
                val permissionGranted = grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionStatus = when (permissionGranted) {
                    true -> Pigeon.PermissionStatus.GRANTED
                    else -> Pigeon.PermissionStatus.DENIED
                }
                val permissionResult = Pigeon.PermissionResult.Builder()
                    .setPermissionType(Pigeon.PermissionType.ACTIVITY_RECOGNITION)
                    .setPermissionStatus(permissionStatus)
                    .build()
                callback?.onSuccess(permissionResult)
            }
            else -> {
                val error =
                    "onRequestPermissionsResult:  requestCode $requestCode permissions: ${permissions.map { it }} grantResults: ${grantResults.map { it.toString() }}"
                Timber.tag(TAG).e(error)
                callback?.onError(Exception(error))
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        Timber.tag(TAG)
            .d("onActivityResult: requestCode $requestCode resultCode: $resultCode")
        if (resultCode == Activity.RESULT_OK && requestCode == Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            val permissionResult = Pigeon.PermissionResult.Builder()
                .setPermissionType(Pigeon.PermissionType.O_AUTH)
                .setPermissionStatus(Pigeon.PermissionStatus.GRANTED)
                .build()
            callback?.onSuccess(permissionResult)
            return true
        }

        val error =
            "onActivityResult: Something went wrong! Error code could not be retrieved! requestCode $requestCode resultCode: $resultCode"
        Timber.tag(TAG).e(error)
        callback?.onError(Exception(error))
        return false
    }
}