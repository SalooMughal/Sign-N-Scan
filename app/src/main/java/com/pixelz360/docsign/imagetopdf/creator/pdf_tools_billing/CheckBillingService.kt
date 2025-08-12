package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing

import android.content.Context
import com.android.billingclient.api.BillingClient

import android.app.Activity
import com.android.billingclient.api.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class CheckBillingService(private val activity: Activity, private val adUnitId: String) {

//    private var billingClient: BillingClient? = null
//    private var mInterstitialAd: InterstitialAd? = null
//
//    private val monthlyPlanSku = "pdf_tools_monthly_plan"
//    private val weeklyPlanSku = "pdf_tools_weekly_plan"
//    private val annualPlanSku = "pdf_tools_annual_plan"
//
//    init {
//        // Initialize AdMob and BillingClient
//        initializeAdMob()
//        initializeBillingClient()
//    }
//
//    // Initialize AdMob
//    private fun initializeAdMob() {
//        MobileAds.initialize(activity) {}
//        loadAd()
//    }
//
//    // Initialize BillingClient
//    private fun initializeBillingClient() {
//        billingClient = BillingClient.newBuilder(activity)
//            .setListener(purchasesUpdatedListener)
//            .enablePendingPurchases()
//            .build()
//
//        billingClient?.startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    // Billing Client setup successful, check for existing purchases
//                    checkSubscriptionStatus()
//                }
//            }
//
//            override fun onBillingServiceDisconnected() {
//                // Try to reconnect
//            }
//        })
//    }
//
//    // Check if the user has an active subscription
//    private fun checkSubscriptionStatus() {
//        val purchasesResult = billingClient?.queryPurchases(BillingClient.SkuType.SUBS)
//        val purchases = purchasesResult?.purchasesList
//
//        if (purchases != null) {
//            for (purchase in purchases) {
//                if (purchase.sku == monthlyPlanSku || purchase.sku == weeklyPlanSku || purchase.sku == annualPlanSku) {
//                    // Subscription is active, hide ads
//                    mInterstitialAd = null // Disable or hide the ad here
//                    return
//                }
//            }
//        }
//        // Show ad if no active subscription
//        showAd()
//    }
//
//    // BillingClient listener to handle purchase updates
//    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
//        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
//            for (purchase in purchases) {
//                if (purchase.sku == monthlyPlanSku || purchase.sku == weeklyPlanSku || purchase.sku == annualPlanSku) {
//                    // Subscription is successful
//                    mInterstitialAd = null // Disable or hide the ad
//                    break
//                }
//            }
//        }
//    }
//
//    // Show ad if no active subscription
//    private fun showAd() {
//        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded) {
//            mInterstitialAd!!.show(activity)
//        }
//    }
//
//    // Load the ad
//    private fun loadAd() {
//        InterstitialAd.load(activity, adUnitId, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
//            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                mInterstitialAd = interstitialAd
//            }
//
//            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                mInterstitialAd = null
//            }
//        })
//    }
}






