package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf

import android.R.attr.label
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.google.android.material.textfield.TextInputEditText
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.pixelz360.docsign.imagetopdf.creator.Constant
import com.pixelz360.docsign.imagetopdf.creator.PdfPythonEditorTools.PdfAdapter
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.FileUtils
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.getAdsForLiftTimeString
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.isPremium
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager.AdCallback
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.CamActivity
import com.pixelz360.docsign.imagetopdf.creator.databinding.RangeByPdfSplitterActivityBinding
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class RangeByPdfSplitterActivity : AppCompatActivity() {

    private lateinit var binding: RangeByPdfSplitterActivityBinding


    var pdfFileViewModel: PdfFileViewModel? = null

    var splitOption:String?=null
    var selectedFilePath:String?=null

    private var adManager: AdManager? = null



    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeoutRunnable: Runnable




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RangeByPdfSplitterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Get color from resources
        val statusBarColor = ContextCompat.getColor(this@RangeByPdfSplitterActivity, R.color.white)
        // Change status bar color
        com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils.changeStatusBarColor(statusBarColor, this@RangeByPdfSplitterActivity)



        adManager = AdManager()


        //        adManager11.loadAd(requireActivity(), "ca-app-pub-3097817755522298/5613637998");
//        adManager11.loadAd(requireActivity(), getString(R.string.convert_to_pdf_final_button_screen_intertial_ad));
        adManager!!.loadAd(this@RangeByPdfSplitterActivity, getString(R.string.split_pdf_completion_button_intertial_ad))



//        val adRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest)






        pdfFileViewModel = ViewModelProvider(this)[PdfFileViewModel::class.java]

        splitOption = intent.getStringExtra("splitOption")
        selectedFilePath = intent.getStringExtra("selectedFilePath")

        if (splitOption==null && selectedFilePath==null){
            return
        }

        val reader = PdfReader(selectedFilePath)
        val numberOfPages = reader.numberOfPages
        binding.totalPages.text = "Total Pages: $numberOfPages"

        binding.toolbarTitle.text = splitOption

        Log.d("checknewadtion","RangeByPdfSplitterActivity  "+splitOption )
        Log.d("checknewadtion","RangeByPdfSplitterActivity. selectedFilePath  "+selectedFilePath )
        Log.d("checknewadtion","RangeByPdfSplitterActivity. selectedFilePath  "+"Total Pages: $numberOfPages" )

        if (splitOption.equals("Equal Page Ranges")){
        binding.equalPageRangesLayout.visibility = VISIBLE
        binding.pageByRangeAndDeletePageLayout.visibility = GONE
        }else{
            binding.equalPageRangesLayout.visibility = GONE
            binding.pageByRangeAndDeletePageLayout.visibility = VISIBLE
        }

        binding.fromPageBtn.setOnClickListener {

            intervalDailog("fromPageBtn")

        }

        binding.toPageBtn.setOnClickListener {
            intervalDailog("toPageBtn")
        }

        binding.equalPageRangesLayout.setOnClickListener {
            intervalDailog("equalPageRangesLayout")
        }


        binding.splitBtn.setOnClickListener {

//            goToNextActivity()


            if (!isInternetAvailable()) {
                // No internet, proceed directly
                goToNextActivity()
            } else if (isPremium(this@RangeByPdfSplitterActivity) ||
                getAdsForLiftTimeString(this@RangeByPdfSplitterActivity) == "ads_free_life_time"
            ) {
                // User is premium, skip ad
                goToNextActivity()
            } else {
                // Show ad first, then navigate
                adManager!!.showAdIfAvailable(
                    this@RangeByPdfSplitterActivity,
                    getString(R.string.split_pdf_completion_button_intertial_ad),
                    object : AdCallback {
                        override fun onAdDismissed() {
                            goToNextActivity()
                        }

                        override fun onAdFailedToShow() {
                            goToNextActivity()
                        }
                    })
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

        if (binding.extractAllSwitchBtn.isChecked){
            splitPdfFile("Extract All Pages")
            Log.d("checknewadtion","Extract All Pages split" )
        }else{
            splitPdfFile(splitOption.toString())

            Log.d("checknewadtion","extractAllSwitchBtn disable" )
        }




    }

    private fun intervalDailog(category: String) {

        val dialogRename = Dialog(this@RangeByPdfSplitterActivity, R.style.renameDialogStyle)
        dialogRename.setContentView(R.layout.interval_dailog)

        val window = dialogRename.window
        if (window != null) {
            window.setGravity(Gravity.CENTER)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val metrics = Resources.getSystem().displayMetrics
            val screenWidth = metrics.widthPixels
            val desiredWidth = screenWidth - 2 * FileUtils.dpToPx(this@RangeByPdfSplitterActivity, 30)
            val params = dialogRename.window!!.attributes
            params.width = desiredWidth
            window.attributes = params
        }


        val inputNumberEt = dialogRename.findViewById<TextInputEditText>(R.id.inputNumberEt)
        val cancelBtn = dialogRename.findViewById<TextView>(R.id.cancelBtn)
        val okBtn = dialogRename.findViewById<TextView>(R.id.okBtn)
        val clearTextIcon = dialogRename.findViewById<ImageView>(R.id.clearTextIcon)

        clearTextIcon.setOnClickListener { inputNumberEt.setText("") }


        cancelBtn.setOnClickListener { dialogRename.dismiss() }
        okBtn.setOnClickListener {

            if (binding.fromPageNumber.text.isNotEmpty()){

                if (category.equals("fromPageBtn")){

                    binding.fromPageNumber.setText(inputNumberEt.text)

                    UtilString.getInstance().showSnackbar(this@RangeByPdfSplitterActivity, "From Page Number: "+inputNumberEt.text)

                }else if (category.equals("toPageBtn")){

                    binding.toPageNumber.setText(inputNumberEt.text)

                    UtilString.getInstance().showSnackbar(this@RangeByPdfSplitterActivity, "To Page Number: "+inputNumberEt.text)

                }else if (category.equals("equalPageRangesLayout")){

                    binding.equalPageRangesNumber.setText(inputNumberEt.text)

                    UtilString.getInstance().showSnackbar(this@RangeByPdfSplitterActivity, "Fixed Ranges: "+inputNumberEt.text)

                }


            }else{
                UtilString.getInstance().showSnackbar(this@RangeByPdfSplitterActivity, "Please Input No")

            }

            dialogRename.dismiss()
        }



        dialogRename.show()


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
//            Toast.makeText(this, "Please select a PDF file first", Toast.LENGTH_SHORT).show()

            UtilString.getInstance().showSnackbar(this@RangeByPdfSplitterActivity, "Please select a PDF file first")
            return
        }

//        val splitOption = binding.splitOptionsSpinner.selectedItem.toString()
        val startPage = binding.fromPageNumber.text.toString().toIntOrNull() ?: 1
        val endPage = binding.toPageNumber.text.toString().toIntOrNull() ?: -1
        val pagesPerSplit = binding.equalPageRangesNumber.text.toString().toIntOrNull() ?: 1

//        val outputFolder = File(filesDir, "split_pdfs")

        val outputFolder = File(getExternalFilesDir(null), Constant.PDF_FOLDER)

        if (!outputFolder.exists()) outputFolder.mkdirs()

        val newlyGeneratedFiles = mutableListOf<File>() // Track new files

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val reader = PdfReader(selectedFilePath)
                when (splitOption) {
                    "Split by Range" -> splitByRange(reader, startPage, endPage, outputFolder, newlyGeneratedFiles)
                    "Equal Page Ranges" -> splitIntoEqualRanges(reader, pagesPerSplit, outputFolder, newlyGeneratedFiles)
                    "Extract All Pages" -> extractAllPages(reader, outputFolder, newlyGeneratedFiles)
                    "Delete Pages by Range" -> deletePagesByRange(reader, startPage, endPage, outputFolder, newlyGeneratedFiles)
                }

                runOnUiThread {
//                    Toast.makeText(this@RangeByPdfSplitterActivity, "PDF operation completed successfully", Toast.LENGTH_SHORT).show()
                    UtilString.getInstance().showSnackbar(this@RangeByPdfSplitterActivity, "PDF operation completed successfully")

                    // Save newly generated files to the database
                    saveGeneratedFilesToDatabase(newlyGeneratedFiles)

                    // Refresh the RecyclerView
//                    loadPdfFiles(newlyGeneratedFiles)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
//                    Toast.makeText(this@RangeByPdfSplitterActivity, "PDF operation failed", Toast.LENGTH_SHORT).show()
                    UtilString.getInstance().showSnackbar(this@RangeByPdfSplitterActivity, "PDF operation failed")

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
                    AccountsOrGuesHelper.checkAccountOrNot(this@RangeByPdfSplitterActivity),
                    false,
                    "pdf"
                )


                Log.d("checknewadtion","pdfFile.  "+pdfFile.getFilePath())



                pdfFileViewModel?.insertPdfFile(pdfFile) // Save file to the database

                val intent = Intent(this@RangeByPdfSplitterActivity, Prview_Screen::class.java)
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
    

}
