package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.pixelz360.docsign.imagetopdf.creator.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ExtractTextPdfFileActivity : AppCompatActivity() {
    private lateinit var selectedPdfPath: TextView
    private lateinit var extractedTextView: TextView
    private lateinit var copyButton: Button
    private lateinit var shareButton: Button
    private var selectedPdfUri: Uri? = null
    private var extractedText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extract_text)

        selectedPdfPath = findViewById(R.id.selectedPdfPath)
        extractedTextView = findViewById(R.id.extractedTextView)
        copyButton = findViewById(R.id.copyButton)
        shareButton = findViewById(R.id.shareButton)
        val selectPdfButton: Button = findViewById(R.id.selectPdfButton)
        val extractButton: Button = findViewById(R.id.extractButton)

        selectPdfButton.setOnClickListener { selectPdfFile() }

        extractButton.setOnClickListener {
            if (selectedPdfUri != null) {
                extractTextFromPdf(selectedPdfUri!!)
            } else {
                Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
            }
        }

        copyButton.setOnClickListener {
            copyTextToClipboard(extractedText)
        }

        shareButton.setOnClickListener {
            shareTextAsFile(extractedText)
        }
    }

    private fun selectPdfFile() {
        filePickerLauncher.launch("application/pdf")
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedPdfUri = uri
                selectedPdfPath.text = "Selected PDF: ${uri.path}"
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun extractTextFromPdf(pdfUri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(pdfUri)
            if (inputStream == null) {
                Toast.makeText(this, "Failed to open PDF file", Toast.LENGTH_SHORT).show()
                return
            }

            // Load the PDF document
            val pdfReader = PdfReader(inputStream)
            val pdfDocument = PdfDocument(pdfReader)

            // Extract text from the PDF
            val textContent = StringBuilder()
            for (i in 1..pdfDocument.numberOfPages) {
                val page = pdfDocument.getPage(i)
                val extractedText = PdfTextExtractor.getTextFromPage(page)
                textContent.append(extractedText).append("\n\n") // Preserve paragraph formatting
            }

            pdfReader.close()

            // Display extracted text in TextView
            extractedText = textContent.toString()
            extractedTextView.text = extractedText
            extractedTextView.movementMethod = ScrollingMovementMethod() // Make TextView scrollable

            Toast.makeText(this, "Text extracted successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error during extraction: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Extracted Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun shareTextAsFile(text: String) {
        try {
            // Create a file to save the text
            val outputDir = File(getExternalFilesDir(null), "ExtractedText")
            if (!outputDir.exists()) outputDir.mkdirs()

            val outputFile = File(outputDir, "extracted_text_${System.currentTimeMillis()}.txt")
            FileOutputStream(outputFile).use { fos ->
                fos.write(text.toByteArray())
            }

            // Get the URI of the file
            val fileUri: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                outputFile
            )

            // Create a share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Share file via"))
        } catch (e: Exception) {
            Toast.makeText(this, "Error while sharing: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}


//private fun openTextFile(file: File) {
//    try {
//        val uri = FileProvider.getUriForFile(
//            this,
//            "${applicationContext.packageName}.provider",
//            file
//        )
//
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            setDataAndType(uri, "text/plain")
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//
//        startActivity(Intent.createChooser(intent, "Open with"))
//    } catch (e: Exception) {
//        Toast.makeText(this, "No app found to open the file", Toast.LENGTH_SHORT).show()
//        e.printStackTrace()
//    }
//}















//        selectedPdfPath = findViewById(R.id.selectedPdfPath)
//        val selectPdfButton: Button = findViewById(R.id.selectPdfButton)
//        val convertButton: Button = findViewById(R.id.convertButton)
//
//        selectPdfButton.setOnClickListener { selectPdfFile() }
//        convertButton.setOnClickListener {
//            if (selectedPdfUri != null) {
//                convertPdfToWord(selectedPdfUri!!)
//            } else {
//                Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun selectPdfFile() {
//        filePickerLauncher.launch("application/pdf")
//    }
//
//    private val filePickerLauncher =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            if (uri != null) {
//                selectedPdfUri = uri
//                selectedPdfPath.text = "Selected PDF: ${uri.path}"
//            } else {
//                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    private fun convertPdfToWord(pdfUri: Uri) {
//        try {
//            val inputStream: InputStream? = contentResolver.openInputStream(pdfUri)
//            if (inputStream == null) {
//                Toast.makeText(this, "Failed to open PDF file", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            // Load the PDF
//            val pdfReader = PdfReader(inputStream)
//            val pdfDocument = PdfDocument(pdfReader)
//
//            // Create a Word document
//            val wordDocument = XWPFDocument()
//            val outputDir = File(getExternalFilesDir(null), "ConvertedFiles")
//            if (!outputDir.exists()) outputDir.mkdirs()
//
//            val outputFile = File(outputDir, "converted_${System.currentTimeMillis()}.docx")
//
//            // Process PDF pages
//            for (i in 1..pdfDocument.numberOfPages) {
//                val page = pdfDocument.getPage(i)
//                val extractedText = PdfTextExtractor.getTextFromPage(page)
//
//                // Attempt to retain formatting
//                val paragraphs = extractParagraphsWithFormatting(page)
//
//                for (paragraph in paragraphs) {
//                    val xwpfParagraph = wordDocument.createParagraph()
//                    val run = xwpfParagraph.createRun()
//
//                    run.setText(paragraph.text)
//
//                    // Adjust formatting
//                    if (paragraph.isHeading) {
//                        run.isBold = true
//                        run.fontSize = 16
//                    } else {
//                        run.fontSize = 12
//                    }
//                }
//            }
//
//            pdfReader.close()
//
//            // Save the Word document
//            wordDocument.write(FileOutputStream(outputFile))
//            wordDocument.close()
//
//            Toast.makeText(this, "File saved: ${outputFile.absolutePath}", Toast.LENGTH_LONG).show()
//            openDocFile(outputFile)
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error during conversion: ${e.message}", Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//        }
//    }
//
//    private fun openDocFile(file: File) {
//        try {
//            val uri = FileProvider.getUriForFile(
//                this,
//                "${applicationContext.packageName}.provider",
//                file
//            )
//
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                setDataAndType(
//                    uri,
//                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
//                )
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//
//            startActivity(Intent.createChooser(intent, "Open with"))
//        } catch (e: Exception) {
//            Toast.makeText(this, "No app found to open the file", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * Mock method to simulate extracting formatted paragraphs.
//     */
//    private fun extractParagraphsWithFormatting(page: com.itextpdf.kernel.pdf.PdfPage): List<FormattedParagraph> {
//        val text = PdfTextExtractor.getTextFromPage(page)
//        val paragraphs = text.split("\n")
//        val formattedParagraphs = mutableListOf<FormattedParagraph>()
//
//        paragraphs.forEach {
//            if (it.matches(Regex("^[A-Z].*"))) { // Assume a simple heading detection
//                formattedParagraphs.add(FormattedParagraph(it, true))
//            } else {
//                formattedParagraphs.add(FormattedParagraph(it, false))
//            }
//        }
//        return formattedParagraphs
//    }
//
//    data class FormattedParagraph(val text: String, val isHeading: Boolean)
//}




































//import android.Manifest
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import com.itextpdf.text.pdf.PdfReader
//import com.itextpdf.text.pdf.parser.PdfTextExtractor
//import com.pixelz360.docsign.imagetopdf.creator.Constant
//import com.pixelz360.docsign.imagetopdf.creator.R
//import org.apache.poi.xwpf.usermodel.XWPFDocument
//import org.apache.poi.xwpf.usermodel.XWPFParagraph
//import java.io.File
//import java.io.FileOutputStream
//import java.io.InputStream
//
//class PdfToWordActivity : AppCompatActivity() {
//
//    private lateinit var selectedPdfPath: TextView
//    private var selectedPdfUri: Uri? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main4)
//
//        selectedPdfPath = findViewById(R.id.selectedPdfPath)
//        val selectPdfButton: Button = findViewById(R.id.selectPdfButton)
//        val convertButton: Button = findViewById(R.id.convertButton)
//
//        selectPdfButton.setOnClickListener {
//            selectPdfFile()
//        }
//
//        convertButton.setOnClickListener {
//
//            convertPdfToWord()
//
////            if (checkPermissions()) {
////                convertPdfToWord()
////            } else {
////                requestPermissions()
////            }
//        }
//    }
//
//    private fun selectPdfFile() {
//        filePickerLauncher.launch("application/pdf")
//    }
//
//    private val filePickerLauncher =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            if (uri != null) {
//                selectedPdfUri = uri
//                selectedPdfPath.text = "Selected PDF: ${uri.path}"
//            } else {
//                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    private fun checkPermissions(): Boolean {
//        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        return readPermission == android.content.pm.PackageManager.PERMISSION_GRANTED &&
//                writePermission == android.content.pm.PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
//            100
//        )
//    }
//
//    private fun convertPdfToWord() {
//        if (selectedPdfUri == null) {
//            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        try {
//            // Step 1: Extract structured text from the PDF
//            val inputStream: InputStream? = contentResolver.openInputStream(selectedPdfUri!!)
//            val reader = PdfReader(inputStream)
//            val numberOfPages = reader.numberOfPages
//
//            val pagesContent = mutableListOf<String>()
//            for (i in 1..numberOfPages) {
//                val pageContent = PdfTextExtractor.getTextFromPage(reader, i)
//                pagesContent.add(pageContent.trim())
//            }
//            reader.close()
//
//            // Step 2: Save extracted text to a Word file with proper formatting
//            saveTextAsWordWithFormatting(pagesContent)
//
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//        }
//    }
//
//    private fun saveTextAsWordWithFormatting(pagesContent: List<String>) {
//        try {
//            val root = File(getExternalFilesDir(null), Constant.PDF_FOLDER)
//            if (!root.exists()) root.mkdirs()
//
//            val outputFileName = "formatted_${System.currentTimeMillis()}.docx"
//            val outputFile = File(root, outputFileName)
//
//            XWPFDocument().use { document ->
//                for (pageContent in pagesContent) {
//                    val lines = pageContent.split("\n")
//                    for (line in lines) {
//                        val paragraph: XWPFParagraph = document.createParagraph()
//                        val run = paragraph.createRun()
//
//                        // Example: Treat lines with more than 50 characters as normal text and shorter ones as headings
//                        if (line.length < 50) {
//                            paragraph.style = "Heading1"
//                            run.isBold = true
//                        } else {
//                            paragraph.style = "Normal"
//                        }
//                        run.setText(line)
//                    }
//                    // Add a page break after each page
//                    document.createParagraph().isPageBreak = true
//                }
//
//                FileOutputStream(outputFile).use { out ->
//                    document.write(out)
//                }
//            }
//
//            Toast.makeText(this, "File saved: ${outputFile.absolutePath}", Toast.LENGTH_LONG).show()
//            openDocFile(outputFile)
//
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//        }
//    }
//
//
//    private fun openDocFile(file: File) {
//        try {
//            val uri = FileProvider.getUriForFile(
//                this,
//                "${applicationContext.packageName}.provider",
//                file
//            )
//
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//
//            startActivity(Intent.createChooser(intent, "Open with"))
//        } catch (e: Exception) {
//            Toast.makeText(this, "No app found to open the file", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//        }
//}
