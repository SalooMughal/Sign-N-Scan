    package com.pixelz360.docsign.imagetopdf.creator.editModule


    import android.app.Dialog
    import android.content.Context
    import android.content.Intent
    import android.content.res.Resources
    import android.graphics.Bitmap
    import android.graphics.Matrix
    import android.net.Uri
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper
    import android.util.Log
    import android.view.Gravity
    import android.view.View.GONE
    import android.view.View.VISIBLE
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.LinearLayout
    import android.widget.SeekBar
    import android.widget.TextView
    import android.widget.Toast
    import androidx.activity.result.PickVisualMediaRequest
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.app.AppCompatDelegate
    import androidx.appcompat.widget.SwitchCompat
    import androidx.core.content.ContextCompat
    import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
    import com.google.android.gms.ads.rewarded.RewardedAd
    import com.google.android.material.textfield.TextInputEditText
    import com.google.firebase.analytics.FirebaseAnalytics
    import com.pixelz360.docsign.imagetopdf.creator.HomeActivity
    import com.pixelz360.docsign.imagetopdf.creator.R
    import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityEditMoudleBinding
    import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils
    import com.yalantis.ucrop.UCrop
    import dagger.hilt.android.AndroidEntryPoint
    import jp.co.cyberagent.android.gpuimage.GPUImage
    import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
    import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorInvertFilter
    import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
    import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
    import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
    import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
    import jp.co.cyberagent.android.gpuimage.filter.GPUImageMonochromeFilter
    import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.MainScope
    import kotlinx.coroutines.Runnable
    import kotlinx.coroutines.cancel
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import java.io.File

    @AndroidEntryPoint
    class EditMoudleActivity : AppCompatActivity() {



        lateinit var binding: ActivityEditMoudleBinding
        private var adapter: ImagePagerAdapter? = null
        private lateinit var gpuImage: GPUImage
        private var currentFilter: GPUImageFilter? = null
        private var lastFilterApplied: GPUImageFilter? = null

        private var contrastFilter: GPUImageContrastFilter? = null
        private var brightnessFilter: GPUImageBrightnessFilter? = null
        private var detailsFilter: CustomDetailsFilter? = null

        private val mainScope = MainScope()

        // Variables to store seekbar progress and switch state
        private var savedContrastProgress = 0
        private var savedBrightnessProgress = 0
        private var savedDetailsProgress = 0
        private var isSwitchChecked = false

        private val handler = Handler(Looper.getMainLooper())
        private var debounceRunnable: Runnable? = null
        private val debounceDelay = 300L // Adjust debounce delay as needed


        //name with extension of the image


        //name with extension of the image
    //    var timestamp = System.currentTimeMillis()
    //    var mainFileName = "Untitled_file_Doc24 $timestamp"


        val timestamp = System.currentTimeMillis()
        val seconds = (timestamp / 1000) % 60  // Extract seconds from timestamp
        val mainFileName = "Untitled PDF $seconds"

        var renameFileName = mainFileName

        // Store applied filters for each image
        private val imageFilters: MutableMap<Int, GPUImageFilter?> = mutableMapOf()
        private val filterCache: MutableMap<Int, Bitmap?> = mutableMapOf()

        private val originalBitmapCache: MutableMap<Int, Bitmap?> = mutableMapOf()


        // Bitmap resizing function to optimize filter application
        private fun getResizedBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            val ratioBitmap = bitmap.width.toFloat() / bitmap.height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

            var finalWidth = maxWidth
            var finalHeight = maxHeight

            if (ratioMax > 1) {
                finalWidth = (maxHeight * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth / ratioBitmap).toInt()
            }

            return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
        }

        private var rewardedAd: RewardedAd? = null


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityEditMoudleBinding.inflate(layoutInflater)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            setContentView(binding.root)


            // Get color from resources
            val statusBarColor = ContextCompat.getColor(this@EditMoudleActivity, R.color.white)

            // Change status bar color
            FileUtils.changeStatusBarColor(statusBarColor, this@EditMoudleActivity)


    //        MobileAds.initialize(this)
    //        loadRewardedAd()

    //        var adRequest = AdRequest.Builder().build()
    //        RewardedAd.load(this,"ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
    //            override fun onAdFailedToLoad(adError: LoadAdError) {
    //                Log.d("RewardedAd", adError?.toString().toString())
    //                rewardedAd = null
    //            }
    //
    //            override fun onAdLoaded(ad: RewardedAd) {
    //                Log.d("RewardedAd", "Ad was loaded.")
    //                rewardedAd = ad
    //            }
    //        })


            gpuImage = GPUImage(this)

            //        if (renameFileName.length >= 10) {
            //            renameFileName = renameFileName.substring(0, 10)+ "...";
            //
            //        }
            Log.d("checkame",renameFileName)

            binding.fileName.setText(renameFileName)





            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            binding.adjustBtn.setOnClickListener {
                val dialog = Dialog(this@EditMoudleActivity, R.style.FileSortingDialogStyle)
                dialog.setContentView(R.layout.adjust_dailog)

                val window = dialog.window
                if (window != null) {
                    window.setGravity(Gravity.BOTTOM)
                    window.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                val SwitchCompatApplyAll = dialog.findViewById<SwitchCompat>(R.id.SwitchCompatApplyAll)
                val buttonContrast = dialog.findViewById<LinearLayout>(R.id.buttonContrast)
                val buttonBrightness = dialog.findViewById<LinearLayout>(R.id.buttonBrightness)
                val buttonDetails = dialog.findViewById<LinearLayout>(R.id.buttonDetails)
                val seekBarContrast = dialog.findViewById<SeekBar>(R.id.seekBarContrast)
                val seekBarBrightness = dialog.findViewById<SeekBar>(R.id.seekBarBrightness)
                val seekBarDetails = dialog.findViewById<SeekBar>(R.id.seekBarDetails)
                val contrastIcon = dialog.findViewById<ImageView>(R.id.contrastIcon)
                val brightnessIcon = dialog.findViewById<ImageView>(R.id.brightnessIcon)
                val detailsIcon = dialog.findViewById<ImageView>(R.id.detailsIcon)
                val dissmissBtn = dialog.findViewById<ImageView>(R.id.dissmissBtn)
                val saveBtn = dialog.findViewById<ImageView>(R.id.saveBtn)


                // Log a predefined event
                val analytics = FirebaseAnalytics.getInstance(this)
                val bundle = Bundle()
                bundle.putString("activity_name", "EditMoudleActivity")
                analytics.logEvent("activity_created", bundle)
                // Using predefined Firebase Analytics events
                // Using predefined Firebase Analytics events
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "EditMoudleActivity")
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
                analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


                dissmissBtn.setOnClickListener {
                    dialog.dismiss()
                }

                saveBtn.setOnClickListener {
                    dialog.dismiss()
                }

                // Restore saved state
                seekBarContrast.progress = savedContrastProgress
                seekBarBrightness.progress = savedBrightnessProgress
                seekBarDetails.progress = savedDetailsProgress
                SwitchCompatApplyAll.isChecked = isSwitchChecked

                SwitchCompatApplyAll.setOnCheckedChangeListener { _, isChecked ->
                    isSwitchChecked = isChecked
                    if (seekBarContrast.progress == 0 && seekBarBrightness.progress == 0 && seekBarDetails.progress == 0) {
                        Toast.makeText(this, "Please adjust the filter first", Toast.LENGTH_SHORT).show()
                        SwitchCompatApplyAll.isChecked = false
                        isSwitchChecked = false
                    } else {
                        debounce {
                            if (isChecked) {
                                mainScope.launch { applyAllFiltersToAllImages() }
                            } else {
                                mainScope.launch { resetAllImages() }
                            }
                        }
                    }
                }

                buttonContrast.setOnClickListener {
                    seekBarContrast.visibility = VISIBLE
                    seekBarBrightness.visibility = GONE
                    seekBarDetails.visibility = GONE

                    contrastIcon.setImageDrawable(resources.getDrawable(R.drawable.constrat_icon))
                    brightnessIcon.setImageDrawable(resources.getDrawable(R.drawable.brightness_icon))
                    detailsIcon.setImageDrawable(resources.getDrawable(R.drawable.details_icon))
                }

                buttonBrightness.setOnClickListener {
                    seekBarContrast.visibility = GONE
                    seekBarBrightness.visibility = VISIBLE
                    seekBarDetails.visibility = GONE

                    contrastIcon.setImageDrawable(resources.getDrawable(R.drawable.unselected_constrat_icon))
                    brightnessIcon.setImageDrawable(resources.getDrawable(R.drawable.brightness_selected_icon))
                    detailsIcon.setImageDrawable(resources.getDrawable(R.drawable.details_icon))
                }

                buttonDetails.setOnClickListener {
                    seekBarContrast.visibility = GONE
                    seekBarBrightness.visibility = GONE
                    seekBarDetails.visibility = VISIBLE

                    contrastIcon.setImageDrawable(resources.getDrawable(R.drawable.unselected_constrat_icon))
                    brightnessIcon.setImageDrawable(resources.getDrawable(R.drawable.brightness_icon))
                    detailsIcon.setImageDrawable(resources.getDrawable(R.drawable.details_selected_icon))
                }

                seekBarContrast.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        //                    debounce {
                        //                        val contrast = progress / 100.0f * 2 // Convert progress to contrast level (0 to 2)
                        //                        contrastFilter = GPUImageContrastFilter(contrast)
                        //                        applyRealTimeFilter(contrastFilter, SwitchCompatApplyAll)
                        //                        savedContrastProgress = progress
                        //                    }

                        debounce {
                            val contrast = progress / 100.0f * 2 // Convert progress to contrast level (0 to 2)
                            applyFilterToCurrentImage(GPUImageContrastFilter(contrast))
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })

                seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        //                    debounce {
                        //                        val brightness = progress / 100.0f * 2 - 1 // Convert progress to brightness level (-1 to 1)
                        //                        brightnessFilter = GPUImageBrightnessFilter(brightness)
                        //                        applyRealTimeFilter(brightnessFilter, SwitchCompatApplyAll)
                        //                        savedBrightnessProgress = progress
                        //                    }

                        debounce {
                            val brightness = progress / 100.0f * 2 - 1 // Convert progress to brightness level (-1 to 1)
                            applyFilterToCurrentImage(GPUImageBrightnessFilter(brightness))
                        }

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })

                seekBarDetails.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        //                    debounce {
                        //                        val detailsLevel = progress / 100.0f * 4 - 2 // Convert progress to details level (-2 to 2)
                        //                        detailsFilter = CustomDetailsFilter(detailsLevel)
                        //                        applyRealTimeFilter(detailsFilter, SwitchCompatApplyAll)
                        //                        savedDetailsProgress = progress
                        //                    }

                        debounce {
                            val detailsLevel = progress / 100.0f * 4 - 2 // Convert progress to details level (-2 to 2)
                            applyFilterToCurrentImage(CustomDetailsFilter(detailsLevel))
                        }


                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })

                dialog.setOnDismissListener {
                    savedContrastProgress = seekBarContrast.progress
                    savedBrightnessProgress = seekBarBrightness.progress
                    savedDetailsProgress = seekBarDetails.progress
                    isSwitchChecked = SwitchCompatApplyAll.isChecked
                }

                dialog.show()
            }


            binding.editBtn.setOnClickListener {

                val dialog = Dialog(this@EditMoudleActivity, R.style.renameDialogStyle)
                dialog.setContentView(R.layout.rename_dailog)


                val window = dialog.window
                if (window != null) {
                    window.setGravity(Gravity.CENTER)
                    window.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    window.setBackgroundDrawableResource(android.R.color.transparent)
                    val metrics = Resources.getSystem().displayMetrics
                    val screenWidth = metrics.widthPixels
                    val desiredWidth: Int = screenWidth - 2 * dpToPx(this@EditMoudleActivity, 30)
                    val params = window.attributes
                    params.width = desiredWidth
                    window.attributes = params
                }

                val pdfNewNameEt = dialog.findViewById<TextInputEditText>(R.id.pdfNewNameEt)
                val renameBtn = dialog.findViewById<TextView>(R.id.renameBtn)
                val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)
                val clearTextIcon = dialog.findViewById<ImageView>(R.id.clearTextIcon)

                clearTextIcon.setOnClickListener { pdfNewNameEt.setText("") }

                pdfNewNameEt.setText(renameFileName)
                cancelBtn.setOnClickListener { dialog.dismiss() }

                renameBtn.setOnClickListener {
                    val newName = pdfNewNameEt.text.toString().trim { it <= ' ' }
                    if (newName.isEmpty()) {
                        Toast.makeText(this@EditMoudleActivity, "Please enter a name for the PDF", Toast.LENGTH_SHORT).show()
                    } else {
                        renameFileName = newName
                        binding.fileName.setText(renameFileName)
                        dialog.dismiss()
                    }
                }


                dialog.show()




            }

            binding.backButton.setOnClickListener {


                discardDailog()

            }

            // Button for Original Image
            binding.buttonOriginal.setOnClickListener {


                binding.progressBar.visibility = VISIBLE


                resetToOriginalImage()


                binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_selected))
                binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.docs_second_un_selected_icon))
                binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_un_selected_icon))
                binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_un_selected_icon))

                Toast.makeText(this, "Reset to Original…", Toast.LENGTH_SHORT).show()
            }


            binding.buttonDocs.setOnClickListener {
                applyFilterToCurrentImage(GPUImageGrayscaleFilter())

                binding.progressBar.visibility = VISIBLE

                binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
                binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.doc_second_selected_icon))
                binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_un_selected_icon))
                binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_un_selected_icon))


                Toast.makeText(this, "Applying grayscale filter…", Toast.LENGTH_SHORT).show()
            }

            binding.buttonImage.setOnClickListener {
                applyFilterToCurrentImage(GPUImageSepiaToneFilter())
                binding.progressBar.visibility = VISIBLE

                binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
                binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.docs_second_un_selected_icon))
                binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_second_selected_icon))
                binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_un_selected_icon))

                Toast.makeText(this, "Applying sepia filter…", Toast.LENGTH_SHORT).show()
            }

//            binding.buttonInvert.setOnClickListener {
//                applyFilterToCurrentImage(GPUImageColorInvertFilter())
//
//
//                binding.progressBar.visibility = VISIBLE
//
//                binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
//                binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.docs_second_un_selected_icon))
//                binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_un_selected_icon))
//                binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_un_selected_icon))
//                binding.invertIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_second_selected_icon)) // Highlight selected
//
//                Toast.makeText(this, "Applying invert filter…", Toast.LENGTH_SHORT).show()
//            }
//
//
//            binding.buttonBW.setOnClickListener {
//                applyFilterToCurrentImage(CustomBWFilter())
//
//                binding.progressBar.visibility = VISIBLE
//
//                // Update selection UI
////                binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
////                binding.bwIcon.setImageDrawable(resources.getDrawable(R.drawable.bw_selected_icon))
//
//                Toast.makeText(this, "Applying Black & White filter…", Toast.LENGTH_SHORT).show()
//            }
//



            binding.buttonEnhance.setOnClickListener {
                applyFilterToCurrentImage(CustomEnhanceFilter())

                binding.progressBar.visibility = VISIBLE


                binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
                binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.docs_second_un_selected_icon))
                binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_un_selected_icon))
                binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_selected_icon))

                Toast.makeText(this, "Applying enhance filter…", Toast.LENGTH_SHORT).show()
            }








            //        binding.buttonDocs.setOnClickListener {
            //            if (lastFilterApplied !is GPUImageGrayscaleFilter) {
            //                applyFilter(GPUImageGrayscaleFilter())
            //            }
            //
            //
            //
            //            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
            //            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.doc_second_selected_icon))
            //            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_un_selected_icon))
            //            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_un_selected_icon))
            //
            //
            //
            //
            //            Toast.makeText(this, "Applying filter…", Toast.LENGTH_SHORT).show()
            //        }
            //        binding.buttonImage.setOnClickListener {
            //            if (lastFilterApplied !is GPUImageSepiaToneFilter) {
            //                applyRealTimeFilterDummy(GPUImageContrastFilter(1.72F))
            //            }
            //
            //
            //            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
            //            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.docs_second_un_selected_icon))
            //            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_second_selected_icon))
            //            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_un_selected_icon))
            //
            //            Toast.makeText(this, "Applying filter…", Toast.LENGTH_SHORT).show()
            //
            //        }
            //        binding.buttonEnhance.setOnClickListener {
            //            if (lastFilterApplied !is CustomEnhanceFilter) {
            //                applyFilter(CustomEnhanceFilter())
            //            }
            //
            //
            //
            //            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
            //            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.docs_second_un_selected_icon))
            //            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_un_selected_icon))
            //            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_selected_icon))
            //
            //
            //            Toast.makeText(this, "Applying filter…", Toast.LENGTH_SHORT).show()
            //
            //
            //
            //        }







            binding.doneBtn.setOnClickListener {

                openNextActivity()


            }

            binding.cropBtn.setOnClickListener {
                cropCurrentImage()
                Log.d("cropbtn", "Crop Button click")

            }

            binding.buttonPrevious.setOnClickListener {
                rotateImage(-90f)
            }

            binding.buttonNext.setOnClickListener {
                rotateImage(90f)
            }
        }

    //    private fun loadRewardedAd() {
    //        var adRequest = AdRequest.Builder().build()
    //        RewardedAd.load(this, getString(R.string.edit_module_activity_reworded_ad), adRequest, object : RewardedAdLoadCallback() {
    //            override fun onAdLoaded(ad: RewardedAd) {
    //                rewardedAd = ad
    //                Log.d("RewardedAd", "Ad successfully loaded")
    //            }
    //
    //            override fun onAdFailedToLoad(adError: LoadAdError) {
    //                Log.e("RewardedAd", "Ad failed to load: ${adError.message}")
    //                // Retry loading the ad after some time
    //                Handler(Looper.getMainLooper()).postDelayed({ loadRewardedAd() }, 3000)
    //            }
    //        })
    //    }

        // Apply filter to the current image

    //    private fun applyFilterToCurrentImage(filter: GPUImageFilter?) {
    //        val currentItem = binding.viewPager.currentItem
    //        val originalBitmap = adapter!!.getOriginalBitmap(currentItem)
    //
    //        if (originalBitmap != null) {
    //
    //            binding.progressBar.visibility = GONE
    //
    //
    //            mainScope.launch(Dispatchers.Default) {
    //                // Use a smaller preview bitmap for quick application
    //                val previewBitmap = getPreviewBitmap(originalBitmap)
    //
    //                gpuImage.setImage(previewBitmap)
    //                gpuImage.setFilter(filter)
    //
    //
    //                val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //
    //                withContext(Dispatchers.Main) {
    //                    if (filteredBitmap != null) {
    //                        // Show the filtered preview bitmap
    //                        adapter!!.setBitmap(currentItem, filteredBitmap)
    //                        // Store filter and cache the preview bitmap
    //                        imageFilters[currentItem] = filter
    //                        filterCache[currentItem] = filteredBitmap
    //
    //                        binding.progressBar.visibility = GONE
    //
    //                    }
    //                }
    //            }
    //        }
    //    }

        private fun applyFilterToCurrentImage(filter: GPUImageFilter?) {
            val currentItem = binding.viewPager.currentItem
            val originalBitmap = adapter?.getOriginalBitmap(currentItem)

            if (originalBitmap != null) {
                mainScope.launch(Dispatchers.Default) {
                    gpuImage.setImage(originalBitmap) // Use the original image
                    gpuImage.setFilter(filter)
                    val filteredBitmap = gpuImage.bitmapWithFilterApplied

                    withContext(Dispatchers.Main) {
                        if (filteredBitmap != null) {
                            adapter?.setBitmap(currentItem, filteredBitmap)
                            binding.progressBar.visibility = GONE
                        }
                    }
                }
            } else {
                Log.e("applyFilter", "Original bitmap is null")
            }
        }


    //    private fun getPreviewBitmap(bitmap: Bitmap, maxSize: Int = 812): Bitmap {
    //        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    //        val width: Int
    //        val height: Int
    //        if (aspectRatio > 1) {
    //            width = maxSize
    //            height = (maxSize / aspectRatio).toInt()
    //        } else {
    //            height = maxSize
    //            width = (maxSize * aspectRatio).toInt()
    //        }
    //        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    //    }

        private fun getPreviewBitmap(bitmap: Bitmap, maxSize: Int = 2048): Bitmap {
            val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val width: Int
            val height: Int
            if (aspectRatio > 1) {
                width = maxSize
                height = (maxSize / aspectRatio).toInt()
            } else {
                height = maxSize
                width = (maxSize * aspectRatio).toInt()
            }

            return if (bitmap.width > maxSize || bitmap.height > maxSize) {
                Bitmap.createScaledBitmap(bitmap, width, height, true)
            } else {
                bitmap // Return the original bitmap if it's already smaller than the maxSize
            }
        }




    //        private fun     applyFilterToCurrentImage(filter: GPUImageFilter?) {
    //            val currentItem = binding.viewPager.currentItem
    //            val originalBitmap = adapter!!.getOriginalBitmap(currentItem)
    //
    //            if (originalBitmap != null) {
    //                mainScope.launch(Dispatchers.Default) {
    //                    val resizedBitmap = getResizedBitmap(originalBitmap, 1080, 1080)
    //
    //                    gpuImage.setImage(resizedBitmap)
    //                    gpuImage.setFilter(filter)
    //
    //                    val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //
    //                    withContext(Dispatchers.Main) {
    //                        if (filteredBitmap != null) {
    //                            adapter!!.setBitmap(currentItem, filteredBitmap)
    //                            imageFilters[currentItem] = filter // Store applied filter for the image
    //                            filterCache[currentItem] = filteredBitmap // Cache the filtered bitmap
    //                        }
    //                    }
    //                }
    //            }
    //        }

        // Restore filter when switching between images
        private fun restoreFilterForImage(position: Int) {
            val cachedBitmap = filterCache[position]
            val originalBitmap = adapter!!.getOriginalBitmap(position)

            if (cachedBitmap != null) {
                // Restore the cached filtered image
                adapter!!.setBitmap(position, cachedBitmap)
            } else if (originalBitmap != null) {
                // Reapply the filter if no cached bitmap exists
                val filter = imageFilters[position]
                applyFilterToCurrentImage(filter)
            }
        }

        // Reset to original image with optimized performance
    //    private fun resetToOriginalImage() {
    //        val currentItem = binding.viewPager.currentItem
    //        var originalBitmap = originalBitmapCache[currentItem]
    //
    //        if (originalBitmap == null) {
    //            // If the original bitmap is not cached, get and cache it
    //            val fullOriginalBitmap = adapter!!.getOriginalBitmap(currentItem)
    //            if (fullOriginalBitmap != null) {
    //                originalBitmap = getPreviewBitmap(fullOriginalBitmap, 1080) // Use smaller version for preview
    //                originalBitmapCache[currentItem] = originalBitmap // Cache the resized original bitmap
    //
    //                binding.progressBar.visibility = GONE
    //            }
    //        }
    //
    //        if (originalBitmap != null) {
    //            mainScope.launch(Dispatchers.Default) {
    //                // Offload setting the bitmap to a background thread
    //                withContext(Dispatchers.Main) {
    //                    adapter!!.setBitmap(currentItem, originalBitmap)
    //                    imageFilters[currentItem] = null // Remove any applied filter
    //                    filterCache.remove(currentItem)  // Clear the cached filtered bitmap
    //                    binding.progressBar.visibility = GONE
    //                }
    //            }
    //        }
    //    }

        private fun resetToOriginalImage() {
            val currentItem = binding.viewPager.currentItem
            val originalBitmap = adapter?.getOriginalBitmap(currentItem)

            if (originalBitmap != null) {
                mainScope.launch(Dispatchers.Main) {
                    adapter?.setBitmap(currentItem, originalBitmap)
                    binding.progressBar.visibility = GONE
                }
            } else {
                Log.e("resetToOriginal", "Original bitmap is null for item: $currentItem")
            }
        }



    //        // Applying filter with caching
    //        private fun applyCachedFilter(filter: GPUImageFilter, filterKey: String) {
    //            val cachedBitmap = filterCache[filterKey]
    //            if (cachedBitmap != null) {
    //                adapter?.setBitmap(binding.viewPager.currentItem, cachedBitmap)
    //            } else {
    //                applyFilterInBackground(filter, filterKey)
    //            }
    //        }

        // Offloading the filter application to the background thread
    //        private fun applyFilterInBackground(filter: GPUImageFilter?, filterKey: String) {
    //            if (adapter == null || gpuImage == null) return
    //
    //            mainScope.launch(Dispatchers.Default) {
    //                val currentItem: Int = binding.viewPager.currentItem
    //                val originalBitmap = adapter!!.getOriginalBitmap(currentItem)
    //
    //                if (originalBitmap != null) {
    //                    val resizedBitmap = getResizedBitmap(originalBitmap, 1080, 1080) // Resize large images
    //
    //                    gpuImage.setImage(resizedBitmap)
    //                    gpuImage.setFilter(filter)
    //
    //                    val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //
    //                    withContext(Dispatchers.Main) {
    //                        if (filteredBitmap != null) {
    //                            adapter!!.setBitmap(currentItem, filteredBitmap)
    //                            filterCache[filterKey] = filteredBitmap // Cache the filtered bitmap
    //                        }
    //                    }
    //                }
    //            }
    //        }

        // Real-time filter application for seekbars (contrast, details)
    //        private fun applyRealTimeFilter(filter: GPUImageFilter?, filterKey: String) {
    //            if (adapter == null || gpuImage == null) return
    //
    //            val currentItem: Int = binding.viewPager.currentItem
    //            val originalBitmap = adapter!!.getOriginalBitmap(currentItem)
    //
    //            if (originalBitmap != null && filter != null) {
    //                mainScope.launch(Dispatchers.Default) {
    //                    val resizedBitmap = getResizedBitmap(originalBitmap, 1080, 1080) // Resize large images
    //
    //                    gpuImage.setImage(resizedBitmap)
    //                    gpuImage.setFilter(filter)
    //
    //                    val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //
    //                    withContext(Dispatchers.Main) {
    //                        if (filteredBitmap != null) {
    //                            adapter!!.setBitmap(currentItem, filteredBitmap)
    //                            filterCache[filterKey] = filteredBitmap // Cache the filtered bitmap
    //                        }
    //                    }
    //                }
    //            }
    //        }












        private fun discardDailog() {

            val dialogRename = Dialog(this@EditMoudleActivity, R.style.renameDialogStyle)
            dialogRename.setContentView(R.layout.discard_dailog)

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
                val desiredWidth = screenWidth - 2 * dpToPx(this@EditMoudleActivity, 30)
                val params = dialogRename.window!!.attributes
                params.width = desiredWidth
                window.attributes = params
            }


            val cancelBtn = dialogRename.findViewById<TextView>(R.id.cancelBtn)
            val discardBtn = dialogRename.findViewById<TextView>(R.id.discardBtn)



            cancelBtn.setOnClickListener { dialogRename.dismiss() }

            discardBtn.setOnClickListener {

                dialogRename.dismiss()

                val intent =  Intent(this@EditMoudleActivity, HomeActivity::class.java)
                startActivity(intent    )

            }

            dialogRename.show()

        }






        private fun dpToPx(context: Context, dp: Int): Int {
            return Math.round(dp * context.resources.displayMetrics.density)
        }

        private fun openNextActivity() {
            if (adapter == null) {
                Log.e("checkimage", "Adapter is null, cannot proceed to next activity")
                return
            }

            val uris = mutableListOf<Uri>()
            for (i in 0 until adapter!!.itemCount) {
                val uri = adapter!!.getUri(i)
                if (uri != null) {
                    uris.add(uri)
                }
            }

            val intent = Intent(this, AllEditImagesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            intent.putParcelableArrayListExtra("images", ArrayList(uris))
            intent.putExtra("renameFileName", renameFileName)
            intent.putExtra("modulename", "Image_to_pdf_Module")
            startActivity(intent)

            finish();
            overridePendingTransition(0, 0);  // Disable activity transition animation
        }



        //        Log.d("checkimage", "Proceeded to AllEditImagesActivity with file name: $renameFileName")


        private fun applyFilter(filter: GPUImageFilter?) {
            if (adapter == null) {
                Log.e("filterapply", "Adapter is null, cannot apply filter")
                return
            }

            if (filter == lastFilterApplied) {
                return
            }
            lastFilterApplied = filter
            currentFilter = filter
            val currentItem: Int = binding.viewPager.currentItem
            val originalBitmap = adapter!!.getOriginalBitmap(currentItem)

            if (originalBitmap != null) {
                gpuImage.setImage(originalBitmap)
                val filteredBitmap = if (filter == null) {
                    originalBitmap
                } else {
                    gpuImage.setFilter(filter)
                    gpuImage.bitmapWithFilterApplied
                }
                adapter!!.setBitmap(currentItem, filteredBitmap)
                Log.d("filterapply", "Filter applied at position: $currentItem")
            } else {
                Log.d("filterapply", "Original bitmap is null at position: $currentItem")
            }
        }


        private fun applyRealTimeFilterDummy(filter: GPUImageFilter?) {

            if (filter != null) {
                // Proceed with applying the filter

                val currentItem: Int = binding.viewPager.currentItem
                val originalBitmap = adapter!!.getOriginalBitmap(currentItem)

                if (originalBitmap != null && filter != null) {
                    gpuImage.setImage(originalBitmap) // Reset the GPUImage instance
                    gpuImage.setFilter(filter)
                    val filteredBitmap = gpuImage.bitmapWithFilterApplied
                    if (filteredBitmap != null) {
                        adapter!!.setBitmap(currentItem, filteredBitmap)
                    }
                }

            } else {
                Log.e("EditMoudleActivity", "Filter is null, cannot apply filter")
            }




        }

        private fun applyRealTimeFilter(filter: GPUImageFilter?, switchCompatApplyAll: SwitchCompat) {
            // Ensure adapter and gpuImage are not null
            if (adapter == null || gpuImage == null) {
                Log.e("EditMoudleActivity", "Adapter or GPUImage is null, cannot apply filter")
                return
            }

            // Get the current item from the ViewPager
            val currentItem: Int = binding.viewPager.currentItem
            val originalBitmap = adapter!!.getOriginalBitmap(currentItem)

            // Ensure the original bitmap and the filter are not null
            if (originalBitmap != null && filter != null) {
                // Reset the GPUImage instance with the original bitmap
                gpuImage.setImage(originalBitmap)
                gpuImage.setFilter(filter)

                // Get the filtered bitmap
                val filteredBitmap = gpuImage.bitmapWithFilterApplied
                if (filteredBitmap != null) {
                    adapter!!.setBitmap(currentItem, filteredBitmap)

                    // Apply the filter to all images if the switch is checked
                    if (switchCompatApplyAll.isChecked) {
                        mainScope.launch {
                            applyAllFiltersToAllImages()
                        }
                    }
                } else {
                    Log.e("EditMoudleActivity", "Filtered bitmap is null")
                }
            } else {
                // Log the cause of the issue if the bitmap or filter is null
                if (originalBitmap == null) Log.e("EditMoudleActivity", "Original bitmap is null")
                if (filter == null) Log.e("EditMoudleActivity", "Filter is null")
            }
        }


        private suspend fun applyAllFiltersToAllImages() = withContext(Dispatchers.Default) {
            val filters = GPUImageFilterGroup().apply {
                contrastFilter?.let { addFilter(it) }
                brightnessFilter?.let { addFilter(it) }
                detailsFilter?.let { addFilter(it) }
            }

            for (i in 0 until adapter!!.itemCount) {
                val originalBitmap = adapter!!.getOriginalBitmap(i)
                if (originalBitmap != null) {
                    gpuImage.setImage(originalBitmap) // Reset the GPUImage instance
                    gpuImage.setFilter(filters)
                    val filteredBitmap = gpuImage.bitmapWithFilterApplied
                    withContext(Dispatchers.Main) {
                        adapter!!.setBitmap(i, filteredBitmap)
                    }
                }
            }
        }

        private suspend fun resetAllImages() = withContext(Dispatchers.Default) {
            for (i in 0 until adapter!!.itemCount) {
                val originalBitmap = adapter!!.getOriginalBitmap(i)
                if (originalBitmap != null) {
                    withContext(Dispatchers.Main) {
                        adapter!!.setBitmap(i, originalBitmap)
                    }
                }
            }
        }

        //    private fun resetToOriginalImage() {
        //        // Reset the filters
        //        lastFilterApplied = null
        //        currentFilter = null
        //        contrastFilter = null
        //        brightnessFilter = null
        //        detailsFilter = null
        //
        //        // Get the current item from the ViewPager
        //        val currentItem: Int = binding.viewPager.currentItem
        //
        //        // Check if the adapter is not null and safely retrieve the bitmap
        //        if (adapter != null) {
        //            val originalBitmap = adapter?.getOriginalBitmap(currentItem)
        //
        //            // Ensure the bitmap is not null before applying it
        //            if (originalBitmap != null) {
        //                adapter?.setBitmap(currentItem, originalBitmap)
        //            } else {
        //                Log.e("EditMoudleActivity", "Original bitmap is null for item: $currentItem")
        //            }
        //        } else {
        //            Log.e("EditMoudleActivity", "Adapter is null, cannot reset to original image")
        //        }
        //    }



        private fun cropCurrentImage() {
            val currentItem: Int = binding.viewPager.currentItem

            if (adapter == null) {
                Log.e("cropbtn", "Adapter is null, cannot perform cropping")
                return
            }

            val originalBitmap = adapter!!.getBitmap(currentItem)

            if (originalBitmap != null) {
                Log.d("cropbtn", "Original bitmap found at position: $currentItem")
                val uri = adapter!!.getUriFromBitmap(originalBitmap)

                if (uri != null) {
                    Log.d("cropbtn", "URI found, starting UCrop")
                    val destinationUri = Uri.fromFile(File(cacheDir, "filtered_image_${System.currentTimeMillis()}.png"))

                    try {
                        UCrop.of(uri, destinationUri).start(this, 69)
                    } catch (e: Exception) {
                        Log.e("cropbtn", "UCrop Exception: ${e.message}")
                    }

                } else {
                    Log.e("cropbtn", "URI is null, cannot proceed with cropping")
                }
            } else {
                Log.e("cropbtn", "Original bitmap is null at position: $currentItem")
            }
        }
        private fun rotateImage(degrees: Float) {
            val currentItem: Int = binding.viewPager.currentItem

            // Check if the adapter is null
            if (adapter == null) {
                Log.e("rotateImage", "Adapter is null, cannot rotate image")
                return
            }

            // Get the bitmap for the current item and check if it's null
            val originalBitmap = adapter!!.getBitmap(currentItem)
            if (originalBitmap == null) {
                Log.e("rotateImage", "Original bitmap is null at position: $currentItem, cannot rotate image")
                return
            }

            try {
                // Create a matrix to rotate the bitmap
                val matrix = Matrix().apply { postRotate(degrees) }

                // Create a new rotated bitmap
                val rotatedBitmap = Bitmap.createBitmap(
                    originalBitmap, 0, 0,
                    originalBitmap.width, originalBitmap.height,
                    matrix, true
                )

                // Set the rotated bitmap as the original in the adapter
                adapter!!.setOriginalBitmap(currentItem, rotatedBitmap)

                // Apply the current filter, if available, or set the rotated bitmap
                currentFilter?.let {
                    gpuImage.setImage(rotatedBitmap)
                    gpuImage.setFilter(it)
                    val filteredBitmap = gpuImage.bitmapWithFilterApplied
                    adapter!!.setBitmap(currentItem, filteredBitmap)
                } ?: adapter!!.setBitmap(currentItem, rotatedBitmap)

                Log.d("rotateImage", "Image rotated successfully")

            } catch (e: Exception) {
                Log.e("rotateImage", "Error rotating image: ${e.message}")
            }
        }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
                val resultUri = UCrop.getOutput(data!!)
                Log.d("checkbitmap", "Cropped image result URI: $resultUri")

                val currentItem: Int = binding.viewPager.currentItem
                val croppedBitmap = adapter?.getBitmapFromUri(resultUri)
                if (croppedBitmap != null) {
                    adapter?.setOriginalBitmap(currentItem, croppedBitmap)
                    currentFilter?.let {
                        gpuImage.setImage(croppedBitmap)
                        gpuImage.setFilter(it)
                        val filteredBitmap = gpuImage.bitmapWithFilterApplied
                        adapter?.setBitmap(currentItem, filteredBitmap)
                    }
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(data!!)
                Log.e("CropError", "UCrop Error: ${cropError?.message}")
            }
        }


        private val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(100)) { uris ->
                val sharedPreferences = getSharedPreferences("image_picker_prefs", MODE_PRIVATE)
                val freeUsageCount = sharedPreferences.getInt("free_usage_count", 0)

                if (uris != null && uris.isNotEmpty()) {

                    handleImageSelection(uris)

    //                if (freeUsageCount < 3) {
    //                    // Free unlimited selection for the first 3 times
    //                    sharedPreferences.edit().putInt("free_usage_count", freeUsageCount + 1).apply()
    //                    handleImageSelection(uris)
    //                    Log.d("RewardedAd", "reeUsageCount < 3")
    //                } else if (uris.size <= 15) {
    //                    // Free user selection (limit 15 images)
    //                    handleImageSelection(uris)
    //                    Log.d("RewardedAd", "<= 15")
    //
    //                } else {
    //                    // Show Rewarded Ad for unlimited selection
    //
    //                    rewardedAd?.let { ad ->
    //                        ad.show(this, OnUserEarnedRewardListener { rewardItem ->
    //                            // Handle the reward.
    //                            val rewardAmount = rewardItem.amount
    //                            val rewardType = rewardItem.type
    //                            handleImageSelection(uris)
    //                            Log.d("RewardedAd", "rewardedAd show un limited")
    //                        })
    //                    } ?: run {
    //                        Log.d("RewardedAd", "Ad not ready")
    //                        Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show()
    //                    }
    //
    //
    ////                    if (::rewardedAd.isInitialized) {
    ////                        rewardedAd.show(this) {
    ////                            // Ad watched, allow unlimited selection for this session
    ////                            handleImageSelection(uris)
    ////                            Log.d("RewardedAd", "rewardedAd show un limited")
    ////
    ////                        }
    ////                    } else {
    ////                        Log.d("RewardedAd", "Ad not ready")
    ////                        Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show()
    ////                    }
    //                }
                } else {
                    // No media selected
                    Log.d("PhotoPicker", "No media selected")
                    val intent = Intent(this@EditMoudleActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        private fun handleImageSelection(uris: List<Uri>) {

           val dailog = showCounImageDailog(uris)
            dailog.show()

            for (uri in uris) {
                // Take persistable URI permission for each image
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            dailog.dismiss()
            setupViewPager(uris)
        }



    //    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(100)) { uris ->
    //
    //        if (uris != null && uris.isNotEmpty()) {
    //            for (uri in uris) {
    //                // Take persistable URI permission for each image
    //                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    //
    //            }
    //
    //            showCounImageDailog(uris)
    //
    //            setupViewPager(uris)
    //        } else {
    //
    //
    //            val intent = Intent(this@EditMoudleActivity,HomeActivity::class.java)
    //            startActivity(intent)
    //            finish()
    //
    //
    //
    //            Log.d("PhotoPicker11", "No media selected")
    //        }
    //
    //
    //    }

        private fun showCounImageDailog(uris: List<@JvmSuppressWildcards Uri>): Dialog {

            val dialog = Dialog(this@EditMoudleActivity, R.style.renameDialogStyle)
            dialog.setContentView(R.layout.show_count_images_dailog)

            val window = dialog.window
            if (window != null) {
                window.setGravity(Gravity.CENTER)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                window.setBackgroundDrawableResource(android.R.color.transparent)
                val metrics = Resources.getSystem().displayMetrics
                val screenWidth = metrics.widthPixels
                val desiredWidth = screenWidth - 2 * dpToPx(this@EditMoudleActivity, 30)
                val params = dialog.window!!.attributes
                params.width = desiredWidth
                window.attributes = params
            }

            val selectedMediaCancelBtn = dialog.findViewById<TextView>(R.id.selectedMediaCancelBtn)
            val contentMedia = dialog.findViewById<TextView>(R.id.contentMedia)

            contentMedia.text = "1 of " + uris.size + " Ready"



            selectedMediaCancelBtn.setOnClickListener {


                dialog.dismiss()

            }

            dialog.window?.apply {
                // Set the layout parameters to match parent width and wrap content in height
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)




                // Set the background to be transparent
                setBackgroundDrawableResource(android.R.color.transparent)
                // Apply additional custom styling or layout parameters here
                val metrics = Resources.getSystem().displayMetrics
                val screenWidth = metrics.widthPixels
                val desiredWidth = screenWidth - 2 * dpToPx(context, 30) // For example
                val params = attributes
                params.width = desiredWidth
                attributes = params
            }


            return dialog


    //        dialog.dismiss()
        }

        private fun reOpenImagePicker() {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        override fun onBackPressed() {
            discardDailog()
        }


        private fun setupViewPager(uriList: List<Uri>) {
            adapter = ImagePagerAdapter(this, uriList, binding.deleteBtn)
            binding.viewPager.adapter = adapter
            Log.d("filterapply", "itemCount " + binding.viewPager.adapter?.itemCount.toString())

            val currentItem: Int = binding.viewPager.currentItem
            binding.pageNumber.text =
                (currentItem + 1).toString() + "/" + binding.viewPager.adapter?.itemCount.toString()

            binding.backViewPagerPage.setOnClickListener {
                val currentItem: Int = binding.viewPager.currentItem
                if (currentItem > 0) {
                    binding.viewPager.currentItem = currentItem - 1
                    binding.pageNumber.text =
                        (binding.viewPager.currentItem + 1).toString() + "/" + binding.viewPager.adapter?.itemCount.toString()
                }
            }

            binding.againViewPagerPage.setOnClickListener {
                val currentItem: Int = binding.viewPager.currentItem
                if (currentItem < adapter!!.itemCount - 1) {
                    binding.viewPager.currentItem = currentItem + 1

                    binding.pageNumber.text = (binding.viewPager.currentItem + 1).toString() + "/" + binding.viewPager.adapter?.itemCount.toString()
                }
            }

            binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.e("Selected_Page", " onPageSelected  "+position.toString())
                    binding.pageNumber.text = (position + 1).toString() + "/" + binding.viewPager.adapter?.itemCount.toString()

                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    Log.e("Selected_Page", " onPageScrollStateChanged  "+state.toString())
                }
            })


        }

        private fun debounce(action: () -> Unit) {
            debounceRunnable?.let { handler.removeCallbacks(it) }
            debounceRunnable = Runnable { action() }
            handler.postDelayed(debounceRunnable!!, debounceDelay)
        }

        override fun onDestroy() {
            super.onDestroy()
            mainScope.cancel()
        }
    }















































    //class EditMoudleActivity : AppCompatActivity() {
    //
    //    lateinit var binding: ActivityEditMoudleBinding
    //    private var adapter: ImagePagerAdapter? = null
    //    private lateinit var gpuImage: GPUImage
    //    private var currentFilter: GPUImageFilter? = null
    //
    //    override fun onCreate(savedInstanceState: Bundle?) {
    //        super.onCreate(savedInstanceState)
    //        binding = ActivityEditMoudleBinding.inflate(layoutInflater)
    //        setContentView(binding.root)
    //
    //        gpuImage = GPUImage(this)
    //
    //        binding.buttonGallery.setOnClickListener {
    //            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    //        }
    //
    //        binding.buttonOriginal.setOnClickListener { applyFilter(null) }
    //        binding.buttonDocs.setOnClickListener { applyFilter(GPUImageGrayscaleFilter()) }
    //        binding.buttonImage.setOnClickListener { applyFilter(GPUImageSepiaToneFilter()) }
    //        binding.buttonEnhance.setOnClickListener { applyFilter(CustomEnhanceFilter()) }
    //        binding.buttonContrast.setOnClickListener { toggleSeekBarVisibility(binding.seekBarContrast) }
    //        binding.buttonBrightness.setOnClickListener { toggleSeekBarVisibility(binding.seekBarBrightness) }
    //
    //        binding.seekBarContrast.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    //            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    //                val contrast = progress / 100.0f * 2 // Convert progress to contrast level (0 to 2)
    //                applyRealTimeFilter(GPUImageContrastFilter(contrast))
    //            }
    //
    //            override fun onStartTrackingTouch(seekBar: SeekBar) {}
    //            override fun onStopTrackingTouch(seekBar: SeekBar) {}
    //        })
    //
    //        binding.seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    //            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    //                val brightness = progress / 100.0f * 2 - 1 // Convert progress to brightness level (-1 to 1)
    //                applyRealTimeFilter(GPUImageBrightnessFilter(brightness))
    //            }
    //
    //            override fun onStartTrackingTouch(seekBar: SeekBar) {}
    //            override fun onStopTrackingTouch(seekBar: SeekBar) {}
    //        })
    //
    //        binding.buttonPrevious.setOnClickListener {
    //            val currentItem: Int = binding.viewPager.currentItem
    //            if (currentItem > 0) {
    //                binding.viewPager.currentItem = currentItem - 1
    //            }
    //        }
    //
    //        binding.buttonNext.setOnClickListener {
    //            val currentItem: Int = binding.viewPager.currentItem
    //            if (currentItem < adapter!!.itemCount - 1) {
    //                binding.viewPager.currentItem = currentItem + 1
    //            }
    //        }
    //
    //        binding.SwitchCompatApplyAll.setOnCheckedChangeListener { _, isChecked ->
    //            if (isChecked) {
    //                applyFilterToAllImages(currentFilter)
    //            }else {
    //                resetAllImages()
    //            }
    //        }
    //    }
    //
    //    private fun resetAllImages() {
    //        for (i in 0 until adapter!!.itemCount) {
    //            val originalBitmap = adapter!!.getBitmap(i)
    //            if (originalBitmap != null) {
    //                adapter!!.setBitmap(i, originalBitmap)
    //            }
    //        }
    //    }
    //
    //    private fun toggleSeekBarVisibility(seekBar: SeekBar) {
    //        seekBar.visibility = if (seekBar.visibility == View.GONE) View.VISIBLE else View.GONE
    //    }
    //
    //    private fun applyFilter(filter: GPUImageFilter?) {
    //        currentFilter = filter
    //        val currentItem: Int = binding.viewPager.currentItem
    //        val originalBitmap = adapter!!.getBitmap(currentItem)
    //        if (originalBitmap != null) {
    //            val filteredBitmap: Bitmap
    //            filteredBitmap = if (filter == null) {
    //                // Apply the original bitmap
    //                originalBitmap
    //            } else {
    //                gpuImage.setImage(originalBitmap)
    //                gpuImage.setFilter(filter)
    //                gpuImage.bitmapWithFilterApplied
    //            }
    //            Log.d("filterapply", "Filter applied at position: $currentItem")
    //            adapter!!.setBitmap(currentItem, filteredBitmap)
    //        } else {
    //            Log.d("filterapply", "Original bitmap is null at position: $currentItem")
    //        }
    //    }
    //
    //    private fun applyRealTimeFilter(filter: GPUImageFilter?) {
    //        val currentItem: Int = binding.viewPager.currentItem
    //        val originalBitmap = adapter!!.getBitmap(currentItem)
    //
    //        if (originalBitmap != null && filter != null) {
    //            gpuImage.setImage(originalBitmap)
    //            gpuImage.setFilter(filter)
    //            val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //            if (filteredBitmap != null) {
    //                adapter!!.setBitmap(currentItem, filteredBitmap)
    //            }
    //        }
    //    }
    //
    //    private fun applyFilterToAllImages(filter: GPUImageFilter?) {
    //        for (i in 0 until adapter!!.itemCount) {
    //            val originalBitmap = adapter!!.getBitmap(i)
    //            if (originalBitmap != null) {
    //                val filteredBitmap: Bitmap
    //                filteredBitmap = if (filter == null) {
    //                    originalBitmap
    //                } else {
    //                    gpuImage.setImage(originalBitmap)
    //                    gpuImage.setFilter(filter)
    //                    gpuImage.bitmapWithFilterApplied
    //                }
    //                adapter!!.setBitmap(i, filteredBitmap)
    //            }
    //        }
    //    }
    //
    //    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(8)) { uris ->
    //        if (uris.isNotEmpty()) {
    //            Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
    //            setupViewPager(uris)
    //        } else {
    //            Log.d("PhotoPicker", "No media selected")
    //        }
    //    }
    //
    //    private fun setupViewPager(uriList: List<Uri>) {
    //        adapter = ImagePagerAdapter(this, uriList)
    //        binding.viewPager.adapter = adapter
    //    }
    //}
































































    //package com.pixelz360.docsign.imagetopdf.creator.editModule
    //
    //
    //import android.app.Dialog
    //import android.content.Context
    //import android.content.Intent
    //import android.content.res.Resources
    //import android.graphics.Bitmap
    //import android.graphics.Matrix
    //import android.net.Uri
    //import android.os.Bundle
    //import android.os.Handler
    //import android.os.Looper
    //import android.util.Log
    //import android.view.Gravity
    //import android.view.View.GONE
    //import android.view.View.VISIBLE
    //import android.view.ViewGroup
    //import android.widget.ImageView
    //import android.widget.LinearLayout
    //import android.widget.SeekBar
    //import android.widget.TextView
    //import android.widget.Toast
    //import androidx.activity.result.PickVisualMediaRequest
    //import androidx.activity.result.contract.ActivityResultContracts
    //import androidx.appcompat.app.AppCompatActivity
    //import androidx.appcompat.widget.SwitchCompat
    //import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
    //import com.cleversolutions.ads.AdCallback
    //import com.cleversolutions.ads.AdLoadCallback
    //import com.cleversolutions.ads.AdStatusHandler
    //import com.cleversolutions.ads.AdType
    //import com.cleversolutions.ads.android.CAS
    //import com.google.android.material.textfield.TextInputEditText
    //import com.google.firebase.analytics.FirebaseAnalytics
    //import com.pixelz360.docsign.imagetopdf.creator.MainActivity
    //import com.pixelz360.docsign.imagetopdf.creator.R
    //import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityEditMoudleBinding
    //import com.yalantis.ucrop.UCrop
    //import jp.co.cyberagent.android.gpuimage.GPUImage
    //import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
    //import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
    //import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
    //import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
    //import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
    //import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
    //import kotlinx.coroutines.Dispatchers
    //import kotlinx.coroutines.MainScope
    //import kotlinx.coroutines.Runnable
    //import kotlinx.coroutines.cancel
    //import kotlinx.coroutines.launch
    //import kotlinx.coroutines.withContext
    //import java.io.File
    //
    //
    //class EditMoudleActivity : AppCompatActivity() {
    //
    //
    //
    //    lateinit var binding: ActivityEditMoudleBinding
    //    private var adapter: ImagePagerAdapter? = null
    //    private lateinit var gpuImage: GPUImage
    //    private var currentFilter: GPUImageFilter? = null
    //    private var lastFilterApplied: GPUImageFilter? = null
    //
    //    private var contrastFilter: GPUImageContrastFilter? = null
    //    private var brightnessFilter: GPUImageBrightnessFilter? = null
    //    private var detailsFilter: CustomDetailsFilter? = null
    //
    //    private val mainScope = MainScope()
    //
    //    // Variables to store seekbar progress and switch state
    //    private var savedContrastProgress = 0
    //    private var savedBrightnessProgress = 0
    //    private var savedDetailsProgress = 0
    //    private var isSwitchChecked = false
    //
    //    private val handler = Handler(Looper.getMainLooper())
    //    private var debounceRunnable: Runnable? = null
    //    private val debounceDelay = 300L // Adjust debounce delay as needed
    //
    //
    //    //name with extension of the image
    //
    //
    //    //name with extension of the image
    //    var timestamp = System.currentTimeMillis()
    //    var mainFileName = "Untitled_file_Doc24 $timestamp"
    //
    //    var renameFileName = mainFileName
    //
    //
    //
    //    override fun onCreate(savedInstanceState: Bundle?) {
    //        super.onCreate(savedInstanceState)
    //        binding = ActivityEditMoudleBinding.inflate(layoutInflater)
    //        setContentView(binding.root)
    //
    //
    //
    //
    //        gpuImage = GPUImage(this)
    //
    ////        if (renameFileName.length >= 10) {
    ////            renameFileName = renameFileName.substring(0, 10)+ "...";
    ////
    ////        }
    //        Log.d("checkame",renameFileName)
    //
    //        binding.fileName.setText(renameFileName)
    //
    //
    //        // Load Interstitial Ad
    //        loadInterstitialAd()
    //
    //
    //        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    //
    //        binding.adjustBtn.setOnClickListener {
    //            val dialog = Dialog(this@EditMoudleActivity, R.style.FileSortingDialogStyle)
    //            dialog.setContentView(R.layout.adjust_dailog)
    //
    //            val window = dialog.window
    //            if (window != null) {
    //                window.setGravity(Gravity.BOTTOM)
    //                window.setLayout(
    //                    ViewGroup.LayoutParams.MATCH_PARENT,
    //                    ViewGroup.LayoutParams.WRAP_CONTENT
    //                )
    //            }
    //
    //            val SwitchCompatApplyAll = dialog.findViewById<SwitchCompat>(R.id.SwitchCompatApplyAll)
    //            val buttonContrast = dialog.findViewById<LinearLayout>(R.id.buttonContrast)
    //            val buttonBrightness = dialog.findViewById<LinearLayout>(R.id.buttonBrightness)
    //            val buttonDetails = dialog.findViewById<LinearLayout>(R.id.buttonDetails)
    //            val seekBarContrast = dialog.findViewById<SeekBar>(R.id.seekBarContrast)
    //            val seekBarBrightness = dialog.findViewById<SeekBar>(R.id.seekBarBrightness)
    //            val seekBarDetails = dialog.findViewById<SeekBar>(R.id.seekBarDetails)
    //            val contrastIcon = dialog.findViewById<ImageView>(R.id.contrastIcon)
    //            val brightnessIcon = dialog.findViewById<ImageView>(R.id.brightnessIcon)
    //            val detailsIcon = dialog.findViewById<ImageView>(R.id.detailsIcon)
    //            val dissmissBtn = dialog.findViewById<ImageView>(R.id.dissmissBtn)
    //            val saveBtn = dialog.findViewById<ImageView>(R.id.saveBtn)
    //
    //
    //            // Log a predefined event
    //            val analytics = FirebaseAnalytics.getInstance(this)
    //            val bundle = Bundle()
    //            bundle.putString("activity_name", "EditMoudleActivity")
    //            analytics.logEvent("activity_created", bundle)
    //// Using predefined Firebase Analytics events
    //            // Using predefined Firebase Analytics events
    //            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "EditMoudleActivity")
    //            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
    //            analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    //
    //
    //            dissmissBtn.setOnClickListener {
    //                dialog.dismiss()
    //            }
    //
    //            saveBtn.setOnClickListener {
    //                dialog.dismiss()
    //            }
    //
    //            // Restore saved state
    //            seekBarContrast.progress = savedContrastProgress
    //            seekBarBrightness.progress = savedBrightnessProgress
    //            seekBarDetails.progress = savedDetailsProgress
    //            SwitchCompatApplyAll.isChecked = isSwitchChecked
    //
    //            SwitchCompatApplyAll.setOnCheckedChangeListener { _, isChecked ->
    //                isSwitchChecked = isChecked
    //                if (seekBarContrast.progress == 0 && seekBarBrightness.progress == 0 && seekBarDetails.progress == 0) {
    //                    Toast.makeText(this, "Please adjust the filter first", Toast.LENGTH_SHORT).show()
    //                    SwitchCompatApplyAll.isChecked = false
    //                    isSwitchChecked = false
    //                } else {
    //                    debounce {
    //                        if (isChecked) {
    //                            mainScope.launch { applyAllFiltersToAllImages() }
    //                        } else {
    //                            mainScope.launch { resetAllImages() }
    //                        }
    //                    }
    //                }
    //            }
    //
    //            buttonContrast.setOnClickListener {
    //                seekBarContrast.visibility = VISIBLE
    //                seekBarBrightness.visibility = GONE
    //                seekBarDetails.visibility = GONE
    //
    //                contrastIcon.setImageDrawable(resources.getDrawable(R.drawable.constrat_icon))
    //                brightnessIcon.setImageDrawable(resources.getDrawable(R.drawable.brightness_icon))
    //                detailsIcon.setImageDrawable(resources.getDrawable(R.drawable.details_icon))
    //            }
    //
    //            buttonBrightness.setOnClickListener {
    //                seekBarContrast.visibility = GONE
    //                seekBarBrightness.visibility = VISIBLE
    //                seekBarDetails.visibility = GONE
    //
    //                contrastIcon.setImageDrawable(resources.getDrawable(R.drawable.unselected_constrat_icon))
    //                brightnessIcon.setImageDrawable(resources.getDrawable(R.drawable.brightness_selected_icon))
    //                detailsIcon.setImageDrawable(resources.getDrawable(R.drawable.details_icon))
    //            }
    //
    //            buttonDetails.setOnClickListener {
    //                seekBarContrast.visibility = GONE
    //                seekBarBrightness.visibility = GONE
    //                seekBarDetails.visibility = VISIBLE
    //
    //                contrastIcon.setImageDrawable(resources.getDrawable(R.drawable.unselected_constrat_icon))
    //                brightnessIcon.setImageDrawable(resources.getDrawable(R.drawable.brightness_icon))
    //                detailsIcon.setImageDrawable(resources.getDrawable(R.drawable.details_selected_icon))
    //            }
    //
    //            seekBarContrast.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    //                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    //                    debounce {
    //                        val contrast = progress / 100.0f * 2 // Convert progress to contrast level (0 to 2)
    //                        contrastFilter = GPUImageContrastFilter(contrast)
    //                        applyRealTimeFilter(contrastFilter, SwitchCompatApplyAll)
    //                        savedContrastProgress = progress
    //                    }
    //                }
    //
    //                override fun onStartTrackingTouch(seekBar: SeekBar) {}
    //                override fun onStopTrackingTouch(seekBar: SeekBar) {}
    //            })
    //
    //            seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    //                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    //                    debounce {
    //                        val brightness = progress / 100.0f * 2 - 1 // Convert progress to brightness level (-1 to 1)
    //                        brightnessFilter = GPUImageBrightnessFilter(brightness)
    //                        applyRealTimeFilter(brightnessFilter, SwitchCompatApplyAll)
    //                        savedBrightnessProgress = progress
    //                    }
    //                }
    //
    //                override fun onStartTrackingTouch(seekBar: SeekBar) {}
    //                override fun onStopTrackingTouch(seekBar: SeekBar) {}
    //            })
    //
    //            seekBarDetails.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    //                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    //                    debounce {
    //                        val detailsLevel = progress / 100.0f * 4 - 2 // Convert progress to details level (-2 to 2)
    //                        detailsFilter = CustomDetailsFilter(detailsLevel)
    //                        applyRealTimeFilter(detailsFilter, SwitchCompatApplyAll)
    //                        savedDetailsProgress = progress
    //                    }
    //                }
    //
    //                override fun onStartTrackingTouch(seekBar: SeekBar) {}
    //                override fun onStopTrackingTouch(seekBar: SeekBar) {}
    //            })
    //
    //            dialog.setOnDismissListener {
    //                savedContrastProgress = seekBarContrast.progress
    //                savedBrightnessProgress = seekBarBrightness.progress
    //                savedDetailsProgress = seekBarDetails.progress
    //                isSwitchChecked = SwitchCompatApplyAll.isChecked
    //            }
    //
    //            dialog.show()
    //        }
    //
    //
    //        binding.editBtn.setOnClickListener {
    //
    //            val dialog = Dialog(this@EditMoudleActivity, R.style.renameDialogStyle)
    //            dialog.setContentView(R.layout.rename_dailog)
    //
    //
    //            val window = dialog.window
    //            if (window != null) {
    //                window.setGravity(Gravity.CENTER)
    //                window.setLayout(
    //                    ViewGroup.LayoutParams.MATCH_PARENT,
    //                    ViewGroup.LayoutParams.WRAP_CONTENT
    //                )
    //                window.setBackgroundDrawableResource(android.R.color.transparent)
    //                val metrics = Resources.getSystem().displayMetrics
    //                val screenWidth = metrics.widthPixels
    //                val desiredWidth: Int = screenWidth - 2 * dpToPx(this@EditMoudleActivity, 30)
    //                val params = window.attributes
    //                params.width = desiredWidth
    //                window.attributes = params
    //            }
    //
    //            val pdfNewNameEt = dialog.findViewById<TextInputEditText>(R.id.pdfNewNameEt)
    //            val renameBtn = dialog.findViewById<TextView>(R.id.renameBtn)
    //            val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)
    //            val clearTextIcon = dialog.findViewById<ImageView>(R.id.clearTextIcon)
    //
    //            clearTextIcon.setOnClickListener { pdfNewNameEt.setText("") }
    //
    //            pdfNewNameEt.setText(renameFileName)
    //            cancelBtn.setOnClickListener { dialog.dismiss() }
    //
    //            renameBtn.setOnClickListener {
    //                val newName = pdfNewNameEt.text.toString().trim { it <= ' ' }
    //                if (newName.isEmpty()) {
    //                    Toast.makeText(this@EditMoudleActivity, "Please enter a name for the PDF", Toast.LENGTH_SHORT).show()
    //                } else {
    //                    renameFileName = newName
    //                    binding.fileName.setText(renameFileName)
    //                    dialog.dismiss()
    //                }
    //            }
    //
    //
    //            dialog.show()
    //
    //
    //
    //
    //        }
    //
    //        binding.backButton.setOnClickListener {
    //
    //
    //            discardDailog()
    //
    //        }
    //
    //        binding.buttonOriginal.setOnClickListener {
    //            resetToOriginalImage()
    //
    ////            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.orignal_icon))
    ////            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.doc_unselcted_icon))
    ////            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.image_unselcted_icon))
    ////            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_unselcted_icon))
    //
    //            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_selected))
    //            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.docs_second_un_selected_icon))
    //            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_un_selected_icon))
    //            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_un_selected_icon))
    //
    //
    ////            binding.orignalTextView.setBackgroundResource(R.drawable.pdf_item_name_bg)
    ////            binding.docTextView.setBackgroundResource(R.drawable.edit_color_unselected_bg)
    ////            binding.imageTextView.setBackgroundResource(R.drawable.edit_color_unselected_bg)
    ////            binding.enhanceTextView.setBackgroundResource(R.drawable.edit_color_unselected_bg)
    //
    //            Toast.makeText(this, "Applying filter…", Toast.LENGTH_SHORT).show()
    //
    //        }
    //        binding.buttonDocs.setOnClickListener {
    //            if (lastFilterApplied !is GPUImageGrayscaleFilter) {
    //                applyFilter(GPUImageGrayscaleFilter())
    //            }
    //
    ////            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.orignal_unselcted_icon))
    ////            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.doc_selcted_icon))
    ////            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.image_unselcted_icon))
    ////            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_unselcted_icon))
    //
    //            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
    //            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.doc_second_selected_icon))
    //            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_un_selected_icon))
    //            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_un_selected_icon))
    //
    //
    //
    //
    //            Toast.makeText(this, "Applying filter…", Toast.LENGTH_SHORT).show()
    //        }
    //        binding.buttonImage.setOnClickListener {
    //            if (lastFilterApplied !is GPUImageSepiaToneFilter) {
    //                applyRealTimeFilterDummy(GPUImageContrastFilter(1.72F))
    //            }
    //
    //
    //            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
    //            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.docs_second_un_selected_icon))
    //            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_second_selected_icon))
    //            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_un_selected_icon))
    //
    //            Toast.makeText(this, "Applying filter…", Toast.LENGTH_SHORT).show()
    //
    //        }
    //        binding.buttonEnhance.setOnClickListener {
    //            if (lastFilterApplied !is CustomEnhanceFilter) {
    //                applyFilter(CustomEnhanceFilter())
    //            }
    //
    ////            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.orignal_unselcted_icon))
    ////            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.doc_unselcted_icon))
    ////            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.image_unselcted_icon))
    ////            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_selcted_icon))
    //
    //
    //
    //            binding.orgnalIcon.setImageDrawable(resources.getDrawable(R.drawable.original_second_un_selected))
    //            binding.docIcon.setImageDrawable(resources.getDrawable(R.drawable.docs_second_un_selected_icon))
    //            binding.imageIcon.setImageDrawable(resources.getDrawable(R.drawable.spark_black_un_selected_icon))
    //            binding.enhanceIcon.setImageDrawable(resources.getDrawable(R.drawable.enhance_second_selected_icon))
    //
    //
    //            Toast.makeText(this, "Applying filter…", Toast.LENGTH_SHORT).show()
    //
    //
    ////            binding.orignalTextView.setBackgroundResource(R.drawable.edit_color_unselected_bg)
    ////            binding.docTextView.setBackgroundResource(R.drawable.edit_color_unselected_bg)
    ////            binding.imageTextView.setBackgroundResource(R.drawable.edit_color_unselected_bg)
    ////            binding.enhanceTextView.setBackgroundResource(R.drawable.pdf_item_name_bg)
    //        }
    //
    //
    //
    //
    //
    //
    //
    //        binding.doneBtn.setOnClickListener {
    ////            openNextActivity()
    //
    //
    //            if (CAS.getManager()?.isInterstitialReady == true) {
    //                CAS.getManager()?.showInterstitial(this, object : AdCallback {
    //                    override fun onShown(ad: AdStatusHandler) {
    //                        Log.d("CAS", "Interstitial ad shown.")
    //                    }
    //
    //                    override fun onShowFailed(message: String) {
    //                        Log.e("CAS", "Ad show failed: $message")
    //                        openNextActivity()
    //
    //
    //                    }
    //
    //                    override fun onClosed() {
    //                        Log.d("CAS", "Interstitial ad closed.")
    //
    //                        openNextActivity()
    //
    //                    }
    //                })
    //            } else {
    //                openNextActivity()
    //
    //            }
    //
    //
    //
    //
    //        }
    //
    //        binding.cropBtn.setOnClickListener {
    //            cropCurrentImage()
    //            Log.d("cropbtn", "Crop Button click")
    //
    //        }
    //
    //        binding.buttonPrevious.setOnClickListener {
    //            rotateImage(-90f)
    //        }
    //
    //        binding.buttonNext.setOnClickListener {
    //            rotateImage(90f)
    //        }
    //    }
    //
    //    private fun loadInterstitialAd() {
    //        CAS.getManager()?.loadInterstitial()
    //        CAS.getManager()?.onAdLoadEvent?.add(object : AdLoadCallback {
    //            override fun onAdLoaded(type: AdType) {
    //                if (type == AdType.Interstitial) {
    //                    Log.d("CAS", "Interstitial ad loaded.")
    //                }
    //            }
    //
    //            override fun onAdFailedToLoad(type: AdType, error: String?) {
    //                if (type == AdType.Interstitial) {
    //                    Log.e("CAS", "Interstitial ad failed to load: $error")
    //                    Toast.makeText(this@EditMoudleActivity, "Interstitial ad failed to load: $error", Toast.LENGTH_SHORT).show()
    //                }
    //            }
    //        })
    //    }
    //
    //    private fun discardDailog() {
    //
    //        val dialogRename = Dialog(this@EditMoudleActivity, R.style.renameDialogStyle)
    //        dialogRename.setContentView(R.layout.discard_dailog)
    //
    //        val window = dialogRename.window
    //        if (window != null) {
    //            window.setGravity(Gravity.CENTER)
    //            window.setLayout(
    //                ViewGroup.LayoutParams.MATCH_PARENT,
    //                ViewGroup.LayoutParams.WRAP_CONTENT
    //            )
    //            window.setBackgroundDrawableResource(android.R.color.transparent)
    //            val metrics = Resources.getSystem().displayMetrics
    //            val screenWidth = metrics.widthPixels
    //            val desiredWidth = screenWidth - 2 * dpToPx(this@EditMoudleActivity, 30)
    //            val params = dialogRename.window!!.attributes
    //            params.width = desiredWidth
    //            window.attributes = params
    //        }
    //
    //
    //        val cancelBtn = dialogRename.findViewById<TextView>(R.id.cancelBtn)
    //        val discardBtn = dialogRename.findViewById<TextView>(R.id.discardBtn)
    //
    //
    //
    //        cancelBtn.setOnClickListener { dialogRename.dismiss() }
    //
    //        discardBtn.setOnClickListener {
    //
    //            dialogRename.dismiss()
    //
    //            val intent =  Intent(this@EditMoudleActivity,MainActivity::class.java)
    //            startActivity(intent    )
    //
    //        }
    //
    //        dialogRename.show()
    //
    //    }
    //
    //
    //
    //
    //
    //
    //    private fun dpToPx(context: Context, dp: Int): Int {
    //        return Math.round(dp * context.resources.displayMetrics.density)
    //    }
    //
    //    private fun     openNextActivity() {
    //        if (adapter == null) {
    //            Log.e("checkimage", "Adapter is null, cannot proceed to next activity")
    //            return
    //        }
    //
    //        val uris = mutableListOf<Uri>()
    //        for (i in 0 until adapter!!.itemCount) {
    //            val uri = adapter!!.getUri(i)
    //            if (uri != null) {
    //                uris.add(uri)
    //            }
    //        }
    //
    //        val intent = Intent(this, AllEditImagesActivity::class.java)
    //        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    //
    //        intent.putParcelableArrayListExtra("images", ArrayList(uris))
    //        intent.putExtra("renameFileName", renameFileName)
    //        startActivity(intent)
    //
    //        finish();
    //        overridePendingTransition(0, 0);  // Disable activity transition animation
    //    }
    //
    //
    //
    ////        Log.d("checkimage", "Proceeded to AllEditImagesActivity with file name: $renameFileName")
    //
    //
    //    private fun applyFilter(filter: GPUImageFilter?) {
    //        if (adapter == null) {
    //            Log.e("filterapply", "Adapter is null, cannot apply filter")
    //            return
    //        }
    //
    //        if (filter == lastFilterApplied) {
    //            return
    //        }
    //        lastFilterApplied = filter
    //        currentFilter = filter
    //        val currentItem: Int = binding.viewPager.currentItem
    //        val originalBitmap = adapter!!.getOriginalBitmap(currentItem)
    //
    //        if (originalBitmap != null) {
    //            gpuImage.setImage(originalBitmap)
    //            val filteredBitmap = if (filter == null) {
    //                originalBitmap
    //            } else {
    //                gpuImage.setFilter(filter)
    //                gpuImage.bitmapWithFilterApplied
    //            }
    //            adapter!!.setBitmap(currentItem, filteredBitmap)
    //            Log.d("filterapply", "Filter applied at position: $currentItem")
    //        } else {
    //            Log.d("filterapply", "Original bitmap is null at position: $currentItem")
    //        }
    //    }
    //
    //
    //    private fun applyRealTimeFilterDummy(filter: GPUImageFilter?) {
    //
    //        if (filter != null) {
    //            // Proceed with applying the filter
    //
    //            val currentItem: Int = binding.viewPager.currentItem
    //            val originalBitmap = adapter!!.getOriginalBitmap(currentItem)
    //
    //            if (originalBitmap != null && filter != null) {
    //                gpuImage.setImage(originalBitmap) // Reset the GPUImage instance
    //                gpuImage.setFilter(filter)
    //                val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //                if (filteredBitmap != null) {
    //                    adapter!!.setBitmap(currentItem, filteredBitmap)
    //                }
    //            }
    //
    //        } else {
    //            Log.e("EditMoudleActivity", "Filter is null, cannot apply filter")
    //        }
    //
    //
    //
    //
    //    }
    //
    //    private fun applyRealTimeFilter(filter: GPUImageFilter?, switchCompatApplyAll: SwitchCompat) {
    //        // Ensure adapter and gpuImage are not null
    //        if (adapter == null || gpuImage == null) {
    //            Log.e("EditMoudleActivity", "Adapter or GPUImage is null, cannot apply filter")
    //            return
    //        }
    //
    //        // Get the current item from the ViewPager
    //        val currentItem: Int = binding.viewPager.currentItem
    //        val originalBitmap = adapter!!.getOriginalBitmap(currentItem)
    //
    //        // Ensure the original bitmap and the filter are not null
    //        if (originalBitmap != null && filter != null) {
    //            // Reset the GPUImage instance with the original bitmap
    //            gpuImage.setImage(originalBitmap)
    //            gpuImage.setFilter(filter)
    //
    //            // Get the filtered bitmap
    //            val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //            if (filteredBitmap != null) {
    //                adapter!!.setBitmap(currentItem, filteredBitmap)
    //
    //                // Apply the filter to all images if the switch is checked
    //                if (switchCompatApplyAll.isChecked) {
    //                    mainScope.launch {
    //                        applyAllFiltersToAllImages()
    //                    }
    //                }
    //            } else {
    //                Log.e("EditMoudleActivity", "Filtered bitmap is null")
    //            }
    //        } else {
    //            // Log the cause of the issue if the bitmap or filter is null
    //            if (originalBitmap == null) Log.e("EditMoudleActivity", "Original bitmap is null")
    //            if (filter == null) Log.e("EditMoudleActivity", "Filter is null")
    //        }
    //    }
    //
    //
    //    private suspend fun applyAllFiltersToAllImages() = withContext(Dispatchers.Default) {
    //        val filters = GPUImageFilterGroup().apply {
    //            contrastFilter?.let { addFilter(it) }
    //            brightnessFilter?.let { addFilter(it) }
    //            detailsFilter?.let { addFilter(it) }
    //        }
    //
    //        for (i in 0 until adapter!!.itemCount) {
    //            val originalBitmap = adapter!!.getOriginalBitmap(i)
    //            if (originalBitmap != null) {
    //                gpuImage.setImage(originalBitmap) // Reset the GPUImage instance
    //                gpuImage.setFilter(filters)
    //                val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //                withContext(Dispatchers.Main) {
    //                    adapter!!.setBitmap(i, filteredBitmap)
    //                }
    //            }
    //        }
    //    }
    //
    //    private suspend fun resetAllImages() = withContext(Dispatchers.Default) {
    //        for (i in 0 until adapter!!.itemCount) {
    //            val originalBitmap = adapter!!.getOriginalBitmap(i)
    //            if (originalBitmap != null) {
    //                withContext(Dispatchers.Main) {
    //                    adapter!!.setBitmap(i, originalBitmap)
    //                }
    //            }
    //        }
    //    }
    //
    //    private fun resetToOriginalImage() {
    //        // Reset the filters
    //        lastFilterApplied = null
    //        currentFilter = null
    //        contrastFilter = null
    //        brightnessFilter = null
    //        detailsFilter = null
    //
    //        // Get the current item from the ViewPager
    //        val currentItem: Int = binding.viewPager.currentItem
    //
    //        // Check if the adapter is not null and safely retrieve the bitmap
    //        if (adapter != null) {
    //            val originalBitmap = adapter?.getOriginalBitmap(currentItem)
    //
    //            // Ensure the bitmap is not null before applying it
    //            if (originalBitmap != null) {
    //                adapter?.setBitmap(currentItem, originalBitmap)
    //            } else {
    //                Log.e("EditMoudleActivity", "Original bitmap is null for item: $currentItem")
    //            }
    //        } else {
    //            Log.e("EditMoudleActivity", "Adapter is null, cannot reset to original image")
    //        }
    //    }
    //
    //
    //
    //    private fun cropCurrentImage() {
    //        val currentItem: Int = binding.viewPager.currentItem
    //
    //        if (adapter == null) {
    //            Log.e("cropbtn", "Adapter is null, cannot perform cropping")
    //            return
    //        }
    //
    //        val originalBitmap = adapter!!.getBitmap(currentItem)
    //
    //        if (originalBitmap != null) {
    //            Log.d("cropbtn", "Original bitmap found at position: $currentItem")
    //            val uri = adapter!!.getUriFromBitmap(originalBitmap)
    //
    //            if (uri != null) {
    //                Log.d("cropbtn", "URI found, starting UCrop")
    //                val destinationUri = Uri.fromFile(File(cacheDir, "filtered_image_${System.currentTimeMillis()}.png"))
    //
    //                try {
    //                    UCrop.of(uri, destinationUri).start(this, 69)
    //                } catch (e: Exception) {
    //                    Log.e("cropbtn", "UCrop Exception: ${e.message}")
    //                }
    //
    //            } else {
    //                Log.e("cropbtn", "URI is null, cannot proceed with cropping")
    //            }
    //        } else {
    //            Log.e("cropbtn", "Original bitmap is null at position: $currentItem")
    //        }
    //    }
    //    private fun rotateImage(degrees: Float) {
    //        val currentItem: Int = binding.viewPager.currentItem
    //
    //        // Check if the adapter is null
    //        if (adapter == null) {
    //            Log.e("rotateImage", "Adapter is null, cannot rotate image")
    //            return
    //        }
    //
    //        // Get the bitmap for the current item and check if it's null
    //        val originalBitmap = adapter!!.getBitmap(currentItem)
    //        if (originalBitmap == null) {
    //            Log.e("rotateImage", "Original bitmap is null at position: $currentItem, cannot rotate image")
    //            return
    //        }
    //
    //        try {
    //            // Create a matrix to rotate the bitmap
    //            val matrix = Matrix().apply { postRotate(degrees) }
    //
    //            // Create a new rotated bitmap
    //            val rotatedBitmap = Bitmap.createBitmap(
    //                originalBitmap, 0, 0,
    //                originalBitmap.width, originalBitmap.height,
    //                matrix, true
    //            )
    //
    //            // Set the rotated bitmap as the original in the adapter
    //            adapter!!.setOriginalBitmap(currentItem, rotatedBitmap)
    //
    //            // Apply the current filter, if available, or set the rotated bitmap
    //            currentFilter?.let {
    //                gpuImage.setImage(rotatedBitmap)
    //                gpuImage.setFilter(it)
    //                val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //                adapter!!.setBitmap(currentItem, filteredBitmap)
    //            } ?: adapter!!.setBitmap(currentItem, rotatedBitmap)
    //
    //            Log.d("rotateImage", "Image rotated successfully")
    //
    //        } catch (e: Exception) {
    //            Log.e("rotateImage", "Error rotating image: ${e.message}")
    //        }
    //    }
    //
    //
    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //        super.onActivityResult(requestCode, resultCode, data)
    //
    //        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
    //            val resultUri = UCrop.getOutput(data!!)
    //            Log.d("checkbitmap", "Cropped image result URI: $resultUri")
    //
    //            val currentItem: Int = binding.viewPager.currentItem
    //            val croppedBitmap = adapter?.getBitmapFromUri(resultUri)
    //            if (croppedBitmap != null) {
    //                adapter?.setOriginalBitmap(currentItem, croppedBitmap)
    //                currentFilter?.let {
    //                    gpuImage.setImage(croppedBitmap)
    //                    gpuImage.setFilter(it)
    //                    val filteredBitmap = gpuImage.bitmapWithFilterApplied
    //                    adapter?.setBitmap(currentItem, filteredBitmap)
    //                }
    //            }
    //        } else if (resultCode == UCrop.RESULT_ERROR) {
    //            val cropError = UCrop.getError(data!!)
    //            Log.e("CropError", "UCrop Error: ${cropError?.message}")
    //        }
    //    }
    //
    //    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(100)) { uris ->
    //        if (uris.isNotEmpty()) {
    //            Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
    //            setupViewPager(uris)
    //        } else {
    //            Log.d("PhotoPicker", "No media selected")
    //
    ////            reOpenImagePicker()
    //        }
    //
    //
    //    }
    //
    //    private fun reOpenImagePicker() {
    //        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    //
    //    }
    //
    //    override fun onBackPressed() {
    //        discardDailog()
    //    }
    //
    //
    //    private fun setupViewPager(uriList: List<Uri>) {
    //        adapter = ImagePagerAdapter(this, uriList, binding.deleteBtn)
    //        binding.viewPager.adapter = adapter
    //        Log.d("filterapply", "itemCount " + binding.viewPager.adapter?.itemCount.toString())
    //
    //        val currentItem: Int = binding.viewPager.currentItem
    //        binding.pageNumber.text =
    //            (currentItem + 1).toString() + "/" + binding.viewPager.adapter?.itemCount.toString()
    //
    //        binding.backViewPagerPage.setOnClickListener {
    //            val currentItem: Int = binding.viewPager.currentItem
    //            if (currentItem > 0) {
    //                binding.viewPager.currentItem = currentItem - 1
    //                binding.pageNumber.text =
    //                    (binding.viewPager.currentItem + 1).toString() + "/" + binding.viewPager.adapter?.itemCount.toString()
    //            }
    //        }
    //
    //        binding.againViewPagerPage.setOnClickListener {
    //            val currentItem: Int = binding.viewPager.currentItem
    //            if (currentItem < adapter!!.itemCount - 1) {
    //                binding.viewPager.currentItem = currentItem + 1
    //
    //                binding.pageNumber.text = (binding.viewPager.currentItem + 1).toString() + "/" + binding.viewPager.adapter?.itemCount.toString()
    //            }
    //        }
    //
    //        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
    //            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    //                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
    //            }
    //
    //            override fun onPageSelected(position: Int) {
    //                super.onPageSelected(position)
    //                Log.e("Selected_Page", " onPageSelected  "+position.toString())
    //                binding.pageNumber.text = (position + 1).toString() + "/" + binding.viewPager.adapter?.itemCount.toString()
    //
    //            }
    //
    //            override fun onPageScrollStateChanged(state: Int) {
    //                super.onPageScrollStateChanged(state)
    //                Log.e("Selected_Page", " onPageScrollStateChanged  "+state.toString())
    //            }
    //        })
    //
    //
    //    }
    //
    //    private fun debounce(action: () -> Unit) {
    //        debounceRunnable?.let { handler.removeCallbacks(it) }
    //        debounceRunnable = Runnable { action() }
    //        handler.postDelayed(debounceRunnable!!, debounceDelay)
    //    }
    //
    //    override fun onDestroy() {
    //        super.onDestroy()
    //        mainScope.cancel()
    //    }
    //}
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    ////class EditMoudleActivity : AppCompatActivity() {
    ////
    ////    lateinit var binding: ActivityEditMoudleBinding
    ////    private var adapter: ImagePagerAdapter? = null
    ////    private lateinit var gpuImage: GPUImage
    ////    private var currentFilter: GPUImageFilter? = null
    ////
    ////    override fun onCreate(savedInstanceState: Bundle?) {
    ////        super.onCreate(savedInstanceState)
    ////        binding = ActivityEditMoudleBinding.inflate(layoutInflater)
    ////        setContentView(binding.root)
    ////
    ////        gpuImage = GPUImage(this)
    ////
    ////        binding.buttonGallery.setOnClickListener {
    ////            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    ////        }
    ////
    ////        binding.buttonOriginal.setOnClickListener { applyFilter(null) }
    ////        binding.buttonDocs.setOnClickListener { applyFilter(GPUImageGrayscaleFilter()) }
    ////        binding.buttonImage.setOnClickListener { applyFilter(GPUImageSepiaToneFilter()) }
    ////        binding.buttonEnhance.setOnClickListener { applyFilter(CustomEnhanceFilter()) }
    ////        binding.buttonContrast.setOnClickListener { toggleSeekBarVisibility(binding.seekBarContrast) }
    ////        binding.buttonBrightness.setOnClickListener { toggleSeekBarVisibility(binding.seekBarBrightness) }
    ////
    ////        binding.seekBarContrast.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    ////            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    ////                val contrast = progress / 100.0f * 2 // Convert progress to contrast level (0 to 2)
    ////                applyRealTimeFilter(GPUImageContrastFilter(contrast))
    ////            }
    ////
    ////            override fun onStartTrackingTouch(seekBar: SeekBar) {}
    ////            override fun onStopTrackingTouch(seekBar: SeekBar) {}
    ////        })
    ////
    ////        binding.seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    ////            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    ////                val brightness = progress / 100.0f * 2 - 1 // Convert progress to brightness level (-1 to 1)
    ////                applyRealTimeFilter(GPUImageBrightnessFilter(brightness))
    ////            }
    ////
    ////            override fun onStartTrackingTouch(seekBar: SeekBar) {}
    ////            override fun onStopTrackingTouch(seekBar: SeekBar) {}
    ////        })
    ////
    ////        binding.buttonPrevious.setOnClickListener {
    ////            val currentItem: Int = binding.viewPager.currentItem
    ////            if (currentItem > 0) {
    ////                binding.viewPager.currentItem = currentItem - 1
    ////            }
    ////        }
    ////
    ////        binding.buttonNext.setOnClickListener {
    ////            val currentItem: Int = binding.viewPager.currentItem
    ////            if (currentItem < adapter!!.itemCount - 1) {
    ////                binding.viewPager.currentItem = currentItem + 1
    ////            }
    ////        }
    ////
    ////        binding.SwitchCompatApplyAll.setOnCheckedChangeListener { _, isChecked ->
    ////            if (isChecked) {
    ////                applyFilterToAllImages(currentFilter)
    ////            }else {
    ////                resetAllImages()
    ////            }
    ////        }
    ////    }
    ////
    ////    private fun resetAllImages() {
    ////        for (i in 0 until adapter!!.itemCount) {
    ////            val originalBitmap = adapter!!.getBitmap(i)
    ////            if (originalBitmap != null) {
    ////                adapter!!.setBitmap(i, originalBitmap)
    ////            }
    ////        }
    ////    }
    ////
    ////    private fun toggleSeekBarVisibility(seekBar: SeekBar) {
    ////        seekBar.visibility = if (seekBar.visibility == View.GONE) View.VISIBLE else View.GONE
    ////    }
    ////
    ////    private fun applyFilter(filter: GPUImageFilter?) {
    ////        currentFilter = filter
    ////        val currentItem: Int = binding.viewPager.currentItem
    ////        val originalBitmap = adapter!!.getBitmap(currentItem)
    ////        if (originalBitmap != null) {
    ////            val filteredBitmap: Bitmap
    ////            filteredBitmap = if (filter == null) {
    ////                // Apply the original bitmap
    ////                originalBitmap
    ////            } else {
    ////                gpuImage.setImage(originalBitmap)
    ////                gpuImage.setFilter(filter)
    ////                gpuImage.bitmapWithFilterApplied
    ////            }
    ////            Log.d("filterapply", "Filter applied at position: $currentItem")
    ////            adapter!!.setBitmap(currentItem, filteredBitmap)
    ////        } else {
    ////            Log.d("filterapply", "Original bitmap is null at position: $currentItem")
    ////        }
    ////    }
    ////
    ////    private fun applyRealTimeFilter(filter: GPUImageFilter?) {
    ////        val currentItem: Int = binding.viewPager.currentItem
    ////        val originalBitmap = adapter!!.getBitmap(currentItem)
    ////
    ////        if (originalBitmap != null && filter != null) {
    ////            gpuImage.setImage(originalBitmap)
    ////            gpuImage.setFilter(filter)
    ////            val filteredBitmap = gpuImage.bitmapWithFilterApplied
    ////            if (filteredBitmap != null) {
    ////                adapter!!.setBitmap(currentItem, filteredBitmap)
    ////            }
    ////        }
    ////    }
    ////
    ////    private fun applyFilterToAllImages(filter: GPUImageFilter?) {
    ////        for (i in 0 until adapter!!.itemCount) {
    ////            val originalBitmap = adapter!!.getBitmap(i)
    ////            if (originalBitmap != null) {
    ////                val filteredBitmap: Bitmap
    ////                filteredBitmap = if (filter == null) {
    ////                    originalBitmap
    ////                } else {
    ////                    gpuImage.setImage(originalBitmap)
    ////                    gpuImage.setFilter(filter)
    ////                    gpuImage.bitmapWithFilterApplied
    ////                }
    ////                adapter!!.setBitmap(i, filteredBitmap)
    ////            }
    ////        }
    ////    }
    ////
    ////    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(8)) { uris ->
    ////        if (uris.isNotEmpty()) {
    ////            Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
    ////            setupViewPager(uris)
    ////        } else {
    ////            Log.d("PhotoPicker", "No media selected")
    ////        }
    ////    }
    ////
    ////    private fun setupViewPager(uriList: List<Uri>) {
    ////        adapter = ImagePagerAdapter(this, uriList)
    ////        binding.viewPager.adapter = adapter
    ////    }
    ////}