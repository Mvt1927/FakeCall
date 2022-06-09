/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 9/6/2022
 */
/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 17/5/2022
 */
/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Create 10/3/2022
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
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.fakecall.R
import com.example.fakecall.`object`.BlurBuilder
import com.example.fakecall.fragment.FunctionButtonFragment
import com.example.fakecall.fragment.NumpadFragment
import com.google.android.material.button.MaterialButton


class OnCallingActivity : AppCompatActivity() {

    companion object {
        private const val NAME: String = "name"
        private const val PHONE: String = "phone"
        private const val LOCATION: String = "location"
        private const val BACKGROUND: String = "background"
        private const val AVATAR: String = "avatar"
        private const val VOICE: String = "voice"

        //        private const val RINGTONE: String = "ringtone"
        private const val DEFAULT_TEXT: String = ""
    }

    private lateinit var sharedPref: SharedPreferences

    private lateinit var nameEditText: TextView
    private lateinit var phoneEditText: TextView
    private lateinit var locationEditText: TextView
    private lateinit var avatarImageView: ImageView
    private lateinit var countTimeTextView: TextView

    private lateinit var btnSpeaker: MaterialButton
    private lateinit var btnNumpad: MaterialButton

    private lateinit var closeCall: ImageButton

    private lateinit var handlerCountTime: Handler
    private var countTime = 0

    private lateinit var voicePath: String
    private lateinit var voice: MediaPlayer
    private lateinit var mAudioManager: AudioManager
    private var isSpeaker: Boolean = false
    private var isNumpad: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oncalling)
        val fragment = FunctionButtonFragment()
        showFragment(fragment)
        sharedPref = getSharedPreferences("file", 0)
        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        locationEditText = findViewById(R.id.locationEditText)
        avatarImageView = findViewById(R.id.Avatar)
        countTimeTextView = findViewById(R.id.text_view_count_time)
        btnSpeaker = findViewById(R.id.btn_speaker)
        btnNumpad = findViewById(R.id.btn_numpad)
        closeCall = findViewById(R.id.btn_deny_call)

        closeCall.setOnClickListener {
            voice.stop()
            stopCountTime()
            countTimeTextView.setTextColor(resources.getColor(R.color.Danger))
            Handler().postDelayed({
                val startFakeCall = Intent(this, MainActivity::class.java)
                startActivity(startFakeCall)
                finish()
            }, 1000)
        }
        btnSpeaker.setOnClickListener {
//            startAnimation(it,R.anim.btn_animation_1)
            toggleButtonSpeaker()
        }
        btnNumpad.setOnClickListener {
            toggleButtonNumpad()
        }
        voice = MediaPlayer()
        setFitAndTransparentBackground()
        setCaller()
        handlerCountTime = Handler()
        Handler().postDelayed({
            makeVoice()
            startCountTime()
        }, 1000)
    }

    //    private fun startAnimation(view: View, id: Int) {
//        val animation: Animation = AnimationUtils.loadAnimation(this, id)
//        view.startAnimation(animation)
//    }
    fun showFragment(fragment: Fragment) {
        val fragManager = supportFragmentManager.beginTransaction()
        fragManager.replace(R.id.fragment, fragment)
        fragManager.commit()
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

    private fun makeVoice(streamType: Int = AudioManager.STREAM_VOICE_CALL, msec: Int? = null) {
        if (Build.VERSION.SDK_INT >= 21) {
            voice.setAudioAttributes(
                AudioAttributes.Builder()
                    .setLegacyStreamType(streamType)
                    .build()
            )
        } else {
            voice.setAudioStreamType(streamType)
        }
        if (voicePath != "")
            voice.setDataSource(this, voicePath.toUri())
        else {
            val uri: Uri =
                Uri.parse("android.resource://" + packageName.toString() + "/" + R.raw.default_voice)
            voice.setDataSource(this, uri)
        }
        voice.isLooping = true
        voice.prepare()
        if (msec != null)
            voice.seekTo(msec)
        voice.start()
    }

    private fun toggleButtonSpeaker() {
        isSpeaker = !isSpeaker
        val mCurrentPosition = voice.currentPosition
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        voice.reset()
        when (isSpeaker) {
            true -> {
                btnSpeaker.icon.setTint(resources.getColor(R.color.Dark))
                btnSpeaker.background.setTint(resources.getColor(R.color.Light))
                makeVoice(AudioManager.STREAM_MUSIC, mCurrentPosition)
            }
            false -> {
                btnSpeaker.icon.setTint(resources.getColor(R.color.Light))
                btnSpeaker.background.setTint(resources.getColor(R.color.invisible_full))
                makeVoice(AudioManager.STREAM_VOICE_CALL, mCurrentPosition)
            }
        }
    }

    private fun toggleButtonNumpad() {
        isNumpad = !isNumpad
        when (isNumpad) {
            true -> {
                btnNumpad.icon.setTint(resources.getColor(R.color.Dark))
                btnNumpad.background.setTint(resources.getColor(R.color.Light))
                val fragment = NumpadFragment()
                avatarImageView.visibility = ImageView.GONE
                showFragment(fragment)
            }
            false -> {
                btnNumpad.icon.setTint(resources.getColor(R.color.Light))
                btnNumpad.background.setTint(resources.getColor(R.color.invisible_full))
                val fragment = FunctionButtonFragment()
                avatarImageView.visibility = ImageView.VISIBLE
                showFragment(fragment)
            }
        }
    }

    private fun getTimeFromInt(s: Int): String {
        val second = String.format("%02d", s % 60)
        val minute = String.format("%02d:", s / 60 % 60)
        val hour = if ((s / 3600 % 60) == 0) ""
        else String.format("%02d:", s / 3600 % 60)
        return "$hour$minute$second"
    }

    private var runnableCountTime: Runnable = object : Runnable {
        override fun run() {
            try {
                countTimeTextView.text = getTimeFromInt(countTime++)
            } finally {
                handlerCountTime.postDelayed(this, 1000)
            }
        }
    }

    private fun startCountTime() {
        countTime = 0
        runnableCountTime.run()
    }

    private fun stopCountTime() {
        countTime = 0
        handlerCountTime.removeCallbacks(runnableCountTime)
    }

    private fun setCaller() {
        val name = sharedPref.getString(NAME, DEFAULT_TEXT)
        val phone = sharedPref.getString(PHONE, DEFAULT_TEXT)
        val location = sharedPref.getString(LOCATION, DEFAULT_TEXT)
        val avatar = sharedPref.getString(AVATAR, DEFAULT_TEXT)
        val background = sharedPref.getString(BACKGROUND, DEFAULT_TEXT)
        val voice = sharedPref.getString(VOICE, DEFAULT_TEXT)
        setImageAvatar(avatar)
        setImageBackground(background)
        nameEditText.text = name
        phoneEditText.text = phone
        locationEditText.text = location
        voicePath = voice.toString()
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

    /*Override*/
    override fun onDestroy() {
        stopCountTime()
        try {
            voice.stop()
            voice.release()
        } catch (ex: Exception) {

        }
        super.onDestroy()
    }
}