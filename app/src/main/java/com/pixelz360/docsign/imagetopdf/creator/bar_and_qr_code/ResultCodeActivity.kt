package com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.util.Linkify
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityResultCodeBinding


class ResultCodeActivity : AppCompatActivity() {

    lateinit var binding: ActivityResultCodeBinding


    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        if (it.resultCode == Activity.RESULT_OK) {
//            val barcode = it?.data?.getStringExtra("BarcodeResult")
//            binding.txtResult.text = barcode
//        }
    }



    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeoutRunnable: Runnable



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get color from resources
        val statusBarColor = ContextCompat.getColor(this@ResultCodeActivity, R.color.white)
        // Change status bar color
        com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils.changeStatusBarColor(statusBarColor, this@ResultCodeActivity)





//        loadInterstitialAd()



        val resultCode = intent.getStringExtra("BarcodeResult")

        firebaseAnalytics(this@ResultCodeActivity,"ResultCodeActivity")

        Log.d("dd122wee", "Result: ${resultCode?.isUrlValid()}")


        if (resultCode?.isUrlValid() == true){
            binding.txtResult.text = resultCode
            Linkify.addLinks(binding.txtResult, Linkify.WEB_URLS)
        }else{
            binding.txtResult.text = resultCode

        }


        binding.homeBtn.setOnClickListener {
            val intent = Intent(this@ResultCodeActivity,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }



        binding.btnCopy.setOnClickListener {

            goToNextActivity(true)



        }


        binding.btnSingle.setOnClickListener {



            goToNextActivity(false)


        }


        binding.backButton.setOnClickListener {
//            val intent = Intent(this@ResultCodeActivity, MainActivity::class.java)
            val intent = Intent(this@ResultCodeActivity, HomeActivity::class.java)
            intent.putExtra("ScannerSide","ScannerSide")
            startActivity(intent)
            finish()
        }

    }

//    private fun loadInterstitialAd() {
//        val adRequest = AdRequest.Builder().build()
//        isAdLoading = true
//        binding.progressBar.visibility = View.GONE
//
//        timeoutRunnable = Runnable {
//            if (isAdLoading) {
//                isAdLoading = false
//                binding.progressBar.visibility = View.GONE
//                goToNextActivity()
//            }
//        }
//        handler.postDelayed(timeoutRunnable, 10000)
//
//        InterstitialAd.load(this, getString(R.string.language_intertial_ad), adRequest, object : InterstitialAdLoadCallback() {
//            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                mInterstitialAd = interstitialAd
//                isAdLoading = false
//                binding.progressBar.visibility = View.GONE
//                handler.removeCallbacks(timeoutRunnable)
//                setAdCallbacks()
//            }
//
//            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                mInterstitialAd = null
//                isAdLoading = false
//                binding.progressBar.visibility = View.GONE
//                handler.removeCallbacks(timeoutRunnable)
//                goToNextActivity()
//            }
//        })
//    }
//
//    private fun setAdCallbacks() {
//        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
//            override fun onAdDismissedFullScreenContent() {
//                mInterstitialAd = null
//                loadInterstitialAd()
//                goToNextActivity()
//            }
//
//            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                mInterstitialAd = null
//                loadInterstitialAd()
//                goToNextActivity()
//            }
//        }
//    }
//
//    private fun showAdOrLoad() {
//        if (mInterstitialAd != null) {
//            mInterstitialAd?.show(this)
//        } else if (isAdLoading) {
//            binding.progressBar.visibility = View.VISIBLE
//        } else {
//            goToNextActivity()
//        }
//    }

    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }


    fun goToNextActivity(b: Boolean) {

        if (!b){


            val i = Intent(this, CamActivity::class.java)
            i.putExtra("title", "Example")
            i.putExtra("msg", "Scan Barcode")
            getContent.launch(i)


        }else{
            val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label.toString(), binding.txtResult.text)
            clipboard.setPrimaryClip(clip)
            binding.btnCopy.text = "Copied"
            binding.btnCopy.setTextColor(resources.getColor(R.color.red))
        }





    }

    fun String.isUrlValid(): Boolean {
        return this.contains("https") && this.contains(".")
    }

}