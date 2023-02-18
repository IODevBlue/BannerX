package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.basetransformer.BannerXTransformer

/**
 * This flips and turns a Banner over 180 degrees:
 * - to the left to reveal the rightward Banner.
 * - to the right to reveal the leftward Banner.
 *
 * Transformation is only performed between two Banners at the same time which are adjacent each other.
 * @author IODevBlue
 * @since 1.0.0
 */
class FlipBannerTransformer: BannerXTransformer() {

    companion object {
        /** Representing a complete 180 degrees. */
       @JvmStatic private val CIRCLE_ANGLE = 180.0F
    }

    override fun transformLeftBanner(view: View, position: Float) {
        view.apply {
            position.let {
                translationX = -width * it
                rotationY = CIRCLE_ANGLE * it
                if(it > -0.5) {
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.INVISIBLE
                }
            }

        }
    }

    override fun transformRightBanner(view: View, position: Float) {
        view.apply {
            position.let {
                translationX = -width * it
                rotationY = CIRCLE_ANGLE * it
                if(it < 0.5) {
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun transformInvisibleBanners(view: View, position: Float) {}
}