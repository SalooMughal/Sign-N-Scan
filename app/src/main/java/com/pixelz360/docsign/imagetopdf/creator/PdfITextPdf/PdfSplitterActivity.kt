package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.pixelz360.docsign.imagetopdf.creator.Constant
import com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools.PdfAdapter
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.getAdsForLiftTimeString
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.isPremium
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager.AdCallback
import com.pixelz360.docsign.imagetopdf.creator.databinding.PdfSplitterItextActivityBinding
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PdfSplitterActivity : AppCompatActivity() {

    private lateinit var binding: PdfSplitterItextActivityBinding
    private var selectedFilePath: String? = null
    private lateinit var pdfAdapter: PdfAdapter
//    private lateinit var pdfDao: PdfFileDao

    var pdfFileViewModel: PdfFileViewModel? = null

    private var isAdLoading = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeoutRunnable: Runnable
    private var adManager: AdManager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PdfSplitterItextActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Get color from resources
        val statusBarColor = ContextCompat.getColor(this@PdfSplitterActivity, R.color.white)
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor, this@PdfSplitterActivity)


        adManager = AdManager()


        //        adManager11.loadAd(requireActivity(), "ca-app-pub-3097817755522298/5613637998");
//        adManager11.loadAd(requireActivity(), getString(R.string.convert_to_pdf_final_button_screen_intertial_ad));
        adManager!!.loadAd(this@PdfSplitterActivity, getString(R.string.split_pdf_completion_button_intertial_ad))


//        val adRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest)

        pdfFileViewModel = ViewModelProvider(this)[PdfFileViewModel::class.java]

        selectPdfFile()

//        setupSpinner()

//        binding.selectPdfButton.setOnClickListener {
//
//            selectPdfFile()
//
//        }
//        binding.splitPdfBtn.setOnClickListener { splitPdfFile("Extract All Pages") }


        binding.extractAllCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->

            Log.d("checknewadtion","extractAllCheckBox setOnCheckedChangeListener")

        }

        binding.splitBtn.setOnClickListener {


            if (!isInternetAvailable()) {
                // No internet, proceed directly
                if (!binding.extractAllCheckBox.isChecked){
                    UtilString.getInstance().showSnackbar(this@PdfSplitterActivity, "Please Fill Check Button")

                }else{


                    goToNextActivity()


                }
            } else if (isPremium(this@PdfSplitterActivity) ||
                getAdsForLiftTimeString(this@PdfSplitterActivity) == "ads_free_life_time"
            ) {
                // User is premium, skip ad
                if (!binding.extractAllCheckBox.isChecked){
                    UtilString.getInstance().showSnackbar(this@PdfSplitterActivity, "Please Fill Check Button")

                }else{


                    goToNextActivity()


                }
            } else {
                // Show ad first, then navigate
                adManager!!.showAdIfAvailable(
                    this@PdfSplitterActivity,
                    getString(R.string.split_pdf_completion_button_intertial_ad),
                    object : AdCallback {
                        override fun onAdDismissed() {
                            if (!binding.extractAllCheckBox.isChecked){
                                UtilString.getInstance().showSnackbar(this@PdfSplitterActivity, "Please Fill Check Button")

                            }else{


                                goToNextActivity()


                            }
                        }

                        override fun onAdFailedToShow() {
                            if (!binding.extractAllCheckBox.isChecked){
                                UtilString.getInstance().showSnackbar(this@PdfSplitterActivity, "Please Fill Check Button")

                            }else{


                                goToNextActivity()


                            }
                        }
                    })
            }




//            if (!binding.extractAllCheckBox.isChecked){
//                UtilString.getInstance().showSnackbar(this@PdfSplitterActivity, "Please Fill Check Button")
//
//            }else{
//
//
//                goToNextActivity()
//
//
//            }




            Log.d("checknewadtion","splitBtn setOnClickListener")

        }

        binding.splitByRangeBtn.setOnClickListener {

            if (selectedFilePath!=null){
                splitOptionSendRangeActivity("Split by Range")

                Log.d("checknewadtion","Split by Range")

            }

        }

        binding.splitFixRangeBtn.setOnClickListener {

            if (selectedFilePath!=null){
                splitOptionSendRangeActivity("Equal Page Ranges")

                Log.d("checknewadtion","Equal Page Ranges")
            }

        }

        binding.deletePagesBtn.setOnClickListener {

            if (selectedFilePath!=null){
                splitOptionSendRangeActivity("Delete Pages by Range")

                Log.d("checknewadtion","Delete Pages by Range")
            }

        }

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


    fun goToNextActivity() {


        if (binding.extractAllCheckBox.isChecked){
            splitPdfFile("Extract All Pages")
        }




    }



    private fun splitOptionSendRangeActivity(splitOption: String) {
        val intent = Intent(this@PdfSplitterActivity, RangeByPdfSplitterActivity::class.java)
        intent.putExtra("splitOption",splitOption)
        intent.putExtra("selectedFilePath",selectedFilePath)
        startActivity(intent)
    }


//    private fun setupSpinner() {
//        val options = arrayOf("Split by Range", "Equal Page Ranges", "Extract All Pages", "Delete Pages by Range")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
//        binding.splitOptionsSpinner.adapter = adapter
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
    private fun getFilePathFromUri(uri: Uri): String? {
        var fileName = "unknown.pdf"
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)) ?: "unknown.pdf"
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

    private fun splitPdfFile(splitOption: String) {
        if (selectedFilePath == null) {
            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()
            return
        }

//        val splitOption = binding.splitOptionsSpinner.selectedItem.toString()
//        val startPage = binding.startPageEditText.text.toString().toIntOrNull() ?: 1
//        val endPage = binding.endPageEditText.text.toString().toIntOrNull() ?: -1
//        val pagesPerSplit = binding.pagesPerSplitEditText.text.toString().toIntOrNull() ?: 1

//        val outputFolder = File(filesDir, "split_pdfs")

        val outputFolder = File(getExternalFilesDir(null), Constant.PDF_FOLDER)

        if (!outputFolder.exists()) outputFolder.mkdirs()

        val newlyGeneratedFiles = mutableListOf<File>() // Track new files

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val reader = PdfReader(selectedFilePath)
                when (splitOption) {
//                    "Split by Range" -> splitByRange(reader, startPage, endPage, outputFolder, newlyGeneratedFiles)
//                    "Equal Page Ranges" -> splitIntoEqualRanges(reader, pagesPerSplit, outputFolder, newlyGeneratedFiles)
                    "Extract All Pages" -> extractAllPages(reader, outputFolder, newlyGeneratedFiles)
//                    "Delete Pages by Range" -> deletePagesByRange(reader, startPage, endPage, outputFolder, newlyGeneratedFiles)
                }

                runOnUiThread {
//                    Toast.makeText(this@PdfSplitterActivity, "PDF operation completed successfully", Toast.LENGTH_SHORT).show()

                    UtilString.getInstance().showSnackbar(this@PdfSplitterActivity, "PDF operation completed successfully")


                    // Save newly generated files to the database
                    saveGeneratedFilesToDatabase(newlyGeneratedFiles)

//                    val intent = Intent(this@PdfSplitterActivity, Prview_Screen::class.java)
//                    intent.putExtra("pdffilePath", "" + newlyGeneratedFiles.get(0).absolutePath)
//                    intent.putExtra("fileName", newlyGeneratedFiles.get(0).absolutePath)
//                    startActivity(intent)


                    // Refresh the RecyclerView
//                    loadPdfFiles(newlyGeneratedFiles)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
//                    Toast.makeText(this@PdfSplitterActivity, "PDF operation failed", Toast.LENGTH_SHORT).show()

                    UtilString.getInstance().showSnackbar(this@PdfSplitterActivity, "PDF operation failed")
                }
            }
        }
    }

    private fun saveGeneratedFilesToDatabase(newlyGeneratedFiles: List<File>) {
        lifecycleScope.launch(Dispatchers.IO) {
            newlyGeneratedFiles.forEach { file ->
                val fileSizeBytes = file.length()
                val timestamp = file.lastModified()


                val pdfFile = com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile(
                    file.absolutePath,
                    file.name,
                    "Splited Doc",
                    "#FA0004",
                    null,
                    fileSizeBytes.toDouble(),
                    timestamp,
                    false,
                    false,
                    true,
                    false,
                    false,
                    true,
                    false,
                    false,
                    false,
                    AccountsOrGuesHelper.checkAccountOrNot(this@PdfSplitterActivity),
                    false,
                    "pdf"
                )


                pdfFileViewModel?.insertPdfFile(pdfFile) // Save file to the database

                val intent = Intent(this@PdfSplitterActivity, Prview_Screen::class.java)
                intent.putExtra("pdffilePath", "" + pdfFile.getFilePath())
                intent.putExtra("fileName", pdfFile.fileName)
                startActivity(intent)
                finish()

            }
        }
    }



    private fun splitByRange(reader: PdfReader, startPage: Int, endPage: Int, outputFolder: File, newlyGeneratedFiles: MutableList<File>) {
        val outputPath = File(outputFolder, "split_range_${startPage}_to_${endPage}.pdf")
        val document = Document()
        val pdfCopy = PdfCopy(document, FileOutputStream(outputPath))
        document.open()

        for (i in startPage..endPage) {
            val page = pdfCopy.getImportedPage(reader, i)
            pdfCopy.addPage(page)
        }

        document.close()

        // Add the generated file to the list
        newlyGeneratedFiles.add(outputPath)
    }

    private fun splitIntoEqualRanges(reader: PdfReader, pagesPerSplit: Int, outputFolder: File, newlyGeneratedFiles: MutableList<File>) {
        val totalPages = reader.numberOfPages
        for (i in 1..totalPages step pagesPerSplit) {
            val endPage = minOf(i + pagesPerSplit - 1, totalPages)
            splitByRange(reader, i, endPage, outputFolder, newlyGeneratedFiles)
        }
    }

    private fun extractAllPages(reader: PdfReader, outputFolder: File, newlyGeneratedFiles: MutableList<File>) {
        for (i in 1..reader.numberOfPages) {
            splitByRange(reader, i, i, outputFolder, newlyGeneratedFiles)
        }
    }

    private fun deletePagesByRange(reader: PdfReader, startPage: Int, endPage: Int, outputFolder: File, newlyGeneratedFiles: MutableList<File>) {
        val outputPath = File(outputFolder, "deleted_pages_${startPage}_to_${endPage}.pdf")
        val document = Document()
        val pdfCopy = PdfCopy(document, FileOutputStream(outputPath))
        document.open()

        for (i in 1..reader.numberOfPages) {
            if (i < startPage || i > endPage) {
                val page = pdfCopy.getImportedPage(reader, i)
                pdfCopy.addPage(page)
            }
        }

        document.close()

        // Add the generated file to the list
        newlyGeneratedFiles.add(outputPath)
    }
    




//
//    private fun splitByRange(reader: PdfReader, startPage: Int, endPage: Int, outputFolder: File) {
//        val outputPath = File(outputFolder, "split_range_${startPage}_to_${endPage}.pdf").absolutePath
//        val document = Document()
//        val pdfCopy = PdfCopy(document, FileOutputStream(outputPath))
//        document.open()
//
//        for (i in startPage..endPage) {
//            val page = pdfCopy.getImportedPage(reader, i)
//            pdfCopy.addPage(page)
//        }
//
//        document.close()
//    }
//
//    private fun splitIntoEqualRanges(reader: PdfReader, pagesPerSplit: Int, outputFolder: File) {
//        val totalPages = reader.numberOfPages
//        for (i in 1..totalPages step pagesPerSplit) {
//            val endPage = minOf(i + pagesPerSplit - 1, totalPages)
//            splitByRange(reader, i, endPage, outputFolder)
//        }
//    }
//
//    private fun extractAllPages(reader: PdfReader, outputFolder: File) {
//        for (i in 1..reader.numberOfPages) {
//            splitByRange(reader, i, i, outputFolder)
//        }
//    }
//
//    private fun deletePagesByRange(reader: PdfReader, startPage: Int, endPage: Int, outputFolder: File) {
//        val outputPath = File(outputFolder, "deleted_pages_${startPage}_to_${endPage}.pdf").absolutePath
//        val document = Document()
//        val pdfCopy = PdfCopy(document, FileOutputStream(outputPath))
//        document.open()
//
//        for (i in 1..reader.numberOfPages) {
//            if (i < startPage || i > endPage) {
//                val page = pdfCopy.getImportedPage(reader, i)
//                pdfCopy.addPage(page)
//            }
//        }
//
//        document.close()
//    }

    private fun loadPdfFiles() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val files = pdfDao.getAllFiles()
//            runOnUiThread {
//                pdfAdapter.updateFiles(files)
//            }
//        }
    }

    private fun openPdf(filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.fromFile(File(filePath)), "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    companion object {
        private const val PICK_PDF_FILE = 111
    }
}
