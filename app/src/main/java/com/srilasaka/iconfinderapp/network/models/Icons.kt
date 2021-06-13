package com.srilasaka.iconfinderapp.network.models

data class Icons(
    val icons: List<Icon>?,
    val total_count: Int?
)

data class Icon(
    val categories: List<Category>?,
    val containers: List<Container>?,
    val icon_id: Int,
    val is_icon_glyph: Boolean?,
    val is_premium: Boolean?,
    val is_purchased: Boolean?,
    val prices: List<Price>?,
    val published_at: String?,
    val raster_sizes: List<RasterSize>?,
    val styles: List<Style>?,
    val tags: List<String>?,
    val type: String?,
    val vector_sizes: List<VectorSize>?
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

data class Format(
    val download_url: String?,
    val format: String?,
    val preview_url: String?
)

data class FormatVector(
    val download_url: String?,
    val format: String?
)