/*
 * Copyright (C) 2021 Kavin Raju S
 * Licensed under the Zero Noncommercial Public License v1.0
 *
 *
 */

package com.srilasaka.iconfinderapp.utils

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.srilasaka.iconfinderapp.network.utils.EndPoints
import com.srilasaka.iconfinderapp.ui.utils.DialogBoxes
import com.srilasaka.iconfinderapp.ui.utils.FILTER_SCREEN
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM

/**
 * Declaring the SPAN COUNT values used in this application
 */
const val SPAN_COUNT_1 = 1
const val SPAN_COUNT_2 = 2
const val SPAN_COUNT_4 = 4

/**
Helper method to download a file provided with
 * context
 * downloadManager
 * url of the file
 * filename - this is used to store the file with its name and desired extension
 */
fun downloadFile(
    context: Context?,
    downloadManager: DownloadManager?,
    url: String?,
    fileName: String
): Boolean {
    return try {
        Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .mkdirs()
        val request = DownloadManager.Request(Uri.parse(url))
        request.addRequestHeader("Authorization", EndPoints.AuthorizationHeader)
        request.setTitle("Downloading $fileName")
        request.setDescription("Downloading")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            fileName
        )
        downloadManager?.enqueue(request)
            ?: Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show()

        DownloadManager.STATUS_SUCCESSFUL == 8
    } catch (e: Exception) {
        false
    }
}

/**
 * Helper method to open the DialogBoxes that has been implemented in [DialogBoxes]
 * @param context -
 * @param filterScreen - [FILTER_SCREEN] ENUM which has the screen that implemented the filter option
 */
fun openDialogBox(context: Context, filterScreen: FILTER_SCREEN): DialogBoxes {
    Log.d("openDialogBox()", "filterScreen $filterScreen")
    val sharedPrefID = when (filterScreen) {
        FILTER_SCREEN.ICON_SET -> {
            SharedPreferencesUtils.USER_FILTER_PREFERENCES_ICON_SET_ID
        }
        FILTER_SCREEN.ICONS -> {
            SharedPreferencesUtils.USER_FILTER_PREFERENCES_ICONS_ID
        }
    }
    val sharedPreferencesUtils = SharedPreferencesUtils(context, sharedPrefID)
    val premium = when (filterScreen) {
        FILTER_SCREEN.ICON_SET -> {
            Log.d(
                "openDialogBox()",
                "getIconSetIsPremiumFilterOption ${sharedPreferencesUtils.getIconSetIsPremiumFilterOption()}"
            )
            PREMIUM.valueOf(sharedPreferencesUtils.getIconSetIsPremiumFilterOption())
        }
        FILTER_SCREEN.ICONS -> {
            Log.d(
                "openDialogBox()",
                "getIconsIsPremiumFilterOption ${sharedPreferencesUtils.getIconsIsPremiumFilterOption()}"
            )
            PREMIUM.valueOf(sharedPreferencesUtils.getIconsIsPremiumFilterOption())
        }
    }
    Log.d("openDialogBox()", "premium $premium")

    return DialogBoxes(
        context,
        filterScreen,
        premium
    )
}

/**
 * Helper function to get the stored premium value from [SharedPreferencesUtils] based on [FILTER_SCREEN]
 */
fun getPremium(context: Context, filterScreen: FILTER_SCREEN): PREMIUM {
    return when (filterScreen) {
        FILTER_SCREEN.ICON_SET -> {
            val sharedPreferencesUtils = SharedPreferencesUtils(
                context, SharedPreferencesUtils.USER_FILTER_PREFERENCES_ICON_SET_ID
            )
            PREMIUM.valueOf(sharedPreferencesUtils.getIconsIsPremiumFilterOption())
        }
        FILTER_SCREEN.ICONS -> {
            val sharedPreferencesUtils = SharedPreferencesUtils(
                context, SharedPreferencesUtils.USER_FILTER_PREFERENCES_ICONS_ID
            )
            PREMIUM.valueOf(sharedPreferencesUtils.getIconsIsPremiumFilterOption())
        }
    }
}

/**
 * Helper function to get the stored premium value from [SharedPreferencesUtils] based on [FILTER_SCREEN]
 */
fun screenOrientationIsPortrait(context: Context): Boolean {
    return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

/**
 * Helper method to check if the network connection is available or not.
 */
fun checkConnectionStatus(context: Context, connectionType: Int): Boolean {
    val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connMgr.activeNetwork ?: return false
        val activeNetwork = connMgr.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(connectionType) -> true
            else -> false
        }
    } else {
        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network)?.apply {
                return type == connectionType
            }
        }
    }
    return false
}

/**
 * Helper function to check the storage permission
 */
fun Fragment.checkStoragePermission(requestCode: Int): Boolean {
    return if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            requestCode
        )
        false
    } else {
        true
    }
}
