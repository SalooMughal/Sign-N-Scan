package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.QueryProductDetailsParams.Product


class BillingIASForAppAdsFreeSubs(private val context: Context) {
    private val prefUtilForAppAdsFree by lazy { PrefUtilForAppAdsFree(context) }

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
            .setListener { billingResult, _ ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.e("checkbilling", "initBilling: BillingIASForAppAdsFreeSubs side")
//                    val intent = Intent(context,AddSuccessfullyActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//                    context.startActivity(intent)

//                    PrefUtilForAppAdsFree.setPremiumString(purchase.products[0], context)

//                    PrefUtilForAppAdsFree.setPremium(context, true)

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
            Product.newBuilder().setProductId("weekly_plan").setProductType(ProductType.SUBS).build(),
            Product.newBuilder().setProductId("monthly_plan").setProductType(ProductType.SUBS).build(),
            Product.newBuilder().setProductId("yearly_plan").setProductType(ProductType.SUBS).build(),
            Product.newBuilder().setProductId("ads_free_app").setProductType(ProductType.SUBS).build(),
        )

        //                .setProductId("android.test.purchased")

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)

        billingClient?.queryProductDetailsAsync(params.build()) { _, productDetailsList ->
            // Process the result
            Log.e("checkbilling", "showProducts: ${productDetailsList.size}")
            Log.e("checkbilling", "Available products: free add ${productDetailsList.joinToString { it.productId }}")
            Log.e("checkbilling", "Available products: free add "+productDetailsList[3].subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice.toString())

            productsDetailsList = productDetailsList
            productDetailsList.forEachIndexed { index, _ ->
                prefUtilForAppAdsFree.setString("key$index", productDetailsList[index].subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice.toString())
                Log.d("checkbilling", "ids free add  "+index.toString()+"   "+productDetailsList[index].subscriptionOfferDetails?.get(0)?.pricingPhases)


            }
        }
    }


    companion object {
        private var billingClient: BillingClient? = null
        private var productsDetailsList: MutableList<ProductDetails>? = null
        @JvmStatic
        fun launchPurchaseFlow(plan: Int, activity: Activity) {
            try {
                productsDetailsList?.let { list ->
                    val offerToken = list[plan].subscriptionOfferDetails?.get(0)?.offerToken
                    val productDetailsParamsList =
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(list[plan])
                                .setOfferToken(offerToken!!)
                                .build()
                        )
                    val billingFlowParams =
                        BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()

                    billingClient?.launchBillingFlow(activity, billingFlowParams)
                }
            } catch (e: Exception) {
                Log.e("checkbilling", "launchPurchaseFlow: ${e.message} ", )
                Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }

        @JvmStatic
        fun verifySubPurchase(context: Context, onResults: () -> Unit) {
            billingClient?.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()) { _, purchaseList ->
                // Process the result
                Log.e("checkbilling", "verifySubPurchase:")



                if (purchaseList.size > 0) {
                    val purchase = purchaseList[0]
                    val purchaseToken = purchase.purchaseToken

                    // Acknowledge the purchase
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken).build()

                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            // Acknowledgment successful
//                            PrefUtilForAppAdsFree.setPremium(context, true)
                            Log.e("checkbilling", "verifySubPurchase: ${purchase.products.size}")
                            PrefUtilForAppAdsFree.setPremiumString(purchase.products[0], context)
//                            val intent = Intent(context, AddSuccessfullyActivity::class.java)
//                            context.startActivity(intent)
                            onResults()

                            Log.e("checkbilling", "AddSuccessfullyActivity: BillingIASForAppAdsFreeSubs side")
                        } else {
                            // Acknowledgment failed
                            Log.e("checkbilling", "Acknowledge purchase failed with response code: ${billingResult.responseCode}")
                            PrefUtilForAppAdsFree.setPremiumString("", context)
//                            PrefUtilForAppAdsFree.setPremium(context, false)
                        }
                    }
                } else {
                    PrefUtilForAppAdsFree.setPremiumString("", context)
//                    PrefUtilForAppAdsFree.setPremium(context, false)
                }
            }
        }




    }
}


