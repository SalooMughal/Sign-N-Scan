package com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class InterstitialAdManager_Java {

    private final Activity activity;
    private final String adUnitId;
    private InterstitialAd interstitialAd;
    private boolean isAdLoading = false;
    private boolean isAdShowing = false; // Prevent repeated clicks
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;

    public InterstitialAdManager_Java(Activity activity, String adUnitId) {
        this.activity = activity;
        this.adUnitId = adUnitId;
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        isAdLoading = true;

        InterstitialAd.load(activity, adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                interstitialAd = ad;
                isAdLoading = false;
                Log.d("InterstitialAdManager", "Ad loaded successfully");
                if (timeoutRunnable != null) {
                    handler.removeCallbacks(timeoutRunnable);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                interstitialAd = null;
                isAdLoading = false;
                Log.e("InterstitialAdManager", "Ad failed to load: " + adError.getMessage());
                if (timeoutRunnable != null) {
                    handler.removeCallbacks(timeoutRunnable);
                }
            }
        });
    }

    public void showAdOrProceed(ProgressBar progressBar, Runnable onProceed) {
        if (isAdShowing) {
            // Ignore repeated clicks while an ad is already showing
            Log.d("InterstitialAdManager", "Ad is already showing, ignoring click");
            return;
        }

        if (interstitialAd != null) {
            // Ad is ready, show it
            isAdShowing = true; // Lock clicks
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    interstitialAd = null;
                    isAdShowing = false;
                    loadAd(); // Reload ad after dismissal
                    onProceed.run();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    interstitialAd = null;
                    isAdShowing = false;
                    loadAd(); // Reload ad on failure
                    onProceed.run();
                }
            });
            interstitialAd.show(activity);
        } else if (isAdLoading) {
            // Ad is still loading, show progress bar
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }

            // Timeout logic: Wait for 5 seconds
            timeoutRunnable = () -> {
                if (isAdLoading) {
                    isAdLoading = false;
                    isAdShowing = false; // Unlock after timeout
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Log.d("InterstitialAdManager", "Ad loading timeout, proceeding");
                    onProceed.run();
                }
            };
            handler.postDelayed(timeoutRunnable, 5000); // Wait for 5 seconds
        } else {
            // No ad available, proceed immediately
            onProceed.run();
        }
    }
}
