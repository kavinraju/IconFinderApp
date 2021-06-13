package com.srilasaka.iconfinderapp.local_database.icon_set_table

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IconSetsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIconSets(iconSets: List<IconSetsEntry>)

    @Query("SELECT * FROM icon_sets_table")
    fun getIconSets(): PagingSource<Int, IconSetsEntry>

    @Query("DELETE FROM icon_sets_table")
    suspend fun deleteAllIconSets()
}