package com.helloinside.health_connect_flutter

import android.Manifest
import android.os.Build

const val TAG = "health_connect_plugin"

class Permission {

    object Type {
        // https://developer.android.com/about/versions/10/privacy/changes
        // https://developers.google.com/fit/android/authorization#android-10
        // ACTIVITY_RECOGNITION_PERMISSION is automatically granted for SDK < 29
        val ACTIVITY_RECOGNITION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACTIVITY_RECOGNITION
        } else {
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        }

        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val BODY_SENSORS = Manifest.permission.BODY_SENSORS
    }

    object Code {
        const val ACTIVITY_RECOGNITION = 19
        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 20
    }

}