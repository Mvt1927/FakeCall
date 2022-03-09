/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 23/2/2022
 */

@file:Suppress("DEPRECATION")

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
import android.os.*
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri

class CallingActivity: AppCompatActivity(){

    companion object {
        private const val NAME: String = "name"
        private const val PHONE: String = "phone"
        private const val LOCATION: String = "location"
        private const val AVATAR:String = "avatar"
        private const val RINGTONE:String = "ringtone"
        private const val DEFAULT_TEXT: String = ""
    }

    private val mInterval:Long = 35000

    private var close:Boolean = false

    private lateinit var ringtone: Ringtone
    private lateinit var vibrationEffect: VibrationEffect
    private lateinit var vibrator:Vibrator
    lateinit var audioManager: AudioManager
    private var statusNotification = -1;
    private lateinit var returnIntent:Intent

    private lateinit var ringtonePath:String

    private lateinit var mHandler: Handler

    lateinit var sharedPref: SharedPreferences

    lateinit var nameEditText: TextView
    lateinit var phoneEditText: TextView
    lateinit var locationEditText: TextView
    lateinit var avatarImageView:ImageView

    private lateinit var denyCall:ImageButton
    lateinit var silent:GridLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        sharedPref = getSharedPreferences("file",0)
        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        locationEditText = findViewById(R.id.locationEditText)
        avatarImageView = findViewById(R.id.Avatar)
        denyCall = findViewById(R.id.btn_deny_call)
        silent = findViewById(R.id.btn_silent)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mHandler = Handler()

        denyCall.setOnClickListener { onBtnDenyCallClick() }
        silent.setOnClickListener {
            onBtnSilentClick()
        }

        setFitAndTransparentBackground()
        setCaller()
        startRepeatingTask()
        startNotification()
    }

    private fun startRepeatingTask(){
        mStatusChecker.run()
    }

    private fun stopRepeatingTask(){
        mHandler.removeCallbacks(mStatusChecker)
    }

    var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                if (close)
                    closeActivity()
                else close = true
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(this, mInterval)
            }
        }
    }

    private fun closeActivity(){
        when (statusNotification){
            1 -> ringtone.stop()
            0 -> vibrator.cancel()
        }
        this.finish()
    }

    private fun setCaller(){
        val name = sharedPref.getString(NAME,DEFAULT_TEXT)
        val phone = sharedPref.getString(PHONE,DEFAULT_TEXT)
        val location = sharedPref.getString(LOCATION,DEFAULT_TEXT)
        val avatar = sharedPref.getString(AVATAR,DEFAULT_TEXT)
        val ringtone = sharedPref.getString(RINGTONE, DEFAULT_TEXT)

        nameEditText.text = name
        phoneEditText.text = phone
        locationEditText.text = location
        setImageBackground(avatar)
        ringtonePath = ringtone.toString()
    }

    private fun setFitAndTransparentBackground(){
        val w: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            w.setDecorFitsSystemWindows(false)
        }else w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.statusBarColor = ContextCompat.getColor(this,R.color.invisible);
        w.navigationBarColor= ContextCompat.getColor(this,R.color.invisible)
    }

    private fun startNotification(){
        when(audioManager.ringerMode){
            AudioManager.RINGER_MODE_NORMAL -> {
                makeRingtone()
            }
            AudioManager.RINGER_MODE_VIBRATE -> makePhoneVibrate()
        }
    }
    private fun stopNotification(){

    }


    private fun onBtnDenyCallClick(){
        val denyCall = Intent(this,MainActivity::class.java)
        startActivity(denyCall)
        when (statusNotification){
            1 -> ringtone.stop()
            0 -> vibrator.cancel()
        }
        finish()
    }
    fun onBtnSilentClick(){
        val img: ImageView = findViewById(R.id.btn_silent_img)
        val text: TextView = findViewById(R.id.btn_silent_text)

        img.setColorFilter(resources.getColor(R.color.blue))
        text.setTextColor(resources.getColor(R.color.blue))
            when (statusNotification){
                1 -> ringtone.stop()
                0 -> vibrator.cancel()
        }
    }
    private fun setImageBackground(path:String? = ""){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    var imageBackground:Drawable?
                    if (path != ""){
                        imageBackground = Drawable.createFromPath(path)
                    }else imageBackground = peekWallpaper()
                    if (imageBackground != null){
                        val blurredBitmap: Bitmap = BlurBuilder.blur(this,imageBackground.toBitmap())
                        val background:ConstraintLayout = findViewById(R.id.background)
                        background.setBackgroundDrawable(BitmapDrawable(resources,blurredBitmap))
                    }
                }
    }
    private fun makePhoneVibrate(){
        statusNotification = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }
    fun makeRingtone(){
        statusNotification = 1
        ringtone = if (ringtonePath!="")
            RingtoneManager.getRingtone(this, ringtonePath.toUri())
        else RingtoneManager.getRingtone(this,Settings.System.DEFAULT_RINGTONE_URI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ringtone.isLooping=true
        }
        ringtone.play()
    }
    override fun onResume() {
        super.onResume()
        when (statusNotification){
            1 -> ringtone.play()
            0 -> makePhoneVibrate()
        }
    }
    override fun onPause() {
        super.onPause()
        when (statusNotification){
            1 -> ringtone.stop()
            0 -> vibrator.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
        when (statusNotification){
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