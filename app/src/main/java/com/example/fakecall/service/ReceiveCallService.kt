package com.example.fakecall.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.fakecall.receiver.AlarmReceiver
import java.util.*

object ReceiveCallService : AppCompatActivity() {
    private const val requestCode = 10

    @JvmStatic
    val flag = 0

    @SuppressLint("UnspecifiedImmutableFlag")
    fun start(time_hour: Int, time_minute: Int, p0: Context, alarmManager: AlarmManager) {
        val intent = Intent(p0, AlarmReceiver::class.java)
        intent.action = "CALL_ACTIVITY_ACTION"
        val pendingIntent = PendingIntent.getBroadcast(p0, requestCode, intent, flag)
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, time_hour)
            set(Calendar.MINUTE, time_minute)
        }
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun stop(p0: Context, alarmManager: AlarmManager) {
        val intent = Intent(p0, AlarmReceiver::class.java)
        intent.action = "CALL_ACTIVITY_ACTION"
        val pendingIntent =
            PendingIntent.getBroadcast(p0, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}