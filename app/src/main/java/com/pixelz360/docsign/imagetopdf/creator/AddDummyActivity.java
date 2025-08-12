package com.pixelz360.docsign.imagetopdf.creator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class AddDummyActivity extends AppCompatActivity {


//    private Button nextActivity;
//
//    private BillingManager billingManager;
//    private final long WEEK_IN_MILLIS = 7 * 24 * 60 * 60 * 1000L;
//    private final long MONTH_IN_MILLIS = 30 * 24 * 60 * 60 * 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dummy);



//        Button weeklyButton = findViewById(R.id.btnWeekly);
//        Button monthlyButton = findViewById(R.id.btnMonthly);
//        Button permanentButton = findViewById(R.id.btnPermanent);
//
//        // Initialize BillingManager and pass the TextViews for price display
//        billingManager = new BillingManager(this, weeklyPriceTextView, monthlyPriceTextView, permanentPriceTextView);
//        billingManager.startConnection();
//
//        // Check if ads should be removed
//        if (shouldRemoveAds()) {`
//            // Ads removed, hide the adView
//            Log.d("checkAd","Add Is Removed");
//        } else {`
//            // Show ads
//            Log.d("checkAd","Add Is not Removed");
//
//        }
//
//        // Set button listeners for purchasing plans
//        weeklyButton.setOnClickListener(v -> billingManager.initiatePurchase("weekly_subscription", true));
//        monthlyButton.setOnClickListener(v -> billingManager.initiatePurchase("monthly_subscription", true));
//        permanentButton.setOnClickListener(v -> billingManager.initiatePurchase("add_free_app", false));
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        billingManager.endConnection();
//    }```````````````````````````````````````````````````````````````````````````````````````
//
//    private boolean shouldRemoveAds() {
//        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
//        boolean isAdsRemoved = prefs.getBoolean("ads_removed", false);
//        String subscriptionType = prefs.getString("subscription_type", "");
//        long purchaseTime = prefs.getLong("purchase_time", 0);
//
//        if (!isAdsRemoved) {
//            // Ads were never removed
//            return false;
//        }
//
//        // If the subscription is permanent, ads are always removed
//        if (subscriptionType.equals("permanent")) {
//            return true;
//        }
//monthly
//        // For weekly and , calculate if the subscription has expired
//        long currentTime = System.currentTimeMillis();
//        if (subscriptionType.equals("weekly")) {
//            // Check if the week has passed since purchase
//            if ((currentTime - purchaseTime) < WEEK_IN_MILLIS) {
//                return true; // Weekly subscription is still vali d
//            } else {
//                // Subscription has expired, show ads again
//                prefs.edit().putBoolean("ads_removed", false).apply();
//                return false;
//            }
//        } else if (subscriptionType.equals("monthly")) {
//            // Check if the month has passed since purchase
//            if ((currentTime - purchaseTime) < MONTH_IN_MILLIS) {
//                return true; // Monthly subscription is still valid
//            } else {
//                // Subscription has expired, show ads again
//                prefs.edit().putBoolean("ads_removed", false).apply();
//                return false;
//            }
//        }
//
//        // Default case: Ads should be shown if no valid subscription is found
//        return false;
    }
}
