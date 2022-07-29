package com.helloinside.health_connect_flutter

import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

class HealthConnectFlutterPlugin : FlutterPlugin, ActivityAware {

    private lateinit var healthConnectHostApiImpl: HealthConnectHostApiImpl
    private lateinit var healthConnectFlutterApi: Pigeon.HealthConnectFlutterApi

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        healthConnectHostApiImpl = HealthConnectHostApiImpl(null, null)
        Pigeon.HealthConnectHostApi.setup(
            binding.binaryMessenger,
            healthConnectHostApiImpl
        )
        healthConnectFlutterApi = Pigeon.HealthConnectFlutterApi(binding.binaryMessenger)
        healthConnectHostApiImpl.healthConnectFlutterApi = healthConnectFlutterApi
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        Pigeon.HealthConnectHostApi.setup(
            binding.binaryMessenger,
            null
        )
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        healthConnectHostApiImpl.activityPluginBinding = activityPluginBinding
    }

    override fun onDetachedFromActivity() {
        healthConnectHostApiImpl.activityPluginBinding = null
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        healthConnectHostApiImpl.activityPluginBinding = activityPluginBinding
    }

    override fun onDetachedFromActivityForConfigChanges() {
        healthConnectHostApiImpl.activityPluginBinding = null
    }

}