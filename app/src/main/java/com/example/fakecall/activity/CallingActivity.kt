/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 17/5/2022
 */
/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 23/2/2022
 */

@file:Suppress("DEPRECATION")

package com.example.fakecall.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioDeviceInfo
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
import com.example.fakecall.R
import com.example.fakecall.`object`.BlurBuilder


class CallingActivity : AppCompatActivity() {

    companion object {
        private const val NAME: String = "name"
        private const val PHONE: String = "phone"
        private const val LOCATION: String = "location"
        private const val AVATAR: String = "avatar"
        private const val RINGTONE: String = "ringtone"
        private const val BACKGROUND: String = "background"
        private const val DEFAULT_TEXT: String = ""
    }

    private val mInterval: Long = 35000

    private var close: Boolean = false

    private lateinit var ringtone: Ringtone
    private lateinit var vibrationEffect: VibrationEffect
    private lateinit var vibrator: Vibrator
    private lateinit var audioManager: AudioManager
    private var statusNotification = -1

    private lateinit var ringtonePath: String

    private lateinit var mHandler: Handler
    private lateinit var handlerAudioManager: Handler

    private lateinit var sharedPref: SharedPreferences

    private lateinit var nameEditText: TextView
    private lateinit var phoneEditText: TextView
    private lateinit var locationEditText: TextView
    private lateinit var avatarImageView: ImageView

    private lateinit var denyCall: ImageButton
    private lateinit var acceptCall: ImageButton
    private lateinit var silent: GridLayout
    private var isHeadset: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        sharedPref = getSharedPreferences("file", 0)
        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        locationEditText = findViewById(R.id.locationEditText)
        avatarImageView = findViewById(R.id.Avatar)
        denyCall = findViewById(R.id.btn_deny_call)
        acceptCall = findViewById(R.id.btn_accept_call)
        silent = findViewById(R.id.btn_silent)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mHandler = Handler()
        handlerAudioManager = Handler()
        denyCall.setOnClickListener { onBtnDenyCallClick() }
        silent.setOnClickListener {
            onBtnSilentClick()
        }
        acceptCall.setOnClickListener {
            stopNotification()
            val startFakeCall = Intent(this, OnCallingActivity::class.java)
            startActivity(startFakeCall)
            finish()
        }
        setFitAndTransparentBackground()
        setCaller()
        this.startRepeatingTask()
        startNotification()
    }

    private fun startRepeatingTask() {
        mStatusChecker.run()
    }

    private fun stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker)
    }

    private var mStatusChecker: Runnable = object : Runnable {
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


    private fun closeActivity() {
        stopNotification()
        this.finish()
    }

    private fun startAudioChangeType() {
        audioManagerType.run()
    }

    private fun stopAudioChangeType() {
        handlerAudioManager.removeCallbacks(audioManagerType)
    }

    private var audioManagerType: Runnable = object : Runnable {
        override fun run() {
            try {
                if (isWiredHeadsetOn() != isHeadset) {
                    isHeadset = isWiredHeadsetOn()
                    ringtone.stop()
                    if (isHeadset) {
                        ringtone.streamType = AudioManager.STREAM_MUSIC
                    } else {
                        ringtone.streamType = AudioManager.STREAM_RING
                    }
                    ringtone.play()
                }
            } finally {
                handlerAudioManager.postDelayed(this, 0)
            }
        }

    }


    private fun isWiredHeadsetOn(): Boolean {
        val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (deviceInfo in audioDevices) {
            if (deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                || deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
            ) return true
        }
        return false
    }

    private fun setCaller() {
        val name = sharedPref.getString(NAME, DEFAULT_TEXT)
        val phone = sharedPref.getString(PHONE, DEFAULT_TEXT)
        val location = sharedPref.getString(LOCATION, DEFAULT_TEXT)
        val background = sharedPref.getString(BACKGROUND, DEFAULT_TEXT)
        val avatar = sharedPref.getString(AVATAR, DEFAULT_TEXT)
        val ringtone = sharedPref.getString(RINGTONE, DEFAULT_TEXT)





        nameEditText.text = name
        phoneEditText.text = phone
        locationEditText.text = location
        setImageAvatar(avatar)
        setImageBackground(background)
        ringtonePath = ringtone.toString()
    }

    private fun setFitAndTransparentBackground() {
        val w: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            w.setDecorFitsSystemWindows(false)
        } else w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        w.statusBarColor = ContextCompat.getColor(this, R.color.invisible)
        w.navigationBarColor = ContextCompat.getColor(this, R.color.invisible)
    }

    private fun startNotification() {
        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                makeRingtone()
                startAudioChangeType()
            }
            AudioManager.RINGER_MODE_VIBRATE -> makePhoneVibrate()
        }
    }

    private fun stopNotification() {
        when (statusNotification) {
            1 -> {
                stopAudioChangeType()
                ringtone.stop()
            }
            0 -> vibrator.cancel()
        }
    }


    private fun onBtnDenyCallClick() {
        val denyCall = Intent(this, MainActivity::class.java)
        startActivity(denyCall)
        stopNotification()
        finish()
    }

    private fun onBtnSilentClick() {
        val img: ImageView = findViewById(R.id.btn_silent_img)
        val text: TextView = findViewById(R.id.btn_silent_text)
        img.setColorFilter(resources.getColor(R.color.blue))
        text.setTextColor(resources.getColor(R.color.blue))
        stopNotification()
    }

    private fun setImageBackground(path: String? = "") {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val imageBackground: Drawable? = if (path != "") {
                Drawable.createFromPath(path)
            } else peekWallpaper()
            if (imageBackground != null) {
                val blurredBitmap: Bitmap = BlurBuilder.blur(this, imageBackground.toBitmap())
                val background: ConstraintLayout = findViewById(R.id.background)
                background.setBackgroundDrawable(BitmapDrawable(resources, blurredBitmap))
            }
        }
    }

    private fun setImageAvatar(path: String? = "") {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            var imageBackground: Drawable? = null
            if (path != "") {
                imageBackground = Drawable.createFromPath(path)
            }
            if (imageBackground != null) {
                avatarImageView.setImageDrawable(imageBackground)
            } else avatarImageView.setImageResource(R.drawable.ic_avatar)
        }
    }

    private fun makePhoneVibrate() {
        statusNotification = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mVibratePattern: LongArray = longArrayOf(1000, 800, 1000, 800, 1000, 800, 1000)
            val mAmplitudes: IntArray = intArrayOf(0, -1, 0, -1, 0, -1, 0)
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.VIBRATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (vibrator.hasVibrator()) {
                    vibrationEffect =
                        VibrationEffect.createWaveform(mVibratePattern, mAmplitudes, 1)
                    vibrator.vibrate(vibrationEffect)
                }
            }
        }
    }

    private fun makeRingtone() {
        statusNotification = 1
        ringtone = if (ringtonePath != "")
            RingtoneManager.getRingtone(this, ringtonePath.toUri())
        else RingtoneManager.getRingtone(this, Settings.System.DEFAULT_RINGTONE_URI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ringtone.isLooping = true
            isHeadset = isWiredHeadsetOn()
            ringtone.streamType = if (isWiredHeadsetOn()) AudioManager.STREAM_MUSIC
            else AudioManager.STREAM_RING
        }
        ringtone.play()

    }

    override fun onResume() {
        super.onResume()
        stopNotification()
        startNotification()
    }

    override fun onPause() {
        super.onPause()
        stopNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
        stopNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        val denyCall = Intent(this, MainActivity::class.java)
        startActivity(denyCall)
        stopNotification()
        finish()
    }
}