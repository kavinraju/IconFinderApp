package com.srilasaka.iconfinderapp.ui.icon_details

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.srilasaka.iconfinderapp.databinding.FragmentIconDetailsBinding
import com.srilasaka.iconfinderapp.network.utils.State
import com.srilasaka.iconfinderapp.utils.downloadFile

/**
 * [Fragment] to display the details od the IconSet from [IconsFragment].
 */
class IconDetailsFragment : Fragment() {

    private val TAG: String? = IconDetailsFragment::class.simpleName

    private var _binding: FragmentIconDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var args: IconDetailsFragmentArgs
    private val downloadManager: DownloadManager by lazy {
        context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    private val viewModel: IconDetailsFragmentViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val factory = IconDetailsFragmentViewModel.Factory(
            application,
            args.iconID
        )

        ViewModelProvider(this, factory).get(IconDetailsFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get the arguments through safe args
        args = IconDetailsFragmentArgs.fromBundle(requireArguments())

        // Get a reference to the binding object
        _binding = FragmentIconDetailsBinding.inflate(inflater, container, false)

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

        // Set the basic Icon Details fetched
        viewModel.iconDetailsFlow.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loading -> {
                    showStatusLoading()
                }
                is State.Success -> {
                    showStatusSuccess()
                    viewModel.setIconDetails(state.data)
                    //setUpIconDetailsOnUI(viewModel.basicDetailsModel.value)
                }
                is State.Failed -> {
                    showStatusFailed()
                    Log.d(TAG, "State.Failed ${state.message}")
                }
            }

        })

        // Refresh the iconsAdapter when button retry is clicked.
        binding.loadStateViewItem.btnRetry.setOnClickListener {
            viewModel.refreshIconDetails()
        }

        // Download the file when Download button is clicked.
        binding.btnIconDownload.setOnClickListener {
            downloadFile(
                context,
                downloadManager,
                viewModel.iconDetails.value?.format_64_download_url,
                viewModel.iconDetails.value?.icon_id.toString()
            )
        }

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

}