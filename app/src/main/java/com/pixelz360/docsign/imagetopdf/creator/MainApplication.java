package com.pixelz360.docsign.imagetopdf.creator;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.MobileAds;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.BillingIASForAppAdsFreeInApp;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.BillingIASForAppAdsFreeSubs;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.BillingIASForPdfTools;

import java.util.Calendar;
import java.util.Locale;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application  {
//    private AppOpenAdManager appOpenAdManager;

    private final long WEEK_IN_MILLIS = 7 * 24 * 60 * 60 * 1000L;
    private final long MONTH_IN_MILLIS = 30 * 24 * 60 * 60 * 1000L;
    private final long YEAR_IN_MILLIS = 365 * 24 * 60 * 60 * 1000L;

    BillingIASForPdfTools billingIASForPdfTools;
    BillingIASForAppAdsFreeInApp billingIASForAppAdsFree;
    BillingIASForAppAdsFreeSubs billingIASForAppAdsFreeSubs;

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, initializationStatus -> {
            Log.d("AdManager", "AdMob initialized.");
        });

//        appOpenAdManager = new AppOpenAdManager(this);
        Log.d("checkloadopenad","MainApplication onCreate");


        // Get Test Device ID from Logcat
//        List<String> testDeviceIds = Collections.singletonList("3db83191-3af2-4942-bcca-fd9aa3706dcf");
//        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder()
//                .setTestDeviceIds(testDeviceIds)
//                .build();
//        MobileAds.setRequestConfiguration(requestConfiguration);


//        // Initialize CAS with test ad mode


            // Check if ads should be removed
            if (shouldRemoveAds()) {
                // Ads removed, hide the adView
                Log.d("checkAd","Add Is Removed");
            } else {
                // Show ads
                Log.d("checkAd","Add Is not Removed");

//                initializeCAS();

            }


            billingIASForAppAdsFree = new BillingIASForAppAdsFreeInApp(this);
        billingIASForAppAdsFreeSubs = new BillingIASForAppAdsFreeSubs(this);
        billingIASForPdfTools  = new BillingIASForPdfTools(this);




//        initializeCAS();

        ApplicationObserver observer = new ApplicationObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(observer);


    }

    public static String formatTimestamp(long timestamp)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd/MM/yyyy", calendar).toString();

        return date;
    }

    private boolean shouldRemoveAds() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isAdsRemoved = prefs.getBoolean("ads_removed", false);
        String subscriptionType = prefs.getString("subscription_type", "");
        long purchaseTime = prefs.getLong("subscription_end_time", 0);

        if (!isAdsRemoved) {
            // Ads were never removed
            return false;
        }

        // If the subscription is permanent, ads are always removed
        if (subscriptionType.equals("permanent")) {
            return true;
        }

        // For weekly and monthly, calculate if the subscription has expired
        long currentTime = System.currentTimeMillis();
        if (subscriptionType.equals("weekly")) {
            // Check if the week has passed since purchase
            if ((currentTime - purchaseTime) < WEEK_IN_MILLIS) {
                return true; // Weekly subscription is still valid
            } else {
                // Subscription has expired, show ads again
                prefs.edit().putBoolean("ads_removed", false).apply();
                return false;
            }
        } else if (subscriptionType.equals("monthly")) {
            // Check if the month has passed since purchase
            if ((currentTime - purchaseTime) < MONTH_IN_MILLIS) {
                return true; // Monthly subscription is still valid
            } else {
                // Subscription has expired, show ads again
                prefs.edit().putBoolean("ads_removed", false).apply();
                return false;
            }
        } else if (subscriptionType.equals("yearly")) {
            // Check if the month has passed since purchase
            if ((currentTime - purchaseTime) < YEAR_IN_MILLIS) {
                return true; // Monthly subscription is still valid
            } else {
                // Subscription has expired, show ads again
                prefs.edit().putBoolean("ads_removed", false).apply();
                return false;
            }
        }




        // Default case: Ads should be shown if no valid subscription is found
        return false;
    }


//    private void initializeCAS() {
//        // Configure CAS settings
//        CAS.buildManager()
//                .withCasId("com.pixelz360.docsign.imagetopdf.creator") // Your CAS App ID
//                .withCompletionListener(config -> {
//                    // Retrieve initialization results
//                    String initErrorOrNull = config.getError();
//
//
//                    // Handle any initialization errors
//                    if (initErrorOrNull != null) {
//                        Log.e("CAS111111", "Initialization Error: " + initErrorOrNull);
//                    } else {
//                        Log.d("CAS111111", "CAS initialized successfully.");
//
//                        CAS.getSettings().setAllowInterstitialAdsWhenVideoCostAreLower(true);
//                        CAS.getSettings().setMutedAdSounds(true);
//                        manager = config.getManager(); // Store the MediationManager instance
//
//                    }
//                })
//                .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded, AdType.AppOpen) // List ad formats used in app
//                .withTestAdMode(true)
//                .initialize(this); // Initialize CAS without test ad mode
//
//        // Optionally, log to ensure original ads are being used
//        Log.d("CAS111111", "Test Ad Mode is disabled. Real ads should be shown.   ");
//    }





    class ApplicationObserver implements DefaultLifecycleObserver {
        @Override
        public void onResume(LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onResume(owner);
            Log.d("lifecycle11", "onResume: APPLICATION RESUMED");
        }

        @Override
        public void onPause(LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onPause(owner);
            Log.d("lifecycle11", "onPause: APPLICATION PAUSED");
        }

        // Implement any other lifecycle event as needed
    }
}

