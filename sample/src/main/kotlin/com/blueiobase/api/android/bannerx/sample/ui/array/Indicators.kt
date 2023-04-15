package com.blueiobase.api.android.bannerx.sample.ui.array

import com.blueiobase.api.android.bannerx.base.BannerXIndicator
import com.blueiobase.api.android.bannerx.base.DefaultBannerIndicator

class Indicators {

    private var current = 0

    val list = listOf (
        DefaultBannerIndicator()
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

    fun startFrom(indicator: BannerXIndicator) {
        list.forEach {
            if (it::class == indicator::class) {
                current = list.indexOf(it)
            }
        }
    }
    fun next(): BannerXIndicator {
        ++current
        if (current >= list.size) current = 0
        return list[current]
    }

    fun previous(): BannerXIndicator {
        --current
        if (current < 0) current = list.size-1
        return list[current]
    }

    fun getIndex(indicator: BannerXIndicator): Int {
        list.forEach {
            if (it::class == indicator::class) {
                return list.indexOf(it)
            }
        }
        return 0
    }
}