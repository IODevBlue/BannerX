package com.blueiobase.api.android.bannerx.transformers

import android.os.Parcel
import android.view.View
import com.blueiobase.api.android.bannerx.basetransformer.BannerXTransformer
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

/**
 * This zooms in the rightward Banner from the center (when entering the screen) while simultaneously zooming out the leftward Banner inwardly towards the
 * center.
 * Also vice versa, it zooms out the rightward Banner towards the center (when exiting the screen) while simultaneously zooming in the leftward Banner
 * outwardly from the center.
 *
 * A fade animation can be applied along with the transformation to make it more appealing. This is disabled by default.
 *
 * Transformation is only performed between two Banners at the same time which are adjacent each other.
 *
 * **NOTE:** This Transformer does not operate well when `applyBannerOnClickScale` XML attribute is set to `true` or when
 * `BannerScaleAnimateParams` is operational in `BannerX`. This is because both classes would attempt to manipulate the `scaleX` and `scaleY` value
 * of a `Banner` simultaneously which distorts the animation. It is recommended to remove usage of the `BannerScaleAnimateParams` when using
 * this transformer.
 * @author IODevBlue
 * @since 1.0.0
 */
@Parcelize
class CenterScaleBannerTransformer: BannerXTransformer() {

    private companion object: Parceler<CenterScaleBannerTransformer> {
        override fun create(parcel: Parcel): CenterScaleBannerTransformer {
            return CenterScaleBannerTransformer().apply {
                applyFade = when(parcel.readInt()) {
                    0 -> false
                    else -> true
                }
            }
        }

        override fun CenterScaleBannerTransformer.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(if(applyFade) 1 else 0)
        }

    }


    /** Make the [CenterScaleBannerTransformer] apply dynamic alpha values to adjacent Banners. */
    var applyFade = false

    override fun transformLeftBanner(view: View, position: Float) {
        view.apply {
            position.let {
                translationX = -width * it
                pivotX = width * 0.5F
                pivotY = height * 0.5F
                scaleX = 1 + it
                scaleY = 1 + it
                alpha = if(applyFade) 1 + it
                else if(it < -0.95F) 0F else 1F
            }
        }
    }

    override fun transformRightBanner(view: View, position: Float) {
        view.apply {
            position.let {
                translationX = -width * it
                pivotX = width * 0.5F
                pivotY = height * 0.5F
                scaleX = 1 - it
                scaleY = 1 - it
                alpha = if(applyFade) 1 - it
                else if(it > 0.95F) 0F else 1F
            }
        }
    }

    override fun transformInvisibleBanners(view: View, position: Float) {}
}