package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.pixelz360.docsign.imagetopdf.creator.FirstActivity;
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;

public class AdLoaderActivity extends Activity {

    private AppOpenAd appOpenAd;
//    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"; // Replace with real AdMob Ad Unit ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);






        if (PrefUtilForAppAdsFree.isPremium(AdLoaderActivity.this) || PrefUtilForAppAdsFree.getAdsForLiftTimeString(AdLoaderActivity.this).equals("ads_free_life_time")){
            // Try loading Open App Ad
            Log.d("checkloadopenad", "isPremium FirstActivity");
        }else {
            loadAd();
            Log.d("checkloadopenad", "loadAd() AdLoaderActivity");
        }



    }

    private void loadAd() {
        AppOpenAd.load(this, getString(R.string.open_app_ads), new com.google.android.gms.ads.AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        appOpenAd = ad;
                        showAd();
                    }

                    public void onAdFailedToLoad(AdError adError) {
                        Log.d("checkloadopenad", "Ad Failed to Load in AdLoaderActivity: " + adError.getMessage());
                        moveToHomeActivity();
                    }
                });
    }

    private void showAd() {
        if (appOpenAd != null) {
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    moveToHomeActivity();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    moveToHomeActivity();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    appOpenAd = null; // Ad is no longer available after being shown
                }
            });
            appOpenAd.show(this);
        } else {
            moveToHomeActivity();
        }
    }

    private void moveToHomeActivity() {
        startActivity(new Intent(AdLoaderActivity.this, HomeActivity.class));
        finish(); // Ensure AdLoaderActivity is closed
    }
}
