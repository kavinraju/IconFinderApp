/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.ui.utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.network.utils.EndPoints

@BindingAdapter("loadSubscriptionTypeIcon")
fun ImageView.loadSubscriptionTypeIcon(isPremium: Boolean) {
    setImageResource(
        if (isPremium) {
            R.drawable.icon_premium
        } else {
            R.drawable.icon_free
        }
    )
}

@BindingAdapter("loadImageUsingGlide")
fun ImageView.loadImageUsingGlide(imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {

        val glideUrl = GlideUrl(imageUrl) {
            mapOf(Pair("Authorization", EndPoints.AuthorizationHeader))
        }
        Glide
            .with(this.context)
            .asBitmap()
            .load(glideUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    setImageDrawable(BitmapDrawable(resources, resource))
                }

            })
    } else {
        setImageResource(R.drawable.icon_download)
    }
}

@BindingAdapter("loadHtmlText")
fun TextView.loadHtmlText(htmlString: String?) {
    if (!htmlString.isNullOrEmpty()) {
        val spanned = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
        text = spanned
    }
}

@BindingAdapter("makeAsHyperLink")
fun TextView.makeAsHyperLink(link: String?) {
    if (!link.isNullOrEmpty()) {
        isClickable = true
        linksClickable = true
    }
}
