package com.dailyshayari

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
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


fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
    val displayName = "shayari_${System.currentTimeMillis()}.png"
    val mimeType = "image/png"

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "DailyShayari")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        try {
            resolver.openOutputStream(it)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("SAVE_DEBUG", "Failed to save bitmap", e)
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    } ?: run {
        Log.e("SAVE_DEBUG", "Failed to create new MediaStore record.")
        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
    }
}
