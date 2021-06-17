package com.srilasaka.iconfinderapp.network.models

import com.srilasaka.iconfinderapp.local_database.author_details_table.AuthorEntry

data class Author(
    val author_id: Int?,
    val user_id: Int?,
    val iconsets_count: Int?,
    val name: String,
    val website_url: String?,

    // detail will not be null if the data is not available
    val detail: String?
)

/** mapAsIconSetsEntry() is a helper function to convert the [Author] object [AuthorEntry] */
fun Author.mapAsAuthorEntry(): AuthorEntry {
    return AuthorEntry(
        this.author_id!!,
        this.iconsets_count,
        this.name,
        this.website_url,
        null,
        this.detail
    )
}
