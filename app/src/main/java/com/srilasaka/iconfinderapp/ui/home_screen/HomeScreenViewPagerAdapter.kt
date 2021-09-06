/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.ui.home_screen

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.srilasaka.iconfinderapp.ui.home_screen.icon_set.IconSetFragment
import com.srilasaka.iconfinderapp.ui.home_screen.icons.IconsFragment

class HomeScreenViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return IconSetFragment.newInstance()
            }
            1 -> {
                return IconsFragment.newInstance()
            }
            else -> throw  ClassCastException("Unknown fragment ${position}")
        }
    }
}