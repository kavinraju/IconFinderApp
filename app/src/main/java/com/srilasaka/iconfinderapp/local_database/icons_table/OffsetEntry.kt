package com.srilasaka.iconfinderapp.local_database.icons_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offset_table")
data class OffsetEntry(
    @PrimaryKey(autoGenerate = false) val iconID: Int,
    val query: String,
    val offset: Int
)
