package com.helloinside.health_connect_flutter

import android.Manifest

class Permission {

    object Group {
        const val LOG_TAG = "health_connect_plugin"
        const val ACTIVITY_RECOGNITION_PERMISSION = Manifest.permission.ACTIVITY_RECOGNITION
        const val ACTIVITY_RECOGNITION_CODE = 19
    }

    object Status {
        val PERMISSION_STATUS_DENIED = 0
        val PERMISSION_STATUS_GRANTED = 1
        val PERMISSION_STATUS_RESTRICTED = 2
        val PERMISSION_STATUS_LIMITED = 3
        val PERMISSION_STATUS_NEVER_ASK_AGAIN = 4
    }

}