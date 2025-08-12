package com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfGState
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.pixelz360.docsign.imagetopdf.creator.Constant
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfWatermarkingBinding
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt


@AndroidEntryPoint
class PdfWatermarkingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfWatermarkingBinding
    private var selectedFilePath: String? = null
    private var selectedImagePath: String? = null

    var pdfFileViewModel: PdfFileViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfWatermarkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfFileViewModel = ViewModelProvider(this)[PdfFileViewModel::class.java]


        setupRecyclerView()
        setupFontTypeSpinner()

        binding.selectPdfButton.setOnClickListener { selectPdfFile() }

        binding.watermarkTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.textWatermarkOption) {
                binding.watermarkEditText.visibility = View.VISIBLE
                binding.selectImageButton.visibility = View.GONE
            } else {
                binding.watermarkEditText.visibility = View.GONE
                binding.selectImageButton.visibility = View.VISIBLE
            }
        }

        binding.selectImageButton.setOnClickListener { selectImageFile() }
        binding.addWatermarkBtn.setOnClickListener { addWatermarkToPdf() }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFontTypeSpinner() {
        val fontSpinner: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.font_type_array,
            android.R.layout.simple_spinner_item
        )
        fontSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.fontTypeSpinner.adapter = fontSpinner
    }

    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, 1)
    }

    private fun selectImageFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.data?.let { uri ->
            when (requestCode) {
                1 -> selectedFilePath = getFilePathFromUri(uri)
                2 -> selectedImagePath = getFilePathFromUri(uri)
            }
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

    private fun addWatermarkToPdf() {
        if (selectedFilePath == null) {
            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
            return
        }

        val startPage = binding.startPageEditText.text.toString().toIntOrNull() ?: 1
        val endPage = binding.endPageEditText.text.toString().toIntOrNull() ?: -1

        val root = File(getExternalFilesDir(null), Constant.PDF_FOLDER)
        if (!root.exists()) root.mkdirs()

        val outputFileName = "watermarked_${System.currentTimeMillis()}.pdf"
        val outputFile = File(root, outputFileName)

        val verticalPosition = when (binding.verticalPositionGroup.checkedRadioButtonId) {
            R.id.positionTop -> 800f
            R.id.positionMiddle -> 400f
            R.id.positionBottom -> 100f
            else -> 400f
        }

        val horizontalPosition = when (binding.horizontalPositionGroup.checkedRadioButtonId) {
            R.id.positionLeft -> 100f
            R.id.positionCenter -> 300f
            R.id.positionRight -> 500f
            else -> 300f
        }

        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    val reader = PdfReader(selectedFilePath)
                    val stamper = PdfStamper(reader, FileOutputStream(outputFile))

                    val actualEndPage = if (endPage == -1 || endPage > reader.numberOfPages) reader.numberOfPages else endPage

                    if (binding.watermarkTypeGroup.checkedRadioButtonId == R.id.textWatermarkOption) {
                        val watermarkText = binding.watermarkEditText.text.toString()
                        val fontSize = binding.fontSizeSeekBar.progress.toFloat()
                        val transparency = binding.transparencySeekBar.progress / 100f
                        val rotation = binding.rotationSeekBar.progress.toFloat()

                        val color = when (binding.colorGroup.checkedRadioButtonId) {
                            R.id.colorBlack -> BaseColor.BLACK
                            R.id.colorWhite -> BaseColor.WHITE
                            R.id.colorRed -> BaseColor.RED
                            R.id.colorDarkBlue -> BaseColor(0, 0, 139) // RGB for dark blue
                            else -> BaseColor.BLACK
                        }

                        val fontType = when (binding.fontTypeSpinner.selectedItemPosition) {
                            0 -> "font/garogier_regular.ttf"
                            1 -> "font/gilroy_bold.ttf"
                            2 -> "font/gilroy_light.ttf"
                            3 -> "font/gilroy_medium.ttf"
                            4 -> "font/gilroy_regular.ttf"
                            else -> "font/gilroy_semi_bold.ttf"
                        }

                        val baseFont = BaseFont.createFont("assets/$fontType", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
                        val font = Font(baseFont, fontSize, Font.NORMAL, color)

                        for (i in startPage..actualEndPage) {
                            val canvas = stamper.getOverContent(i)
                            val gState = PdfGState()
                            gState.setFillOpacity(transparency) // Set fill opacity
                            canvas.setGState(gState)

                            ColumnText.showTextAligned(
                                canvas,
                                Element.ALIGN_CENTER,
                                Phrase(watermarkText, font),
                                horizontalPosition,
                                verticalPosition,
                                rotation
                            )
                        }
                    }else if (binding.watermarkTypeGroup.checkedRadioButtonId == R.id.imageWatermarkOption && selectedImagePath != null) {
                        val transparency = binding.transparencySeekBar.progress / 100f
                        val rotationDegrees = binding.rotationSeekBar.progress.toFloat() // Rotation in degrees
                        val rotationRadians = Math.toRadians(rotationDegrees.toDouble()).toFloat() // Convert to radians

                        val image = Image.getInstance(selectedImagePath)
                        image.scaleToFit(200f, 200f) // Scale the image

                        for (i in startPage..actualEndPage) {
                            val canvas = stamper.getOverContent(i)
                            val pageSize = reader.getPageSizeWithRotation(i)

                            // Get the page dimensions
                            val pageWidth = pageSize.right - pageSize.left
                            val pageHeight = pageSize.top - pageSize.bottom

                            // Horizontal Position
                            val positionX = when (binding.horizontalPositionGroup.checkedRadioButtonId) {
                                R.id.positionLeft -> pageSize.left + 50f // Left margin
                                R.id.positionCenter -> pageSize.left + (pageWidth - image.scaledWidth) / 2 // Center horizontally
                                R.id.positionRight -> pageSize.right - image.scaledWidth - 50f // Right margin
                                else -> pageSize.left + (pageWidth - image.scaledWidth) / 2 // Default to center
                            }

                            // Vertical Position
                            val positionY = when (binding.verticalPositionGroup.checkedRadioButtonId) {
                                R.id.positionTop -> pageSize.top - image.scaledHeight - 50f // Top margin
                                R.id.positionMiddle -> pageSize.bottom + (pageHeight - image.scaledHeight) / 2 // Center vertically
                                R.id.positionBottom -> pageSize.bottom + 50f // Bottom margin
                                else -> pageSize.bottom + (pageHeight - image.scaledHeight) / 2 // Default to middle
                            }

                            // Set position
                            image.setAbsolutePosition(positionX, positionY)

                            // Apply rotation
                            image.rotation = rotationDegrees.roundToInt()

                            // Apply transparency
                            val gState = PdfGState()
                            gState.setFillOpacity(transparency) // Set transparency
                            canvas.setGState(gState)

                            // Add the image to the page
                            canvas.addImage(image)
                        }
                    }






//                    else if (binding.watermarkTypeGroup.checkedRadioButtonId == R.id.imageWatermarkOption && selectedImagePath != null) {
//                        val transparency = binding.transparencySeekBar.progress / 100f
//                        val rotationDegrees = binding.rotationSeekBar.progress.toFloat() // Rotation in degrees
//                        val rotationRadians = Math.toRadians(rotationDegrees.toDouble()).toFloat() // Convert to radians
//
//                        val image = Image.getInstance(selectedImagePath)
//                        image.setAbsolutePosition(horizontalPosition, verticalPosition)
//                        image.scaleToFit(200f, 200f)
//                        image.rotation = rotationRadians.toInt() // Set rotation in radians
//
//                        for (i in startPage..actualEndPage) {
//                            val canvas = stamper.getOverContent(i)
//                            val gState = PdfGState()
//                            gState.setFillOpacity(transparency) // Set transparency
//                            canvas.setGState(gState)
//                            canvas.addImage(image)
//                        }
//                    }





                    stamper.close()
                    reader.close()
                    saveToDatabase(outputFile, outputFileName)

                    runOnUiThread {
                        Toast.makeText(this@PdfWatermarkingActivity, "Watermark added successfully: ${outputFile.absolutePath}", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@PdfWatermarkingActivity, Prview_Screen::class.java)
                        intent.putExtra("pdffilePath", outputFile.absolutePath)
                        intent.putExtra("fileName", outputFileName)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }





    private fun saveToDatabase(outputPath: File, outputFileName: String) {
        // Save the path to your database

        val fileDate: File = File(outputPath.absolutePath)
        val fileSizeBytes = outputPath.length()
//            val timestamp: Long = file.lastModified()
//                    val directory = File(outputPath)
        var timestamp = outputPath.lastModified()



        val pdfFile = PdfFile(
            outputPath.absolutePath,
            outputFileName,
            "WaterMark Doc",
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
            AccountsOrGuesHelper.checkAccountOrNot(this@PdfWatermarkingActivity),
            false,
            "pdf"
        )


        pdfFileViewModel!!.insertPdfFile(pdfFile)
    }
}























//import android.os.Bundle
//
//import androidx.appcompat.app.AppCompatActivity
//
//import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfWatermarkingBinding
//
//
//class PdfWatermarkingActivity : AppCompatActivity() {

//    private lateinit var pdfDao: PdfFileDao
//    private lateinit var pdfAdapter: PdfAdapter
//    private var selectedFilePath: String? = null
//    private var selectedImagePath: String? = null
//    private lateinit var binding: ActivityPdfWatermarkingBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPdfWatermarkingBinding.inflate(layoutInflater)
//        setContentView(binding.root)

//        // Initialize Python
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//
//        // Setup Room Database
//        val db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java, "pdf_database"
//        ).build()
//        pdfDao = db.pdfFileDao()
//
//        // Setup RecyclerView
//        pdfAdapter = PdfAdapter(emptyList()) { pdfFile ->
//            openPdf(this, pdfFile.filePath)
//        }
//        binding.recyclerView.apply {
//            layoutManager = LinearLayoutManager(this@PdfWatermarkingActivity)
//            adapter = pdfAdapter
//        }
//
//        // Set up font type Spinner
//        setupFontTypeSpinner()
//
//        // Select PDF Button
//        binding.selectPdfButton.setOnClickListener { selectPdfFile() }
//
//        // Set watermark type
//        binding.watermarkTypeGroup.setOnCheckedChangeListener { group, checkedId ->
//            if (checkedId == R.id.textWatermarkOption) {
//                binding.watermarkEditText.visibility = View.VISIBLE
//                binding.selectImageButton.visibility = View.GONE
//            } else {
//                binding.watermarkEditText.visibility = View.GONE
//                binding.selectImageButton.visibility = View.VISIBLE
//            }
//        }
//
//        // Select Image Button for Image Watermark
//        binding.selectImageButton.setOnClickListener { selectImageFile() }
//
//        // Add Watermark Button
//        binding.addWatermarkBtn.setOnClickListener { addWatermarkToPdf() }
//
//        loadPdfFiles()
//    }

//    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
//        uri?.let {
//            selectedFilePath = getFilePathFromUri(it)
//            Toast.makeText(this, "File Selected: $selectedFilePath", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun selectPdfFile() {
//        pdfPickerLauncher.launch(arrayOf("application/pdf"))
//    }
//
//
//    private fun setupFontTypeSpinner() {
//        val fontSpinner: Spinner = binding.fontTypeSpinner
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.font_type_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            fontSpinner.adapter = adapter
//        }
//    }
//
//    private fun addWatermarkToPdf() {
//        if (selectedFilePath == null) {
//            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val py = Python.getInstance()
//        val pyObject = py.getModule("pdf_watermarker")
//        val outputFileName = "watermarked_${System.currentTimeMillis()}.pdf"
//        val outputPath = File(filesDir, outputFileName).absolutePath
//
//        // Get alignment options
//        val verticalPosition = when (binding.verticalPositionGroup.checkedRadioButtonId) {
//            R.id.positionTop -> "top"
//            R.id.positionMiddle -> "middle"
//            R.id.positionBottom -> "bottom"
//            else -> "middle"
//        }
//        val horizontalPosition = when (binding.horizontalPositionGroup.checkedRadioButtonId) {
//            R.id.positionLeft -> "left"
//            R.id.positionCenter -> "center"
//            R.id.positionRight -> "right"
//            else -> "center"
//        }
//
//        if (binding.watermarkTypeGroup.checkedRadioButtonId == R.id.textWatermarkOption) {
//            val watermarkText = binding.watermarkEditText.text.toString()
//            val fontSize = binding.fontSizeSeekBar.progress
//            val transparency = binding.transparencySeekBar.progress
//            val rotation = binding.rotationSeekBar.progress
//            val startPage = binding.startPageEditText.text.toString().toIntOrNull() ?: 1
//            val endPage = binding.endPageEditText.text.toString().toIntOrNull() ?: -1
//
//            // Retrieve the selected color and font style
//            val color = when (binding.colorGroup.checkedRadioButtonId) {
//                R.id.colorBlack -> "#000000"
//                R.id.colorWhite -> "#FFFFFF"
//                R.id.colorRed -> "#FF0000"
//                R.id.colorDarkBlue -> "#00008B"
//                else -> "#000000"
//            }
//            val fontType = when (binding.fontTypeSpinner.selectedItemPosition) {
//                0 -> "font/garogier_regular.ttf"
//                1 -> "font/gilroy_bold.ttf"
//                2 -> "font/gilroy_light.ttf"
//                3 -> "font/gilroy_medium.ttf"
//                4 -> "font/gilroy_regular.ttf"
//                else -> "font/gilroy_semi_bold.ttf"
//            }
//            val fontBytes = assets.open(fontType).readBytes()
//
//            pyObject.callAttr(
//                "add_text_watermark",
//                selectedFilePath,
//                outputPath,
//                watermarkText,
//                fontSize,
//                transparency,
//                rotation,
//                verticalPosition,
//                horizontalPosition,
//                color,
//                fontBytes,
//                startPage,
//                endPage
//            )
//        } else if (binding.watermarkTypeGroup.checkedRadioButtonId == R.id.imageWatermarkOption && selectedImagePath != null) {
//            val transparency = binding.transparencySeekBar.progress
//            val rotation = binding.rotationSeekBar.progress
//            val startPage = binding.startPageEditText.text.toString().toIntOrNull() ?: 1
//            val endPage = binding.endPageEditText.text.toString().toIntOrNull() ?: -1
//
//            pyObject.callAttr(
//                "add_image_watermark",
//                selectedFilePath,
//                outputPath,
//                selectedImagePath,
//                transparency,
//                rotation,
//                verticalPosition,
//                horizontalPosition,
//                startPage,
//                endPage
//            )
//        }
//
//        saveToDatabase(outputPath)
//        Toast.makeText(this, "Watermark added and PDF saved", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun selectImageFile() {
////        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
////            addCategory(Intent.CATEGORY_OPENABLE)
////            type = "image/*"
////        }
////        imagePickerLauncher.launch(intent)
//
//        imagePickerLauncher.launch(arrayOf("image/*"))
//    }
//
//    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
//        uri?.let {
//            selectedImagePath = getFilePathFromUri(it)
//            Toast.makeText(this, "Image Selected: $selectedImagePath", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun saveToDatabase(outputPath: String) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            pdfDao.insert(PdfFile(filePath = outputPath))
//            loadPdfFiles()
//        }
//    }
//
//    private fun loadPdfFiles() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val files = pdfDao.getAllFiles()
//            runOnUiThread { pdfAdapter.updateFiles(files) }
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
//}
