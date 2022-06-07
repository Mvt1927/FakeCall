package com.example.fakecall.`object`
/*
* Copyright by https://stackoverflow.com/users/511082/b-yng
* Convert to kotlin by Android Studio
* */
import android.content.Context
import android.graphics.Bitmap
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import kotlin.math.roundToInt

object BlurBuilder {
    private var BITMAP_SCALE: Float = 0.15f
    private var BLUR_RADIUS: Float = 20f

    fun blur(
        context: Context,
        image: Bitmap,
        scale: Float = BITMAP_SCALE,
        radius: Float = BLUR_RADIUS,
    ): Bitmap {
        val with: Int = (image.width * scale).roundToInt()
        val height: Int = (image.height * scale).roundToInt()
        val inputBitMap: Bitmap = Bitmap.createScaledBitmap(image, with, height, false)
        val outputBitMap: Bitmap = Bitmap.createBitmap(inputBitMap)

        val rs: RenderScript = RenderScript.create(context)
        val theIntrinsics: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn: Allocation = Allocation.createFromBitmap(rs, inputBitMap)
        val tmpOut: Allocation = Allocation.createFromBitmap(rs, outputBitMap)
        theIntrinsics.setRadius(radius)
        theIntrinsics.setInput(tmpIn)
        theIntrinsics.forEach(tmpOut)
        tmpOut.copyTo(outputBitMap)

        return outputBitMap
    }
}