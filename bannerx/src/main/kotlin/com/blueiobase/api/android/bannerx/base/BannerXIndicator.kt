package com.blueiobase.api.android.bannerx.base

import android.content.Context
import android.os.Parcelable
import android.view.View

/**
 * This class represents an animated indicator anchored at the bottom of `BannerX` to indicate the current `Banner` object.
 *
 * The [IndicatorParams] data class stores data for generally setting up [BannerXIndicator] classes to share and use the same properties.
 * This is to aid in prompt setup if these data are available for uniformity when switching [BannerXIndicator] implementations.
 *
 * Although not required, it is recommended that each implementation of this abstract class should have properties with the
 * same signature name as properties in the [IndicatorParams] which perform similar functions.
 * @author IODevBlue
 * @since 1.0.0
 */
abstract class BannerXIndicator(
    /** Use the properties stored in this [IndicatorParams] object to setup the [BannerXIndicator] instance. */
    open val params: IndicatorParams?): Parcelable {

    /**
     * Reset an existing [BannerXIndicator] instance to use the properties from the provided [params] object.
     * @param params New parameters to be set.
     */
    abstract infix fun resetUsingParams(params: IndicatorParams)
    /**
     * Validates if this [BannerXIndicator] supports displaying `Banner` titles.
     * @return `true` if it can display `Banner` titles, `false` if otherwise.
     */
    abstract fun supportsBannerTitles(): Boolean
    /**
     * Get the [View] representation of the [BannerXIndicator].
     * @return The [BannerXIndicator] in [View] format.
     */
    abstract fun getIndicatorView(): View?
    /**
     * This is invoked by `BannerX` when the current `Banner` is being scrolled.
     * @param position The index position of the current `Banner` being displayed.
     *        The next `Banner` at position+1 will be visible if [positionOffset] is > 0.
     * @param positionOffset [Float] value in the range [0, 1) indicating the offset from the `Banner` at [position].
     * @param positionOffsetPixels Same as [positionOffset] but in pixels.
     */
    abstract fun onBannerScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
    /**
     * This is invoked when a new `Banner` is selected by `BannerX`.
     *
     * This can be used to set a new [BannerXIndicator] panel position.
     * @position The index of the newly selected `Banner`.
     */
    abstract fun onBannerSelected(position: Int)

    /**
     * The [Context] object to be used internally by the [BannerXIndicator] implementation.
     *
     * Since [BannerXIndicator] is a [Parcelable], this method is required to setup the [BannerXIndicator] implementation after
     * recreation of its instance. A [Context] is **NOT** to be put in the parcel used for reconstructing an instance.
     *
     * This method should be called on the [BannerXIndicator] instance before apply it in `BannerX`.
     * @param context The [Context] object.
     * @return The [BannerXIndicator] instance which can be used to chain the [resetUsingParams] method.
     */
    abstract fun setContext(context: Context): BannerXIndicator
    /**
     * Apply a fade animation when the [BannerXIndicator] is idle to hide it.
     * @see removeFadeOnIdle
     */
    abstract fun applyFadeOnIdle()
    /**
     * Apply an animation to gradually display the [BannerXIndicator] when hidden.
     *
     * This method is technically a reverse of the [applyFadeOnIdle] operation.
     */
    abstract fun removeFadeOnIdle()
}