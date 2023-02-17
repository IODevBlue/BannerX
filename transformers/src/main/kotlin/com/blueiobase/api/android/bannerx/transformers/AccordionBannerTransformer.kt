package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.basetransformer.BannerXTransformer

/**
 * This [BannerXTransformer] compresses the currently viewed Banner while simultaneously displaying the next Banner like an accordion.
 *
 * Transformation is only performed between two Banners at the same time which are adjacent each other.
 * @author IODevBlue
 * @since 1.0.0
 */
class AccordionBannerTransformer: BannerXTransformer() {

    override fun transformLeftBanner(view: View, position: Float) {
        view.apply {
            pivotX = width.toFloat()
            scaleX = 1.0F + position
        }
    }

    override fun transformRightBanner(view: View, position: Float) {
        view.apply {
            pivotX = 0F
            scaleX = 1.0F - position
        }
    }

    override fun transformInvisibleBanners(view: View, position: Float) {}
}