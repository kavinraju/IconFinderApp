package com.srilasaka.iconfinderapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.local_database.remote_keys_table.RemoteKeysEntry
import com.srilasaka.iconfinderapp.network.models.mapAsIconSetsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import retrofit2.HttpException
import java.io.IOException


@ExperimentalPagingApi
class IconSetsRemoteMediator(
    private val query: String?,
    private val database: IconsFinderDatabase,
    private val networkService: IconFinderAPIService
) : RemoteMediator<Int, IconSetsEntry>() {

    private val TAG: String? = IconSetsRemoteMediator::class.simpleName
    private val iconSetsDao = database.iconSetsDao
    private val remoteKeysDao = database.remoteKeysDao

    override suspend fun initialize(): InitializeAction {
        // Load fresh data when ever the app is open new
       /* val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)
        return if (System.currentTimeMillis()- remoteKeysDao.getLastUpdatedTime() >= cacheTimeout) {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.

        }*/
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, IconSetsEntry>
    ): MediatorResult {

        Log.d(TAG, "load()")

        // The network load method takes an optional after=<iconset_id>
        // parameter. For every page after the first, pass the last user
        // ID to let it continue from where it left off. For REFRESH,
        // pass null to load the first page.
        val iconSetID = when (loadType) {
            LoadType.REFRESH -> {
                Log.d(TAG, "LoadType.REFRESH")
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextIconSetID
            }
            // In this example, you never need to prepend, since REFRESH
            // will always load the first page in the list. Immediately
            // return, reporting end of pagination.
            LoadType.PREPEND -> {
                Log.d(TAG, "LoadType.PREPEND")
                val remoteKeysEntry = getRemoteKeyClosestToFirstItem(state)
                val prevKey = remoteKeysEntry?.prevIconSetID
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeysEntry != null)
                }
                prevKey
                //return MediatorResult.Success(endOfPaginationReached = id != null)

                /*return MediatorResult.Success(
                    endOfPaginationReached = true
                )*/
            }
            LoadType.APPEND -> {
                Log.d(TAG, "LoadType.APPEND")
                val remoteKeysEntry = getRemoteKeyClosestToLastItem(state)
                val nextKey = remoteKeysEntry?.nextIconSetID
                Log.d(TAG, "nextKey = ${nextKey}")
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeysEntry != null)
                }

                nextKey
                //val firstItem = state.firstItemOrNull()

                // You must explicitly check if the last item is null when
                // appending, since passing null to networkService is only
                // valid for initial load. If lastItem is null it means no
                // items were loaded after the initial REFRESH and there are
                // no more items to load.
               /* if (firstItem == null) {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }*/

                // Get the last item from the icon-sets list and return its ID
                //firstItem.iconset_id
            }
        }

        try {
            // Suspending network load via Retrofit.
            val response = networkService.getAllPublicIconSets(after = iconSetID)
            val iconSets = response.iconsets
            val endOfPaginationReached = iconSets == null || iconSets.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Delete the data in the database
                    iconSetsDao.deleteAllIconSets()
                    remoteKeysDao.deleteRemoteKeys()
                }

                Log.d(TAG, "iconSets = ${iconSets}")
                Log.d(TAG, "endOfPaginationReached = $endOfPaginationReached")

                val prevKey = if (iconSetID == null) null else iconSets?.get(0)?.iconset_id
                val nextKey = if (iconSets == null || iconSets.isEmpty()) null else iconSets[iconSets.size - 1].iconset_id
                val remoteKeys = iconSets!!.map {
                    RemoteKeysEntry(iconSetID = it.iconset_id, prevIconSetID = prevKey, nextIconSetID = nextKey)
                }

                // Insert new IconSets data into database, which invalidates the current PagingData,
                // allowing Paging to present the updates in the DB.
                iconSetsDao.insertAllIconSets(iconSets.mapAsIconSetsEntry())

                remoteKeysDao.insertRemoteKeys(remoteKeys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            Log.e(TAG, "exception = ${exception}")
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            Log.e(TAG, "exception = ${exception}")
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, IconSetsEntry>): RemoteKeysEntry? {
        return state.anchorPosition?.let { position ->
            Log.d(TAG, "getRemoteKeyClosestToCurrentPosition():  = ${state.closestItemToPosition(position)?.iconset_id}")
            state.closestItemToPosition(position)?.iconset_id?.let { iconsetId ->
                    database.remoteKeysDao.getRemoteKeysEntry(iconsetId)
            }
        }
    }

    private suspend fun getRemoteKeyClosestToFirstItem(state: PagingState<Int, IconSetsEntry>): RemoteKeysEntry? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { iconSetsEntry ->
                Log.d(TAG, "getRemoteKeyClosestToFirstItem():  = ${iconSetsEntry.iconset_id}")
                //iconSetsEntry.iconset_id
                database.remoteKeysDao.getRemoteKeysEntry(iconSetsEntry.iconset_id!!)
            }
    }

    private suspend fun getRemoteKeyClosestToLastItem(state: PagingState<Int, IconSetsEntry>): RemoteKeysEntry? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { iconSetsEntry ->
                Log.d(TAG, "getRemoteKeyClosestToLastItem():  = ${iconSetsEntry.iconset_id}")
                //iconSetsEntry.iconset_id
                database.remoteKeysDao.getRemoteKeysEntry(iconSetsEntry.iconset_id!!)
            }
    }
}