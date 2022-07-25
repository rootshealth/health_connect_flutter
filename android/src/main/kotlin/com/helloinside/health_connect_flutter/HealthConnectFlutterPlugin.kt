package com.helloinside.health_connect_flutter

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.PluginRegistry
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit


class HealthConnectFlutterPlugin : FlutterPlugin, ActivityAware {

    private lateinit var healthConnectPluginImpl: HealthConnectPluginImpl

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        healthConnectPluginImpl = HealthConnectPluginImpl(null)
        Pigeon.HealthConnectPlugin.setup(
            binding.binaryMessenger,
            healthConnectPluginImpl
        )
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        Pigeon.HealthConnectPlugin.setup(
            binding.binaryMessenger,
            null
        )
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        healthConnectPluginImpl.activityPluginBinding = activityPluginBinding
    }

    override fun onDetachedFromActivity() {
        healthConnectPluginImpl.activityPluginBinding = null
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        healthConnectPluginImpl.activityPluginBinding = activityPluginBinding
    }

    override fun onDetachedFromActivityForConfigChanges() {
        healthConnectPluginImpl.activityPluginBinding = null
    }

}

class HealthConnectPluginImpl(
    var activityPluginBinding: ActivityPluginBinding?
) :
    Pigeon.HealthConnectPlugin {

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
        .build()

    override fun requestActivityRecognitionPermission(result: Pigeon.Result<Pigeon.PermissionResult>?) {
        Log.e(TAG, "requestActivityRecognitionPermission")
        if (hasActivityRecognitionPermission()) {
            result?.success(
                Pigeon.PermissionResult.Builder()
                    .setPermissionType(Pigeon.PermissionType.activityRecognition)
                    .setPermissionStatus(Pigeon.PermissionStatus.granted)
                    .build()
            )
            return
        }

        activityPluginBinding?.let { activityPluginBinding ->
            val listener =
                PluginRegistry.RequestPermissionsResultListener { requestCode, _, grantResults ->
                    when (requestCode) {
                        Permission.Code.ACTIVITY_RECOGNITION -> {
                            val permissionGranted = grantResults.isNotEmpty() &&
                                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                            val permissionStatus = when (permissionGranted) {
                                true -> Pigeon.PermissionStatus.granted
                                else -> Pigeon.PermissionStatus.denied
                            }
                            val permissionResult = Pigeon.PermissionResult.Builder()
                                .setPermissionType(Pigeon.PermissionType.activityRecognition)
                                .setPermissionStatus(permissionStatus)
                                .build()
                            result?.success(permissionResult)
                            permissionGranted
                        }
                        else -> {
                            result?.error(Exception("Something went wrong! Error code could not be retrieved"))
                            false
                        }
                    }
                }
            activityPluginBinding.removeRequestPermissionsResultListener(listener)
            activityPluginBinding.addRequestPermissionsResultListener(listener)

            ActivityCompat.requestPermissions(
                activityPluginBinding.activity,
                arrayOf(Permission.Type.ACTIVITY_RECOGNITION_PERMISSION),
                Permission.Code.ACTIVITY_RECOGNITION
            )
            return
        }
        Log.e(TAG, "Activity was not attached")
    }

    override fun hasActivityRecognitionPermission(): Boolean {
        Log.e(TAG, "hasActivityRecognitionPermission")
        activityPluginBinding?.activity?.let {
            return ContextCompat.checkSelfPermission(
                it,
                Permission.Type.ACTIVITY_RECOGNITION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }
        Log.e(TAG, "Activity was not properly attached")
        return false
    }

    override fun requestOAuthPermission(result: Pigeon.Result<Pigeon.PermissionResult>?) {
        Log.e(TAG, "requestOAuthPermission")
        if (hasOAuthPermission()) {
            result?.success(
                Pigeon.PermissionResult.Builder()
                    .setPermissionType(Pigeon.PermissionType.oAuth)
                    .setPermissionStatus(Pigeon.PermissionStatus.granted)
                    .build()
            )
            return
        }

        activityPluginBinding?.let { activityPluginBinding ->
            val listener =
                PluginRegistry.RequestPermissionsResultListener { requestCode, _, grantResults ->
                    when (requestCode) {
                        Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                            val permissionGranted = grantResults.isNotEmpty() &&
                                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                            val permissionStatus = when (permissionGranted) {
                                true -> Pigeon.PermissionStatus.granted
                                else -> Pigeon.PermissionStatus.denied
                            }
                            val permissionResult = Pigeon.PermissionResult.Builder()
                                .setPermissionType(Pigeon.PermissionType.oAuth)
                                .setPermissionStatus(permissionStatus)
                                .build()
                            result?.success(permissionResult)
                            permissionGranted
                        }
                        else -> {
                            result?.error(Exception("Something went wrong! Error code could not be retrieved"))
                            false
                        }
                    }
                }
            activityPluginBinding.removeRequestPermissionsResultListener(listener)
            activityPluginBinding.addRequestPermissionsResultListener(listener)

            val account =
                GoogleSignIn.getAccountForExtension(activityPluginBinding.activity, fitnessOptions)
            GoogleSignIn.requestPermissions(
                activityPluginBinding.activity,
                Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            )
            return
        }
        Log.e(TAG, "Activity was not attached")
    }

    override fun hasOAuthPermission(): Boolean {
        activityPluginBinding?.activity?.let { activity ->
            val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            return GoogleSignIn.hasPermissions(account, fitnessOptions)
        }
        Log.e(TAG, "Activity was not properly attached")
        return false
    }

    override fun openSettings() {
        activityPluginBinding?.activity?.let {
            val intent = Intent()
            val uri = Uri.fromParts("package", it.packageName, null)
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).data = uri
            it.startActivity(intent)
            return
        }
        Log.e(TAG, "Activity was not properly attached")
    }

    override fun disconnect(result: Pigeon.Result<Void>?) {
        activityPluginBinding?.activity?.let {
            if (hasOAuthPermission()) {
                Fitness.getConfigClient(it, GoogleSignIn.getAccountForExtension(it, fitnessOptions))
                    .disableFit()
                    .addOnSuccessListener {
                        Log.d(TAG, "Disabled Google Fit")
                        result?.success(null)
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "There was an error disabling Google Fit", e)
                        result?.error(Exception(e))
                    }
                return
            }
            result?.success(null)
        }
        Log.e(TAG, "Activity was not properly attached")
    }


    override fun getHealthConnectData(result: Pigeon.Result<Pigeon.HealthConnectData>?) {
        activityPluginBinding?.activity?.let { activity ->
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
                .build()
            val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                    activity, // your activity
                    Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions
                )
                return
            }
            val endDateTime = LocalDateTime.now()
            val startDateTime = endDateTime.minusYears(1)
            val endSeconds = endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
            val startSeconds = startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()

            val readRequest = DataReadRequest.Builder()
                .read(DataType.TYPE_HEIGHT)
                .read(DataType.TYPE_WEIGHT)
                .enableServerQueries()
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                //.bucketByTime(1, TimeUnit.DAYS)
                .build()

            //TODO replace in the future with Health Connect
            Fitness.getHistoryClient(activity, account)
                .readData(readRequest)
                .addOnSuccessListener {
                    Log.d(TAG, it.dataSets.toString())

                    val healthConnectDataBuilder = Pigeon.HealthConnectData.Builder()

                    val heightDataSet = it.getDataSet(DataType.TYPE_HEIGHT)
                    if (!heightDataSet.isEmpty && heightDataSet.dataPoints.isNotEmpty()) {
                        val heightValue =
                            heightDataSet.dataPoints.first().getValue(Field.FIELD_HEIGHT)
                        val height = heightValue.asFloat().toDouble()
                        healthConnectDataBuilder.setHeight(height)
                    }

                    val weightDataSet = it.getDataSet(DataType.TYPE_WEIGHT)
                    if (!weightDataSet.isEmpty && weightDataSet.dataPoints.isNotEmpty()) {
                        val weightValue =
                            weightDataSet.dataPoints.first().getValue(Field.FIELD_WEIGHT)
                        val weight = weightValue.asFloat().toDouble()
                        healthConnectDataBuilder.setWeight(weight)
                    }

                    val healthConnectData = healthConnectDataBuilder.build();
                    result?.success(healthConnectData)
                }
                .addOnFailureListener {
                    Log.e(TAG, "OnFailure()", it)
                    result?.error(it)
                }
                .addOnCompleteListener {
                    //TODO
                }
        }
        Log.e(TAG, "Activity was not attached")
    }

    override fun getHealthConnectWorkoutData(result: Pigeon.Result<Pigeon.HealthConnectWorkoutData>?) {
        activityPluginBinding?.activity?.let { activity ->

            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
                .build()

            val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                    activity, // your activity
                    Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions
                )
                return
            }
            val endDateTime = LocalDateTime.now()
            val startDateTime = endDateTime.minusYears(1)
            val endSeconds = endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
            val startSeconds = startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()

            val readRequest = DataReadRequest.Builder()
                .read(DataType.TYPE_WORKOUT_EXERCISE)
                .enableServerQueries()
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .build()

            //TODO replace in the future with Health Connect
            Fitness.getHistoryClient(activity, account)
                .readData(readRequest)
                .addOnSuccessListener {
                    Log.d(TAG, it.dataSets.toString())

                    val healthConnectWorkoutDataBuilder = Pigeon.HealthConnectWorkoutData.Builder()

                    val workoutDataSet = it.getDataSet(DataType.TYPE_WORKOUT_EXERCISE);
                    if (!workoutDataSet.isEmpty && workoutDataSet.dataPoints.isNotEmpty()) {
                        val workoutValues = workoutDataSet.dataPoints
                        Log.d(TAG, workoutValues.toString());
                        //healthConnectDataBuilder.setWeight(weight)
                    }

                    val healthConnectData = healthConnectWorkoutDataBuilder.build();
                    result?.success(healthConnectData)
                }
                .addOnFailureListener {
                    Log.e(TAG, "OnFailure()", it)
                    result?.error(it)
                }
                .addOnCompleteListener {
                    //TODO
                }
            return
        }
        Log.e(TAG, "Activity was not attached")
    }

}