package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.List;



public class BillingManagerSubs {

    private BillingClient billingClient;
    private Activity activity;
    private TextView weeklyPriceTextView, monthlyPriceTextView, yearllyPriceTextView, adsFreeTextPrice;

    public BillingManagerSubs(Activity activity, TextView weeklyPriceTextView, TextView monthlyPriceTextView,
                          TextView yearllyPriceTextView, TextView adsFreeTextPrice) {
        this.activity = activity;
        this.weeklyPriceTextView = weeklyPriceTextView;
        this.monthlyPriceTextView = monthlyPriceTextView;
        this.yearllyPriceTextView = yearllyPriceTextView;
        this.adsFreeTextPrice = adsFreeTextPrice;

        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(purchaseListener)
                .build();
    }

    public void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    querySkuDetails(); // Query SKU details
                    verifySubscriptionStatus(); // Verify subscription status on setup
                } else {
                    Log.e("BillingManager", "Billing setup failed: " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.e("BillingManager", "Billing service disconnected.");
                startConnection(); // Retry connection
            }
        });
    }

    // Query SKU Details
    private void querySkuDetails() {
        List<String> skuList = List.of("weekly_plan", "monthly_plan", "yearly_plan", "ads_free_life_time");

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    String sku = skuDetails.getSku();
                    String price = skuDetails.getPrice();

                    switch (sku) {
                        case "weekly_plan":
                            weeklyPriceTextView.setText(price);
                            Log.d("BillingManagerSubs","weeklyPriceTextView "+price);

                            break;
                        case "monthly_plan":
                            monthlyPriceTextView.setText(price);
                            Log.d("BillingManagerSubs","monthlyPriceTextView "+price);

                            break;
                        case "yearly_plan":
                            yearllyPriceTextView.setText(price);

                            Log.d("BillingManagerSubs","yearllyPriceTextView "+price);

                            break;
                        case "ads_free_life_time":
                            adsFreeTextPrice.setText(price);
                            break;
                    }
                }
            } else {
                Log.e("BillingManager", "Failed to query SKU details: " + billingResult.getDebugMessage());
            }
        });
    }

    // Method to initiate purchase
    public void initiatePurchase(String skuId, boolean isSubscription) {
        List<String> skuList = List.of(skuId);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS);

        billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();
                    billingClient.launchBillingFlow(activity, billingFlowParams);
                }
            } else {
                Toast.makeText(activity, "Error initiating purchase: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Purchases Updated Listener
    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handleSubscription(purchase);
                Toast.makeText(activity, "Purchase successful!", Toast.LENGTH_SHORT).show();
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(activity, "Purchase canceled by user", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Purchase failed: " + billingResult.getDebugMessage());
        }
    };



    // Verify Subscription Status
    public void verifySubscriptionStatus() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                boolean isActive = false;

                for (Purchase purchase : purchases) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        handleSubscription(purchase);
                        isActive = true; // At least one valid subscription found
                    }
                }

                if (!isActive) {
                    // If no active subscriptions, reset shared preferences
                    resetSharedPreferences();
                }
            } else {
                Log.e("BillingManager", "Error verifying subscription: " + billingResult.getDebugMessage());
                resetSharedPreferences(); // Reset if query failed
            }
        });
    }

    // Handle Subscription
    private void handleSubscription(Purchase purchase) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit();
        long currentTime = System.currentTimeMillis();

        if (purchase.getProducts().contains("weekly_plan")) {
            editor.putBoolean("ads_removed", true);
//            editor.putString("subscription_type", "weekly");
//            editor.putLong("subscription_end_time", currentTime + 7 * 24 * 60 * 60 * 1000L);
        } else if (purchase.getProducts().contains("monthly_plan")) {
            editor.putBoolean("ads_removed", true);
//            editor.putString("subscription_type", "monthly");
//            editor.putLong("subscription_end_time", currentTime + 30 * 24 * 60 * 60 * 1000L);
        } else if (purchase.getProducts().contains("yearly_plan")) {
            editor.putBoolean("ads_removed", true);
//            editor.putString("subscription_type", "yearly");
//            editor.putLong("subscription_end_time", currentTime + 365 * 24 * 60 * 60 * 1000L);
        } else if (purchase.getProducts().contains("ads_free_life_time")) {
            editor.putBoolean("ads_removed", true);
//            editor.putString("subscription_type", "permanent");
//            editor.putLong("subscription_end_time", Long.MAX_VALUE);
        }

        editor.apply();
        Toast.makeText(activity, "Purchase verified successfully", Toast.LENGTH_SHORT).show();
    }

    // Reset Shared Preferences if no active subscription
    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = activity.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit();
        editor.putBoolean("ads_removed", false);
        editor.putString("subscription_type", "");
        editor.apply();
    }

    public void endConnection() {
        billingClient.endConnection();
    }

    // Purchase Listener
    PurchasesUpdatedListener purchaseListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handleSubscription(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(activity, "Purchase canceled", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("BillingManager", "Purchase failed: " + billingResult.getDebugMessage());
        }
    };
}

