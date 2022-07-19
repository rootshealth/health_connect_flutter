package com.helloinside.health_connect_flutter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


class HealthConnectFlutterPlugin : FlutterPlugin, ActivityAware {

    private lateinit var healthConnectPluginImpl: HealthConnectPluginImpl

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        healthConnectPluginImpl = HealthConnectPluginImpl(null);
        Pigeon.HealthConnectPlugin.setup(
            flutterPluginBinding.binaryMessenger,
            healthConnectPluginImpl
        )
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {}

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        healthConnectPluginImpl.activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        healthConnectPluginImpl.activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        healthConnectPluginImpl.activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        healthConnectPluginImpl.activity = null
    }

}

class HealthConnectPluginImpl(var activity: Activity?) : Pigeon.HealthConnectPlugin {

    override fun initialize(
        params: Pigeon.HealthConnectInitializationParams,
        result: Pigeon.Result<Void>?
    ) {
        TODO("Not yet implemented")
    }

    override fun requestPermission(result: Pigeon.Result<Void>?) {
        Log.e(Permission.Group.LOG_TAG, "requestPermission")
        if (activity == null) {
            Log.e(Permission.Group.LOG_TAG, "Activity was not attached")
            return
        }
        when {
            hasPermission() -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(
                activity!!,
                Permission.Group.ACTIVITY_RECOGNITION_PERMISSION
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //Alert Dialog that will take user to settings where he can manually give the permissions
                val alert = AlertDialog.Builder(activity!!)
                    .setMessage("You have permanently disabled the permission")
                    .setPositiveButton(
                        "Go to Settings"
                    ) { _, _ -> openSettings() }.setNegativeButton("Don't Go", null)
                    .setCancelable(false).create()
                alert.setTitle("Give permission manually")
                alert.show()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Permission.Group.ACTIVITY_RECOGNITION_PERMISSION),
                    Permission.Group.ACTIVITY_RECOGNITION_CODE
                )
            }
        }
    }

    override fun getHealthConnectData(result: Pigeon.Result<Pigeon.HealthConnectData>?) {
        result?.success(Pigeon.HealthConnectData.Builder().setId("1").setData("Test5").build())
    }

    override fun hasPermission(): Boolean {
        if (activity == null) {
            Log.e(Permission.Group.LOG_TAG, "Activity was not properly attached")
            return false
        }
        return ContextCompat.checkSelfPermission(
            activity!!,
            Permission.Group.ACTIVITY_RECOGNITION_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openSettings() {
        val intent = Intent()
        val uri = Uri.fromParts("package", activity!!.packageName, null)
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).data = uri
        activity!!.startActivity(intent)
    }

}