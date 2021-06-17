package com.srilasaka.iconfinderapp.ui.author_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentAuthorDetailsBinding
import com.srilasaka.iconfinderapp.local_database.author_details_table.AuthorEntry
import com.srilasaka.iconfinderapp.network.utils.State
import com.srilasaka.iconfinderapp.ui.adapters.*
import com.srilasaka.iconfinderapp.ui.utils.FILTER_SCREEN
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import com.srilasaka.iconfinderapp.ui.utils.UiModel
import com.srilasaka.iconfinderapp.utils.getPremium
import com.srilasaka.iconfinderapp.utils.screenOrientationIsPortrait
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * [Fragment] to display the details of Creator from [IconSetFragment] or [IconsFragment].
 */
class AuthorDetailsFragment : Fragment() {

    private val TAG: String = AuthorDetailsFragment::class.java.simpleName

    private var _binding: FragmentAuthorDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var args: AuthorDetailsFragmentArgs
    private lateinit var iconSetAdapter: IconSetAdapter
    private lateinit var authorDetailsAdapter: AuthorDetailsAdapter
    private var job: Job? = null

    private val viewModel: AuthorDetailsFragmentViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val factory = AuthorDetailsFragmentViewModel.Factory(
            application,
            args.authorID,
            args.licenseType
        )
        ViewModelProvider(this, factory).get(AuthorDetailsFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get the arguments through safe args
        args = AuthorDetailsFragmentArgs.fromBundle(requireArguments())

        // Get a reference to the binding object
        _binding = FragmentAuthorDetailsBinding.inflate(inflater, container, false)

        setUIComponents()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUIComponents() {

        // Set viewModel to binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Set the basic IconSet Details fetched
        viewModel.authorEntryFlow.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loading -> {
                    showStatusLoading()
                }
                is State.Success -> {
                    showStatusSuccess()
                    // detail value is returned from API if there's no data
                    viewModel.setAuthorDetails(state.data)
                }
                is State.Failed -> {
                    showStatusFailed()
                }
                is State.NoData -> {
                    showEmptyList(true)
                }
            }

        })

        viewModel.authorEntry.observe(viewLifecycleOwner, Observer { authorEntry ->
            initAdapter(authorEntry)
        })
        // Refresh the iconSetAdapter when button retry is clicked.
        binding.loadStateViewItem.btnRetry.setOnClickListener { if (this::iconSetAdapter.isInitialized) iconSetAdapter.refresh() }
    }

    /**
     * Helper function to show the UI on Success of fetch of basic IconSet Details
     */
    private fun showStatusSuccess() {
        // Set the load_state_view_item
        binding.loadStateViewItem.tvErrorDescription.visibility = View.GONE
        binding.loadStateViewItem.btnRetry.visibility = View.GONE
        binding.loadStateViewItem.progressBar.visibility = View.GONE

        // Set the layout_basic_details
        //binding.layoutBasicDetails.clLayoutBasicDetails.visibility = View.VISIBLE
    }

    /**
     * Helper function to show the UI on Loading of fetch of basic IconSet Details
     */
    private fun showStatusLoading() {
        // Set the load_state_view_item
        binding.loadStateViewItem.tvErrorDescription.visibility = View.GONE
        binding.loadStateViewItem.btnRetry.visibility = View.GONE
        binding.loadStateViewItem.progressBar.visibility = View.VISIBLE

        // Set the layout_basic_details
        //binding.layoutBasicDetails.clLayoutBasicDetails.visibility = View.GONE
    }

    /**
     * Helper function to show the UI on Failed Status of fetch of basic IconSet Details
     */
    private fun showStatusFailed() {
        // Set the load_state_view_item
        binding.loadStateViewItem.tvErrorDescription.visibility = View.VISIBLE
        binding.loadStateViewItem.btnRetry.visibility = View.VISIBLE
        binding.loadStateViewItem.progressBar.visibility = View.GONE

        // Set the layout_basic_details
        //binding.layoutBasicDetails.clLayoutBasicDetails.visibility = View.GONE
    }

    /**
     * Helper method to initialize [IconSetAdapter] and related objects
     */
    private fun initAdapter(authorEntry: AuthorEntry) {
        iconSetAdapter =
            IconSetAdapter(IconSetAdapter.IconSetAdapterClickListener { iconSetID: Int, price: String ->
                findNavController().navigate(
                    AuthorDetailsFragmentDirections.actionAuthorDetailsFragmentToIconSetDetailsFragment(
                        iconSetID,
                        price
                    )
                )
            })

        setAuthorDetailsAdapter(authorEntry)

        // Logic for screen item layout manager
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
                        return if ((position == 0 && authorDetailsAdapter.getItemViewType(position) == R.layout.layout_author_details)
                            || iconSetAdapter.getItemViewType(position) == R.layout.load_state_view_item && position == iconSetAdapter.itemCount + 1
                        )
                            if (screenOrientationIsPortrait) 1 else 2
                        else 1
                    }

                }
            }
        }

        binding.rvIconsetOfAuthorList.layoutManager = layoutManager

        val concatAdapter = ConcatAdapter(authorDetailsAdapter, iconSetAdapter.withLoadStateFooter(
            //header = IconSetLoadSetAdapter { iconSetAdapter.retry() },
            footer = LoadStateAdapter { iconSetAdapter.retry() }
        ))
        binding.rvIconsetOfAuthorList.adapter = concatAdapter

        lifecycleScope.launchWhenCreated {

            iconSetAdapter.loadStateFlow.collectLatest { loadStates ->

                // Show list is empty
                val isEmptyList =
                    loadStates.source.refresh is LoadState.NotLoading && iconSetAdapter.itemCount == 0
                showEmptyList(isEmptyList)

                // Only show the list if refresh succeeds
                binding.rvIconsetOfAuthorList.isVisible =
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

        // Fetch the Author's Iconsets
        fetchIconSetOfAuthor(viewModel.getAuthorID())
    }

    /**
     * Helper function to set the [AuthorDetailsAdapter]
     */
    private fun setAuthorDetailsAdapter(authorEntry: AuthorEntry) {
        authorDetailsAdapter = AuthorDetailsAdapter()

        // set the @param authorEntry to the authorDetailsAdapter
        lifecycleScope.launch {
            authorDetailsAdapter.submitList(listOf(UiModel.AuthorDataItem(authorEntry)))
        }
    }

    /**
     * Helper method to call the [AuthorDetailsFragmentViewModel.fetchIconSetOfAuthor] method from [AuthorDetailsFragmentViewModel]
     * @param authorID - authorID is passed through Safe Args
     * @param premium - default value is retrieved from the function [getPremium]
     *
     * [getPremium] function requires
     * @param [Context], @param [FILTER_SCREEN]
     */
    private fun fetchIconSetOfAuthor(
        authorID: Int,
        premium: PREMIUM = getPremium(requireContext(), FILTER_SCREEN.ICON_SET)
    ) {
        Log.d(TAG, "fetchIconSetOfAuthor")
        // Use viewModel object to collect the data
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.fetchIconSetOfAuthor(authorID, premium).collectLatest {
                iconSetAdapter.submitData(it)
            }
        }
    }

    /**
     * Helper method to update UI when no items were fetched
     */
    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.noDataViewItem.clNoDataLayout.visibility = View.VISIBLE
            binding.rvIconsetOfAuthorList.visibility = View.GONE
            binding.loadStateViewItem.progressBar.visibility = View.GONE
        } else {
            binding.noDataViewItem.clNoDataLayout.visibility = View.GONE
            binding.rvIconsetOfAuthorList.visibility = View.VISIBLE
        }
    }
}