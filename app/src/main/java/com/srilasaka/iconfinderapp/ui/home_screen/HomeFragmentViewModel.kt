package com.srilasaka.iconfinderapp.ui.home_screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.srilasaka.iconfinderapp.data.IconFinderRepository
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String? = HomeFragmentViewModel::class.simpleName

    // Dependency Injection Happens here
    private val repository: IconFinderRepository = IconFinderRepository(
        IconFinderAPIService.create(),
        IconsFinderDatabase.getInstance(application)
    )
    /*val database = IconsFinderDatabase.getInstance(application)
    private val iconSetDataSource: DataSource.Factory<Int, IconSetsEntry> =
        database.iconSetsDao.getIconSets()
    val myPagingConfig = Config(
        pageSize = 20,
        prefetchDistance = 150,
        enablePlaceholders = false
    )
    val iconSetList = iconSetDataSource.toLiveData(pageSize = 20)*/

    private var iconSetsQueryResult: Flow<PagingData<UiModel.IconSetDataItem>>? = null

    @ExperimentalPagingApi
    fun iconSetsQuery(): Flow<PagingData<UiModel.IconSetDataItem>> {
        Log.d(TAG, "iconSetsQuery")
        val previousResult = iconSetsQueryResult

        val newResult: Flow<PagingData<UiModel.IconSetDataItem>> = repository.getPublicIconSets()
            .map { pagingData -> pagingData.map { UiModel.IconSetDataItem(it) } }
            .cachedIn(viewModelScope)

        iconSetsQueryResult = newResult
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

sealed class UiModel {
    data class IconSetDataItem(val iconSetsEntry: IconSetsEntry) : UiModel()
}