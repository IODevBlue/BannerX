package com.blueiobase.api.android.bannerx.baseindicator

/**
 * Interface contract for `BannerXIndicator` classes that display `Banner` titles.
 * @author IODevBlue
 * @since 1.0.0
 */
interface BannerTitleDisplayable {

    /**
     * Updates the list of titles to be displayed by the indicator.
     * @param list The new list of `Banner` titles
     */
    fun updateTitleList(list: List<String>)

    /**
     * Toggles the visibility of the `TextView` displaying `Banner` titles.
     * @param display `true` if the `TextView` should be displayed, `false` if otherwise.
     */
    fun displayTitleTextView(display: Boolean)
}