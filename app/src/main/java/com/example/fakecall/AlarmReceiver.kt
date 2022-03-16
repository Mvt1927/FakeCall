/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 16/3/2022
 */
package com.example.fakecall

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(p0: Context, p1: Intent) {
        val i = Intent()
        i.setClassName(p0, "com.example.fakecall.CallingActivity")
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        p0.startActivity(i)
    }
}
