package com.example.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ThemeImageUtils {

    /**
     * Persists a chosen content:// or file:// URI into internal persistent app storage
     * so that preview and cover images never expire and can be loaded reliably without permission loss.
     */
    fun persistImageUri(context: Context, uri: Uri): String {
        return try {
            val previewDir = File(context.filesDir, "previews").apply { mkdirs() }
            val fileName = "preview_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg"
            val destFile = File(previewDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            uri.toString()
        }
    }
}
