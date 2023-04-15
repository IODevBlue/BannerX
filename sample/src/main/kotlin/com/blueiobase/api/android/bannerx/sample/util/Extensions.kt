package com.blueiobase.api.android.bannerx.sample.util

import android.app.Activity
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Extension function on an [Activity] to display a [Toast] message.
 * @param message The [String] message to display as the [Toast] message.
 */
fun Activity.makeToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Extension function on an [Activity] to make it fullscreen.
 */
fun Activity.createWindowInset(): WindowInsetsControllerCompat {
    val wic: WindowInsetsControllerCompat
    window.let {
        WindowCompat.setDecorFitsSystemWindows(it, false)
        wic = WindowInsetsControllerCompat(it, it.decorView)
        wic.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        wic.hide(WindowInsetsCompat.Type.systemBars())
    }
    return wic
}