package com.srilasaka.iconfinderapp.ui.home_screen.icons

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentIconsBinding
import com.srilasaka.iconfinderapp.ui.adapters.IconsAdapter
import com.srilasaka.iconfinderapp.ui.adapters.LoadStateAdapter
import com.srilasaka.iconfinderapp.ui.home_screen.HomeFragmentDirections
import com.srilasaka.iconfinderapp.ui.home_screen.HomeFragmentViewModel
import com.srilasaka.iconfinderapp.ui.utils.FILTER_SCREEN
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import com.srilasaka.iconfinderapp.utils.downloadFile
import com.srilasaka.iconfinderapp.utils.getPremium
import com.srilasaka.iconfinderapp.utils.openDialogBox
import com.srilasaka.iconfinderapp.utils.screenOrientationIsPortrait
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class IconsFragment : Fragment() {

    private val TAG: String = IconsFragment::class.java.simpleName

    /**
     * Declaring the UI Components
     */
    private var _binding: FragmentIconsBinding? = null
    private var _searchView: EditText? = null
    private var _filterView: ImageView? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val searchView get() = _searchView!!
    private val filterView get() = _filterView!!

    private val viewModel: HomeFragmentViewModel by viewModels()
    private lateinit var adapter: IconsAdapter
    private var job: Job? = null
    private val downloadManager: DownloadManager by lazy {
        context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }
    private var queryString: String = DEFAULT_QUERY

    companion object {
        private const val LAST_SEARCH_QUERY_SAVED_INSTANCE_KEY = "last_search_query"
        private const val DEFAULT_QUERY = ""

        fun newInstance(): IconsFragment {
            val fragment = IconsFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_icons, container, false)

        setUIComponents(savedInstanceState)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the search query so to recover it on screen rotations
        outState.putString(
            LAST_SEARCH_QUERY_SAVED_INSTANCE_KEY,
            queryString
        )
    }

    override fun onResume() {
        super.onResume()
        searchView.setText(queryString)
        setUpFilterViewClickListener()
    }

    override fun onPause() {
        super.onPause()
        filterView.setOnClickListener(null)
        searchView.setText(DEFAULT_QUERY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _searchView = null
        filterView.setOnClickListener(null)
        _filterView = null
    }


    /**
     * Helper method to set up UI componments
     */
    private fun setUIComponents(savedInstanceState: Bundle?) {

        // Get a reference to the SearchView in [HomeFragment] object
        _searchView = parentFragment?.view?.findViewById<EditText>(R.id.input_search_view)
        // Get a reference to the Filter View in [HomeFragment] object
        _filterView = parentFragment?.view?.findViewById(R.id.iv_filter_view)
        initAdapter()
        initSwipeToRefresh()

        queryString =
            savedInstanceState?.getString(LAST_SEARCH_QUERY_SAVED_INSTANCE_KEY) ?: DEFAULT_QUERY
        initSearch(queryString)
        // Refresh the adapter when button retry is clicked.
        binding.loadStateViewItem.btnRetry.setOnClickListener { if(this::adapter.isInitialized) adapter.refresh() }
    }

    /**
     * Helper method to initialize search view and related objects
     */
    private fun initSearch(query: String) {
        searchView.apply {
            // Set the query to the view
            setText(query)

            setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    updateListFromInputQuery()
                    true
                } else {
                    false
                }
            }

            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    updateListFromInputQuery()
                    true
                } else {
                    false
                }
            }
        }


        searchQuery(query)
    }

    /**
     * Helper method to sanitize the query and call [searchQuery]
     */
    private fun updateListFromInputQuery() {
        searchView.text.trim().let {
            if (it.isNotEmpty()) {
                queryString = it.toString()
                searchQuery(queryString)
            }
        }
    }

    /**
     * Helper method to call the [HomeFragmentViewModel.searchIcons] method from [HomeFragmentViewModel]
     * @param query -
     * @param premium - default value is retrieved from the function [getPremium]
     *
     * [getPremium] function requires
     * @param [Context], @param [FILTER_SCREEN]
     */
    private fun searchQuery(
        query: String,
        premium: PREMIUM = getPremium(requireContext(), FILTER_SCREEN.ICONS)
    ) {
        // Use viewModel object to collect the data
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.searchIcons(query, premium).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    /**
     * Helper method to initialize [IconsAdapter] and related objects
     */
    private fun initAdapter() {
        adapter = IconsAdapter(IconsAdapter.IconsAdapterClickListener(
            downloadClickListener = { downloadUrl, iconId ->
                downloadFile(
                    context,
                    downloadManager,
                    downloadUrl,
                    iconId.toString()
                )
            },
            iconItemClickListener = { iconID ->
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToIconDetailsFragment(iconID)
                )
            }
        ))
        val screenOrientationIsPortrait = screenOrientationIsPortrait(requireContext())
        val gridLayoutManager = GridLayoutManager(
            context,
            if (screenOrientationIsPortrait) 2 else 4
        )
        gridLayoutManager.apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    // Logic to make the IconSetLoadSetAdapter at Footer of the IconsAdapter span across
                    // entire screen
                    return if (adapter.getItemViewType(position) == R.layout.load_state_view_item)
                        if (screenOrientationIsPortrait) 2 else 4
                    else 1
                }

            }
        }

        binding.rvSearchIconsList.layoutManager = gridLayoutManager

        binding.rvSearchIconsList.adapter = adapter.withLoadStateFooter(
            //header = IconSetLoadSetAdapter { adapter.retry() },
            footer = LoadStateAdapter { adapter.retry() }
        )

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->

                // Adding this check as collectLatest gets triggered when the _binding is null
                if (_binding != null) {
                    // Show the Swipe Refresh when the adapter LoadState is Loading
                    binding.swipeRefresh.isRefreshing =
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
                    binding.rvSearchIconsList.isVisible =
                        loadStates.source.refresh is LoadState.NotLoading && !isEmptyList

                    // show the retry button if the initial load or refresh fails and display the error message
                    binding.loadStateViewItem.btnRetry.isVisible =
                        loadStates.source.refresh is LoadState.Error
                    binding.loadStateViewItem.tvErrorDescription.isVisible =
                        loadStates.source.refresh is LoadState.Error

                    /*val errorState = loadStates.source.append as? LoadState.Error
                        ?: loadStates.source.prepend as? LoadState.Error
                        ?: loadStates.source.append as? LoadState.Error
                        ?: loadStates.source.prepend as? LoadState.Error

                    errorState?.let {
                        binding.loadStateViewItem.tvErrorDescription.text = it.error.toString()
                    }*/
                }

            }
        }
    }

    /**
     * Helper method to update UI when no items were fetched
     */
    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.noDataViewItem.clNoDataLayout.visibility = View.VISIBLE
            binding.rvSearchIconsList.visibility = View.GONE
        } else {
            binding.noDataViewItem.clNoDataLayout.visibility = View.GONE
            binding.rvSearchIconsList.visibility = View.VISIBLE
        }
    }

    /**
     * Helper method to initialize SwipeRefresh UI Item
     */
    private fun initSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener { adapter.refresh() }
        /** Hide the progress bar on the [R.layout.load_state_view_item] as we have the swipe refresh */
        binding.loadStateViewItem.progressBar.visibility = View.GONE
    }

    /**
     * Helper method to setup filterView setOnClickListener.
     * It's been separated from the [setUIComponents] method as this has to be called in [onResume]
     * as this filterView setOnClickListener has to be set to NULL on [onPause], so that
     * setOnClickListener on filterView is not called we me move to [IconSetFragment]
     */
    private fun setUpFilterViewClickListener() {
        filterView.setOnClickListener {
            val dialog = openDialogBox(requireContext(), FILTER_SCREEN.ICONS)
            dialog.openFilterOption {
                searchQuery(queryString, it)
            }
        }
    }
}