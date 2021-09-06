/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.network.models

import com.srilasaka.iconfinderapp.local_database.icon_details_table.IconDetailsEntry
import com.srilasaka.iconfinderapp.local_database.icon_set_details_table.IconSetDetailsEntry

data class IconDetails(
    val categories: List<Category>?,
    val containers: List<Container>?,
    val icon_id: Int,
    val iconset: Iconset?,
    val is_icon_glyph: Boolean?,
    val is_premium: Boolean?,
    val published_at: String?,
    val raster_sizes: List<RasterSize>?,
    val styles: List<Style>?,
    val tags: List<String>?,
    val type: String?,
    val vector_sizes: List<VectorSize>?
)

/** mapAsIconSetDetailsEntry() is a helper function to convert the [IconSetDetails] object
 * to list of [IconSetDetailsEntry]
z*/
fun IconDetails.mapAsIconSetDetailsEntry(): IconDetailsEntry {
    val format64 = if (this.raster_sizes != null && this.raster_sizes.isNotEmpty()) {

        this.raster_sizes[this.raster_sizes.size - 1].formats?.get(0)
    } else null

    val styleName =
        if (this.styles != null && this.styles.isNotEmpty()) this.styles[0].name else "N/A"

    val iconSetsEntry = this.iconset?.mapAsIconSetsEntry()

    return IconDetailsEntry(
        this.categories,
        this.containers,
        this.icon_id,
        iconSetsEntry,
        this.is_icon_glyph,
        this.is_premium,
        this.published_at,

        // Deconstructed the RasterSize format of size 64
        this.raster_sizes,
        (format64?.download_url ?: "N/A").toString(),
        (format64?.format ?: "N/A").toString(),
        (format64?.preview_url ?: "N/A").toString(),

        this.styles,
        styleName!!, // [styles[0].name]

        this.tags,
        this.type,
        this.vector_sizes
    )
}
