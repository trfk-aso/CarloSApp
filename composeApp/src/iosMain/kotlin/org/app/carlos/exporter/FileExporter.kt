package org.app.carlos.exporter

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*

actual class FileExporter {

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun exportTextFile(fileName: String, content: String): Boolean {
        return try {
            val documents = NSFileManager.defaultManager
                .URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)

            val documentsDir = documents[0] as? NSURL ?: return false
            val fileURL = documentsDir.URLByAppendingPathComponent(fileName)!!

            val nsContent = NSString.create(string = content)
            nsContent.writeToURL(
                fileURL,
                atomically = true,
                encoding = NSUTF8StringEncoding.toULong(),
                error = null
            )
            true
        } catch (e: Throwable) {
            println(e)
            false
        }
    }
}

@Composable
actual fun provideFileExporter(): FileExporter = FileExporter()