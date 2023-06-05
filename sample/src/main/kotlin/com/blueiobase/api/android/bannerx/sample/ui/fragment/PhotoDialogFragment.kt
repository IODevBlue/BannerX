package com.blueiobase.api.android.bannerx.sample.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.blueiobase.api.android.bannerx.BannerX
import com.blueiobase.api.android.bannerx.model.Banner
import com.blueiobase.api.android.bannerx.sample.R
import com.blueiobase.api.android.bannerx.util.BannerScaleAnimateParams
import com.pexels.android.model.video.Photographer

open class PhotoDialogFragment(private val banner: Banner, private val photographer: Photographer): DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.fragment_photo_info, null)
        view.apply {
            with(banner) {
                (findViewById<ImageView>(R.id.banner_image)).setImageBitmap(image as Bitmap)
                (findViewById<TextView>(R.id.photo_name)).text = title
            }
            with(photographer) {
                (findViewById<TextView>(R.id.photographer_name)).text = name
                BannerX.doScaleAnimateOn(
                    findViewById<Button>(R.id.visit_button),
                    BannerScaleAnimateParams(),
                    onClickListener = {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                )
            }
            BannerX.doScaleAnimateOn(
                findViewById<ImageButton>(R.id.close_button),
                BannerScaleAnimateParams(),
                onClickListener = { dismiss() }
            )

        }
        dialog.setContentView(view)
        return dialog
    }
}