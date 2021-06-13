package com.srilasaka.iconfinderapp.local_database.remote_keys_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRemoteKeys(remoteKey: List<RemoteKeysEntry>)

    @Query("SELECT * FROM remote_keys WHERE iconSetID = :iconSetID")
    suspend fun getRemoteKeysEntry(iconSetID: Int): RemoteKeysEntry?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()
}