package com.pixelz360.docsign.imagetopdf.creator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.AdLoaderActivity;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree;
import com.pixelz360.docsign.imagetopdf.creator.language.LanguageHelper;

public class FirstActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 7000; // 5 seconds
    private AppOpenAd appOpenAd;
    private boolean isAdLoaded = false;
    private boolean isFirstTime = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_sample_app_open_ad);

        // Set Status Bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.red));

        // Load language settings
        LanguageHelper.loadLocale(this);

        // Check if it's the first time opening the app
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        isFirstTime = preferences.getBoolean("isFirstTime", true);

        // Load Firebase Analytics
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        logAnalyticsEvent(analytics);



        if (PrefUtilForAppAdsFree.isPremium(FirstActivity.this) || PrefUtilForAppAdsFree.getAdsForLiftTimeString(FirstActivity.this).equals("ads_free_life_time")){
            // Try loading Open App Ad
            Log.d("checkloadopenad", "isPremium FirstActivity");
        }else {
            loadOpenAppAd();
            Log.d("checkloadopenad", "loadOpenAppAd() FirstActivity");

        }

        // Wait for SPLASH_DURATION before navigating
        new Handler().postDelayed(() -> navigateToNextActivity(), SPLASH_DURATION);
    }

    private void loadOpenAppAd() {
        AppOpenAd.load(this, getString(R.string.open_app_ads), new AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        appOpenAd = ad;
                        isAdLoaded = true;
                        Log.d("checkloadopenad", "Ad Loaded Successfully");
                    }

                    public void onAdFailedToLoad(com.google.android.gms.ads.AdError adError) {
                        isAdLoaded = false;
                        Log.d("checkloadopenad", "Ad Failed to Load: " + adError.getMessage());
                    }
                });
    }

    private void navigateToNextActivity() {
        Intent intent;
        if (isFirstTime) {
            intent = new Intent(FirstActivity.this, GetStartedActivity.class);
            intent.putExtra("namelanguage", "yes");
            getPreferences(MODE_PRIVATE).edit().putBoolean("isFirstTime", false).apply();
        } else if (isAdLoaded) {
            // If ad is loaded, move to AdLoaderActivity
            intent = new Intent(FirstActivity.this, AdLoaderActivity.class);
        } else {
            // If no ad, go to HomeActivity directly
            intent = new Intent(FirstActivity.this, HomeActivity.class);
        }

        startActivity(intent);
        finish();
    }

    private void logAnalyticsEvent(FirebaseAnalytics analytics) {
        Bundle bundle = new Bundle();
        bundle.putString("activity_name", "FirstActivity");
        analytics.logEvent("activity_created", bundle);
    }
}
