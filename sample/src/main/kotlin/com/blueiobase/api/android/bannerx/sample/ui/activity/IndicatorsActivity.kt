package com.blueiobase.api.android.bannerx.sample.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.blueiobase.api.android.bannerx.BannerX
import com.blueiobase.api.android.bannerx.model.Banner
import com.blueiobase.api.android.bannerx.sample.MainApplication
import com.blueiobase.api.android.bannerx.sample.ui.array.Indicators
import com.blueiobase.api.android.bannerx.sample.ui.fragment.PhotoDialogFragment
import com.blueiobase.api.android.bannerx.sample.util.makeToast
import com.blueiobase.api.android.bannerx.util.BannerScaleAnimateParams
import com.blueiobase.api.android.networkvalidator.networkValidator

class IndicatorsActivity: AppCompatActivity() {

    val app by lazy { application as MainApplication }

    private val githubButton: ImageButton by lazy { findViewById(R.id.github_button) }
    private val repoLink = "https://github.com/IODevBlue/BannerX-Indicators"
    private var hasLoaded = false

    private val bannerx by lazy { findViewById<BannerX>(R.id.bannerx) }
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var currentInstruction = 0
    private val textSwitcher by lazy { findViewById<TextSwitcher>(R.id.instructions_text_switcher) }
    private val instructionsArr by lazy { resources.getStringArray(R.array.tap_instructions) }
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
    private val indicatorsSpinner by lazy { findViewById<AppCompatSpinner>(R.id.indicators_spinner) }

    private val indicators by lazy { Indicators() }

    private val observer =  Observer<MutableList<Banner>> {
        runOnUiThread {
            if(!hasLoaded && app.loadedCompletely) {
                bannerx.processList(app.portraitPhotos, forceUpdateIfEmpty = false)
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
                            bannerx.processList(portraitPhotos, forceUpdateIfEmpty = false)
                            hasLoaded = true
                        }
                    }
                } catch (exception: Exception) {
                    makeToast(exception.cause?.message?:"Error retrieving photographs from Pexel API.")
                }
            } else {
                if (loadedCompletely) {
                    if(!hasLoaded) {
                        bannerx.processList(portraitPhotos, forceUpdateIfEmpty = false)
                        hasLoaded = true
                    }
                } else {
                    makeToast("Network error. Please check network connection.")
                    stopQuery()
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_indicators)
        textSwitcher.setFactory { layoutInflater.inflate(R.layout.item_text_switcher, null) }
        findViewById<Toolbar>(R.id.indicators_toolbar).apply { setNavigationOnClickListener { finish() } }
        with(ArrayAdapter(this, android.R.layout.simple_spinner_item, indicators.asStringList())) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (indicatorsSpinner).run {
                adapter = this@with
                setSelection(indicators.getIndex(bannerx.bannerXIndicator))
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { bannerx.bannerXIndicator = indicators.list[p2] }
                    override fun onNothingSelected(p0: AdapterView<*>?) {/* no-op */ }
                }
            }
        }
        bannerx.apply {
            setOnBannerClickListener { _, _, i ->
                if (app.loadedCompletely) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(app.rawSources[i].url)
                    startActivity(intent)
                }
                makeToast("Clicked banner number: $i")
            }
            setOnBannerLongClickListener { b, _, i ->
                if (app.loadedCompletely) {
                    (PhotoDialogFragment(b, app.photographers[i])).show(
                        supportFragmentManager,
                        "Photo_Information"
                    )
                }
                makeToast("Long clicked banner number: $i")
            }
            setOnBannerDoubleClickListener { _, _, i ->
                if (app.loadedCompletely) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(app.photographers[i].url)
                    startActivity(intent)
                }
                makeToast("Double clicked banner number: $i")
            }
        }

        BannerX.doScaleAnimateOn(githubButton, BannerScaleAnimateParams(), onClickListener = {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(repoLink)
            startActivity(intent)
        })
        networkValidator { s(isOnline()) }.setOnNetworkStateChangedListener { b, _ -> runOnUiThread { s(b) } }
        app.liveData.observe(this, observer)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    override fun onStart() {
        super.onStart()
        handler.postDelayed(textSwitchRunnable, 5000L)
    }
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(textSwitchRunnable)
    }
}