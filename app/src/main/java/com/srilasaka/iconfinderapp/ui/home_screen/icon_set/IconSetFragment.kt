package com.srilasaka.iconfinderapp.ui.home_screen.icon_set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentIconSetBinding

class IconSetFragment: Fragment() {

    /**
     * Declaring the UI Components
     */
    private lateinit var binding: FragmentIconSetBinding

    companion object{
        fun newInstance(): IconSetFragment{
            val fragment = IconSetFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_icon_set, container,false)

        return binding.root
    }
}