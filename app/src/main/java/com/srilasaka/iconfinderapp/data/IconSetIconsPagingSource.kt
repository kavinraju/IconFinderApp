package com.srilasaka.iconfinderapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icons_table.IconsEntry
import com.srilasaka.iconfinderapp.local_database.icons_table.OffsetEntry
import com.srilasaka.iconfinderapp.network.models.mapAsIconSetsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import retrofit2.HttpException
import java.io.IOException
import java.util.*

@OptIn(ExperimentalPagingApi::class)
class IconSetIconsPagingSource(
    private val iconsetID: Int,
    private val isPREMIUM: PREMIUM,
    private val database: IconsFinderDatabase,
    private val networkService: IconFinderAPIService
) : PagingSource<Int, IconsEntry>() {

    private val TAG: String? = IconSetIconsPagingSource::class.simpleName

    //private val iconSetsDao = database.iconSetsDao
    private val offsetDao = database.offsetDao

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IconsEntry> {
        Log.d(TAG, "load()")

        return try {

            /**
             * [OffsetEntry.getOffsetEntry] fetches the [OffsetEntry] having the highest
             * [OffsetEntry.offset] so that we can call the query incrementally from 0 to 100.
             */
            // Get the offset position from [OffsetEntry] table
            val offsetEntry = offsetDao.getOffsetEntry()
            var offset = 0
            if (offsetEntry != null && offsetEntry.offset < 100) {
                // Increment the [OffsetEntry.offset] value if it's of same query
                offset = offsetEntry.offset + 1
            } else if (offsetEntry != null && (offsetEntry.offset == 100)) {
                // Reset the [OffsetEntry] & offset, if query is different or offset value has reached it max,100.
                offsetDao.deleteAllIOffSetEntries()
                offset = 0
            }
            // Get the response by making an API call
            val response = networkService.getIconSetIcons(
                iconsetID = iconsetID,
                offset = offset,
                premium = isPREMIUM.name.lowercase(Locale.getDefault())
            )

            // Get the list of [Icon]
            val icons = response.icons!!.mapAsIconSetsEntry()

            // Create a list of OffsetEntry and insert onto local DB
            val offsets = icons.map {
                OffsetEntry(it.icon_id, "", offset)
            }
            offsetDao.insertAllOffset(offsets)

            /** return the LoadResult.Page() object by passing in the
             * @param data - Data to be displayed using the PagingDataAdapter
             * @param prevKey - Passing null always to the prevKey avoids making recursive call
             * @param nextKey - Passed the [Icon.icon_id] of the last item of the page so that
             *                  next set of data is fetched based on this ID.
             */
            LoadResult.Page(
                data = icons,
                prevKey = null, //iconSets.firstOrNull()?.iconset_id,
                nextKey = icons.lastOrNull()?.icon_id
            )
        } catch (exception: IOException) {
            Log.e(TAG, "exception = ${exception}")
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.e(TAG, "exception = ${exception}")
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, IconsEntry>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.icon_id
        }
    }

    override val keyReuseSupported: Boolean
        get() = true
}