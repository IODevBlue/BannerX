package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.transformers.base.BannerXTransformer
import kotlinx.parcelize.Parcelize

/**
 * This [BannerXTransformer] compresses the currently viewed `Banner` while simultaneously displaying the next Banner like an accordion.
 *
 * Transformation is only performed between two `Banners` at the same time which are adjacent each other.
 *
 * **NOTE:** This Transformer does not operate well when `applyBannerOnClickScale` XML attribute is set to `true` or when
 * `BannerScaleAnimateParams` is operational in `BannerX`. This is because both classes would attempt to manipulate the `scaleX` value
 * of a `Banner` simultaneously which distorts the animation. It is recommended to remove usage of the `BannerScaleAnimateParams` when using
 * this transformer.
 * @author IODevBlue
 * @since 1.0.0
 */
@Parcelize
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