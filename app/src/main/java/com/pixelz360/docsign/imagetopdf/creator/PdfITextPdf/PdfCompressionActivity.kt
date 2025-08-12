package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixelz360.docsign.imagetopdf.creator.Constant
import com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools.PdfAdapter
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.getAdsForLiftTimeString
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.isPremium
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager.AdCallback
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfCompressionBinding
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class PdfCompressionActivity : AppCompatActivity() {

//    private lateinit var pdfDao: PdfFileDao
    private var selectedFilePath: String? = null
    private lateinit var pdfAdapter: PdfAdapter
    lateinit var binding:ActivityPdfCompressionBinding
//    private var mUtilPdf: UtilPdf? = null
var pdfFileViewModel: PdfFileViewModel? = null

    private var adManager: AdManager? = null


    val PICK_PDF_FILE = 111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfCompressionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get color from resources
        val statusBarColor = ContextCompat.getColor(this@PdfCompressionActivity, R.color.white)
        // Change status bar color
        com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils.changeStatusBarColor(statusBarColor, this@PdfCompressionActivity)

        adManager = AdManager()


        //        adManager11.loadAd(requireActivity(), "ca-app-pub-3097817755522298/5613637998");
//        adManager11.loadAd(requireActivity(), getString(R.string.convert_to_pdf_final_button_screen_intertial_ad));
        adManager!!.loadAd(this@PdfCompressionActivity, getString(R.string.split_pdf_completion_button_intertial_ad))


        pdfFileViewModel = ViewModelProvider(this)[PdfFileViewModel::class.java]

         selectedFilePath = intent.getStringExtra("selectedFilePath")




//        val db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java, "pdf_database"
//        ).build()

//        pdfDao = db.pdfFileDao()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        pdfAdapter = PdfAdapter(emptyList()) { pdfFile ->
            openPdf(this, pdfFile.filePath)
        }
        recyclerView.adapter = pdfAdapter

        findViewById<View>(R.id.selectPdfButton).setOnClickListener {
            selectPdfFile()
        }

//        loadPdfFiles()

        // High compression button click
        binding.compressPdfButton.setOnClickListener {


            if (!isInternetAvailable()) {
                // No internet, proceed directly
                compressPdfFile()
            } else if (isPremium(this@PdfCompressionActivity) ||
                getAdsForLiftTimeString(this@PdfCompressionActivity) == "ads_free_life_time"
            ) {
                // User is premium, skip ad
                compressPdfFile()
            } else {
                // Show ad first, then navigate
                adManager!!.showAdIfAvailable(
                    this@PdfCompressionActivity,
                    getString(R.string.split_pdf_completion_button_intertial_ad),
                    object : AdCallback {
                        override fun onAdDismissed() {
                            compressPdfFile()
                        }

                        override fun onAdFailedToShow() {
                            compressPdfFile()
                        }
                    })
            }


//            compressPdfFile()

        }
        binding.pages.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(compressionPer: CharSequence, start: Int, before: Int, count: Int) {
                Log.d("checkcount", "count: $count")
                Log.d("checkcount", "CharSequence: ${compressionPer.toString()}")

                if (compressionPer.isNotEmpty()) {
                    try {
                        val percentage = compressionPer.toString().toInt()

                        when {
                            percentage in 0..30 -> {
                                binding.infoTextQuantity.text = "Low compression & High Quality"
                                Log.d("checkcount", "Low compression & High Quality")
                            }
                            percentage in 31..60 -> {
                                binding.infoTextQuantity.text = "Low compression & Medium Quality"
                                Log.d("checkcount", "Low compression & Medium Quality")
                            }
                            percentage in 61..100 -> {
                                binding.infoTextQuantity.text = "High compression & Low Quality"
                                Log.d("checkcount", "High compression & Low Quality")
                            }
                            else -> {
                                binding.infoTextQuantity.text = "Invalid compression percentage"
                                Log.d("checkcount", "Invalid compression percentage")
                            }
                        }
                    } catch (e: NumberFormatException) {
                        binding.infoTextQuantity.text = "Invalid input"
                        Log.d("checkcount", "Invalid input: ${e.message}")
                    }
                } else {
                    binding.infoTextQuantity.text = ""
                }
            }
        })




        binding.backButton.setOnClickListener {
            onBackPressed()
            finish()
        }



    }

    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }



    private fun compressPdfFile() {
        if (selectedFilePath == null) {
            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
            return
        }

        val input = binding.pages.text.toString()
        val compressionLevel: Int

        try {
            compressionLevel = input.toInt()

            if (compressionLevel > 100 || compressionLevel <= 0) {
                Toast.makeText(this, "Invalid compression level. Please enter a value between 1 and 100.", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show()
            return
        }

        val timestamp1 = System.currentTimeMillis()
        val seconds = (timestamp1 / 1000) % 60  // Extract seconds from timestamp
        val mainFileName = "Untitled Compressed Doc $seconds"

        val newName = "${mainFileName}.pdf"
        val root = File(getExternalFilesDir(null), Constant.PDF_FOLDER)
        root.mkdirs()

        val outputPath = File(root, newName).absolutePath
        UtilPdf.compressPDF(selectedFilePath, outputPath, 100 - compressionLevel)

        // Save to database
        val fileSizeBytes = File(outputPath).length()
        val timestamp = File(outputPath).lastModified()


        val pdfFile = PdfFile(
            outputPath,
            newName,
            "Compressed Doc",
            "#A020F0", // Different color for compressed files
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
            false,
            AccountsOrGuesHelper.checkAccountOrNot(this@PdfCompressionActivity),
            false,
            "pdf"
        )

        lifecycleScope.launch(Dispatchers.IO) {
            pdfFileViewModel!!.insertPdfFile(pdfFile)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@PdfCompressionActivity, "PDF Compressed Successfully! Saved to: $outputPath", Toast.LENGTH_SHORT).show()

                // Launch preview screen
                val intent = Intent(this@PdfCompressionActivity, Prview_Screen::class.java)
                intent.putExtra("pdffilePath", outputPath)
                intent.putExtra("fileName", newName)
                startActivity(intent)
                finish()
            }
        }
    }








//    private fun compressPdfFile() {
//        if (selectedFilePath == null) {
//            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//
//        val input = binding.pages.text.toString()
//        val check: Int
//        check = input.toInt()
//        val outputPath = selectedFilePath!!.replace(getString(R.string.pdf_ext), "_edited" + check + getString(R.string.pdf_ext))
//        UtilPdf.compressPDF(selectedFilePath, outputPath, 100 - check)
//        Log.d("checksucesspath", " selectedFilePath 1 $selectedFilePath")
//        Log.d("checksucesspath", " activity side outputPath  $outputPath")
//
//
//
//
////        try {
////            check = input.toInt()
////            if (check > 100 || check <= 0 || selectedFilePath == null) {
////                UtilString.getInstance().showSnackbar(this@PdfCompressionActivity, R.string.invalid_entry)
////            } else {
////                val outputPath = selectedFilePath!!.replace(getString(R.string.pdf_ext), "_edited" + check + getString(R.string.pdf_ext))
////                mUtilPdf!!.compressPDF(selectedFilePath, outputPath, 100 - check)
////                Log.d("checksucesspath", " selectedFilePath 1 $selectedFilePath")
////
////            }
////        } catch (e: NumberFormatException) {
////            UtilString.getInstance().showSnackbar(this@PdfCompressionActivity, R.string.invalid_entry)
////            Log.d("checksucesspath", " invalid_entry  "+R.string.invalid_entry)
////
////        }catch (e:NullPointerException){
////            Log.e("tag", "compressPDF: ")
////            Log.d("checksucesspath", " compressPDF  "+e.message)
////
////        }
////
//
//
////        val updateHandler = Handler()
////        val runnable = Runnable {
////            value1=true
////        }
////        updateHandler.postDelayed(runnable, 1000)
//
//
//        lifecycleScope.launch(Dispatchers.IO) {
//
//
//
//
//
//
//            pdfDao.insert(PdfFile(filePath = outputPath))
//
//
//
//            loadPdfFiles()
//        }
//
//
//
////        val py = Python.getInstance()
////        val pyObject = py.getModule("pdf_compressor")
////        val outputPath = "${filesDir}/compressed_file.pdf"
//////        val result = pyObject.callAttr("compress_pdf", selectedFilePath, outputPath)
////        val result = pyObject.callAttr("compress_pdf", selectedFilePath, outputPath, compressionLevel)
////
////
////
////        if (result != null && File(outputPath).exists()) {
////
////            Log.d("checkpath","outputPath  "+outputPath)
////
////
////
////            lifecycleScope.launch(Dispatchers.IO) {
////
////
////                pdfDao.insert(PdfFile(filePath = outputPath))
////                loadPdfFiles()
////            }
////
////
////            Toast.makeText(this, "PDF compressed and saved", Toast.LENGTH_SHORT).show()
////        } else {
////            Toast.makeText(this, "Compression failed", Toast.LENGTH_SHORT).show()
////        }
//    }

//    private fun loadPdfFiles() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val files = pdfDao.getAllFiles()
//            runOnUiThread {
//                pdfAdapter.updateFiles(files)
//            }
//        }
//    }


    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }

        startActivityForResult(intent, PICK_PDF_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedFilePath = getFilePathFromUri(uri)
                Toast.makeText(this, "File Selected: $selectedFilePath", Toast.LENGTH_SHORT).show()
            }
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




    private fun openPdf(context: Context, filePath: String) {
        val file = File(filePath)
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }
}
