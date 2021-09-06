/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.local_database.icon_details_table

import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.network.models.*

data class IconDetailsEntry(
    // Covert the List<Category>? to List<String>
    val categories: List<Category>?,
    val containers: List<Container>?,
    val icon_id: Int,

    /** Deconstruct the [Iconset] info into individual columns or get the [Iconset.iconset_id]
     * to reference to [IconSetsEntry] while creating the table
     * */
    val iconset: IconSetsEntry?,

    val is_icon_glyph: Boolean?,
    val is_premium: Boolean?,
    val published_at: String?,

    //val raster_sizes: List<RasterSize>?,
    /** Deconstructed the [RasterSize] format of size 64 */
    val raster_sizes: List<RasterSize>?,
    val format_64_download_url: String?,
    val format_64: String?,
    val format_64_preview_url: String?,

    val styles: List<Style>?,
    val styleName: String, // [styles[0].name]

    val tags: List<String>?,
    val type: String?,
    val vector_sizes: List<VectorSize>?
)