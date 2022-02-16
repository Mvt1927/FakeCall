package com.example.fakecall

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
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
    }
}