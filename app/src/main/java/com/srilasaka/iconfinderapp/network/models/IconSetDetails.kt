package com.srilasaka.iconfinderapp.network.models

data class IconSetDetails(
    val are_all_icons_glyph: Boolean?,
    val author: AuthorDetails?,
    val categories: List<Category>?,
    val icons_count: Int?,
    val iconset_id: Int?,
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

data class AuthorDetails(
    val company: String?,
    val iconsets_count: Int?,
    val is_designer: Boolean?,
    val name: String?,
    val user_id: Int?,
    val author_id: Int?,
    val username: String?,
    val website_url: String?
)