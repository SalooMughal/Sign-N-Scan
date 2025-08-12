package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfEnAndDeCryptingBinding


class PdfEnAndDeCryptingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfEnAndDeCryptingBinding
//    private lateinit var pdfDao: PdfFileDao
//    private lateinit var pdfAdapter: PdfAdapter
//    private var selectedFilePath: String? = null
//    private lateinit var passwordEditText: EditText
//    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfEnAndDeCryptingBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        passwordEditText = findViewById(R.id.passwordEditText)
//        statusTextView = findViewById(R.id.statusTextView)
//
//        // Initialize Python
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//
//        // Initialize Room Database
//        val db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java, "pdf_database"
//        ).build()
//        pdfDao = db.pdfFileDao()
//
//        // Initialize RecyclerView
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        pdfAdapter = PdfAdapter(emptyList()) { pdfFile ->
//            openPdf(this, pdfFile.filePath)
//        }
//        recyclerView.adapter = pdfAdapter
//
//        loadPdfFiles()  // Load files into RecyclerView
//
//        // Set up button listeners
//        binding.selectPdfButton.setOnClickListener {
//            selectPdfFile()
//        }
//        binding.encryptPdfBtn.setOnClickListener {
//            encryptPdf()
//        }
//        binding.decryptPdfBtn.setOnClickListener {
//            decryptPdf()
//        }
    }

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
//    private fun encryptPdf() {
//        val password = passwordEditText.text.toString()
//        if (selectedFilePath == null || password.isEmpty()) {
//            Toast.makeText(this, "Please select a PDF file and enter a password", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val py = Python.getInstance()
//        val pyObject = py.getModule("pdf_encryption")
//        val outputFileName = "encrypted_${System.currentTimeMillis()}.pdf"
//        val outputPath = File(filesDir, outputFileName).absolutePath
//
//        val result = pyObject.callAttr("encrypt_pdf", selectedFilePath, outputPath, password)
//
//        if (result != null && File(outputPath).exists()) {
//            saveToDatabase(outputPath)
//            Toast.makeText(this, "PDF encrypted and saved", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Encryption failed", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun decryptPdf() {
//        val password = passwordEditText.text.toString()
//        if (selectedFilePath == null || password.isEmpty()) {
//            Toast.makeText(this, "Please select a PDF file and enter the password", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val py = Python.getInstance()
//        val pyObject = py.getModule("pdf_encryption")
//        val outputFileName = "decrypted_${System.currentTimeMillis()}.pdf"
//        val outputPath = File(filesDir, outputFileName).absolutePath
//
//        val result = pyObject.callAttr("decrypt_pdf", selectedFilePath, outputPath, password)
//
//        if (result != null && File(outputPath).exists()) {
//            saveToDatabase(outputPath)
//            Toast.makeText(this, "PDF decrypted and saved", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Decryption failed", Toast.LENGTH_SHORT).show()
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
//    }
//
//    companion object {
//        const val PICK_PDF_FILE = 111
//    }
}
