/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 17/5/2022
 */
package com.example.fakecall.activity

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fakecall.receiver.AlarmReceiver


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.action = "CALL_ACTIVITY_ACTION"
        val alarmUp = PendingIntent.getBroadcast(this, 10,
            intent,
            PendingIntent.FLAG_NO_CREATE) != null
        if (alarmUp) {
            startActivity(Intent(this@SplashActivity, StopReceiverActivity::class.java))
        } else startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        this.finish()
    }
}