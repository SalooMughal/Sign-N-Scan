package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfMergingBinding


class PdfMergingActivity : AppCompatActivity() {

//    private lateinit var pdfDao: PdfFileDao
//    private lateinit var pdfAdapter: PdfAdapter
//    private lateinit var selectedFilePaths: MutableList<String>
    private lateinit var binding: ActivityPdfMergingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfMergingBinding.inflate(layoutInflater)
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
//        selectedFilePaths = mutableListOf()
//
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        pdfAdapter = PdfAdapter(emptyList()) { pdfFile ->
//            openPdf(this, pdfFile.filePath)
//        }
//        recyclerView.adapter = pdfAdapter
//
//        findViewById<View>(R.id.selectPdfsButton).setOnClickListener {
//            selectPdfFiles()
//        }
//
//        binding.mergePdfBtn.setOnClickListener {
//            mergePdfFiles()
//        }
    }

//    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri>? ->
//        uris?.forEach { uri ->
//            selectedFilePaths.add(getFilePathFromUri(uri))
//        }
//        Toast.makeText(this, "${uris?.size ?: 0} PDF files selected", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun selectPdfFiles() {
//        pdfPickerLauncher.launch(arrayOf("application/pdf"))
//    }
//
//    private fun mergePdfFiles() {
//        if (selectedFilePaths.isEmpty()) {
//            Toast.makeText(this, "Please select PDF files first", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val py = Python.getInstance()
//        val pyObject = py.getModule("pdf_merger")
//        val outputFileName = "merged_${System.currentTimeMillis()}.pdf"
//        val outputPath = File(filesDir, outputFileName).absolutePath
//
//        val result = pyObject.callAttr("merge_pdfs", selectedFilePaths.toTypedArray(), outputPath)
//
//
//        if (result != null && File(outputPath).exists()) {
//            lifecycleScope.launch(Dispatchers.IO) {
//                pdfDao.insert(PdfFile(filePath = outputPath))
//                loadPdfFiles()
//            }
//            Toast.makeText(this, "PDFs merged successfully and saved", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "PDF merging failed", Toast.LENGTH_SHORT).show()
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
//    }
}
