/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.local_database.remote_keys_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKeys(remoteKey: List<RemoteKeysEntry>)

    @Query("SELECT MAX(lastUpdated) FROM remote_keys")
    suspend fun getLastUpdatedTime(): Long

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()
}