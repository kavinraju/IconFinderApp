package com.srilasaka.iconfinderapp.ui.home_screen.icon_set

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentIconSetBinding
import com.srilasaka.iconfinderapp.ui.home_screen.HomeFragmentViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class IconSetFragment : Fragment() {

    private val TAG: String = IconSetFragment::class.java.simpleName

    /**
     * Declaring the UI Components
     */
    private var _binding: FragmentIconSetBinding? = null
    private var _searchView: EditText? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val searchView get() = _searchView!!

    private val viewModel: HomeFragmentViewModel by viewModels()
    private val adapter = IconSetAdapter()
    private var job: Job? = null
    private lateinit var queryString: String

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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_icon_set, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUIComponents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _searchView = null
    }

    /**
     * Helper method to set up UI components
     */
    private fun setUIComponents() {
        initAdapter()
        initSwipeToRefresh()
        // Refresh the adapter when button retry is clicked.
        binding.loadStateViewItem.btnRetry.setOnClickListener { adapter.refresh() }
    }

    /**
     * Helper method to initialize [IconSetAdapter] and related objects
     */
    private fun initAdapter() {
        binding.rvIconSetList.adapter = adapter.withLoadStateFooter(
            //header = IconSetLoadSetAdapter { adapter.retry() },
            footer = IconSetLoadSetAdapter { adapter.retry() }
        )

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->

                // Show the Swipe Refresh when the adapter LoadState is Loading
                binding.swiperefresh.isRefreshing =
                    loadStates.source.refresh is LoadState.Loading
                Log.d(TAG, "loadStateFlow loadStates = $loadStates")
                Log.d(
                    TAG,
                    "loadStateFlow loadStates.mediator?.refresh = ${loadStates.source.refresh}"
                )

                // Show list is empty
                val isEmptyList =
                    loadStates.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                showEmptyList(isEmptyList)

                // Only show the list if refresh succeeds
                binding.rvIconSetList.isVisible =
                    loadStates.source.refresh is LoadState.NotLoading && !isEmptyList

                // show the retry button if the initial load or refresh fails and display the error message
                binding.loadStateViewItem.btnRetry.isVisible =
                    loadStates.source.refresh is LoadState.Error || isEmptyList
                binding.loadStateViewItem.tvErrorDescription.isVisible =
                    loadStates.source.refresh is LoadState.Error || isEmptyList

                /*val errorState = loadStates.source.append as? LoadState.Error
                    ?: loadStates.source.prepend as? LoadState.Error
                    ?: loadStates.source.append as? LoadState.Error
                    ?: loadStates.source.prepend as? LoadState.Error

                errorState?.let {
                    binding.loadStateViewItem.tvErrorDescription.text = it.error.toString()
                }*/


            }
        }

        // Use viewModel object to collect the data
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.iconSetsQuery().collectLatest {
                adapter.submitData(it)
                Log.d(TAG, "collectLatest $it")
            }
        }
    }

    /**
     * Helper method to update UI when no items were fetched
     */
    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.noDataViewItem.clNoDataLayout.visibility = View.VISIBLE
            binding.rvIconSetList.visibility = View.GONE
        } else {
            binding.noDataViewItem.clNoDataLayout.visibility = View.GONE
            binding.rvIconSetList.visibility = View.VISIBLE
        }
    }

    /**
     * Helper method to initialize SwipeRefresh UI Item
     */
    private fun initSwipeToRefresh() {
        binding.swiperefresh.setOnRefreshListener { adapter.refresh() }
        /** Hide the progress bar on the [R.layout.load_state_view_item] as we have the swipe refresh */
        binding.loadStateViewItem.progressBar.visibility = View.GONE
    }
}