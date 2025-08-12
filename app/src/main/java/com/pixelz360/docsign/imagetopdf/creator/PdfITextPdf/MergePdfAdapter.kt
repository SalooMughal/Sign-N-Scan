package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.pixelz360.docsign.imagetopdf.creator.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date


class MergePdfAdapter(
    private val pdfFiles: MutableList<Uri>,
    var context: Context
) : RecyclerView.Adapter<MergePdfAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = pdfFiles[position]

        val file: File = getFile(context, uri)


        var lastModDate:  Date= Date(file.lastModified())
        var simpleDateFormat: SimpleDateFormat
        simpleDateFormat = SimpleDateFormat("dd.LLLL HH:mm:ss aaa")


        val size: String = formatSize(file.length()) // this will give you the size of the file

        holder.pdfFileName.text = file.name
        holder.date.text = simpleDateFormat.format(lastModDate)
        holder.sizeTv.text = size

        holder.thumbnailIv.setImageBitmap(pdfToBitmap(file))



    }

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename = File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(ins!!, destinationFilename)
            }
        } catch (ex: java.lang.Exception) {
            Log.e("Save File", ex.message.toString())
            ex.printStackTrace()
        }
        return destinationFilename
    }

    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while ((ins.read(buffer).also { length = it }) > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: java.lang.Exception) {
            Log.e("Save File", ex.message.toString())
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor = checkNotNull(
            context.contentResolver.query(uri, null, null, null, null)
        )
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun formatSize(size: Long): String {
        var size = size
        var suffix: String? = null

        if (size >= 1024) {
            suffix = " Bytes"
            size /= 1024
            if (size >= 1024) {
                suffix = " MB"
                size /= 1024
            }
        }
        val resultBuffer = StringBuilder(size.toString())

        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }

    private fun pdfToBitmap(pdfFile: File): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val renderer =
                PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
            val pageCount = renderer.pageCount
            if (pageCount > 0) {
                val page = renderer.openPage(0)
                bitmap = Bitmap.createBitmap((page.width), (page.height), Bitmap.Config.ARGB_8888)

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                renderer.close()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return bitmap
    }

    override fun getItemCount(): Int = pdfFiles.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pdfFileName: TextView = view.findViewById(R.id.pdfFileName)
        val sizeTv: TextView = view.findViewById(R.id.sizeTv)
        val date: TextView = view.findViewById(R.id.date)
        val thumbnailIv: ShapeableImageView = view.findViewById(R.id.thumbnailIv)
    }
}

