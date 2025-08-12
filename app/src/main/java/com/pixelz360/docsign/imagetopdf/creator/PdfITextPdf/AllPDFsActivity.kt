package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf


import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityAllPdfsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllPDFsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllPdfsBinding
    private val selectedFiles = mutableListOf<Uri>()

//    private val filePickerLauncher =
//        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
//
//            uri?.let {
//                selectedFiles.add(it)
//                setupRecyclerView(selectedFiles)
//            } ?: run {
//                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
//            }
//        }


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
                            selectedFiles.add(it)

//                            setupRecyclerView(selectedFiles)

                            val intent = Intent(this, MergeActivity::class.java)
                            intent.putParcelableArrayListExtra("selectedFiles", ArrayList(selectedFiles))
                            startActivity(intent)

                        }
                    }
                }
                //If single image selected
                else if (data?.data != null) {
                    val imageUri: Uri? = data.data
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllPdfsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openFilePicker()

        binding.pdfRecyclerView.layoutManager = LinearLayoutManager(this)
        setupRecyclerView(selectedFiles)

        binding.nextButton.setOnClickListener {
            if (selectedFiles.isEmpty()) {
                Toast.makeText(this, "Please select at least one file", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MergeActivity::class.java)
                intent.putParcelableArrayListExtra("selectedFiles", ArrayList(selectedFiles))
                startActivity(intent)
            }
        }


        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun openFilePicker() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//
//            type = "application/pdf"
//        }
//        filePickerLauncher.launch(intent)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "application/pdf"
        selectImagesActivityResult.launch(intent)

    }

    private fun setupRecyclerView(uris: List<Uri>) {
        val adapter = PdfAllFilesAdapter(uris) { selected ->
            // Update your UI or perform actions based on selected files
        }
        binding.pdfRecyclerView.adapter = adapter
    }
}

























//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.provider.Settings
//import android.util.Log
//import android.view.View.GONE
//import android.view.View.VISIBLE
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.pixelz360.docsign.imagetopdf.creator.R
//import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityAllPdfsBinding
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.File
//
//@AndroidEntryPoint
//class AllPDFsActivity : AppCompatActivity() {
//
//
//    private lateinit var pdfAllFilesAdapter: PdfAllFilesAdapter
//    private val selectedFiles = mutableListOf<File>()
//    private val MANAGE_EXTERNAL_STORAGE_REQUEST = 111
//
//    lateinit var binding:ActivityAllPdfsBinding
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAllPdfsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Check permissions for Android 11+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager()) {
//                val intent = Intent(
//                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
//                    Uri.parse("package:$packageName")
//                )
//                startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST)
//            }
//        }
//
//
//
//        binding.pdfRecyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Fetch PDF files using coroutines
//        lifecycleScope.launch {
//            val pdfFiles = fetchPdfFiles(Environment.getExternalStorageDirectory())
//            Log.d("checkfile","file size is comming 1")
//            binding.progressBar.visibility = GONE
//            binding.pdfRecyclerView.visibility = VISIBLE
//            binding.nextButton.visibility = VISIBLE
//
//            setupRecyclerView(pdfFiles)
//        }
//
//        binding.nextButton.setOnClickListener {
//            if (selectedFiles.isEmpty()) {
//                Toast.makeText(this, "Please select at least one file", Toast.LENGTH_SHORT).show()
//            } else {
//                val intent = Intent(this@AllPDFsActivity, MergeActivity::class.java)
//                intent.putExtra("selectedFiles", ArrayList(selectedFiles))
//                startActivity(intent)
//            }
//        }
//
//        binding.backButton.setOnClickListener {
//            onBackPressed()
//            finish()
//        }
//    }
//
//    private suspend fun fetchPdfFiles(directory: File): List<File> {
//        return withContext(Dispatchers.IO) {
//            getAllPdfFiles(directory)
//        }
//    }
//
//    private fun setupRecyclerView(pdfFiles: List<File>) {
//
//
//            binding.progressBar.visibility = GONE
//
//            Log.d("checkfile","progressBar gone 3")
//            pdfAllFilesAdapter = PdfAllFilesAdapter(pdfFiles) { selected ->
//                selectedFiles.clear()
//                selectedFiles.addAll(selected)
//            }
//            binding.pdfRecyclerView.adapter = pdfAllFilesAdapter
//
//
//
//    }
//
//    private fun getAllPdfFiles(directory: File): List<File> {
//        val pdfFiles = mutableListOf<File>()
//        val files = directory.listFiles()
//        files?.forEach { file ->
//            if (file.isDirectory) {
//                pdfFiles.addAll(getAllPdfFiles(file))
//            } else if (file.name.endsWith(".pdf")) {
//                pdfFiles.add(file)
//            }
//        }
//        return pdfFiles
//    }
//}
