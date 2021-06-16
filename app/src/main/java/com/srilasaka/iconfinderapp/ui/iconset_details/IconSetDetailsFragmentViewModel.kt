package com.srilasaka.iconfinderapp.ui.iconset_details

import android.app.Application
import androidx.lifecycle.*
import com.srilasaka.iconfinderapp.data.IconFinderRepository
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icon_set_details_table.IconSetDetailsEntry
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.local_database.icons_table.IconsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import com.srilasaka.iconfinderapp.network.utils.State
import com.srilasaka.iconfinderapp.ui.models.BasicDetailsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
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
        _iconSetDetails.value = iconSetDetailsEntry
        _basicDetailsModel.value = BasicDetailsModel(
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

/**
 * UiModel is a sealed class which could host multiple data items into a single class type and
 * we can refer to this generic class.
 */
sealed class UiModel {
    data class IconSetDataItem(val iconSetsEntry: IconSetsEntry) : UiModel()
    data class IconsDataItem(val iconsEntry: IconsEntry) : UiModel()
}