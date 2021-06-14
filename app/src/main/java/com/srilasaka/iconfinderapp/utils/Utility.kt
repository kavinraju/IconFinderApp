package com.srilasaka.iconfinderapp

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.srilasaka.iconfinderapp.network.utils.EndPoints


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
