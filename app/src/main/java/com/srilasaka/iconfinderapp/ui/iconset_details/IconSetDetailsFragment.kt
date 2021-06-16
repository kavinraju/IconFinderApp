package com.srilasaka.iconfinderapp.ui.iconset_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.srilasaka.iconfinderapp.databinding.FragmentIconSetDetailsBinding
import com.srilasaka.iconfinderapp.network.utils.State

/**
 * [Fragment] to display the details od the IconSet from [IconSetFragment].
 */
class IconSetDetailsFragment : Fragment() {

    private var _binding: FragmentIconSetDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var args: IconSetDetailsFragmentArgs

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
    }

    private fun showStatusSuccess() {
        // Set the load_state_view_item
        binding.loadStateViewItem.tvErrorDescription.visibility = View.GONE
        binding.loadStateViewItem.btnRetry.visibility = View.GONE
        binding.loadStateViewItem.progressBar.visibility = View.GONE

        // Set the layout_basic_details
        binding.layoutBasicDetails.clLayoutBasicDetails.visibility = View.VISIBLE
    }

    private fun showStatusLoading() {
        // Set the load_state_view_item
        binding.loadStateViewItem.tvErrorDescription.visibility = View.GONE
        binding.loadStateViewItem.btnRetry.visibility = View.GONE
        binding.loadStateViewItem.progressBar.visibility = View.VISIBLE

        // Set the layout_basic_details
        binding.layoutBasicDetails.clLayoutBasicDetails.visibility = View.GONE
    }

    private fun showStatusFailed() {
        // Set the load_state_view_item
        binding.loadStateViewItem.tvErrorDescription.visibility = View.VISIBLE
        binding.loadStateViewItem.btnRetry.visibility = View.VISIBLE
        binding.loadStateViewItem.progressBar.visibility = View.GONE

        // Set the layout_basic_details
        binding.layoutBasicDetails.clLayoutBasicDetails.visibility = View.GONE
    }

}