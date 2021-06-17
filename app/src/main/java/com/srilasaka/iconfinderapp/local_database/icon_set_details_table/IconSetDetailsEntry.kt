package com.srilasaka.iconfinderapp.local_database.icon_set_details_table

import androidx.room.Entity
import com.srilasaka.iconfinderapp.network.models.AuthorDetails
import com.srilasaka.iconfinderapp.network.models.License

@Entity(tableName = "icon_sets_details_table")
data class IconSetDetailsEntry(
    val are_all_icons_glyph: Boolean?,

    /** Deconstructed the [AuthorDetails] info into individual columns */
    val author_company: String?,
    val author_iconsets_count: Int?,
    val author_is_designer: Boolean?,
    val author_name: String?,
    val author_user_id: Int?,
    val author_author_id: Int?,
    val author_username: String?,
    val author_website_url: String?,

    //val categories: List<Category>?,
    val icons_count: Int?,
    val iconset_id: Int,
    val identifier: String?,
    val is_premium: Boolean?,

    /** Deconstructed the first [Price] & [License] info into individual columns*/
    //val price: String?,
    val license_id: String?,
    val license_name: String?,
    val license_scope: String?,
    val license_url: String?,

    val name: String?,
    val published_at: String?,
    val readme: String?,
    //val styles: List<Style>?,
    val type: String?,
    val website_url: String?
)