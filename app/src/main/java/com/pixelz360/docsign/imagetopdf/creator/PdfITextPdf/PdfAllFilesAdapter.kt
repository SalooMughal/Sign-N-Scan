package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.pixelz360.docsign.imagetopdf.creator.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class PdfAllFilesAdapter(
    private val pdfFiles: List<Uri>,
    private val onSelectionChanged: (selectedFiles: List<File>) -> Unit
) : RecyclerView.Adapter<PdfAllFilesAdapter.ViewHolder>() {

    private val selectedFiles = mutableSetOf<File>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_file_all, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = pdfFiles[position]

        var file = File(uri.toString())

        var lastModDate:  Date= Date(file.lastModified())
        var simpleDateFormat: SimpleDateFormat
        simpleDateFormat = SimpleDateFormat("dd.LLLL HH:mm:ss aaa")


        val size: String = formatSize(file.length()) // this will give you the size of the file

        holder.pdfFileName.text = file.name
        holder.date.text = simpleDateFormat.format(lastModDate)
        holder.sizeTv.text = size

        Log.d("checkdate",simpleDateFormat.format(lastModDate))


        holder.pdfCheckBox.isChecked = selectedFiles.contains(file)

//        holder.thumbnailIv.setImageBitmap(pdfToBitmap(file))

        // Handle CheckBox clicks
        holder.pdfCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedFiles.add(file)
            } else {
                selectedFiles.remove(file)
            }
            onSelectionChanged(selectedFiles.toList()) // Notify the parent activity
        }

        // Handle entire row click to toggle selection
        holder.itemView.setOnClickListener {
            holder.pdfCheckBox.isChecked = !holder.pdfCheckBox.isChecked
        }
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
        val pdfCheckBox: CheckBox = view.findViewById(R.id.pdfCheckBox)
        val pdfFileName: TextView = view.findViewById(R.id.pdfFileName)
        val sizeTv: TextView = view.findViewById(R.id.sizeTv)
        val date: TextView = view.findViewById(R.id.date)
        val thumbnailIv: ShapeableImageView = view.findViewById(R.id.thumbnailIv)
    }
}











//class PdfAllFilesAdapter(
//    private val pdfFiles: List<File>,
//    private val onSelectionChanged: (File, Boolean) -> Unit
//) : RecyclerView.Adapter<PdfAllFilesAdapter.ViewHolder>() {
//
//    private val selectedFiles = mutableSetOf<File>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val file = pdfFiles[position]
//        holder.textView.text = file.name
//        holder.itemView.isSelected = selectedFiles.contains(file)
//
//        holder.itemView.setOnClickListener {
//            if (selectedFiles.contains(file)) {
//                selectedFiles.remove(file)
//                    onSelectionChanged(file, false)
//            } else {
//                selectedFiles.add(file)
//                onSelectionChanged(file, true)
//            }
//            notifyItemChanged(position)
//        }
//    }
//
//    override fun getItemCount(): Int = pdfFiles.size
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val textView: TextView = view.findViewById(android.R.id.text1)
//    }
//}
