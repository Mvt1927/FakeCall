/*******************************************************************************
 * Copyright (c) Mvt1927
 * Update 2/3/2022
 */
package com.example.fakecall

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment


class CustomDialog internal constructor(

    var c: Context ,
    private val ID: Int,
    var t:CharSequence="Test",
    var m: CharSequence="Test",
    ):DialogFragment(){
    var buttonClick = -1
    private lateinit var listener: CustomDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(c,R.style.CustomDialogTheme)
            builder.setTitle(t)
                .setMessage("$m\n\n")
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
            // Create the AlertDialog object and return it
            builder.create()
        }?: throw IllegalStateException("")
            // Use the Builder class for convenient dialog construction
    }



    interface CustomDialogListener {
        fun onCustomDialogDismiss(button:Int,id: Int)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as CustomDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement CustomDialogListener"))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onCustomDialogDismiss(buttonClick,ID)
    }
}


