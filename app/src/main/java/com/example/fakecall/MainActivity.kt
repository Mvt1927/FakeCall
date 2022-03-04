package com.example.fakecall

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Display
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.loader.content.CursorLoader
import java.io.File
import java.io.IOException


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),CustomDialog.CustomDialogListener{
    companion object {
        private const val REQUEST_READ_PERMISSION = 999
        private const val REQUEST_RECORD_PERMISSION = 997

        private const val NAME: String = "name"
        private const val PHONE: String = "phone"
        private const val LOCATION: String = "location"
        private const val AVATAR:String = "avatar"
        private const val BACKGROUND:String = "background"
        private const val RINGTONE:String = "ringtone"
        private const val VOICE:String = "voice"
        private const val DEFAULT_TEXT: String = ""

        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission_group.STORAGE
        )
    }
    private var backPressedTime: Long = 0
    private var isCustomDialogShow:Boolean = false

    private lateinit var nameEditText:EditText
    private lateinit var phoneEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var avatarImageView:ImageView
    private var pickCode: Int? = null
    private lateinit var sharedPref: SharedPreferences
    private lateinit var backToast: Toast

    private lateinit var btnBackground:Button
    private lateinit var btnRingtone: Button
    private lateinit var btnVoice: Button
    private lateinit var btnStart: ImageButton
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        locationEditText = findViewById(R.id.locationEditText)
        avatarImageView = findViewById(R.id.Avatar)
        btnStart = findViewById(R.id.btn_start_fake_call)
        btnBackground = findViewById(R.id.btnSelectBackground)
        btnRingtone = findViewById(R.id.btnSelectRingtone)
        btnVoice = findViewById(R.id.btnAddVoice)

        sharedPref = getSharedPreferences("file",0)

        nameEditText.doAfterTextChanged {
            if (sharedPref.getString(NAME, DEFAULT_TEXT) != nameEditText.text.toString()) {
                saveData(NAME,nameEditText.text.toString())
            }
        }

        phoneEditText.doAfterTextChanged{
            if (sharedPref.getString(PHONE,DEFAULT_TEXT) != (phoneEditText.text.toString())){
                saveData(PHONE,phoneEditText.text.toString())
            }
        }

        locationEditText.doAfterTextChanged{
            if (sharedPref.getString(LOCATION,DEFAULT_TEXT) != (locationEditText.text.toString())){
                saveData(LOCATION,locationEditText.text.toString())
            }
        }

        avatarImageView.setOnClickListener {
            onAvatarClick()
        }

        btnBackground.setOnClickListener{
            onBtnSetBackgroundClick()
        }

        btnRingtone.setOnClickListener {
            onBtnSetRingtoneClick()
        }

        btnVoice.setOnClickListener {
            onBtnSetVoiceClick()
        }

        btnStart.setOnClickListener{
            onBtnStartFakeCallClick()
        }

        setCaller()
    }

    private fun onBtnSetVoiceClick() {
        //        4 -> Voice
        val customDialog = CustomDialog(this,4,getString(R.string.change_voice),getString(R.string.change_voice_message))
        if (!isCustomDialogShow){
            isCustomDialogShow = true
            customDialog.show(supportFragmentManager,"CustomDialog_ID4")
        }
    }

    private fun onBtnSetRingtoneClick() {
        //        3 -> Ringtone
        val customDialog = CustomDialog(this,4,getString(R.string.change_ringtone),getString(R.string.change_ringtone_message))
        if (!isCustomDialogShow){
            isCustomDialogShow = true
            customDialog.show(supportFragmentManager,"CustomDialog_ID3")
        }
    }

    private fun onBtnSetBackgroundClick(){
//        2 -> background
        val customDialog = CustomDialog(this,2,getString(R.string.change_background),getString(R.string.change_background_message))
        if (!isCustomDialogShow){
            isCustomDialogShow = true
            customDialog.show(supportFragmentManager,"CustomDialog_ID2")
        }
    }

    private fun onAvatarClick(){
//        1 -> avatar
        val customDialog = CustomDialog(this,1,getString(R.string.s_change_avatar_title),getString(R.string.s_change_avatar_mess))
        if (!isCustomDialogShow){
            isCustomDialogShow = true
            customDialog.show(supportFragmentManager,"CustomDialog_ID1")
        }
    }

    private fun onBtnStartFakeCallClick(){
        val startFakeCall = Intent(this,CallingActivity::class.java)
        startActivity(startFakeCall)
        finish()
    }

    private fun saveData(title: String, value: String) {
        val sp = sharedPref.edit()
        sp.putString(title, value)
        sp.apply()
    }

    private fun setCaller(){
        val name = sharedPref.getString(NAME,DEFAULT_TEXT)
        val phone = sharedPref.getString(PHONE,DEFAULT_TEXT)
        val location = sharedPref.getString(LOCATION,DEFAULT_TEXT)
        val avatar = sharedPref.getString(AVATAR,DEFAULT_TEXT)
        var obj = -1

        nameEditText.setText(name)
        phoneEditText.setText(phone)
        locationEditText.setText(location)

        when (avatar.hashCode()){
            0 -> if (avatar == ""){
                obj = 0
            }
        }
        when (obj){
            0 -> avatarImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
            else -> {
                if (avatar!=null&&Drawable.createFromPath(avatar)!=null)
                    avatarImageView.setImageDrawable(Drawable.createFromPath(avatar))
                else {
                    avatarImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
                    CustomToast.warning(this,getString(R.string.un_load_image))
                }
            }
        }
    }

    private fun performCrop(uri: Uri,id: Int){
        try {
            var aspectX = 1
            var aspectY = 1
            var outputX = 800
            var outputY = 800
            var path = getString(R.string.appFolder)+getString(R.string.avatarImagePath)
            if (id==2){
                val display:Display = windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                aspectX = size.x
                aspectY = size.y
                outputX = aspectX
                outputY = aspectY
                path = getString(R.string.appFolder)+getString(R.string.backgroundImagePath)
            }
            val cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri,"image/*")
            cropIntent.putExtra("crop", "true")
            cropIntent.putExtra("aspectX", aspectX)
            cropIntent.putExtra("aspectY", aspectY)
            cropIntent.putExtra("outputX", outputX)
            cropIntent.putExtra("outputY", outputY)
            val f = File(Environment.getExternalStorageDirectory(),path)
            try { f.createNewFile() } catch (ex:IOException){
                Log.e("io",ex.message.toString())
            }
            cropIntent.putExtra("output",Uri.fromFile(f))
            startActivityForResult(cropIntent,4+id)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(this,"Device doesn't support the crop action",Toast.LENGTH_LONG).show()
        }
    }

    private fun getRealPathFromURI(contentUri: Uri?): String {
        val cursor = CursorLoader(
            applicationContext,
            contentUri!!,
            arrayOf("_data"),
            null,
            null,
            null
        ).loadInBackground()
        val columnIndex = cursor!!.getColumnIndexOrThrow("_data")
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    private fun pick(){
        when(pickCode){
            1 -> pickAvatar()
            2 -> pickBackground()
            3 -> pickRingtone()
            4 -> pickVoice()
            7 -> setDefault(2)
            8 -> setDefault(3)
            9 -> setDefault(4)
        }
    }
    private fun pickVoice(){
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager)!= null){
            intent.type = "audio/*"
            startActivityForResult(intent,4)
        }else{
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
        }
    }
    private fun pickRingtone(){
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager)!= null){
            intent.type = "audio/*"
            startActivityForResult(intent,3)
        }else{
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
        }
    }
    private fun pickBackground(){
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager)!= null){
            intent.type = "image/*"
            startActivityForResult(intent,2)
        }else{
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
        }
    }
    private fun pickAvatar(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager)!= null){
            intent.type = "image/*"
            startActivityForResult(intent,1)
        }else{
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
        }
    }
    private fun setDefault(id: Int){
/*
*           1 -> get avatar
*           2 -> get background
*           3 -> get ringtone
*           4 -> get voice
*           5 -> get image cropped
* */    when(id){
            1   ->  {
                saveData(AVATAR,DEFAULT_TEXT)
                avatarImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
                CustomToast.success(this,getString(R.string.s_change_to_default))
            }
            2   ->  {
                saveData(BACKGROUND,DEFAULT_TEXT)
                CustomToast.success(this,getString(R.string.s_change_to_default))
            }
            3   ->  {
                saveData(RINGTONE,DEFAULT_TEXT)
                CustomToast.success(this,getString(R.string.s_change_to_default))
            }
            4   ->  {
                saveData(VOICE,DEFAULT_TEXT)
                CustomToast.success(this,getString(R.string.s_change_to_default))
            }
        }
    }

    private fun requestPermission(id:Int = 999, r:Boolean = true ){
        if (Build.VERSION.SDK_INT >=23){
            when(id){
                REQUEST_READ_PERMISSION -> requestPermissions(PERMISSIONS_STORAGE,id)
                998 -> requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),id)
                REQUEST_RECORD_PERMISSION -> requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),id)
            }
            return
        }
        if (r) pick()
    }

    /*      @Override       */

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        backToast = Toast.makeText(this, R.string.back_message, Toast.LENGTH_SHORT)
        if ((backPressedTime + 2000) > System.currentTimeMillis()) {
            backToast.cancel()
            this.finish()
            super.onBackPressed()
            return
        } else {
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        setCaller()
    }

    override fun onRequestPermissionsResult(requestCode:Int, permission:Array<String>, grantResults: IntArray ){
        super.onRequestPermissionsResult(requestCode,permission, grantResults)
        val boolean: Boolean
        when(requestCode){
            998   -> {
            }
            999 -> {
                boolean=(grantResults[0]==0)
                if (boolean) pick()
            }
            997 -> {
                boolean=(grantResults[0]==0)
                if (boolean) pick()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
/*
*           1 -> get avatar
*           2 -> get background
*           3 -> get ringtone
*           4 -> get voice
*           5 -> get image cropped
*
* */
            1,2 -> if (resultCode == -1){
                performCrop(data!!.data!!,requestCode)
            }else CustomToast.warning(this,getString(R.string.cancel_pick))
            3 -> if (resultCode == -1){
                val ringtone = getRealPathFromURI(data?.data)
                saveData(RINGTONE,ringtone)
                CustomToast.success(this,getString(R.string.up_image_success))
            }else CustomToast.warning(this,getString(R.string.cancel_pick))
            4 -> if (resultCode == -1){
                val audio:String =getRealPathFromURI(data?.data)
                saveData(VOICE,audio)
                CustomToast.success(this,getString(R.string.up_image_success))
            }else CustomToast.warning(this,getString(R.string.cancel_pick))
            5 -> if (resultCode == -1){
                saveData(AVATAR,Environment.getExternalStorageDirectory().path+getString(R.string.appFolder)+getString(R.string.avatarImagePath))
                CustomToast.success(this,getString(R.string.up_image_success))
            }else CustomToast.warning(this,getString(R.string.cancel_crop))
            6 -> if (resultCode == -1){
                saveData(BACKGROUND,Environment.getExternalStorageDirectory().path+getString(R.string.appFolder)+getString(R.string.backgroundImagePath))
                CustomToast.success(this,getString(R.string.up_image_success))
            }else CustomToast.warning(this,getString(R.string.cancel_crop))
        }
    }

    override fun onCustomDialogDismiss(button: Int, id: Int) {
        isCustomDialogShow = false
/*      1 -> avatar
*       2 -> background
*       3 -> ringtone
*       4 -> voice
*                   -1 -> cancel
*                   0  -> default
*                   1  -> import
* */
        when(id){
            1   ->  when(button){
                -1  ->  { return }
                0   ->  {
                    setDefault(id)
                    return
                }
                1   ->  {
                    pickCode = 1
                    requestPermission(REQUEST_READ_PERMISSION)
                    return
                }
            }
            2,3,4  ->   when(button){
                -1  ->  { return }
                0   ->  {
                    pickCode = 5+id
                    requestPermission(999,false)
                    return
                }
                1   ->  {
                    pickCode = id
                    requestPermission(999)
                    return
                }
            }
        }
    }

    /*      @Override       */
}