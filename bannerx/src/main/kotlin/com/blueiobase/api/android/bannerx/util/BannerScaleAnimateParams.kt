package com.blueiobase.api.android.bannerx.util

import android.view.animation.AccelerateDecelerateInterpolator


/**
 * This class holds data and properties for applying scale animations to `Banner` objects.
 * @author IODevBlue
 * @since 1.0.0
 */
data class BannerScaleAnimateParams(
    /**
     * The scale factor applied to the `Banner`.
     *
     * A value greater than 1.0F expands the `Banner` while a value less than 1.0F pushes the `Banner` inwards.
     */
    val scale: Float = DEFAULT_SCALE,
    /** The duration of the scale animation. */
    val scaleDuration: Long = DEFAULT_SCALE_DURATION,
    /** The duration of the release animation.*/
    val releaseDuration: Long = DEFAULT_RELEASE_DURATION,
    /** The interpolator used for the scale animation.*/
    val scaleInterpolator: AccelerateDecelerateInterpolator = AccelerateDecelerateInterpolator(),
    /** The interpolator used for the release animation.*/
    val releaseInterpolator: AccelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()) {

    companion object {
        /** The default scale factor. */
        const val DEFAULT_SCALE = 0.955F
        /** The default scale duration. */
        const val DEFAULT_SCALE_DURATION = 50L
        /** The default release duration. */
        const val DEFAULT_RELEASE_DURATION = 125L
    }

}