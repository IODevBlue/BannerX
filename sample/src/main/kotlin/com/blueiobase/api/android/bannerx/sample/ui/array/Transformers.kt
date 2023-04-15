package com.blueiobase.api.android.bannerx.sample.ui.array

import com.blueiobase.api.android.bannerx.base.BannerXTransformer
import com.blueiobase.api.android.bannerx.base.DefaultBannerTransformer
import com.blueiobase.api.android.bannerx.transformers.*

object Transformers {

    private var current = 0

    val list = listOf(
        DefaultBannerTransformer(),
        AccordionBannerTransformer(), AlphaBannerTransformer(),
        CartwheelBannerTransformer(), CenterScaleBannerTransformer(),
        DescentBannerTransformer(), FlipBannerTransformer(),
        OverlayBannerTransformer(), PunchyBannerTransformer(),
        ScaleBannerTransformer(), SidedStackBannerTransformer()
    )
    private val stringList = mutableListOf<String>()

    fun asStringList(): List<String> {
        return if (stringList.isNotEmpty() && stringList.size == list.size) stringList
        else buildList {
            list.forEach {
                add(it::class.simpleName!!)
            }
        }.also { stringList.clear(); stringList.addAll(it) }
    }

    fun startFrom(transformer: BannerXTransformer) {
        list.forEach {
            if (it::class == transformer::class) {
                current = list.indexOf(it)
            }
        }
    }
    fun next(): BannerXTransformer {
        ++current
        if (current >= list.size) current = 0
        return list[current]
    }

    fun previous(): BannerXTransformer {
        --current
        if (current < 0) current = list.size-1
        return list[current]
    }

    fun getIndex(transformer: BannerXTransformer): Int {
        list.forEach {
            if (it::class == transformer::class) {
                return list.indexOf(it)
            }
        }
        return 0
    }
}