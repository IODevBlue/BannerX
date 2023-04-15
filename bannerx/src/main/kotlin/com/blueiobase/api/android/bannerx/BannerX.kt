package com.blueiobase.api.android.bannerx

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.blueiobase.api.android.bannerx.BannerX.OnBannerChangeListener
import com.blueiobase.api.android.bannerx.base.*
import com.blueiobase.api.android.bannerx.enums.*
import com.blueiobase.api.android.bannerx.model.Banner
import com.blueiobase.api.android.bannerx.util.BannerScaleAnimate
import com.blueiobase.api.android.bannerx.util.BannerScaleAnimateParams
import com.blueiobase.api.android.bannerx.util.Util
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import kotlin.properties.Delegates

/**
 * BannerX is an android widget which provides an intuitive way to display advertisements and slideshows.
 *
 * BannerX uses an internal [ViewPager2] implementation to provide swiping and automated scrolling capabilities.
 * The callback events of this internal [ViewPager2] are provided through the [OnBannerChangeListener] interface.
 *
 * The position of the last viewed [Banner] is internally stored making it easy to continue auto-looping on configuration changes, however the
 * list of [Banner] items and any custom [ViewPager2.PageTransformer] implementations have to be provided during every instance restoration.
 * @author IODevBlue
 * @since 1.0.0
 */
class BannerX @JvmOverloads constructor(context: Context, private val attrs: AttributeSet? = null, defStyle: Int = 0)
    : RelativeLayout(context, attrs, defStyle) {


    companion object {
        /**
         * `WRAP_CONTENT` for [RelativeLayout] in code.
         * @see [RelativeLayout.LayoutParams.WRAP_CONTENT]
         */
        private const val WRAP_CONTENT = LayoutParams.WRAP_CONTENT
        /**
         * `MATCH_PARENT` for [RelativeLayout] in code.
         * @see [RelativeLayout.LayoutParams.MATCH_PARENT]
         */
        private const val MATCH_PARENT = LayoutParams.MATCH_PARENT
        /** [Int] maximum value: **2147483647**. */
        private const val MAX_VALUE = Int.MAX_VALUE

        /**
         * Utility method to apply touch and click scaling to a [View] when using a [CustomAdapter] in [BannerX].
         * @param view The [View] which a scale animation should be applied on when touched on clicked.
         * @param params The [BannerScaleAnimateParams] containing scaling parameters.
         * @param onClickListener The listener for click events.
         * @param onLongClickListener The listener for long click events.
         * @param onTouchListener The listener for touch events. If this parameter is set to `null`, a scale animation is applied to the [view]
         *        when touched. Unless this behavior is to be overridden, it is best to keep this parameter `null`.
         * @see [BannerX.bannerScaleAnimateParams]
         */
        @JvmStatic
        @JvmOverloads
        fun doScaleAnimateOn(view: View, params: BannerScaleAnimateParams, onClickListener: OnClickListener? = null,
                             onLongClickListener: OnLongClickListener? = null,
                             onTouchListener: OnTouchListener? = null) {
            BannerScaleAnimate.on(view).withParams(params).onTouchListener(onTouchListener).onLongClickListener(onLongClickListener).onClickListener(onClickListener)
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // VARIABLES
    ///////////////////////////////////////////////////////////////////////////
    /** Control variable to verify if [BannerX] is being instantiated for the first time. */
    private var mIsFirstStart = true

    /**
     * The [IndicatorParams] to be applied to the current [bannerXIndicator].
     *
     * Changing the [IndicatorParams] instance in [BannerX] immediately applies it to the current [BannerXIndicator].
     */
    var indicatorParams = IndicatorParams()
        set(value) {
            field = value
            field.numberOfIndicatorPanels = mBannerList.size
            if(!mIsFirstStart) {
                restartAutoLoopAfterExec {
                    bannerXIndicator.resetUsingParams(value)
                }
            }
        }
    /**
     * The parameters for the scale animation applied to [Banner] objects when touched or clicked.
     *
     * The [BannerScaleAnimateParams] interferes with the scale animations of several [BannerXTransformer] objects applied on [BannerX],
     * so the use case of applying scaling when pressing and touching a [Banner] would vary.
     *
     * **NOTE:** This is only applied if a [CustomAdapter] is **NOT** available in [BannerX].
     * If a [CustomAdapter] has been provided to [BannerX] then use the [BannerX.doScaleAnimateOn] static method or clear the [CustomAdapter] using [clearCustomAdapter].
     */
    var bannerScaleAnimateParams: BannerScaleAnimateParams? = null
        set(value) {
            field = value
            if (!mIsFirstStart && (mCustomAdapter == null || value == null)) {
                stopAutoLoop()
                initViewPager()
            }
        }
    /**
     * The [BannerXTransformer] to handling swiping animations.
     * @see setCustomPageTransformer
     */
    var bannerXTransformer: BannerXTransformer = DefaultBannerTransformer()
        set(value) {
            field = value
            stopAutoLoop()
            if(!mIsFirstStart && !mIsSavedInstance) initViewPager() //Necessary because some transformers alter alpha, translationX/Y values etc
        }
    /**
     * The [BannerXIndicator] widget for displaying the number of [Banner] objects.
     *
     * Setting a new indicator will reuse the parameters defined in [indicatorParams].
     *
     * If reusing the current [indicatorParams] instance is not the intended behaviour, then invoking
     * `setBannerXIndicator()` (on JVM) or changing the value though property access in Kotlin is not advisable.
     *
     * Rather, reconfigure the parameters in the current [indicatorParams] instance then proceed to change the [BannerXIndicator].
     */
    var bannerXIndicator: BannerXIndicator = DefaultBannerIndicator(context)
        set(value) {
            field.getIndicatorView()?.let { iv -> if (this@BannerX == iv.parent) removeView(iv) }
            value.let {
                field = it
                if(!mIsFirstStart && !mIsSavedInstance) {
                    restartAutoLoopAfterExec {
                        if(it.getIndicatorView() == null) it.setContext(context)
                        it.getIndicatorView()?.let { giv ->
                            (giv.parent as? ViewGroup)?.removeView(giv)
                            it.resetUsingParams(indicatorParams)
                            this@BannerX.addView(giv, mIndicatorVerticalLayoutParams)
                            giv.visibility = if(allowIndicator) VISIBLE else GONE
                        }
                        if(it.supportsBannerTitles()) {
                            val titleList = arrayListOf<String>()
                            mBannerList.forEach {mbl-> titleList.add(mbl.title) }
                            (it as BannerTitleDisplayable).updateTitleList(titleList)
                        }
                    }
                }
            }
        }
   /** The vertical position of the current [indicator][bannerXIndicator]. **Default:** [IndicatorVerticalAlignment.BOTTOM] */
    var indicatorVerticalAlignment: IndicatorVerticalAlignment = IndicatorVerticalAlignment.BOTTOM
        set(value){
            field = value
            when(value) {
                IndicatorVerticalAlignment.TOP -> {
                    mIndicatorVerticalLayoutParams.apply {
                        removeRule(ALIGN_PARENT_BOTTOM)
                        addRule(ALIGN_PARENT_TOP)
                    }
                }
                else -> {
                    mIndicatorVerticalLayoutParams.apply {
                        removeRule(ALIGN_PARENT_TOP)
                        addRule(ALIGN_PARENT_BOTTOM)
                    }
                }
            }
            if(!mIsFirstStart && !mIsSavedInstance) {
                bannerXIndicator.getIndicatorView()?.let {
                    if(this@BannerX == it.parent) removeView(it)
                    this@BannerX.addView(it, mIndicatorVerticalLayoutParams)
                }
            }
    }

    /**
     * Listener for [Banner] scroll state changes.
     * @see ScrollState
     */
    var onBannerChangeListener: OnBannerChangeListener? = null
    /**
     * Listener for clicks on [Banner] items.
     * @see setOnBannerClickListener
     */
    var onBannerClickListener: OnBannerClickListener? = null
    /**
     * Listener for long clicks on [Banner] items.
     * @see setOnBannerLongClickListener
     */
    var onBannerLongClickListener: OnBannerLongClickListener? = null
    /**
     * Listener for double clicks on [Banner] items.
     * @see setOnBannerDoubleClickListener
     */
    var onBannerDoubleClickListener: OnBannerDoubleClickListener? = null
    /**
     * Enable or disable auto-looping/slideshows. Auto-looping is enabled by default.
     * @see isManualLoopable
     */
    var isAutoLoopable = true
        set(value) {
            field = value
            if(!mIsFirstStart && !mIsSavedInstance) {
                if(value) {
                    if(!mActionOnViewPager2TouchCancel)
                        initViewPager()
                    else
                        startAutoLoop()
                } else {
                    stopAutoLoop()
                }
            }
        }
    /**
     * Enable or disable user initiated infinite scrolling. It is disabled by default.
     *
     * This flag is useful in scenarios where [auto-looping][isAutoLoopable] is explicitly disabled and the infinite scrolling feature is still needed.
     * @see isAutoLoopable
     */
    var isManualLoopable = false
    /** Make [BannerX] swipeable. Enabled by default. */
    var isSwipeable = true
        set(value) {
            field = value
            if (!mIsFirstStart && !mIsSavedInstance) {
                stopAutoLoop()
                initViewPager()
            }
        }
    /**
     * Enable clicks on each [Banner]. Enabled by default.
     * @see OnBannerClickListener
     * @see OnBannerDoubleClickListener
     * @see OnBannerLongClickListener
     * @see setOnBannerClickListener
     */
    var isBannerClickable = true
    /** The over-scroll behaviour to be applied to [BannerX] when content has reached the end. **Default:** [ALWAYS][OverScrollMode.ALWAYS]. */
    var overScrollMode = OverScrollMode.ALWAYS
        set(value) {
            field = value
            if (!mIsFirstStart && !mIsSavedInstance) {
                stopAutoLoop()
                initViewPager()
            }
        }
    /**
     * The time it takes for slideshows to swap [Banner] objects. Setting this value restarts auto-looping immediately.
     *
     * **Default:** 5000L (1000L is one second)
     */
    var autoLoopDelay = 5000L
        set(value) {
            field = value
            if (isAutoLoopable && (!mIsFirstStart && !mIsSavedInstance)) {
                restartAutoLoopAfterExec { mAutoScrollAnimator?.startDelay = value }
            }
        }
    /**
     * The swiping speed applied during auto-looping [Banner] objects. Setting this value restarts auto-looping immediately.
     *
     * **Default:** 1000L (1000L is one second)
     */
    var autoLoopSpeed = 1000L
        set(value) {
            field = value
            if (isAutoLoopable  && (!mIsFirstStart && !mIsSavedInstance)) {
                restartAutoLoopAfterExec { mAutoScrollAnimator?.duration = value }
            }
        }
    /**
     * Enable or disable the [bannerXIndicator] widget's [TextView]. It is disabled by default.
     *
     * **NOTE:** For this to have any effect, the current [BannerXIndicator] must be a [BannerTitleDisplayable].
     */
    var showIndicatorText = false
        set(value) {
            value.let {
                field = it
                indicatorParams.showIndicatorText = it
                if((!mIsFirstStart && !mIsSavedInstance) && indicatorSupportsTitles()) (bannerXIndicator as BannerTitleDisplayable).displayTitleTextView(it)
            }
        }
    /** Enable or disable the indicator widget. **Default:** true */
    var allowIndicator = true
        set(value) {
            field = value
            if(!mIsFirstStart && !mIsSavedInstance) bannerXIndicator.getIndicatorView()?.visibility = if(value) VISIBLE else GONE
        }

    /** The direction of movement for [BannerX] when auto-looping. **Default:** [LTR][BannerXDirection.LTR]. */
    var bannerXDirection = BannerXDirection.LTR
    /** The orientation for [BannerX]. */
    var bannerXOrientation = BannerXOrientation.HORIZONTAL
        set(value) {
            field = value
            if(!mIsFirstStart && !mIsSavedInstance) {
                stopAutoLoop()
                mEndAnimationTotally = false
                mAutoScrollAnimator?.apply { removeAllUpdateListeners(); removeAllListeners() }
                mAutoScrollAnimator = null
                buildScrollAnimator()
                initViewPager()
            }
        }
    /**
     * Verifies if [BannerX] auto-looping is running.
     * @see isAutoLoopable
     * @see isStarted
     * @see isStopped
     */
    var isRunning = false
        private set
    /**
     * Verifies if [BannerX] auto-looping has started.
     * @see isAutoLoopable
     * @see isRunning
     * @see isStopped
     */
    var isStarted = false
        private set
    /**
     * Verifies if [BannerX] auto-looping has stopped.
     * @see isAutoLoopable
     * @see isRunning
     * @see isStarted
     */
    var isStopped = true
        private set



    /** The [Drawable] acting as a placeholder for [Banner] objects with no available display [image][Banner.image]. */
    private var mBannerPlaceholderDrawable = ContextCompat.getDrawable(context, R.drawable.place_holder)!!
    /** The tint applied to the [mBannerPlaceholderDrawable]. **Default** BLUE. */
    @ColorInt private var mBannerPlaceholderDrawableTint = 0
    /** The drawable used as the default banner background. **Default:** GREY. */
    private var mBannerDefaultBackground: Drawable? = null
    /**
     * Enables and disables clip mode. It is disabled by default.
     *
     * Clip mode is when [BannerX] removes padding values when scrolling and applies it when scrolling stops.
     */
    private var mIsClipMode = false
    /**
     * The amount of onscreen visibility the left [Banner] would get when it is not centered during [clip mode][mIsClipMode]. **Default:** 30dp
     * @see mClipModeRightBannerMargin
     */
    @Dimension private var mClipModeLeftBannerMargin = Util.dpToPx(30)
    /**
     * The amount of onscreen visibility the right [Banner] would get when it is not centered during [clip mode][mIsClipMode]. **Default:** 30dp
     * @see mClipModeLeftBannerMargin
     */
    @Dimension private var mClipModeRightBannerMargin = Util.dpToPx(30)
    /** The padding value applied to the Top position of [BannerX] when [clip mode][mIsClipMode] is enabled. **Default:** 10dp. */
    @Dimension private var mClipModeTopBannerXMargin = Util.dpToPx(10)
    /** The vertical alignment `LayoutParams` for the [DefaultBannerIndicator]. */
    private val mIndicatorVerticalLayoutParams by lazy { LayoutParams( MATCH_PARENT, WRAP_CONTENT) }
    /** The position of the item currently shown by the [mInternalViewPager2]. */
    private var mPosition = 0
    /** [Float] value in the range [0, 1) indicating the offset from the [Banner] at [mPosition]. */
    private var mPositionOffset = 0F
    /** Internal [ViewPager2] to handle swiping and scrolling of [Banner] objects. */
    private var mInternalViewPager2: ViewPager2? = null
    /** The list of [Banner] objects to be displayed. */
    private val mBannerList = mutableListOf<Banner>()
    /** The margin between the bottom of the [mInternalViewPager2] and the top of the [bannerXIndicator]. */
    private var mBannerBottomMargin = 0
    /** The [CustomAdapter] used to to bind custom [Banner] views. */
    private var mCustomAdapter: CustomAdapter? = null
    /** The image [scale type][ImageView.ScaleType] to apply to a `Banner` [image][Banner.image]. */
    private var mBannerImageScaleType = ImageView.ScaleType.FIT_CENTER
    /** Verifies if large [Banner] images should be compressed if their dimensions are too large (1012 x 1216). */
    private var mBannerImageCompress = false
    /** The maximum width and height [Pair] to be used when [compression][mBannerImageCompress] is enabled. */
    private var mBannerImageCompressPair: Pair<Float, Float>? = null
    /** The [ValueAnimator] used to animate the [mInternalViewPager2] automatically. */
    private var mAutoScrollAnimator: ValueAnimator? = null
    /** The previous animated [Int] value of the [mAutoScrollAnimator] when it begins animation. */
    private var mPreviousAnimatedValue = 0
    /** The default edges and corners modifier for the internal banner adapter. */
    private var mDefaultShapeAppearanceModel = ShapeAppearanceModel()
    /** Control [Boolean] to verify if [BannerX] is re-instantiating itself through a call to [onRestoreInstanceState]. */
    private var mIsSavedInstance = false
    /** The total width of the [mInternalViewPager2]. */
    private var mWidth = 0
    /** The total height of the [mInternalViewPager2]. */
    private var mHeight: Int by Delegates.observable(0) { _, _, _ ->
        buildScrollAnimator()
        mInternalViewPager2?.viewTreeObserver?.let { if(it.isAlive) it.removeOnGlobalLayoutListener(ogl) }
    }
    /** Listener to get [mWidth] when the layout is completed. */
    private val ogl = ViewTreeObserver.OnGlobalLayoutListener {
        this@BannerX.mWidth = mInternalViewPager2?.width?:0
        this@BannerX.mHeight = mInternalViewPager2?.height?:0
    }
    /** Control [Boolean] to end the [mAutoScrollAnimator] without repeating itself. */
    private var mEndAnimationTotally = false
    /** Control [Boolean] to verify if the [mInternalViewPager2] has a touch listener set. */
    private var mActionOnViewPager2TouchCancel = false
    /** The index of the [Banner] that should be displayed when [restart] is invoked. */
    private var mStartingBanner = -1


    init {
        initAttrs()
        mIsFirstStart = false
        bannerXIndicator.getIndicatorView()?.visibility = if(allowIndicator) VISIBLE else GONE
    }




    ///////////////////////////////////////////////////////////////////////////
    // INTERFACES
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Interface for monitoring and responding to [Banner] changes and state.
     * @see ViewPager2.OnPageChangeCallback
     */
    interface OnBannerChangeListener {
        /**
         * Callback invoked when the current [Banner] is being scrolled.
         * @param position The index position of the current [Banner] being displayed.
         *        The next [Banner] at position+1 will be visible if [positionOffset] is > 0.
         * @param positionOffset [Float] value in the range [0, 1) indicating the offset from the [Banner] at [position].
         * @param positionOffsetPixels Same as [positionOffset] but in pixels.
         */
        fun onBannerScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
        /**
         * Callback invoked when a drag operation is:
         * - Initiated by the user.
         * - Through a [fakeDrag] operation.
         * - When [BannerX] scrolling activity halts.
         * @param state The current scrolling state of [BannerX]
         */
        fun onBannerScrollStateChanged(state: ScrollState)
        /**
         * Callback invoked when a [Banner] is selected.
         *
         * **NOTE:** The scroll animation may not have completed by the time this callback is invoked.
         * @position The index of the newly selected [Banner].
         */
        fun onBannerSelected(position: Int)
    }
    /** Interface for detecting item clicks on [BannerX]. */
    interface OnBannerClickListener {
        /**
         * Callback invoked when a [Banner] is clicked.
         * @param banner The [Banner] currently being displayed in [view].
         * @param view The [View] which displays the [banner].
         * @param position The current position of the [banner] in [BannerX] collection.
         */
        fun onBannerClick(banner: Banner, view: View, position: Int)
    }

    /** Interface for detecting double clicks on [BannerX]. */
    interface OnBannerDoubleClickListener {
        /**
         * Callback invoked when a [Banner] is double clicked.
         * @param banner The [Banner] currently being displayed in [view].
         * @param view The [View] which displays the [banner].
         * @param position The current position of the [banner] in the [BannerX] collection.
         */
        fun onBannerDoubleClick(banner: Banner, view: View, position: Int)
    }
    /** Interface for detecting long clicks on [BannerX]. */
    interface OnBannerLongClickListener {
        /**
         * Callback invoked when a [Banner] is long clicked.
         * @param banner The [Banner] currently being displayed in [view]
         * @param view The [View] which displays the [banner]
         * @param position The current position of the [banner] in the [BannerX] collection.
         */
        fun onBannerLongClickListener(banner: Banner, view: View, position: Int)
    }
    /**
     * [BannerX] adapter for loading [Banner] objects with a custom [View].
     *
     * This is best implemented when there is a need to be in more control of the binding of [Banner] data to [View] objects.
     * It follows the [RecyclerView.Adapter] pattern.
     * @see useCustomAdapter
     */
    interface CustomAdapter {
        /**
         * Callback invoked when [BannerX] needs to bind the [bannerViewHolder] at the specified [position].
         * @param bannerViewHolder The [BannerViewHolder] hosting the visual representation of the [Banner] at [position].
         * @param position The index position of the [Banner] in the list of [Banner] objects to be displayed.
         */
        fun onBindBannerViewHolder(bannerViewHolder: BannerViewHolder, position: Int)
        /**
         * Callback invoked when [BannerX] needs to create a [BannerViewHolder] for a [Banner] item.
         * @param parent The [ViewGroup] which would add the inflated [View] of the created [BannerViewHolder] as a child in its hierarchy.
         * @param viewType The type of the [Banner].
         * @return parent The [BannerViewHolder] hosting the visual representation of a [Banner] item.
         */
        fun onCreateBannerViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder
        /**
         * Gets the type of [Banner].
         *
         * If there is no type differentiation, then return 0.
         * @return The type of [Banner].
         */
        fun getBannerType(position: Int): Int
    }




    ///////////////////////////////////////////////////////////////////////////
    // INNER CLASSES
    ///////////////////////////////////////////////////////////////////////////
    /** This class is used internally by [BannerX] to contain inflated [View] objects for each [Banner] in its collection. */
    open class BannerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    /** Class handling double click detection. */
    private abstract class OnDoubleClickListener: OnClickListener {
       companion object {
           /** The duration between double taps. */
           @JvmStatic private val DURATION = 300L
       }
        /** The last time a click was made. */
        private var lastClickTime = 0L
        /** Verifies if a single tap was made. */
        private var madeTap = true

        override fun onClick(p0: View?) {
            val clickTime = System.currentTimeMillis()
            madeTap = if(clickTime - lastClickTime < DURATION) {
                onDoubleClick(p0!!)
                false
            } else true
            p0?.postDelayed({
                if(madeTap) {
                    onSingleClick()
                }
            }, DURATION)
            lastClickTime = clickTime
        }
        /**
         * Callback invoked when a double click has been detected on [view].
         * @param view The [View] object upon which a double click was detected.
         */
        abstract fun onDoubleClick(view: View)
        /** Callback invoked when a single click has been detected. */
        abstract fun onSingleClick()

    }
    /** A [ViewPager2.OnPageChangeCallback] implementation. */
    private inner class PageChangeCallback : ViewPager2.OnPageChangeCallback() {

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            onBannerChangeListener?.onBannerScrollStateChanged (
                when(state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> ScrollState.DRAGGING
                    ViewPager2.SCROLL_STATE_SETTLING -> ScrollState.SETTLING
                    else -> ScrollState.IDLE
                }
            )
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            this@BannerX.mPositionOffset = positionOffset
            refinePosition(position).let {
                bannerXIndicator.onBannerScrolled(it, positionOffset, positionOffsetPixels)
                onBannerChangeListener?.onBannerScrolled(it, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (mBannerList.isNotEmpty()) {
                refinePosition(position).let {
                    this@BannerX.mPosition = it
                    bannerXIndicator.onBannerSelected(it)
                    onBannerChangeListener?.onBannerSelected(it)
                }
            }
        }
    }
    /** The [RecyclerView.Adapter] for the [mInternalViewPager2]. */
    private inner class BannerXRecyclerViewAdapter: RecyclerView.Adapter<BannerViewHolder>() {
        /** Functor to apply click listeners. */
        private val applyListeners = fun(banner: Banner, view: View, adapterPosition: Int) {
            BannerScaleAnimate.on(view).withParams(bannerScaleAnimateParams).onClickListener(
                object: OnDoubleClickListener() {
                    override fun onDoubleClick(view: View) {
                        if(isBannerClickable) onBannerDoubleClickListener?.onBannerDoubleClick(banner, view, adapterPosition)
                    }

                    override fun onSingleClick() {
                        if(isBannerClickable) onBannerClickListener?.onBannerClick(banner, view, adapterPosition)
                    }
                }
            ).onLongClickListener {
                if (isBannerClickable) onBannerLongClickListener?.onBannerLongClickListener(banner, view, adapterPosition)
                true
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
            val bannerViewHolder =
                if(mCustomAdapter == null) { //If no custom adapter is available.
                    val itemView = LayoutInflater.from(context).inflate(R.layout.item_bannerx_image, parent, false)
                    BannerViewHolder(itemView)
                } else {
                    mCustomAdapter!!.onCreateBannerViewHolder(parent, viewType)
                }
            return bannerViewHolder
        }
        override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
            holder.apply {
                val rf = refinePosition(position)
                if(mBannerList.isNotEmpty()) {
                    val x = mBannerList[rf]
                    if (mCustomAdapter == null) {
                        val imageView = itemView.findViewById<ShapeableImageView>(R.id.item_bannerx_iv)
                        imageView.let {
                            it.scaleType = mBannerImageScaleType
                            it.background = x.background?:mBannerDefaultBackground
                            it.contentDescription = x.title
                            it.shapeAppearanceModel = x.shapeAppearanceModel?:mDefaultShapeAppearanceModel
                            x.image.let { itx->
                                when(itx) {
                                    is Drawable -> it.setImageDrawable(
                                        if(mBannerImageCompress) Util.compressBitmap(itx.toBitmap(), mBannerImageCompressPair).toDrawable(resources)
                                        else itx)
                                    is Bitmap -> it.setImageBitmap(if(mBannerImageCompress) Util.compressBitmap(itx, mBannerImageCompressPair) else itx)
                                    is Uri -> it.setImageURI(itx)
                                    else -> it.setImageDrawable(mBannerPlaceholderDrawable)
                                }
                            }
                        }
                    } else {
                        mCustomAdapter!!.onBindBannerViewHolder(this, rf)
                    }
                    applyListeners(x, itemView, rf)
                }
            }
        }
        override fun getItemCount() = when {
                isAutoLoopable || isManualLoopable -> MAX_VALUE
                else -> numberOfBanners()
            }
        override fun getItemViewType(position: Int): Int {
            return if(mCustomAdapter != null) {
                mCustomAdapter!!.getBannerType(position)
            } else super.getItemViewType(position)
        }

    }





    ///////////////////////////////////////////////////////////////////////////
    // OVERRIDE FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action) {
            MotionEvent.ACTION_DOWN -> stopAutoLoop()
            MotionEvent.ACTION_UP -> { mEndAnimationTotally = false; startAutoLoop() }
            MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(false)
            MotionEvent.ACTION_OUTSIDE -> { mEndAnimationTotally = false; startAutoLoop() }
        }
        return super.dispatchTouchEvent(ev)
    }
    override fun onSaveInstanceState(): Parcelable {
        mAutoScrollAnimator?.apply {
            removeAllUpdateListeners()
            removeAllListeners()
        }
        return Bundle().apply {
            putParcelable("super", super.onSaveInstanceState())
            putInt("position", mPosition)
            putInt("indicatorVerticalAlignment", indicatorVerticalAlignment.ordinal)
            putBoolean("isAutoLoopable", isAutoLoopable)
            putBoolean("isManualLoopable", isManualLoopable)
            putBoolean("isSwipeable", isSwipeable)
            putBoolean("isBannerClickable", isBannerClickable)
            putString("overScrollMode", overScrollMode.name)
            putLong("autoLoopDelay", autoLoopDelay)
            putLong("autoLoopSpeed", autoLoopSpeed)
            putBoolean("showIndicatorText", showIndicatorText)
            putBoolean("allowIndicator", allowIndicator)
            putString("bannerXDirection", bannerXDirection.name)
            putString("bannerXOrientation", bannerXOrientation.name)
            putParcelable("transformer", (bannerXTransformer as Parcelable))
            putParcelable("indicator", bannerXIndicator)
        }
    }
    @Suppress("Deprecation")
    override fun onRestoreInstanceState(state: Parcelable?) {
        stopAutoLoop()
        mIsSavedInstance = true
        bannerXIndicator.getIndicatorView()?.let { iv -> if (this@BannerX == iv.parent) removeView(iv) }
        (state as? Bundle)?.let {
            val superParcel = if(Util.isAndroid33()) it.getParcelable("super", Parcelable::class.java) else it.getParcelable("super")
            super.onRestoreInstanceState(superParcel)
            mPosition = it.getInt("position", 0)
            indicatorVerticalAlignment =
                when(it.getInt("indicatorVerticalAlignment", 1)) {
                    0 -> IndicatorVerticalAlignment.TOP
                    else -> IndicatorVerticalAlignment.BOTTOM
                }
            isAutoLoopable = it.getBoolean("isAutoLoopable", isAutoLoopable)
            isManualLoopable = it.getBoolean("isManualLoopable", isManualLoopable)
            isSwipeable = it.getBoolean("isSwipeable", isSwipeable)
            isBannerClickable = it.getBoolean("isBannerClickable", isBannerClickable)
            overScrollMode = when(it.getString("overScrollMode", OverScrollMode.ALWAYS.name)) {
                OverScrollMode.IF_CONTENT_SCROLLS.name -> OverScrollMode.IF_CONTENT_SCROLLS
                OverScrollMode.NEVER.name -> OverScrollMode.NEVER
                else -> OverScrollMode.ALWAYS
            }
            autoLoopDelay = it.getLong("autoLoopDelay", autoLoopDelay)
            autoLoopSpeed = it.getLong("autoLoopSpeed", autoLoopSpeed)
            showIndicatorText = it.getBoolean("showIndicatorText", showIndicatorText)
            allowIndicator = it.getBoolean("allowIndicator", allowIndicator)
            bannerXDirection = when(it.getString("bannerXDirection", BannerXDirection.LTR.name)) {
                BannerXDirection.RTL.name -> BannerXDirection.RTL
                else -> BannerXDirection.LTR
            }
            bannerXOrientation = when(it.getString("bannerXOrientation", BannerXOrientation.HORIZONTAL.name)) {
                BannerXOrientation.VERTICAL.name -> BannerXOrientation.VERTICAL
                else -> BannerXOrientation.HORIZONTAL
            }

            (if(Util.isAndroid33()) it.getParcelable("transformer", Parcelable::class.java) else it.getParcelable("transformer"))?.let { bxt ->
                bannerXTransformer = bxt as BannerXTransformer
            }
            (if(Util.isAndroid33()) it.getParcelable("indicator", Parcelable::class.java) else it.getParcelable("indicator"))?.let { bxi ->
                bannerXIndicator = (bxi as BannerXIndicator).setContext(context)
                bannerXIndicator.apply {
                    getIndicatorView()?.let { giv ->
                        if(this@BannerX == giv.parent) removeView(giv)
                        this resetUsingParams indicatorParams
                        this@BannerX.addView(giv, mIndicatorVerticalLayoutParams)
                        giv.visibility = if(allowIndicator) VISIBLE else GONE
                    }
                    if(supportsBannerTitles()) {
                        val titleList = arrayListOf<String>()
                        mBannerList.forEach {mbl-> titleList.add(mbl.title) }
                        (this as BannerTitleDisplayable).updateTitleList(titleList)
                    }
                }
            }
            initViewPager()
        }
        mIsSavedInstance = false
    }
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when(visibility) {
            VISIBLE -> {
                mEndAnimationTotally = false
                startAutoLoop()
                mAutoScrollAnimator?.apply { provideAnimatorUpdateListener(this); provideAnimatorListener(this) }
            }
            INVISIBLE, GONE -> stopAutoLoop()
        }
    }





    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Sets the [ShapeAppearanceModel] used to modify the corners and edges for each [Banner] image.
     *
     * **NOTE:** This is only used when a [CustomAdapter] has **NOT** been set.
     * If a [CustomAdapter] has been provided to [BannerX] either clear it using [clearCustomAdapter], or provide a [ShapeAppearanceModel]
     * for each individual [Banner] using [Banner.shapeAppearanceModel].
     * @param model The [ShapeAppearanceModel] to use for each [Banner] image.
     * @param restart Restart [BannerX] using the [model] for each [Banner] image.
     * @return The current [BannerX] instance. Can be used to chain the [processList] operation.
     */
    @JvmOverloads fun setShapeAppearanceModel(model: ShapeAppearanceModel, restart: Boolean = false): BannerX {
        mDefaultShapeAppearanceModel = model
        if(restart) restart(true)
        return this
    }
    /**
     * Execute a fake drag operation on [BannerX].
     * @param offset The number of pixels to drag by.
     *        A positive offset shows the left [Banner] and a negative offset shows the right [Banner].
     *        For example: An offset of 0.5F shows the half of the immediate leftward [Banner],
     *        an offset of -1 shows the immediate rightward [Banner] completely, an offset of -2 shows 2 rightward [Banner] objects etc.
     * @param duration The duration of the fake drag operation.
     * @param delay The amount of delay applied before the fake drag operation starts.
     * @param interpolator Optional animation interpolator to apply to the fake drag operation.
     */
    @JvmOverloads fun fakeDrag(offset: Float, duration: Long = 0, delay: Long = 0L, interpolator: TimeInterpolator? = null) {
        mInternalViewPager2?.let {
            ValueAnimator.ofInt(0, mWidth).apply {
                addUpdateListener { _-> it.fakeDragBy(offset) }
                addListener (object: Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) { it.beginFakeDrag()}
                    override fun onAnimationEnd(p0: Animator) { it.endFakeDrag() }
                    override fun onAnimationCancel(p0: Animator) { it.endFakeDrag() }
                    override fun onAnimationRepeat(p0: Animator) { /* no-op */}
                })
                if(duration!=0L) this.duration = duration
                if (delay!=0L) startDelay = delay
                interpolator?.let{itp->this.interpolator = itp }
            }.start()
        }
    }
    /**
     * Set a [CustomAdapter] which [BannerX] would use to inflate [Banner] items.
     *
     * **NOTE:** The [CustomAdapter] should be set **BEFORE** a call to [processList]. However if a list of [Banner] objects has already been provided
     * then, [BannerX] needs to reset itself by invoking [restart].
     * @param customAdapter The [CustomAdapter] instance to be used in inflating items
     * @return The current [BannerX] instance. Can be used to chain the [processList] operation.
     */
    fun useCustomAdapter(customAdapter: CustomAdapter): BannerX {
        this.mCustomAdapter = customAdapter
        return this
    }
    /**
     * Clears the [CustomAdapter] provided to [BannerX].
     * @param restart `true` if [BannerX] should restart immediately after clearing the [CustomAdapter],
     *        `false` if otherwise. It restarts immediately by default.
     * @return The current [BannerX] instance to chain other operations.
     */
    @JvmOverloads fun clearCustomAdapter(restart: Boolean = true): BannerX {
        mCustomAdapter = null
        if(restart) restart()
        return this
    }
    /**
     * Starts auto-looping/slideshow.
     * @see isAutoLoopable
     * @see stopAutoLoop
     */
    fun startAutoLoop() {
        if(isAutoLoopable) {
            mAutoScrollAnimator?.start()
            isStarted = true
            isStopped = false
            isRunning = true
        }
    }
    /**
     * Stops the auto-looping/slideshow.
     * @see isAutoLoopable
     * @see startAutoLoop
     */
    fun stopAutoLoop() {
        mEndAnimationTotally = true
        mAutoScrollAnimator?.cancel()
        isStarted = false
        isStopped = true
        isRunning = false
    }
    /**
     * Sets an [OnBannerClickListener] for [BannerX].
     * @param exec The receiver function to be executed for each item clicked.
     */
    inline fun setOnBannerClickListener(crossinline exec: (Banner, View, Int)->Unit) {
        val objc =  object: OnBannerClickListener {
            override fun onBannerClick(banner: Banner, view: View, position: Int) {
                exec(banner, view, position)
            }
        }
        onBannerClickListener = objc
    }
    /**
     * Sets an [OnBannerLongClickListener] for [BannerX].
     * @param exec The receiver function to be executed for each item long clicked.
     */
    inline fun setOnBannerLongClickListener(crossinline exec: (Banner, View, Int)->Unit) {
        val objc=  object: OnBannerLongClickListener {
            override fun onBannerLongClickListener(banner: Banner, view: View, position: Int) {
                exec(banner, view, position)
            }
        }
        onBannerLongClickListener = objc
    }
    /**
     * Sets an [OnBannerDoubleClickListener] for [BannerX].
     * @param exec The receiver function to be executed for each item double clicked.
     */
    inline fun setOnBannerDoubleClickListener(crossinline exec: (Banner, View, Int)->Unit) {
        val objc=  object: OnBannerDoubleClickListener {
            override fun onBannerDoubleClick(banner: Banner, view: View, position: Int) {
                exec(banner, view, position)
            }
        }
        onBannerDoubleClickListener = objc
    }
    /**
     * Gets the number of [Banner] objects this [BannerX] is handling.
     * @return The number of [Banner] objects being handled.
     */
    fun numberOfBanners() = mBannerList.size
    /**
     * Sets the [Banner] at [position] to be displayed.
     * @param position Index position of the [Banner] in the list of [Banner] objects displayed by [BannerX].
     * @param smoothScroll If there should be a smooth scrolling animation when changing [Banner] positions
     */
    @JvmOverloads fun setCurrentBanner(position: Int, smoothScroll: Boolean = false) {
        mInternalViewPager2?.let {
            if(mBannerList.isNotEmpty() && position <= mBannerList.size - 1) {
                if(isManualLoopable || isAutoLoopable) {
                    mAutoScrollAnimator?.let { asa ->
                        if(asa.isStarted) stopAutoLoop()
                        val diff = position - refinePosition(it.currentItem)
                        when {
                            diff < 0 -> for(i in -1..diff) it.setCurrentItem(it.currentItem+1, smoothScroll)
                            diff > 0 -> for(i in 1..diff) it.setCurrentItem(it.currentItem+1, smoothScroll)
                        }
                        mEndAnimationTotally = false
                        startAutoLoop()
                    }
                } else {
                    it.setCurrentItem(position, smoothScroll)
                }
            }
        }
    }
    /**
     * Load the data provided in the [newList] into [BannerX].
     *
     * The [forceUpdateIfEmpty] parameter informs [BannerX] to reject or accept the [newList] if it is empty. It accepts
     * an empty list by default.
     * @param newList The [List] to process.
     * @param forceUpdateIfEmpty `true` If [BannerX] should accept an empty list and clear its data, `false` if otherwise.
     * @param startAtIndex The index of the [Banner] to be displayed first. If the index is greater than the [newList] size,
     *        then the [Banner] at index 0 (zero) is selected.
     */
    @JvmOverloads fun processList(newList: List<Banner>, forceUpdateIfEmpty: Boolean = true, startAtIndex: Int = 0) {
        newList.let {
            if(it.isEmpty()) {
                if(forceUpdateIfEmpty && mBannerList.isNotEmpty()) {
                    mBannerList.clear()
                } else return
                isAutoLoopable = false
                mIsClipMode = false
            }
            mBannerList.clear()
            mBannerList.addAll(it)
            indicatorParams.numberOfIndicatorPanels = mBannerList.size
            mStartingBanner = if(startAtIndex > newList.size - 1 || startAtIndex < 0) 0 else startAtIndex
            if(it.size < 3) mIsClipMode = false
            if(startAtIndex > 0) restart(false, restartAtIndex = startAtIndex) else restart()
        }
    }
    /**
     * Force [BannerX] to invalidate itself.
     * @param retainCurrentPosition `true` if [BannerX] should continue the slideshow from where it stopped,
     *        `false` if it should restart from beginning.
     * @param restartAtIndex The index number of the [Banner] that should be displayed after the restart operation.
     *        For this parameter to be effective, the [retainCurrentPosition] should be set to `false`.
     */
    @JvmOverloads fun restart(retainCurrentPosition: Boolean = true, restartAtIndex: Int = 0) {
        stopAutoLoop()
        mBannerPlaceholderDrawable =
            if(mBannerImageCompress)
                Util.compressBitmap(mBannerPlaceholderDrawable.toBitmap(), mBannerImageCompressPair).toDrawable(resources)
            else mBannerPlaceholderDrawable
        if(mBannerPlaceholderDrawableTint == 0)
            Util.tintDrawable(context, drawableObj = mBannerPlaceholderDrawable, color = ContextCompat.getColor(context, R.color.main_blue))
        bannerXIndicator.apply {
            getIndicatorView()?.let {
                if(this@BannerX == it.parent) removeView(it)
                this resetUsingParams indicatorParams
                this@BannerX.addView(it, mIndicatorVerticalLayoutParams)
                it.visibility = if(allowIndicator) VISIBLE else GONE
            }
            if(supportsBannerTitles()) {
                val titleList = arrayListOf<String>()
                mBannerList.forEach { titleList.add(it.title) }
                (this as BannerTitleDisplayable).updateTitleList(titleList)
            }
        }
        if(!retainCurrentPosition) { mStartingBanner = restartAtIndex }
        initViewPager(retainCurrentPosition)
    }

    /**
     * Set a custom [ViewPager2.PageTransformer] for [BannerX] to use.
     *
     * **NOTE:** Custom [ViewPager2.PageTransformer] implementations are not saved during configuration changes.
     *
     * It is recommended to use any of the provided [BannerXTransformer] for [BannerX] which are saved during configuration changes.
     * @param transformer A custom built [ViewPager2.PageTransformer].
     * @param retainState `true` if [BannerX] should continue pagination on current [Banner], `false` to start from beginning.
     */
    @JvmOverloads fun setCustomPageTransformer(transformer: ViewPager2.PageTransformer, retainState: Boolean = true) {
        stopAutoLoop()
        initViewPager(retainState, transformer)
    }
    /**
     * Display a fixed amount of stub [Banner] objects.
     *
     * **NOTE:** Only a maximum of 5 stubs can be generated.
     * @param numberOfStubs The number of stub [Banner] objects to generate.
     * @param placeholder The image for each generated [Banner]. By default, the library uses the drawable provided
     *        in the XML layout (if any) or uses its generic placeholder.
     */
    @JvmOverloads
    fun displayStubs(@IntRange(from = 1, to = 5) numberOfStubs: Int = 5, placeholder: Drawable? = null) {
        placeholder?.let { mBannerPlaceholderDrawable = it }
        processList(buildList(numberOfStubs) {
            for(i in 1..numberOfStubs) add(Banner())
        })
    }




    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Validates if the current [BannerXIndicator] can display [titles][Banner.title] of [Banner] objects.
     * @return `true` if [bannerXIndicator] can display titles, `false` if otherwise.
     */
    private fun indicatorSupportsTitles() = bannerXIndicator.supportsBannerTitles()
    /**
     * Initializes the [mInternalViewPager2].
     * @param retainState `true` if [ViewPager2] should re-instantiate on the current [mPosition], `false` if otherwise.
     * @param transformer The [ViewPager2.PageTransformer] to use.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initViewPager(retainState: Boolean = true, transformer: ViewPager2.PageTransformer? = null) {
        mInternalViewPager2?.let {
            if(!mIsSavedInstance) mPosition = if(retainState) refinePosition(it.currentItem) else mStartingBanner
            if(this == it.parent) { removeView(it) }
        }
        mInternalViewPager2 = null
        mAutoScrollAnimator?.apply { removeAllUpdateListeners();removeAllListeners() }
        mInternalViewPager2 = ViewPager2(context)
        mInternalViewPager2!!.apply {
            id = generateViewId()
            adapter = BannerXRecyclerViewAdapter()
            orientation = when(bannerXOrientation) {
                BannerXOrientation.HORIZONTAL -> ViewPager2.ORIENTATION_HORIZONTAL
                BannerXOrientation.VERTICAL -> ViewPager2.ORIENTATION_VERTICAL
            }
            registerOnPageChangeCallback(PageChangeCallback())
            overScrollMode =
                when(this@BannerX.overScrollMode) {
                    OverScrollMode.ALWAYS -> OVER_SCROLL_ALWAYS
                    OverScrollMode.IF_CONTENT_SCROLLS -> OVER_SCROLL_IF_CONTENT_SCROLLS
                    else -> OVER_SCROLL_NEVER
                }
            isUserInputEnabled = isSwipeable
            setPageTransformer(transformer?:bannerXTransformer)
            val lp = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            lp.setMargins(0, 0, 0, mBannerBottomMargin)
            if(mIsClipMode) {
                this@BannerX.clipChildren = false
                clipToPadding = false
                offscreenPageLimit = 3
                clipChildren = false
                setPaddingRelative(mClipModeLeftBannerMargin, mClipModeTopBannerXMargin, mClipModeRightBannerMargin, mBannerBottomMargin)
            }
            this@BannerX.addView(this, 0, lp)
            if(mBannerList.isNotEmpty()) {
                if(isAutoLoopable || isManualLoopable) {
                    mEndAnimationTotally = false
                    this.setOnTouchListener { _, motionEvent ->
                        if(isSwipeable) {
                            motionEvent.action.let {
                                mActionOnViewPager2TouchCancel = true
                                if(it == MotionEvent.ACTION_CANCEL || it == MotionEvent.ACTION_UP) { //When user lifts finger up after interrupting autoplaying...
                                    mEndAnimationTotally = false; startAutoLoop() //...execute this action
                                }
                            }
                        }
                        true
                    }
                    mPosition += MAX_VALUE / 2 - (MAX_VALUE / 2) % numberOfBanners()
                }
                setCurrentItem(mPosition, false)
                if(mWidth==0 || mHeight==0) { viewTreeObserver.addOnGlobalLayoutListener(ogl) }
                else if(isAutoLoopable) {
                    mAutoScrollAnimator?.let { asa -> provideAnimatorUpdateListener(asa); provideAnimatorListener(asa) }
                    startAutoLoop()
                }
            }
        }
    }
    /**
     * Gets the mod value of [position] with respects to the [numberOfBanners].
     * @param position An impure [Banner] index.
     * @return The true index of [position] as it is in the [mBannerList].
     */
    private fun refinePosition(position: Int) =  if(numberOfBanners() > 0) { position % numberOfBanners() } else position
    /** Initialize the XML attributes. */
    private fun initAttrs() {
        var stubs = true
        var stubNo: Int
        context.obtainStyledAttributes(attrs, R.styleable.BannerX).apply {
            isAutoLoopable = getBoolean(R.styleable.BannerX_isAutoLoopable, isAutoLoopable)
            isManualLoopable = getBoolean(R.styleable.BannerX_isManualLoopable, isManualLoopable)
            autoLoopDelay = getInteger(R.styleable.BannerX_autoLoopDelay, autoLoopDelay.toInt()).toLong()
            autoLoopSpeed = getInteger(R.styleable.BannerX_autoLoopSpeed, autoLoopSpeed.toInt()).toLong()
            isSwipeable = getBoolean(R.styleable.BannerX_isSwipeable, isSwipeable)
            isBannerClickable = getBoolean(R.styleable.BannerX_isBannerClickable, isBannerClickable)
            bannerXOrientation = when (getInt(R.styleable.BannerX_bannerXOrientation, 0)) {
                0 -> BannerXOrientation.HORIZONTAL
                else -> BannerXOrientation.VERTICAL
            }
            bannerXDirection = when (getInt(R.styleable.BannerX_bannerXDirection, 0)) {
                0 -> BannerXDirection.LTR
                else -> BannerXDirection.RTL
            }
            allowIndicator = getBoolean(R.styleable.BannerX_allowIndicator, allowIndicator)
            getDrawable(R.styleable.BannerX_bannerPlaceholderDrawable)?.let{ mBannerPlaceholderDrawable = it }
            mBannerImageCompress = getBoolean(R.styleable.BannerX_bannerImageCompress, mBannerImageCompress)
            mBannerPlaceholderDrawableTint = getColor(R.styleable.BannerX_bannerPlaceholderDrawableTint, mBannerPlaceholderDrawableTint)
            mBannerDefaultBackground = getDrawable(R.styleable.BannerX_bannerDefaultBackground)
                ?:ColorDrawable(ContextCompat.getColor(context, R.color.grey))
            val compressWidth = getFloat(R.styleable.BannerX_bannerImageCompressMaxWidth, 0F)
            val compressHeight = getFloat(R.styleable.BannerX_bannerImageCompressMaxHeight, 0F)
            if(compressWidth > 0 && compressHeight > 0) { mBannerImageCompressPair = compressWidth to compressHeight }
            mIsClipMode = getBoolean(R.styleable.BannerX_isClipMode, mIsClipMode)
            mBannerBottomMargin = getDimensionPixelSize(R.styleable.BannerX_bannerBottomMargin, mBannerBottomMargin)
            mBannerImageScaleType =
                when(getInt(R.styleable.BannerX_bannerImageScaleType, mBannerImageScaleType.ordinal)) {
                    0 -> ImageView.ScaleType.MATRIX
                    2 -> ImageView.ScaleType.FIT_START
                    3 -> ImageView.ScaleType.FIT_CENTER
                    4 -> ImageView.ScaleType.FIT_END
                    5 -> ImageView.ScaleType.CENTER
                    6 -> ImageView.ScaleType.CENTER_CROP
                    7 -> ImageView.ScaleType.CENTER_INSIDE
                    else -> ImageView.ScaleType.FIT_XY
                }
            bannerScaleAnimateParams =
                if(getBoolean(R.styleable.BannerX_applyBannerOnClickScale, true)) {
                    BannerScaleAnimateParams(
                        getFloat(R.styleable.BannerX_bannerOnClickScale, 0.955F),
                        getInteger(R.styleable.BannerX_bannerOnClickScaleDuration, 50).toLong(),
                        getInteger(R.styleable.BannerX_bannerOnClickScaleReleaseDuration, 125).toLong())
                } else null
            getInt(R.styleable.BannerX_indicatorVerticalAlignment, 1).toLong().also { ivp ->
                mIndicatorVerticalLayoutParams.addRule(when(ivp) {
                    0L -> ALIGN_PARENT_TOP.also { indicatorVerticalAlignment = IndicatorVerticalAlignment.TOP }
                    else -> ALIGN_PARENT_BOTTOM.also { indicatorVerticalAlignment = IndicatorVerticalAlignment.BOTTOM }
                })
            }
            showIndicatorText = getBoolean(R.styleable.BannerX_showIndicatorText, showIndicatorText)
            if(getBoolean(R.styleable.BannerX_clipModeOnIndicator, false)) {
                if(showIndicatorText) {
                    mIndicatorVerticalLayoutParams.setMargins(mClipModeLeftBannerMargin, 0, mClipModeRightBannerMargin, 0)
                } else {
                    mIndicatorVerticalLayoutParams.setMargins(0, 0, 0, 0)
                }
            }
            mClipModeTopBannerXMargin = getDimensionPixelSize(R.styleable.BannerX_clipModeTopBannerXMargin, mClipModeTopBannerXMargin)
            mClipModeLeftBannerMargin =  getDimensionPixelSize(R.styleable.BannerX_clipModeLeftBannerMargin, mClipModeLeftBannerMargin)
            mClipModeRightBannerMargin =  getDimensionPixelSize(R.styleable.BannerX_clipModeRightBannerMargin, mClipModeRightBannerMargin)
            stubs = getBoolean(R.styleable.BannerX_displayStubsOnStart, stubs)
            stubNo = getInteger(R.styleable.BannerX_numberOfStubs, 5)
            if(getBoolean(R.styleable.BannerX_applyBannerImageCornerRadius, true)) {
                ShapeAppearanceModel.Builder().let {
                    mDefaultShapeAppearanceModel = it.setAllCornerSizes(
                        getDimension(R.styleable.BannerX_bannerImageCornerRadius, resources.getDimension(R.dimen.default_corner_radius))
                    ).build()
                }
            }
            indicatorParams.let {
                it.indicatorUnselectedDrawable = getDrawable(R.styleable.BannerX_indicatorUnselectedDrawable)
                it.indicatorSelectedDrawable = getDrawable(R.styleable.BannerX_indicatorSelectedDrawable)
                it.indicatorBackgroundDrawable = getDrawable(R.styleable.BannerX_indicatorBackground)
                it.indicatorStartEndPadding = getDimensionPixelSize(R.styleable.BannerX_indicatorStartEndPadding, Util.dpToPx(3))
                it.indicatorTopBottomPadding = getDimensionPixelSize(R.styleable.BannerX_indicatorTopBottomPadding, Util.dpToPx(6))
                it.indicatorStartEndMargin = getDimensionPixelSize(R.styleable.BannerX_indicatorStartEndMargin, Util.dpToPx(10))
                it.indicatorTextColor = getColor(R.styleable.BannerX_indicatorTextColor, Color.WHITE)
                it.applyMarqueeOnIndicatorText = getBoolean(R.styleable.BannerX_applyMarqueeOnIndicatorText, false)
                it.indicatorTextSize = getDimensionPixelSize(R.styleable.BannerX_indicatorTextSize, Util.spToPx(context, 15F)).toFloat()
                it.indicatorTextTypeface =
                    getResourceId(R.styleable.BannerX_indicatorTextFont, 0).let { itf ->
                        val t = Typeface.DEFAULT
                        when (itf) {
                            in -MAX_VALUE..0 -> t
                            else -> try {
                                ResourcesCompat.getFont(context, itf)?:t
                            } catch(ex: Resources.NotFoundException) {
                                t
                            }
                        }
                    }
                it.allowNumberIndicator = getBoolean(R.styleable.BannerX_allowNumberIndicator, false)
                it.numberIndicatorBackgroundDrawable = getDrawable(R.styleable.BannerX_numberIndicatorBackground)
                it.indicatorHorizontalArrangement =
                    when (getInt(R.styleable.BannerX_indicatorHorizontalArrangement, 1)) {
                        0 -> IndicatorHorizontalArrangement.START
                        2 -> IndicatorHorizontalArrangement.END
                        else -> IndicatorHorizontalArrangement.CENTER
                    }
                it.indicatorFadeOnIdle = getBoolean(R.styleable.BannerX_indicatorFadeOnIdle, false)
                it.indicatorFadeOnIdleDuration = getInteger(R.styleable.BannerX_indicatorFadeOnIdleDuration, 3000).toLong()
            }
        }.recycle()
        if(stubs) displayStubs(stubNo)
    }
    /** Builds the [mAutoScrollAnimator] instance. */
    private fun buildScrollAnimator() {
        mInternalViewPager2?.let {
            mAutoScrollAnimator = ValueAnimator.ofInt(0,
                when(bannerXOrientation) {
                    BannerXOrientation.VERTICAL -> mHeight - if(mIsClipMode) mClipModeTopBannerXMargin+mBannerBottomMargin else 0
                    else -> mWidth - if(mIsClipMode) mClipModeLeftBannerMargin+mClipModeRightBannerMargin else 0
                }
            )
            mAutoScrollAnimator!!.apply {
                provideAnimatorUpdateListener(this)
                provideAnimatorListener(this)
                interpolator = AccelerateDecelerateInterpolator()
                startDelay = autoLoopDelay
                duration = autoLoopSpeed
            }
            startAutoLoop()
        }
    }
    /** Provides a new [ValueAnimator.AnimatorUpdateListener] instance for the [mAutoScrollAnimator]. */
    private fun provideAnimatorUpdateListener(animator: ValueAnimator) {
        animator.addUpdateListener {
            val currentVal = it.animatedValue as Int
            val pxToDrag =
                (currentVal - mPreviousAnimatedValue).toFloat() * when (bannerXDirection) {
                    BannerXDirection.LTR -> -1
                    else -> 1
                }
            mInternalViewPager2?.fakeDragBy(pxToDrag)
            mPreviousAnimatedValue = currentVal
        }
    }
    /** Provides a new [Animator.AnimatorListener] instance for the [mAutoScrollAnimator]. */
    private fun provideAnimatorListener(animator: ValueAnimator) {
        animator.addListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                mInternalViewPager2?.beginFakeDrag()
                isStarted = true
                isStopped = false
                isRunning = true
            }
            override fun onAnimationEnd(p0: Animator) {
                mInternalViewPager2?.endFakeDrag()
                mPreviousAnimatedValue = 0
                isStarted = false
                isStopped = true
                isRunning = false
                if(!mEndAnimationTotally) startAutoLoop()
            }
            override fun onAnimationCancel(p0: Animator) { mInternalViewPager2?.endFakeDrag() }
            override fun onAnimationRepeat(p0: Animator) { /* no-op */ }
        })
    }
    /**
     * Restarts the auto-loop after executing the lambda.
     * @param exec The lambda to execute
     */
    private inline fun restartAutoLoopAfterExec( crossinline exec: ()-> Unit) {
        stopAutoLoop()
        exec()
        mEndAnimationTotally = false
        startAutoLoop()
    }
}