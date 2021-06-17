package com.srilasaka.iconfinderapp.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.ui.utils.UiModel
import com.srilasaka.iconfinderapp.ui.view_holders.BasicDetailsViewHolder

class BasicDetailsAdapter(private val clickListener: BasicDetailsAdapterClickListener) :
    ListAdapter<UiModel.BasicDetailsDataItem, ViewHolder>(UI_MODEL_COMPARATOR) {

    companion object {
        private val UI_MODEL_COMPARATOR =
            object : DiffUtil.ItemCallback<UiModel.BasicDetailsDataItem>() {
                override fun areContentsTheSame(
                    oldItem: UiModel.BasicDetailsDataItem,
                    newItem: UiModel.BasicDetailsDataItem
                ): Boolean {
                    //return (oldItem is UiModel.BasicDetailsDataItem && newItem is UiModel.BasicDetailsDataItem) &&
                    return oldItem.basicDetailsModel.creatorName == newItem.basicDetailsModel.creatorName
                }

                override fun areItemsTheSame(
                    oldItem: UiModel.BasicDetailsDataItem,
                    newItem: UiModel.BasicDetailsDataItem
                ): Boolean =
                    oldItem == newItem
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return BasicDetailsViewHolder.from(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.BasicDetailsDataItem -> R.layout.layout_basic_details
            null -> throw UnsupportedOperationException("Unknown view")
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.BasicDetailsDataItem -> (holder as BasicDetailsViewHolder).bind(
                    uiModel.basicDetailsModel,
                    clickListener
                )
            }
        }
    }

    /**
     * Click Listener
     */
    class BasicDetailsAdapterClickListener(val onClickCreatorNameListener: (authorID: Int?, userID: Int?, licenseType: String) -> Unit) {

        fun onClickCreatorName(authorID: Int?, userID: Int?, licenseType: String) =
            onClickCreatorNameListener(authorID, userID, licenseType)
    }
}