package com.helloinside.health_connect_flutter

import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import timber.log.Timber

class HealthConnectFlutterPlugin : FlutterPlugin, ActivityAware {

    private lateinit var healthConnectHostApiImpl: HealthConnectHostApiImpl

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        // TODO enable once Google Fit is ready
        //if (BuildConfig.DEBUG) {
        Timber.plant(Timber.DebugTree())
        //}
        healthConnectHostApiImpl =
            HealthConnectHostApiImpl(
                null,
                null,
                null,
            )
        Pigeon.HealthConnectHostApi.setup(
            binding.binaryMessenger,
            healthConnectHostApiImpl
        )
        healthConnectHostApiImpl.healthConnectFlutterApi =
            Pigeon.HealthConnectFlutterApi(binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        Pigeon.HealthConnectHostApi.setup(
            binding.binaryMessenger,
            null
        )
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) =
        setupHealthConnectHostApiImpl(activityPluginBinding)

    override fun onDetachedFromActivity() = cleanUp()

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) =
        setupHealthConnectHostApiImpl(activityPluginBinding)

    override fun onDetachedFromActivityForConfigChanges() = cleanUp()

    private fun setupHealthConnectHostApiImpl(activityPluginBinding: ActivityPluginBinding) {
        healthConnectHostApiImpl.activityPluginBinding = activityPluginBinding
        healthConnectHostApiImpl.permissionManager = PermissionManager(activityPluginBinding)
    }

    private fun cleanUp() {
        healthConnectHostApiImpl.permissionManager?.cleanUp()
        healthConnectHostApiImpl.permissionManager = null
        healthConnectHostApiImpl.activityPluginBinding = null
    }

}