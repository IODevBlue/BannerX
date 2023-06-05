package com.blueiobase.api.android.bannerx.sample.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.blueiobase.api.android.bannerx.BannerX
import com.blueiobase.api.android.bannerx.model.Banner
import com.blueiobase.api.android.bannerx.sample.MainApplication
import com.blueiobase.api.android.bannerx.sample.R
import com.blueiobase.api.android.bannerx.sample.ui.array.Transformers
import com.blueiobase.api.android.bannerx.sample.ui.fragment.FullscreenPhotoDialogFragment
import com.blueiobase.api.android.bannerx.sample.util.createWindowInset
import com.blueiobase.api.android.bannerx.sample.util.makeToast
import com.blueiobase.api.android.bannerx.util.BannerScaleAnimateParams
import com.blueiobase.api.android.networkvalidator.networkValidator

class FullscreenActivity : AppCompatActivity() {

    private val bannerx by lazy { findViewById<BannerX>(R.id.fullscreen_bannerx) }
    private val currentTransformerTV by lazy { findViewById<TextView>(R.id.current_transformer) }
    private val nextButton: ImageButton by lazy { findViewById(R.id.next) }
    private val previousButton: ImageButton by lazy { findViewById(R.id.previous) }
    private val interpol = OvershootInterpolator()
    private val app by lazy { application as MainApplication }
    private var hasLoaded = false

    val list = mutableListOf<Banner>()

    private val observer =  Observer<MutableList<Banner>> {
        if(!hasLoaded && app.loadedCompletely) {
            list.apply {
                clear()
                addAll(
                    when (resources.configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> it
                        else -> app.portraitPhotos
                    }
                )
            }
            runOnUiThread {
                if(list.isNotEmpty()) {
                    bannerx.useCustomAdapter(object: BannerX.CustomAdapter {
                        override fun getBannerType(position: Int) = 0
                        override fun onBindBannerViewHolder(bannerViewHolder: BannerX.BannerViewHolder, position: Int) {
                            (bannerViewHolder as CustomBannerViewHolder).apply {
                                image.setImageBitmap(list[position].image as Bitmap)
                            }
                        }
                        override fun onCreateBannerViewHolder(parent: ViewGroup, viewType: Int): BannerX.BannerViewHolder {
                            return CustomBannerViewHolder(layoutInflater.inflate(R.layout.custom_banner_layout, parent, false))
                        }
                    }).processList(list, forceUpdateIfEmpty = false, intent.extras?.getInt("current_pos")?:0)
                }
            }
            hasLoaded = true
        }
    }
    private val s = fun(b: Boolean) {
        app.apply {
            if(b) {
                try {
                    if(!loadedCompletely && !isQuerying()) { launchQuery() }
                    else {
                        if(!hasLoaded) {
                            list.apply {
                                clear()
                                addAll(
                                    when (resources.configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> app.liveData.value!!
                                        else -> app.portraitPhotos
                                    }
                                )
                            }
                            if(list.isNotEmpty()) {
                                bannerx.useCustomAdapter(object: BannerX.CustomAdapter {
                                    override fun getBannerType(position: Int) = 0
                                    override fun onBindBannerViewHolder(bannerViewHolder: BannerX.BannerViewHolder, position: Int) {
                                        (bannerViewHolder as CustomBannerViewHolder).apply {
                                            image.setImageBitmap(list[position].image as Bitmap)
                                        }
                                    }
                                    override fun onCreateBannerViewHolder(parent: ViewGroup, viewType: Int): BannerX.BannerViewHolder {
                                        return CustomBannerViewHolder(layoutInflater.inflate(R.layout.custom_banner_layout, parent, false))
                                    }
                                }).processList(list, forceUpdateIfEmpty = false, intent.extras?.getInt("current_pos")?:0)
                            }
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createWindowInset()
        setContentView(R.layout.activity_fullscreen)
        findViewById<ImageButton>(R.id.back_button).setOnClickListener { finish() }
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
                makeToast("Long clicked banner number: $i")
                if(app.loadedCompletely) {
                    (FullscreenPhotoDialogFragment(this@FullscreenActivity, b, app.photographers[i])).show(supportFragmentManager, "Photo_Information")
                }
            }
            setOnBannerDoubleClickListener { _, v, i ->
                makeToast("Double clicked banner number: $i")
                if(app.loadedCompletely) { showLike(v) }
            }
            with(bannerXTransformer) {
                Transformers.startFrom(this)
                currentTransformerTV.text = this::class.simpleName
            }
            BannerX.doScaleAnimateOn(nextButton, BannerScaleAnimateParams(), onClickListener = {
                with(Transformers.next()) {
                    bannerXTransformer = this
                    currentTransformerTV.text = this::class.simpleName
                }
            })
            BannerX.doScaleAnimateOn(previousButton, BannerScaleAnimateParams(), onClickListener = {
                with(Transformers.previous()) {
                    bannerXTransformer = this
                    currentTransformerTV.text = this::class.simpleName
                }
            })
        }
        networkValidator { s(isOnline()) }.setOnNetworkStateChangedListener { b, _ -> runOnUiThread { s(b) } }
        app.liveData.observe(this, observer)
    }

    override fun onResume() {
        super.onResume()
        createWindowInset()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        list.apply {
            clear()
            addAll(
                when (resources.configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> app.liveData.value!!
                    else -> app.portraitPhotos }
            )
        }
        bannerx.processList(list, false)
        with(bannerx.bannerXTransformer) {
            Transformers.startFrom(this)
            currentTransformerTV.text = this::class.simpleName
        }
    }
    private fun showLike(view: View) {
        with(view.findViewById<ImageView>(R.id.like)) {
            animate().apply {
                cancel()
                scaleX(1F)
                scaleY(1F)
                alpha(1F)
                duration = 300L
                interpolator = interpol
            }
            postDelayed({
                animate().apply {
                    alpha(0F)
                    scaleX(0.25F)
                    scaleY(0.25F)
                    duration = 300L
                    interpolator = interpol
                }
            }, 1000L)
        }
    }
    private class CustomBannerViewHolder(view: View): BannerX.BannerViewHolder(view) {
        val image: ImageView by lazy { view.findViewById<ImageView>(R.id.banner_image_full) }
    }
}