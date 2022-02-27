/*******************************************************************************
 * Copyright (c) Mvt1927
 * Update 23/2/2022
 */


package com.example.fakecall

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap


class CallingActivity: AppCompatActivity() {
    private lateinit var ringtone: Ringtone
    private lateinit var vibrationEffect: VibrationEffect
    private lateinit var vibrator:Vibrator
    private lateinit var audioManager: AudioManager

    @RequiresApi(Build.VERSION_CODES.O)
    private var backPressedTime:Long = 0
    lateinit var backToast:Toast

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)
        getImageBackground()

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val ringerMode = audioManager.ringerMode
        val denyCall:ImageView = findViewById(R.id.btn_deny_call)
        val silent:GridLayout = findViewById(R.id.btn_silent)
        //play ring stone
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val decorView: View = window.decorView
        if (hasFocus) {
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // this flag do=Semi-transparent bars temporarily appear and then hide again
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // it Make Content Appear Behind the Navigation Bar
                    )// dong nay de cho content background hien thi toan man hinh
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            )
            w.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            );
            Color.TRANSPARENT
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.statusBarColor = ContextCompat.getColor(this,R.color.invisible);
            w.navigationBarColor= ContextCompat.getColor(this,R.color.invisible)
        }
    }
    private fun onBtnDenyCallClick(){
        val denyCall = Intent(this,MainActivity::class.java)
        startActivity(denyCall)
        ringtone.stop()
        vibrator.cancel()
        finish()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun onBtnSilentClick(){
        val img: ImageView = findViewById(R.id.btn_silent_img)
        val text: TextView = findViewById(R.id.btn_silent_text)

        img.setColorFilter(getColor(R.color.blue))
        text.setTextColor(getColor(R.color.blue))
        ringtone.stop()
        vibrator.cancel()
    }
    private fun getImageBackground(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            val imageBackground:Drawable? = peekWallpaper()
            if (imageBackground!=null){
                val blurredBitmap: Bitmap = BlurBuilder.blur(this,imageBackground.toBitmap())
                val background:ConstraintLayout = findViewById(R.id.background)
//                background.background = blurredBitmap.toDrawable(resources)
                background.setBackgroundDrawable(BitmapDrawable(resources,blurredBitmap))
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun makePhoneVibrate(){
        val mVibratePattern:LongArray = longArrayOf(1000, 800, 1000, 800, 1000, 800, 1000)
        val mAmplitudes:IntArray = intArrayOf(0, -1, 0, -1, 0, -1, 0)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.VIBRATE)==PackageManager.PERMISSION_GRANTED){
            if (vibrator.hasVibrator()) {
                vibrationEffect = VibrationEffect.createWaveform(mVibratePattern, mAmplitudes, 1)
                vibrator.vibrate(vibrationEffect)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        backToast = Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG)
        if ((backPressedTime + 2000) > System.currentTimeMillis()) {
            backToast.cancel()
            ringtone.stop()
            vibrator.cancel()
            this.finish()
            super.onBackPressed()
            return
        } else {
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

}