package com.srilasaka.iconfinderapp.ui.home_screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.srilasaka.iconfinderapp.data.IconFinderRepository
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.local_database.icons_table.IconsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String? = HomeFragmentViewModel::class.simpleName

    // Dependency Injection Happens here
    private val repository: IconFinderRepository = IconFinderRepository(
        IconFinderAPIService.create(),
        IconsFinderDatabase.getInstance(application)
    )

    private var iconSetsQueryResult: Flow<PagingData<UiModel.IconSetDataItem>>? = null
    private var searchIconsQueryResult: Flow<PagingData<UiModel.IconsDataItem>>? = null

    private var currentSearchIconsQuery: String? = null

    /**
     * iconSetsQuery() function queries the data from the [IconFinderRepository.getPublicIconSets].
     */
    fun iconSetsQuery(premium: PREMIUM): Flow<PagingData<UiModel.IconSetDataItem>> {
        Log.d(TAG, "iconSetsQuery")
        val newResult: Flow<PagingData<UiModel.IconSetDataItem>> =
            repository.getPublicIconSets(premium)
                .map { pagingData -> pagingData.map { UiModel.IconSetDataItem(it) } }
                .cachedIn(viewModelScope)

        iconSetsQueryResult = newResult
        return newResult

    }

    /**
     * searchIcons() function queries the data from the [IconFinderRepository.getPublicIconSets].
     */
    fun searchIcons(query: String, premium: PREMIUM): Flow<PagingData<UiModel.IconsDataItem>> {
        Log.d(TAG, "searchIcons")
        val lastResult = searchIconsQueryResult
        if (query == currentSearchIconsQuery && lastResult != null) {
            return lastResult
        }

        val newResult: Flow<PagingData<UiModel.IconsDataItem>> =
            repository.searchIcons(query, premium)
                .map { pagingData -> pagingData.map { UiModel.IconsDataItem(it) } }
                .cachedIn(viewModelScope)

        searchIconsQueryResult = newResult
        return newResult

    }

    /**
     * Factory for constructing HomeFragmentViewModel
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)) {
                return HomeFragmentViewModel(application) as T
            }
            throw  IllegalArgumentException("Unable to construct ViewModel")
        }
    }
}

/**
 * UiModel is a sealed class which could host multiple data items into a single class type and
 * we can refer to this generic class.
 */
sealed class UiModel {
    data class IconSetDataItem(val iconSetsEntry: IconSetsEntry) : UiModel()
    data class IconsDataItem(val iconsEntry: IconsEntry) : UiModel()
}