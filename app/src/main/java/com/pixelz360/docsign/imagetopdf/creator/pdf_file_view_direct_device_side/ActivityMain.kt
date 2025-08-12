package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityMain2Binding
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class ActivityMain : AppCompatActivity() {
//    private var recyclerView: UtilEmptyRViewSign? = null
    var items: MutableList<File>? = null
    private var mAdapter: AdapterRViewSign? = null
    private var mBottomSheetDialog: BottomSheetDialog? = null
    var pdfData: Uri? = null
    private var selectedFile: File? = null
    var value = true
    private val mHandler = Handler()
    var value_sign = true
    private lateinit var adView: AdView
    var loading_adlayout: RelativeLayout? = null
    var currentUnifiedNativeAdTop: NativeAd? = null
    private var mInterstitialAd: InterstitialAd? = null

    lateinit var binding:ActivityMain2Binding
//    lateinit var shardperf: PrefPdfFromText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
//        shardperf = PrefPdfFromText(this)


//        if (checkForInternet() && !shardperf.getpayment() && MainApplication.myConfig != null && MainApplication.myConfig?.PdfSignatureBanner?.show == true) {
//            MainApplication.myConfig?.HomeScreenBanner?.id?.let { loadBannerAd(it) }
//        } else {
//            binding.appBarMain.bannerGC.visibility = View.GONE
//            binding.appBarMain.adsBanner.visibility = View.GONE
//        }
//
//        binding.appBarMain.backBtn1.setOnClickListener {
//
//            onBackPressed()
//
//        }




//        loading_adlayout = findViewById(R.id.loading_adlayout)

//        val fab = findViewById<Button>(R.id.fab)
//        fab.setOnClickListener {
//            if (value) {
//                value = false
//                val intent = Intent(applicationContext, ActivityDigiSign::class.java)
//                intent.putExtra("ActivityAction", "FileSearch")
//                startActivityForResult(intent, Merge_Request_CODE)
//            }
//            Handler(Looper.getMainLooper()).postDelayed({
//                value=true
//            },700)
//        }
        CheckStoragePermission()
//        recyclerView = findViewById(R.id.mainRecycleView11)
            binding.mainRecycleView11!!.layoutManager = LinearLayoutManager(this)
    binding.mainRecycleView11!!.setHasFixedSize(true)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            CheckStoragePermission()
        }
        HandleExternalData()

        Log.d("PdfViewActivity11", "ActivityMain. oncreate")

    }



    public override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)
        if (requestCode == Merge_Request_CODE && resultCode == RESULT_OK) {
            if (result != null) {
                CreateDataSource()
                mAdapter!!.notifyItemInserted(items!!.size - 1)
                Log.d("PdfViewActivity11", "CreateDataSource() ")

            }
        }
        if (resultCode == RESULT_OK && requestCode == RQS_OPEN_DOCUMENT_TREE) {
            if (result != null) {
                val uriTree = result.data
                val documentFile = DocumentFile.fromTreeUri(this, uriTree!!)
                if (selectedFile != null) {
                    val newFile = documentFile!!.createFile("application/pdf", selectedFile!!.name)

                    Log.d("PdfViewActivity11", "onActivityResult uriTree "+uriTree)


                    try {
                        copy(selectedFile!!, newFile)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    selectedFile = null
                    if (mBottomSheetDialog != null) mBottomSheetDialog!!.dismiss()
                    val toast = Toast.makeText(
                        this,
                        "Copy files to: " + documentFile.name,
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        CreateDataSource()
//        refreshAd1()

    }

    @Throws(IOException::class)
    fun copy(selectedFile: File, newFile: DocumentFile?) {
        try {
            val out = contentResolver.openOutputStream(newFile!!.uri)
            val `in` = FileInputStream(selectedFile.path)
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out!!.write(buffer, 0, read)
            }
            `in`.close()
            out!!.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun HandleExternalData() {
        val intent = intent
        val action = intent.action
        val type = intent.type
        var imageUri: Uri? = null
        if (Intent.ACTION_SEND == action || Intent.ACTION_VIEW == action && type != null) {
            if ("application/pdf" == type) {
                if (Intent.ACTION_SEND == action) imageUri =
                    intent.getParcelableExtra(Intent.EXTRA_STREAM) else if (Intent.ACTION_VIEW == action) imageUri =
                    intent.data
                if (imageUri != null) {
                    val list = ArrayList<Uri>()
                    list.add(imageUri)
                    StartSignatureActivity("PDFOpen", list)

                    Log.d("PdfViewActivity11", "HandleExternalData() "+imageUri)
                    Log.d("PdfViewActivity11", "HandleExternalData() "+list.size.toString())

                }
            }
        }
    }

    fun StartSignatureActivity(message: String?, imageUris: ArrayList<Uri>?) {
        val intent = Intent(applicationContext, ActivityDigiSign::class.java)
        intent.putExtra("ActivityAction", message)
        intent.putExtra("PDFOpen", imageUris)
        startActivityForResult(intent, Merge_Request_CODE)
        Log.d("PdfViewActivity11", "StartSignatureActivity ")

    }

    private fun CheckStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle("Storage Permission")
                alertDialog.setMessage(
                    "Storage permission is required in order to " +
                            "provide Image to PDF feature, please enable permission in app settings"
                )
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "Settings"
                ) { dialog, id ->
                    val i = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.app.pdfconverter.compresspdf.mergepdffiles.fillandsign.example.myapplication")
                    )
                    startActivity(i)
                    dialog.dismiss()
                }
                alertDialog.show()
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    2
                )
            }
        }
    }

    private fun CreateDataSource() {
        if (binding.mainRecycleView11 != null) {
            items = ArrayList()
            val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/PDF Converter"
            val myDir = File("$fullPath/DigitalSignature")
            if (!myDir.exists()) {
                myDir.mkdirs()
            }
            val files = myDir.listFiles()

            try {
                if (files != null) {
                    Arrays.sort(files) { file1, file2 ->
                        val result = file2.lastModified() - file1.lastModified()
                        if (result < 0) {
                            -1
                        } else if (result > 0) {
                            1
                        } else {
                            0
                        }
                    }
                    for (i in files.indices) {
                        items!!.add(files[i])
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

            //set data and list adapter
            mAdapter = AdapterRViewSign(this, items)
            mAdapter!!.setOnItemClickListener(object : AdapterRViewSign.OnItemClickListener {
                override fun onItemClick(view: View, value: File, position: Int) {
                    if (value_sign) {
                        value_sign = false
//                        showBottomSheetDialog(value)
                    }
                }

                override fun onItemLongClick(view: View, obj: File, pos: Int) {
                    if (value_sign) {
                        value_sign = false
//                        showBottomSheetDialog(obj)
                    }
                }
            })
            binding.mainRecycleView11!!.adapter = mAdapter
        }
    }
//
//    private fun showBottomSheetDialog(currentFile: File) {
//        val view = layoutInflater.inflate(R.layout.sheet_list, null)
//        view.findViewById<View>(R.id.cancel_btn).setOnClickListener {
//            value_sign = true
//            mBottomSheetDialog!!.dismiss()
//        }
//        view.findViewById<View>(R.id.lyt_email).setOnClickListener {
//            value_sign = true
//            val contentUri = FileProvider.getUriForFile(
//                applicationContext, applicationContext.packageName, currentFile
//            )
//            val target = Intent(Intent.ACTION_SEND)
//            target.type = "text/plain"
//            target.putExtra(Intent.EXTRA_STREAM, contentUri)
//            target.putExtra(Intent.EXTRA_SUBJECT, "Subject")
//            startActivity(Intent.createChooser(target, "Send via Email..."))
//            mBottomSheetDialog!!.dismiss()
//        }
//        view.findViewById<View>(R.id.lyt_share).setOnClickListener {
//            value_sign = true
//            val contentUri = FileProvider.getUriForFile(
//                applicationContext, applicationContext.packageName, currentFile
//            )
//            val target =
//                ShareCompat.IntentBuilder.from(this@ActivityMain).setStream(contentUri).intent
//            target.data = contentUri
//            target.type = "application/pdf"
//            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            if (target.resolveActivity(packageManager) != null) {
//                startActivity(target)
//                mBottomSheetDialog!!.dismiss()
//            }
//        }
//        view.findViewById<View>(R.id.lyt_rename).setOnClickListener {
//            value_sign = true
//            mBottomSheetDialog!!.dismiss()
//            showCustomRenameDialog(currentFile)
//        }
//        view.findViewById<View>(R.id.lyt_delete).setOnClickListener {
//            value_sign = true
//            mBottomSheetDialog!!.dismiss()
//            showCustomDeleteDialog(currentFile)
//        }
//        view.findViewById<View>(R.id.lyt_copyTo).setOnClickListener {
//            value_sign = true
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            startActivityForResult(intent, RQS_OPEN_DOCUMENT_TREE)
//            selectedFile = currentFile
//        }
//        view.findViewById<View>(R.id.lyt_openFile).setOnClickListener {
//            value_sign = true
//            val target = Intent(Intent.ACTION_VIEW)
//            val contentUri = FileProvider.getUriForFile(applicationContext, applicationContext.packageName, currentFile)
//            target.setDataAndType(contentUri, "application/pdf")
//            target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            val intent = Intent.createChooser(target, "Open File")
//            try {
//                startActivity(intent)
//                mBottomSheetDialog!!.dismiss()
//            } catch (e: ActivityNotFoundException) {
//                //Snackbar.make(mCoordLayout, "Install PDF reader application.", Snackbar.LENGTH_LONG).show();
//            }
//        }
//        mBottomSheetDialog = BottomSheetDialog(this)
//        mBottomSheetDialog!!.setContentView(view)
//        mBottomSheetDialog!!.setCancelable(false)
//        mBottomSheetDialog!!.show()
//        mBottomSheetDialog!!.setOnDismissListener {
//            mBottomSheetDialog = null
//            value_sign = true
//        }
//    }
//
//    fun showCustomRenameDialog(currentFile: File) {
//        val builder = AlertDialog.Builder(this)
//        val inflater = this.layoutInflater
//        val view = inflater.inflate(R.layout.rename_layout, null)
//        builder.setCancelable(false)
//        builder.setView(view)
//        val editText = view.findViewById<View>(R.id.renameEditText2) as EditText
//
//        editText.setText(currentFile.name)
//        editText.isCursorVisible=true
//        editText.setSelection(editText.length())
//        builder.setTitle("Rename")
//        builder.setPositiveButton("Rename") { dialog, id ->
//            val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/PDF Converter"
//            val file = File("$fullPath/DigitalSignature", editText.text.toString())
//            currentFile.renameTo(file)
//            dialog.dismiss()
//            CreateDataSource()
//            mAdapter!!.notifyItemInserted(items!!.size - 1)
//        }
//        builder.setNegativeButton("Cancel") { dialog, id -> }
//        val dialog = builder.create()
//        dialog.show()
//    }

    fun showCustomDeleteDialog(currentFile: File) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure want to delete this file?")
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { dialog, id ->
            currentFile.delete()
            CreateDataSource()
            mAdapter!!.notifyItemInserted(items!!.size - 1)
        }
        builder.setNegativeButton("Cancel") { dialog, id -> }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val Merge_Request_CODE = 43
        private const val RQS_OPEN_DOCUMENT_TREE = 24
    }



//    private fun refreshAd1() {
//
//        val rlLoading = binding.appBarMain.loadingLayoutTop
//        val rlAds = binding.appBarMain.adsContainerLayoutTop
//        val rlAds11 = binding.appBarMain.cardShowHideTop
//        val myAdTemplate = binding.appBarMain.adTemplateTop
//
//        val builder = AdLoader.Builder(this, resources.getString(R.string.native_ad_unit_id_pdf_sign))
//
//        builder.forNativeAd { NativeAd ->
//
//            var activityDestroyed = false
//            activityDestroyed = isDestroyed
//            if (activityDestroyed || isFinishing || isChangingConfigurations) {
//                NativeAd.destroy()
//                return@forNativeAd
//            }
//
//            currentUnifiedNativeAdTop?.destroy()
//            currentUnifiedNativeAdTop = NativeAd
//            val adView: NativeAdView = layoutInflater.inflate(R.layout.adaptive_unified, null) as NativeAdView
//            populateUnifiedNativeAdView1(NativeAd, adView)
//            myAdTemplate.visibility= View.VISIBLE
//            rlLoading.visibility = View.GONE
//            rlAds.visibility = View.VISIBLE
//            rlAds11.visibility = View.VISIBLE
//
//            binding.appBarMain.adTemplateTop.removeAllViews()
//            binding.appBarMain.adTemplateTop.addView(adView)
//        }
//
//        val videoOptions = VideoOptions.Builder()
//            .setStartMuted(true)
//            .build()
//
//        val adOptions = com.google.android.gms.ads.formats.NativeAdOptions.Builder()
//            .setVideoOptions(videoOptions)
//            .build()
//
//        builder.withNativeAdOptions(adOptions)
//
//        val adLoader = builder.withAdListener(object : AdListener() {
//            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                super.onAdFailedToLoad(loadAdError)
//                Log.e("checkNative",loadAdError.toString())
//                rlLoading.visibility = View.GONE
//                rlAds.visibility = View.GONE
//                rlAds11.visibility = View.GONE
//                myAdTemplate.visibility= View.GONE
//            }
//        }).build()
//
//        adLoader.loadAd(AdRequest.Builder().build())
//
//    }



//    private fun populateUnifiedNativeAdView1(nativeAd: NativeAd, adView: NativeAdView) {
//        try{
//            adView.mediaView = adView.findViewById(R.id.ad_media)!!
//
//            adView.headlineView = adView.findViewById(R.id.ad_headline)
//            adView.bodyView = adView.findViewById(R.id.ad_body)
//            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
//            adView.iconView = adView.findViewById(R.id.ad_app_icon)
//
//            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
//
//            (adView.headlineView as TextView).text = nativeAd.headline
//            nativeAd.mediaContent?.let { adView.mediaView!!.setMediaContent(it) }
//            adView.mediaView!!.visibility=View.GONE
//            adView.bodyView!!.visibility = View.VISIBLE
//            (adView.bodyView as TextView).text = nativeAd.body
//
//
//            adView.callToActionView!!.visibility = View.VISIBLE
//            (adView.callToActionView as Button).text = nativeAd.callToAction
//
//            (adView.iconView as ImageView).setImageDrawable(
//                nativeAd.icon!!.drawable
//            )
//            adView.iconView!!.visibility = View.VISIBLE
//
//            (adView.advertiserView as TextView).text = nativeAd.advertiser
//            adView.advertiserView!!.visibility = View.VISIBLE
//
//            adView.setNativeAd(nativeAd)
//
//
//        }catch (e:Exception){
//            e.printStackTrace()
//        }
//
//    }


    private fun loadBannerAd(adid : String) {
        // Create a new AdView
        adView = AdView(this@ActivityMain)
        Log.e("banner", "Failed to ${adid}.")
        adView.adUnitId
        adView.adUnitId = adid // Your AdMob Banner ID
//        val adSize = adSize
//        if (adSize == null) {
//            return
//        }
//        adView.setAdSize(adSize)



        val adRequest = AdRequest.Builder().build()

        // Show loading indicator
//        binding.appBarMain.adsBanner.visibility = View.VISIBLE
//        binding.appBarMain.linearLayoutLoading.visibility = View.VISIBLE
//        binding. appBarMain.linearLayoutAdsContainer.visibility = View.GONE

        // Set up AdListener to handle ad events
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.e("banner", "onAdLoaded: Ad loaded successfully")
//                binding.appBarMain.adsBanner.visibility = View.VISIBLE
//                binding.appBarMain.linearLayoutLoading.visibility = View.GONE
//                binding.appBarMain.linearLayoutAdsContainer.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("banner", "onAdFailedToLoad: ${adError.message}")
//                binding.appBarMain.linearLayoutLoading.visibility = View.GONE
//                binding.appBarMain.linearLayoutAdsContainer.visibility = View.GONE
            }
        }
//        binding.appBarMain.bannerGC.removeAllViews()
//        binding.appBarMain.bannerGC.addView(adView)
        adView.loadAd(adRequest)
    }

//    private val adSize: AdSize?
//        get() {
//            val display = windowManager.defaultDisplay
//            val outMetrics = DisplayMetrics()
//            display.getMetrics(outMetrics)
//
//            val density = outMetrics.density
//            var adWidthPixels = binding.appBarMain.bannerGC?.width?.toFloat() ?: 0f
//            if (adWidthPixels == 0f) {
//                adWidthPixels = outMetrics.widthPixels.toFloat()
//            }
//            val adWidth = (adWidthPixels / density).toInt()
//            return if (adWidth > 300) {
//                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
//            } else {
//                AdSize.BANNER
//            }
//        }

}