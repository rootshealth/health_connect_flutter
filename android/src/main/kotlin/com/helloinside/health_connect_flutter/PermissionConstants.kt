package com.helloinside.health_connect_flutter

import android.Manifest

const val TAG = "health_connect_plugin"

class Permission {

    object Type {
        const val ACTIVITY_RECOGNITION_PERMISSION = Manifest.permission.ACTIVITY_RECOGNITION
        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val BODY_SENSORS = Manifest.permission.BODY_SENSORS
    }

    object Code {
        const val ACTIVITY_RECOGNITION = 19
        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 20
    }

}