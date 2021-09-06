/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.ui.home_screen.icon_set

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srilasaka.iconfinderapp.databinding.RecyclerItemIconSetBinding
import com.srilasaka.iconfinderapp.local_database.icon_set_table.IconSetsEntry
import com.srilasaka.iconfinderapp.ui.adapters.IconSetAdapter

/**
 * View Holder of the IconSet RecyclerView list item
 */
class IconSetViewHolder private constructor(val binding: RecyclerItemIconSetBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        iconSetsEntry: IconSetsEntry?,
        clickListener: IconSetAdapter.IconSetAdapterClickListener
    ) {
        if (iconSetsEntry == null) {
            //Show the Loading UI
        } else {
            binding.model = iconSetsEntry
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): IconSetViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RecyclerItemIconSetBinding.inflate(layoutInflater, parent, false)
            return IconSetViewHolder(binding)
        }
    }
}