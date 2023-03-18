package com.blueiobase.api.android.bannerx.baseindicator

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import com.blueiobase.api.android.bannerx.baseindicator.enums.IndicatorHorizontalArrangement
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * The default [BannerXIndicator] for `BannerX`.
 *
 * This indicator supports displaying `Banner` titles and number of `Banner` objects. However, this indicator shows either the indicator panels or
 * number of `Banner` objects but never both.
 *
 * **NOTE:** The list of `Banner` titles are not parceled during re-instantiation of this class, the [updateTitleList] must be manually invoked.
 * @author IODevBlue
 * @since 1.0.0
 */
@Parcelize
class DefaultBannerIndicator(
    @IgnoredOnParcel private val context: Context? = null,
    @IgnoredOnParcel override val params: IndicatorParams? = null): BannerXIndicator(params), BannerTitleDisplayable {

    /** The drawable indicator representing an unselected `Banner` object. */
    @IgnoredOnParcel private var indicatorUnselectedDrawable: Drawable? = null
    /** The drawable indicator representing the currently selected `Banner` object. */
    @IgnoredOnParcel private var indicatorSelectedDrawable: Drawable? = null
    /** The uniform padding value applied to the inner horizontal bounds at the Start and End positions of the `BannerX` indicator. */
    @IgnoredOnParcel @Dimension private var indicatorStartEndPadding = 0
    /** The uniform padding value applied to the inner vertical bounds at the Top and Bottom positions of the `BannerX` indicator. */
    @IgnoredOnParcel @Dimension private var indicatorTopBottomPadding = 0
    /** The uniform margin value applied to the outer horizontal bounds at the Start and End positions of the `BannerX` indicator. */
    @IgnoredOnParcel @Dimension private var indicatorStartEndMargin = 0

    /** The [TextView] present in the [DefaultBannerIndicator] to display `Banner` titles. */
    @IgnoredOnParcel private var indicatorTextView: TextView? = null

    /** Content layout for the [DefaultBannerIndicator] and it is also the direct child to [rootLayout]. */
    @IgnoredOnParcel private var indicatorLayout: LinearLayout? = null
    /** The root layout for the [DefaultBannerIndicator]. */
    @IgnoredOnParcel private var rootLayout: RelativeLayout? = null
    /** A [TextView] indicator widget to display the number of `Banner` objects. */
    @IgnoredOnParcel private var numberIndicatorTextView: TextView? = null
    /** The list of `Banner` titles to be displayed in the [indicatorTextView]. */
    @IgnoredOnParcel private val titleList by lazy { mutableListOf<String>() }
    /** The horizontal alignment `LayoutParams` for the [DefaultBannerIndicator]. */
    @IgnoredOnParcel private val indicatorHorizontalLayoutParams by lazy { RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT) }
    /** The horizontal position of the [DefaultBannerIndicator]. **Default:** [CENTER][IndicatorHorizontalArrangement.CENTER]. */
    @IgnoredOnParcel private var indicatorHorizontalArrangement = IndicatorHorizontalArrangement.CENTER
    /** Append a `'...'` at the end of any long text to truncate it when shown in the [indicatorTextView]. */
    @IgnoredOnParcel private var applyMarqueeOnIndicatorText = false
    /** The background used in the [DefaultBannerIndicator].*/
    @IgnoredOnParcel private var bg: Drawable? = null


    /** The background [Drawable] for the [DefaultBannerIndicator]. */
    @IgnoredOnParcel var indicatorBackgroundDrawable: Drawable? = null
        set(value) {
            field = value
            rootLayout?.background = value
        }

    /** The background [Drawable] for the [DefaultBannerIndicator]'s number indicator widget. */
    @IgnoredOnParcel var numberIndicatorBackgroundDrawable: Drawable? = null
        set(value) {
            field = value
            numberIndicatorTextView?.background = value
        }
    /**
     * Enable or disable the [DefaultBannerIndicator] from showing Banner titles. It is disabled by default
     * @see supportsBannerTitles
     * @see allowNumberIndicator
     */
    @IgnoredOnParcel var showIndicatorText = false
        set(value) {
            field = value
            if(value) {
                indicatorTextView?.visibility = View.VISIBLE
            } else {
                indicatorTextView?.visibility = View.GONE
            }
        }
    /**
     * Enable or disable displaying number of banners on the [DefaultBannerIndicator]. It is disabled by default.
     * @see showIndicatorText
     */
    @IgnoredOnParcel var allowNumberIndicator = false
        set(value) {
            field = value
            if(value) {
                numberIndicatorTextView?.visibility = View.VISIBLE
            } else {
                numberIndicatorTextView?.visibility = View.GONE
            }
        }
    /** The [Typeface] applied to texts displayed by the [DefaultBannerIndicator]. */
    @IgnoredOnParcel var indicatorTextTypeface: Typeface? = Typeface.DEFAULT
        set(value) {
            value.let {
                field = value
                numberIndicatorTextView?.typeface = it
                indicatorTextView?.typeface = it
            }
        }
    /** Color applied to texts displayed by the [DefaultBannerIndicator]. */
    @IgnoredOnParcel @ColorInt var indicatorTextColor = 0
        set(value) {
            value.let {
                field = it
                numberIndicatorTextView?.setTextColor(it)
                indicatorTextView?.setTextColor(it)

            }
        }
    /** Text size applied to texts displayed by the [DefaultBannerIndicator]. */
    @IgnoredOnParcel @Dimension var indicatorTextSize = 0F
        set(value) {
            value.let {
                field = it
                numberIndicatorTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
                indicatorTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
            }
        }
    /** Enable or disable a fade animation when the [DefaultBannerIndicator] is idle. **Default:** `false`. */
    @IgnoredOnParcel var indicatorFadeOnIdle = false
    /** The time it takes for [DefaultBannerIndicator] to be visible when idle before it fades away. */
    @IgnoredOnParcel var indicatorFadeOnIdleDuration = 0L



    init {
        context?.let {
            rootLayout = RelativeLayout(it)
            params?.let{ itp -> setupWithParams(itp) }
            bg  = ColorDrawable(ContextCompat.getColor(it, R.color.default_indicator_bg)).also { bgc ->
                indicatorBackgroundDrawable = bgc
                numberIndicatorBackgroundDrawable = bgc
            }
            indicatorUnselectedDrawable = ContextCompat.getDrawable(it, R.drawable.dot_unselected)
            indicatorSelectedDrawable = ContextCompat.getDrawable(it, R.drawable.dot_selected)
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // OVERRIDE METHODS
    ///////////////////////////////////////////////////////////////////////////
    override fun resetUsingParams(params: IndicatorParams) {
        rootLayout?.apply {
            removeAllViews()
            setupWithParams(params)
        }
    }

    override fun supportsBannerTitles() = true

    override fun getIndicatorView() = rootLayout

    override fun onBannerScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        removeFadeOnIdle()
        if(showIndicatorText) {
            indicatorTextView?.let {
                if(titleList.isNotEmpty()) {
                    titleList.let {itl->
                        if(positionOffset > 0.5) {
                            it.text = itl[if(position == itl.size-1) position else position + 1]
                            it.alpha = positionOffset
                        } else {
                            it.text = itl[position]
                            it.alpha = 1 - positionOffset
                        }
                    }
                }
            }
        }
    }

    override fun onBannerSelected(position: Int) {
        selectPanel(position)
        if(titleList.isNotEmpty()) {
            if(showIndicatorText) {
                indicatorTextView?.let {
                    it.text = titleList[if(position >= titleList.size) 0 else position]
                }
            }
            if (allowNumberIndicator) {
                numberIndicatorTextView?.let {
                    it.text = String.format("%d/%d", position+1, titleList.size)
                }
            }
        }
        applyFadeOnIdle()
    }

    override fun setContext(context: Context): BannerXIndicator {
        context.let {
            rootLayout = RelativeLayout(it)
            params?.let{ itp -> setupWithParams(itp) }
            bg  = ColorDrawable(ContextCompat.getColor(it, R.color.default_indicator_bg)).also { bgc ->
                indicatorBackgroundDrawable = bgc
                numberIndicatorBackgroundDrawable = bgc
            }
            indicatorUnselectedDrawable = ContextCompat.getDrawable(it, R.drawable.dot_unselected)
            indicatorSelectedDrawable = ContextCompat.getDrawable(it, R.drawable.dot_selected)
        }
        return this
    }

    override fun updateTitleList(list: List<String>) {
        titleList.apply {
            clear()
            titleList.addAll(list)
            inflatePanels(list.size)
        }
    }

    override fun displayTitleTextView(display: Boolean) {
        if(display) {
            indicatorTextView?.visibility = View.VISIBLE
            numberIndicatorTextView?.visibility = View.VISIBLE
        } else {
            indicatorTextView?.visibility = View.GONE
            numberIndicatorTextView?.visibility = View.GONE
        }
    }

    override fun applyFadeOnIdle() {
        if(indicatorFadeOnIdle)  {
            rootLayout?.apply {
                postDelayed(
                    {
                        animate().cancel()
                        animate().alpha(0F).duration = 300
                    }, indicatorFadeOnIdleDuration)
            }
        }
    }

    override fun removeFadeOnIdle() {
        if(indicatorFadeOnIdle)  {
            rootLayout?.apply {
                animate().cancel()
                animate().alpha(1F).duration = 300

            }
        }
    }





    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Internal function to select an indicator panel indicating the currently selected `Banner`.
     * @param position The position to switch to.
     */
    private fun selectPanel(position: Int) {
        indicatorLayout?.apply {
            for (i in 0..childCount) {
                if (i == position) {
                    (getChildAt(i) as? ImageView)?.setImageDrawable(indicatorSelectedDrawable)
                } else {
                    (getChildAt(i) as? ImageView)?.setImageDrawable(indicatorUnselectedDrawable)
                }
                getChildAt(i)?.requestLayout()
            }
        }
    }

    /** Inflate the [DefaultBannerIndicator] panels. */
    private fun inflatePanels(numberOfPanels: Int) {
        indicatorLayout?.apply {
            removeAllViews()
            val lp = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            lp.let { lpi->
                lpi.gravity = Gravity.CENTER_VERTICAL
                lpi.setMargins(indicatorStartEndPadding, indicatorTopBottomPadding, indicatorStartEndPadding, indicatorTopBottomPadding)
            }
            for(i in 0 until numberOfPanels) {
                ImageView(context).let { iv ->
                    iv.layoutParams = lp
                    iv.setImageDrawable(indicatorUnselectedDrawable)
                    this@apply.addView(iv)
                }
            }
        }
        numberIndicatorTextView?.let {
            if(allowNumberIndicator) {
                if(titleList.size > 1) {
                    it.visibility = View.VISIBLE
                } else {
                    it.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Sets up the [DefaultBannerIndicator] with these [params]
     * @param params Parameters for setup
     */
    private fun setupWithParams(params: IndicatorParams) {
        rootLayout?.apply {
            params.let {
                val id = R.id.indicator_text_id
                background = it.indicatorBackgroundDrawable?:indicatorBackgroundDrawable.also { ibd -> indicatorBackgroundDrawable = ibd }
                setPaddingRelative(it.indicatorStartEndMargin, it.indicatorTopBottomPadding, it.indicatorStartEndMargin, it.indicatorTopBottomPadding)
                indicatorStartEndMargin = it.indicatorStartEndMargin
                indicatorTopBottomPadding = it.indicatorTopBottomPadding
                indicatorStartEndPadding = it.indicatorStartEndPadding
                showIndicatorText = it.showIndicatorText
                it.indicatorTextTypeface?.let { itt -> indicatorTextTypeface = itt }
                indicatorTextColor = it.indicatorTextColor
                indicatorTextSize = it.indicatorTextSize
                applyMarqueeOnIndicatorText = it.applyMarqueeOnIndicatorText
                it.indicatorSelectedDrawable?.let{ isd -> indicatorSelectedDrawable = isd }
                it.indicatorUnselectedDrawable?.let { iud -> indicatorUnselectedDrawable = iud}
                it.numberIndicatorBackgroundDrawable?.let { nid -> numberIndicatorBackgroundDrawable = nid }
                indicatorHorizontalArrangement = when(it.indicatorHorizontalArrangement) {
                    0L -> IndicatorHorizontalArrangement.START
                    2L -> IndicatorHorizontalArrangement.END
                    else -> IndicatorHorizontalArrangement.CENTER
                }
                allowNumberIndicator = it.allowNumberIndicator.also { ani ->
                    if(ani) {
                        numberIndicatorTextView = TextView(context)
                        numberIndicatorTextView!!.let { nit ->
                            nit.id = id
                            nit.gravity = Gravity.CENTER
                            nit.maxLines = 1
                            nit.ellipsize = TextUtils.TruncateAt.END
                            nit.typeface = indicatorTextTypeface
                            nit.setTextColor(indicatorTextColor)
                            nit.setTextSize(TypedValue.COMPLEX_UNIT_PX, indicatorTextSize)
                            nit.visibility = View.INVISIBLE
                            nit.background = numberIndicatorBackgroundDrawable
                            this.addView(nit, indicatorHorizontalLayoutParams)
                        }
                    } else {
                        indicatorLayout = LinearLayout(context)
                        indicatorLayout!!.apply {
                            orientation = LinearLayout.HORIZONTAL
                            this.id = id
                            rootLayout!!.addView(this, indicatorHorizontalLayoutParams)
                        }
                    }
                }
                val textLP = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                textLP.addRule(RelativeLayout.CENTER_VERTICAL)
                showIndicatorText = it.showIndicatorText.also { sit ->
                    if(sit) {
                        indicatorTextView = TextView(context)
                        indicatorTextView!!.apply {
                            gravity = Gravity.CENTER_VERTICAL
                            maxLines = 1
                            typeface = indicatorTextTypeface
                            if(applyMarqueeOnIndicatorText) {
                                ellipsize = TextUtils.TruncateAt.MARQUEE
                                marqueeRepeatLimit = 3
                                isSelected = true
                            } else {
                                ellipsize = TextUtils.TruncateAt.END
                            }
                            setTextColor(indicatorTextColor)
                            setTextSize(TypedValue.COMPLEX_UNIT_PX, indicatorTextSize)
                        }
                        this.addView(indicatorTextView, textLP)
                    }
                }
                indicatorFadeOnIdle = it.indicatorFadeOnIdle
                indicatorFadeOnIdleDuration = it.indicatorFadeOnIdleDuration

                when(indicatorHorizontalArrangement) {
                    IndicatorHorizontalArrangement.START -> {
                        indicatorHorizontalLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                        indicatorTextView?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        textLP.addRule(RelativeLayout.END_OF, id)
                    }
                    IndicatorHorizontalArrangement.END -> {
                        indicatorHorizontalLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                        textLP.addRule(RelativeLayout.START_OF, id)
                    }
                    else -> {
                        indicatorHorizontalLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                        textLP.addRule(RelativeLayout.START_OF, id)
                    }
                }
                inflatePanels(it.numberOfIndicatorPanels)
            }
        }
    }
}