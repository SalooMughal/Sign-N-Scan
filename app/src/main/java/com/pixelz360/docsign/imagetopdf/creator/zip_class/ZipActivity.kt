package com.pixelz360.docsign.imagetopdf.creator.zip_class

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.FileUtils.dpToPx
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ZipActivity : AppCompatActivity() {

    private lateinit var btnSelectFiles: Button
    private lateinit var tvSelectedFiles: TextView
    private var selectedFiles = mutableListOf<File>()
    private lateinit var progressDialog: ProgressDialog
    private val FILE_SELECT_CODE = 100
    val FOLDER_SELECT_CODE = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zip)

        btnSelectFiles = findViewById(R.id.btnSelectFiles)
        tvSelectedFiles = findViewById(R.id.tvSelectedFiles)

        btnSelectFiles.setOnClickListener {
            selectFilesAndFolders()
        }
    }

    // ✅ Open File Picker to select multiple files & folders
// ✅ Open File Picker to select multiple files & folders

    private fun selectFilesAndFolders() {
//        val options = arrayOf("Select Files", "Select Multiple Folders")
//        AlertDialog.Builder(this)
//            .setTitle("Choose Selection Mode")
//            .setItems(options) { _, which ->
//                when (which) {
//                    0 -> selectFiles()  // Select multiple files
//                    1 -> showFolderSelectionDialog()  // Show all folders for multi-selection
//                }
//            }
//            .show()

        selectFiles()  // Select multiple files

    }

    // ✅ Show dialog to select multiple folders
    private fun showFolderSelectionDialog() {
        val folders = getAllFolders()

        if (folders.isEmpty()) {
            Toast.makeText(this, "No folders found!", Toast.LENGTH_SHORT).show()
            return
        }

        val folderNames = folders.map { it.name }.toTypedArray()
        val selectedItems = BooleanArray(folders.size)

        AlertDialog.Builder(this)
            .setTitle("Select Folders")
            .setMultiChoiceItems(folderNames, selectedItems) { _, which, isChecked ->
                selectedItems[which] = isChecked
            }
            .setPositiveButton("OK") { _, _ ->
                selectedFiles.clear()

                for (i in selectedItems.indices) {
                    if (selectedItems[i]) {
                        val selectedFolder = folders[i]
                        selectedFiles.add(selectedFolder)
                        addFolderContentsToSelection(selectedFolder) // ✅ Add folder contents
                    }
                }

                if (selectedFiles.isNotEmpty()) {
                    tvSelectedFiles.text = "Selected Folders: ${selectedFiles.joinToString { it.name }}"
                    showZipFolderNameDialog()
                } else {
                    Toast.makeText(this, "No folders selected", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    // ✅ Get all folders from storage
    private fun getAllFolders(): List<File> {
        val storageDir = Environment.getExternalStorageDirectory()
        return storageDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
    }



    // ✅ Select multiple files
    private fun selectFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "*/*"

        try {
            startActivityForResult(Intent.createChooser(intent, "Select Files"), FILE_SELECT_CODE)
        } catch (ex: Exception) {
            Log.e("FilePicker", "Error opening file picker", ex)
        }
    }

    // ✅ Select a folder (Full Folder Selection)
    private fun selectFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        try {
            startActivityForResult(intent, FOLDER_SELECT_CODE)
        } catch (ex: Exception) {
            Log.e("FolderPicker", "Error opening folder picker", ex)
        }
    }


    // ✅ Handle File Selection Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FILE_SELECT_CODE -> { // ✅ User selected files
                    selectedFiles.clear()
                    data?.let {
                        if (it.clipData != null) {
                            for (i in 0 until it.clipData!!.itemCount) {
                                val uri = it.clipData!!.getItemAt(i).uri
                                val filePath = getFileFromUri(uri)
                                if (filePath != null) selectedFiles.add(File(filePath))
                            }
                        } else if (it.data != null) {
                            val filePath = getFileFromUri(it.data!!)
                            if (filePath != null) selectedFiles.add(File(filePath))
                        }
                    }
                }

                FOLDER_SELECT_CODE -> { // ✅ User selected a folder
                    val folderUri = data?.data
                    if (folderUri != null) {
                        val folderPath = getFolderPath(folderUri)
                        if (folderPath != null) {
                            selectedFiles.add(File(folderPath))
                            addFolderContentsToSelection(File(folderPath)) // 🔥 Add folder contents
                        }
                    }
                }
            }

            if (selectedFiles.isNotEmpty()) {
                tvSelectedFiles.text = "Selected: ${selectedFiles.joinToString { it.name }}"
                showZipFolderNameDialog()
            } else {
                Toast.makeText(this, "No files or folders selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ Recursively add all files from the selected folder
// ✅ Recursively add all files from selected folders into the ZIP
    private fun addFolderContentsToSelection(folder: File) {
        folder.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                addFolderContentsToSelection(file) // ✅ Recursively add subfolders
            } else {
                selectedFiles.add(file) // ✅ Add each file inside the folder
            }
        }
    }



    private fun getFolderPath(uri: Uri): String? {
        val documentId = DocumentsContract.getTreeDocumentId(uri)
        val parts = documentId.split(":")
        return if (parts.size >= 2) {
            Environment.getExternalStorageDirectory().absolutePath + "/" + parts[1]
        } else {
            null
        }
    }



    // ✅ Show Dialog to Enter ZIP Folder Name
    private fun showZipFolderNameDialog() {
//        val editText = android.widget.EditText(this)
//        editText.hint = "Enter ZIP folder name"
//
//        AlertDialog.Builder(this)
//            .setTitle("Set ZIP Folder Name")
//            .setView(editText)
//            .setPositiveButton("OK") { _, _ ->
//                val zipFolderName = editText.text.toString().trim()
//                if (zipFolderName.isNotEmpty()) {
//                    zipSelectedFiles(zipFolderName)
//                } else {
//                    Toast.makeText(this, "Folder name cannot be empty!", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()





// Declare a class-level variable for the dialog
         var zipCreteFolderNameDialog: Dialog? = null


        // Create and set up the dialog
        zipCreteFolderNameDialog = Dialog(this@ZipActivity, R.style.renameDialogStyle).apply {
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
                val desiredWidth = screenWidth - 2 * dpToPx(this@ZipActivity, 30)
                val params = it.attributes
                params.width = desiredWidth
                it.attributes = params
            }

            val pdfNewNameEt = findViewById<TextInputEditText>(R.id.pdfNewNameEt)
            val renameBtn = findViewById<TextView>(R.id.renameBtn)
            val cancelBtn = findViewById<TextView>(R.id.cancelBtn)
            val title = findViewById<TextView>(R.id.title)
            val clearTextIcon = findViewById<ImageView>(R.id.clearTextIcon)






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
                val zipFolderName = pdfNewNameEt.text.toString().trim()
                if (zipFolderName.isNotEmpty()) {
                    zipSelectedFiles(zipFolderName)

                    Log.d("checkfoldername",zipFolderName)
                } else {
                    Toast.makeText(this@ZipActivity, "Folder name cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                zipCreteFolderNameDialog?.dismiss()
            }

            // Show the dialog
            show()
        }












    }

    // ✅ Compress Selected Files into ZIP
    private fun zipSelectedFiles(zipFolderName: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating ZIP...")
        progressDialog.setMessage("Please wait while compressing files")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val zipFile = File(downloadsDir, "$zipFolderName.zip")

                val zip = ZipFile(zipFile)
                val zipParams = ZipParameters().apply {
                    compressionMethod = CompressionMethod.DEFLATE // ✅ Proper compression
                }

                for (file in selectedFiles) {
                    if (file.isDirectory) {
                        zip.addFolder(file, zipParams) // ✅ Add full folder
                    } else {
                        zip.addFile(file, zipParams) // ✅ Add individual file
                    }
                }

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
//                    showSnackbar("ZIP saved in: ${zipFile.absolutePath}") { openFolder(zipFile.parentFile!!) }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@ZipActivity, "Error creating ZIP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ✅ Convert URI to File Path Properly (Preserve Extensions)
    private fun getFileFromUri(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)

            // Get original file name with extension
            val fileName = getFileName(uri) ?: "unknown_file"
            val file = File(getExternalFilesDir(null), fileName)

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

    // ✅ Get Original File Name (Preserve Extensions)
    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex)
                }
            }
        }
        return name
    }

    // ✅ Open Folder After Zipping
    private fun openFolder(folder: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.fromFile(folder), "resource/folder")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }
}
