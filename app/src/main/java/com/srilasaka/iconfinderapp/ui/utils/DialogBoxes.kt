/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.ui.utils

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.srilasaka.iconfinderapp.R
import com.srilasaka.iconfinderapp.utils.SharedPreferencesUtils

/**
 * [DialogBoxes] implements the dialog boxes required in this app.
 */
class DialogBoxes(
    private val context: Context,
    private val filterScreen: FILTER_SCREEN,
    private val selectedIsPremium: PREMIUM
) {

    /**
     * This creates a MaterialAlertDialogBuilder for the showing the filter options based on only [PREMIUM] values
     */
    fun openFilterOption(onFilterSelected: (premium: PREMIUM) -> Unit) {

        var checkedItem = selectedIsPremium.ordinal

        MaterialAlertDialogBuilder(context)
            .setTitle(context.resources.getString(R.string.filter))
            .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog: DialogInterface?, which: Int ->
            }
            .setPositiveButton(context.resources.getString(R.string.select)) { dialog: DialogInterface?, which: Int ->

                val premium = when (checkedItem) {
                    PREMIUM.TRUE.ordinal -> {
                        savePremiumPreference(context, PREMIUM.TRUE)
                    }
                    PREMIUM.FALSE.ordinal -> {
                        savePremiumPreference(context, PREMIUM.FALSE)
                    }
                    else -> savePremiumPreference(context, PREMIUM.ALL)
                }
                onFilterSelected(premium)

            }.setSingleChoiceItems(
                arrayOf(
                    context.getString(R.string.premium),
                    context.getString(R.string.free),
                    PREMIUM.ALL.name
                ),
                checkedItem
            ) { dialog: DialogInterface?, which: Int ->
                checkedItem = which
            }.show()
    }

    /**
     * Helper function to save the [PREMIUM] values selected
     * @return [PREMIUM] - returns the @param premium for simplicity purpose
     */
    private fun savePremiumPreference(context: Context, premium: PREMIUM): PREMIUM {
        when (filterScreen) {
            FILTER_SCREEN.ICON_SET -> {
                val sharedPrefID = SharedPreferencesUtils.USER_FILTER_PREFERENCES_ICON_SET_ID
                val sharedPreferences = SharedPreferencesUtils(context, sharedPrefID)
                sharedPreferences.setIconSetIsPremiumFilterOption(premium.name)
            }
            FILTER_SCREEN.ICONS -> {
                val sharedPrefID = SharedPreferencesUtils.USER_FILTER_PREFERENCES_ICONS_ID
                val sharedPreferences = SharedPreferencesUtils(context, sharedPrefID)
                sharedPreferences.setIconsIsPremiumFilterOption(premium.name)

            }
        }
        return premium
    }
}