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
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentIconDetailsBinding
import com.srilasaka.iconfinderapp.network.utils.State
import com.srilasaka.iconfinderapp.ui.adapters.BasicDetailsAdapter
import com.srilasaka.iconfinderapp.utils.checkStoragePermission
import com.srilasaka.iconfinderapp.utils.downloadFile

private const val CHECK_STORAGE_PERMISSION_CODE = 100

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

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUIComponents()
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
            if (checkStoragePermission(CHECK_STORAGE_PERMISSION_CODE)) {
                downloadFile(
                    context,
                    downloadManager,
                    viewModel.iconDetails.value?.format_64_download_url,
                    viewModel.iconDetails.value?.icon_id.toString()
                )
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.storage_permission_not_available),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        // Set the clickListener to the R.layout.layout_basic_details
        val clickListener = BasicDetailsAdapter.BasicDetailsAdapterClickListener(
            onClickCreatorNameListener = { authorID: Int?, userID: Int?, licenseType: String ->
                if (authorID == null && userID != null) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.sorry_no_data_available),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null).show()
                } else if (authorID != null) {
                    findNavController().navigate(
                        IconDetailsFragmentDirections.actionIconDetailsFragmentToAuthorDetailsFragment(
                            authorID,
                            viewModel.iconDetails.value?.iconset?.license_name ?: "N/A"
                        )
                    )
                }
            })
        binding.layoutBasicDetails.clickListener = clickListener
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