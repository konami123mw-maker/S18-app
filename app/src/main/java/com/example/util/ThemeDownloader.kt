package com.example.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object ThemeDownloader {

    /**
     * Downloads an external theme file directly to the user's Android device storage (Downloads folder),
     * showing native system download notifications and actual file delivery rather than a mere web redirect.
     */
    fun startRealDownload(
        context: Context,
        url: String,
        themeTitle: String,
        versionStr: String = "1.0",
        onStatus: (String) -> Unit = {}
    ) {
        val cleanUrl = url.trim()
        if (cleanUrl.isBlank()) {
            val errMsg = "ملف الثيم أو الرابط غير محدد"
            Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
            onStatus(errMsg)
            return
        }

        val safeTitle = themeTitle.trim().replace(Regex("[^a-zA-Z0-9_\\-\\.]"), "_").ifEmpty { "theme" }
        val safeVersion = versionStr.trim().replace(Regex("[^a-zA-Z0-9_\\-\\.]"), "_").ifEmpty { "1.0" }

        // Determine appropriate extension (.mtz, .zip, .hwt, etc.)
        val extension = when {
            cleanUrl.endsWith(".mtz", ignoreCase = true) -> ".mtz"
            cleanUrl.endsWith(".hwt", ignoreCase = true) -> ".hwt"
            cleanUrl.endsWith(".apk", ignoreCase = true) -> ".apk"
            cleanUrl.endsWith(".theme", ignoreCase = true) -> ".theme"
            cleanUrl.endsWith(".zip", ignoreCase = true) -> ".zip"
            cleanUrl.contains(".mtz", ignoreCase = true) -> ".mtz"
            cleanUrl.contains(".zip", ignoreCase = true) -> ".zip"
            else -> ".zip"
        }
        val fileName = "S18_${safeTitle}_v${safeVersion}$extension"

        // Handle local file uploaded directly from device
        if (cleanUrl.startsWith("/") || cleanUrl.startsWith("file://") || cleanUrl.startsWith("content://")) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!downloadsDir.exists()) downloadsDir.mkdirs()
                    val targetFile = File(downloadsDir, fileName)

                    if (cleanUrl.startsWith("content://")) {
                        val uri = Uri.parse(cleanUrl)
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(targetFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                    } else {
                        val srcPath = if (cleanUrl.startsWith("file://")) cleanUrl.removePrefix("file://") else cleanUrl
                        val srcFile = File(srcPath)
                        if (srcFile.exists()) {
                            srcFile.copyTo(targetFile, overwrite = true)
                        } else {
                            throw java.io.FileNotFoundException("الملف غير موجود في الجهاز: $srcPath")
                        }
                    }

                    withContext(Dispatchers.Main) {
                        val msg = "تم حفظ ملف الثيم بنجاح في مجلد التنزيلات (Downloads): $fileName"
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        onStatus(msg)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        val err = "فشل حفظ ملف الثيم: ${e.localizedMessage}"
                        Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        onStatus(err)
                    }
                }
            }
            return
        }

        if (!cleanUrl.startsWith("http://", ignoreCase = true) && !cleanUrl.startsWith("https://", ignoreCase = true)) {
            val errMsg = "رابط التحميل غير صالح (يجب أن يبدأ بـ https:// أو يكون ملفاً محلياً)"
            Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
            onStatus(errMsg)
            return
        }

        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            if (downloadManager != null) {
                val downloadUri = Uri.parse(cleanUrl)
                val request = DownloadManager.Request(downloadUri).apply {
                    setTitle("$themeTitle ($versionStr)")
                    setDescription("جارٍ تنزيل ملف الثيم الفعلي إلى جهازك...")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    setAllowedOverMetered(true)
                    setAllowedOverRoaming(true)
                }

                downloadManager.enqueue(request)
                val msg = "بدأ تنزيل ملف الثيم في مجلد التنزيلات (Downloads): $fileName"
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                onStatus(msg)
                return
            }
        } catch (e: Exception) {
            // If DownloadManager enqueue encounters restricted environment or URI permission, fallback to direct streaming
        }

        // Fallback Coroutine HTTP Downloader directly to Downloads directory
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "جارٍ تنزيل الملف الفعلي من الرابط الخارجي...", Toast.LENGTH_SHORT).show()
                }

                val connection = (URL(cleanUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 30000
                    instanceFollowRedirects = true
                    setRequestProperty("User-Agent", "S18ThemeDownloader/1.0")
                    connect()
                }

                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val targetFile = File(downloadsDir, fileName)

                connection.inputStream.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }

                withContext(Dispatchers.Main) {
                    val successMsg = "تم اكتمال تنزيل الملف وحفظه في: Downloads/$fileName"
                    Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()
                    onStatus(successMsg)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val err = "تعذر تنزيل الملف مباشرة: ${e.localizedMessage ?: "فشل الاتصال"}"
                    Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                    onStatus(err)
                }
            }
        }
    }
}
