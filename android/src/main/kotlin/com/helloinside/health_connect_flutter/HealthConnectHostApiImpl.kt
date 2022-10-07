package com.helloinside.health_connect_flutter

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.data.Session
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import com.helloinside.health_connect_flutter.Pigeon.PermissionResult
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class OAuthPermissionException :
    Exception("OAuth permission not granted! Please request permission first!")

class HealthConnectHostApiImpl(
    var activityPluginBinding: ActivityPluginBinding?,
    var healthConnectFlutterApi: Pigeon.HealthConnectFlutterApi?,
    var permissionManager: PermissionManager?,
) :
    Pigeon.HealthConnectHostApi {

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
        .build()

    private var receiver: BroadcastReceiver? = null

    override fun requestPermissions(result: Pigeon.Result<Pigeon.PermissionResult>?) {
        Timber.tag(TAG).d("requestPermissions")
        activityPluginBinding?.apply {
            permissionManager?.callback = object : PermissionManager.Callback {
                override fun onSuccess(permissionResult: PermissionResult) {
                    result?.success(permissionResult)
                }

                override fun onError(exception: Exception) {
                    result?.error(exception)
                }
            }
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Permission.Type.ACTIVITY_RECOGNITION_PERMISSION,
                    Permission.Type.BODY_SENSORS
                ),
                Permission.Code.ACTIVITY_RECOGNITION
            )
            return
        }
        Timber.tag(TAG).e("requestPermissions: Activity was not attached")
    }

    override fun hasActivityRecognitionPermission(): Boolean {
        activityPluginBinding?.activity?.let {
            val permissionGranted = ContextCompat.checkSelfPermission(
                it,
                Permission.Type.ACTIVITY_RECOGNITION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
            Timber.tag(TAG).d("hasActivityRecognitionPermission: $permissionGranted")
            return permissionGranted
        }
        Timber.tag(TAG).e("hasActivityRecognitionPermission: Activity was not attached")
        return false
    }

    override fun hasBodySensorsPermission(): Boolean {
        activityPluginBinding?.activity?.let {
            val permissionGranted = ContextCompat.checkSelfPermission(
                it,
                Permission.Type.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED
            Timber.tag(TAG).d("hasBodySensorPermission: $permissionGranted")
            return permissionGranted
        }
        Timber.tag(TAG).e("hasBodySensorPermission: Activity was not attached")
        return false
    }

    override fun requestOAuthPermission(result: Pigeon.Result<Pigeon.PermissionResult>?) {
        Timber.tag(TAG).d("requestOAuthPermission")
        activityPluginBinding?.apply {
            permissionManager?.callback = object : PermissionManager.Callback {
                override fun onSuccess(permissionResult: PermissionResult) {
                    result?.success(permissionResult)
                }

                override fun onError(exception: Exception) {
                    result?.error(exception)
                }
            }
            val account =
                GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            GoogleSignIn.requestPermissions(
                activity,
                Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            )
            return
        }
        Timber.tag(TAG).e("requestOAuthPermission: Activity was not attached")
    }

    override fun hasOAuthPermission(): Boolean {
        activityPluginBinding?.activity?.let { activity ->
            val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            val hasPermissions = GoogleSignIn.hasPermissions(account, fitnessOptions)
            Timber.tag(TAG).d("hasOAuthPermission: $hasPermissions")
            return GoogleSignIn.hasPermissions(account, fitnessOptions)
        }
        Timber.tag(TAG).e("hasOAuthPermission: Activity was not attached")
        return false
    }

    override fun openSettings() {
        Timber.tag(TAG).d("openSettings")
        activityPluginBinding?.activity?.let {
            val intent = Intent()
            val uri = Uri.fromParts("package", it.packageName, null)
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).data = uri
            it.startActivity(intent)
            return
        }
        Timber.tag(TAG).e("openSettings: Activity was not attached")
    }

    override fun disconnect(result: Pigeon.Result<Boolean>?) {
        Timber.tag(TAG).d("disconnect")
        activityPluginBinding?.activity?.let { activity ->

            try {
                receiver?.let { activity.unregisterReceiver(it) }
            } catch (e: IllegalArgumentException) {
                Timber.tag(TAG).d(e.toString())
            }

            if (hasOAuthPermission()) {
                Fitness.getConfigClient(
                    activity,
                    GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
                )
                    .disableFit()
                    .continueWithTask { _ ->
                        // https://github.com/android/fit-samples/issues/28
                        val signInOptions = GoogleSignInOptions.Builder()
                            .addExtension(fitnessOptions)
                            .build()
                        GoogleSignIn.getClient(activity, signInOptions).revokeAccess()
                    }
                    .addOnFailureListener { e ->
                        if (e is ApiException && e.statusCode == CommonStatusCodes.SIGN_IN_REQUIRED) {
                            Timber.tag(TAG).d("disconnect: Disabled Google Fit")
                            result?.success(true)
                        } else {
                            Timber.tag(TAG)
                                .e(e, "disconnect: There was an error disabling Google Fit")
                            result?.error(Exception(e))
                        }
                    }
                return
            }
            result?.success(true)
        }
        Timber.tag(TAG).e("disconnect: Activity was not attached")
    }

    override fun getHealthConnectData(result: Pigeon.Result<Pigeon.HealthConnectData>?) {
        Timber.tag(TAG).d("getHealthConnectData")
        activityPluginBinding?.activity?.let { activity ->
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
                .build()
            val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                result?.error(OAuthPermissionException())
//                GoogleSignIn.requestPermissions(
//                    activity,
//                    Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
//                    account,
//                    fitnessOptions
//                )
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
                    //Timber.tag(TAG).d(it.dataSets.toString())
                    //Timber.tag(TAG).d(it.buckets.toString())

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


                    val heartBuckets = it.buckets
                    if (heartBuckets.isNotEmpty()) {
                        logBuckets(heartBuckets)
                    }

                    val healthConnectData = healthConnectDataBuilder.build()
                    logHealthData(healthConnectData)
                    result?.success(healthConnectData)
                }
                .addOnFailureListener {
                    Timber.tag(TAG).e(it, "getHealthConnectData: OnFailure()")
                    result?.error(it)
                }
            return
        }
        Timber.tag(TAG).e("getHealthConnectData: Activity was not attached")
    }

    override fun getHealthConnectWorkoutsData(
        predicate: Pigeon.Predicate,
        result: Pigeon.Result<List<Pigeon.HealthConnectWorkoutData>>?
    ) {
        Timber.tag(TAG).d("getHealthConnectWorkoutsData")
        var requestInProgress = false
        activityPluginBinding?.activity?.let { activity ->
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
                .build()

            val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                result?.error(OAuthPermissionException())
//                GoogleSignIn.requestPermissions(
//                    activity,
//                    Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
//                    account,
//                    fitnessOptions
//                )
                return
            }

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
                    Timber.tag(TAG).d(
                        "getHealthConnectWorkoutsData: Number of returned sessions is: %s",
                        sessions.size
                    )
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
                    Timber.tag(TAG).e(it, "getHealthConnectWorkoutsData: OnFailure()")
                    if (requestInProgress) {
                        requestInProgress = false
                        result?.error(it)
                    }
                }
            return
        }
        Timber.tag(TAG).e("getHealthConnectWorkoutsData: Activity was not attached")
    }

    override fun subscribeToHealthConnectWorkoutsData() {
        Timber.tag(TAG).d("subscribeToHealthConnectWorkoutsData: started")
        activityPluginBinding?.activity?.let { activity ->
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
                .build()

            val account = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                Timber.tag(TAG)
                    .e("subscribeToHealthConnectWorkoutsData: Permission not granted! Please request permission first!")
//                GoogleSignIn.requestPermissions(
//                    activity,
//                    Permission.Code.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
//                    account,
//                    fitnessOptions
//                )
                return
            }

            val intent = Intent(activity, WorkoutSessionBroadcastReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_MUTABLE)
            Fitness.getSessionsClient(activity, account).registerForSessions(pendingIntent)
            Timber.tag(TAG)
                .d(
                    "subscribeToHealthConnectWorkoutsData: registerForSessions with intent: %s",
                    pendingIntent.toString()
                )

            try {
                if (receiver == null) {
                    receiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            val session = intent?.getParcelableExtra<Session>(intentExtraName)
                            Timber.tag(TAG).d(
                                "subscribeToHealthConnectWorkoutsData: BroadcastReceiver: session: %s",
                                session
                            )
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
                    Timber.tag(TAG)
                        .d("subscribeToHealthConnectWorkoutsData: BroadcastReceiver created");
                } else {
                    activity.unregisterReceiver(receiver)
                    Timber.tag(TAG)
                        .d("subscribeToHealthConnectWorkoutsData: BroadcastReceiver unregistered");
                }
                activity.registerReceiver(receiver, intentFilter)
                Timber.tag(TAG)
                    .d("subscribeToHealthConnectWorkoutsData: BroadcastReceiver registered");
            } catch (e: IllegalArgumentException) {
                Timber.tag(TAG).d(e.toString())
            }
            return
        }
        Timber.tag(TAG).e("subscribeToHealthConnectWorkoutsData: Activity was not attached")
    }

}