package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.basetransformer.BannerXTransformer

/**
 * This is the same as the `DefaultBannerTransformer` except that it adds an additional transparency feature to exiting and entering Banners.
 *
 * Transformation is only performed between two Banners at the same time which are adjacent each other.
 * @author IODevBlue
 * @since 1.0.0
 */
class AlphaBannerTransformer(): BannerXTransformer() {

    /** The minimum alpha value between 0.0F to 1.0F. **Default:** 0.5F. */
    var alphaSeed: Float = 0.5F
        set(value) {
            if(value in 0.0F..1.0F) {
                field = value
            }
        }

    override fun transformLeftBanner(view: View, position: Float) {
        view.alpha = (alphaSeed + (1 - alphaSeed) * (1 + position))
    }

    override fun transformRightBanner(view: View, position: Float) {
        view.alpha = (alphaSeed + (1 - alphaSeed) * (1 - position))
    }

    override fun transformInvisibleBanners(view: View, position: Float) {
        view.alpha = 0F
    }
}