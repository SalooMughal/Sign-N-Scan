package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.pixelz360.docsign.imagetopdf.creator.FirstActivity;
import com.pixelz360.docsign.imagetopdf.creator.MainApplication;
import com.pixelz360.docsign.imagetopdf.creator.R;

//public class AppOpenAdManager implements Application.ActivityLifecycleCallbacks {
//    private final MainApplication myApplication;
//    private AppOpenAd appOpenAd;
//    private boolean isAdShowing = false;
//    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"; // Replace with your AdMob Ad Unit ID
//
//    public AppOpenAdManager(MainApplication application) {
//        this.myApplication = application;
//        myApplication.registerActivityLifecycleCallbacks(this);
//        Log.d("checkloadopenad","AppOpenAdManager");
//
//        loadAd();
//    }

//    public void loadAd() {
//        AdRequest request = new AdRequest.Builder().build();
//        AppOpenAd.load(myApplication, myApplication.getString(R.string.open_app_ads), request,
//                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
//                new AppOpenAd.AppOpenAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(AppOpenAd ad) {
//                        appOpenAd = ad;
//                        Log.d("checkloadopenad","onAdLoaded");
//                    }
//
//                    public void onAdFailedToLoad(AdError adError) {
//                        appOpenAd = null;
//                        Log.d("checkloadopenad","onAdFailedToLoad");
//                    }
//                });
//    }
//
//    private long lastAdShownTime = 0;
//    private static final long AD_DELAY = 30 * 60 * 1000; // 30 minutes in milliseconds
//
//    public void showAdIfAvailable(Activity activity) {
//        long currentTime = System.currentTimeMillis();
//
//        if (appOpenAd != null && !isAdShowing && (currentTime - lastAdShownTime > AD_DELAY)) {
//            appOpenAd.show(activity);
//            lastAdShownTime = currentTime;
//            isAdShowing = true;
//            Log.d("checkloadopenad","showAdIfAvailable appOpenAd.show(activity); ");
//
//        } else {
//            loadAd(); // Load a new ad if needed
//            Log.d("checkloadopenad","showAdIfAvailable loadAd(); ");
//        }
//    }
//
//
////    public void showAdIfAvailable(Activity activity) {
////        if (appOpenAd != null && !isAdShowing) {
////            appOpenAd.show(activity);
////            isAdShowing = true;
////            Log.d("checkloadopenad","showAdIfAvailable appOpenAd.show(activity); ");
////        } else {
////            loadAd();
////            Log.d("checkloadopenad","showAdIfAvailable loadAd(); ");
////        }
////    }
//
//    @Override
//    public void onActivityStarted(@NonNull Activity activity) {
//        showAdIfAvailable(activity);
//        Log.d("checkloadopenad","AppOpenAdManager onActivityStarted "+activity);
//
//        if (activity instanceof FirstActivity) {
////            appOpenAdManager.showAdIfAvailable(activity);
//            showAdIfAvailable(activity);
//            Log.d("checkloadopenad","AppOpenAdManager onActivityStarted " + activity);
//        }
//
//    }
//
//    @Override
//    public void onActivityStopped(@NonNull Activity activity) {}
//
//    @Override
//    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {}
//
//    @Override
//    public void onActivityResumed(@NonNull Activity activity) {}
//
//    @Override
//    public void onActivityPaused(@NonNull Activity activity) {}
//
//    @Override
//    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
//
//    @Override
//    public void onActivityDestroyed(@NonNull Activity activity) {}
//}

