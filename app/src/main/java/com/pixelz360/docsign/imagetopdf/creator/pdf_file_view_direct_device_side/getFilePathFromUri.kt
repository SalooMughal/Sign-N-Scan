package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream


fun getFilePathFromUri(context: Context, uri: Uri): String {
    // Handle content URI
    if (uri.scheme == "content") {
        val fileName = getFileName(context, uri) ?: "temp_file"
        val tempFile = File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile.path
    }

    // Handle file URI
    if (uri.scheme == "file") {
        return uri.path ?: throw IllegalArgumentException("Invalid file URI")
    }

    throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
}

@SuppressLint("Range")
private fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    }
    return name
}
