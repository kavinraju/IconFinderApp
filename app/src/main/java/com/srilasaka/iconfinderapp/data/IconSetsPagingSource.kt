/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.network.models.mapAsIconSetsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import retrofit2.HttpException
import java.io.IOException
import java.util.*

@OptIn(ExperimentalPagingApi::class)
class IconSetsPagingSource(
    private val query: String?,
    private val premium: PREMIUM,
    private val database: IconsFinderDatabase,
    private val networkService: IconFinderAPIService
) : PagingSource<Int, IconSetsEntry>() {

    private val TAG: String? = IconSetsPagingSource::class.simpleName
    private val iconSetsDao = database.iconSetsDao

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IconSetsEntry> {
        Log.d(TAG, "load()")

        return try {
            // Get the response by making an API call
            val response = networkService.getAllPublicIconSets(
                after = if (params is LoadParams.Append || params is LoadParams.Prepend) params.key else null,
                premium = premium.name.lowercase(Locale.getDefault())
            )

            /** Convert the [IconSets] object to [IconSetsEntry] */
            val iconSets = response.iconsets!!.mapAsIconSetsEntry()

            /** return the LoadResult.Page() object by passing in the
             * @param data - Data to be displayed using the PagingDataAdapter
             * @param prevKey - Passing null always to the prevKey avoids making recursive call
             * @param nextKey - Passed the [IconSets.iconset_id] of the last item of the page so that
             *                  next set of data is fetched based on this ID.
             */
            LoadResult.Page(
                data = iconSets,
                prevKey = null, //iconSets.firstOrNull()?.iconset_id,
                nextKey = iconSets.lastOrNull()?.iconset_id
            )
        } catch (exception: IOException) {
            Log.e(TAG, "exception = ${exception}")
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.e(TAG, "exception = ${exception}")
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, IconSetsEntry>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.iconset_id
        }
    }
}