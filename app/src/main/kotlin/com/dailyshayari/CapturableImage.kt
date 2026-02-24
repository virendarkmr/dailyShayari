package com.dailyshayari

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun shareBitmap(context: Context, bitmap: Bitmap) {
    Log.d("SHARE_DEBUG", "Bitmap width: ${bitmap.width}, height: ${bitmap.height}")

    // Use a subdirectory for clarity, though cache root is also fine
    val cachePath = File(context.cacheDir, "images/")
    cachePath.mkdirs()
    val file = File(cachePath, "shayari_${System.currentTimeMillis()}.png")

    try {
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    } catch (e: Exception) {
        Log.e("SHARE_DEBUG", "Failed to write bitmap to file", e)
        return
    }

    Log.d("SHARE_DEBUG", "File exists: ${file.exists()}, size: ${file.length()}")

    if (!file.exists() || file.length() == 0L) {
        Log.e("SHARE_DEBUG", "File not created or is empty. Aborting share.")
        return
    }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share Shayari"))
}
