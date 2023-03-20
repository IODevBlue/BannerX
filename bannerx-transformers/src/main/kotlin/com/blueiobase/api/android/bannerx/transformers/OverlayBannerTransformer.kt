package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.basetransformer.BannerXTransformer
import kotlinx.parcelize.Parcelize
import kotlin.math.abs


/**
 * A [BannerXTransformer] which overlays the center-most Banner above the edges of its immediate left and right Banner. It stretches the Y coordinates of
 * the Banner approaching the center.
 *
 * **NOTE:** This Transformer does not operate well when `applyBannerOnClickScale` XML attribute is set to `true` or when
 * `BannerScaleAnimateParams` is operational in `BannerX`. This is because both classes would attempt to manipulate the `scaleX` and `scaleY` value
 * of a `Banner` simultaneously which distorts the animation. It is recommended to remove usage of the `BannerScaleAnimateParams` when using
 * this transformer.
 * @author IODevBlue
 * @since 1.0.0
 */
@Parcelize
class OverlayBannerTransformer(
    /** The scale applied to the `scaleX()` and `scaleY()` respectively for each Banner. **Default:** 0.75F. */
    var scale: Float = 0.75F,
    /** The transparency applied to the immediate left and right Banners adjacent the centered Banner. **Default:** 1F. */
    var alpha: Float = 1F): BannerXTransformer() {

    /**
     * Constructor an [OverlayBannerTransformer] using a [Pair] object
     * @param pair [Pair] whose first value is the applied [scale] while the second value is the applied [alpha].
     */
    @Suppress("Unused")
    constructor(pair: Pair<Float, Float>): this(pair.first, pair.second)

    override fun transformLeftBanner(view: View, position: Float) {
        view.apply {
            alpha = 1 + position * (1 - this@OverlayBannerTransformer.alpha)
            (scale.coerceAtLeast(1 - abs(position))).let {
                scaleX = it
                scaleY = it
            }
            translationX = -width * scale * position
            translationZ = position
        }
    }

    override fun transformRightBanner(view: View, position: Float) {
        view.apply {
            alpha = 1 - position * (1 - this@OverlayBannerTransformer.alpha)
            (scale.coerceAtLeast(1 - abs(position))).let {
                scaleX = it
                scaleY = it
            }
            translationX = width * scale * -position
            translationZ = -position
        }
    }

    override fun transformInvisibleBanners(view: View, position: Float) {
        view.apply {
            alpha = 1F
            scaleX = scale
            scaleY = scale
        }
    }
}