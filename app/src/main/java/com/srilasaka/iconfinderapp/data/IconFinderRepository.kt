package com.srilasaka.iconfinderapp.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.local_database.icons_table.IconsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import kotlinx.coroutines.flow.Flow

/**
 * This Repository works with the local and the remote data sources.
 */
class IconFinderRepository(
    private val service: IconFinderAPIService,
    private val database: IconsFinderDatabase
) {
    private val TAG: String? = IconFinderRepository::class.simpleName

    fun getPublicIconSets(): Flow<PagingData<IconSetsEntry>> {
        Log.d(TAG, "New Icon Sets query")

        val pagingSourceFactory = { database.iconSetsDao.getIconSets() }

        return Pager(
            PagingConfig(pageSize = NUMBER_OF_ITEMS_TO_FETCH, enablePlaceholders = false)
        ) {
            IconSetsPagingSource(
                query = null,
                database,
                service
            )
        }.flow
    }

    /**
     * searchIcons() searches the Icons that matches the @param query.
     *
     * @param query - text typed the user for querying the icons
     */
    fun searchIcons(query: String, premium: PREMIUM): Flow<PagingData<IconsEntry>> {
        Log.d(TAG, "New Icons query")

        //val pagingSourceFactory = { database.iconSetsDao.getIconSets() }

        return Pager(
            PagingConfig(pageSize = NUMBER_OF_ITEMS_TO_FETCH, enablePlaceholders = false)
        ) {
            SearchIconsPagingSource(
                query = query,
                isPREMIUM = premium,
                database,
                service
            )
        }.flow
    }

    companion object {
        const val NUMBER_OF_ITEMS_TO_FETCH = 20
    }
}