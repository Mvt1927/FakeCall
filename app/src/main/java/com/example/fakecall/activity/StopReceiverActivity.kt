/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 17/5/2022
 */
package com.example.fakecall.activity

import android.app.AlarmManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fakecall.R
import com.example.fakecall.custom.CustomDialog2
import com.example.fakecall.service.ReceiveCallService

class StopReceiverActivity : AppCompatActivity(), CustomDialog2.CustomDialogListener {
    private var isCustomDialogShow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_receiver)
//        val customDialog = CustomDialog2(
//            this,
//            getString(R.string.change_background),
//            getString(R.string.change_background_message)
//        )
//        if (!isCustomDialogShow) {
//            isCustomDialogShow = true
//            customDialog.show(supportFragmentManager, "CustomDialog2_ID1")
//        }
        val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        ReceiveCallService.stop(applicationContext, alarmManager)
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    override fun onCustomDialogDismiss(button: Int) {
        when (button) {
            1 -> {
                val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                ReceiveCallService.stop(applicationContext, alarmManager)
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
            -1 -> {
                finish()
            }
        }
        isCustomDialogShow = false
    }
}