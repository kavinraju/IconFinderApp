/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.network.models

import com.srilasaka.iconfinderapp.local_database.icon_set_details_table.IconSetDetailsEntry

data class IconSetDetails(
    val are_all_icons_glyph: Boolean?,
    val author: AuthorDetails?,
    val categories: List<Category>?,
    val icons_count: Int?,
    val iconset_id: Int,
    val identifier: String?,
    val is_premium: Boolean?,
    val license: License?,
    val name: String?,
    val published_at: String?,
    val readme: String?,
    val styles: List<Style>?,
    val type: String?,
    val website_url: String?
)

/** mapAsIconSetDetailsEntry() is a helper function to convert the [IconSetDetails] object
 * to list of [IconSetDetailsEntry]
z*/
fun IconSetDetails.mapAsIconSetDetailsEntry(): IconSetDetailsEntry {
    return IconSetDetailsEntry(
        this.are_all_icons_glyph,

        // Author details
        this.author?.company,
        this.author?.iconsets_count,
        this.author?.is_designer,
        this.author?.name,
        this.author?.user_id,
        this.author?.author_id,
        this.author?.username,
        this.author?.website_url,

        this.icons_count,
        this.iconset_id,
        this.identifier,
        this.is_premium,

        // License details
        (this.license?.license_id ?: "N/A").toString(),
        this.license?.name ?: "N/A",
        this.license?.scope ?: "N/A",
        this.license?.url ?: "N/A",

        this.name,
        this.published_at,
        this.readme,
        this.type,
        this.website_url
    )

}
