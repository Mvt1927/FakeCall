package com.example.fakecall

import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TimePicker
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var time:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        time = findViewById(R.id.time)
        time.setOnClickListener{
            var mcurrentTime:Calendar = Calendar.getInstance();
            var hour:Int = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            var minute:Int = mcurrentTime.get(Calendar.MINUTE);
            var mTimePicker:TimePickerDialog;
            mTimePicker = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute -> time.setText("$selectedHour:$selectedMinute") } ,
                hour,
                minute,
                true
                )
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
        }
        var startFakeCall:ImageButton = findViewById(R.id.btn_start_fake_call)
            startFakeCall.setOnClickListener{ onBtnStartFakeCallClick() }

    }
    fun onBtnStartFakeCallClick(){
        val startFakeCall = Intent(this,CallingActivity::class.java)
        startActivity(startFakeCall)
        val string:String = getString(R.string.app_name)
        finish()
    }
}