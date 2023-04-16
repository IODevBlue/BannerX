package com.blueiobase.api.android.bannerx.sample.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.blueiobase.api.android.bannerx.BannerX
import com.blueiobase.api.android.bannerx.enums.ScrollState
import com.blueiobase.api.android.bannerx.model.Banner
import com.blueiobase.api.android.bannerx.sample.MainApplication
import com.blueiobase.api.android.bannerx.sample.ui.fragment.PhotoDialogFragment
import com.blueiobase.api.android.bannerx.sample.util.makeToast
import com.blueiobase.api.android.bannerx.transformers.ScaleBannerTransformer
import com.blueiobase.api.android.bannerx.util.BannerScaleAnimateParams
import com.blueiobase.api.android.networkvalidator.networkValidator
import com.blueiobase.app.android.shopper.util.navigateTo

class MainActivity: AppCompatActivity() {

    private val bannerx by lazy { findViewById<BannerX>(R.id.bannerx) }
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val githubButton: ImageButton by lazy { findViewById(R.id.github_button) }
    private val visitPexelButton: Button by lazy { findViewById(R.id.visit_pexel_api) }
    private val indicatorsButton: Button by lazy { findViewById(R.id.indicators) }
    private val transformersButton: Button by lazy { findViewById(R.id.transformers) }
    private val playgroundButton: Button by lazy { findViewById(R.id.playground) }
    private val fullscreenButton: Button by lazy { findViewById(R.id.fullscreen) }

    private val app by lazy { application as MainApplication }
    private var hasLoaded = false

    private val repoLink = "https://github.com/IODevBlue/BannerX"
    private val pexelApiLink = "https://pexels.com/api"

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
    private var currentBanner = 0

    private val observer =  Observer<MutableList<Banner>> {
        runOnUiThread {
            if(!hasLoaded && app.loadedCompletely) {
                bannerx.processList(it, forceUpdateIfEmpty = false)
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
                            liveData.value!!.let { ldv ->
                                if(ldv.isNotEmpty()) {
                                    bannerx.processList(ldv, forceUpdateIfEmpty = false)
                                    hasLoaded = true
                                }
                            }
                        }
                    }
                } catch (exception: Exception) {
                    makeToast(exception.cause?.message?:"Error retrieving photographs from Pexels API.")
                }
            } else {
                if (loadedCompletely) {
                    if(!hasLoaded) {
                        bannerx.processList(liveData.value!!, forceUpdateIfEmpty = false)
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
        setContentView(R.layout.activity_main)
        textSwitcher.setFactory { layoutInflater.inflate(R.layout.item_text_switcher, null) }
        BannerX.apply {
            doScaleAnimateOn(githubButton, BannerScaleAnimateParams(), onClickListener = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(repoLink)
                startActivity(intent)
            })
            doScaleAnimateOn(visitPexelButton, BannerScaleAnimateParams(), onClickListener = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(pexelApiLink)
                startActivity(intent)
            })
            doScaleAnimateOn(indicatorsButton, BannerScaleAnimateParams(), onClickListener = {
                navigateTo(IndicatorsActivity::class.java){ Bundle().apply { putInt("current_pos", currentBanner) } }
            })
            doScaleAnimateOn(transformersButton, BannerScaleAnimateParams(), onClickListener = {
                navigateTo(TransformersActivity::class.java){ Bundle().apply { putInt("current_pos", currentBanner) } }
            })
            doScaleAnimateOn(fullscreenButton, BannerScaleAnimateParams(), onClickListener = {
                navigateTo(FullscreenActivity::class.java){ Bundle().apply { putInt("current_pos", currentBanner) } }
            })
            doScaleAnimateOn(playgroundButton, BannerScaleAnimateParams(), onClickListener = {
                navigateTo(PlaygroundActivity::class.java){ Bundle().apply { putInt("current_pos", currentBanner) } }
            })
        }
        bannerx.apply {
            setOnBannerClickListener { _, _, i ->
                if(app.loadedCompletely) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(app.rawSources[i].url)
                    startActivity(intent)
                }
                makeToast("Clicked banner number: $i")
            }
            setOnBannerLongClickListener { b, _, i ->
                if(app.loadedCompletely) {
                    (PhotoDialogFragment(b, app.photographers[i])).show(supportFragmentManager, "Photo_Information")
                }
                makeToast("Long clicked banner number: $i")
            }
            setOnBannerDoubleClickListener {_, _, i ->
                if(app.loadedCompletely) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(app.photographers[i].url)
                    startActivity(intent)
                }
                makeToast("Double clicked banner number: $i")
            }
            onBannerChangeListener = object: BannerX.OnBannerChangeListener {
                override fun onBannerScrollStateChanged(state: ScrollState) {/* no-op */}
                override fun onBannerScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {/* no-op */}
                override fun onBannerSelected(position: Int) { currentBanner = position }
            }
            bannerXTransformer = ScaleBannerTransformer()
        }
        networkValidator { s(isOnline()) }.setOnNetworkStateChangedListener { b, _ -> runOnUiThread { s(b) } }
        app.liveData.observe(this, observer)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bannerx.processList(app.liveData.value!!)
    }

    override fun onResume() {
        super.onResume()
        app.apply {
            if(!hasLoaded && loadedCompletely) {
                bannerx.processList(app.liveData.value!!, forceUpdateIfEmpty = false)
                hasLoaded = true
            }
        }
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