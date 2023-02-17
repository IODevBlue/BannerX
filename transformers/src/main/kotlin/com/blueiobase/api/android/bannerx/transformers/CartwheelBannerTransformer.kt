package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.basetransformer.BannerXTransformer

/**
 * This performs a cartwheel on the current Banner and spins it:
 * - to the left to reveal the rightward Banner.
 * - to the right to reveal the leftward Banner.
 *
 * Transformation is only performed between two Banners at the same time which are adjacent each other.
 * @author IODevBlue
 * @since 1.0.0
 */
class CartwheelBannerTransformer: BannerXTransformer() {

    /** The degree of rotation (between `0.0F - 40.0F`) applied to the cartwheel. **Default:** 13.5F degrees. */
    var cartwheelRotation = 13.5F
        set(value) {
            if(value in 0.0F..40.0F) {
                field = value
            }
        }

    override fun transformLeftBanner(view: View, position: Float) {
        view.apply {
            pivotX = measuredWidth * 0.5F
            pivotY = measuredHeight.toFloat()
            rotation = cartwheelRotation * position
        }
    }

    override fun transformRightBanner(view: View, position: Float) {
        transformLeftBanner(view, position)
    }

    override fun transformInvisibleBanners(view: View, position: Float) {
        view.apply {
            pivotX = measuredWidth * 0.5F
            pivotY = measuredHeight.toFloat()
            rotation = 0F
        }
    }
}