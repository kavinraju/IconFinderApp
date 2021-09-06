/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.local_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsDao
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.local_database.icons_table.OffsetDao
import com.srilasaka.iconfinderapp.local_database.icons_table.OffsetEntry
import com.srilasaka.iconfinderapp.local_database.remote_keys_table.RemoteKeysDao
import com.srilasaka.iconfinderapp.local_database.remote_keys_table.RemoteKeysEntry

@Database(
    entities = [IconSetsEntry::class, RemoteKeysEntry::class, OffsetEntry::class],
    version = 1,
    exportSchema = false
)
abstract class IconsFinderDatabase : RoomDatabase() {

    abstract val iconSetsDao: IconSetsDao
    abstract val remoteKeysDao: RemoteKeysDao
    abstract val offsetDao: OffsetDao

    companion object {
        @Volatile
        private var INSTANCE: IconsFinderDatabase? = null

        fun getInstance(context: Context): IconsFinderDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        IconsFinderDatabase::class.java,
                        "icons_finder_databse"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
