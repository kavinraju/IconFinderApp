package com.srilasaka.iconfinderapp.network.models

data class Author(
    val author_id: Int?,
    val user_id: Int?,
    val iconsets_count: Int?,
    val name: String,
    val website_url: String?
)

data class Category(
    val identifier: String?,
    val name: String?
)

data class Price(
    val currency: String,
    val license: License,
    val price: Double
)

data class License(
    val license_id: Int,
    val name: String,
    val scope: String?,
    val url: String?
)

data class Style(
    val identifier: String?,
    val name: String?
)