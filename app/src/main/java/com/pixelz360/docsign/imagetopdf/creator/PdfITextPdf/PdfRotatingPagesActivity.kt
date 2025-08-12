package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.itextpdf.text.pdf.PdfName
import com.itextpdf.text.pdf.PdfNumber
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.pixelz360.docsign.imagetopdf.creator.Constant
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile

import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfRotatingPagesBinding
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PdfRotatingPagesActivity : AppCompatActivity() {

    private var selectedFilePath: String? = null
    private lateinit var binding: ActivityPdfRotatingPagesBinding
    private lateinit var pdfFileViewModel: PdfFileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfRotatingPagesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        pdfFileViewModel = ViewModelProvider(this)[PdfFileViewModel::class.java]


        binding.selectPdfButton.setOnClickListener {
            selectPdfFile()
        }

        binding.rotatePdfBtn.setOnClickListener {
            rotatePdfFile()
        }
    }

    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            selectedFilePath = getFilePathFromUri(uri)
            Toast.makeText(this, "File Selected: $selectedFilePath", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectPdfFile() {
        pdfPickerLauncher.launch(arrayOf("application/pdf"))
    }

    private fun rotatePdfFile() {
        if (selectedFilePath == null) {
            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
            return
        }


        val startPage = binding.startPageEditText.text.toString().toIntOrNull() ?: 1
        val endPage = binding.endPageEditText.text.toString().toIntOrNull() ?: -1

        val rotationAngle = getSelectedRotationAngle()
        try {
            val root = File(getExternalFilesDir(null), Constant.PDF_FOLDER)
            if (!root.exists()) root.mkdirs()

            val outputFileName = "rotated_${System.currentTimeMillis()}.pdf"
            val outputFile = File(root, outputFileName)

            val reader = PdfReader(selectedFilePath)
            val stamper = PdfStamper(reader, FileOutputStream(outputFile))

            val actualEndPage = if (endPage == -1 || endPage > reader.numberOfPages) reader.numberOfPages else endPage

            // Rotate each page
            for (i in startPage..actualEndPage) {
                val pageDict = reader.getPageN(i)
                val rotation = (reader.getPageRotation(i) + rotationAngle) % 360
                pageDict.put(PdfName.ROTATE, PdfNumber(rotation))
            }

            stamper.close()
            reader.close()

            saveToDatabase(outputFile, outputFileName)
            Toast.makeText(this, "PDF rotated and saved: $outputFileName", Toast.LENGTH_SHORT).show()


            val intent = Intent(this@PdfRotatingPagesActivity, Prview_Screen::class.java)
            intent.putExtra("pdffilePath", outputFile.absolutePath)
            intent.putExtra("fileName", outputFileName)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Rotation failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveToDatabase(outputPath: File, outputFileName: String) {
        val fileDate: File = File(outputPath.absolutePath)
        val fileSizeBytes = outputPath.length()
//            val timestamp: Long = file.lastModified()
//                    val directory = File(outputPath)
        var timestamp = outputPath.lastModified()



        val pdfFile = PdfFile(
            outputPath.absolutePath,
            outputFileName,
            "Rotate",
            "#13D4FF",
            null,
            fileSizeBytes.toDouble(),
            timestamp,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            true,
            AccountsOrGuesHelper.checkAccountOrNot(this@PdfRotatingPagesActivity),
            false,
            "pdf"
        )


        pdfFileViewModel!!.insertPdfFile(pdfFile)
    }



    private fun getSelectedRotationAngle(): Int {
        val rotationGroup = findViewById<RadioGroup>(R.id.rotationOptions)
        return when (rotationGroup.checkedRadioButtonId) {
            R.id.rotate90 -> 90
            R.id.rotate180 -> 180
            R.id.rotate270 -> 270
            else -> 90 // Default to 90° if no option is selected
        }
    }


    @SuppressLint("Range")
    private fun getFilePathFromUri(uri: Uri): String {
        var fileName = "unknown"
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        val file = File(filesDir, fileName)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
        return file.absolutePath
    }
}
