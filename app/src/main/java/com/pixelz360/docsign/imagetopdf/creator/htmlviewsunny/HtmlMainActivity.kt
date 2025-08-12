package com.pixelz360.docsign.imagetopdf.creator.htmlviewsunny

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.pixelz360.docsign.imagetopdf.creator.R
import com.shaun.htmlviewsunny.HtmlView
import java.io.BufferedReader
import java.io.InputStreamReader

class HtmlMainActivity : AppCompatActivity() {

    private lateinit var htmlTextView: HtmlView
    private val FILE_SELECT_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_html)

        val btnChooseFile: Button = findViewById(R.id.btnChooseFile)
        htmlTextView = findViewById(R.id.htmlTextView)

        btnChooseFile.setOnClickListener {
            openFileChooser()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/html"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            startActivityForResult(Intent.createChooser(intent, "Select an HTML file"), FILE_SELECT_CODE)
        } catch (ex: Exception) {
            Log.e("FileChooser", "Error opening file chooser", ex)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                displayHtmlCodeAsText(uri)
            }
        }
    }

    private fun displayHtmlCodeAsText(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()

            reader.useLines { lines ->
                lines.forEach { stringBuilder.append(it).append("\n") }
            }

            val rawHtml = stringBuilder.toString()

            // Convert HTML to show as text
            val escapedHtml = Html.escapeHtml(rawHtml)

            htmlTextView.setText(escapedHtml)

        } catch (e: Exception) {
            Log.e("HTML Viewer", "Error loading HTML file", e)
        }
    }
}

