package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.transformers.base.BannerXTransformer
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

/**
 * This is similar to the [OverlayBannerTransformer] except there is no overlay between the center-most `Banner` and its immediate left and right `Banners`.
 *
 *  **NOTE:** This Transformer might not operate well when `applyBannerOnClickScale` XML attribute is set to `true` or when
 * `BannerScaleAnimateParams` is operational in `BannerX`. This is because both classes would attempt to manipulate the `scaleY` value
 * of a `Banner` simultaneously which distorts the animation. It is recommended to remove usage of the `BannerScaleAnimateParams` when using
 * this transformer.
 * @author IODevBlue
 * @since 1.0.0
 */
@Parcelize
class ScaleBannerTransformer(
    /** The minimum vertical stretch that would be applied to each Banner. */
    var verticalStrain: Float = 0.88F
): BannerXTransformer() {

    override fun transformLeftBanner(view: View, position: Float) {
        view.scaleY = verticalStrain.coerceAtLeast(1 - abs(position))
    }

    override fun transformRightBanner(view: View, position: Float) {
        view.scaleY = verticalStrain.coerceAtLeast(1 - abs(position))
    }

    override fun transformInvisibleBanners(view: View, position: Float) {
        view.scaleY = verticalStrain
    }
}