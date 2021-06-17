package com.srilasaka.iconfinderapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM

class SharedPreferencesUtils(context: Context, sharedPreferenceID: Int) {

    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor

    companion object {
        private const val PRIVATE_MODE = 0
        const val DEFAULT_PREFERENCES_NAME = "default_pref"
        const val USER_FILTER_PREFERENCES_ICON_SET_ID = 0
        const val USER_FILTER_PREFERENCES_ICON_SET_NAME = "user_filter_pref_for_icon_set_query"
        const val USER_FILTER_PREFERENCES_ICONS_ID = 1
        const val USER_FILTER_PREFERENCES_ICONS_NAME = "user_filter_pref_for_icons_query"

        // Keys
        const val FILTER_ICON_SET_IS_PREMIUM_KEY = "filter_icon_set_is_premium_key"
        const val FILTER_ICONS_IS_PREMIUM_KEY = "filter_icons_is_premium_key"
    }

    init {
        sharedPreferences = when (sharedPreferenceID) {
            USER_FILTER_PREFERENCES_ICON_SET_ID -> context.getSharedPreferences(
                USER_FILTER_PREFERENCES_ICON_SET_NAME, PRIVATE_MODE
            )
            USER_FILTER_PREFERENCES_ICONS_ID -> context.getSharedPreferences(
                USER_FILTER_PREFERENCES_ICONS_NAME, PRIVATE_MODE
            )
            else -> context.getSharedPreferences(
                DEFAULT_PREFERENCES_NAME, PRIVATE_MODE
            )
        }

        editor = sharedPreferences.edit()
    }

    /**
     * ICON SET
     * FILTER_ICON_SET_IS_PREMIUM_KEY can hold 3 types of values
     *  1. true
     *  2. false
     *  3. all
     */
    fun setIconSetIsPremiumFilterOption(isPremium: String) {
        Log.d("setIconSetIsPremiumFi", "isPremium $isPremium")
        editor.apply {
            putString(FILTER_ICON_SET_IS_PREMIUM_KEY, isPremium)
            apply()
        }
    }

    /**
     * ICON SET
     * FILTER_ICON_SET_IS_PREMIUM_KEY can hold 3 types of values
     *  1. true
     *  2. false
     *  3. all
     *  @return default value is all
     */
    fun getIconSetIsPremiumFilterOption(): String {
        return sharedPreferences.getString(FILTER_ICON_SET_IS_PREMIUM_KEY, PREMIUM.ALL.name)
            ?: PREMIUM.ALL.name
    }

    /**
     * ICONS
     * FILTER_ICONS_IS_PREMIUM_KEY can hold 3 types of values
     *  1. true
     *  2. false
     *  3. all
     */
    fun setIconsIsPremiumFilterOption(isPremium: String) {
        Log.d("setIconsIsPremiumFilter", "isPremium $isPremium")
        editor.apply {
            putString(FILTER_ICONS_IS_PREMIUM_KEY, isPremium)
            apply()
        }
    }

    /**
     * ICONS
     * FILTER_ICONS_IS_PREMIUM_KEY can hold 3 types of values
     *  1. true
     *  2. false
     *  3. all
     *  @return default value is all
     */
    fun getIconsIsPremiumFilterOption(): String {
        return sharedPreferences.getString(FILTER_ICONS_IS_PREMIUM_KEY, PREMIUM.ALL.name)
            ?: PREMIUM.ALL.name
    }

}