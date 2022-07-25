package com.helloinside.health_connect_flutter

import android.app.Activity
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

    override fun requestPermission() {
        Log.e(LOG_TAG, "requestPermission")
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Permission.Type.ACTIVITY_RECOGNITION_PERMISSION),
                Permission.Code.ACTIVITY_RECOGNITION
            )
            return
        }
        Log.e(LOG_TAG, "Activity was not attached")
    }

    override fun hasPermission(): Boolean {
        activity?.let {
            return ContextCompat.checkSelfPermission(
                it,
                Permission.Type.ACTIVITY_RECOGNITION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }
        Log.e(LOG_TAG, "Activity was not properly attached")
        return false
    }

    override fun openSettings() {
        activity?.let {
            val intent = Intent()
            val uri = Uri.fromParts("package", activity!!.packageName, null)
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).data = uri
            it.startActivity(intent)
            return
        }
        Log.e(LOG_TAG, "Activity was not properly attached")
    }

    override fun disconnect(result: Pigeon.Result<Void>?) {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
            .build()

        val account = GoogleSignIn.getAccountForExtension(activity!!, fitnessOptions)
        if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            activity?.let {
                Fitness.getConfigClient(it, GoogleSignIn.getAccountForExtension(it, fitnessOptions))
                    .disableFit()
                    .addOnSuccessListener {
                        Log.d(LOG_TAG, "Disabled Google Fit")
                        result?.success(null)
                    }
                    .addOnFailureListener { e ->
                        Log.d(LOG_TAG, "There was an error disabling Google Fit", e)
                        result?.error(Exception(e))
                    }
                return
            }
            Log.e(LOG_TAG, "Activity was not properly attached")
            return
        }
        result?.success(null)
    }

    override fun getHealthConnectData(result: Pigeon.Result<Pigeon.HealthConnectData>?) {
        if (activity == null) {
            Log.e(LOG_TAG, "Activity was not attached")
            return
        }
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .build()

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

    override fun getHealthConnectWorkoutData(result: Pigeon.Result<Pigeon.HealthConnectWorkoutData>?) {
        if (activity == null) {
            Log.e(LOG_TAG, "Activity was not attached")
            return
        }

        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
            .build()

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
            .read(DataType.TYPE_WORKOUT_EXERCISE)
            .enableServerQueries()
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            .build()

        //TODO replace in the future with Health Connect
        Fitness.getHistoryClient(activity!!, account)
            .readData(readRequest)
            .addOnSuccessListener {
                Log.d(LOG_TAG, it.dataSets.toString())

                val healthConnectWorkoutDataBuilder = Pigeon.HealthConnectWorkoutData.Builder()

                val workoutDataSet = it.getDataSet(DataType.TYPE_WORKOUT_EXERCISE);
                if (!workoutDataSet.isEmpty && workoutDataSet.dataPoints.isNotEmpty()) {
                    val workoutValues = workoutDataSet.dataPoints
                    Log.d(LOG_TAG, workoutValues.toString());
                    //healthConnectDataBuilder.setWeight(weight)
                }

                val healthConnectData = healthConnectWorkoutDataBuilder.build();
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

}