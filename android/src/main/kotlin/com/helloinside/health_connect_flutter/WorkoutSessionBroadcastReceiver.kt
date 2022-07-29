package com.helloinside.health_connect_flutter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.google.android.gms.fitness.data.Session
import java.util.concurrent.TimeUnit

const val intentAction = "workout_session_broadcast_action"
const val intentExtraName = "workout_session"
val intentFilter = IntentFilter(intentAction)

class WorkoutSessionBroadcastReceiver : BroadcastReceiver() {

    override
    fun onReceive(context: Context?, intent: Intent?) {
        Log.e(TAG, "onReceive: ${intent.toString()}")
        intent?.let {
            Session.extract(intent)?.let {
                logSession(it)
                // We need only ended workouts
                if (it.getEndTime(TimeUnit.SECONDS) != 0L) {
                    val broadcastIntent = Intent(intentAction)
                    broadcastIntent.putExtra(intentExtraName, it)
                    context?.sendBroadcast(broadcastIntent);
                }
            }
        }
    }
}