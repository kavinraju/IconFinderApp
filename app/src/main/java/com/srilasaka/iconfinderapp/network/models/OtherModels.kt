package com.srilasaka.iconfinderapp.network.models

data class AuthorDetails(
    val company: String?,
    val iconsets_count: Int?,
    val is_designer: Boolean?,
    val name: String?,
    val user_id: Int?,
    val author_id: Int?,
    val username: String?,
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

data class Container(
    val download_url: String?,
    val format: String?
)

data class RasterSize(
    val formats: List<Format>?,
    val size: Int?,
    val size_height: Int?,
    val size_width: Int?
)

data class VectorSize(
    val formats: List<FormatVector>?,
    val size: Int?,
    val size_height: Int?,
    val size_width: Int?,
    val target_sizes: List<List<Int>>?
)

data class FormatVector(
    val download_url: String?,
    val format: String?
)

data class Format(
    val download_url: String?,
    val format: String?,
    val preview_url: String?
)