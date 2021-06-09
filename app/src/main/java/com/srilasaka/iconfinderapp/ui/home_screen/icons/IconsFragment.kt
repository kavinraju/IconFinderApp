package com.srilasaka.iconfinderapp.ui.home_screen.icons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentIconSetBinding
import com.srilasaka.iconfinderapp.databinding.FragmentIconsBinding

class IconsFragment : Fragment() {

    /**
     * Declaring the UI Components
     */
    private lateinit var binding: FragmentIconsBinding

    companion object {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_icons, container,false)

        return binding.root
    }
}