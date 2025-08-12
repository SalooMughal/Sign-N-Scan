package com.pixelz360.docsign.imagetopdf.creator.zip_class

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityTtfSvgViewBinding
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityZipBinding
import net.lingala.zip4j.ZipFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class Ttf_And_Svg_View_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTtfSvgViewBinding
    private val FILE_SELECT_CODE = 100
    private var customFont: Typeface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTtfSvgViewBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val uri: Uri? = intent?.data
        if (uri != null) {
            val mimeType = contentResolver.getType(uri) ?: ""
            val fileName = getFileNameFromUri(uri) // Get actual file name

            when {
                mimeType.equals("image/svg+xml", ignoreCase = true) || fileName.endsWith(".svg", ignoreCase = true) -> loadSVG(uri)
                mimeType.equals("application/x-font-ttf", ignoreCase = true) ||
                        mimeType.equals("font/ttf", ignoreCase = true) ||
                        fileName.endsWith(".ttf", ignoreCase = true) -> loadFontFromUri(uri)  // ✅ Handle TTF
                else -> Log.d("checkfileuri", "Unsupported file type: $mimeType, Filename: $fileName")
            }

        }

        binding.btnChooseFile.setOnClickListener {
            openFileChooser()
        }
    }

    private fun loadSVG(uri: Uri) {

        binding.svgWebView.visibility = VISIBLE
        binding.previewText.visibility = GONE

        try {
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val svgContent = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()

                val svgHtml = """
                <html>
                <body style="margin: 0; padding: 0; text-align: center;">
                <img src="data:image/svg+xml;base64,${android.util.Base64.encodeToString(svgContent.toByteArray(), android.util.Base64.DEFAULT)}" width="100%" />
                </body>
                </html>
            """

//                val webView: WebView = findViewById(R.id.svgWebView)
                    binding.svgWebView.settings.javaScriptEnabled = true
                binding.svgWebView.loadData(svgHtml, "text/html", "UTF-8")

                Log.d("SVG Viewer", "SVG loaded successfully")
            } else {
                Log.d("SVG Viewer", "InputStream is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("SVG Viewer", "Exception: ${e.message}")
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

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a ZIP file"), FILE_SELECT_CODE)
        } catch (ex: Exception) {
            Log.e("FileChooser", "Error opening file chooser", ex)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                Log.d("File URI", "Selected ZIP: $uri")

                // Convert URI to File Path
//                val zipFilePath = getFileFromUri(uri)
//                if (zipFilePath != null) {
//                    unzipFile(zipFilePath)
//                } else {
//                    Toast.makeText(this, "Error getting file path", Toast.LENGTH_SHORT).show()
//                }

                loadFontFromUri(uri);

            }
        }
    }

    private fun loadFontFromUri(uri: Uri) {
        try {

            binding.svgWebView.visibility = GONE
            binding.previewText.visibility = VISIBLE


            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                // Save the file to a temporary location
                val file = File(cacheDir, "temp_font.ttf")
                file.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                inputStream.close()

                // Load the font from the temporary file
                val customFont = Typeface.createFromFile(file)
                binding.previewText.typeface = customFont

                Log.d("File URI", "Font successfully loaded")
            } else {
                Log.d("File URI", "InputStream is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("File URI", "Exception: ${e.message}")
        }
    }


    // Convert content:// URI to File Path
    private fun getFileFromUri(uri: Uri): String? {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "temp.zip")

            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Unzip Function
    private fun unzipFile(zipFilePath: String) {
        try {
            // Destination folder inside Downloads
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outputFolder = File(downloadsDir, "Extracted_ZIP")

            if (!outputFolder.exists()) outputFolder.mkdirs()

            // Extract ZIP file
            val zipFile = ZipFile(zipFilePath)
            zipFile.extractAll(outputFolder.absolutePath)

            Toast.makeText(this, "Unzipped to: ${outputFolder.absolutePath}", Toast.LENGTH_LONG).show()
            Log.d("Unzip", "Extraction complete: ${outputFolder.absolutePath}")

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error unzipping file", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
