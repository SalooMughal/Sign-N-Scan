package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfAddingMetadataBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PdfAddingMetadataActivity : AppCompatActivity() {

    private lateinit var pdfDao: PdfFileDao
    private lateinit var pdfAdapter: PdfAdapter
    private var selectedFilePath: String? = null
    private lateinit var binding: ActivityPdfAddingMetadataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddingMetadataBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // Initialize Python
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//
//        val db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java, "pdf_database"
//        ).build()
//
//        pdfDao = db.pdfFileDao()
//
//        // Initialize RecyclerView
//        pdfAdapter = PdfAdapter(emptyList()) { pdfFile ->
//            openPdf(this, pdfFile.filePath)
//        }
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.adapter = pdfAdapter
//
//        loadPdfFiles()  // Load existing files from the database
//
//        binding.selectPdfButton.setOnClickListener {
//            selectPdfFile()
//        }
//
//        binding.addMetadataBtn.setOnClickListener {
//            addMetadataToPdf()
//        }
//    }
//
//    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
//        uri?.let {
//            selectedFilePath = getFilePathFromUri(uri)
//            Toast.makeText(this, "File Selected: $selectedFilePath", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun selectPdfFile() {
//        pdfPickerLauncher.launch(arrayOf("application/pdf"))
//    }
//
//    private fun addMetadataToPdf() {
//        if (selectedFilePath == null) {
//            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Retrieve metadata from input fields
//        val title = binding.titleEditText.text.toString()
//        val author = binding.authorEditText.text.toString()
//        val subject = binding.subjectEditText.text.toString()
//
//        val py = Python.getInstance()
//        val pyObject = py.getModule("pdf_metadata_adder")
//        val outputFileName = "metadata_${System.currentTimeMillis()}.pdf"
//        val outputPath = File(filesDir, outputFileName).absolutePath
//
//        val result = pyObject.callAttr("add_metadata", selectedFilePath, outputPath, title, author, subject)
//
//        if (result != null && File(outputPath).exists()) {
//            saveToDatabase(outputPath)
//            Toast.makeText(this, "Metadata added and PDF saved", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Failed to add metadata", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun saveToDatabase(filePath: String) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            pdfDao.insert(PdfFile(filePath = filePath))
//            loadPdfFiles()
//        }
//    }
//
//    private fun loadPdfFiles() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val files = pdfDao.getAllFiles()
//            runOnUiThread {
//                pdfAdapter.updateFiles(files)
//            }
//        }
//    }
//
//    @SuppressLint("Range")
//    private fun getFilePathFromUri(uri: Uri): String {
//        var fileName = "unknown"
//        if (uri.scheme == "content") {
//            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
//                if (cursor.moveToFirst()) {
//                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                }
//            }
//        }
//        val file = File(filesDir, fileName)
//        contentResolver.openInputStream(uri)?.use { inputStream ->
//            FileOutputStream(file).use { outputStream ->
//                val buffer = ByteArray(1024)
//                var length: Int
//                while (inputStream.read(buffer).also { length = it } > 0) {
//                    outputStream.write(buffer, 0, length)
//                }
//            }
//        }
//        return file.absolutePath
//    }
//
//    private fun openPdf(context: Context, filePath: String) {
//        val file = File(filePath)
//        val uri = Uri.fromFile(file)
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            setDataAndType(uri, "application/pdf")
//            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        }
//        context.startActivity(intent)
    }
}
