package com.srilasaka.iconfinderapp.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icon_set_details_table.IconSetDetailsEntry
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.local_database.icons_table.IconsEntry
import com.srilasaka.iconfinderapp.network.models.mapAsIconSetDetailsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import com.srilasaka.iconfinderapp.network.utils.State
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

/**
 * This Repository works with the local and the remote data sources.
 */
class IconFinderRepository(
    private val service: IconFinderAPIService,
    private val database: IconsFinderDatabase
) {
    private val TAG: String? = IconFinderRepository::class.simpleName

    fun getPublicIconSets(premium: PREMIUM): Flow<PagingData<IconSetsEntry>> {
        Log.d(TAG, "New Icon Sets query")

        val pagingSourceFactory = { database.iconSetsDao.getIconSets() }

        return Pager(
            PagingConfig(pageSize = NUMBER_OF_ITEMS_TO_FETCH, enablePlaceholders = false)
        ) {
            IconSetsPagingSource(
                query = null,
                premium = premium,
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

    /**
     * getIconSetDetails() fetches the details of the IconSet with @param iconsetID
     */
    fun getIconSetDetails(iconsetID: Int) = flow<State<IconSetDetailsEntry>> {
        // Emit Loading state for indicating the loading process
        emit(State.Loading())

        try {
            val response = service.getIconSetDetails(iconsetID).await()
            val iconSetDetailsEntry = response.mapAsIconSetDetailsEntry()

            emit(State.Success(iconSetDetailsEntry))
        } catch (exception: IOException) {
            Log.e(TAG, "exception = ${exception}")
            emit(State.failed(exception.toString()))
        } catch (exception: HttpException) {
            Log.e(TAG, "exception = ${exception}")
            emit(State.failed(exception.toString()))
        }


    }.flowOn(Dispatchers.IO)

    companion object {
        const val NUMBER_OF_ITEMS_TO_FETCH = 20
    }
}