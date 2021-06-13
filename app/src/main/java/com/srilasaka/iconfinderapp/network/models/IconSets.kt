package com.srilasaka.iconfinderapp.network.models

import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import java.util.*

data class IconSets(
    val iconsets: List<Iconset>?,
    val total_count: Int?
)

data class Iconset(
    val are_all_icons_glyph: Boolean?,
    val author: Author?,
    val categories: List<Category>?,
    val icons_count: Int?,
    val iconset_id: Int,
    val identifier: String?,
    val is_premium: Boolean?,
    val license: License?,
    val name: String?,
    val prices: List<Price>?,
    val published_at: String?,
    val readme: String?,
    val styles: List<Style>?,
    val type: String?,
    val website_url: String?
)

/** mapAsIconSetsEntry() is a helper function to convert the list of [IconSets] object
 * to list of [IconSetsEntry]
z*/
fun List<Iconset>.mapAsIconSetsEntry(): List<IconSetsEntry> {
    return map {

        val currencySymbol =
            if (it.prices != null) Currency.getInstance(it.prices[0].currency).symbol else null
        val firstPrice =
            if (currencySymbol != null) "${it.prices?.get(0)?.price} ${currencySymbol}" else ""

        IconSetsEntry(
            it.are_all_icons_glyph,
            it.author?.author_id,
            it.author?.user_id,
            it.author?.iconsets_count ?: 0,
            it.author?.name,
            it.author?.website_url ?: "",
            it.icons_count,
            it.iconset_id,
            it.identifier,
            it.is_premium,
            firstPrice,
            (it.license?.license_id ?: "N/A").toString(),
            it.license?.name ?: "N/A",
            it.license?.scope ?: "N/A",
            it.license?.url ?: "N/A",
            it.name,
            it.published_at,
            it.readme,
            it.type,
            it.website_url
        )
    }
}
