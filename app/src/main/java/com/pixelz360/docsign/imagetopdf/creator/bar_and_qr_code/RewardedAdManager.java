package com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.HashMap;

public class RewardedAdManager {

    private final Activity activity;
    private final HashMap<String, RewardedAd> rewardedAds = new HashMap<>();
    private final HashMap<String, Boolean> isAdLoading = new HashMap<>();

    public RewardedAdManager(Activity activity) {
        this.activity = activity;
    }

    public void loadAd(String adUnitId) {
        if (isAdLoading.getOrDefault(adUnitId, false)) return; // Prevent duplicate requests

        AdRequest adRequest = new AdRequest.Builder().build();
        isAdLoading.put(adUnitId, true);

        RewardedAd.load(activity, adUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAds.put(adUnitId, ad);
                isAdLoading.put(adUnitId, false);
                Log.d("RewardedAdManager", "Ad loaded successfully for adUnitId: " + adUnitId);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                rewardedAds.remove(adUnitId);
                isAdLoading.put(adUnitId, false);
                Log.e("RewardedAdManager", "Ad failed to load for adUnitId: " + adUnitId + ". Error: " + adError.getMessage());
            }
        });
    }

    public void showAd(String adUnitId, Runnable onRewardEarned, Runnable onAdNotAvailable) {
        RewardedAd rewardedAd = rewardedAds.get(adUnitId);

        if (rewardedAd != null) {
            rewardedAd.show(activity, rewardItem -> {
                Log.d("RewardedAdManager", "User earned reward from adUnitId: " + adUnitId);
                rewardedAds.remove(adUnitId); // Invalidate current ad
                loadAd(adUnitId); // Reload for next use
                onRewardEarned.run();
            });

            rewardedAd.setFullScreenContentCallback(new com.google.android.gms.ads.FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d("RewardedAdManager", "Ad dismissed for adUnitId: " + adUnitId);
                    rewardedAds.remove(adUnitId);
                    loadAd(adUnitId);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    Log.e("RewardedAdManager", "Ad failed to show for adUnitId: " + adUnitId + ". Error: " + adError.getMessage());
                    rewardedAds.remove(adUnitId);
                    loadAd(adUnitId);
                    onAdNotAvailable.run();
                }
            });
        } else {
            Log.d("RewardedAdManager", "No ad available for adUnitId: " + adUnitId);
            onAdNotAvailable.run();
        }
    }
}

