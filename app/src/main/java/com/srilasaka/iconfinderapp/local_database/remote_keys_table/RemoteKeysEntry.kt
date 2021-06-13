package com.srilasaka.iconfinderapp.local_database.remote_keys_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntry(
    /*@PrimaryKey() val iconSetID: Int,
    val lastUpdated: Long*/
    @PrimaryKey val iconSetID: Int,
    val prevIconSetID: Int?,
    val nextIconSetID: Int?
)