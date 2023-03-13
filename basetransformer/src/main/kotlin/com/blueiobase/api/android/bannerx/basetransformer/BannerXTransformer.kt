package com.blueiobase.api.android.bannerx.basetransformer

import android.os.Parcelable
import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * The base class for all [Transformers][ViewPager2.PageTransformer] provided for `BannerX`.
 *
 * To further understand the [Float] position parameter:
 * - < -1.0F: Invisible banner to the far left
 * - <= 0.0F: A banner to the immediate left of centered banner
 * - 0: The banner completely centered and fully on screen.
 * - <= 1.0: A banner to the immediate right of centered banner
 * - *>* 1.0F: Invisible banner to the far right
 * @author IODevBlue
 * @since 1.0.0
 */
abstract class BannerXTransformer: ViewPager2.PageTransformer, Parcelable {

    override fun transformPage(banner: View, position: Float) {
        when(position) {
            in -1F..0F -> transformLeftBanner(banner, position)
            in 0F..1F -> transformRightBanner(banner, position)
            else -> transformInvisibleBanners(banner, position)
        }
    }

    /**
     * Apply transformations to the immediate left banner.
     * @param view The banner at the immediate left of centered Banner
     * @param position Current position of [view]
     */
    abstract fun transformLeftBanner(view: View, position: Float)
    /**
     * Apply transformations to the immediate right banner.
     * @param view The banner at the immediate right of centered Banner
     * @param position Current position of [view]
     */
    abstract fun transformRightBanner(view: View, position: Float)
    /**
     * Apply transformations to any invisible banner offscreen.
     * @param view An invisible banner
     * @param position Current position of [view]
     */
    abstract fun transformInvisibleBanners(view: View, position: Float)

}