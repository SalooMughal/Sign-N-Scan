package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.QueryProductDetailsParams.Product

class BillingIASForAppAdsFreeInApp(val context: Context) {
    private val prefUtilForAppAdsFree by lazy { PrefUtilForAppAdsFree(context) }

    init {
        try {
            initBilling()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initBilling() {
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    Log.e("checkbilling", "User canceled the purchase")
                } else {
                    Log.e("checkbilling", "Purchase failed with response code: ${billingResult.responseCode}")
                }
            }
            .build()

        establishConnection()
    }

    fun establishConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    verifyInAppPurchase(context) {}
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                establishConnection()
            }
        })
    }

    private fun showProducts() {
        val productList = listOf(
            Product.newBuilder().setProductId("ads_free_life_time").setProductType(ProductType.INAPP).build()
        )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)

        billingClient?.queryProductDetailsAsync(params.build()) { _, productDetailsList ->
            productsDetailsList = productDetailsList

            // Save price in SharedPreferences
            productDetailsList.forEach { productDetails ->
                val price = productDetails.oneTimePurchaseOfferDetails?.formattedPrice ?: "N/A"
                prefUtilForAppAdsFree.setString("ads_free_life_time_price", price) // Save price
                Log.d("checkbilling", "Saved Price for INAPP: $price")
            }
        }
    }



    companion object {
        private var billingClient: BillingClient? = null
        private var productsDetailsList: MutableList<ProductDetails>? = null

        @JvmStatic
        fun launchPurchaseFlow(activity: Activity) {
            try {
                productsDetailsList?.let { list ->
                    val productDetails = list[0] // Only one product: "ads_free_life_time"

                    val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()

                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(listOf(productDetailsParams))
                        .build()

                    billingClient?.launchBillingFlow(activity, billingFlowParams)
                }
            } catch (e: Exception) {
                Log.e("checkbilling", "launchPurchaseFlow: ${e.message} ")
                Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }

        @JvmStatic
        fun verifyInAppPurchase(context: Context, onResults: () -> Unit) {
            billingClient?.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP).build()) { _, purchaseList ->
                purchaseList.forEach {
                    handlePurchase(it)
                    Log.e("checkbilling", "verifyInAppPurchase: INAPP INAPP ${it.packageName} ")

                    if (it.isAcknowledged){
                        PrefUtilForAppAdsFree.setAdsForLiftTimeString("ads_free_life_time", context)

                        Log.e("checkbilling", "Purchased INAPP")
                        PrefUtilForAppAdsFree.setPremium(context, true)

                    }else{
                        PrefUtilForAppAdsFree.setAdsForLiftTimeString("no life time", context)
                        Log.d("checkbilling", "no life time")
                        Log.e("checkbilling", "NOT Purchased INAPP")

                        PrefUtilForAppAdsFree.setPremium(context, false)

                    }
                }
            }
        }

        private fun handlePurchase(purchase: Purchase) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()

                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                            PrefUtilForAppAdsFree.setPremiumString(purchase.products[0], context)
//                            val intent = Intent(context, AddSuccessfullyActivity::class.java)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            context.startActivity(intent)
                        }
                    }
                }

            }
        }
    }
}































//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//import android.widget.Toast
//import com.android.billingclient.api.*
//import com.android.billingclient.api.BillingClient.ProductType
//import com.android.billingclient.api.QueryProductDetailsParams.Product
//
//
//class BillingIASForAppAdsFree(private val context: Context) {
//    private val prefUtilForAppAdsFree by lazy { PrefUtilForAppAdsFree(context) }
//
//    init {
//        try {
//            initBilling()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun initBilling() {
//        //Initialize a BillingClient with PurchasesUpdatedListener onCreate method
//        billingClient = BillingClient.newBuilder(context)
//            .enablePendingPurchases()
//            .setListener { billingResult, _ ->
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    Log.e("checkbilling", "initBilling: ")
//                    val intent = Intent(context,AddSuccessfullyActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                    context.startActivity(intent)
//
////                    PrefUtilForAppAdsFree.setPremiumString(purchase.products[0], context)
//
////                    PrefUtilForAppAdsFree.setPremium(context, true)
//
//                }
//            }.build()
//
//        establishConnection()
//    }
//
//    fun establishConnection() {
//        billingClient?.startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    Log.e("checkbilling", "onBillingSetupFinished: connected")
//                    verifySubPurchase(context) {}
//                    showProducts()
//                }
//            }
//
//            override fun onBillingServiceDisconnected() {
//                try {
//                    establishConnection()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        })
//    }
//
//    private fun showProducts() {
//        val productList = listOf(
//            Product.newBuilder().setProductId("weekly_plan").setProductType(ProductType.SUBS).build(),
//            Product.newBuilder().setProductId("monthly_plan").setProductType(ProductType.SUBS).build(),
//            Product.newBuilder().setProductId("yearly_plan").setProductType(ProductType.SUBS).build(),
//            Product.newBuilder().setProductId("ads_free_app").setProductType(ProductType.SUBS).build(),
//        )
//
//        //                .setProductId("android.test.purchased")
//
//        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)
//
//        billingClient?.queryProductDetailsAsync(params.build()) { _, productDetailsList ->
//            // Process the result
//            Log.e("checkbilling", "showProducts: ${productDetailsList.size}")
//            Log.e("checkbilling", "Available products: free add ${productDetailsList.joinToString { it.productId }}")
//            Log.e("checkbilling", "Available products: free add "+productDetailsList[3].subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice.toString())
//
//            productsDetailsList = productDetailsList
//            productDetailsList.forEachIndexed { index, _ ->
//                prefUtilForAppAdsFree.setString("key$index", productDetailsList[index].subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice.toString())
//                Log.d("checkbilling", "ids free add  "+index.toString()+"   "+productDetailsList[index].subscriptionOfferDetails?.get(0)?.pricingPhases)
//
//
//            }
//        }
//    }
//
//
//    companion object {
//        private var billingClient: BillingClient? = null
//        private var productsDetailsList: MutableList<ProductDetails>? = null
//        @JvmStatic
//        fun launchPurchaseFlow(plan: Int, activity: Activity) {
//            try {
//                productsDetailsList?.let { list ->
//                    val offerToken = list[plan].subscriptionOfferDetails?.get(0)?.offerToken
//                    val productDetailsParamsList =
//                        listOf(
//                            BillingFlowParams.ProductDetailsParams.newBuilder()
//                                .setProductDetails(list[plan])
//                                .setOfferToken(offerToken!!)
//                                .build()
//                        )
//                    val billingFlowParams =
//                        BillingFlowParams.newBuilder()
//                            .setProductDetailsParamsList(productDetailsParamsList)
//                            .build()
//
//                    billingClient?.launchBillingFlow(activity, billingFlowParams)
//                }
//            } catch (e: Exception) {
//                Log.e("checkbilling", "launchPurchaseFlow: ${e.message} ", )
//                Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        @JvmStatic
//        fun verifySubPurchase(context: Context, onResults: () -> Unit) {
//            billingClient?.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()) { _, purchaseList ->
//                // Process the result
//                Log.e("checkbilling", "verifySubPurchase:")
//
//
//
//                if (purchaseList.size > 0) {
//                    val purchase = purchaseList[0]
//                    val purchaseToken = purchase.purchaseToken
//
//                    // Acknowledge the purchase
//                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken).build()
//
//                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
//                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                            // Acknowledgment successful
////                            PrefUtilForAppAdsFree.setPremium(context, true)
//                            Log.e("checkbilling", "verifySubPurchase: ${purchase.products.size}")
//                            PrefUtilForAppAdsFree.setPremiumString(purchase.products[0], context)
//                            val intent = Intent(context, AddSuccessfullyActivity::class.java)
//                            context.startActivity(intent)
//                            onResults()
//                        } else {
//                            // Acknowledgment failed
//                            Log.e("checkbilling", "Acknowledge purchase failed with response code: ${billingResult.responseCode}")
//                            PrefUtilForAppAdsFree.setPremiumString("", context)
////                            PrefUtilForAppAdsFree.setPremium(context, false)
//                        }
//                    }
//                } else {
//                    PrefUtilForAppAdsFree.setPremiumString("", context)
////                    PrefUtilForAppAdsFree.setPremium(context, false)
//                }
//            }
//        }
//
//
//
//
//    }
//}
//
//
