package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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

    /**
     * Persists a company brand logo URI into internal storage.
     */
    fun persistCompanyLogo(context: Context, uri: Uri): String {
        return try {
            val logoDir = File(context.filesDir, "company_logos").apply { mkdirs() }
            val fileName = "logo_${System.currentTimeMillis()}_${(1000..9999).random()}.png"
            val destFile = File(logoDir, fileName)

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

    /**
     * Extracts human-readable file name from a Uri.
     */
    fun getFileNameFromUri(context: Context, uri: Uri): String {
        var name = "theme_file.zip"
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index != -1) {
                            val str = cursor.getString(index)
                            if (!str.isNullOrBlank()) name = str
                        }
                    }
                }
            } catch (_: Exception) {}
        } else {
            uri.path?.let { p ->
                val cut = p.lastIndexOf('/')
                if (cut != -1) name = p.substring(cut + 1)
            }
        }
        return name
    }

    /**
     * Persists an uploaded theme package file (.zip, .mtz, .hwt, etc.) into persistent app storage.
     * Returns Pair(absoluteFilePath, fileSizeBytes).
     */
    fun persistThemeFile(context: Context, uri: Uri): Pair<String, Long> {
        return try {
            val themesDir = File(context.filesDir, "themes_storage").apply { mkdirs() }
            val originalName = getFileNameFromUri(context, uri).ifBlank { "theme_${System.currentTimeMillis()}.zip" }
            val safeName = "s18_theme_${System.currentTimeMillis()}_$originalName"
            val destFile = File(themesDir, safeName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            Pair(destFile.absolutePath, destFile.length())
        } catch (e: Exception) {
            Pair(uri.toString(), 0L)
        }
    }
}

