package com.example.fakecall

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color.blue
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.log


class CallingActivity: AppCompatActivity() {
    var REQUEST_VIBRATE:Int = 1;
    lateinit var ringtone: Ringtone
    lateinit var vibrationEffect: VibrationEffect
    lateinit var vibrator:Vibrator
    lateinit var audioManager: AudioManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        var ringerMode = audioManager.ringerMode
        var denyCall:ImageView = findViewById(R.id.btn_deny_call)
        var silent:GridLayout = findViewById(R.id.btn_silent)
        //Phát nhạc chuông thông báo
        ringtone = RingtoneManager.getRingtone(this,Settings.System.DEFAULT_RINGTONE_URI)
        when(ringerMode){
            AudioManager.RINGER_MODE_NORMAL -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ringtone.isLooping=true
                    ringtone.play()
                }
            }
            AudioManager.RINGER_MODE_VIBRATE -> makePhoneVibrate()
        }

        //
        denyCall.setOnClickListener { onBtnDenyCallClick() }
        silent.setOnClickListener { onBtnSilentClick() }
    }
    fun onBtnDenyCallClick(){
        val denyCall = Intent(this,MainActivity::class.java)
        startActivity(denyCall)
        ringtone.stop()
        vibrator.cancel()
        finish()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun onBtnSilentClick(){
        var img: ImageView = findViewById(R.id.btn_silent_img)
        var text: TextView = findViewById(R.id.btn_silent_text)

        img.setColorFilter(getColor(R.color.blue))
        text.setTextColor(getColor(R.color.blue))
        ringtone.stop()
        vibrator.cancel()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun makePhoneVibrate(){
        val mVibratePattern:LongArray = longArrayOf(1000, 800, 1000, 800, 1000, 800, 1000)
        var mAmplitudes:IntArray = intArrayOf(0, -1, 0, -1, 0, -1, 0)
//        var test1= android.Manifest.permission.VIBRATE;
//        var test2= PackageManager.PERMISSION_GRANTED
//        var test3= ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE)
//        var test4= Settings.Global.MODE_RINGER
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.VIBRATE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf<String>(android.Manifest.permission.VIBRATE),REQUEST_VIBRATE)
        }else{
//            var test = vibrator.hasVibrator()
            vibrationEffect = VibrationEffect.createWaveform(mVibratePattern,mAmplitudes,1)
            vibrator.vibrate(vibrationEffect)
//            if (!vibrator.hasAmplitudeControl()) {
//                vibrationEffect = VibrationEffect.createWaveform(mVibratePattern,mAmplitudes,1)
//                vibrator.vibrate(vibrationEffect)
//            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==REQUEST_VIBRATE){
            if (grantResults.isNotEmpty() &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                makePhoneVibrate()
            }else{
                Toast.makeText(this,"Permisson Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}