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

fun List<Iconset>.mapAsIconSetsEntry(): List<IconSetsEntry> {
    return map {

        var firstPrice: String? = null
        if(it.prices != null) {
            val currencySymbol = Currency.getInstance(it.prices.get(0).currency).symbol
            firstPrice = "${it.prices.get(0).price} ${currencySymbol}"
        }

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
