/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.local_database.icon_set_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_sets_table")
data class IconSetsEntry(
    val are_all_icons_glyph: Boolean?,

    /** Deconstructed the [Author] info into individual columns */
    val author_id: Int?,
    val user_id: Int?,
    val author_iconsets_count: Int?,
    val author_name: String?,
    val author_website_url: String?,

    val icons_count: Int?,
    @PrimaryKey val iconset_id: Int,
    val identifier: String?,
    val is_premium: Boolean?,

    /** Deconstructed the first [Price] & [License] info into individual columns*/
    val price: String,
    val license_id: String?,
    val license_name: String?,
    val license_scope: String?,
    val license_url: String?,

    val name: String?,
    val published_at: String?,
    val readme: String?,
    val type: String?,
    val website_url: String?
)