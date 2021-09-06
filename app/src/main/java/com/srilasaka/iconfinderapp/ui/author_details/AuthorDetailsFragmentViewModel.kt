/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.ui.author_details

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.srilasaka.iconfinderapp.data.IconFinderRepository
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.author_details_table.AuthorEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import com.srilasaka.iconfinderapp.network.utils.State
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import com.srilasaka.iconfinderapp.ui.utils.UiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class AuthorDetailsFragmentViewModel(application: Application, authorID: Int, licenseType: String) :
    AndroidViewModel(application) {

    private val TAG: String? = AuthorDetailsFragmentViewModel::class.simpleName

    // Dependency Injection Happens here
    private val repository: IconFinderRepository = IconFinderRepository(
        IconFinderAPIService.create(),
        IconsFinderDatabase.getInstance(application)
    )

    /**
     *  _authorID is declared to store the passed to the fragment [IconSetDetailsFragment].
     *  authorID is exposed to the View.
     */
    private val _authorID = MutableLiveData<Int>()
    private val authorIDLiveData: LiveData<Int> = _authorID

    /**
     *  _licenseType is declared to store the [String]
     *  licenseType is exposed to the View.
     */
    private val _licenseType = MutableLiveData<String>()
    val licenseType: LiveData<String> = _licenseType

    /**
     *  _basicDetailsModel is declared to store the passed to the fragment [IconSetDetailsFragment].
     *  authorID is exposed to the View.
     */
    private val _authorEntry = MutableLiveData<AuthorEntry>()
    val authorEntry: LiveData<AuthorEntry> = _authorEntry

    private var iconSetIconsResult: Flow<PagingData<UiModel.IconSetDataItem>>? = null

    init {
        _authorID.value = authorID
        _licenseType.value = licenseType
    }

    /**
     * Declaring the [AuthorEntry] as a Flow object
     */
    val authorEntryFlow = liveData<State<AuthorEntry>>(Dispatchers.Main) {
        emitSource(
            repository.getAuthorDetails(authorIDLiveData.value!!)
                .onStart {
                    emit(State.Loading())
                }.catch {
                    emit(State.failed(it.message ?: ""))
                }.asLiveData(Dispatchers.IO)
        )
    }

    /**
     * Helper function to set [authorEntry]
     */
    fun setAuthorDetails(authorEntry: AuthorEntry) {
        // Set the licenseType value to the authorEntry
        //authorEntry.licenseType = licenseType.value
        _authorEntry.value = AuthorEntry(
            authorEntry.author_id,
            authorEntry.iconsets_count,
            authorEntry.name,
            authorEntry.website_url,
            licenseType.value,
            authorEntry.detail
        )

    }

    /**
     * fetchIconSetOfAuthor() function queries the data from the [IconFinderRepository.getIconSetsOfAuthor].
     */
    fun fetchIconSetOfAuthor(
        authorID: Int,
        premium: PREMIUM
    ): Flow<PagingData<UiModel.IconSetDataItem>> {
        Log.d(TAG, "fetchIconSetOfAuthor")

        val newResult: Flow<PagingData<UiModel.IconSetDataItem>> =
            repository.getIconSetsOfAuthor(authorIDLiveData.value!!, premium)
                .map { pagingData ->
                    pagingData.map {
                        UiModel.IconSetDataItem(it)
                    }
                }
                .cachedIn(viewModelScope)

        iconSetIconsResult = newResult
        return newResult

    }

    /**
     * Getter of authorIDLiveData
     */
    fun getAuthorID(): Int = authorIDLiveData.value!!

    /**
     * Factory for constructing HomeFragmentViewModel
     */
    class Factory(
        private val application: Application,
        private val authorID: Int,
        private val licenseType: String
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthorDetailsFragmentViewModel::class.java)) {
                return AuthorDetailsFragmentViewModel(application, authorID, licenseType) as T
            }
            throw  IllegalArgumentException("Unable to construct ViewModel")
        }
    }
}