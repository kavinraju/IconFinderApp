/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.local_database.icons_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OffsetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOffset(iconSets: List<OffsetEntry>)

    /** This query fetches the [OffsetEntry] having the highest [OffsetEntry.offset] so that we can call the query incrementally up to 100 */
    @Query("SELECT * FROM offset_table WHERE `offset` = (SELECT MAX(`offset`) FROM offset_table)")
    suspend fun getOffsetEntry(): OffsetEntry?

    @Query("DELETE FROM offset_table")
    suspend fun deleteAllIOffSetEntries()
}