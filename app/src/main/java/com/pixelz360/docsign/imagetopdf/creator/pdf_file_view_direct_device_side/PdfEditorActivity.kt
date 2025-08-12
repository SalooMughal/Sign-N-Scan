package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixelz360.docsign.imagetopdf.creator.R
import java.io.File

class PdfEditActivity : AppCompatActivity() {

    private lateinit var adapter: PdfTextAdapter
    private val textDataList = mutableListOf<PdfTextData>()
    private var pdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_editor)

        val btnSelectPdf = findViewById<Button>(R.id.btnSelectPdf)
        val btnSavePdf = findViewById<Button>(R.id.btnSavePdf)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PdfTextAdapter(textDataList)
        recyclerView.adapter = adapter

        btnSelectPdf.setOnClickListener {
            selectPdfFile()
        }

        btnSavePdf.setOnClickListener {
            pdfUri?.let { uri ->
                saveUpdatedPdf(uri, adapter.getUpdatedTextData())
            } ?: Toast.makeText(this, "No PDF selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            pdfUri = data?.data
            pdfUri?.let { uri ->
                extractPdf(uri)
            }
        }
    }

    private fun extractPdf(uri: Uri) {
        textDataList.clear()
        textDataList.addAll(extractPdfTextWithFormatting(this@PdfEditActivity,uri))
        adapter.notifyDataSetChanged()
    }

    private fun saveUpdatedPdf(uri: Uri, updatedTextData: List<PdfTextData>) {
        val outputFilePath = File(filesDir, "updated_pdf_exact_format.pdf").path
        updatePdfText(this@PdfEditActivity,uri, updatedTextData, outputFilePath)
        Toast.makeText(this, "PDF updated and saved to: $outputFilePath", Toast.LENGTH_LONG).show()
    }
}
