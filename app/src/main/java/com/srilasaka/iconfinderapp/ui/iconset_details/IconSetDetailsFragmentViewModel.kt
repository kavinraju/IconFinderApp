/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.ui.iconset_details

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.srilasaka.iconfinderapp.data.IconFinderRepository
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icon_set_details_table.IconSetDetailsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import com.srilasaka.iconfinderapp.network.utils.State
import com.srilasaka.iconfinderapp.ui.models.BasicDetailsModel
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import com.srilasaka.iconfinderapp.ui.utils.UiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class IconSetDetailsFragmentViewModel(application: Application, iconSetID: Int, price: String) :
    AndroidViewModel(application) {

    private val TAG: String? = IconSetDetailsFragmentViewModel::class.simpleName

    // Dependency Injection Happens here
    private val repository: IconFinderRepository = IconFinderRepository(
        IconFinderAPIService.create(),
        IconsFinderDatabase.getInstance(application)
    )

    /**
     *  _iconSetID is declared to store the passed to the fragment [IconSetDetailsFragment].
     *  iconSetID is exposed to the View.
     */
    private val _iconSetID = MutableLiveData<Int>()
    val iconSetIDLiveData: LiveData<Int> = _iconSetID

    /**
     *  _price is declared to store the passed to the fragment [IconSetDetailsFragment].
     *  price is exposed to the View.
     */
    private val _price = MutableLiveData<String>()
    val price: LiveData<String> = _price

    /**
     *  _iconSetDetails is declared to store the [IconSetDetailsEntry]
     *  iconSetDetails is exposed to the View.
     */
    private val _iconSetDetails = MutableLiveData<IconSetDetailsEntry>()
    val iconSetDetails: LiveData<IconSetDetailsEntry> = _iconSetDetails

    /**
     *  _iconSetID is declared to store the passed to the fragment [IconSetDetailsFragment].
     *  iconSetID is exposed to the View.
     */
    private val _basicDetailsModel = MutableLiveData<BasicDetailsModel>()
    val basicDetailsModel: LiveData<BasicDetailsModel> = _basicDetailsModel

    private var iconSetIconsResult: Flow<PagingData<UiModel.IconsDataItem>>? = null

    init {
        _iconSetID.value = iconSetID
        _price.value = price
    }

    /**
     * Declaring the [IconSetDetailsEntry] as a Flow object
     */
    val iconSetDetailsFlow = liveData<State<IconSetDetailsEntry>>(Dispatchers.Main) {
        emitSource(
            repository.getIconSetDetails(iconSetIDLiveData.value!!)
                .onStart {
                    emit(State.Loading())
                }.catch {
                    emit(State.failed(it.message ?: ""))
                }.asLiveData(Dispatchers.IO)
        )
    }

    /**
     * Helper function to set [iconSetDetails]
     */
    fun setIconSetDetails(iconSetDetailsEntry: IconSetDetailsEntry) {
        Log.d(TAG, "setIconSetDetails: iconSetDetailsEntry $iconSetDetailsEntry")
        _iconSetDetails.value = iconSetDetailsEntry
        _basicDetailsModel.value = BasicDetailsModel(
            iconSetDetailsEntry.author_author_id,
            iconSetDetailsEntry.author_user_id,
            iconSetDetailsEntry.author_name ?: "N/A",
            iconSetDetailsEntry.is_premium!!,
            iconSetDetailsEntry.readme ?: "N/A",
            iconSetDetailsEntry.license_name ?: "N/A",
            price.value ?: "N/A",
            iconSetDetailsEntry.name ?: "N/A",
            iconSetDetailsEntry.type ?: "N/A",
            iconSetDetailsEntry.website_url ?: "N/A"
        )
    }

    /**
     * iconSetIcons() function queries the data from the [IconFinderRepository.getIconSetIcons].
     */
    fun fetchIconSetIcons(
        iconsetID: Int,
        premium: PREMIUM
    ): Flow<PagingData<UiModel.IconsDataItem>> {
        Log.d(TAG, "iconSetIcons")

        val newResult: Flow<PagingData<UiModel.IconsDataItem>> =
            repository.getIconSetIcons(iconsetID, premium)
                .map { pagingData ->
                    pagingData.map {
                        UiModel.IconsDataItem(it)
                    }
                }
                .cachedIn(viewModelScope)

        iconSetIconsResult = newResult
        return newResult

    }

    /**
     * Getter of iconSetIDLiveData
     */
    fun getIconSetID(): Int = iconSetIDLiveData.value!!

    /**
     * Getter of price
     */
    fun getPrice(): String = price.value!!

    /**
     * Factory for constructing HomeFragmentViewModel
     */
    class Factory(
        private val application: Application,
        private val iconSetID: Int,
        private val price: String
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(IconSetDetailsFragmentViewModel::class.java)) {
                return IconSetDetailsFragmentViewModel(application, iconSetID, price) as T
            }
            throw  IllegalArgumentException("Unable to construct ViewModel")
        }
    }
}