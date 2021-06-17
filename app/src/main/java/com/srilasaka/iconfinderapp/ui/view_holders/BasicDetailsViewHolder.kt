package com.srilasaka.iconfinderapp.ui.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srilasaka.iconfinderapp.databinding.LayoutBasicDetailsBinding
import com.srilasaka.iconfinderapp.ui.adapters.BasicDetailsAdapter
import com.srilasaka.iconfinderapp.ui.models.BasicDetailsModel

/**
 * View Holder of the BasicDetails Layout RecyclerView list item
 */
class BasicDetailsViewHolder private constructor(
    val binding: LayoutBasicDetailsBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        basicDetailsModel: BasicDetailsModel,
        clickListener: BasicDetailsAdapter.BasicDetailsAdapterClickListener
    ) {
        binding.model = basicDetailsModel
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): BasicDetailsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = LayoutBasicDetailsBinding.inflate(layoutInflater, parent, false)
            return BasicDetailsViewHolder(binding)
        }
    }
}