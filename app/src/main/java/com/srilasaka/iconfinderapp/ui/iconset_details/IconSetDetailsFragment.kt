package com.srilasaka.iconfinderapp.ui.iconset_details

import android.app.DownloadManager
import android.content.Context
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
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentIconSetDetailsBinding
import com.srilasaka.iconfinderapp.network.utils.State
import com.srilasaka.iconfinderapp.ui.adapters.BasicDetailsAdapter
import com.srilasaka.iconfinderapp.ui.adapters.IconsAdapter
import com.srilasaka.iconfinderapp.ui.adapters.LoadStateAdapter
import com.srilasaka.iconfinderapp.ui.models.BasicDetailsModel
import com.srilasaka.iconfinderapp.ui.utils.FILTER_SCREEN
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM
import com.srilasaka.iconfinderapp.ui.utils.UiModel
import com.srilasaka.iconfinderapp.utils.downloadFile
import com.srilasaka.iconfinderapp.utils.getPremium
import com.srilasaka.iconfinderapp.utils.screenOrientationIsPortrait
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * [Fragment] to display the details od the IconSet from [IconSetFragment].
 */
class IconSetDetailsFragment : Fragment() {

    private val TAG: String = IconSetDetailsFragment::class.java.simpleName

    private var _binding: FragmentIconSetDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var args: IconSetDetailsFragmentArgs
    private lateinit var iconsAdapter: IconsAdapter
    private lateinit var basicDetailsAdapter: BasicDetailsAdapter
    private var job: Job? = null
    private val downloadManager: DownloadManager by lazy {
        context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    private val viewModel: IconSetDetailsFragmentViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val factory = IconSetDetailsFragmentViewModel.Factory(
            application,
            args.iconSetID,
            args.price
        )
        ViewModelProvider(this, factory).get(IconSetDetailsFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get the arguments through safe args
        args = IconSetDetailsFragmentArgs.fromBundle(requireArguments())

        // Get a reference to the binding object
        _binding = FragmentIconSetDetailsBinding.inflate(inflater, container, false)

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
        viewModel.iconSetDetailsFlow.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loading -> {
                    showStatusLoading()
                }
                is State.Success -> {
                    showStatusSuccess()
                    viewModel.setIconSetDetails(state.data)
                }
                is State.Failed -> {
                    showStatusFailed()
                }
            }

        })

        viewModel.basicDetailsModel.observe(viewLifecycleOwner, { baseDetailsModel ->
            // Set the RecyclerView iconsAdapter for IconSet Icons fetched.
            // Initialize here as we need baseDetailsModel data for the adapter
            initAdapter(baseDetailsModel)
        })

        // Refresh the iconsAdapter when button retry is clicked.
        binding.loadStateViewItem.btnRetry.setOnClickListener { iconsAdapter.refresh() }
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
     * Helper method to initialize [IconsAdapter] and related objects
     */
    private fun initAdapter(baseDetailsModel: BasicDetailsModel) {
        iconsAdapter = IconsAdapter(IconsAdapter.IconsAdapterClickListener(
            downloadClickListener = { downloadUrl, iconId ->
                downloadFile(context, downloadManager, downloadUrl, iconId.toString())
            },
            iconItemClickListener = { iconID ->
                findNavController().navigate(
                    IconSetDetailsFragmentDirections.actionIconSetDetailsFragmentToIconDetailsFragment(iconID)
                )
            }
        )
        )

        setBasicDetailsAdapter(baseDetailsModel)

        val screenOrientationIsPortrait = screenOrientationIsPortrait(requireContext())
        val gridLayoutManager = GridLayoutManager(
            context,
            if (screenOrientationIsPortrait) 2 else 4
        )
        gridLayoutManager.apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    // Logic to make the BasicDetailsAdapter at 0th position and IconSetLoadSetAdapter
                    // at Footer of the IconsAdapter, span across entire screen.
                    // About checks we make
                    // position == 0 because we want the R.layout.load_state_view_item at the last position
                    // position == iconsAdapter.itemCount + 1 because we want the R.layout.layout_basic_details at the first position
                    return if ((position == 0 && basicDetailsAdapter.getItemViewType(position) == R.layout.layout_basic_details)
                        || (iconsAdapter.getItemViewType(position) == R.layout.load_state_view_item && position == iconsAdapter.itemCount + 1)
                    )
                        if (screenOrientationIsPortrait) 2 else 4
                    else 1
                }

            }
        }

        binding.rvIconsetIconsList.layoutManager = gridLayoutManager

        val concatAdapter = ConcatAdapter(basicDetailsAdapter, iconsAdapter.withLoadStateFooter(
            //header = IconSetLoadSetAdapter { iconsAdapter.retry() },
            footer = LoadStateAdapter { iconsAdapter.retry() }
        ))
        binding.rvIconsetIconsList.adapter = concatAdapter

        lifecycleScope.launchWhenCreated {

            iconsAdapter.loadStateFlow.collectLatest { loadStates ->

                // Show list is empty
                val isEmptyList =
                    loadStates.source.refresh is LoadState.NotLoading && iconsAdapter.itemCount == 0
                showEmptyList(isEmptyList)

                // Only show the list if refresh succeeds
                binding.rvIconsetIconsList.isVisible =
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

        fetchIconSetIcons(viewModel.getIconSetID())
    }

    /**
     * Helper function to set the [BasicDetailsAdapter]
     */
    private fun setBasicDetailsAdapter(baseDetailsModel: BasicDetailsModel) {
        basicDetailsAdapter = BasicDetailsAdapter(
            BasicDetailsAdapter.BasicDetailsAdapterClickListener(onClickCreatorNameListener = { authorID: Int, userID: Int ->

            })
        )

        // set the @param baseDetailsModel to the basicDetailsAdapter
        lifecycleScope.launch {
            basicDetailsAdapter.submitList(listOf(UiModel.BasicDetailsDataItem(baseDetailsModel)))
        }
    }

    /**
     * Helper method to call the [IconSetDetailsFragmentViewModel.fetchIconSetIcons] method from [IconSetDetailsFragmentViewModel]
     * @param iconsetID - iconsetID is passed through Safe Args
     * @param premium - default value is retrieved from the function [getPremium]
     *
     * [getPremium] function requires
     * @param [Context], @param [FILTER_SCREEN]
     */
    private fun fetchIconSetIcons(
        iconsetID: Int,
        premium: PREMIUM = getPremium(requireContext(), FILTER_SCREEN.ICON_SET)
    ) {
        Log.d(TAG, "fetchIconSetIcons")
        // Use viewModel object to collect the data
        job?.cancel()
        job = lifecycleScope.launch {
            Log.d(TAG, "lifecycleScope.launch")
            viewModel.fetchIconSetIcons(iconsetID, premium).collectLatest {
                iconsAdapter.submitData(it)
            }
        }
    }

    /**
     * Helper method to update UI when no items were fetched
     */
    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.noDataViewItem.clNoDataLayout.visibility = View.VISIBLE
            binding.rvIconsetIconsList.visibility = View.GONE
        } else {
            binding.noDataViewItem.clNoDataLayout.visibility = View.GONE
            binding.rvIconsetIconsList.visibility = View.VISIBLE
        }
    }
}