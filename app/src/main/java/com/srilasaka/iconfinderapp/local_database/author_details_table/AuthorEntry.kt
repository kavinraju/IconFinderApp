/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.local_database.author_details_table

data class AuthorEntry(
    val author_id: Int,
    val iconsets_count: Int?,
    val name: String,
    val website_url: String?,
    val licenseType: String?,

    // detail will not be null if the data is not available
    val detail: String?
)