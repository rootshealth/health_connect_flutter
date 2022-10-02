package com.helloinside.health_connect_flutter

import android.Manifest

const val TAG = "health_connect_plugin"

class Permission {

    object Type {
        const val ACTIVITY_RECOGNITION_PERMISSION = "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val BODY_SENSORS = Manifest.permission.BODY_SENSORS
    }

    object Code {
        const val ACTIVITY_RECOGNITION = 19
        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 20
    }

    object Status {
        val PERMISSION_STATUS_DENIED = 0
        val PERMISSION_STATUS_GRANTED = 1
        val PERMISSION_STATUS_RESTRICTED = 2
        val PERMISSION_STATUS_LIMITED = 3
        val PERMISSION_STATUS_NEVER_ASK_AGAIN = 4
    }

}