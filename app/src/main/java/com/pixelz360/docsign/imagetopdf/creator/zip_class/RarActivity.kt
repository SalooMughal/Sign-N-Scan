package com.pixelz360.docsign.imagetopdf.creator.zip_class




import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pixelz360.docsign.imagetopdf.creator.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.RandomAccessFile

class RarActivity : AppCompatActivity() {

    private lateinit var btnSelectFiles: Button
    private lateinit var tvSelectedFiles: TextView
    private var selectedFiles = mutableListOf<File>()
    private lateinit var progressDialog: ProgressDialog
    private val FILE_SELECT_CODE = 100
    private val FOLDER_SELECT_CODE = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zip)

        btnSelectFiles = findViewById(R.id.btnSelectFiles)
        tvSelectedFiles = findViewById(R.id.tvSelectedFiles)

        btnSelectFiles.setOnClickListener {
            selectFilesAndFolders()
        }

        try {
            SevenZip.initSevenZipFromPlatformJAR()
        } catch (e: Exception) {
            Log.e("RarActivity", "7-Zip-JBinding Initialization Failed: ${e.message}")
        }
    }

    private fun selectFilesAndFolders() {
        val options = arrayOf("Select Files", "Select Multiple Folders")
        AlertDialog.Builder(this)
            .setTitle("Choose Selection Mode")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> selectFiles()
                    1 -> showFolderSelectionDialog()
                }
            }
            .show()
    }

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
                        addFolderContentsToSelection(selectedFolder) // ✅ Corrected method
                    }
                }

                if (selectedFiles.isNotEmpty()) {
                    tvSelectedFiles.text = "Selected Folders: ${selectedFiles.joinToString { it.name }}"
                    showRarFolderNameDialog()
                } else {
                    Toast.makeText(this, "No folders selected", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getAllFolders(): List<File> {
        val storageDir = Environment.getExternalStorageDirectory()
        return storageDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FILE_SELECT_CODE -> {
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
            }

            if (selectedFiles.isNotEmpty()) {
                tvSelectedFiles.text = "Selected: ${selectedFiles.joinToString { it.name }}"
                showRarFolderNameDialog()
            } else {
                Toast.makeText(this, "No files or folders selected", Toast.LENGTH_SHORT).show()
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

    // ✅ FIXED: Recursively add all files from the selected folders
    private fun addFolderContentsToSelection(folder: File) {
        folder.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                addFolderContentsToSelection(file) // ✅ Recursively add subfolders
            } else {
                selectedFiles.add(file) // ✅ Add each file inside the folder
            }
        }
    }

    private fun showRarFolderNameDialog() {
        val editText = android.widget.EditText(this)
        editText.hint = "Enter RAR folder name"

        AlertDialog.Builder(this)
            .setTitle("Set RAR Folder Name")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val rarFolderName = editText.text.toString().trim()
                if (rarFolderName.isNotEmpty()) {
                    createRarArchive(rarFolderName)
                } else {
                    Toast.makeText(this, "Folder name cannot be empty!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createRarArchive(rarFolderName: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating RAR...")
        progressDialog.setMessage("Please wait while compressing files")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val rarFile = File(downloadsDir, "$rarFolderName.rar")

                val randomAccessFile = RandomAccessFile(rarFile, "rw")
                val outStream = RandomAccessFileOutStream(randomAccessFile)
                val sevenZip = SevenZip.openOutArchive7z()

//                val archiveCallback = ArchiveCallback(selectedFiles)
//                sevenZip.createArchive(outStream, selectedFiles.size, archiveCallback)

                sevenZip.close()
                outStream.close()
                randomAccessFile.close()

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@RarActivity, "RAR saved in: ${rarFile.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@RarActivity, "Error creating RAR", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
