package com.blueiobase.api.android.bannerx.basetransformer

import android.view.View

/**
 * The default [BannerXTransformer] which provides a generic horizontal movement between Banners.
 * @author IODevBlue
 * @since 1.0.0
 */
class DefaultBannerTransformer: BannerXTransformer() {
    override fun transformLeftBanner(view: View, position: Float) {}

    override fun transformRightBanner(view: View, position: Float) {}

    override fun transformInvisibleBanners(view: View, position: Float) {}
}