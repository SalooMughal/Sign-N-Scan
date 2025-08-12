package com.pixelz360.docsign.imagetopdf.creator

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ParcelFileDescriptor
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.room.Room.databaseBuilder
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.AppDatabase
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.FileUtils
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPrviewScreenBinding
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class Prview_Screen : AppCompatActivity() {

    lateinit var binding: ActivityPrviewScreenBinding
    private var pdffilePath: String? = null
    private var fileName: String? = null
    private var fileType: String? = null
    private var db: AppDatabase? = null

    private val viewModel: PdfFileViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrviewScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Get color from resources
        val statusBarColor = ContextCompat.getColor(this@Prview_Screen, R.color.white)

        // Change status bar color
        com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils.changeStatusBarColor(
            statusBarColor,
            this@Prview_Screen
        )


//        val adRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest)


        // Initialize the database
        db = databaseBuilder<AppDatabase>(this@Prview_Screen, AppDatabase::class.java, "pdf_database").build()

        pdffilePath = intent.getStringExtra("pdffilePath")
        fileName = intent.getStringExtra("fileName")
        fileType = intent.getStringExtra("fileType")
        Log.d("1111121", "onCreate: pdffilePath: $pdffilePath")

        if (fileType.equals("signature")){
            binding.viewFileBtn.setBackground(getResources().getDrawable(R.drawable.btn_add_sign_text_ok_bg));


            val next = "<font color='#1F1F1F'>Location: </font>"
            binding.location.setText(Html.fromHtml( next+pdffilePath))
            binding.location.setTextColor(ContextCompat.getColor(this@Prview_Screen, R.color.green))


        }else{
            binding.viewFileBtn.setBackground(getResources().getDrawable(R.drawable.btn_add_text_ok_bg));
            val next = "<font color='#1F1F1F'>Location: </font>"
            binding.location.setText(Html.fromHtml( next+pdffilePath))
            binding.location.setTextColor(ContextCompat.getColor(this@Prview_Screen, R.color.red))
        }



        // Log a predefined event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("activity_name", "Prview_Screen")
        analytics.logEvent("activity_created", bundle)
// Using predefined Firebase Analytics events
        // Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Prview_Screen")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)



            binding.fileName.setText(fileName)


        loadThumbnailFromPdfFile(pdffilePath)





        // Observe PDF files
//        viewModel.allPdfFiles.observe(this) { pdfFiles ->
//
//           setDataWithDatabase(pdfFiles,pdffilePath,fileName)
//
//        }

        viewModel.getPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(this@Prview_Screen),"pdf").observe(this){ pdfFiles ->
            setDataWithDatabase(pdfFiles,pdffilePath,fileName)
            }




        binding.backButton.setOnClickListener {
//            val intent = Intent(this@Prview_Screen, MainActivity::class.java)
            val intent = Intent(this@Prview_Screen, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.homeBtn.setOnClickListener {
            val intent = Intent(this@Prview_Screen, HomeActivity::class.java)
            intent.putExtra("ScannerSide","PreviewSide")
            startActivity(intent)
            finish()
        }



    }

    private fun setDataWithDatabase(
        pdfFiles: List<PdfFile>,
        pdffilePath: String?,
        fileName: String?
    ) {


        pdfFiles.forEach { pdfFile ->
            if (pdfFile.fileName == this.fileName && pdfFile.filePath == this.pdffilePath) {
                Log.d("1111sdf121", "Database file name: ${pdfFile.fileName}")
                Log.d("1111sdf121", "Now saved fileName: ${this.fileName}")
                Log.d("1111sdf121", "Database filePath: ${pdfFile.filePath}")
                Log.d("1111sdf121", "Now saved pdffilePath: ${this.pdffilePath}")

                // Favorite button functionality
                binding.favrateBtn.setOnClickListener {
                    pdfFile.isFavorite = !pdfFile.isFavorite
                    viewModel.updatePdfFile(pdfFile)

                    if (fileType.equals("signature")){
                        if (pdfFile.isFavorite) {
                            Log.d("1111sdf121", "Set isFavorite to true")
                            binding.favrateBtn.setColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_ATOP)
                            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("1111sdf121", "Set isFavorite to false")
                            binding.favrateBtn.setColorFilter(ContextCompat.getColor(this, R.color.favrarte_prview_icon), PorterDuff.Mode.SRC_ATOP)
                            Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        if (pdfFile.isFavorite) {
                            Log.d("1111sdf121", "Set isFavorite to true")
                            binding.favrateBtn.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP)
                            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("1111sdf121", "Set isFavorite to false")
                            binding.favrateBtn.setColorFilter(ContextCompat.getColor(this, R.color.favrarte_prview_icon), PorterDuff.Mode.SRC_ATOP)
                            Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                        }
                    }


                }

                // View file functionality
                binding.viewFileBtn.setOnClickListener {
                    checkPasswordOrNot(pdfFile)

//                    val file1: File = File(pdfFile.filePath)
//
//                    // Example of loading PDF
//                    val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(file1, ParcelFileDescriptor.MODE_READ_ONLY))


                }

                // Share file functionality
                binding.shareBtn.setOnClickListener {
                    val file = File(pdfFile.filePath)
                    val contentUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
                    val shareIntent = ShareCompat.IntentBuilder.from(this)
                        .setStream(contentUri)
                        .setType("application/pdf")
                        .intent
                        .apply { addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }

                    if (shareIntent.resolveActivity(packageManager) != null) {
                        startActivity(shareIntent)
                    }
                }

                // Delete file functionality
                binding.deleteBtn.setOnClickListener {
                    val dialogRename = Dialog(this, R.style.renameDialogStyle)
                    dialogRename.setContentView(R.layout.delete_dailog)

                    dialogRename.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
                        dialogRename.dismiss()
                    }

                    dialogRename.findViewById<TextView>(R.id.deleteBtn).setOnClickListener {
                        viewModel.deletePdfFile(pdfFile)
                        val file = File(this.pdffilePath!!)
                        if (file.exists() && file.delete()) {
                            Log.d("Delete Operation", "File deleted successfully: ${this.pdffilePath}")
                        } else {
                            Log.d("Delete Operation", "Failed to delete file: ${this.pdffilePath}")
                        }
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    dialogRename.show()
                }

                // Rename file functionality
                binding.editFile.setOnClickListener {
                    renameDailog(pdfFile, this) // Adjust `renameDailog` if it still uses `db` directly
                }
            }
        }


    }

    private fun checkPasswordOrNot(pdfFile: PdfFile?) {
        if (pdfFile!!.password != null && !pdfFile.password.isEmpty()) {

            val alert: ViewPasswordDialog = ViewPasswordDialog(pdfFile)
            alert.showDialog(this@Prview_Screen)

        } else {
            openPdfFile(pdfFile.filePath,pdfFile)
        }
    }


    fun renameDailog(pdfFile: PdfFile, fragmentActivity: FragmentActivity) {
        val dialogRename = Dialog(fragmentActivity, R.style.renameDialogStyle)
        dialogRename.setContentView (R.layout.rename_dailog)

//        Window window = dialogRename.getWindow();
//        if (window != null) {
//            window.setGravity(Gravity.CENTER);
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Ensures no background
//        }
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
            val desiredWidth = screenWidth - 2 * FileUtils.dpToPx(fragmentActivity, 30)
            val params = dialogRename.window!!.attributes
            params.width = desiredWidth
            window.attributes = params
        }
        val pdfNewNameEt = dialogRename.findViewById<TextInputEditText>(R.id.pdfNewNameEt)
        val cancelBtn = dialogRename.findViewById<TextView>(R.id.cancelBtn)
        val renameBtn = dialogRename.findViewById<TextView>(R.id.renameBtn)
        val clearTextIcon = dialogRename.findViewById<ImageView>(R.id.clearTextIcon)

        clearTextIcon.setOnClickListener {
            pdfNewNameEt.setText("")
        }


        cancelBtn.setOnClickListener { dialogRename.dismiss() }
        val file = File(pdfFile.filePath)
        //get current name of the pdf document to show in the pdfNewNameEt EditText
        val previousName = "" + file.name
        Log.d(TAG, "pdfRename: previous Name$previousName")
        pdfNewNameEt.setText(binding.fileName.text)
        renameBtn.setOnClickListener {
            val newName = pdfNewNameEt.text.toString().trim { it <= ' ' }
            Log.d(TAG, "onClick: newName: $newName")
            if (newName.isEmpty()) {
                Toast.makeText(fragmentActivity, "Please enter a name for the PDF", Toast.LENGTH_SHORT).show()
            } else {
                try {



                    // Extract folder names

                    var newFile:File?=null

                    // Extract folder names
                    val folderName1 = FileUtils.getFolderName(file.path)

                    if (folderName1 == "ID Card") {
                         newFile = File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/ID Card/$newName.pdf")
                        file.renameTo(newFile)
                        viewModel.renamePdfFile(pdfFile.filePath, newFile.path)
//                        Thread {
//                            db.pdfFileDao().renameFile(pdfFile.filePath, newFile!!.path)
//                        }.start()
                        Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show()
                        dialogRename.dismiss()
                    } else if (folderName1 == "Business Card") {
                         newFile = File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/Business Card/$newName.pdf")
                        file.renameTo(newFile)

                        viewModel.renamePdfFile(pdfFile.filePath, newFile.path)
//                        Thread {
//                            db.pdfFileDao().renameFile(pdfFile.filePath, newFile!!.path)
//                        }.start()
                        Log.d("checkfilename","file name "+ newFile.name)
                        Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show()
                        dialogRename.dismiss()
                    } else if (folderName1 == "Academic Docs") {
                         newFile = File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/Academic Docs/$newName.pdf")
                        file.renameTo(newFile)
                        viewModel.renamePdfFile(pdfFile.filePath, newFile.path)

//                        Thread {
//                            db.pdfFileDao().renameFile(pdfFile.filePath, newFile!!.path)
//                        }.start()
                        Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show()
                        dialogRename.dismiss()
                    } else if (folderName1 == "Personal Tag") {
                         newFile = File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/Personal Tag/$newName.pdf")
                        file.renameTo(newFile)
                        viewModel.renamePdfFile(pdfFile.filePath, newFile.path)

//                        Thread {
//                            db.pdfFileDao().renameFile(pdfFile.filePath, newFile!!.path)
//                        }.start()
                        Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show()
                        dialogRename.dismiss()
                    } else if (folderName1 == "DOCUMENTS") {

                         newFile = File(fragmentActivity.getExternalFilesDir(null), Constant.PDF_FOLDER + "/" + newName + ".pdf")
                        file.renameTo(newFile)
                        viewModel.renamePdfFile(pdfFile.filePath, newFile.path)

//                        Thread { db.pdfFileDao().renameFile(pdfFile.filePath, newFile!!.path) }
//                            .start()
                        Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show()


                    } else if (folderName1 == "DigitalSignature") {
                        val root = fragmentActivity.filesDir
                         newFile = File("$root/DigitalSignature", newName+ ".pdf")
                        file.renameTo(newFile)
                        viewModel.renamePdfFile(pdfFile.filePath, newFile.path)

//                        Thread {
//                            db.pdfFileDao().renameFile(pdfFile.filePath, newFile.path)
//                        }.start()
                        Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show()
                    }

























                    binding.fileName.text = newFile!!.name




                    // Observe data from ViewModel
                    Handler().postDelayed({
//                        viewModel.allPdfFiles.observe(this) { pdfFiles ->
//
//                            pdfFiles.forEach { pdfFile ->
//                                val newFileName = File(newFile.name) // Adjust to your logic for `newFile`
//                                val previousName = File(pdfFile.filePath).name
//
//                                if (previousName == newFileName.name) {
//                                    Log.d("1111sdf121", "Database file name: ${pdfFile.fileName}")
//                                    Log.d("1111sdf121", "Now saved fileName: $fileName")
//                                    Log.d("1111sdf121", "Database filePath: ${pdfFile.filePath}")
//                                    Log.d("1111sdf121", "Now saved pdffilePath: $pdffilePath")
//
//                                    // Favorite button functionality
//                                    binding.favrateBtn.setOnClickListener {
//                                        pdfFile.isFavorite = !pdfFile.isFavorite
//                                        viewModel.updatePdfFile(pdfFile)
//                                        updateFavoriteButton(pdfFile.isFavorite)
//                                    }
//
//                                    // View file functionality
//                                    binding.viewFileBtn.setOnClickListener {
//                                        viewFile(pdfFile)
//                                    }
//
//                                    // Share file functionality
//                                    binding.shareBtn.setOnClickListener {
//                                        shareFile(pdfFile.filePath)
//                                    }
//
//                                    // Delete file functionality
//                                    binding.deleteBtn.setOnClickListener {
//                                        showDeleteDialog(pdfFile)
//                                    }
//
//                                    // Rename file functionality
//                                    binding.editFile.setOnClickListener {
//                                        renameDailog(pdfFile, this) // Adjust `renameDailog` if necessary
//                                    }
//                                }
//                            }
//                        }

                        viewModel.getPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(this@Prview_Screen),"pdf").observe(this){ pdfFiles ->
                            pdfFiles.forEach { pdfFile ->
                                val newFileName = File(newFile.name) // Adjust to your logic for `newFile`
                                val previousName = File(pdfFile.filePath).name

                                if (previousName == newFileName.name) {
                                    Log.d("1111sdf121", "Database file name: ${pdfFile.fileName}")
                                    Log.d("1111sdf121", "Now saved fileName: $fileName")
                                    Log.d("1111sdf121", "Database filePath: ${pdfFile.filePath}")
                                    Log.d("1111sdf121", "Now saved pdffilePath: $pdffilePath")

                                    // Favorite button functionality
                                    binding.favrateBtn.setOnClickListener {
                                        pdfFile.isFavorite = !pdfFile.isFavorite
                                        viewModel.updatePdfFile(pdfFile)
                                        updateFavoriteButton(pdfFile.isFavorite)
                                    }

                                    // View file functionality
                                    binding.viewFileBtn.setOnClickListener {
                                        viewFile(pdfFile)
                                    }

                                    // Share file functionality
                                    binding.shareBtn.setOnClickListener {
                                        shareFile(pdfFile.filePath)
                                    }

                                    // Delete file functionality
                                    binding.deleteBtn.setOnClickListener {
                                        showDeleteDialog(pdfFile)
                                    }

                                    // Rename file functionality
                                    binding.editFile.setOnClickListener {
                                        renameDailog(pdfFile, this) // Adjust `renameDailog` if necessary
                                    }
                                }
                            }
                        }


                    }, 1000)


                    dialogRename.dismiss()
                } catch (e: java.lang.Exception) {
                    Log.d(TAG, "renameBtn onClick: ", e)
//                    Toast.makeText(fragmentActivity, "Failed to rename due to " + e.message, Toast.LENGTH_SHORT).show()
                }
            }







        }
        dialogRename.show()







    }

    private fun updateFavoriteButton(isFavorite: Boolean) {

        if (fileType.equals("signature")){
            val color = if (isFavorite) R.color.green else R.color.favrarte_prview_icon
            binding.favrateBtn.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_ATOP)
            val message = if (isFavorite) "Added to Favorites" else "Removed from Favorites"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }else{
            val color = if (isFavorite) R.color.red else R.color.favrarte_prview_icon
            binding.favrateBtn.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_ATOP)
            val message = if (isFavorite) "Added to Favorites" else "Removed from Favorites"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }




    }

    private fun showDeleteDialog(pdfFile: PdfFile) {
        val dialog = Dialog(this, R.style.renameDialogStyle)
        dialog.setContentView(R.layout.delete_dailog)

        dialog.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<TextView>(R.id.deleteBtn).setOnClickListener {
            viewModel.deletePdfFile(pdfFile)
            deleteFileFromStorage(pdfFile.filePath)
            navigateToHome()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteFileFromStorage(filePath: String?) {
        val file = File(filePath.toString())
        if (file.exists() && file.delete()) {
            Log.d("Delete Operation", "File deleted successfully: $filePath")
        } else {
            Log.d("Delete Operation", "Failed to delete file: $filePath")
        }
    }

    private fun shareFile(filePath: String?) {
        val file = File(filePath.toString())
        val contentUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setStream(contentUri)
            .setType("application/pdf")
            .intent
            .apply { addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }

        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(shareIntent)
        }
    }





    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }




//    private fun showInterstitialAd() {
//        assert(manager != null)
//        if (manager!!.isInterstitialReady) {
//            manager!!.showInterstitial(this@Prview_Screen, callback_intertial)
//        } else {
//            Log.d("CAS", "Interstitial ad not ready to be shown.")
//        }
//    }

    private fun viewFile(pdfFile: PdfFile) {
        if (pdfFile.password != null && pdfFile.password.isNotEmpty()) {
//

//                            showPasswordDialog(pdfFile);
            val alert: ViewPasswordDialog = ViewPasswordDialog(pdfFile)
            alert.showDialog(this@Prview_Screen)

        } else {
            openPdfFile(pdfFile.filePath, pdfFile)
        }
    }


    class ViewPasswordDialog(var pdfFile: PdfFile) {
        fun showDialog(activity: Context) {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.lock_dialog)
            val passwordEt = dialog.findViewById<TextInputEditText>(R.id.passwordEt)
            val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)
            val saveBtn = dialog.findViewById<TextView>(R.id.saveBtn)
            val rateUsBtn = dialog.findViewById<TextView>(R.id.rateUsBtn)


            // Set up the input
            passwordEt.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            cancelBtn.setOnClickListener { dialog.dismiss() }
            saveBtn.setOnClickListener {
                val enteredPassword = passwordEt.text.toString()
                if (enteredPassword == pdfFile.password) {
                    openPdfFile(pdfFile.filePath,activity)
                    dialog.dismiss()
                } else {
                    Toast.makeText(activity, "Incorrect password", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            if (dialog.window != null) {
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                val metrics = Resources.getSystem().displayMetrics
                val screenWidth = metrics.widthPixels
                val desiredWidth = screenWidth - 2 * dpToPx(activity, 0)
                val params = dialog.window!!.attributes
                params.width = desiredWidth
                dialog.window!!.attributes = params
            }
            dialog.show()
        }

        private fun openPdfFile(filePath: String?, activity: Context) {

            val intent = Intent(activity, PdfViewActivity::class.java)
            intent.putExtra("pdfUri", "" + Uri.fromFile(File(filePath.toString())))
            activity.startActivity(intent)


        }


        private fun openPlayStore(context: Context) {
            val packageName = context.packageName
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }

        private fun dpToPx(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }


    private fun openPdfFile(filePath: String?, pdfFile: PdfFile) {


        if (pdfFile.fileTag == "protect doc") {
            val file = File(filePath) // Replace with your PDF file path
            val uri = FileProvider.getUriForFile(
                this@Prview_Screen,
                getPackageName() + ".provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this@Prview_Screen, "No PDF viewer found", Toast.LENGTH_SHORT).show()
            }
        } else {
            val intent = Intent(this@Prview_Screen, PdfViewActivity::class.java)
            intent.putExtra("pdfUri", "" + Uri.fromFile(File(filePath)))
            startActivity(intent)
        }


//        val intent = Intent(applicationContext, ActivityDigiSign::class.java)
//        intent.putExtra("ActivityAction", "Preview")
//        intent.putExtra("preview_file_path", filePath)
//
//        startActivity(intent)

        Log.d("PdfViewActivity11", "openPdfFile")
    }


    private fun loadThumbnailFromPdfFile(filePath: String?) {
        Log.d(TAG, "loadThumbnailFromPdfFile: $filePath")

        try {
            val file = File(filePath.toString())
            if (!file.exists()) {
                throw IOException("File does not exist at path: $filePath")
            }
            val fileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            val page = pdfRenderer.openPage(0)

            // Increase the width and height for better quality
            val originalWidth = page.width
            val originalHeight = page.height
            val scaleFactor =
                2 // Adjust this factor to increase the quality (2 means double the resolution)
            val width = originalWidth * scaleFactor
            val height = originalHeight * scaleFactor
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // Create a matrix for the transformation
            val matrix = Matrix()
            matrix.setScale(scaleFactor.toFloat(), scaleFactor.toFloat())

            // Render the page onto the bitmap
            page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            pdfRenderer.close()
            fileDescriptor.close()
            Glide.with(this@Prview_Screen)
                .load(bitmap)
                .fitCenter()
                .into(binding.image)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Error loading PDF thumbnail: " + e.message)
        }



    }

//    private fun loadThumbnailFromPdfFile(filePath: String?) {
//        Log.d(TAG, "loadThumbnailFromPdfFile: $filePath")
//
//        try {
//            val file = File(filePath)
//            if (!file.exists()) {
//                throw IOException("File does not exist at path: $filePath")
//            }
//
//            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
//            val pdfRenderer = PdfRenderer(fileDescriptor)
//            val page = pdfRenderer.openPage(0)
//
//            // Calculate scale factor dynamically based on target thumbnail size
//            val targetWidth = 500 // Target width for the thumbnail
//            val scaleFactor = targetWidth.toFloat() / page.width.toFloat()
//            val width = (page.width * scaleFactor).toInt()
//            val height = (page.height * scaleFactor).toInt()
//
//            // Use Bitmap.Config.RGB_565 for lower memory usage
//            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//
//            // Render the page onto the bitmap
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//
//            // Close the page and pdfRenderer
//            page.close()
//            pdfRenderer.close()
//            fileDescriptor.close()
//
//            // Load the bitmap into the ImageView using Glide
//            Glide.with(this@Prview_Screen)
//                .load(bitmap)
//                .fitCenter()
//                .into(binding.image)
//
//            // Recycle bitmap after loading into ImageView
//            bitmap.recycle()
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Log.e(TAG, "Error loading PDF thumbnail: ${e.message}")
//        } catch (e: OutOfMemoryError) {
//            e.printStackTrace()
//            Log.e(TAG, "OutOfMemoryError while loading PDF thumbnail: ${e.message}")
//            // Optionally, show a placeholder or handle low memory gracefully
//        }
//    }


    override fun onBackPressed() {
        val intent = Intent(this@Prview_Screen, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//    }


}