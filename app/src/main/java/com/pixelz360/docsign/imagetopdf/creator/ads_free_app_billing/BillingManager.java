package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class BillingManager {

    private BillingClient billingClient;
    private Activity activity;
    private TextView weeklyPriceTextView, monthlyPriceTextView, yearllyPriceTextView, adsFreeTextPrice;

    public BillingManager(Activity activity, TextView weeklyPriceTextView, TextView monthlyPriceTextView, TextView yearllyPriceTextView, TextView adsFreeTextPrice, Context context) {
        this.activity = activity;
        this.weeklyPriceTextView = weeklyPriceTextView;
        this.monthlyPriceTextView = monthlyPriceTextView;
        this.yearllyPriceTextView = yearllyPriceTextView;
        this.adsFreeTextPrice = adsFreeTextPrice;

        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(purchaseListener)
                .build();

        Log.d("BillingManager","BillingManager constrctor ");

    }

    public void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    querySkuDetails(); // Query available SKU details
                    checkSubscriptionStatus(); // Check subscription status on setup
                } else {
                    Log.d("BillingManager", "Billing setup failed: " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d("BillingManager", "Billing service disconnected. Trying to reconnect...");
                startConnection(); // Retry connection
            }
        });
    }

    // Method to query SKU details
    private void querySkuDetails() {
//        List<String> skuList = List.of("weekly_subscription", "monthly_subscription", "yearly_subscription", "permanent_unlock");
        List<String> skuList = List.of("weekly_plan", "monthly_plan", "yearly_plan", "ads_free_life_time");
//        List<String> skuList = List.of("ads_free_app", "monthly_plan", "weekly_plan", "yearly_plan");

        Log.d("BillingManager","skuList "+skuList.size());


        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();

        Log.d("BillingManager","querySkuDetails ");


        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
//                Log.d("BillingManager","skuDetailsList skuDetailsList "+skuDetailsList.get(2));

                for (SkuDetails skuDetails : skuDetailsList) {
                    String sku = skuDetails.getSku();
                    String price = skuDetails.getPrice();

                    Log.d("BillingManager","sku "+sku);
                    Log.d("BillingManager","skuDetailsList "+skuDetailsList.get(2).getSku());

//
//                    switch (sku) {
//                        case "weekly_subscription":
//                            weeklyPriceTextView.setText(price);
//                            Log.d("BillingManager","weeklyPriceTextView "+price);
//
//                            break;
//                        case "monthly_subscription":
//                            monthlyPriceTextView.setText(price);
//                            Log.d("BillingManager","monthlyPriceTextView "+price);
//                            break;
//                        case "yearly_subscription":
//                            yearllyPriceTextView.setText(price);
//                            Log.d("BillingManager","yearllyPriceTextView "+price);
//                            break;
//                        case "permanent_unlock":
//                            adsFreeTextPrice.setText(price);
//                            break;
//                    }

                    switch (sku) {
                        case "weekly_plan":
                            weeklyPriceTextView.setText(price);
                            Log.d("BillingManager","weeklyPriceTextView "+price);

                            break;
                        case "monthly_plan":
                            monthlyPriceTextView.setText(price);
                            Log.d("BillingManager","monthlyPriceTextView "+price);

                            break;
                        case "yearly_plan":
                            yearllyPriceTextView.setText(price);

                            Log.d("BillingManager","yearllyPriceTextView "+price);

                            break;
                        case "ads_free_app":
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
                .setType(isSubscription ? BillingClient.SkuType.SUBS : BillingClient.SkuType.SUBS);

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

    PurchasesUpdatedListener purchaseListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(activity, "Purchase canceled by user", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("BillingManager", "Purchase failed: " + billingResult.getDebugMessage());
        }
    };



    // Verify Subscription Status
    public void verifySubscriptionStatus() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                boolean isActive = false;

                for (Purchase purchase : purchases) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        handlePurchase(purchase);
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



    // Method to handle purchase
    private void handlePurchase(Purchase purchase) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit();
        long currentTime = System.currentTimeMillis();

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
//            if (purchase.getProducts().contains("weekly_subscription")) {
//                editor.putBoolean("ads_removed", true);
//                editor.putString("subscription_type", "weekly");
//                editor.putLong("subscription_end_time", currentTime + 7 * 24 * 60 * 60 * 1000L); // 1 week
//            } else if (purchase.getProducts().contains("monthly_subscription")) {
//                editor.putBoolean("ads_removed", true);
//                editor.putString("subscription_type", "monthly");
//                editor.putLong("subscription_end_time", currentTime + 30 * 24 * 60 * 60 * 1000L); // 1 month
//            } else if (purchase.getProducts().contains("yearly_subscription")) {
//                editor.putBoolean("ads_removed", true);
//                editor.putString("subscription_type", "yearly");
//                editor.putLong("subscription_end_time", currentTime + 365 * 24 * 60 * 60 * 1000L); // 1 year
//            } else if (purchase.getProducts().contains("permanent_unlock")) {
//                editor.putBoolean("ads_removed", true);
//                editor.putString("subscription_type", "permanent");
//                editor.putLong("subscription_end_time", Long.MAX_VALUE); // Permanent unlock
//            }



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
            } else if (purchase.getProducts().contains("ads_free_app")) {
                editor.putBoolean("ads_removed", true);
//            editor.putString("subscription_type", "permanent");
//            editor.putLong("subscription_end_time", Long.MAX_VALUE);
            }


            editor.apply();

            Toast.makeText(activity, "Purchase successful", Toast.LENGTH_LONG).show();

            // Navigate to success screen
            Intent intent = new Intent(activity, AddSuccessfullyActivity.class);
            activity.startActivity(intent);
            activity.finish();
        } else {
            Log.e("BillingManager", "Purchase state not completed: " + purchase.getPurchaseState());
        }
    }

    public void checkSubscriptionStatus() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase); // Handle any active subscriptions
                }
            } else {
                SharedPreferences.Editor editor = activity.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit();
                editor.putBoolean("ads_removed", false);
                editor.apply();
            }
        });
    }

    // Method to restore purchases
    public void restorePurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                boolean hasRestoredPurchases = false;
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase); // Restore any active subscriptions
                    hasRestoredPurchases = true;
                }
                if (hasRestoredPurchases) {
                    Toast.makeText(activity, "Purchases restored successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "No purchases to restore", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "Failed to restore purchases", Toast.LENGTH_SHORT).show();
                Log.e("BillingManager", "Error restoring purchases: " + billingResult.getDebugMessage());
            }
        });

        // If you also have one-time in-app purchases, query for them as well
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                boolean hasRestoredPurchases = false;
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase); // Restore any active in-app purchases
                    hasRestoredPurchases = true;
                }
                if (hasRestoredPurchases) {
                    Toast.makeText(activity, "In-app purchases restored successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = activity.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit();
        editor.putBoolean("ads_removed", false);
        editor.putString("subscription_type", "");
        editor.apply();
    }


    public void endConnection() {
        billingClient.endConnection();
    }
}
