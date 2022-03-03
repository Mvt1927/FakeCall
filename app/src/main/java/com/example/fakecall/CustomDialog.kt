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
import androidx.fragment.app.DialogFragment

class CustomDialog internal constructor(
    var c: Context ,
    var t:CharSequence="Test",
    var m: CharSequence="Test",
    private val i: Int = 1):DialogFragment(){

    lateinit var sharedPref:SharedPreferences
    var buttonClick = -1


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
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
                            var dialog:CustomDialog = CustomDialog(c,i=0)
                            dialog.show(parentFragmentManager,"Dialog")
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
        }?: throw IllegalStateException("")
            // Use the Builder class for convenient dialog construction
    }

}

