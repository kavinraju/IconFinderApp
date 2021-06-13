package com.srilasaka.iconfinderapp.ui.home_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.srilasaka.iconfinderapp.databinding.LoadStateViewItemBinding

class LoadStateViewHolder(
    private val binding: LoadStateViewItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnRetry.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.btnRetry.isVisible = loadState is LoadState.Error
        binding.tvErrorDescription.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = LoadStateViewItemBinding.inflate(layoutInflater, parent, false)
            return LoadStateViewHolder(binding, retry)
        }
    }
}