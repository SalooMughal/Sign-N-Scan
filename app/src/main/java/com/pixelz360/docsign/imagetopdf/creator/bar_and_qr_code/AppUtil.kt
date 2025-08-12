package com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixelz360.docsign.imagetopdf.creator.editModule.AllEditImagesActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

fun copyTextToClipboard(context: Context, message: String, label: String = "copy") {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(
        label,
        message
    )
    Toast.makeText(context, "$message is copied", Toast.LENGTH_SHORT).show()
    clipboardManager.setPrimaryClip(clipData)
}

fun shareImageUri(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        type = "image/png"
    }
    context.startActivity(intent)
}

fun saveImage(context: Context, image: Bitmap): Uri? {
    val imagesFolder = File(context.cacheDir, "images")
    var uri: Uri? = null
    try {
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "shared_qr_code.png")
        val stream = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        uri = FileProvider.getUriForFile(context, "com.pixelz360.docsign.imagetopdf.creator.provider",file)
    } catch (e: IOException) {
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
    }
    return uri
}


fun firebaseAnalytics(context: Context, activityName: String) {


    // Log a predefined event
    val analytics = FirebaseAnalytics.getInstance(context)

    val bundle = Bundle()
    bundle.putString("activity_name", activityName)
    analytics.logEvent("activity_created", bundle)

// Using predefined Firebase Analytics events

// Using predefined Firebase Analytics events
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, activityName)
    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
    analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)



}
