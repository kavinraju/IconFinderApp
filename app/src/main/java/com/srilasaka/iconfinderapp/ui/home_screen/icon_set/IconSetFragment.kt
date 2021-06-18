package com.srilasaka.iconfinderapp.ui.home_screen.icon_set

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentIconSetBinding
import com.srilasaka.iconfinderapp.ui.adapters.IconSetAdapter
import com.srilasaka.iconfinderapp.ui.adapters.LoadStateAdapter
import com.srilasaka.iconfinderapp.ui.home_screen.HomeFragmentDirections
import com.srilasaka.iconfinderapp.ui.home_screen.HomeFragmentViewModel
import com.srilasaka.iconfinderapp.ui.home_screen.icons.IconsFragment
import com.srilasaka.iconfinderapp.ui.utils.FILTER_SCREEN
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import com.srilasaka.iconfinderapp.utils.getPremium
import com.srilasaka.iconfinderapp.utils.openDialogBox
import com.srilasaka.iconfinderapp.utils.screenOrientationIsPortrait
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class
IconSetFragment : Fragment() {

    private val TAG: String = IconSetFragment::class.java.simpleName

    /**
     * Declaring the UI Components
     */
    private var _binding: FragmentIconSetBinding? = null
    private var _searchView: EditText? = null
    private var _filterView: ImageView? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val searchView get() = _searchView!!
    private val filterView get() = _filterView!!

    private val viewModel: HomeFragmentViewModel by viewModels()
    private lateinit var adapter: IconSetAdapter
    private var job: Job? = null

    companion object {
        fun newInstance(): IconSetFragment {
            return IconSetFragment()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_icon_set, container, false)

        setUIComponents()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUpFilterViewClickListener()
        // Make SearchView of Type [EditText] not editable
        searchView.inputType = 0x00000000
    }

    override fun onPause() {
        super.onPause()
        filterView.setOnClickListener(null)
        // Make SearchView of Type [EditText] editable
        searchView.inputType = 0x00000001
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _searchView = null
        filterView.setOnClickListener(null)
        _filterView = null
    }

    /**
     * Helper method to set up UI components
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUIComponents() {

        // Get a reference to the SearchView in [HomeFragment] object
        _searchView = parentFragment?.view?.findViewById<EditText>(R.id.input_search_view)
        // Get a reference to the Filter view in [HomeFragment] object
        _filterView = parentFragment?.view?.findViewById(R.id.iv_filter_view)

        initAdapter()
        initSwipeToRefresh()
        // Refresh the adapter when button retry is clicked.
        binding.loadStateViewItem.btnRetry.setOnClickListener { if(this::adapter.isInitialized) adapter.refresh() }

        searchView.setOnClickListener {
            Snackbar.make(
                binding.root,
                getString(R.string.search_option_is_not_available_now),
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null).show()
        }
    }

    /**
     * Helper method to call the [HomeFragmentViewModel.iconSetsQuery] method from [HomeFragmentViewModel]
     * @param premium - default value is retrieved from the function [getPremium]
     *
     * [getPremium] function requires
     * @param [Context], @param [FILTER_SCREEN]
     */
    private fun queryIconSetsData(
        premium: PREMIUM = getPremium(
            requireContext(),
            FILTER_SCREEN.ICON_SET
        )
    ) {
        // Use viewModel object to collect the data
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.iconSetsQuery(premium).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    /**
     * Helper method to initialize [IconSetAdapter] and related objects
     */
    private fun initAdapter() {
        adapter =
            IconSetAdapter(IconSetAdapter.IconSetAdapterClickListener { iconSetID: Int, price: String ->
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToIconSetDetailsFragment(
                        iconSetID,
                        price
                    )
                )
            })

        // Get the screen orientation and set the layoutManager of binding.rvIconSetList accordingly.
        // Use LinearLayoutManager for Portrait mode.
        // Use GridLayoutManager for Landscape mode.
        val screenOrientationIsPortrait = screenOrientationIsPortrait(requireContext())
        val layoutManager = if (screenOrientationIsPortrait) LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        else {
            GridLayoutManager(context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        // Logic to make the LoadStateAdapter at Footer of the IconSetAdapter span across
                        // entire screen
                        return if (adapter.getItemViewType(position) == R.layout.load_state_view_item)
                            if (screenOrientationIsPortrait) 1 else 2
                        else 1
                    }

                }
            }
        }
        binding.rvIconSetList.layoutManager = layoutManager

        binding.rvIconSetList.adapter = adapter.withLoadStateFooter(
            //header = IconSetLoadSetAdapter { adapter.retry() },
            footer = LoadStateAdapter { adapter.retry() }
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

        // Fetch the IconSet data
        queryIconSetsData()
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

    /**
     * Helper method to setup filterView setOnClickListener.
     * It's been separated from the [setUIComponents] method as this has to be called in [onResume]
     * as this filterView setOnClickListener has to be set to NULL on [onPause], so that
     * setOnClickListener on filterView is not called we me move to [IconsFragment]
     */
    private fun setUpFilterViewClickListener() {
        filterView.setOnClickListener {
            val dialog = openDialogBox(requireContext(), FILTER_SCREEN.ICON_SET)
            dialog.openFilterOption {
                queryIconSetsData(it)
            }
        }
    }
}