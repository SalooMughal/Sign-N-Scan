package com.pixelz360.docsign.imagetopdf.creator.zip_class

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
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
import net.sf.sevenzipjbinding.impl.OutItemFactory
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream
import java.io.*

class SevenZArchiveActivity : AppCompatActivity() {

    private lateinit var btnSelectFiles: Button
    private lateinit var tvSelectedFiles: TextView
    private var selectedFiles = mutableListOf<File>()
    private lateinit var progressDialog: ProgressDialog
    private val FILE_SELECT_CODE = 100
    private val FOLDER_SELECT_CODE = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seven_zarchive)

        btnSelectFiles = findViewById(R.id.btnSelectFiles)
        tvSelectedFiles = findViewById(R.id.tvSelectedFiles)

        btnSelectFiles.setOnClickListener {
            selectFilesAndFolders()
        }

        try {
            SevenZip.initSevenZipFromPlatformJAR()
        } catch (e: Exception) {
            Log.e("SevenZArchive", "7-Zip Initialization Failed: ${e.message}")
        }
    }

    private fun selectFilesAndFolders() {


        selectFiles()

//        val options = arrayOf("Select Files", "Select Folders")
//        AlertDialog.Builder(this)
//            .setTitle("Choose Selection Mode")
//            .setItems(options) { _, which ->
//                when (which) {
//                    0 -> selectFiles()
//                    1 -> selectFolder()
//                }
//            }
//            .show()
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

    private fun selectFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        try {
            startActivityForResult(intent, FOLDER_SELECT_CODE)
        } catch (ex: Exception) {
            Log.e("FolderPicker", "Error opening folder picker", ex)
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

                FOLDER_SELECT_CODE -> {
                    val folderUri = data?.data
                    if (folderUri != null) {
                        val folderPath = getFolderPath(folderUri)
                        if (folderPath != null) {
                            selectedFiles.add(File(folderPath))
                            addFolderContentsToSelection(File(folderPath))
                        }
                    }
                }
            }

            if (selectedFiles.isNotEmpty()) {
                tvSelectedFiles.text = "Selected: ${selectedFiles.joinToString { it.name }}"
                showArchiveNameDialog()
            } else {
                Toast.makeText(this, "No files or folders selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addFolderContentsToSelection(folder: File) {
        folder.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                addFolderContentsToSelection(file)
            } else {
                selectedFiles.add(file)
            }
        }
    }

    private fun getFolderPath(uri: Uri): String? {
        return uri.path?.split(":")?.getOrNull(1)?.let {
            Environment.getExternalStorageDirectory().absolutePath + "/" + it
        }
    }

    private fun showArchiveNameDialog() {
        val editText = android.widget.EditText(this)
        editText.hint = "Enter archive name"

        AlertDialog.Builder(this)
            .setTitle("Set Archive Name")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val archiveName = editText.text.toString().trim()
                if (archiveName.isNotEmpty()) {
//                    createSevenZArchive(archiveName)
                    create7zFile(archiveName)
                    
                } else {
                    Toast.makeText(this, "Archive name cannot be empty!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun create7zFile(archiveName: String) {
        if (selectedFiles.isEmpty()) {
            Toast.makeText(this, "No files to archive!", Toast.LENGTH_SHORT).show()
            return
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating 7z Archive...")
        progressDialog.setMessage("Please wait while compressing files")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val parentDir = downloadsDir.absolutePath
                var archiveFile = File(parentDir, "$archiveName.7z")

                var counter = 1
                while (archiveFile.exists()) {
                    archiveFile = File(parentDir, "$archiveName ($counter).7z")
                    counter++
                }

                val baseDirectory = selectedFiles.first().parentFile?.absolutePath ?: ""
                val raf = RandomAccessFile(archiveFile, "rw")
                val outArchive = SevenZip.openOutArchive7z()

                outArchive.setLevel(5) // Medium compression level
                outArchive.setSolid(true) // Enable solid compression
                outArchive.setThreadCount(2) // Set number of threads
                outArchive.setHeaderEncryption(false) // Disable header encryption

                outArchive.createArchive(RandomAccessFileOutStream(raf), selectedFiles.size, object : IOutCreateCallback<IOutItem7z> {
                    override fun setTotal(total: Long) {}

                    override fun setCompleted(complete: Long) {
                        val totalSize = selectedFiles.sumOf { it.length() }
                        val progress = ((complete.toDouble() / totalSize) * 100).toInt()
//                        withContext(Dispatchers.Main) {
//                            progressDialog.progress = progress
//                        }
                        progressDialog.progress = progress

                    }

                    override fun setOperationResult(operationResultOk: Boolean) {}

                    override fun getItemInformation(index: Int, outItemFactory: OutItemFactory<IOutItem7z>): IOutItem7z {
                        val file = selectedFiles[index]
                        val item = outItemFactory.createOutItem()
                        val relativePath = file.absolutePath.removePrefix(baseDirectory).removePrefix("/")

                        item.dataSize = file.length()
                        item.propertyPath = relativePath
                        item.propertyIsDir = file.isDirectory

                        return item
                    }

                    override fun getStream(index: Int): ISequentialInStream? {
                        val file = selectedFiles[index]
                        return if (file.isFile) {
                            RandomAccessFileInStream(RandomAccessFile(file, "r"))
                        } else {
                            null
                        }
                    }
                })

                outArchive.close()
                raf.close()

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@SevenZArchiveActivity, "Archive saved at: ${archiveFile.absolutePath}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@SevenZArchiveActivity, "Error creating archive: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun createSevenZArchive(archiveName: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating 7z Archive...")
        progressDialog.setMessage("Please wait while compressing files")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {


//                create7zFile(archiveName, null, null, null == true, null, selectedFiles)




//                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                val archiveFile = File(downloadsDir, "$archiveName.7z")
//
//                val randomAccessFile = RandomAccessFile(archiveFile, "rw")
//                val outStream = RandomAccessFileOutStream(randomAccessFile)
//                val sevenZip = SevenZip.openOutArchive7z()
//
//                val archiveCallback = ArchiveCallback(selectedFiles)
//                sevenZip.createArchive(outStream, selectedFiles.size, archiveCallback)
//
//                sevenZip.close()
//                outStream.close()
//                randomAccessFile.close()
//
//                withContext(Dispatchers.Main) {
//                    progressDialog.dismiss()
//                    Snackbar.make(
//                        findViewById(android.R.id.content),
//                        "7z Archive saved in: ${archiveFile.absolutePath}",
//                        Snackbar.LENGTH_LONG
//                    ).setAction("Open") {
//                        openFolder(archiveFile.parentFile!!)
//                    }.show()
//                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@SevenZArchiveActivity, "Error creating 7z archive", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    private fun create7zFile(archiveName: String, password: String?, compressionLevel: Int?, solid: Boolean, threadCount: Int?, filesToArchive: MutableList<File>) {
//
//        if (filesToArchive.isEmpty()) {
////            val errorMessage = getString(R.string.no_files_to_archive)
////            showErrorNotification(errorMessage)
////            sendLocalBroadcast(Intent(ACTION_ARCHIVE_ERROR).putExtra(EXTRA_ERROR_MESSAGE, errorMessage))
////            stopForegroundService()
//            return
//        }
//
//        try {
//            val baseDirectory = File(filesToArchive.first()).parentFile?.absolutePath ?: ""
//            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//            val archivePath = sharedPreferences.getString(PREFERENCE_ARCHIVE_DIR_PATH, null)
//            val parentDir: File
//
//            if (!archivePath.isNullOrEmpty()) {
//                parentDir = if (File(archivePath).isAbsolute) {
//                    File(archivePath)
//                } else {
//                    File(Environment.getExternalStorageDirectory(), archivePath)
//                }
//                if (!parentDir.exists()) {
//                    parentDir.mkdirs()
//                }
//            } else {
//                parentDir = File(filesToArchive.first()).parentFile ?: Environment.getExternalStorageDirectory()
//            }
//
//            var sevenZFile = File(parentDir, "$archiveName.7z")
//            var counter = 1
//
//            while (sevenZFile.exists()) {
//                sevenZFile = File(parentDir, "$archiveName ($counter).7z")
//                counter++
//            }
//
//            RandomAccessFile(sevenZFile, "rw").use { raf ->
//                val outArchive = SevenZip.openOutArchive7z()
//
//                outArchive.setLevel(compressionLevel!!)
//                outArchive.setSolid(solid)
//                outArchive.setThreadCount(threadCount!!)
//                outArchive.setHeaderEncryption(true)
//
//                outArchive.createArchive(RandomAccessFileOutStream(raf), filesToArchive.size,
//                    object : IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword, IOutFeatureSetEncryptHeader {
//                        override fun cryptoGetTextPassword(): String? {
//                            return password
//                        }
//
//                        override fun setOperationResult(operationResultOk: Boolean) {
//
//                        }
//
//                        override fun setTotal(total: Long) {
//
//                        }
//
//                        override fun setCompleted(complete: Long) {
////                            val totalSize = filesToArchive.sumOf { File(it).length() }
////                            val progress = ((complete.toDouble() / totalSize) * 100).toInt()
////                            startForeground(NOTIFICATION_ID, createNotification(progress))
////                            updateProgress(progress)
//                        }
//
//                        override fun getItemInformation(index: Int, outItemFactory: OutItemFactory<IOutItem7z>): IOutItem7z {
//                            val item = outItemFactory.createOutItem()
//                            val file = File(filesToArchive[index])
//                            val relativePath = file.absolutePath.removePrefix(baseDirectory).removePrefix("/")
//
//                            item.dataSize = file.length()
//                            item.propertyPath = relativePath
//                            item.propertyIsDir = file.isDirectory
//
//                            return item
//                        }
//
//                        override fun getStream(i: Int): ISequentialInStream {
//
//                            return RandomAccessFileInStream(RandomAccessFile(filesToArchive[i], "r"))
//                        }
//
//                        override fun setHeaderEncryption(enabled: Boolean) {
//                            outArchive.setHeaderEncryption(enabled)
//                        }
//                    })
//
//                outArchive.close()
////                stopForegroundService()
////                showCompletionNotification()
////                sendLocalBroadcast(Intent(ACTION_ARCHIVE_COMPLETE).putExtra(EXTRA_DIR_PATH, sevenZFile.parent))
//            }
//        } catch (e: SevenZipException) {
//            e.printStackTrace()
////            showErrorNotification(e.message ?: getString(R.string.general_error_msg))
////            sendLocalBroadcast(Intent(ACTION_ARCHIVE_ERROR).putExtra(EXTRA_ERROR_MESSAGE, e.message))
//        } catch (e: IOException) {
//            e.printStackTrace()
////            showErrorNotification(e.message ?: getString(R.string.general_error_msg))
////            sendLocalBroadcast(Intent(ACTION_ARCHIVE_ERROR).putExtra(EXTRA_ERROR_MESSAGE, e.message))
//        } catch (e: OutOfMemoryError) {
//            e.printStackTrace()
////            showErrorNotification(e.message ?: getString(R.string.general_error_msg))
////            sendLocalBroadcast(Intent(ACTION_ARCHIVE_ERROR).putExtra(EXTRA_ERROR_MESSAGE, e.message))
//        }
//    }

    private fun openFolder(folder: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.fromFile(folder), "resource/folder")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun getFileFromUri(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
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
}
