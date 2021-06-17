package com.srilasaka.iconfinderapp.ui.icon_details

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.srilasaka.iconfinderapp.data.IconFinderRepository
import com.srilasaka.iconfinderapp.local_database.IconsFinderDatabase
import com.srilasaka.iconfinderapp.local_database.icon_details_table.IconDetailsEntry
import com.srilasaka.iconfinderapp.network.services.IconFinderAPIService
import com.srilasaka.iconfinderapp.network.utils.State
import com.srilasaka.iconfinderapp.ui.models.BasicDetailsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

class IconDetailsFragmentViewModel(application: Application, iconID: Int) :
    AndroidViewModel(application) {

    private val TAG: String? = IconDetailsFragmentViewModel::class.simpleName

    // Dependency Injection Happens here
    private val repository: IconFinderRepository = IconFinderRepository(
        IconFinderAPIService.create(),
        IconsFinderDatabase.getInstance(application)
    )

    /**
     *  _iconID is declared to store the passed to the fragment [IconDetailsFragment].
     *  iconID is exposed to the View.
     */
    private val _iconID = MutableLiveData<Int>()
    val iconIDLiveData: LiveData<Int> = _iconID

    /**
     *  _iconDetails is declared to store the [IconDetailsEntry]
     *  iconDetails is exposed to the View.
     */
    private val _iconDetails = MutableLiveData<IconDetailsEntry>()
    val iconDetails: LiveData<IconDetailsEntry> = _iconDetails

    /**
     *  _basicDetailsModel is declared to store the [BasicDetailsModel].
     *  basicDetailsModel is exposed to the View.
     */
    private val _basicDetailsModel = MutableLiveData<BasicDetailsModel>()
    val basicDetailsModel: LiveData<BasicDetailsModel> = _basicDetailsModel

    init {
        _iconID.value = iconID
    }

    /**
     * Declaring the [IconDetailsEntry] as a Flow object
     */
    val iconDetailsFlow = liveData<State<IconDetailsEntry>>(Dispatchers.Main) {
        Log.d(TAG, "iconDetailsFlow")
        emitSource(
            repository.getIconDetails(iconIDLiveData.value!!)
                .onStart {
                    emit(State.Loading())
                }.catch {
                    emit(State.failed(it.message ?: ""))
                }.asLiveData(Dispatchers.IO)
        )
    }

    /**
     * Helper function to set [iconDetails]
     */
    fun setIconDetails(iconDetailsEntry: IconDetailsEntry) {
        Log.d(TAG, "setIconSetDetails: iconSetDetailsEntry $iconDetailsEntry")
        _iconDetails.value = iconDetailsEntry
        _basicDetailsModel.value = BasicDetailsModel(
            iconDetailsEntry.iconset?.author_id ?: -1,
            iconDetailsEntry.iconset?.author_name ?: "N/A",
            iconDetailsEntry.is_premium!!,
            iconDetailsEntry.iconset?.readme ?: "N/A",
            iconDetailsEntry.iconset?.license_name ?: "N/A",
            iconDetailsEntry.iconset?.price ?: "N/A",
            iconDetailsEntry.iconset?.name ?: "N/A",
            iconDetailsEntry.type ?: "N/A",
            iconDetailsEntry.iconset?.website_url ?: "N/A"
        )
    }

    /**
     * Getter of iconIDLiveData
     */
    fun getIconID(): Int = iconIDLiveData.value!!

    /**
     * Helper function to refresh the IconDetailsEntry in iconDetailsFlow
     */
    fun refreshIconDetails() {

    }

    /**
     * Factory for constructing HomeFragmentViewModel
     */
    class Factory(
        private val application: Application,
        private val iconID: Int
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(IconDetailsFragmentViewModel::class.java)) {
                return IconDetailsFragmentViewModel(application, iconID) as T
            }
            throw  IllegalArgumentException("Unable to construct ViewModel")
        }
    }
}