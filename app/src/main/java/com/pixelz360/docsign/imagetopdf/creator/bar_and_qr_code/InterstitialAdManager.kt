package com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(
    private val activity: Activity,
    private val adUnitId: String
) {
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private val handler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        isAdLoading = true

        InterstitialAd.load(activity, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                isAdLoading = false
                handler.removeCallbacks(timeoutRunnable!!)
                Log.d("InterstitialAdManager", "Ad loaded successfully")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
                isAdLoading = false
                handler.removeCallbacks(timeoutRunnable!!)
                Log.d("InterstitialAdManager", "Ad failed to load: ${adError.message}")
            }
        })
    }

    fun showAdOrProceed(
        progressBar: ProgressBar?,
        onProceed: () -> Unit
    ) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadAd() // Reload ad after dismissal
                    onProceed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    loadAd() // Reload ad on failure
                    onProceed()
                }
            }
            interstitialAd?.show(activity)
        } else if (isAdLoading) {
            progressBar?.visibility = View.VISIBLE
            timeoutRunnable = Runnable {
                if (isAdLoading) {
                    isAdLoading = false
                    progressBar?.visibility = View.GONE
                    Log.d("InterstitialAdManager", "Ad loading timeout, proceeding")
                    onProceed()
                }
            }
            handler.postDelayed(timeoutRunnable!!, 5000) // Wait for 10 seconds
        } else {
            onProceed() // Proceed if no ad is available
        }
    }
}
