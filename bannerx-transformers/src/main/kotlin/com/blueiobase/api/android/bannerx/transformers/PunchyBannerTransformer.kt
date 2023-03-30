package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.transformers.base.BannerXTransformer
import kotlinx.parcelize.Parcelize

/**
 * This pushes/punches in the current `Banner` and applies an alpha value on it as it displays the next `Banner`.
 *
 * Transformation is only performed between two `Banners` at the same time which are adjacent each other.
 *
 *  **NOTE:** This Transformer might not operate well when `applyBannerOnClickScale` XML attribute is set to `true` or when
 * `BannerScaleAnimateParams` is operational in `BannerX`. This is because both classes would attempt to manipulate the `scaleX` and `scaleY` value
 * of a `Banner` simultaneously which distorts the animation. It is recommended to remove usage of the `BannerScaleAnimateParams` when using
 * this transformer.
 * @author IODevBlue
 * @since 1.0.0
 */
@Parcelize
class PunchyBannerTransformer(
    /** The scale applied to the `scaleX()` and `scaleY()` respectively for each Banner. **Default:** 0.75F. */
    var scale: Float = 0.75F,
    /** The transparency applied to the immediate left or right Banners. **Default:** 0.7F. */
    var alpha: Float = 0.7F ): BannerXTransformer() {


    override fun transformLeftBanner(view: View, position: Float) {
        view.apply {
            (scale.coerceAtLeast(1 + position)).let {
                val verticals = height * (1 - it)/2
                val horizontals = width * (1 - it)/2
                translationX = horizontals - verticals/2
                scaleX = it
                scaleY = it
                alpha = this@PunchyBannerTransformer.alpha + (it - this@PunchyBannerTransformer.scale) / (1 - this@PunchyBannerTransformer.scale) * (1 - this@PunchyBannerTransformer.alpha)
            }
        }
    }

    override fun transformRightBanner(view: View, position: Float) {
        view.apply {
            (scale.coerceAtLeast(1 - position)).let {
                val verticals = height * (1 - it)/2
                val horizontals = width * (1 - it)/2
                translationX = horizontals + verticals/2
                scaleX = it
                scaleY = it
                alpha = this@PunchyBannerTransformer.alpha + (it - this@PunchyBannerTransformer.scale) / (1 - this@PunchyBannerTransformer.scale) * (1 - this@PunchyBannerTransformer.alpha)
            }
        }
    }

    override fun transformInvisibleBanners(view: View, position: Float) {
        view.alpha = 0F
    }
}