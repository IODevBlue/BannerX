package com.blueiobase.api.android.bannerx.util

import android.animation.*
import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.view.animation.AccelerateDecelerateInterpolator
import java.lang.ref.WeakReference

/**
 * Utility to add press and expand animations to `Banner` objects when pressed or touched.
 * @author IODevBlue
 * @since 1.0.0
 */
internal class BannerScaleAnimate private constructor(
    /** The `Banner` [View] as it is in the adapter. */
    view: View) {

    companion object {
        /**
         * Apply a [BannerScaleAnimate] on the [View].
         * @param view The [View] to apply a scale animation on.
         * @return A valid [BannerScaleAnimate] instance for the [view].
         */
        fun on(view: View): BannerScaleAnimate {
            val td = BannerScaleAnimate(view)
            td.onTouchListener(null)
            return td
        }
    }
    /** The scale factor applied to the [View]. */
    private var scale = BannerScaleAnimateParams.DEFAULT_SCALE
    /** The duration of the scale animation.*/
    private var scaleDuration = BannerScaleAnimateParams.DEFAULT_SCALE_DURATION
    /** The duration of the release animation.*/
    private var releaseDuration = BannerScaleAnimateParams.DEFAULT_RELEASE_DURATION
    /** The interpolator used for the scale animation.*/
    private var scaleInterpolator = AccelerateDecelerateInterpolator()
    /** The interpolator used for the release animation.*/
    private var releaseInterpolator = AccelerateDecelerateInterpolator()
    /** The [AnimatorSet] to play the scale or release animations. */
    private var scaleAnimSet: AnimatorSet? = null

    /** The scale to be applied to the [View] when it is not pressed down. */
    private val defaultScale = view.scaleX
    /** [WeakReference] for the `Banner` [View]. */
    private val weakView = WeakReference(view)



    init {
        weakView.get()!!.isClickable = true
    }

    /**
     * Apply a [View.OnClickListener] on the `Banner`.
     * @param clickListener The listener for click events.
     * @return The [BannerScaleAnimate] instance for method chaining
     */
    fun onClickListener(clickListener: View.OnClickListener?): BannerScaleAnimate {
        weakView.get()?.setOnClickListener(clickListener)
        return this
    }

    /**
     * Apply a [View.OnLongClickListener] on the `Banner`.
     * @param clickListener The listener for long click events.
     * @return The [BannerScaleAnimate] instance for method chaining
     */
    fun onLongClickListener(clickListener: OnLongClickListener?): BannerScaleAnimate {
        weakView.get()?.setOnLongClickListener(clickListener)
        return this
    }

    /**
     * Apply the parameters from the provided [params] on the [BannerScaleAnimate].
     *
     * If the provided [params] is null, scale animation is automatically disabled.
     * @param params The [BannerScaleAnimateParams] with scale parameters.
     * @return The [BannerScaleAnimate] instance for method chaining
     */
    fun withParams(params: BannerScaleAnimateParams?): BannerScaleAnimate {
        if(params != null) {
            params.let {
                scale = it.scale
                scaleDuration = it.scaleDuration
                releaseDuration = it.releaseDuration
                scaleInterpolator = it.scaleInterpolator
                releaseInterpolator = it.releaseInterpolator
            }
        } else { scale = 1F }
        return this
    }

    /**
     * Apply a [View.OnTouchListener] on the `Banner`.
     *
     * **NOTE:** It is best to avoid providing an [eventListener], [BannerScaleAnimate] relies heavily on
     * using its internal [OnTouchListener] to provide animations.
     * @param eventListener The listener for touch events.
     * @return The [BannerScaleAnimate] instance for method chaining
     */
    @SuppressLint("ClickableViewAccessibility")
    fun onTouchListener(eventListener: OnTouchListener?): BannerScaleAnimate {
        weakView.get()?.apply {
            if (eventListener == null) {
                setOnTouchListener { view, motionEvent ->
                    val rect = Rect(view.left, view.top, view.right, view.bottom)
                    if (view.isClickable) {
                        when(motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> doBannerScale(view, scale, scaleDuration, scaleInterpolator)
                            MotionEvent.ACTION_MOVE -> {
                                if (!rect.contains(view.left + motionEvent.x.toInt(), view.top + motionEvent.y.toInt())) {
                                        doBannerScale(view, defaultScale, releaseDuration, releaseInterpolator)
                                    }
                            }
                            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                                doBannerScale(view, defaultScale, releaseDuration, releaseInterpolator)
                            }
                        }
                    }
                    false
                }
            } else {
                setOnTouchListener{ _, motionEvent ->
                    eventListener.onTouch(weakView.get(), motionEvent)
                }
            }
        }
        return this
    }




    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////
    /** Apply a `Banner` scale. */
    private fun doBannerScale(view: View, pushScale: Float, duration: Long, interpolator: TimeInterpolator?) {
        view.animate().cancel()
        scaleAnimSet?.cancel()
        scaleAnimSet = null
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", pushScale).apply { this.interpolator = interpolator; this.duration = duration }
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", pushScale).apply { this.interpolator = interpolator; this.duration = duration }
        scaleAnimSet = AnimatorSet()
        scaleAnimSet!!.play(scaleX).with(scaleY)
        scaleAnimSet!!.start()
    }
}