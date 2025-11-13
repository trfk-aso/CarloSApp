package org.app.carlos.exporter

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

lateinit var appContext: Context

fun initFileOpener(context: Context) {
    appContext = context.applicationContext
}

actual class FileOpener {
    actual fun openFile(path: String) {
        try {
            var file = File(appContext.getExternalFilesDir(null), path)
            if (!file.exists()) {
                file = File(appContext.filesDir, path)
            }

            if (!file.exists()) {
                println(" File not found: ${file.absolutePath}")
                return
            }

            val uri = FileProvider.getUriForFile(
                appContext,
                "${appContext.packageName}.provider",
                file
            )

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/plain")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(viewIntent, "Open with").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val resolveInfo = appContext.packageManager.resolveActivity(viewIntent, 0)
            if (resolveInfo != null) {
                println(" Found app to open file, launching viewer...")
                appContext.startActivity(chooser)
            } else {
                println(" No viewer app found, launching share dialog instead...")
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val shareChooser = Intent.createChooser(shareIntent, "Share file").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                appContext.startActivity(shareChooser)
            }

        } catch (e: Exception) {
            println(" Error opening file: ${e.message}")
        }
    }
}