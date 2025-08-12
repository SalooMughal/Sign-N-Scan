package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfSplitterBinding


class PdfSplitterActivity : AppCompatActivity() {

//    private lateinit var pdfDao: PdfFileDao
//    private var selectedFilePath: String? = null
//    private lateinit var pdfAdapter: PdfAdapter
    lateinit var binding: ActivityPdfSplitterBinding
//    val PICK_PDF_FILE = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfSplitterBinding.inflate(layoutInflater)
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
//        pdfAdapter = PdfAdapter(emptyList()) { pdfFile ->
//            openPdf(this, pdfFile.filePath)
//        }
//        binding.recyclerView.apply {
//            layoutManager = LinearLayoutManager(this@PdfSplitterActivity)
//            adapter = pdfAdapter
//        }
//
//        setupSplitOptionsSpinner()
//
//        binding.selectPdfButton.setOnClickListener { selectPdfFile() }
//        binding.splitPdfBtn.setOnClickListener { splitPdfFile() }
//
//        loadPdfFiles()
    }

//    private fun setupSplitOptionsSpinner() {
//        val options = resources.getStringArray(R.array.split_options)
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
//        binding.splitOptionsSpinner.adapter = adapter
//    }
//
//    private fun splitPdfFile() {
//        if (selectedFilePath == null) {
//            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val splitOption = binding.splitOptionsSpinner.selectedItem.toString()
//        val startPage = binding.startPageEditText.text.toString().toIntOrNull() ?: 1
//        val endPage = binding.endPageEditText.text.toString().toIntOrNull() ?: -1
//        val pagesPerSplit = binding.pagesPerSplitEditText.text.toString().toIntOrNull() ?: 1
//
//        val py = Python.getInstance()
//        val pyObject = py.getModule("pdf_splitter")
//        val outputFolder = File(filesDir, "split_pdfs")
//        if (!outputFolder.exists()) outputFolder.mkdirs()
//
//        val result = when (splitOption) {
//            "Split by Range" -> pyObject.callAttr("split_by_range", selectedFilePath, outputFolder.absolutePath, startPage, endPage)
//            "Equal Page Ranges" -> pyObject.callAttr("split_into_equal_ranges", selectedFilePath, outputFolder.absolutePath, pagesPerSplit)
//            "Delete Pages by Range" -> pyObject.callAttr("delete_pages_by_range", selectedFilePath, outputFolder.absolutePath, startPage, endPage)
//            "Extract All Pages" -> pyObject.callAttr("extract_all_pages", selectedFilePath, outputFolder.absolutePath)
//            else -> null
//        }
//
//        if (result != null) {
//            val outputPaths = result.asList().map { it.toString() }
//
//            lifecycleScope.launch(Dispatchers.IO) {
//                outputPaths.forEach { path ->
//                    pdfDao.insert(PdfFile(filePath = path))
//                }
//                loadPdfFiles()
//            }
//
//            Toast.makeText(this, "PDF split successfully and saved", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "PDF splitting failed", Toast.LENGTH_SHORT).show()
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
//    private fun selectPdfFile() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/pdf"
//        }
//        startActivityForResult(intent, PICK_PDF_FILE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
//            data?.data?.let { uri ->
//                selectedFilePath = getFilePathFromUri(uri)
//                Toast.makeText(this, "File Selected: $selectedFilePath", Toast.LENGTH_SHORT).show()
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

