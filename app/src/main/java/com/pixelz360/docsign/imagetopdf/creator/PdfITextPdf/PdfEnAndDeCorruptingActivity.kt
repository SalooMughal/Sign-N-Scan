package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.itextpdf.text.pdf.PdfWriter
import com.pixelz360.docsign.imagetopdf.creator.Constant
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfEnAndDeCryptingBinding
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PdfEnAndDeCorruptingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfEnAndDeCryptingBinding
    private var selectedFilePath: String? = null
    private lateinit var pdfFileViewModel: PdfFileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfEnAndDeCryptingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get color from resources
        val statusBarColor = ContextCompat.getColor(this@PdfEnAndDeCorruptingActivity, R.color.white)
        // Change status bar color
        com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils.changeStatusBarColor(statusBarColor, this@PdfEnAndDeCorruptingActivity)


        pdfFileViewModel = ViewModelProvider(this)[PdfFileViewModel::class.java]

        binding.selectPdfButton.setOnClickListener { selectPdfFile() }
        binding.encryptPdfBtn.setOnClickListener { encryptPdf() }
        binding.decryptPdfBtn.setOnClickListener { decryptPdf() }
        binding.homeBtn.setOnClickListener {

            val intent = Intent(this@PdfEnAndDeCorruptingActivity, HomeActivity::class.java)
            startActivity(intent)

        }


        binding.backButton.setOnClickListener {
            onBackPressed()
            finish()
        }


    }



    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.data?.let { uri ->
            selectedFilePath = getFilePathFromUri(uri)
            Toast.makeText(this, "File Selected: $selectedFilePath", Toast.LENGTH_SHORT).show()

            binding.filePathTextView.text = selectedFilePath

        }
    }

    private fun getFilePathFromUri(uri: Uri): String {
        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        } ?: "unknown"
        val file = File(filesDir, fileName)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file.absolutePath
    }

    private fun encryptPdf() {
        val password = binding.passwordEditText.text.toString()
        if (selectedFilePath == null || password.isEmpty()) {
            Toast.makeText(this, "Please select a PDF file and enter a password", Toast.LENGTH_SHORT).show()
            return
        }

        try {
//            val outputFile = File(filesDir, "encrypted_${System.currentTimeMillis()}.pdf")

            val root = File(getExternalFilesDir(null), Constant.PDF_FOLDER)
            if (!root.exists()) root.mkdirs()


//            val outputFileName = "encrypt_${System.currentTimeMillis()}.pdf"

            val timestamp1 = System.currentTimeMillis()
            val seconds = (timestamp1 / 1000) % 60  // Extract seconds from timestamp
            val mainFileName = "Untitled Encrypt Doc $seconds"

            val outputFileName = "${mainFileName}.pdf"

            val outputFile = File(root, outputFileName)

            Log.d("PdfViewActivity11", "filePath encryptPdf  $selectedFilePath")
            Log.d("PdfViewActivity11", "outputFile encryptPdf  ${outputFile.absolutePath}")

//            UtilPdf.compressPDF(selectedFilePath, outputFile.toString(), 100 - 5)

            val reader = PdfReader(selectedFilePath)
            val stamper = PdfStamper(reader, FileOutputStream(outputFile))
            stamper.setEncryption(
                password.toByteArray(),
                null,
                PdfWriter.ALLOW_PRINTING or PdfWriter.ALLOW_COPY,
                PdfWriter.ENCRYPTION_AES_128)
            stamper.close()
            reader.close()

            saveToDatabase(outputFile,outputFileName,"Protect doc")
            Toast.makeText(this, "PDF encrypted and saved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@PdfEnAndDeCorruptingActivity, HomeActivity::class.java)
            intent.putExtra("ScannerSide","PreviewSide")
            startActivity(intent)
            finish()


//            val intent = Intent(this@PdfEnAndDeCryptingActivity, Prview_Screen::class.java)
//            intent.putExtra("pdffilePath", outputFile.absolutePath)
//            intent.putExtra("fileName", outputFileName)
//            startActivity(intent)
//            finish()






//            val intent = Intent(applicationContext, ActivityDigiSign::class.java)
//            intent.putExtra("ActivityAction", "Preview")
//            intent.putExtra("preview_file_path", outputFile.absolutePath)
//            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Encryption failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decryptPdf() {
        val password = binding.passwordEditText.text.toString()
        if (selectedFilePath == null || password.isEmpty()) {
            Toast.makeText(this, "Please select a PDF file and enter the password", Toast.LENGTH_SHORT).show()
            return
        }

        try {
//            val outputFile = File(filesDir, "decrypted_${System.currentTimeMillis()}.pdf")

            val root = File(getExternalFilesDir(null), Constant.PDF_FOLDER)
            if (!root.exists()) root.mkdirs()

            val outputFileName = "decrypt_${System.currentTimeMillis()}.pdf"
            val outputFile = File(root, outputFileName)


//        Intent intent = new Intent(requireActivity(), PdfViewActivity.class);
//        intent.putExtra("pdfUri", "" + Uri.fromFile(new File(filePath)));
//        startActivity(intent);
            Log.d("PdfViewActivity11", "filePath decryptPdf  $selectedFilePath")


            val reader = PdfReader(selectedFilePath, password.toByteArray())
            val stamper = PdfStamper(reader, FileOutputStream(outputFile))
            stamper.close()
            reader.close()

            saveToDatabase(outputFile, outputFileName, "Un Protect doc")
            Toast.makeText(this, "PDF decrypted and saved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@PdfEnAndDeCorruptingActivity, Prview_Screen::class.java)
            intent.putExtra("pdffilePath", outputFile.absolutePath)
            intent.putExtra("fileName", outputFileName)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Decryption failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToDatabase(outputPath: File, outputFileName: String, tag: String) {
        val fileDate: File = File(outputPath.absolutePath)
        val fileSizeBytes = outputPath.length()
//            val timestamp: Long = file.lastModified()
//                    val directory = File(outputPath)
        var timestamp = outputPath.lastModified()


        val pdfFile = PdfFile(
            outputPath.absolutePath,
            outputFileName,
            tag,
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
            AccountsOrGuesHelper.checkAccountOrNot(this@PdfEnAndDeCorruptingActivity),
            false,
            "pdf"
        )


        pdfFileViewModel!!.insertPdfFile(pdfFile)
    }
}
