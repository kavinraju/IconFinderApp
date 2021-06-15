package com.srilasaka.iconfinderapp.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.srilasaka.iconfinderapp.network.utils.EndPoints
import com.srilasaka.iconfinderapp.ui.utils.DialogBoxes
import com.srilasaka.iconfinderapp.ui.utils.FILTER_SCREEN
import com.srilasaka.iconfinderapp.ui.utils.PREMIUM


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
            PREMIUM.valueOf(sharedPreferencesUtils.getIconSetIsPremiumFilterOption())
        }
        FILTER_SCREEN.ICONS -> {
            PREMIUM.valueOf(sharedPreferencesUtils.getIconsIsPremiumFilterOption())
        }
    }

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
    val sharedPrefID = when (filterScreen) {
        FILTER_SCREEN.ICON_SET -> {
            SharedPreferencesUtils.USER_FILTER_PREFERENCES_ICON_SET_ID
        }
        FILTER_SCREEN.ICONS -> {
            SharedPreferencesUtils.USER_FILTER_PREFERENCES_ICONS_ID
        }
    }
    val sharedPreferencesUtils = SharedPreferencesUtils(
        context, sharedPrefID
    )
    return PREMIUM.valueOf(sharedPreferencesUtils.getIconsIsPremiumFilterOption())
}
