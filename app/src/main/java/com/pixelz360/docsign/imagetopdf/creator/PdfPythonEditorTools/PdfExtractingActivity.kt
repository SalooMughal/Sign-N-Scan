package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfExtractingBinding


class PdfExtractingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfExtractingBinding
//    private var selectedFilePath: String? = null
//    private lateinit var extractedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfExtractingBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        extractedTextView = findViewById(R.id.extractedTextView)
//
//        // Initialize Python
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//
//        // Select PDF button click
//        binding.selectPdfButton.setOnClickListener {
//            selectPdfFile()
//        }
//
//        // Extract Text button click
//        binding.extractTextBtn.setOnClickListener {
//            extractTextFromPdf()
//        }
    }

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
//    private fun extractTextFromPdf() {
//        if (selectedFilePath == null) {
//            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val py = Python.getInstance()
//        val pyObject = py.getModule("pdf_extractor")
//        val result = pyObject.callAttr("extract_text_from_pdf", selectedFilePath)
//
//        if (result != null) {
//            val extractedText = result.toString()
//            extractedTextView.text = extractedText
//            Toast.makeText(this, "Text extracted successfully", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Text extraction failed", Toast.LENGTH_SHORT).show()
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
//    companion object {
//        const val PICK_PDF_FILE = 111
//    }
}
