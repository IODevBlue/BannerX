package com.blueiobase.api.android.bannerx.basetransformer

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * The default [BannerXTransformer] which provides generic horizontal movement between Banners.
 * @author IODevBlue
 * @since 1.0.0
 */
class DefaultBannerTransformer() : BannerXTransformer() {
    @Suppress("Unused_Parameter")
    constructor(parcel: Parcel) : this()

    override fun transformLeftBanner(view: View, position: Float) {}

    override fun transformRightBanner(view: View, position: Float) {}

    override fun transformInvisibleBanners(view: View, position: Float) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<DefaultBannerTransformer> {

        override fun createFromParcel(parcel: Parcel): DefaultBannerTransformer {
            return DefaultBannerTransformer(parcel)
        }

        override fun newArray(size: Int): Array<DefaultBannerTransformer?> {
            return arrayOfNulls(size)
        }
    }
}