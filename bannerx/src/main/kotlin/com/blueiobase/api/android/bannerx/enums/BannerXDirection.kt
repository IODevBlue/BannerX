package com.blueiobase.api.android.bannerx.enums

/**
 * Constants representing the direction of movement for `BannerX`.
 * @author IODevBlue
 * @since 1.0.0
 * @see BannerXOrientation
 */
enum class BannerXDirection {
    /**
     * Move `Banners` Left-to-Right.
     *
     * **NOTE:** If the orientation is [VERTICAL][BannerXOrientation.VERTICAL], then the movement direction would
     * be up -> down.
     */
    LTR,
    /**
     * Move `Banners` Right-to-Left.
     *
     * **NOTE:** If the orientation is [VERTICAL][BannerXOrientation.VERTICAL], then the movement direction would
     * be down -> up.
     */
    RTL
}