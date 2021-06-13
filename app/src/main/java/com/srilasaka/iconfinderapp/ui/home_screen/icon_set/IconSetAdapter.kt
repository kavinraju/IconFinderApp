package com.srilasaka.iconfinderapp.ui.home_screen.icon_set

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.ui.home_screen.UiModel

class IconSetAdapter : PagingDataAdapter<UiModel.IconSetDataItem, ViewHolder>(UI_MODEL_COMPARATOR) {

    companion object {
        private val UI_MODEL_COMPARATOR =
            object : DiffUtil.ItemCallback<UiModel.IconSetDataItem>() {
                override fun areContentsTheSame(
                    oldItem: UiModel.IconSetDataItem,
                    newItem: UiModel.IconSetDataItem
                ): Boolean {
                    //return (oldItem is UiModel.IconSetDataItem && newItem is UiModel.IconSetDataItem) &&
                    return oldItem.iconSetsEntry.iconset_id == newItem.iconSetsEntry.iconset_id
                }

                override fun areItemsTheSame(
                    oldItem: UiModel.IconSetDataItem,
                    newItem: UiModel.IconSetDataItem
                ): Boolean =
                    oldItem == newItem
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.recycler_item_icon_set) {
            IconSetViewHolder.from(parent)
        } else {
            IconSetViewHolder.from(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.IconSetDataItem -> R.layout.recycler_item_icon_set
            null -> throw UnsupportedOperationException("Unknown view")
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.IconSetDataItem -> (holder as IconSetViewHolder).bind(uiModel.iconSetsEntry)
            }
        }
    }
}