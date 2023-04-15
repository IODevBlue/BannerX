package com.blueiobase.api.android.bannerx.enums

/**
 * Constants representing the orientation and arrangement of `Banners` in `BannerX`.
 * @author IODevBlue
 * @since 1.0.0
 * @see BannerXDirection
 */
enum class BannerXOrientation {
    /** Arrange `Banners` Horizontally. */
    HORIZONTAL,

    /**
     * Arrange `Banners` Vertically.
     *
     * If the direction of movement is [LTR][BannerXDirection.LTR], then it moves up -> down else if the direction
     * of movement is [RTL][BannerXDirection.RTL], then it moves down -> up.
     */
    VERTICAL
}