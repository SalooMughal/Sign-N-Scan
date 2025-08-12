package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.waterMark

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfGState
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.pixelz360.docsign.imagetopdf.creator.Constant
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.getAdsForLiftTimeString
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree.Companion.isPremium
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager.AdCallback
import com.pixelz360.docsign.imagetopdf.creator.databinding.FragmentImageBinding
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

class ImageFragment : Fragment() {

    lateinit var binding:FragmentImageBinding

    private var selectedFilePath: String? = null
    private var selectedImagePath: String? = null

    var pdfFileViewModel: PdfFileViewModel? = null

    private var adManager: AdManager? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater,container, false)


        pdfFileViewModel = ViewModelProvider(requireActivity())[PdfFileViewModel::class.java]


        adManager = AdManager()


        //        adManager11.loadAd(requireActivity(), "ca-app-pub-3097817755522298/5613637998");
//        adManager11.loadAd(requireActivity(), getString(R.string.convert_to_pdf_final_button_screen_intertial_ad));
        adManager!!.loadAd(requireActivity(), getString(R.string.watermark_pdf_completion_button_intertial_ad))


        binding.selectPdfButton.setOnClickListener { selectPdfFile() }


        binding.selectImageButton.setOnClickListener { selectImageFile() }
        binding.addWatermarkBtn.setOnClickListener {

            if (!isInternetAvailable()) {
                // No internet, proceed directly
                addWatermarkToPdf()
            } else if (isPremium(requireActivity()) ||
                getAdsForLiftTimeString(requireActivity()) == "ads_free_life_time"
            ) {
                // User is premium, skip ad
                addWatermarkToPdf()
            } else {
                // Show ad first, then navigate
                adManager!!.showAdIfAvailable(
                    requireActivity(),
                    getString(R.string.watermark_pdf_completion_button_intertial_ad),
                    object : AdCallback {
                        override fun onAdDismissed() {
                            addWatermarkToPdf()
                        }

                        override fun onAdFailedToShow() {
                            addWatermarkToPdf()
                        }
                    })
            }

//            addWatermarkToPdf()
        }


        binding.transparencySeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            //listener
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //add your event here
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.d("TAG123", " onProgressChanged progress "+progress)
                binding.transparencyValue.text = progress.toString()
            }
        })


        return binding.root
    }

    private fun isInternetAvailable(): Boolean {
        val cm =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }


    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, 1)
    }

    private fun selectImageFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.data?.let { uri ->
            when (requestCode) {
                1 -> selectedFilePath = getFilePathFromUri(uri)
                2 -> selectedImagePath = getFilePathFromUri(uri)



            }
            binding.filePathTextView.text =selectedFilePath
            binding.imagePathTextView.text =selectedImagePath
        }
    }

    private fun getFilePathFromUri(uri: Uri): String {
        val fileName = requireActivity().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        } ?: "unknown"
        val file = File(requireActivity().filesDir, fileName)
        requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file.absolutePath
    }

    private fun addWatermarkToPdf() {
        if (selectedFilePath == null) {
            Toast.makeText(requireActivity(), "Please select a PDF file first", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImagePath == null) {
            Toast.makeText(requireActivity(), "Please select a Image first", Toast.LENGTH_SHORT).show()
            return
        }

        val startPage = binding.startPageEditText.text.toString().toIntOrNull() ?: 1
        val endPage = binding.endPageEditText.text.toString().toIntOrNull() ?: -1

        val root = File(requireActivity().getExternalFilesDir(null), Constant.PDF_FOLDER)
        if (!root.exists()) root.mkdirs()


        val timestamp = System.currentTimeMillis()
        val seconds = (timestamp / 1000) % 60  // Extract seconds from timestamp
        val mainFileName = "Untitled Watermarked Doc $seconds"
//        val outputFileName = "watermarked_${System.currentTimeMillis()}.pdf"

        val outputFileName = "${mainFileName}.pdf"
        val outputFile = File(root, outputFileName)



        val verticalPosition = when (binding.verticalPositionGroup.checkedRadioButtonId) {
            R.id.positionTop -> 800f
            R.id.positionMiddle -> 400f
            R.id.positionBottom -> 100f
            else -> 400f
        }

        val horizontalPosition = when (binding.horizontalPositionGroup.checkedRadioButtonId) {
            R.id.positionLeft -> 100f
            R.id.positionCenter -> 300f
            R.id.positionRight -> 500f
            else -> 300f
        }

        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    val reader = PdfReader(selectedFilePath)
                    val stamper = PdfStamper(reader, FileOutputStream(outputFile))

                    val actualEndPage = if (endPage == -1 || endPage > reader.numberOfPages) reader.numberOfPages else endPage


                        val transparency = binding.transparencySeekBar.progress / 100f





                        val image = Image.getInstance(selectedImagePath)
                        image.scaleToFit(200f, 200f) // Scale the image

                        for (i in startPage..actualEndPage) {
                            val canvas = stamper.getOverContent(i)
                            val pageSize = reader.getPageSizeWithRotation(i)

                            // Get the page dimensions
                            val pageWidth = pageSize.right - pageSize.left
                            val pageHeight = pageSize.top - pageSize.bottom

                            // Horizontal Position
                            val positionX = when (binding.horizontalPositionGroup.checkedRadioButtonId) {
                                R.id.positionLeft -> pageSize.left + 50f // Left margin
                                R.id.positionCenter -> pageSize.left + (pageWidth - image.scaledWidth) / 2 // Center horizontally
                                R.id.positionRight -> pageSize.right - image.scaledWidth - 50f // Right margin
                                else -> pageSize.left + (pageWidth - image.scaledWidth) / 2 // Default to center
                            }

                            // Vertical Position
                            val positionY = when (binding.verticalPositionGroup.checkedRadioButtonId) {
                                R.id.positionTop -> pageSize.top - image.scaledHeight - 50f // Top margin
                                R.id.positionMiddle -> pageSize.bottom + (pageHeight - image.scaledHeight) / 2 // Center vertically
                                R.id.positionBottom -> pageSize.bottom + 50f // Bottom margin
                                else -> pageSize.bottom + (pageHeight - image.scaledHeight) / 2 // Default to middle
                            }

                            // Set position
                            image.setAbsolutePosition(positionX, positionY)

                            // Apply rotation

                            // Apply transparency
                            val gState = PdfGState()
                            gState.setFillOpacity(transparency) // Set transparency
                            canvas.setGState(gState)

                            // Add the image to the page
                            canvas.addImage(image)
                        }







//                    else if (binding.watermarkTypeGroup.checkedRadioButtonId == R.id.imageWatermarkOption && selectedImagePath != null) {
//                        val transparency = binding.transparencySeekBar.progress / 100f
//                        val rotationDegrees = binding.rotationSeekBar.progress.toFloat() // Rotation in degrees
//                        val rotationRadians = Math.toRadians(rotationDegrees.toDouble()).toFloat() // Convert to radians
//
//                        val image = Image.getInstance(selectedImagePath)
//                        image.setAbsolutePosition(horizontalPosition, verticalPosition)
//                        image.scaleToFit(200f, 200f)
//                        image.rotation = rotationRadians.toInt() // Set rotation in radians
//
//                        for (i in startPage..actualEndPage) {
//                            val canvas = stamper.getOverContent(i)
//                            val gState = PdfGState()
//                            gState.setFillOpacity(transparency) // Set transparency
//                            canvas.setGState(gState)
//                            canvas.addImage(image)
//                        }
//                    }





                    stamper.close()
                    reader.close()
                    saveToDatabase(outputFile, outputFileName)

                    requireActivity().runOnUiThread {
                        Toast.makeText(requireActivity(), "Watermark added successfully: ${outputFile.absolutePath}", Toast.LENGTH_SHORT).show()

                        val intent = Intent(requireActivity(), Prview_Screen::class.java)
                        intent.putExtra("pdffilePath", outputFile.absolutePath)
                        intent.putExtra("fileName", outputFileName)
                        startActivity(intent)
                            requireActivity().finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }





    private fun saveToDatabase(outputPath: File, outputFileName: String) {
        // Save the path to your database

        val fileDate: File = File(outputPath.absolutePath)
        val fileSizeBytes = outputPath.length()
//            val timestamp: Long = file.lastModified()
//                    val directory = File(outputPath)
        var timestamp = outputPath.lastModified()



        val pdfFile = PdfFile(
            outputPath.absolutePath,
            outputFileName,
            "WaterMark Doc",
            "#FF8181",
            null,
            fileSizeBytes.toDouble(),
            timestamp,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            true,
            AccountsOrGuesHelper.checkAccountOrNot(requireActivity()),
            false,
            "pdf"
        )


        pdfFileViewModel!!.insertPdfFile(pdfFile)
    }
}
