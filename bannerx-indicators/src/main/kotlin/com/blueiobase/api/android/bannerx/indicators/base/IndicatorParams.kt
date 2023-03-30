package com.blueiobase.api.android.bannerx.indicators.base

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import com.blueiobase.api.android.bannerx.baseindicator.enums.IndicatorHorizontalArrangement

/**
 * This class holds data and properties that can be shared among different [BannerXIndicator] implementations.
 * @author IODevBlue
 * @since 1.0.0
 */
data class IndicatorParams (
    /** The number of indicator panels that should be displayed. */
    var numberOfIndicatorPanels: Int = 0) {
    /** The drawable for an indicator panel representing an unselected `Banner` object. */
    var indicatorUnselectedDrawable: Drawable? = null
    /** The drawable for an indicator panel representing the currently selected `Banner` object. */
    var indicatorSelectedDrawable: Drawable? = null
    /** The background [Drawable] for a `BannerX` indicator. */
    var indicatorBackgroundDrawable: Drawable? = null
    /** The uniform padding value applied to the inner horizontal bounds at the Start and End positions of the `BannerX` indicator. */
    @Dimension var indicatorStartEndPadding = 0
    /** The uniform padding value applied to the inner vertical bounds at the Top and Bottom positions of the `BannerX` indicator. */
    @Dimension var indicatorTopBottomPadding = 0
    /** The uniform margin value applied to the outer horizontal bounds at the Start and End positions of the `BannerX` indicator. */
    @Dimension var indicatorStartEndMargin = 0
    /** Color of the text used by the indicator's `TextView` if present. */
    @ColorInt var indicatorTextColor = 0
    /** Append a `'...'` at the end of any long text  to truncate it on the indicator's `TextView` if present. */
    var applyMarqueeOnIndicatorText = false
    /** Text size used by the indicator's `TextView` if present. */
    @Dimension var indicatorTextSize = 0F
    /** The [Typeface] applied on the text in the indicator's `TextView` if present. */
    var indicatorTextTypeface: Typeface? = null
    /** Enable or disable the indicator widget's `TextView` if there are any. **Default:** `false`. */
    var showIndicatorText = false
    /** Enable or disable displaying number of banners on the indicator widget if there are any. **Default:** `false`. */
    var allowNumberIndicator = false
    /** The background [Drawable] for the `BannerX` number indicator if there are any. */
    var numberIndicatorBackgroundDrawable: Drawable? = null

    /** The horizontal position of the indicator widget. Should be in the range `[0L..2L]` **Default:** [IndicatorHorizontalArrangement.CENTER] = 1L. */
    var indicatorHorizontalArrangement = 1L
        set(value) {
            field = when(value) {
                !in 0L..2L -> 1L
                else -> value
            }
        }
    /** Apply a fade animation when the indicator widget is idle. **Default:** `false`. */
    var indicatorFadeOnIdle = false
    /** The time it takes for the indicator to be visible when idle before it fades away. */
    var indicatorFadeOnIdleDuration = 0L
}