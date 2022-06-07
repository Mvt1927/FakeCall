/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Update 17/5/2022
 */
/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 16/3/2022
 */
package com.example.fakecall.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(p0: Context, p1: Intent) {
        if (p1.action == "CALL_ACTIVITY_ACTION") {
            val i = Intent()
            i.setClassName(p0, "com.example.fakecall.activity.CallingActivity")
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            p0.startActivity(i)
        }
    }
}
