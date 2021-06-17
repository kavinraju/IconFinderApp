package com.srilasaka.iconfinderapp.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.ui.utils.UiModel
import com.srilasaka.iconfinderapp.ui.view_holders.AuthorDetailsViewHolder

class AuthorDetailsAdapter() :
    ListAdapter<UiModel.AuthorDataItem, ViewHolder>(UI_MODEL_COMPARATOR) {

    companion object {
        private val UI_MODEL_COMPARATOR =
            object : DiffUtil.ItemCallback<UiModel.AuthorDataItem>() {
                override fun areContentsTheSame(
                    oldItem: UiModel.AuthorDataItem,
                    newItem: UiModel.AuthorDataItem
                ): Boolean {
                    //return (oldItem is UiModel.AuthorDataItem && newItem is UiModel.AuthorDataItem) &&
                    return oldItem.authorEntry.author_id == newItem.authorEntry.author_id
                }

                override fun areItemsTheSame(
                    oldItem: UiModel.AuthorDataItem,
                    newItem: UiModel.AuthorDataItem
                ): Boolean =
                    oldItem == newItem
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return AuthorDetailsViewHolder.from(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.AuthorDataItem -> R.layout.layout_author_details
            null -> throw UnsupportedOperationException("Unknown view")
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.AuthorDataItem -> (holder as AuthorDetailsViewHolder).bind(
                    uiModel.authorEntry
                    //clickListener
                )
            }
        }
    }

    /**
     * Click Listener
     */
    class AuthorDetailsAdapterClickListener() {
    }
}