package org.app.carlos.exporter

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File

actual class FileExporter(private val context: Context) {

    actual suspend fun exportTextFile(fileName: String, content: String): Boolean {
        return try {
            val file = File(context.getExternalFilesDir(null), fileName)
            file.writeText(content)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

@Composable
actual fun provideFileExporter(): FileExporter {
    val context = LocalContext.current
    return FileExporter(context)
}