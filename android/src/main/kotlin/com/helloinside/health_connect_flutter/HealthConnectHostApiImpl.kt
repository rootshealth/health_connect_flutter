package com.helloinside.health_connect_flutter

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.Session
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.PluginRegistry
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class HealthConnectHostApiImpl(
    var activityPluginBinding: ActivityPluginBinding?,
    var healthConnectFlutterApi: Pigeon.HealthConnectFlutterApi?
) :
    Pigeon.HealthConnectHostApi {

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
        .build()

    override fun requestActivityRecognitionPermission(result: Pigeon.Result<Pigeon.PermissionResult>?) {
        Log.e(TAG, "requestActivityRecognitionPermission")
        // https://stackoverflow.com/questions/65666404/java-lang-illegalstateexception-reply-already-submitted-when-trying-to-call
        var requestInProgress = false
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
                            if (requestInProgress) {
                                requestInProgress = false
                                result?.success(permissionResult)
                            }
                            return@RequestPermissionsResultListener permissionGranted
                        }
                        else -> {
                            if (requestInProgress) {
                                requestInProgress = false
                                result?.error(Exception("Something went wrong! Error code could not be retrieved"))
                            }
                            return@RequestPermissionsResultListener false
                        }
                    }
                }
            activityPluginBinding.removeRequestPermissionsResultListener(listener)
            activityPluginBinding.addRequestPermissionsResultListener(listener)

            ActivityCompat.requestPermissions(
                activityPluginBinding.activity,
                arrayOf(
                    Permission.Type.ACTIVITY_RECOGNITION_PERMISSION,
                    Permission.Type.ACCESS_FINE_LOCATION
                ),
                Permission.Code.ACTIVITY_RECOGNITION
            )
            requestInProgress = true
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
        var requestInProgress = false
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
                PluginRegistry.ActivityResultListener { requestCode, resultCode, _ ->
                    if (resultCode == Activity.RESULT_OK && requestCode == Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                        val permissionResult = Pigeon.PermissionResult.Builder()
                            .setPermissionType(Pigeon.PermissionType.oAuth)
                            .setPermissionStatus(Pigeon.PermissionStatus.granted)
                            .build()
                        if (requestInProgress) {
                            requestInProgress = false
                            result?.success(permissionResult)
                        }
                        return@ActivityResultListener true
                    }
                    if (requestInProgress) {
                        requestInProgress = false
                        result?.error(Exception("Something went wrong! Error code could not be retrieved"))
                    }
                    return@ActivityResultListener false
                }

            activityPluginBinding.removeActivityResultListener(listener)
            activityPluginBinding.addActivityResultListener(listener)

            val account =
                GoogleSignIn.getAccountForExtension(activityPluginBinding.activity, fitnessOptions)
            GoogleSignIn.requestPermissions(
                activityPluginBinding.activity,
                Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            )
            requestInProgress = true
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
                    activity,
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
                        val heightInMeters = heightValue.asFloat().toDouble()
                        val heightInCm = heightInMeters * 100
                        healthConnectDataBuilder.setHeight(heightInCm)
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

    override fun getHealthConnectWorkoutsData(
        predicate: Pigeon.Predicate,
        result: Pigeon.Result<List<Pigeon.HealthConnectWorkoutData>>?
    ) {
        var requestInProgress = false
        activityPluginBinding?.activity?.let { activity ->
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
                .build()

            val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                    activity,
                    Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions
                )
                return
            }

//            val endDateTime = LocalDateTime.now()
//            val startDateTime = endDateTime.minusYears(1)
//            val endSeconds = endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
//            val startSeconds = startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()

            val readRequest = SessionReadRequest.Builder()
                .setTimeInterval(
                    predicate.startDateInMsSinceEpoch,
                    predicate.endDateInMsSinceEpoch,
                    TimeUnit.MILLISECONDS
                )
                .read(DataType.TYPE_WORKOUT_EXERCISE)
                .includeActivitySessions()
                .enableServerQueries()
                .readSessionsFromAllApps()
                .build()

            //TODO replace in the future with Health Connect
            Fitness.getSessionsClient(activity, account)
                .readSession(readRequest)
                .addOnSuccessListener { response ->
                    requestInProgress = true
                    val sessions = response.sessions
                    Log.d(TAG, "Number of returned sessions is: ${sessions.size}")
                    val healthConnectWorkouts = mutableListOf<Pigeon.HealthConnectWorkoutData>()
                    for (session in sessions) {
                        logSession(session)
                        val dataSets = response.getDataSet(session)
                        for (dataSet in dataSets) {
                            logDataSet(dataSet)
                        }
                        healthConnectWorkouts.add(session.toHealthConnectWorkoutData())
                    }
                    if (requestInProgress) {
                        requestInProgress = false
                        result?.success(healthConnectWorkouts)
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "OnFailure()", it)
                    if (requestInProgress) {
                        requestInProgress = false
                        result?.error(it)
                    }
                }
            return
        }
        Log.e(TAG, "Activity was not attached")
    }

    override fun subscribeToHealthConnectWorkoutsData() {
//        for (i in 1..5L) {
//            Thread.sleep(100) // pret
//            // end we are computing it
//            val healthConnectWorkoutDataBuilder =
//                Pigeon.HealthConnectWorkoutData.Builder()
//
//            val healthConnectWorkoutData =
//                healthConnectWorkoutDataBuilder
//                    .setUuid("1")
//                    .setIdentifier("idetifier")
//                    .setStartTimestamp(0 + i)
//                    .setEndTimestamp(1 + i)
//                    .setDuration(
//                        Duration.ofHours(i).toHours()
//                    ).build()
//            healthConnectFlutterApi?.onWorkoutDataUpdated(healthConnectWorkoutData, null)
//        }
//        return

        activityPluginBinding?.activity?.let { activity ->
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
                .build()

            val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                    activity,
                    Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions
                )
                return
            }

            val intent = Intent(activity, WorkoutSessionBroadcastReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_MUTABLE)
            Fitness.getSessionsClient(activity, account).registerForSessions(pendingIntent)

            val receiver: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val session = intent?.getParcelableExtra<Session>(intentExtraName)
                    Log.e(TAG, "BroadcastReceiver: session: $session")
                    session?.let {
                        val reply = Pigeon.HealthConnectFlutterApi.Reply<Void> { }
                        val healthConnectWorkoutData = it.toHealthConnectWorkoutData()
                        healthConnectFlutterApi?.onWorkoutDataUpdated(
                            healthConnectWorkoutData,
                            reply
                        )
                    }
                }
            }

            try {
                activity.registerReceiver(receiver, intentFilter)
            } catch (e: IllegalArgumentException) {
                Log.d(TAG, e.toString())
            }
            return
        }
        Log.e(TAG, "Activity was not attached")
    }

}