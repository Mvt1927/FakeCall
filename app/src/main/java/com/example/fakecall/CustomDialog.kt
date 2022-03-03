/*******************************************************************************
 * Copyright (c) Mvt1927
 * Update 2/3/2022
 */
package com.example.fakecall

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View

class CustomDialog internal constructor(
    var c: Context ,
    title:CharSequence="Test",
    message: CharSequence="Test",
    private val i: Int = 1
):Dialog(c),View.OnClickListener{

    lateinit var sharedPref:SharedPreferences
    var buttonClick = -1

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(c,R.style.CustomDialogTheme)
            when (i){
/*
              0 -> test
              1 -> Default
*/
                0 -> builder.setTitle(t)
                    .setMessage("Test"+"\n\n")
                    .setNeutralButton(R.string.text_cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            Log.i("Cancel","Cancel click")
                        })
                    .setPositiveButton(R.string.text_import,
                        DialogInterface.OnClickListener { dialog, id ->
                            var dialog:CustomDialog = CustomDialog(c,0)
                            dialog.show()
                        })
                    .setNegativeButton(R.string.text_default,
                        DialogInterface.OnClickListener { dialog, id ->
                            Log.i(R.string.text_default.toString(),"Default click")
                        })
                1 -> builder.setTitle(R.string.change_background)
                    .setMessage(c.getString(R.string.change_background_message)+"\n\n")
/*
*                   -1 -> cancel
*                   0  -> default
*                   1  -> import
*
* */
                    .setNeutralButton(R.string.text_cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            buttonClick = -1
                        })

                    .setPositiveButton(R.string.text_import,
                        DialogInterface.OnClickListener { dialog, id ->
                            buttonClick = 1
                        })
                    .setNegativeButton(R.string.text_default,
                        DialogInterface.OnClickListener { dialog, id ->
                            buttonClick = 0
                        })
            }
            // Create the AlertDialog object and return it
            builder.create()
    }
    override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        super.setOnCancelListener(listener)
        buttonClick = -1
    }

    override fun onBackPressed() {
        super.onBackPressed()
        buttonClick = -1
    }

    override fun onClick(v: View) {
        dismiss()
    }
}

