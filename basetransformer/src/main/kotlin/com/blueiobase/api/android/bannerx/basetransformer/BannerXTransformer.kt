package com.blueiobase.api.android.bannerx.basetransformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * The base class for all [Transformers][ViewPager2.PageTransformer] provided for `BannerX`.
 * @author IODevBlue
 * @since 1.0.0
 */
abstract class BannerXTransformer: ViewPager2.PageTransformer {

    override fun transformPage(banner: View, position: Float) {
        val viewPager2 =
            if(banner.parent is ViewPager2) banner.parent as ViewPager2
            else return
        when(val bp = bannerPosition(viewPager2, banner)) {
            in -1F..0F -> transformLeftBanner(banner, bp)
            in 0F..1F -> transformRightBanner(banner, bp)
            else -> transformInvisibleBanners(banner, bp)
        }
    }

    /**
     * Gets the position of [banner] as it is in the [viewPager2].
     * @param viewPager2 The [ViewPager2] containing [banner]
     * @param banner The [View] whose position should be calculated
     * @return A [Float] indicating position of the [banner]
     * - < -1.0F = Invisible banner to the far left
     * - <= 0.0F = A banner to the immediate left of centered banner
     * - 0 = The banner completely centered and fully on Screen.
     * - <= 1.0 = A banner to the immediate right of centered banner
     * - *>* 1.0F = Invisible banner to the far right
     */
    private fun bannerPosition(viewPager2: ViewPager2, banner: View): Float {
        viewPager2.apply {
            val width = measuredWidth - paddingLeft - paddingRight
            return (banner.left - scrollX - paddingLeft/width).toFloat()
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