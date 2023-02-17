package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.basetransformer.BannerXTransformer

/**
 * A [BannerXTransformer] involving two Banners which translates the leftward Banner to the left (if exiting the screen) or to the right
 * (if entering the screen) without changes to its alpha while reducing the depth of the rightward Banner and increasing its alpha (if entering the screen) or increasing its
 * depth and reducing its alpha (if exiting the screen).
 *
 * Transformation is only performed between two Banners at the same time which are adjacent each other.
 * @author IODevBlue
 * @since 1.0.0
 */
class DescentBannerTransformer(): BannerXTransformer() {

    /** The minimum depth (between `0.6F to 1.0F`) of the rightward Banner while descending and emerging. **Default:** 0.6F. */
    var descent: Float = 0.6F
        set(value) {
            if(value in 0.6F..1.0F) {
                field = value
            }
        }

    override fun transformLeftBanner(view: View, position: Float) {
        view.apply {
            alpha = 1F
            translationX = 0F
            scaleX = 1F
            scaleY = 1F
        }
    }

    override fun transformRightBanner(view: View, position: Float) {
        view.apply {
            alpha = 1 - position
            translationX = -width * position
            (descent + (1 - descent) * (1 - position)).let {
                scaleX = it
                scaleY = it
            }

        }
    }

    override fun transformInvisibleBanners(view: View, position: Float) {
        view.alpha = 0F
    }
}