package com.pixelz360.docsign.imagetopdf.creator.all_document_viewer


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.cherry.doc.DocAdapter
import com.cherry.doc.util.DocUtil
import com.cherry.lib.doc.DocViewerActivity
import com.cherry.lib.doc.bean.DocSourceType
import com.cherry.lib.doc.bean.FileType
import com.cherry.lib.doc.util.FileUtils
import com.cherry.permissions.lib.EasyPermissions
import com.cherry.permissions.lib.EasyPermissions.hasPermissions
import com.cherry.permissions.lib.annotations.AfterPermissionGranted
import com.cherry.permissions.lib.dialogs.SettingsDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.UtilString
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.FileUtils.dpToPx
import com.pixelz360.docsign.imagetopdf.creator.all_document_viewer.util.BasicSet
import com.pixelz360.docsign.imagetopdf.creator.all_document_viewer.util.WordUtils
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityAllDocMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.sf.sevenzipjbinding.ArchiveFormat
import net.sf.sevenzipjbinding.ExtractAskMode
import net.sf.sevenzipjbinding.ExtractOperationResult
import net.sf.sevenzipjbinding.IArchiveExtractCallback
import net.sf.sevenzipjbinding.ICryptoGetTextPassword
import net.sf.sevenzipjbinding.IInArchive
import net.sf.sevenzipjbinding.ISequentialOutStream
import net.sf.sevenzipjbinding.PropID
import net.sf.sevenzipjbinding.SevenZip
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.RandomAccessFile


class AllDocMainActivity : AppCompatActivity(),OnClickListener,OnItemClickListener, EasyPermissions.PermissionCallbacks {
    companion object {
        const val REQUEST_CODE_STORAGE_PERMISSION = 124
        const val REQUEST_CODE_STORAGE_PERMISSION11 = 125
        const val REQUEST_CODE_SELECT_DOCUMENT = 0x100
        const val TAG = "AllDocMainActivity"
    }
//    var url = "http://cdn07.foxitsoftware.cn/pub/foxit/manual/phantom/en_us/API%20Reference%20for%20Application%20Communication.pdf"
//    var url = "https://xdts.xdocin.com/demo/resume3.docx"
//    var url = "http://172.16.28.95:8080/data/test2.ppt"
//    var url = "http://172.16.28.95:8080/data/testdocx.ll"

    var mDocAdapter: DocAdapter? = null

    lateinit var binding:ActivityAllDocMainBinding

    private var selectedFilePath: String? = null
    private lateinit var progressDialog: ProgressDialog
    private var isRAR = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            SevenZip.initSevenZipFromPlatformJAR() // ✅ This loads platform libraries
            Log.d("checkfileuri", "SevenZipJBinding initialized successfully!")
        } catch (e: Exception) {
            Log.e("checkfileuri", "Error initializing SevenZipJBinding: ${e.message}")

            // ✅ Try manual initialization as a fallback
            try {
                SevenZip.initSevenZipFromPlatformJAR("lib/")
                Log.d("checkfileuri", "SevenZipJBinding manually initialized!")
            } catch (ex: Exception) {
                Log.e("checkfileuri", "Manual initialization failed: ${ex.message}")
            }
        }




        val uri: Uri? = intent?.data
        if (uri != null) {
            val mimeType = contentResolver.getType(uri) ?: ""
            val fileName = getFileNameFromUri(uri) // Get actual file name

            when {


                mimeType.equals("application/pdf", ignoreCase = true) -> openFile(uri)
                mimeType.equals("application/msword", ignoreCase = true) ||
                        mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ignoreCase = true) -> openFile(uri)
                mimeType.equals("application/vnd.ms-excel", ignoreCase = true) ||
                        mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ignoreCase = true) -> openFile(uri)
                mimeType.equals("application/vnd.ms-powerpoint", ignoreCase = true) ||
                        mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation", ignoreCase = true) -> openFile(uri)
                mimeType.equals("text/plain", ignoreCase = true) -> openFile(uri)
                mimeType.equals("text/html", ignoreCase = true) -> openFile(uri)
                mimeType.equals("application/zip", ignoreCase = true) || fileName.endsWith(".zip", ignoreCase = true) -> unZipFile(uri)
                mimeType.equals("application/vnd.rar", ignoreCase = true) ||
                        mimeType.equals("application/x-rar-compressed", ignoreCase = true) ||
                        mimeType.equals("application/octet-stream", ignoreCase = true) ||
                        fileName.endsWith(".rar", ignoreCase = true) -> unRarFile(uri)   // ✅ Check file extension
                mimeType.equals("application/x-7z-compressed", ignoreCase = true) || fileName.endsWith(".7z", ignoreCase = true) -> {
                    un7zFile(uri)
                    }

                else -> Log.d("checkfileuri", "Unsupported file type: $mimeType, Filename: $fileName")
            }

        }


        binding = ActivityAllDocMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        initView()
//        initData()
    }

    private fun un7zFile(uri: Uri) {
        uri?.let {
            selectedFilePath = getFileFromUri(uri)

            // Convert URI to File Path
            selectedFilePath = getFileFromUri(uri)
            val extension: String = selectedFilePath!!.substring(selectedFilePath!!.lastIndexOf("."))

            Log.d("checkfileuri", "extension: $extension")

            if (selectedFilePath != null && extension.equals(".7z", ignoreCase = true)) {
                show7zFolderNameDialog()
            } else {
                Toast.makeText(this, "Invalid 7z file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun show7zFolderNameDialog() {

        val editText = EditText(this)
        editText.hint = "Enter folder name"

        AlertDialog.Builder(this)
            .setTitle("Set Folder Name")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val folderName = editText.text.toString().trim()
                if (folderName.isNotEmpty() && selectedFilePath != null) {
                    extract7zWithProgress(selectedFilePath!!, folderName)
                } else {
                    Toast.makeText(this, "Folder name cannot be empty!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()



    }

    private fun extract7zWithProgress(archivePath: String, folderName: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Extracting 7z...")
        progressDialog.setMessage("Please wait while extracting files")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val outputFolder = File(downloadsDir, folderName)

                if (!outputFolder.exists()) outputFolder.mkdirs()

                val inputArchive = RandomAccessFile(archivePath, "r")
                val inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, RandomAccessFileInStream(inputArchive))
                val totalItems = inArchive.numberOfItems

                inArchive.extract(null, false, object : IArchiveExtractCallback {
                    override fun setTotal(total: Long) {}

                    override fun setCompleted(complete: Long) {
                        val progress = ((complete.toDouble() / totalItems) * 100).toInt()
                        progressDialog.progress = progress
                    }

                    override fun getStream(index: Int, mode: ExtractAskMode?): ISequentialOutStream {
                        val fileName = inArchive.getStringProperty(index, PropID.PATH)
                        val isFolder = inArchive.getProperty(index, PropID.IS_FOLDER) as Boolean
                        val extractedFile = File(outputFolder, fileName)

                        if (isFolder) {
                            extractedFile.mkdirs()
                        } else {
                            extractedFile.parentFile?.mkdirs()
                            val outputStream = FileOutputStream(extractedFile)
                            return ISequentialOutStream { data -> outputStream.write(data); data.size }
                        }
                        return ISequentialOutStream { 0 }
                    }

                    override fun setOperationResult(result: ExtractOperationResult?) {}
                    override fun prepareOperation(mode: ExtractAskMode?) {}
                })

                inArchive.close()
                inputArchive.close()

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@AllDocMainActivity, "Extracted to: ${outputFolder.absolutePath}", Toast.LENGTH_LONG).show()
                    showSnackbar(this@AllDocMainActivity, "Extracted to: ${outputFolder.absolutePath}") {
                        openFolder(outputFolder)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@AllDocMainActivity, "Error extracting 7z file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun getFileNameFromUri(uri: Uri): String {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown"
    }


    private fun unZipFile(uri: Uri) {
        uri?.let {
            selectedFilePath = getFileFromUri(uri)

            // Convert URI to File Path
            selectedFilePath = getFileFromUri(uri)
            val extension: String = selectedFilePath!!.substring(selectedFilePath!!.lastIndexOf("."))


            Log.d("checkfileuri", "extension   "+extension)

            if (selectedFilePath != null) {
//                isRAR = selectedFilePath!!.endsWith(".rar", ignoreCase = true) // Check if it's a RAR file
                showFolderNameDialog()
            } else {
                Toast.makeText(this, "Error getting file path", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun unRarFile(uri: Uri) {
        uri?.let {
            selectedFilePath = getFileFromUri(uri)

            // Convert URI to File Path
            selectedFilePath = getFileFromUri(uri)
            val extension: String = selectedFilePath!!.substring(selectedFilePath!!.lastIndexOf("."))


            Log.d("checkfileuri", "extension   "+extension)

            if (selectedFilePath != null) {
                isRAR = selectedFilePath!!.endsWith(".rar", ignoreCase = true) // Check if it's a RAR file
                showFolderRarNameDialog()
            } else {
                Toast.makeText(this, "Error getting file path", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // 🔥 Show Dialog to Enter Folder Name
    private fun showFolderRarNameDialog() {
//        val editText = EditText(this)
//        editText.hint = "Enter folder name"
//
//        AlertDialog.Builder(this)
//            .setTitle("Set Folder Name")
//            .setView(editText)
//            .setPositiveButton("OK") { _, _ ->
//                val folderName = editText.text.toString().trim()
//                if (folderName.isNotEmpty() && selectedFilePath != null) {
//                    extractRARWithProgress(selectedFilePath!!, folderName)
//
//                } else {
//                    Toast.makeText(this, "Folder name cannot be empty!", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()







        // Declare a class-level variable for the dialog
        var unRarFileFolderNameDialog: Dialog? = null


        // Create and set up the dialog
        unRarFileFolderNameDialog = Dialog(this@AllDocMainActivity, R.style.renameDialogStyle).apply {
            setContentView(R.layout.zip_folder_name_dailog)
            Log.d("checkdouble", "dialog show")

            val window = window
            setCancelable(false)
            window?.let {
                it.setGravity(Gravity.CENTER)
                it.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                it.setBackgroundDrawableResource(android.R.color.transparent)
                val metrics = Resources.getSystem().displayMetrics
                val screenWidth = metrics.widthPixels
                val desiredWidth = screenWidth - 2 * dpToPx(this@AllDocMainActivity, 30)
                val params = it.attributes
                params.width = desiredWidth
                it.attributes = params
            }

            val pdfNewNameEt = findViewById<TextInputEditText>(R.id.pdfNewNameEt)
            val renameBtn = findViewById<TextView>(R.id.renameBtn)
            val cancelBtn = findViewById<TextView>(R.id.cancelBtn)
            val title = findViewById<TextView>(R.id.title)
            val clearTextIcon = findViewById<ImageView>(R.id.clearTextIcon)

            title.text = "Extract RAR Folder Name"




            // Clear text functionality
            clearTextIcon.setOnClickListener {
                pdfNewNameEt.setText("")
            }

            // Set initial text
//            pdfNewNameEt.setText(renameFileName[0])

            // Cancel button resets the dialog reference
            cancelBtn.setOnClickListener {
                dismiss()
                Log.d("checkdouble", "dialog dismissed, ready to open again")
            }

            // Rename button functionality
            renameBtn.setOnClickListener {
                val folderName = pdfNewNameEt.text.toString().trim()
                if (folderName.isNotEmpty() && selectedFilePath != null) {
                    extractRARWithProgress(selectedFilePath!!, folderName)

                } else {
                    Toast.makeText(this@AllDocMainActivity, "Folder name cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                unRarFileFolderNameDialog?.dismiss()
            }

            // Show the dialog
            show()
        }







    }


    // 🔥 Show Dialog to Enter Folder Name
    private fun showFolderNameDialog() {
//        val editText = EditText(this)
//        editText.hint = "Enter folder name"
//
//        AlertDialog.Builder(this)
//            .setTitle("Set Folder Name")
//            .setView(editText)
//            .setPositiveButton("OK") { _, _ ->
//                val folderName = editText.text.toString().trim()
//                if (folderName.isNotEmpty() && selectedFilePath != null) {
//                    extractZIPWithProgress(selectedFilePath!!, folderName)
//
//                } else {
//                    Toast.makeText(this, "Folder name cannot be empty!", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()




        // Declare a class-level variable for the dialog
        var unZipFileFolderNameDialog: Dialog? = null


        // Create and set up the dialog
        unZipFileFolderNameDialog = Dialog(this@AllDocMainActivity, R.style.renameDialogStyle).apply {
            setContentView(R.layout.zip_folder_name_dailog)
            Log.d("checkdouble", "dialog show")

            val window = window
            setCancelable(false)
            window?.let {
                it.setGravity(Gravity.CENTER)
                it.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                it.setBackgroundDrawableResource(android.R.color.transparent)
                val metrics = Resources.getSystem().displayMetrics
                val screenWidth = metrics.widthPixels
                val desiredWidth = screenWidth - 2 * dpToPx(this@AllDocMainActivity, 30)
                val params = it.attributes
                params.width = desiredWidth
                it.attributes = params
            }

            val pdfNewNameEt = findViewById<TextInputEditText>(R.id.pdfNewNameEt)
            val renameBtn = findViewById<TextView>(R.id.renameBtn)
            val cancelBtn = findViewById<TextView>(R.id.cancelBtn)
            val title = findViewById<TextView>(R.id.title)
            val clearTextIcon = findViewById<ImageView>(R.id.clearTextIcon)


            title.text = "unZip Folder Name"



            // Clear text functionality
            clearTextIcon.setOnClickListener {
                pdfNewNameEt.setText("")
            }

            // Set initial text
//            pdfNewNameEt.setText(renameFileName[0])

            // Cancel button resets the dialog reference
            cancelBtn.setOnClickListener {
                dismiss()
                Log.d("checkdouble", "dialog dismissed, ready to open again")
            }

            // Rename button functionality
            renameBtn.setOnClickListener {
                val folderName = pdfNewNameEt.text.toString().trim()
                if (folderName.isNotEmpty() && selectedFilePath != null) {
                    extractZIPWithProgress(selectedFilePath!!, folderName)

                } else {
                    Toast.makeText(this@AllDocMainActivity, "Folder name cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                unZipFileFolderNameDialog?.dismiss()
            }

            // Show the dialog
            show()
        }









    }



    // ✅ Extract ZIP File with Progress
    private fun extractZIPWithProgress(zipFilePath: String, folderName: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Extracting ZIP...")
        progressDialog.setMessage("Please wait while extracting files")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val outputFolder = File(downloadsDir, folderName)

                if (!outputFolder.exists()) outputFolder.mkdirs()

                val zip = ZipFile(zipFilePath)
                zip.extractAll(outputFolder.absolutePath)

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@AllDocMainActivity, "Unzipped to: ${outputFolder.absolutePath}", Toast.LENGTH_LONG).show()

//                    UtilString.getInstance().showSnackbar(this@AllDocMainActivity, "${outputFolder.absolutePath}")

                    // ✅ Show Snackbar with "Open" button
                    showSnackbar(
                        this@AllDocMainActivity, "Extracted to: ${outputFolder.absolutePath}"
                    ) {
                        openFolder(outputFolder)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@AllDocMainActivity, "Error unzipping file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    private fun openFolder(folder: File) {
//        val uri = Uri.parse(folder.absolutePath)
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.setDataAndType(uri, "resource/folder")
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        try {
//            startActivity(intent)
//        } catch (e: Exception) {
//            Toast.makeText(this, "Cannot open folder", Toast.LENGTH_SHORT).show()
//        }
//    }



    private fun openFolder(folder: File) {


        startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));


//        try {
//            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//
//            if (!folder.exists()) {
//                Toast.makeText(this, "Folder does not exist!", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                // ✅ Android 10+ (Scoped Storage): Open Downloads and highlight the extracted folder
//                val folderPath = folder.absolutePath.substringAfter(downloadsFolder.absolutePath + "/")
//                DocumentsContract.buildDocumentUri(
//                    "com.android.externalstorage.documents",
//                    "primary:Download/$folderPath"
//                )
//            } else {
//                // ✅ Android 9 and below: Open folder directly
//                Uri.fromFile(folder)
//            }
//
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                setDataAndType(uri, "*/*")
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//
//            startActivity(intent)
//
//        } catch (e: ActivityNotFoundException) {
//            Toast.makeText(this, "File Manager not found", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        } catch (e: Exception) {
//            Toast.makeText(this, "Cannot open Downloads folder", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
    }

    fun showSnackbar(activity: Activity, message: String, action: (() -> Unit)? = null) {
        val snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        if (action != null) {
            snackbar.setAction("Open") { action.invoke() }
        }
        snackbar.show()
    }

    private fun extractRARWithProgress(rarFilePath: String, folderName: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Extracting RAR...")
        progressDialog.setMessage("Please wait while extracting files")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val outputFolder = File(downloadsDir, folderName)

                if (!outputFolder.exists()) outputFolder.mkdirs()

                val rarFile = File(rarFilePath)
                val randomAccessFile = RandomAccessFile(rarFile, "r")

                // ✅ Open RAR archive
                val inArchive: IInArchive = SevenZip.openInArchive(ArchiveFormat.RAR, RandomAccessFileInStream(randomAccessFile))
                Log.d("checkfileuri", "RAR Archive Opened: $rarFilePath")
                Log.d("checkfileuri", "Number of items in archive: ${inArchive.numberOfItems}")

                var extractionSuccess = true  // ✅ Track extraction result

                // ✅ Extract with proper error handling
                inArchive.extract(null, false, object : IArchiveExtractCallback, ICryptoGetTextPassword {
                    override fun setTotal(total: Long) {
                        Log.d("checkfileuri", "Total files to extract: $total")
                    }

                    override fun setCompleted(complete: Long) {
                        Log.d("checkfileuri", "Extraction progress: $complete files extracted")
                    }

                    override fun prepareOperation(mode: ExtractAskMode?) {}

                    override fun getStream(index: Int, mode: ExtractAskMode?): ISequentialOutStream {
                        val fileName = inArchive.getStringProperty(index, PropID.PATH)
                        val isDir = inArchive.getProperty(index, PropID.IS_FOLDER) as Boolean
                        val file = File(outputFolder, fileName)

                        Log.d("checkfileuri", "Extracting: $fileName")

                        return if (isDir) {
                            file.mkdirs()
                            ISequentialOutStream { 0 }
                        } else {
                            file.parentFile?.mkdirs()
                            val outputStream = FileOutputStream(file)
                            ISequentialOutStream { data ->
                                outputStream.write(data)
                                data.size
                            }
                        }
                    }

                    override fun setOperationResult(result: ExtractOperationResult?) {
                        if (result != ExtractOperationResult.OK) {
                            Log.e("checkfileuri", "Extraction failed for some files: $result")
                            extractionSuccess = false  // ✅ Mark failure
                        }
                    }

                    override fun cryptoGetTextPassword(): String {
                        Log.e("checkfileuri", "Password required but not provided!")
                        return "" // Empty string means no password; modify to prompt user if needed.
                    }
                })

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (extractionSuccess) {
//                        UtilString.getInstance().showSnackbar(this@AllDocMainActivity, "Extracted to: ${outputFolder.absolutePath}")
                        Log.d("checkfileuri", "Extraction successful: ${outputFolder.absolutePath}")


                        // ✅ Show Snackbar with "Open" button
                        showSnackbar(
                            this@AllDocMainActivity, "Extracted to: ${outputFolder.absolutePath}"
                        ) {
                            openFolder(outputFolder)
                        }


                    } else {
                        Toast.makeText(this@AllDocMainActivity, "Extraction failed for some files", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("checkfileuri", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@AllDocMainActivity, "Error extracting RAR file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // Convert content:// URI to File Path
    private fun getFileFromUri(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "temp.${if (isRAR) "rar" else "zip"}")

            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun openFile(documentUri: Uri) {

        documentUri?.let {
            openDoc(it.toString(), DocSourceType.URI, null)
            Log.d("checkfileuri","openDoc"+it.toString())
        }



    }

    private fun hasRwPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val isExternalStorageManager = Environment.isExternalStorageManager()
            return isExternalStorageManager
        }
        val read = hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return read && write
    }

    @AfterPermissionGranted(REQUEST_CODE_STORAGE_PERMISSION)
    private fun requestStoragePermission() {
        if (hasRwPermission()) {
            // Have permission, do things!
            CoroutineScope(Dispatchers.IO).launch {
                var datas = DocUtil.getDocFile(this@AllDocMainActivity)
                CoroutineScope(Dispatchers.Main).launch {
                    mDocAdapter?.showDatas(datas)
                }
            }
        } else {
            // Ask for one permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                get11Permission()
                return
            }
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your storage to load local doc",
                REQUEST_CODE_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun get11Permission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(java.lang.String.format("package:%s", packageName))
            startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION11)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION11)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_all_doc, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_assets -> {
                openDoc("test.docx",DocSourceType.ASSETS)
                return true
            }
            R.id.action_online -> {
//                openDoc(url,DocSourceType.URL,null)
                return true
            }
            R.id.action_select -> {
                // 使用Intent打开文件管理器并选择文档

                // 使用Intent打开文件管理器并选择文档
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("*/*") // 设置要选择的文件类型，此处为任意文件类型

                startActivityForResult(intent, REQUEST_CODE_SELECT_DOCUMENT) // 启动Activity并设置请求码
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun initView() {
//        setSupportActionBar(toolbar)

        mDocAdapter = DocAdapter(this,this)
        binding.mRvDoc.adapter = mDocAdapter
    }

    fun initData() {
        requestStoragePermission()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    fun checkSupport(path: String): Boolean {
        var fileType = FileUtils.getFileTypeForUrl(path)
        Log.e(javaClass.simpleName,"fileType = $fileType")
        if (fileType == FileType.NOT_SUPPORT) {
            return false
        }
        return true
    }

    fun openDoc(path: String,docSourceType: Int,type: Int? = null) {
        DocViewerActivity.launchDocViewer(this,docSourceType,path,type)
        finish()

        Log.d("checkpagth","click openDoc "+path)
    }

    override fun onItemClick(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
        when (v?.id) {
            R.id.mCvDocCell -> {
                val groupInfo = mDocAdapter?.datas?.get(id.toInt())
                val docInfo = groupInfo?.docList?.get(position)
                var path = docInfo?.path ?: ""
                if (checkSupport(path)) {
                    openDoc(path,DocSourceType.PATH)
                }

//                word2Html(path)
//                WordActivity.launchDocViewer(this,path)
            }
        }
    }

    fun word2Html(sourceFilePath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val htmlFilePath = cacheDir.absolutePath + "/html"
            val htmlFileName = "word_pdf"

            var bs = BasicSet(this@AllDocMainActivity,sourceFilePath,htmlFilePath, htmlFileName)
            bs.picturePath = htmlFilePath

            WordUtils.getInstance(bs).word2html()

            CoroutineScope(Dispatchers.Main).launch {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION11) {
            if (hasRwPermission()) {
                requestStoragePermission()
            }
        } else if (requestCode == REQUEST_CODE_SELECT_DOCUMENT && resultCode == RESULT_OK) {
            val documentUri = data?.data
            Log.d(TAG, "documentUri = $documentUri")
            documentUri?.let {
                openDoc(it.toString(), DocSourceType.URI, null)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    // ============================================================================================
    //  Implementation Permission Callbacks
    // ============================================================================================

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // 会回调 AfterPermissionGranted注解对应方法
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            val settingsDialogBuilder = SettingsDialog.Builder(this)

            when(requestCode) {
                REQUEST_CODE_STORAGE_PERMISSION -> {
                    settingsDialogBuilder.title = getString(
                        com.cherry.permissions.lib.R.string.title_settings_dialog,
                        "Storage Permission")
                    settingsDialogBuilder.rationale = getString(
                        com.cherry.permissions.lib.R.string.rationale_ask_again,
                        "Storage Permission")
                }
            }

            settingsDialogBuilder.build().show()
        }

    }


}