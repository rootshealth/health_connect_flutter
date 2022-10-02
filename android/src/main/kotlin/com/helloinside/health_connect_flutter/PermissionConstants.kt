package com.helloinside.health_connect_flutter

import android.Manifest
import android.os.Build

const val TAG = "health_connect_plugin"

class Permission {

    object Type {
        private const val ACTIVITY_RECOGNITION_PERMISSION_API_28 = Manifest.permission.ACTIVITY_RECOGNITION
        private const val ACTIVITY_RECOGNITION_PERMISSION_API_27 =
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION"

        // https://developer.android.com/about/versions/10/privacy/changes
        // ACTIVITY_RECOGNITION_PERMISSION is automatically granted for SDK < 28
        val ACTIVITY_RECOGNITION_PERMISSION =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ACTIVITY_RECOGNITION_PERMISSION_API_28 else ACTIVITY_RECOGNITION_PERMISSION_API_27

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