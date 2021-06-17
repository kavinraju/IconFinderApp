package com.srilasaka.iconfinderapp.local_database.icons_table

import com.srilasaka.iconfinderapp.network.models.*

data class IconsEntry(
    val categories: List<Category>?,
    val containers: List<Container>?,
    val icon_id: Int,
    val is_icon_glyph: Boolean?,
    val is_premium: Boolean?,
    val is_purchased: Boolean?,

    val prices: List<Price>?,
    /** Deconstructed the first [Price] & [License] info into individual columns */
    val price: String?,
    val license_id: String?,
    val license_name: String?,
    val license_scope: String?,
    val license_url: String?,

    val published_at: String?,

    /** Deconstructed the [RasterSize] format of size 64 */
    val raster_sizes: List<RasterSize>?,
    val format_64_download_url: String?,
    val format_64: String?,
    val format_64_preview_url: String?,

    val styles: List<Style>?,
    val name: String, // [styles[0].name]

    val tags: List<String>?,
    val type: String?,
    val vector_sizes: List<VectorSize>?
)
