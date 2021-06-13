package com.srilasaka.iconfinderapp.ui.home_screen.icon_set

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentIconSetBinding
import com.srilasaka.iconfinderapp.ui.home_screen.HomeFragmentViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class IconSetFragment : Fragment() {

    /**
     * Declaring the UI Components
     */
    private val TAG: String = IconSetFragment::class.java.simpleName
    private lateinit var binding: FragmentIconSetBinding
    private val viewModel: HomeFragmentViewModel by activityViewModels()
    private val adapter = IconSetAdapter()
    private var job: Job? = null

    companion object {
        fun newInstance(): IconSetFragment {
            return IconSetFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_icon_set, container, false)
        Log.d(TAG, "onCreateView")
        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.iconSetsQuery().collectLatest {
                adapter.submitData(it)
            }
        }

    }

    private fun initAdapter() {
        binding.rvIconSetList.adapter = adapter
        /*.withLoadStateHeaderAndFooter(
        header = LoadStateAdapter(), // { adapter.retry() },
        footer = LoadStateAdapter { adapter.retry() }
    )*/
    }
}