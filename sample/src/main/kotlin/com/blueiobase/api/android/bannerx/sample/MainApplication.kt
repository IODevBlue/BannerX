package com.blueiobase.api.android.bannerx.sample

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.MutableLiveData
import com.blueiobase.api.android.bannerx.model.Banner
import com.pexels.android.Pexels
import com.pexels.android.model.photo.PhotoResource
import com.pexels.android.model.video.Photographer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.BufferedInputStream
import java.net.URL

class MainApplication: Application() {
    var liveData = MutableLiveData<MutableList<Banner>>()
    var portraitPhotos = mutableListOf<Banner>()
    var photographers = mutableListOf<Photographer>()
    var rawSources = mutableListOf<PhotoResource>()
    var loadedCompletely = false

    private val pexelApiKey = BuildConfig.API_KEY
    private val scope = CoroutineScope(Dispatchers.Default)
    private val pexelClient by lazy { Pexels.createClient(pexelApiKey) }

    private var job: Job? = null

    init {
        liveData.value = mutableListOf()
    }

    @Throws(IllegalArgumentException::class, Exception::class, HttpException::class)
    fun launchQuery() {
        job = null
        job = scope.launch {
            liveData.postValue(invokeApi())
        }
        job!!.start()
    }

    fun stopQuery() {
        job?.cancel()
    }

    fun isQuerying() = job?.isActive?:false

    private suspend fun invokeApi(): MutableList<Banner> {
        portraitPhotos.clear()
        val asy = scope.async {
            val list = mutableListOf<Banner>()
            rawSources.apply {
                clear()
                addAll(pexelClient.curatedPhotos().photos)
            }.forEach {
                list.add(
                    Banner(it.alt).apply {
                        background = ColorDrawable(Color.parseColor(it.avgColor))
                        URL(it.src.landscape).openConnection().apply {
                            connect()
                            getInputStream().use { gis ->
                                BufferedInputStream(gis).use { bis ->
                                    image = BitmapFactory.decodeStream(bis)
                                }
                            }
                        }
                    })
                portraitPhotos.add(
                    Banner(it.alt).apply {
                        background = ColorDrawable(Color.parseColor(it.avgColor))
                        URL(it.src.portrait).openConnection().apply {
                            connect()
                            getInputStream().use { gis ->
                                BufferedInputStream(gis).use { bis ->
                                    image = BitmapFactory.decodeStream(bis)
                                }
                            }
                        }
                    })
                photographers.add(Photographer(it.photographerId, it.photographer, it.photographerUrl))
                if (rawSources.last() == it) loadedCompletely = true
            }
            list
        }
        return asy.await()
    }
}