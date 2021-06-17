package com.srilasaka.iconfinderapp.ui.utils

import com.srilasaka.iconfinderapp.local_database.author_details_table.AuthorEntry
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.local_database.icons_table.IconsEntry
import com.srilasaka.iconfinderapp.ui.models.BasicDetailsModel

/**
 * UiModel is a sealed class which could host multiple data items into a single class type and
 * we can refer to this generic class.
 */
sealed class UiModel {
    data class IconSetDataItem(val iconSetsEntry: IconSetsEntry) : UiModel()
    data class IconsDataItem(val iconsEntry: IconsEntry) : UiModel()
    data class BasicDetailsDataItem(val basicDetailsModel: BasicDetailsModel) : UiModel()
    data class AuthorDataItem(val authorEntry: AuthorEntry) : UiModel()
}