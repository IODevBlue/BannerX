package com.blueiobase.api.android.bannerx.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Singleton object containing utility methods for the `BannerX` library.
 * @author IODevBlue
 * @since 1.0.0
 */
internal object Util {

    /**
     * Converts Density-Independent Pixels to Pixels
     * @param dp Density-Independent Pixels in [Integer]
     * @return Pixels in [Integer]
     */
    fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()

    /**
     * Converts Converts Scale-Independent Pixels to Pixels
     * @param sp Scale-Independent Pixels in [Float]
     * @return Pixels in [Integer]
     */
    fun spToPx(context: Context, sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
    }

    /**
     * Tints the provided [drawable] to the specified [color].
     * @param context The [Context] instance to perform the tint operation
     * @param drawable The [Int] id for the [Drawable] object
     * @param drawableObj The [Drawable] object to tint
     * @param color The color to be applied as the tint
     * @return The tinted [Drawable]
     */
    fun tintDrawable(context: Context, @DrawableRes drawable: Int = 0,
                     drawableObj: Drawable? = null,
                     @ColorInt color: Int): Drawable {
        val d = DrawableCompat.wrap(if(drawable != 0) ContextCompat.getDrawable(context, drawable)!! else drawableObj!!)
        d.setTint(color)
        return d
    }

    /**
     * Compresses the provided bitmap to a smaller size if it surpasses the maximum dimensions provided in
     * the [maxDimens] or 1012 x 1216.
     * @param bitmap The [Bitmap] to compress
     * @param maxDimens A [Pair] of [Float] (width x height) containing the maximum dimensions the [bitmap] should not surpass.
     * @return A smaller sized [Bitmap].
     */
    @JvmOverloads
    fun compressBitmap(bitmap: Bitmap, maxDimens: Pair<Float, Float>? = null): Bitmap {
        var actualHeight: Int = bitmap.height
        var actualWidth: Int = bitmap.width

        val maxWidth = maxDimens?.first?:1012.0f
        val maxHeight = maxDimens?.second?:1216.0f
        var imgRatio = actualWidth.toFloat() / actualHeight
        val maxRatio = maxWidth / maxHeight
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        return Bitmap.createScaledBitmap(bitmap, actualWidth, actualHeight, true)
    }

    /**
     * Validates if the Android version is 33.
     * @return `true` if it Android 33, `false` if otherwise.
     */
    fun isAndroid33() = Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU

}