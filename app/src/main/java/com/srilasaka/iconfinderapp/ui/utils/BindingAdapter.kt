package com.srilasaka.iconfinderapp.ui.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.srilasaka.iconfinderapp.R

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