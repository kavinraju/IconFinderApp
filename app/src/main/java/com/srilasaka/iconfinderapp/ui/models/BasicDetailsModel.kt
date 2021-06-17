package com.srilasaka.iconfinderapp.ui.models

/**
 * [BasicDetailsModel] is the UI model for the [R.layout.layout_basic_deatils]
 */
data class BasicDetailsModel(
    val authorID: Int?,
    val userID: Int?,
    val creatorName: String,
    val isPremium: Boolean,
    val readme: String,
    val licenseName: String,
    val price: String,
    val title: String,
    val type: String,
    val websiteUrl: String
)
