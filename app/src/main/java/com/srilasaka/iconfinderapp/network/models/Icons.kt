package com.srilasaka.iconfinderapp.network.models

import android.util.Log
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.local_database.icons_table.IconsEntry
import java.util.*

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

/** mapAsIconSetsEntry() is a helper function to convert the list of [IconSets] object
 * to list of [IconSetsEntry]
z*/
fun List<Icon>.mapAsIconSetsEntry(): List<IconsEntry> {

    return map { it ->

        val currencySymbol =
            if (it.prices != null) Currency.getInstance(it.prices[0].currency).symbol else null
        val firstPrice =
            if (currencySymbol != null) "${it.prices?.get(0)?.price} ${currencySymbol}" else ""
        val firstLicense = if (it.prices != null) it.prices[0].license else null

        val format64 = if (it.raster_sizes != null && it.raster_sizes.isNotEmpty()) {

            it.raster_sizes[it.raster_sizes.size - 1].formats?.get(0)
        } else null

        Log.d("TAG", "format_64 $format64")

        val name = if (it.styles != null && it.styles.isNotEmpty()) it.styles[0].name else "N/A"

        IconsEntry(
            it.categories,
            it.containers,
            it.icon_id,
            it.is_icon_glyph,
            it.is_premium,
            it.is_purchased,
            it.prices,

            // Deconstructed the first Price & License info into individual columns
            firstPrice,
            (firstLicense?.license_id ?: "N/A").toString(),
            (firstLicense?.name ?: "N/A").toString(),
            (firstLicense?.scope ?: "N/A").toString(),
            (firstLicense?.url ?: "N/A").toString(),

            it.published_at,

            // Deconstructed the RasterSize format of size 64
            it.raster_sizes,
            (format64?.download_url ?: "N/A").toString(),
            (format64?.format ?: "N/A").toString(),
            (format64?.preview_url ?: "N/A").toString(),

            it.styles,
            name ?: "N/A",

            it.tags,
            it.type,
            it.vector_sizes
        )
    }
}