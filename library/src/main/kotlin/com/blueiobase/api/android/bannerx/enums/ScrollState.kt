package com.blueiobase.api.android.bannerx.enums

import com.blueiobase.api.android.bannerx.BannerX
import com.blueiobase.api.android.bannerx.model.Banner

/**
 * Constants for determining the current scrolling state of [BannerX].
 * @author IODevBlue
 * @since 1.0.0
 */
enum class ScrollState {
    /** Indicates that [BannerX] is completely settled and the current [Banner] is fully in view with no pending animation. */
    IDLE,
    /** Indicates that [BannerX] is currently being dragged by the user or via [fakeDrag()][BannerX.fakeDrag]. */
    DRAGGING,
    /** Indicates that [BannerX] is currently settling to a final position. */
    SETTLING
}