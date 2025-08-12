package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.pixelz360.docsign.imagetopdf.creator.Constant
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.getAdsForLiftTimeString
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.isPremium
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager.AdCallback
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityMergeBinding
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.util.Collections

@AndroidEntryPoint
class MergeActivity : AppCompatActivity() {

    private lateinit var mergeRecyclerView: RecyclerView
    private lateinit var mergeButton: TextView
    private lateinit var mergePdfAdapter: MergePdfAdapter
//    private lateinit var selectedFiles: MutableList<File>
private val selectedUris = mutableListOf<Uri>()

    private var adManager: AdManager? = null


    var pdfFileViewModel: PdfFileViewModel? = null

//    //name with extension of the image
//    val timestamp = System.currentTimeMillis()
//    val mainFileName = "Untitled_file_Doc24 $timestamp"

    val timestamp = System.currentTimeMillis()
    val seconds = (timestamp / 1000) % 60  // Extract seconds from timestamp
    val mainFileName = "Untitled Merged Doc $seconds"

    val renameFileName = arrayOfNulls<String>(1)

    lateinit var binding: ActivityMergeBinding


    private var isAdLoading = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeoutRunnable: Runnable




    val selectImagesActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                //If multiple image selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0
                    for (i in 0 until count) {
                        val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri

                        imageUri?.let {
                            selectedUris.add(it)

                            setupRecyclerView(selectedUris)

//                            val intent = Intent(this, MergeActivity::class.java)
//                            intent.putParcelableArrayListExtra("selectedFiles", ArrayList(selectedUris))
//                            startActivity(intent)

                        }
                    }
                }
                //If single image selected
                else if (data?.data != null) {
                    val imageUri: Uri? = data.data
                }
            }
        }

    private fun setupRecyclerView(selectedUris: MutableList<Uri>) {
        mergePdfAdapter = MergePdfAdapter(selectedUris,this@MergeActivity)
        mergeRecyclerView.layoutManager = LinearLayoutManager(this)
        mergeRecyclerView.adapter = mergePdfAdapter
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMergeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openFilePicker()

        adManager = AdManager()


        //        adManager11.loadAd(requireActivity(), "ca-app-pub-3097817755522298/5613637998");
//        adManager11.loadAd(requireActivity(), getString(R.string.convert_to_pdf_final_button_screen_intertial_ad));
        adManager!!.loadAd(this@MergeActivity, getString(R.string.merge_pdf_conversion_button_intertial_ad))



        // Get color from resources
        val statusBarColor = ContextCompat.getColor(this@MergeActivity, R.color.white)
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor, this@MergeActivity)






        renameFileName[0] = mainFileName


        pdfFileViewModel = ViewModelProvider(this)[PdfFileViewModel::class.java]


//        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "pdf_database")
//            .fallbackToDestructiveMigration()
//            .build()


//        pdfDao = db.pdfFileDao()

        mergeRecyclerView = findViewById(R.id.mergeRecyclerView)
        mergeButton = findViewById(R.id.mergeButton)

//        selectedFiles = intent.getSerializableExtra("selectedFiles") as ArrayList<File>
//        selectedUris = intent.getParcelableArrayListExtra<Uri>("selectedFiles") ?: mutableListOf()










//        mergePdfAdapter = MergePdfAdapter(selectedUris)
//        mergeRecyclerView.layoutManager = LinearLayoutManager(this)
//        mergeRecyclerView.adapter = mergePdfAdapter

        // Allow drag and drop reordering
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                Collections.swap(selectedUris, fromPosition, toPosition)
                mergePdfAdapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(mergeRecyclerView)

        mergeButton.setOnClickListener {
//            mergePdfFiles()

//            goToNextActivity()



            if (!isInternetAvailable()) {
                // No internet, proceed directly
                goToNextActivity()
            } else if (isPremium(this@MergeActivity) ||
                getAdsForLiftTimeString(this@MergeActivity) == "ads_free_life_time"
            ) {
                // User is premium, skip ad
                goToNextActivity()
            } else {
                // Show ad first, then navigate
                adManager!!.showAdIfAvailable(
                    this@MergeActivity,
                    getString(R.string.watermark_pdf_completion_button_intertial_ad),
                    object : AdCallback {
                        override fun onAdDismissed() {
                            goToNextActivity()
                        }

                        override fun onAdFailedToShow() {
                            goToNextActivity()
                        }
                    })
            }










//            if (!isInternetAvailable()) {
//                goToNextActivity()
//            } else {
////                showAdOrLoad()
//
//                adManager11!!.showAdIfAvailable(this@MergeActivity, object : AdCallback {
//                    override fun onAdDismissed() {
//                        // Define your custom action here after the ad is dismissed
//                        goToNextActivity()
//                    }
//
//                    override fun onAdFailedToShow() {
//                        // Define your custom action here if the ad fails to show
//                        goToNextActivity()
//                    }
//                })
//
//
//            }

        }

        binding.backButton.setOnClickListener {
            onBackPressed()
            finish()
        }

    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "application/pdf"
        selectImagesActivityResult.launch(intent)

    }



    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }


    fun goToNextActivity() {
        mergePdfFiles()

        }


    private fun mergePdfFiles() {

        val view =
            LayoutInflater.from(this@MergeActivity).inflate(R.layout.rename_dailog, null)
        @SuppressLint("MissingInflatedId", "LocalSuppress") val pdfNewNameEt =
            view.findViewById<TextInputEditText>(R.id.pdfNewNameEt)
        @SuppressLint("MissingInflatedId", "LocalSuppress") val renameBtn =
            view.findViewById<TextView>(R.id.renameBtn)
        val cancelBtn = view.findViewById<TextView>(R.id.cancelBtn)
        val clearTextIcon = view.findViewById<ImageView>(R.id.clearTextIcon)

        clearTextIcon.setOnClickListener { pdfNewNameEt.setText("") }


        pdfNewNameEt.setText(renameFileName.get(0))


        val builder = AlertDialog.Builder(this@MergeActivity)
        builder.setView(view)

        val alertDialog = builder.create()
        alertDialog.show()


        cancelBtn.setOnClickListener { alertDialog.dismiss() }

        renameBtn.setOnClickListener {
            val newName = pdfNewNameEt.text.toString().trim { it <= ' ' }
            if (newName.isEmpty()) {
                Toast.makeText(this@MergeActivity, "Please enter a name for the PDF document. document.", Toast.LENGTH_SHORT).show()
            } else {
                renameFileName[0] = "$newName.pdf"




                try {

                    val root = File(getExternalFilesDir(null), Constant.PDF_FOLDER)
                    root.mkdirs()

                    val outputPath = File(root, renameFileName[0])



                    val document = Document()
                    val pdfCopy = PdfCopy(document, FileOutputStream(outputPath))
                    document.open()

                    for (uri in selectedUris) {
                        val inputStream = contentResolver.openInputStream(uri) ?: continue
                        val reader = PdfReader(inputStream)
                        for (i in 1..reader.numberOfPages) {
                            pdfCopy.addPage(pdfCopy.getImportedPage(reader, i))
                        }
                        reader.close()
                        inputStream.close()
                    }

                    document.close()






//                    Toast.makeText(this, "PDFs Merged Successfully! Saved to: ${outputPath.absolutePath}", Toast.LENGTH_SHORT).show()

                    // Save the merged file to the database
                    val pdfFile = com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile(
                        outputPath.absolutePath,
                        renameFileName[0],
                        "Merged Doc",
                        "#13D4FF",
                        null,
                        outputPath.length().toDouble(),
                        outputPath.lastModified(),
                        false,
                        false,
                        true,
                        false,
                        true,
                        false,
                        false,
                        false,
                        false,
                        AccountsOrGuesHelper.checkAccountOrNot(this@MergeActivity),
                        false,
                        "pdf"
                    )

                    //                        db.pdfFileDao().insert(pdfFile);
                    pdfFileViewModel!!.insertPdfFile(pdfFile)

                    Log.d("checkSusscessfully","path insnert "+pdfFile)


                    Toast.makeText(this, "PDFs Merged Successfully! Saved to: $outputPath", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@MergeActivity, Prview_Screen::class.java)
                    intent.putExtra("pdffilePath", "" + outputPath.getAbsolutePath())
                    intent.putExtra("fileName", renameFileName[0])
                    startActivity(intent)


                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error Merging PDFs", Toast.LENGTH_SHORT).show()
                }






                alertDialog.dismiss()
            }
        }



















//
//        try {
//            val outputPath = "${getExternalFilesDir(null)}/merged.pdf"
//            val document = Document()
//            val pdfCopy = PdfCopy(document, FileOutputStream(outputPath))
//            document.open()
//
//            for (file in selectedFiles) {
//                val reader = PdfReader(file.absolutePath)
//                for (i in 1..reader.numberOfPages) {
//                    pdfCopy.addPage(pdfCopy.getImportedPage(reader, i))
//                }
//                reader.close()
//            }
//
//            document.close()
//            Toast.makeText(this, "PDFs Merged Successfully! Saved to: $outputPath", Toast.LENGTH_SHORT).show()
//
//            // Save the merged file to the database
////            saveToDatabase(outputPath)
//
//
//
//
//            renameDailog(outputPath)
//
//            val fileDate: File = File(outputPath)
//            val fileSizeBytes = fileDate.length()
////            val timestamp: Long = file.lastModified()
//            val directory = File(outputPath)
//            var timestamp = directory.lastModified()
//
//
//
//
//
//            val pdfFile = com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile(
//                outputPath,
//                fileName,
//                null,
//                fileSizeBytes.toDouble(),
//                timestamp,
//                false,
//                false,
//                false,
//                true,
//                false,
//                false
//            )
//
//            //                        db.pdfFileDao().insert(pdfFile);
//            pdfFileViewModel!!.insertPdfFile(pdfFile)
//
//
//
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(this, "Error Merging PDFs", Toast.LENGTH_SHORT).show()
//        }
    }


    private fun saveToDatabase(outputPath: String) {


//        lifecycleScope.launch(Dispatchers.IO) {
//            pdfDao.insert(PdfFile(filePath = outputPath))
//        }
//
//        val intent = Intent(this@MergeActivity,ShowAllListActivity::class.java)
//        startActivity(intent)
    }
}
