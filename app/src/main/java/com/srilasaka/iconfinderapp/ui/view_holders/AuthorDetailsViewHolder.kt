package com.srilasaka.iconfinderapp.ui.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srilasaka.iconfinderapp.databinding.LayoutAuthorDetailsBinding
import com.srilasaka.iconfinderapp.local_database.author_details_table.AuthorEntry

/**
 * View Holder of the AuthorDetails Layout RecyclerView list item
 */
class AuthorDetailsViewHolder private constructor(
    val binding: LayoutAuthorDetailsBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        authorEntry: AuthorEntry,
        //clickListener: AuthorDetailsAdapter.AuthorDetailsAdapterClickListener
    ) {
        binding.model = authorEntry
        //binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): AuthorDetailsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = LayoutAuthorDetailsBinding.inflate(layoutInflater, parent, false)
            return AuthorDetailsViewHolder(binding)
        }
    }
}