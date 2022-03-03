/*******************************************************************************
 * Copyright (c) Mvt1927
 * Update 23/2/2022
 */


package com.example.fakecall

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
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

    private val NAME: String = "name"
    private val PHONE: String = "phone"
    private val LOCATION: String = "location"
    private val AVATAR:String = "avatar"
    private val BACKGROUND:String = "background"
    private val DEFAULT_TEXT: String = ""


    private lateinit var ringtone: Ringtone
    private lateinit var vibrationEffect: VibrationEffect
    private lateinit var vibrator:Vibrator
    private lateinit var audioManager: AudioManager
    private var status = -1;
    private lateinit var returnIntent:Intent;

    lateinit var sharedPref: SharedPreferences

    lateinit var nameEditText: TextView
    lateinit var phoneEditText: TextView
    lateinit var locationEditText: TextView
    lateinit var avatarImageView:ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    private var backPressedTime:Long = 0
    lateinit var backToast:Toast

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)
        getImageBackground()
        sharedPref = getSharedPreferences("file",0)
        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        locationEditText = findViewById(R.id.locationEditText)
        avatarImageView = findViewById(R.id.Avatar)

        val name = sharedPref.getString(NAME,DEFAULT_TEXT)
        val phone = sharedPref.getString(PHONE,DEFAULT_TEXT)
        val location = sharedPref.getString(LOCATION,DEFAULT_TEXT)
        val avatar = sharedPref.getString(AVATAR,DEFAULT_TEXT)

        nameEditText.setText(name)
        phoneEditText.setText(phone)
        locationEditText.setText(location)
        if (avatar!=null&&Drawable.createFromPath(avatar)!=null)
            avatarImageView.setImageDrawable(Drawable.createFromPath(avatar))
        else {
            avatarImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                w.setDecorFitsSystemWindows(false)
            }
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.statusBarColor = ContextCompat.getColor(this,R.color.invisible);
            w.navigationBarColor= ContextCompat.getColor(this,R.color.invisible)
        }
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
                    status = 1;
                    ringtone.play()
                }
            }
            AudioManager.RINGER_MODE_VIBRATE -> makePhoneVibrate()
        }
        //
        denyCall.setOnClickListener { onBtnDenyCallClick() }
        silent.setOnClickListener { onBtnSilentClick() }
    }

    private fun onBtnDenyCallClick(){
        val denyCall = Intent(this,MainActivity::class.java)
        startActivity(denyCall)
        when (status){
            1 -> ringtone.stop()
            0 -> vibrator.cancel()
        }
        finish()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun onBtnSilentClick(){
        val img: ImageView = findViewById(R.id.btn_silent_img)
        val text: TextView = findViewById(R.id.btn_silent_text)

        img.setColorFilter(getColor(R.color.blue))
        text.setTextColor(getColor(R.color.blue))
        when (status){
            1 -> ringtone.stop()
            0 -> vibrator.cancel()
        }
    }
    private fun getImageBackground(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            val imageBackground:Drawable? = peekWallpaper()
            if (imageBackground!=null){
                val blurredBitmap: Bitmap = BlurBuilder.blur(this,imageBackground.toBitmap() )
                val background:ConstraintLayout = findViewById(R.id.background)
//                background.background = blurredBitmap.toDrawable(resources)
                background.setBackgroundDrawable(BitmapDrawable(resources,blurredBitmap))
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun makePhoneVibrate(){
        status = 0
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
    override fun onResume() {
        super.onResume()
        when (status){
            1 -> ringtone.play()
            0 -> makePhoneVibrate()
        }
    }
    override fun onPause() {
        super.onPause()
        when (status){
            1 -> ringtone.stop()
            0 -> vibrator.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        when (status){
            1 -> ringtone.stop()
            0 -> vibrator.cancel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        val denyCall = Intent(this,MainActivity::class.java)
        startActivity(denyCall)
        finish()
    }
}