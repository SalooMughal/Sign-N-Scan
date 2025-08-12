package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.pixelz360.docsign.imagetopdf.creator.pdf_tools_billing.PdfToolsAddSuccessfullyActivity


class BillingIASForPdfTools(private val context: Context) {
    private val prefUtilForPdfTools by lazy { PrefUtilForPdfTools(context) }
    var pdfTools = ""

    init {
        try {
            initBilling()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initBilling() {
        //Initialize a BillingClient with PurchasesUpdatedListener onCreate method
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener { billingResult, list ->


                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.e("checkbilling", "PdfToolsAddSuccessfullyActivity: BillingIASForPdfTools side")


                    if (PrefUtilForAppAdsFree.isCheckActivityPremium(context)){
                        val intent = Intent(context,PdfToolsAddSuccessfullyActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent)
                    }else{
                        val intent = Intent(context,AddSuccessfullyActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent)
                    }

                    Log.e("checkbilling", "initBilling: fff "+list!!.size)


                    verifySubPurchase(context) {}

//                    PrefUtilForPdfTools.setPremium(context, true)


                }
            }.build()

        establishConnection()
    }

    fun establishConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.e("checkbilling", "onBillingSetupFinished: connected")
                    verifySubPurchase(context) {}
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                try {
                    establishConnection()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun showProducts() {
        val productList = listOf(
            Product.newBuilder().setProductId("pdf_tools_weekly_plan").setProductType(ProductType.SUBS).build(),
            Product.newBuilder().setProductId("pdf_tools_monthly_plan").setProductType(ProductType.SUBS).build(),
            Product.newBuilder().setProductId("pdf_tools_annual_plan").setProductType(ProductType.SUBS).build()
        )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                Log.d("Billing", "Products retrieved successfully: ${productDetailsList.size}")
                productsDetailsList = productDetailsList.toMutableList()

                Log.e("checkbilling", "showProducts pdf tools: ${productDetailsList.size}")




                productDetailsList.forEachIndexed { index, productDetails ->
                    Log.d("Billing", "Product ID: ${productDetails.productId}, Price: ${
                        productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice
                    }")
                    prefUtilForPdfTools.setString("key_new$index", productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice.toString())


                    Log.d("checkbilling", "ids pdf tools  "+index.toString()+"   "+productDetailsList[index].subscriptionOfferDetails?.get(0)?.pricingPhases)

                }
            } else {
                Log.e("Billing", "Failed to retrieve products: ${billingResult.debugMessage}")
            }
        }
    }


    companion object {
        private var billingClient: BillingClient? = null
        private var productsDetailsList: MutableList<ProductDetails>? = null
        @JvmStatic
        fun launchPurchaseFlow(plan: Int, activity: Activity) {

            if (productsDetailsList.isNullOrEmpty()) {
                Log.e("Billing", "No products available for purchase")
                Toast.makeText(activity, "No products available", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                val productDetails = productsDetailsList!![plan]
                val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken
                if (offerToken.isNullOrEmpty()) {
                    Log.e("Billing", "Offer token is null or empty for plan index $plan")
                    return
                }

                val params = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(offerToken)
                                .build()
                        )
                    )
                    .build()

                billingClient?.launchBillingFlow(activity, params)
            } catch (e: Exception) {
                Log.e("Billing", "Error launching purchase flow: ${e.message}")
            }
        }

        @JvmStatic
        fun verifySubPurchase(context: Context, onResults: () -> Unit) {
            billingClient?.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()) { _, purchaseList ->
                // Process the result
                Log.e("checkbilling", "verifySubPurchase:")

                // Check if purchaseList is not empty
                if (purchaseList.isNotEmpty()) {

                    // Assuming purchaseList is a list of purchase objects with a list of SKUs inside each purchase
                    val processedSkus = mutableSetOf<String>() // Track processed SKUs

// Flag to track if anything was successfully acknowledged
                    var acknowledgedAtLeastOnce = false

                    for (purchase in purchaseList) {
                        // Get the list of SKUs for this purchase
                        val skus = purchase.skus

                        // For each SKU in the current purchase
                        for (sku in skus) {
                            Log.e("checkbilling", "verifySubPurchase: SKU: $sku")

                            // Check if the SKU is one of the predefined valid SKUs
                            if (sku in listOf("weekly_plan", "monthly_plan", "yearly_plan", "ads_free_life_time",
                                    "pdf_tools_weekly_plan", "pdf_tools_monthly_plan", "pdf_tools_annual_plan")) {

                                // Acknowledge the purchase only if it hasn't been processed yet
                                if (!processedSkus.contains(sku)) {
                                    processedSkus.add(sku) // Mark the SKU as processed

                                    // Acknowledge the purchase
                                    val purchaseToken = purchase.purchaseToken

                                    // Acknowledge the purchase
                                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchaseToken)
                                        .build()

                                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                            // Acknowledgment successful
                                            Log.e("checkbilling", "verifySubPurchase: Premium set to true for SKU: $sku")
                                            PrefUtilForPdfTools.setPremiumString(sku, context)

                                            // Save the SKU to SharedPreferences
//                                            saveSkuToPreferences(sku, context)

                                            skus.forEach { sku ->
                                                when (sku) {

                                                    "pdf_tools_weekly_plan" -> PrefUtilForAppAdsFree.setAdsForToolsWeeklyString("pdf_tools_weekly_plan", context)
                                                    "pdf_tools_monthly_plan" -> PrefUtilForAppAdsFree.setAdsFoToolsrMonthlyString("pdf_tools_monthly_plan", context)
                                                    "pdf_tools_annual_plan" -> PrefUtilForAppAdsFree.setAdsForToolsYearlyString("pdf_tools_annual_plan", context)
                                                    "weekly_plan" -> PrefUtilForAppAdsFree.setAdsForWeeklyString("weekly_plan", context)
                                                    "monthly_plan" -> PrefUtilForAppAdsFree.setAdsForMonthlyString("monthly_plan", context)
                                                    "yearly_plan" -> PrefUtilForAppAdsFree.setAdsForYearlyString("yearly_plan", context)
                                                    else -> Log.e("checkbilling", "Unknown SKU: $sku")

                                                }

                                                Log.e("checkbilling", "skus.forEach { sku ->: $sku")

                                            }


                                            Log.e("checkbilling", "PdfToolsAddSuccessfullyActivity: BillingIASForPdfTools side")

                                            // Check for active subscriptions and update UI
                                            updateSubscriptionState(context, processedSkus)

                                            // Run results callback
                                            onResults()

                                        } else {
                                            // Acknowledgment failed
                                            Log.e("checkbilling", "Acknowledge purchase failed with response code: ${billingResult.responseCode}")
                                            PrefUtilForPdfTools.setPremiumString("", context)

                                            // No active subscriptions, reset UI
                                            updateSubscriptionState(context, emptySet())
                                            onResults()
                                        }
                                    }

                                    acknowledgedAtLeastOnce = true
                                }
                            }

                        }
                    }


                    // After the loop finishes, you can check the `processedSkus` set for all processed SKUs
                    if (acknowledgedAtLeastOnce) {
                        Log.e("checkbilling", "All processed SKUs: $processedSkus")

                        // Check if any SKU in processedSkus matches PDF tools SKUs
                        if (processedSkus.contains("pdf_tools_weekly_plan") ||
                            processedSkus.contains("pdf_tools_monthly_plan") ||
                            processedSkus.contains("pdf_tools_annual_plan")) {

                            // User has a PDF tools subscription
                            Log.d("checkbilling", "User has premium PDF tools subscription")
                            PrefUtilForPdfTools.setPremium(context, true)
                        } else {
                            // Not a valid PDF tools subscription
                            PrefUtilForPdfTools.setPremium(context, false)
                            Log.d("checkbilling", "Not a paid PDF tools subscription")
                        }

                        // Check if any SKU in processedSkus matches app ads-free SKUs
                        if (processedSkus.contains("weekly_plan") ||
                            processedSkus.contains("monthly_plan") ||
                            processedSkus.contains("yearly_plan") ||
                            processedSkus.contains("ads_free_life_time")) {

                            // User has an app ads-free plan
                            Log.d("checkbilling", "User has ads-free plan")
                            Log.d("checkbilling", "User has ads-free plan processedSkus "+processedSkus)
                            PrefUtilForAppAdsFree.setPremium(context, true)





                        } else {
                            // Not a valid ads-free plan
                            PrefUtilForAppAdsFree.setPremium(context, false)
                            Log.d("checkbilling", "Not a paid app ads-free subscription")
                            PrefUtilForAppAdsFree.setAdsForLiftTimeString("no life time", context)
                            Log.d("checkbilling", "no life time")
                        }
                    }


                } else {
                    // No active subscription found
                    PrefUtilForPdfTools.setPremiumString("", context)
                    PrefUtilForPdfTools.setPremium(context, false)
                    PrefUtilForAppAdsFree.setPremium(context, false)

                    Log.e("checkbilling", "Not Anny Purchase")


                    // Reset values when no active subscription found
                    PrefUtilForAppAdsFree.setAdsForToolsWeeklyString("pdf_tools_weekly_plan_no_purchase", context)
                    PrefUtilForAppAdsFree.setAdsFoToolsrMonthlyString("pdf_tools_monthly_plan_no_purchase", context)
                    PrefUtilForAppAdsFree.setAdsForToolsYearlyString("pdf_tools_annual_plan_no_purchase", context)
                    PrefUtilForAppAdsFree.setAdsForWeeklyString("weekly_plan_no_purchase", context)
                    PrefUtilForAppAdsFree.setAdsForMonthlyString("monthly_plan_no_purchase", context)
                    PrefUtilForAppAdsFree.setAdsForYearlyString("yearly_plan_no_purchase", context)

                }

        }




    }

        /**
         * Update the subscription state in SharedPreferences based on active subscriptions.
         */
        private fun updateSubscriptionState(context: Context, activeSubscriptions: Set<String>) {
            val wasSubscribed = PrefUtilForAppAdsFree.isPremium(context)  // Check previous state
            val isSubscribed = activeSubscriptions.isNotEmpty()

            if (!isSubscribed && wasSubscribed) {
                // User canceled subscription, reset stored values
                Log.e("checkbilling", "Subscription canceled, updating UI")

                PrefUtilForAppAdsFree.setPremium(context, false)
                PrefUtilForPdfTools.setPremium(context, false)

                PrefUtilForAppAdsFree.setAdsForToolsWeeklyString("no_purchase", context)
                PrefUtilForAppAdsFree.setAdsFoToolsrMonthlyString("no_purchase", context)
                PrefUtilForAppAdsFree.setAdsForToolsYearlyString("no_purchase", context)
                PrefUtilForAppAdsFree.setAdsForWeeklyString("no_purchase", context)
                PrefUtilForAppAdsFree.setAdsForMonthlyString("no_purchase", context)
                PrefUtilForAppAdsFree.setAdsForYearlyString("no_purchase", context)
                PrefUtilForAppAdsFree.setAdsForLiftTimeString("no life time", context)
            }

            // Update new state
            if (isSubscribed) {
                PrefUtilForAppAdsFree.setPremium(context, true)
            }
        }



//        private fun saveSkuToPreferences(sku: String, context: Context) {
//
//            if (sku != null) {
//                if ("pdf_tools_weekly_plan" == sku || "pdf_tools_monthly_plan" == sku || "pdf_tools_annual_plan" == sku) {
//                    // User has the monthly plan
//
//                    Log.d("checkbilling", " skusaveSkuToPreferences  " +sku)
//
//                    PrefUtilForPdfTools.setPremium(context, true)
//
//
//
//                }else{
//                    PrefUtilForPdfTools.setPremium(context, false)
//
//                    Log.d("checkbilling", " not Tools pdf paid  ")
//
//
//
//                }
//
//
//
//
//                 if ("weekly_plan" == sku || "monthly_plan" == sku || "yearly_plan" == sku || "ads_free_app" == sku) {
//                    // User has the annual plan
//
//                    Log.d("checkbilling", " skusaveSkuToPreferences  " + sku)
//
//                     PrefUtilForAppAdsFree.setPremium(context, true)
//
//                }else{
//                     Log.d("checkbilling", "not add free paid")
//
//                     PrefUtilForAppAdsFree.setPremium(context, false)
//
//                 }
//
//
//            }
//
//
//
//
////            val sharedPreferences = context.getSharedPreferences("SKU_Prefs", Context.MODE_PRIVATE)
////            val editor = sharedPreferences.edit()
////            editor.clear()
////            editor.commit()
////
////            // Retrieve the existing list of SKUs (if any)
////            val skuSet = sharedPreferences.getStringSet("skus", HashSet())
////
////
////            // Add the new SKU to the set
////            skuSet!!.add(sku)
////
////
////            // Save the updated set of SKUs back to SharedPreferences
////            editor.putStringSet("skus", skuSet)
////            editor.apply()
//
//
//
//        }
    }
}


