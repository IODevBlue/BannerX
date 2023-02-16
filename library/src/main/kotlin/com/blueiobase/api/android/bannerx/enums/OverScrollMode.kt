package com.blueiobase.api.android.bannerx.enums

import com.blueiobase.api.android.bannerx.BannerX

/**
 * Constants representing the over-scrolling behaviour of [BannerX] when it over-scrolls.
 * @author IODevBlue
 * @since 1.0.0
 */
enum class OverScrollMode {
    /** Always allow over-scrolling and show over-scroll effect. */
    ALWAYS,
    /** Over-scroll only if [BannerX] is large enough to scroll and show over-scroll effect. */
    IF_CONTENT_SCROLLS,
    /** Over-scrolling is disabled. No over-scroll effect is shown. */
    NEVER
}
