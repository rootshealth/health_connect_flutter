package com.helloinside.health_connect_flutter

import android.util.Log
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.Session
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

fun logSession(session: Session) {
    if (!BuildConfig.DEBUG) {
        return
    }
    Log.d(TAG, "Session: ")
    Log.d(TAG, "\tName: ${session.name}")
    Log.d(TAG, "\tIdentifier: ${session.identifier}")
    Log.d(TAG, "\tActivity: ${session.activity}")
    Log.d(TAG, "\tDescription: ${session.description}")
    Log.d(TAG, "\tStart Time: ${session.getStartTime(TimeUnit.MINUTES)}")
    Log.d(TAG, "\tEnd Time: ${session.getEndTime(TimeUnit.MINUTES)}")
}

fun logDataSets(dataSets: List<DataSet>) {
    if (!BuildConfig.DEBUG) {
        return
    }
    for (dataSet in dataSets) {
        logDataSet(dataSet)
    }
}

fun logDataSet(dataSet: DataSet) {
    if (!BuildConfig.DEBUG) {
        return
    }
    for (dataPoint in dataSet.dataPoints) {
        logDataPoint(dataPoint)
    }
}

fun logDataPoint(dataPoint: DataPoint) {
    if (!BuildConfig.DEBUG) {
        return
    }
    Log.d(TAG, "Data point:")
    Log.d(TAG, "\tType: ${dataPoint.dataType.name}")
    Log.d(TAG, "\tStart: ${dataPoint.getStartTimeString()}")
    Log.d(TAG, "\tEnd: ${dataPoint.getEndTimeString()}")
    for (field in dataPoint.dataType.fields) {
        Log.d(TAG, "\tField: ${field.name} Value: ${dataPoint.getValue(field)}")
    }
}

fun logBuckets(buckets: List<Bucket>) {
    if (!BuildConfig.DEBUG) {
        return
    }
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