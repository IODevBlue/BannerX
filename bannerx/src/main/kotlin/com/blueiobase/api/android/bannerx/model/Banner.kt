package com.blueiobase.api.android.bannerx.model

import android.graphics.drawable.Drawable
import com.google.android.material.shape.ShapeAppearanceModel

/**
 * This class represents a single banner.
 * @author IODevBlue
 * @since 1.0.0
 */
data class Banner(
    /** Title for this [Banner]. */
    var title: String = "") {
    /**
     * The [Banner] image.
     *
     * **NOTE:** The image is implementation specific and it can be a `Drawable`, a Drawable resource, a local file, a url string, Uri etc.
     * This however depends on if a custom `Adapter` is provided to `BannerX`.
     *
     * If a custom Adapter is **NOT** provided to `BannerX`, it is highly recommended to make the [Banner] image a `Drawable`, a local `Uri` or a `Bitmap`
     * which is internally detected in `BannerX` else a default placeholder is used.
     *
     * If the image is a local `Uri` and a custom Adapter is not provided to `BannerX, then compression would not be applied to the image.
     */
    var image: Any? = null
    /**
     * The background specific to the [Banner].
     *
     * This is useful in scenarios where the [image] has a dominant color which can be used as its background.
     * It can also function as a [Banner]-specific placeholder when no [image] is ready to be displayed.
     *
     * If no specific background is provided, the default from `BannerX` will be applied.
     */
    var background: Drawable? = null
    /**
     * The [ShapeAppearanceModel] to design edges and corners specific to the [Banner].
     *
     * If no specific shape appearance model is provided, the default from `BannerX` will be applied.
     */
    var shapeAppearanceModel: ShapeAppearanceModel? = null
}