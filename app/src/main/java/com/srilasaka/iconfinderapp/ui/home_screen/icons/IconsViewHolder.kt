package com.srilasaka.iconfinderapp.ui.home_screen.icons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srilasaka.iconfinderapp.databinding.RecyclerItemIconsBinding
import com.srilasaka.iconfinderapp.local_database.icons_table.IconsEntry

/**
 * View Holder of the IconSet RecyclerView list item
 */
class IconsViewHolder private constructor(
    val binding: RecyclerItemIconsBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(icon: IconsEntry?, clickListener: IconsAdapter.IconsAdapterClickListener) {
        if (icon == null) {
            //Show the Placeholder UI
        } else {
            binding.model = icon
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): IconsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RecyclerItemIconsBinding.inflate(layoutInflater, parent, false)
            return IconsViewHolder(binding)
        }
    }
}