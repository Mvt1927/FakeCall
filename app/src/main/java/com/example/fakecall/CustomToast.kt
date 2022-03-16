/*******************************************************************************
 * Copyright (c) 2022 Mvt1927
 * Update 3/3/2022
 */
package com.example.fakecall

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import com.pranavpandey.android.dynamic.toasts.DynamicToast

object CustomToast {
    private const val SuccessColor = R.color.Success
    private const val SuccessTextColor = R.color.TextSuccess

    private const val DangerColor = R.color.Danger
    private const val DangerTextColor = R.color.TextDanger

    private const val InfoColor = R.color.TextInfo
    private const val InfoTextColor = R.color.Primary

    private const val WarningColor = R.color.Warning
    private const val WarningTextColor = R.color.TextWarning

    fun success(
        context: Context,
        text: CharSequence? = "Success toast",
        T: Int = SuccessTextColor,
        B: Int = SuccessColor,
        duration: Int = Toast.LENGTH_SHORT,
    ) {
        val b = getColor(context, B)
        val t = getColor(context, T)
        DynamicToast.make(
            context,
            text,
            getDrawable(context, R.drawable.ic_custom_toast_success),
            t,
            b,
            duration
        )
            .show()
    }

    fun warning(
        context: Context,
        text: CharSequence? = "Warning toast",
        T: Int = WarningTextColor,
        B: Int = WarningColor,
        duration: Int = Toast.LENGTH_SHORT,
    ) {
        val b = getColor(context, B)
        val t = getColor(context, T)
        DynamicToast.make(
            context,
            text,
            getDrawable(context, R.drawable.ic_custom_toast_warning),
            t,
            b,
            duration
        )
            .show()
    }

    fun info(
        context: Context,
        text: CharSequence? = "Info toast",
        T: Int = InfoTextColor,
        B: Int = InfoColor,
        duration: Int = Toast.LENGTH_SHORT,
    ) {
        val b = getColor(context, B)
        val t = getColor(context, T)
        DynamicToast.Config.getInstance().setDefaultTintColor(t)
        DynamicToast.make(
            context,
            text,
            getDrawable(context, R.drawable.ic_custom_toast_info),
            t,
            b,
            duration
        ).show()
    }

    fun danger(
        context: Context,
        text: CharSequence? = "Danger toast",
        T: Int = DangerTextColor,
        B: Int = DangerColor,
        duration: Int = Toast.LENGTH_SHORT,
    ) {
        val b = getColor(context, B)
        val t = getColor(context, T)
        DynamicToast.make(
            context,
            text,
            getDrawable(context, R.drawable.ic_custom_toast_danger),
            t,
            b,
            duration
        )
            .show()
    }

}