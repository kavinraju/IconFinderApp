/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.local_database.icon_set_table

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.srilasaka.iconfinderapp.network.models.IconSets

@Dao
interface IconSetsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIconSets(iconSets: List<IconSetsEntry>)

    @Query("SELECT * FROM icon_sets_table")
    fun getIconSets(): PagingSource<Int, IconSetsEntry>

    @Query("DELETE FROM icon_sets_table")
    suspend fun deleteAllIconSets()
}