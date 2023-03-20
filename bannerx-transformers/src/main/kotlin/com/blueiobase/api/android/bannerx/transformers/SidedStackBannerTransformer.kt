package com.blueiobase.api.android.bannerx.transformers

import android.view.View
import com.blueiobase.api.android.bannerx.basetransformer.BannerXTransformer
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

/**
 * This [BannerXTransformer] stacks `Banners` on top each other on the right hand side and translates
 * the top most Banner to the left offscreen.
 *
 * This is similar to the [OverlayBannerTransformer] but without leftward Banners.
 *
 *  **NOTE:** This Transformer might not operate well when `applyBannerOnClickScale` XML attribute is set to `true` or when
 * `BannerScaleAnimateParams` is operational in `BannerX`. This is because both classes would attempt to manipulate the `scaleX` and `scaleY` value
 * of a `Banner` simultaneously which distorts the animation. It is recommended to remove usage of the `BannerScaleAnimateParams` when using
 * this transformer.
 * @author IODevBlue
 * @since 1.0.0
 */
@Parcelize
class SidedStackBannerTransformer (
    /** The amount of Banners that would be preloaded offscreen. */
    private val offScreenLimit: Int = 3): BannerXTransformer() {

    @IgnoredOnParcel
    /** Functor to calculate scale applied to the X and Y locations of the animated [View]. */
    private val scaleFactor = fun(pos: Float) = -SCALE_FACTOR * pos + DEFAULT_SCALE
    @IgnoredOnParcel
    /** Functor to verify if the given [Float] in the provided [offScreenLimit]. */
    private val isInPositionLimit = fun(pos: Float): Boolean {
        return when(pos) {
            in 0F..offScreenLimit - 1F -> true
            else -> false
        }
    }
    @IgnoredOnParcel
    /** Functor to evaluate the scaleX and scaleY of the animated [View]. */
    private val elevationScaleGeneric = fun (position: Float, view: View) {
        position.let {
            view.elevation = -abs(it)
            val sf = scaleFactor(it)
            if(isInPositionLimit(it)) {
                view.apply {
                    scaleX = sf
                    scaleY = sf
                    translationX = -(width/ DEFAULT_TRANSLATION_FACTOR) * it
                }
            } else {
                view.apply {
                    scaleX = DEFAULT_SCALE
                    scaleY = DEFAULT_SCALE
                    translationX = DEFAULT_TRANSLATION_X
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_TRANSLATION_X = .0F
        private const val DEFAULT_TRANSLATION_FACTOR = 1.46F
        private const val SCALE_FACTOR = .14F
        private const val DEFAULT_SCALE = 1F
    }

    override fun transformLeftBanner(view: View, position: Float) {
        view.let {
            elevationScaleGeneric(position, view)
            it.alpha = 1F - abs(position)
        }
    }

    override fun transformRightBanner(view: View, position: Float) {
        view.let {
            elevationScaleGeneric(position, view)
            it.alpha = 1F
        }
    }

    override fun transformInvisibleBanners(view: View, position: Float) {
        view.let {
            elevationScaleGeneric(position, it)
            it.alpha = 0.5F
        }
    }
}