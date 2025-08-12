package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.pixelz360.docsign.imagetopdf.creator.R
import java.io.File
import java.util.Date

class PdfAdapter(private var pdfFiles: List<PdfFile>, private val onClick: (PdfFile) -> Unit
) : RecyclerView.Adapter<PdfAdapter.PdfViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_file, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.bind(pdfFiles[position], onClick)
    }

    override fun getItemCount() = pdfFiles.size

    fun updateFiles(newFiles: List<PdfFile>) {
        pdfFiles = newFiles
        notifyDataSetChanged()
    }

    class PdfViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(pdfFile: PdfFile, onClick: (PdfFile) -> Unit) {
//            itemView.setOnClickListener { onClick(pdfFile) }

            itemView.setOnClickListener {
                val file = File(pdfFile.filePath)
                val fileUri = FileProvider.getUriForFile(itemView.context, "com.pixelz360.docsign.imagetopdf.creator", file)

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(fileUri, "application/pdf")
                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                itemView.context.startActivity(intent)
            }

            val file: File = File(pdfFile.filePath)
            val lastModDate: Date = Date(file.lastModified())

            val size: String = formatSize(file.length()) // this will give you the size of the file


            itemView.findViewById<TextView>(R.id.pdfFileName).text = pdfFile.filePath
            itemView.findViewById<TextView>(R.id.date).text = lastModDate.toString()
            itemView.findViewById<TextView>(R.id.sizeTv).text = size
            itemView.findViewById<ShapeableImageView>(R.id.thumbnailIv).setImageBitmap(pdfToBitmap(file))
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
    }
}