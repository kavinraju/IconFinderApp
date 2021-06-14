package com.srilasaka.iconfinderapp.ui.home_screen.icons

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.ui.home_screen.UiModel

class IconsAdapter(private val clickListener: IconsAdapterClickListener) :
    PagingDataAdapter<UiModel.IconsDataItem, ViewHolder>(UI_MODEL_COMPARATOR) {

    companion object {
        private val UI_MODEL_COMPARATOR =
            object : DiffUtil.ItemCallback<UiModel.IconsDataItem>() {
                override fun areContentsTheSame(
                    oldItem: UiModel.IconsDataItem,
                    newItem: UiModel.IconsDataItem
                ): Boolean {
                    //return (oldItem is UiModel.IconsDataItem && newItem is UiModel.IconsDataItem) &&
                    return oldItem.iconsEntry.icon_id == newItem.iconsEntry.icon_id
                }

                override fun areItemsTheSame(
                    oldItem: UiModel.IconsDataItem,
                    newItem: UiModel.IconsDataItem
                ): Boolean =
                    oldItem == newItem
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.recycler_item_icon_set) {
            IconsViewHolder.from(parent)
        } else {
            IconsViewHolder.from(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            // For the loaded data items from the Paging source
            position < itemCount -> {

                when (getItem(position)) {
                    is UiModel.IconsDataItem -> R.layout.recycler_item_icon_set
                    null -> throw UnsupportedOperationException("Unknown view")
                    else -> throw UnsupportedOperationException("Unknown view")
                }
            }
            // For the footer item
            position == itemCount -> {
                R.layout.load_state_view_item
            }
            else -> throw UnsupportedOperationException("Unknown view")
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.IconsDataItem -> (holder as IconsViewHolder).bind(
                    uiModel.iconsEntry,
                    clickListener
                )
            }
        }
    }

    /**
     * Click Listener
     */
    class IconsAdapterClickListener(val downloadClickListener: (downloadUrl: String, iconID: Int) -> Unit) {
        fun onClickDownloadButton(downloadUrl: String, iconID: Int) =
            downloadClickListener(downloadUrl, iconID)
    }
}