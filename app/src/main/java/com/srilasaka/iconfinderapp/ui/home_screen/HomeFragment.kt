/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.ui.home_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    /**
     * Declaring the UI Components
     */
    private var _binding: FragmentHomeBinding? = null

    /**
     * Declaring required objects
     */
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: HomeScreenViewPagerAdapter
    private val viewModel: HomeFragmentViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val factory = HomeFragmentViewModel.Factory(application)
        ViewModelProvider(this, factory).get(HomeFragmentViewModel::class.java)
    }

    private val TAG: String? = HomeFragment::class.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialise ViewPagerAdapter and set up the TabLayout
        viewPagerAdapter = HomeScreenViewPagerAdapter(this)
        binding.viewPagerHomeScreen.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayoutHome, binding.viewPagerHomeScreen) { tab, position ->
            tab.text = when (position) {
                0 -> {
                    Log.d(TAG, "tab_1_icon_set")
                    getString(R.string.tab_1_icon_set)
                }
                1 -> {
                    Log.d(TAG, "tab_2_icons")
                    getString(R.string.tab_2_icons)
                }
                else -> ""
            }
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}