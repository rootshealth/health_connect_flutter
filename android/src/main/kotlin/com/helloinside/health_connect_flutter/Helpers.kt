package com.helloinside.health_connect_flutter

import android.util.Log
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.Session
import com.helloinside.health_connect_flutter.Pigeon.HealthConnectData
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

fun logSession(session: Session) {
    Timber.tag(TAG).d("Session: ")
    Timber.tag(TAG).d("\t" + "Name: " + session.name)
    Timber.tag(TAG).d("\t" + "Identifier: " + session.identifier)
    Timber.tag(TAG).d("\t" + "Activity: " + session.activity)
    Timber.tag(TAG).d("\t" + "Description: " + session.description)
    Timber.tag(TAG).d("\t" + "Start Time: " + session.getStartTime(TimeUnit.MINUTES))
    Timber.tag(TAG).d("\t" + "End Time: " + session.getEndTime(TimeUnit.MINUTES))
}

fun logDataSets(dataSets: List<DataSet>) {
    for (dataSet in dataSets) {
        logDataSet(dataSet)
    }
}

fun logDataSet(dataSet: DataSet) {
    for (dataPoint in dataSet.dataPoints) {
        logDataPoint(dataPoint)
    }
}

fun logDataPoint(dataPoint: DataPoint) {
    Timber.tag(TAG).d("Data point:")
    Timber.tag(TAG).d("\t" + "Type: " + dataPoint.dataType.name)
    Timber.tag(TAG).d("\t" + "Start: " + dataPoint.getStartTimeString())
    Timber.tag(TAG).d("\t" + "End: " + dataPoint.getEndTimeString())
    for (field in dataPoint.dataType.fields) {
        Timber.tag(TAG).d("\t" + "Field: " + field.name + " Value: " + dataPoint.getValue(field))
    }
}

fun logBuckets(buckets: List<Bucket>) {
//    if (!BuildConfig.DEBUG) {
//        return
//    }
    Log.d(TAG, "Number of buckets: ${buckets.size}")
    for (bucket in buckets) {
        logBucket(bucket)
    }
}

fun logBucket(bucket: Bucket) {
    Log.d(TAG, "----------------")
    Log.d(TAG, "Bucket:")
    Log.d(TAG, "\tActivity: ${bucket.activity}")
    Log.d(TAG, "\tBucketType: ${bucket.bucketType}")
    logDataSets(bucket.dataSets)
    bucket.session?.let {
        logSession(it)
    }
}

fun logHealthData(healthData: HealthConnectData) {
    Timber.tag(TAG).d("HealthConnectData: ")
    Timber.tag(TAG).d("\t" + "Height: " + healthData.height)
    Timber.tag(TAG).d("\t" + "Weight: " + healthData.weight)
}

fun Session.toHealthConnectWorkoutData(): Pigeon.HealthConnectWorkoutData {
    val startTimeInSeconds = getStartTime(TimeUnit.SECONDS)
    val endTimeTimeInSeconds = getEndTime(TimeUnit.SECONDS)
    val durationInSeconds = endTimeTimeInSeconds - startTimeInSeconds

    var activityType = Pigeon.WorkoutActivityType.UNKNOWN
    try {
        activityType = Pigeon.WorkoutActivityType.valueOf(activity.uppercase(Locale.getDefault()))
    } catch (e: Exception) {
        print("Unknown workout: $e")
    }
    return Pigeon.HealthConnectWorkoutData.Builder()
        .setIdentifier(identifier)
        .setName(name)
        .setDescription(description)
        .setActivityType(activityType)
        .setStartTimestamp(startTimeInSeconds)
        .setEndTimestamp(endTimeTimeInSeconds)
        .setDuration(durationInSeconds)
        .build()
}

fun DataPoint.getStartTimeString() =
    Instant.ofEpochSecond(this.getStartTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()

fun DataPoint.getEndTimeString() =
    Instant.ofEpochSecond(this.getEndTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()