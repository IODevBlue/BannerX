package com.blueiobase.api.android.bannerx.sample.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.blueiobase.api.android.bannerx.BannerX
import com.blueiobase.api.android.bannerx.enums.BannerXDirection
import com.blueiobase.api.android.bannerx.enums.BannerXOrientation
import com.blueiobase.api.android.bannerx.enums.IndicatorHorizontalArrangement
import com.blueiobase.api.android.bannerx.enums.IndicatorVerticalAlignment
import com.blueiobase.api.android.bannerx.model.Banner
import com.blueiobase.api.android.bannerx.sample.MainApplication
import com.blueiobase.api.android.bannerx.sample.R
import com.blueiobase.api.android.bannerx.sample.ui.array.Indicators
import com.blueiobase.api.android.bannerx.sample.ui.array.Transformers
import com.blueiobase.api.android.bannerx.sample.ui.fragment.PhotoDialogFragment
import com.blueiobase.api.android.bannerx.sample.util.makeToast
import com.blueiobase.api.android.bannerx.util.BannerScaleAnimateParams
import com.blueiobase.api.android.networkvalidator.networkValidator

class PlaygroundActivity : AppCompatActivity(),
    CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val app by lazy { application as MainApplication }
    private val bannerx by lazy { findViewById<BannerX>(R.id.bannerx) }
    private val pausePlayButton by lazy { findViewById<ImageButton>(R.id.pause_play_button) }

    private var currentInstruction = 0
    private val textSwitcher by lazy { findViewById<TextSwitcher>(R.id.instructions_text_switcher) }
    private val instructionsArr by lazy { resources.getStringArray(R.array.playground_instructions) }
    private val textSwitchRunnable: Runnable by lazy {
        Runnable {
            try {
                textSwitcher.setText(instructionsArr[currentInstruction++])
                if (currentInstruction >= instructionsArr.size) currentInstruction = 0
            } finally {
                handler.postDelayed(textSwitchRunnable, 5000L)
            }
        }
    }
    private var hasLoaded = false

    private val fadeOnIdleCB by lazy { findViewById<AppCompatCheckBox>(R.id.indicator_fade_on_idle_cb) }
    private val allowNumIndicatorCB by lazy { findViewById<AppCompatCheckBox>(R.id.allow_number_indicator) }
    private val isAutoLoopCB by lazy { findViewById<AppCompatCheckBox>(R.id.isAutoLoop_cb) }
    private val isManualLoopCB by lazy { findViewById<AppCompatCheckBox>(R.id.is_manual_loop_cb) }
    private val isSwipeableCB by lazy { findViewById<AppCompatCheckBox>(R.id.is_swipeable_cb) }
    private val isBannerClickableCB by lazy { findViewById<AppCompatCheckBox>(R.id.is_banner_clickable_cb) }
    private val showIndicatorTextCB by lazy { findViewById<AppCompatCheckBox>(R.id.show_indicator_text_cb) }
    private val allowIndicatorCB by lazy { findViewById<AppCompatCheckBox>(R.id.allow_indicator_cb) }

    private val horizontalArrSpinner by lazy { findViewById<AppCompatSpinner>(R.id.horizontal_arrangement_spinner) }
    private val indicatorsSpinner by lazy { findViewById<AppCompatSpinner>(R.id.indicators_spinner) }
    private val transformersSpinner by lazy { findViewById<AppCompatSpinner>(R.id.transformers_spinner) }
    private val bannerXDirectionSpinner by lazy { findViewById<AppCompatSpinner>(R.id.bannerX_direction_spinner) }
    private val bannerXOrientationSpinner by lazy { findViewById<AppCompatSpinner>(R.id.bannerX_orientation_spinner) }
    private val indicatorVertAlignmentSpinner by lazy { findViewById<AppCompatSpinner>(R.id.indicator_vert_align_spinner) }

    private val indicatorFadeOnIdleEditText by lazy { findViewById<AppCompatEditText>(R.id.indicator_fade_on_idle_dur_et) }
    private val indicatorFadeOnIdleButton by lazy { findViewById<Button>(R.id.apply_fade_on_idle_dur_but) }
    private val releaseDurationEditText by lazy { findViewById<AppCompatEditText>(R.id.release_duration_et) }
    private val releaseDurationButton by lazy { findViewById<Button>(R.id.apply_release_dur_but) }
    private val scaleDurationEditText by lazy { findViewById<AppCompatEditText>(R.id.scale_duration_et) }
    private val scaleDurationButton by lazy { findViewById<Button>(R.id.apply_scale_dur_but) }
    private val scaleEditText by lazy { findViewById<AppCompatEditText>(R.id.scale_et) }
    private val scaleButton by lazy { findViewById<Button>(R.id.apply_scale_but) }
    private val autoLoopDelayEditText by lazy { findViewById<AppCompatEditText>(R.id.auto_loop_delay_et) }
    private val autoLoopDelayButton by lazy { findViewById<Button>(R.id.auto_loop_delay_but) }
    private val autoLoopSpeedEditText by lazy { findViewById<AppCompatEditText>(R.id.auto_loop_speed_et) }
    private val autoLoopSpeedButton by lazy { findViewById<Button>(R.id.auto_loop_speed_but) }

    private val visitPexelButton: Button by lazy { findViewById(R.id.visit_pexel_api) }
    private val pexelApiLink = "https://pexels.com/api"
    private val scaleParams by lazy { BannerScaleAnimateParams() }

    private val indicators by lazy { Indicators() }

    private var isFirstInvoke = true

    private val observer =  Observer<MutableList<Banner>> {
        runOnUiThread {
            if(!hasLoaded && app.loadedCompletely) {
                bannerx.processList(it, forceUpdateIfEmpty = false, intent.extras?.getInt("current_pos")?:0)
                hasLoaded = true
            }
        }
    }
    private val s = fun(b: Boolean) {
        app.apply {
            if(b) {
                try {
                    if(!loadedCompletely && !isQuerying()) { launchQuery() }
                    else {
                        if(!hasLoaded) {
                            bannerx.processList(app.liveData.value!!, false, intent.extras?.getInt("current_pos")?:0)
                            hasLoaded = true
                        }
                    }
                } catch (exception: Exception) {
                    makeToast(exception.cause?.message?:"Error retrieving photographs from Pexels API.")
                }
            } else {
                if (!loadedCompletely) {
                    makeToast("Network error. Please check network connection.")
                    stopQuery()
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playground)
        findViewById<Toolbar>(R.id.playground_toolbar).apply { setNavigationOnClickListener { finish() } }
        textSwitcher.setFactory { layoutInflater.inflate(R.layout.item_text_switcher, null) }
        scaleAnimateSetup(); initComponents(); setupTVClickListeners()
        bannerx.apply {
            setOnBannerClickListener { _, _, i ->
                makeToast("Clicked banner number: $i")
            }
            setOnBannerLongClickListener { b, _, i ->
                if(app.loadedCompletely) {
                    (PhotoDialogFragment(b, app.photographers[i])).show(supportFragmentManager, "Photo_Information")
                }
                makeToast("Long clicked banner number: $i")
            }
            setOnBannerDoubleClickListener {_, _, i ->
                makeToast("Double clicked banner number: $i")
            }
        }
        networkValidator { s(isOnline()) }.setOnNetworkStateChangedListener { b, _ -> runOnUiThread { s(b) } }
        app.liveData.observe(this, observer)
    }
    override fun onStart() {
        super.onStart()
        handler.postDelayed(textSwitchRunnable, 5000L)
    }
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(textSwitchRunnable)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bannerx.processList(app.liveData.value!!)
    }

    private fun setupTVClickListeners() {
        (findViewById<TextView>(R.id.allow_number_indicator_tv)).setOnClickListener { allowNumIndicatorCB.let{ cb-> cb.isChecked = !cb.isChecked } }
        (findViewById<TextView>(R.id.indicator_fade_on_idle_tv)).setOnClickListener { fadeOnIdleCB.let{ cb-> cb.isChecked = !cb.isChecked } }
        (findViewById<TextView>(R.id.horizontal_arrangement_tv)).setOnClickListener { horizontalArrSpinner.performClick() }
        (findViewById<TextView>(R.id.indicators_tv)).setOnClickListener { indicatorsSpinner.performClick() }
        (findViewById<TextView>(R.id.transformers_tv)).setOnClickListener { transformersSpinner.performClick() }
        (findViewById<TextView>(R.id.isAutoLoop_tv)).setOnClickListener { isAutoLoopCB.let{ cb-> cb.isChecked = !cb.isChecked } }
        (findViewById<TextView>(R.id.is_manual_loop_tv)).setOnClickListener { isManualLoopCB.let{ cb-> cb.isChecked = !cb.isChecked } }
        (findViewById<TextView>(R.id.is_swipeable_tv)).setOnClickListener { isSwipeableCB.let{ cb-> cb.isChecked = !cb.isChecked } }
        (findViewById<TextView>(R.id.is_banner_clickable_tv)).setOnClickListener { isBannerClickableCB.let{ cb-> cb.isChecked = !cb.isChecked } }
        (findViewById<TextView>(R.id.show_indicator_text_tv)).setOnClickListener { showIndicatorTextCB.let{ cb-> cb.isChecked = !cb.isChecked } }
        (findViewById<TextView>(R.id.allow_indicator_tv)).setOnClickListener { allowIndicatorCB.let{ cb-> cb.isChecked = !cb.isChecked } }
        (findViewById<TextView>(R.id.bannerX_direction_tv)).setOnClickListener { bannerXDirectionSpinner.performClick() }
        (findViewById<TextView>(R.id.bannerX_orientation_tv)).setOnClickListener { bannerXOrientationSpinner.performClick() }
        (findViewById<TextView>(R.id.indicator_vert_align_tv)).setOnClickListener { indicatorVertAlignmentSpinner.performClick() }

    }

    private fun scaleAnimateSetup() {
        BannerX.apply {
            doScaleAnimateOn(pausePlayButton, scaleParams, onClickListener = {
                isAutoLoopCB.isChecked = !isAutoLoopCB.isChecked
            })
            doScaleAnimateOn(visitPexelButton, scaleParams, onClickListener = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(pexelApiLink)
                startActivity(intent)
            })
            doScaleAnimateOn(indicatorFadeOnIdleButton, scaleParams, onClickListener = {
                val value = indicatorFadeOnIdleEditText.text.toString().toLong()
                bannerx.indicatorParams.let {
                    if(it.indicatorFadeOnIdleDuration != value) bannerx.indicatorParams = it.apply { indicatorFadeOnIdleDuration = value }
                }
            })
            doScaleAnimateOn(releaseDurationButton, scaleParams, onClickListener = {
                val value = releaseDurationEditText.text.toString().toLong()
                bannerx.bannerScaleAnimateParams?.let {
                    if(value != it.releaseDuration) bannerx.bannerScaleAnimateParams = it.copy(releaseDuration = value)
                }
            })
            doScaleAnimateOn(scaleDurationButton, scaleParams, onClickListener = {
                val value = scaleDurationEditText.text.toString().toLong()
                bannerx.bannerScaleAnimateParams?.let {
                    if(value != it.scaleDuration) bannerx.bannerScaleAnimateParams = it.copy(scaleDuration = value)
                }
            })
            doScaleAnimateOn(scaleButton, scaleParams, onClickListener = {
                val value = scaleEditText.text.toString().toFloat()
                bannerx.bannerScaleAnimateParams?.let {
                    if(value != it.scale) bannerx.bannerScaleAnimateParams = it.copy(scale = value)
                }
            })
            doScaleAnimateOn(autoLoopDelayButton, scaleParams, onClickListener = {
                val value = autoLoopDelayEditText.text.toString().toLong()
                bannerx.autoLoopDelay.let {
                    if(value != it) bannerx.autoLoopDelay = value
                }
            })
            doScaleAnimateOn(autoLoopSpeedButton, scaleParams, onClickListener = {
                val value = autoLoopSpeedEditText.text.toString().toLong()
                bannerx.autoLoopSpeed.let {
                    if(value != it) bannerx.autoLoopSpeed = value
                }
            })
        }
    }

    private fun initComponents() {
        with(allowNumIndicatorCB) {
            isChecked = bannerx.indicatorParams.allowNumberIndicator
            setOnCheckedChangeListener(this@PlaygroundActivity)
        }
        with(fadeOnIdleCB) {
            isChecked = bannerx.indicatorParams.indicatorFadeOnIdle
            setOnCheckedChangeListener(this@PlaygroundActivity)
        }
        with(isAutoLoopCB) {
            isChecked = bannerx.isAutoLoopable
            setOnCheckedChangeListener(this@PlaygroundActivity)
        }
        with(isManualLoopCB) {
            isChecked = bannerx.isManualLoopable
            setOnCheckedChangeListener(this@PlaygroundActivity)
        }
        with(isSwipeableCB) {
            isChecked = bannerx.isSwipeable
            setOnCheckedChangeListener(this@PlaygroundActivity)
        }
        with(isBannerClickableCB) {
            isChecked = bannerx.isBannerClickable
            setOnCheckedChangeListener(this@PlaygroundActivity)
        }
        with(showIndicatorTextCB) {
            isChecked = bannerx.showIndicatorText
            setOnCheckedChangeListener(this@PlaygroundActivity)
        }
        with(allowIndicatorCB) {
            isChecked = bannerx.allowIndicator
            setOnCheckedChangeListener(this@PlaygroundActivity)
        }

        with(indicatorFadeOnIdleEditText) {
            clearFocus()
            setText(bannerx.indicatorParams.indicatorFadeOnIdleDuration.toString())
        }
        with(releaseDurationEditText) {
            clearFocus()
            setText(bannerx.bannerScaleAnimateParams!!.releaseDuration.toString())
        }
        with(scaleDurationEditText) {
            clearFocus()
            setText(bannerx.bannerScaleAnimateParams!!.scaleDuration.toString())
        }
        with(scaleEditText) {
            clearFocus()
            setText(bannerx.bannerScaleAnimateParams!!.scale.toString())
        }
        with(autoLoopDelayEditText) {
            clearFocus()
            setText(bannerx.autoLoopDelay.toString())
        }
        with(autoLoopSpeedEditText) {
            clearFocus()
            setText(bannerx.autoLoopSpeed.toString())
        }

        with(ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("START", "CENTER", "END"))) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (horizontalArrSpinner).run {
                adapter = this@with
                setSelection(bannerx.indicatorParams.indicatorHorizontalArrangement.ordinal)
                onItemSelectedListener = this@PlaygroundActivity
            }
        }
        with(ArrayAdapter(this, android.R.layout.simple_spinner_item, indicators.asStringList())) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (indicatorsSpinner).run {
                adapter = this@with
                setSelection(indicators.getIndex(bannerx.bannerXIndicator))
                onItemSelectedListener = this@PlaygroundActivity
            }
        }
        with(ArrayAdapter(this, android.R.layout.simple_spinner_item, Transformers.asStringList())) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (transformersSpinner).run {
                adapter = this@with
                setSelection(Transformers.getIndex(bannerx.bannerXTransformer))
                onItemSelectedListener = this@PlaygroundActivity
            }
        }
        with(ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("LTR", "RTL"))) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (bannerXDirectionSpinner).run {
                adapter = this@with
                setSelection(bannerx.bannerXDirection.ordinal) //LTR
                onItemSelectedListener = this@PlaygroundActivity
            }
        }
        with(ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("HORIZONTAL", "VERTICAL"))) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (bannerXOrientationSpinner).run {
                adapter = this@with
                setSelection(bannerx.bannerXOrientation.ordinal) //Always
                onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if(!isFirstInvoke) { //Bug fix in sample.
                            bannerx.bannerXOrientation = when(p2) {
                                1 -> BannerXOrientation.VERTICAL
                                else -> BannerXOrientation.HORIZONTAL
                            }
                        } else {
                            isFirstInvoke = false
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}

                }
            }
        }
        with(ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("TOP", "BOTTOM"))) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (indicatorVertAlignmentSpinner).run {
                adapter = this@with
                setSelection(bannerx.indicatorVerticalAlignment.ordinal) //Bottom
                onItemSelectedListener = this@PlaygroundActivity
            }
        }
    }


    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when(p0) {
            fadeOnIdleCB -> bannerx.indicatorParams = bannerx.indicatorParams.apply { indicatorFadeOnIdle = p1 }
            allowNumIndicatorCB -> bannerx.indicatorParams = bannerx.indicatorParams.apply { allowNumberIndicator = p1 }
            isAutoLoopCB -> {
                bannerx.isAutoLoopable = p1
                pausePlayButton.setImageDrawable(
                    ContextCompat.getDrawable(this,
                        if(p1) R.drawable.ic_baseline_play_arrow_24
                        else R.drawable.ic_baseline_pause_24)!!
                )
            }
            isManualLoopCB -> bannerx.isManualLoopable = p1
            isSwipeableCB -> bannerx.isSwipeable = p1
            isBannerClickableCB -> bannerx.isBannerClickable = p1
            showIndicatorTextCB -> bannerx.showIndicatorText = p1
            allowIndicatorCB -> bannerx.allowIndicator = p1
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when(p0) {
            horizontalArrSpinner -> {
                bannerx.indicatorParams = bannerx.indicatorParams.apply {
                    indicatorHorizontalArrangement = when(p2) {
                        0 -> IndicatorHorizontalArrangement.START
                        2 -> IndicatorHorizontalArrangement.END
                        else -> IndicatorHorizontalArrangement.CENTER
                    }
                }
            }
            indicatorsSpinner -> bannerx.bannerXIndicator = indicators.list[p2]
            transformersSpinner -> bannerx.bannerXTransformer = Transformers.list[p2]
            bannerXDirectionSpinner -> bannerx.bannerXDirection = when(p2) {
                1 -> BannerXDirection.RTL
                else -> BannerXDirection.LTR
            }
//            bannerXOrientationSpinner -> bannerx.bannerXOrientation = when(p2) {
//                1 -> BannerXOrientation.VERTICAL
//                else -> BannerXOrientation.HORIZONTAL
//            }
            indicatorVertAlignmentSpinner -> bannerx.indicatorVerticalAlignment = when(p2) {
                0 -> IndicatorVerticalAlignment.TOP
                else -> IndicatorVerticalAlignment.BOTTOM
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {/* no-op */}
}