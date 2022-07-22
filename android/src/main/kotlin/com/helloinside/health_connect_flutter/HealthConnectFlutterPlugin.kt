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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit


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

    override fun requestPermission(result: Pigeon.Result<Void>?) {
        Log.e(LOG_TAG, "requestPermission")
        if (activity == null) {
            Log.e(LOG_TAG, "Activity was not attached")
            return
        }
        when {
            hasPermission() -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(
                activity!!,
                Permission.Type.ACTIVITY_RECOGNITION_PERMISSION
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
                    arrayOf(Permission.Type.ACTIVITY_RECOGNITION_PERMISSION),
                    Permission.Code.ACTIVITY_RECOGNITION
                )
            }
        }
    }

    override fun requestPermission2() {
        Log.e(LOG_TAG, "requestPermission2")
        if (activity == null) {
            Log.e(LOG_TAG, "Activity was not attached")
            return
        }
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Permission.Type.ACTIVITY_RECOGNITION_PERMISSION),
            Permission.Code.ACTIVITY_RECOGNITION
        )
    }

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
        .build();

    override fun getHealthConnectData(result: Pigeon.Result<Pigeon.HealthConnectData>?) {
        if (activity == null) {
            Log.e(LOG_TAG, "Activity was not attached")
            return
        }
        val account = GoogleSignIn.getAccountForExtension(activity!!, fitnessOptions)
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity!!, // your activity
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
            .read(DataType.TYPE_WORKOUT_EXERCISE)
            .enableServerQueries()
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            //.bucketByTime(1, TimeUnit.DAYS)
            .build()

        //TODO replace in the future with Health Connect
        Fitness.getHistoryClient(activity!!, account)
            .readData(readRequest)
            .addOnSuccessListener {
                Log.d(LOG_TAG, it.dataSets.toString())

                val healthConnectDataBuilder = Pigeon.HealthConnectData.Builder()

                val heightDataSet = it.getDataSet(DataType.TYPE_HEIGHT)
                if (!heightDataSet.isEmpty && heightDataSet.dataPoints.isNotEmpty()) {
                    val heightValue = heightDataSet.dataPoints.first().getValue(Field.FIELD_HEIGHT)
                    val height = heightValue.asFloat().toDouble()
                    healthConnectDataBuilder.setHeight(height)
                }

                val weightDataSet = it.getDataSet(DataType.TYPE_WEIGHT)
                if (!weightDataSet.isEmpty && weightDataSet.dataPoints.isNotEmpty()) {
                    val weightValue = weightDataSet.dataPoints.first().getValue(Field.FIELD_WEIGHT)
                    val weight = weightValue.asFloat().toDouble()
                    healthConnectDataBuilder.setWeight(weight)
                }

                val workoutDataSet = it.getDataSet(DataType.TYPE_WORKOUT_EXERCISE);
                if (!workoutDataSet.isEmpty && workoutDataSet.dataPoints.isNotEmpty()) {
                    val workoutValues = workoutDataSet.dataPoints
                    Log.d(LOG_TAG, workoutValues.toString());
                    //healthConnectDataBuilder.setWeight(weight)
                }

                val healthConnectData = healthConnectDataBuilder.build();
                result?.success(healthConnectData)
            }
            .addOnFailureListener {
                Log.e(LOG_TAG, "OnFailure()", it)
                result?.error(it)
            }
            .addOnCompleteListener {
                //TODO
            }
    }

    override fun hasPermission(): Boolean {
        if (activity == null) {
            Log.e(LOG_TAG, "Activity was not properly attached")
            return false
        }
        return ContextCompat.checkSelfPermission(
            activity!!,
            Permission.Type.ACTIVITY_RECOGNITION_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun openSettings() {
        val intent = Intent()
        val uri = Uri.fromParts("package", activity!!.packageName, null)
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).data = uri
        activity!!.startActivity(intent)
    }

}