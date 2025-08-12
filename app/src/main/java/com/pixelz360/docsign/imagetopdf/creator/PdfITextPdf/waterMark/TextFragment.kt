    package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.waterMark


    import android.content.Context
    import android.content.Intent
    import android.net.ConnectivityManager
    import android.net.Uri
    import android.os.Bundle
    import android.provider.OpenableColumns
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.View.GONE
    import android.view.View.VISIBLE
    import android.view.ViewGroup
    import android.widget.ArrayAdapter
    import android.widget.SeekBar
    import android.widget.SeekBar.OnSeekBarChangeListener
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import com.itextpdf.text.BaseColor
    import com.itextpdf.text.Element
    import com.itextpdf.text.Font
    import com.itextpdf.text.Phrase
    import com.itextpdf.text.pdf.BaseFont
    import com.itextpdf.text.pdf.ColumnText
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
    import com.pixelz360.docsign.imagetopdf.creator.databinding.FragmentTextBinding
    import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper
    import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.runBlocking
    import java.io.File
    import java.io.FileOutputStream


    class TextFragment : Fragment() {

        lateinit var binding:FragmentTextBinding

        private var selectedFilePath: String? = null
        private var selectedImagePath: String? = null

        var pdfFileViewModel: PdfFileViewModel? = null

        private var vote: String? = null
        private var o = 0

        private var adManager: AdManager? = null


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            // Inflate the layout for this fragment
            binding = FragmentTextBinding.inflate(inflater,container,false)


            pdfFileViewModel = ViewModelProvider(requireActivity())[PdfFileViewModel::class.java]


            //// Get screen width
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        int screenWidth = displayMetrics.widthPixels;
//
//// Define the number of buttons (columns)
//        int numberOfColumns = 3;
//
//// Get margins (convert dp to pixels)
//        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
//        int totalMargin = margin * (numberOfColumns - 1);
//
//// Calculate available width per item
//        int itemWidth = (screenWidth - totalMargin) / numberOfColumns;
//
//
//
//        convertPdfBtn.getLayoutParams().width = itemWidth;
//        signBtn.getLayoutParams().width = itemWidth;
//        scanBtn.getLayoutParams().width = itemWidth;
//
//// Apply changes
//        convertPdfBtn.requestLayout();
//        signBtn.requestLayout();
//        scanBtn.requestLayout();


//        loadInterstitialAd();

//        adManager11 = new AdManager();
            adManager = AdManager()


            //        adManager11.loadAd(requireActivity(), "ca-app-pub-3097817755522298/5613637998");
//        adManager11.loadAd(requireActivity(), getString(R.string.convert_to_pdf_final_button_screen_intertial_ad));
            adManager!!.loadAd(requireActivity(), getString(R.string.watermark_pdf_completion_button_intertial_ad))

            setupFontTypeSpinner()

            binding.selectPdfButton.setOnClickListener { selectPdfFile() }
            binding.addWatermarkBtn.setOnClickListener {


//                if (!isInternetAvailable()) {
//                    // No internet, proceed to the next activity
//
//                    Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                    intent.putExtra("ActivityAction", "FileSearch");
//                    startActivityForResult(intent, Merge_Request_CODE);
//
//                } else {
//
//                    if (PrefUtilForAppAdsFree.isPremium(requireActivity()) || PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")){
//
//                        Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                        intent.putExtra("ActivityAction", "FileSearch");
//                        startActivityForResult(intent, Merge_Request_CODE);
//
//                        Log.d("checkbilling"," add remove "+PrefUtilForAppAdsFree.isPremium(requireActivity()));
//
//
//                    }else {
//
//                        Log.d("checkbilling"," add not remove "+PrefUtilForAppAdsFree.isPremium(requireActivity()));

                // Show the ad or proceed if timeout


//                        if (!isInternetAvailable()) {
//                            // No internet, proceed directly
//                            Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                            intent.putExtra("ActivityAction", "FileSearch");
//                            startActivityForResult(intent, Merge_Request_CODE);
//                        } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
//                                PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
//                            // User is premium, skip ad
//                            Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                            intent.putExtra("ActivityAction", "FileSearch");
//                            startActivityForResult(intent, Merge_Request_CODE);
//                        } else {
//                            // Show ad first, then navigate
//                            adManager.showAdIfAvailable(requireActivity(), getString(R.string.sign_documets_intertial_ad), new AdManager.AdCallback() {
//                                @Override
//                                public void onAdDismissed() {
//                                    Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                                    intent.putExtra("ActivityAction", "FileSearch");
//                                    startActivityForResult(intent, Merge_Request_CODE);
//                                }
//
//                                @Override
//                                public void onAdFailedToShow() {
//                                    Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                                    intent.putExtra("ActivityAction", "FileSearch");
//                                    startActivityForResult(intent, Merge_Request_CODE);
//                                }
//                            });
//                        }


//                        adManager.showAdIfAvailable(requireActivity(), new AdManager.AdCallback() {
//                            @Override
//                            public void onAdDismissed() {
//                                // Define your custom action here after the ad is dismissed
//
//                                Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                                intent.putExtra("ActivityAction", "FileSearch");
//                                startActivityForResult(intent, Merge_Request_CODE);
//
//
//                            }
//
//                            @Override
//                            public void onAdFailedToShow() {
//                                // Define your custom action here if the ad fails to show
//
//                                Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                                intent.putExtra("ActivityAction", "FileSearch");
//                                startActivityForResult(intent, Merge_Request_CODE);
//
//                                Log.d("AdManager", "onAdFailedToShow");
//
//                            }
//                        });


//                    }


//                }
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


//                addWatermarkToPdf()
            }

            if (binding.entireWatermarkCheckBox.isChecked) {
                // CheckBox is checked
                Log.d("CheckBox", "Watermark applied to the full page")

                binding.inputLayout.visibility = GONE

            } else {
                // CheckBox is unchecked
                Log.d("CheckBox", "Watermark removed from the full page")
                binding.inputLayout.visibility = VISIBLE
            }


            binding.entireWatermarkCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // CheckBox is checked
                    Log.d("CheckBox", "Watermark applied to the full page")

                    binding.inputLayout.visibility = GONE

                } else {
                    // CheckBox is unchecked
                    Log.d("CheckBox", "Watermark removed from the full page")
                    binding.inputLayout.visibility = VISIBLE
                }
            }

            binding.increaseFontBtn.setOnClickListener {

                Log.d("TAG123", "Increasing value...")
                o += 1
                vote = o.toString()
                binding.txtNumFont.text = vote
                Log.i("TAG123", "vote add: $vote")

            }

            binding.decreaseFontBtn.setOnClickListener {

                Log.d("TAG123", "Decreasing value...")
                if (o > 0) {
                    o -= 1
                    vote = o.toString()
                    binding.txtNumFont.text = vote
                    Log.i("TAG123", "vote minus: $vote")
                } else {
                    Log.d("TAG123", "Value can't be less than 0")
                }

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
                    binding.transparencyValue.text = progress.toString()+"%"
                }
            })

            binding.rotationSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                //listener
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    //add your event here
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    Log.d("TAG123", " onProgressChanged progress "+progress)
                    binding.rotationValue.text = progress.toString()
                }
            })


            return binding.root
        }

        //    private void loadInterstitialAd() {
        //        AdRequest adRequest = new AdRequest.Builder().build();
        //        isAdLoading = true;
        //        binding.progressBar.setVisibility(View.GONE);
        //
        //        timeoutRunnable = new Runnable() {
        //            @Override
        //            public void run() {
        //                if (isAdLoading) {
        //                    isAdLoading = false;
        //                    binding.progressBar.setVisibility(View.GONE);
        //                    goToNextActivity();
        //                }
        //            }
        //        };
        //        handler.postDelayed(timeoutRunnable, 10000);
        //
        //        InterstitialAd.load(requireActivity(), getString(R.string.language_intertial_ad), adRequest, new InterstitialAdLoadCallback() {
        //            @Override
        //            public void onAdLoaded(InterstitialAd interstitialAd) {
        //                mInterstitialAd = interstitialAd;
        //                isAdLoading = false;
        //                binding.progressBar.setVisibility(View.GONE);
        //                handler.removeCallbacks(timeoutRunnable);
        //                setAdCallbacks();
        //            }
        //
        //            @Override
        //            public void onAdFailedToLoad(LoadAdError loadAdError) {
        //                mInterstitialAd = null;
        //                isAdLoading = false;
        //                binding.progressBar.setVisibility(View.GONE);
        //                handler.removeCallbacks(timeoutRunnable);
        //                goToNextActivity();
        //            }
        //        });
        //    }
        //
        //    private void setAdCallbacks() {
        //        if (mInterstitialAd != null) {
        //            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
        //                @Override
        //                public void onAdDismissedFullScreenContent() {
        //                    mInterstitialAd = null;
        //                    loadInterstitialAd();
        //                    goToNextActivity();
        //                }
        //
        //                @Override
        //                public void onAdFailedToShowFullScreenContent(AdError adError) {
        //                    mInterstitialAd = null;
        //                    loadInterstitialAd();
        //                    goToNextActivity();
        //                }
        //            });
        //        }
        //    }
        //
        //    private void goToNextActivity() {
        //
        //        pickImageFromGallery();
        //
        //    }
        //
        //    private void showAdOrLoad() {
        //        if (mInterstitialAd != null) {
        //            mInterstitialAd.show(requireActivity());
        //        } else if (isAdLoading) {
        //            binding.progressBar.setVisibility(View.VISIBLE);
        //        } else {
        //            goToNextActivity();
        //        }
        //    }
        private fun isInternetAvailable(): Boolean {
            val cm =
                requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }


        private fun setupFontTypeSpinner() {
            val fontSpinner: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireActivity(), R.array.font_type_array, android.R.layout.simple_spinner_item)
            fontSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.fontTypeSpinner.adapter = fontSpinner









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

//        private fun addWatermarkToPdf() {
//            if (selectedFilePath == null) {
//                Toast.makeText(requireActivity(), "Please select a PDF file first", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            if (binding.watermarkEditText.text?.isEmpty() == true) {
//                Toast.makeText(requireActivity(), "Please Text Input", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            val startPage = binding.startPageEditText.text.toString().toIntOrNull() ?: 1
//            val endPage = binding.endPageEditText.text.toString().toIntOrNull() ?: -1
//
//            val root = File(requireActivity().getExternalFilesDir(null), Constant.PDF_FOLDER)
//            if (!root.exists()) root.mkdirs()
//
//            val outputFileName = "watermarked_${System.currentTimeMillis()}.pdf"
//            val outputFile = File(root, outputFileName)
//
//            val verticalPosition = when (binding.verticalPositionGroup.checkedRadioButtonId) {
//                R.id.positionTop -> 800f
//                R.id.positionMiddle -> 400f
//                R.id.positionBottom -> 100f
//                else -> 400f
//            }
//
//            val horizontalPosition = when (binding.horizontalPositionGroup.checkedRadioButtonId) {
//                R.id.positionLeft -> 100f
//                R.id.positionCenter -> 300f
//                R.id.positionRight -> 500f
//                else -> 300f
//            }
//
//            runBlocking {
//                launch(Dispatchers.IO) {
//                    try {
//                        val reader = PdfReader(selectedFilePath)
//                        val stamper = PdfStamper(reader, FileOutputStream(outputFile))
//
//                        val actualEndPage = if (endPage == -1 || endPage > reader.numberOfPages) reader.numberOfPages else endPage
//
//                            val watermarkText = binding.watermarkEditText.text.toString()
//    //                        val fontSize = binding.fontSizeSeekBar.progress.toFloat()
//                            val fontSize = binding.txtNumFont.text.toString().toFloat()
//                            val transparency = binding.transparencySeekBar.progress / 100f
//                            val rotation = binding.rotationSeekBar.progress.toFloat()
//
//    //                    Log.d("TAG123", " transparency progress "+transparency)
//
//                            val color = when (binding.colorGroup.checkedRadioButtonId) {
//                                R.id.colorBlack -> BaseColor.BLACK
//                                R.id.colorWhite -> BaseColor.WHITE
//                                R.id.colorRed -> BaseColor.RED
//                                R.id.colorDarkBlue -> BaseColor(0, 0, 139) // RGB for dark blue
//                                else -> BaseColor.BLACK
//                            }
//
//                            val fontType = when (binding.fontTypeSpinner.selectedItemPosition) {
//                                0 -> "font/garogier_regular.ttf"
//                                1 -> "font/gilroy_bold.ttf"
//                                2 -> "font/gilroy_light.ttf"
//                                3 -> "font/gilroy_medium.ttf"
//                                4 -> "font/gilroy_regular.ttf"
//                                else -> "font/gilroy_semi_bold.ttf"
//                            }
//
//                            val baseFont = BaseFont.createFont("assets/$fontType", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
//                            val font = Font(baseFont, fontSize, Font.NORMAL, color)
//
//                            for (i in startPage..actualEndPage) {
//                                val canvas = stamper.getOverContent(i)
//                                val gState = PdfGState()
//                                gState.setFillOpacity(transparency) // Set fill opacity
//                                canvas.setGState(gState)
//
//                                ColumnText.showTextAligned(
//                                    canvas,
//                                    Element.ALIGN_CENTER,
//                                    Phrase(watermarkText, font),
//                                    horizontalPosition,
//                                    verticalPosition,
//                                    rotation
//                                )
//                            }
//
//
//
//
//
//
//
//    //                    else if (binding.watermarkTypeGroup.checkedRadioButtonId == R.id.imageWatermarkOption && selectedImagePath != null) {
//    //                        val transparency = binding.transparencySeekBar.progress / 100f
//    //                        val rotationDegrees = binding.rotationSeekBar.progress.toFloat() // Rotation in degrees
//    //                        val rotationRadians = Math.toRadians(rotationDegrees.toDouble()).toFloat() // Convert to radians
//    //
//    //                        val image = Image.getInstance(selectedImagePath)
//    //                        image.setAbsolutePosition(horizontalPosition, verticalPosition)
//    //                        image.scaleToFit(200f, 200f)
//    //                        image.rotation = rotationRadians.toInt() // Set rotation in radians
//    //
//    //                        for (i in startPage..actualEndPage) {
//    //                            val canvas = stamper.getOverContent(i)
//    //                            val gState = PdfGState()
//    //                            gState.setFillOpacity(transparency) // Set transparency
//    //                            canvas.setGState(gState)
//    //                            canvas.addImage(image)
//    //                        }
//    //                    }
//
//
//
//
//
//                        stamper.close()
//                        reader.close()
//                        saveToDatabase(outputFile, outputFileName)
//
//                        requireActivity().runOnUiThread {
//                            Toast.makeText(requireActivity(), "Watermark added successfully: ${outputFile.absolutePath}", Toast.LENGTH_SHORT).show()
//
//                            val intent = Intent(requireActivity(), Prview_Screen::class.java)
//                            intent.putExtra("pdffilePath", outputFile.absolutePath)
//                            intent.putExtra("fileName", outputFileName)
//                            startActivity(intent)
//
//                            Log.d("PdfViewActivity11", "outputFile.absolutePath  ${outputFile.absolutePath}")
//
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        Log.d("TAG123", " Exception "+e.message)
//
//                        val transparency = binding.transparencySeekBar.progress / 100f
//
//                        Log.d("TAG123", " transparency progress "+transparency.toString())
//                    }
//                }
//            }
//        }


        private fun addWatermarkToPdf() {
            if (selectedFilePath == null) {
                Toast.makeText(requireActivity(), "Please select a PDF file first", Toast.LENGTH_SHORT).show()
                return
            }

            if (binding.watermarkEditText.text?.isEmpty() == true) {
                Toast.makeText(requireActivity(), "Please enter watermark text", Toast.LENGTH_SHORT).show()
                return
            }

            val startPage = binding.startPageEditText.text.toString().toIntOrNull() ?: 1
            val endPage = binding.endPageEditText.text.toString().toIntOrNull() ?: -1

            val root = File(requireActivity().getExternalFilesDir(null), Constant.PDF_FOLDER)
            if (!root.exists()) root.mkdirs()





//            val outputFileName = "watermarked_${System.currentTimeMillis()}.pdf"

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

            val isFullPageWatermark = binding.multipleWatermarkCheckBox.isChecked // Check checkbox state

            runBlocking {
                launch(Dispatchers.IO) {
                    try {
                        val reader = PdfReader(selectedFilePath)
                        val stamper = PdfStamper(reader, FileOutputStream(outputFile))

                        val actualEndPage = if (endPage == -1 || endPage > reader.numberOfPages) reader.numberOfPages else endPage

                        val watermarkText = binding.watermarkEditText.text.toString()
                        val fontSize = binding.txtNumFont.text.toString().toFloat()
                        val transparency = binding.transparencySeekBar.progress / 100f
                        val rotation = binding.rotationSeekBar.progress.toFloat()

                        val color = when (binding.colorGroup.checkedRadioButtonId) {
                            R.id.colorBlack -> BaseColor.BLACK
                            R.id.colorWhite -> BaseColor.WHITE
                            R.id.colorRed -> BaseColor.RED
                            R.id.colorDarkBlue -> BaseColor(0, 0, 139)
                            else -> BaseColor.BLACK
                        }

                        val fontType = when (binding.fontTypeSpinner.selectedItemPosition) {
                            0 -> "font/garogier_regular.ttf"
                            1 -> "font/gilroy_bold.ttf"
                            2 -> "font/gilroy_light.ttf"
                            3 -> "font/gilroy_medium.ttf"
                            4 -> "font/gilroy_regular.ttf"
                            else -> "font/gilroy_semi_bold.ttf"
                        }

                        val baseFont = BaseFont.createFont("assets/$fontType", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
                        val font = Font(baseFont, fontSize, Font.NORMAL, color)

                        for (i in startPage..actualEndPage) {
                            val canvas = stamper.getOverContent(i)
                            val gState = PdfGState()
                            gState.setFillOpacity(transparency)
                            canvas.setGState(gState)

                            if (isFullPageWatermark) {
                                // Apply watermark multiple times on the page
                                for (y in 0..reader.getPageSize(i).height.toInt() step 80) {
                                    for (x in 0..reader.getPageSize(i).width.toInt() step 80) {
                                        ColumnText.showTextAligned(
                                            canvas,
                                            Element.ALIGN_CENTER,
                                            Phrase(watermarkText, font),
                                            x.toFloat(),
                                            y.toFloat(),
                                            rotation
                                        )
                                    }
                                }
                            } else {
                                // Apply watermark once
                                ColumnText.showTextAligned(
                                    canvas,
                                    Element.ALIGN_CENTER,
                                    Phrase(watermarkText, font),
                                    horizontalPosition,
                                    verticalPosition,
                                    rotation
                                )
                            }
                        }

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
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireActivity(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
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
