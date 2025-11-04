package org.app.carlos.exporter

import androidx.compose.runtime.Composable

expect class FileExporter {
    suspend fun exportTextFile(fileName: String, content: String): Boolean
}

@Composable
expect fun provideFileExporter(): FileExporter