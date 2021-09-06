/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.local_database.icons_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offset_table")
data class OffsetEntry(
    @PrimaryKey(autoGenerate = false) val iconID: Int,
    val query: String,
    val offset: Int
)
