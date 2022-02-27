package com.example.fakecall

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import java.util.Calendar.*

class MainActivity : AppCompatActivity() {
    private lateinit var time:Button
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        time = findViewById(R.id.BtnSelectRingtone)
        time.setOnClickListener{
            val currentTime:Calendar = getInstance()
            val hour:Int = currentTime.get(HOUR_OF_DAY)
            val minute:Int = currentTime.get(MINUTE)
            val mTimePicker = TimePickerDialog(this,
                { _, selectedHour, selectedMinute -> time.text = "$selectedHour:$selectedMinute" },
                hour,
                minute,
                true
                )
                mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }
        val startFakeCall:ImageButton = findViewById(R.id.btn_start_fake_call)
            startFakeCall.setOnClickListener{ onBtnStartFakeCallClick() }

    }
    private fun onBtnStartFakeCallClick(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.VIBRATE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.VIBRATE),1)
        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        val startFakeCall = Intent(this,CallingActivity::class.java)
        startActivity(startFakeCall)
        finish()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            1->{
                if (grantResults.isNotEmpty() &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission Accept", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}