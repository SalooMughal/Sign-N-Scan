package com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.HashMap;
import java.util.Map;

public class AdManager {

    private final Map<String, InterstitialAd> adMap = new HashMap<>();

    public interface AdCallback {
        void onAdDismissed(); // Called when the ad is dismissed
        void onAdFailedToShow(); // Called when the ad fails to show
    }

    public void loadAd(Activity activity, String adUnitId) {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                Log.d("AdManager", "Ad loaded successfully: " + adUnitId);
                adMap.put(adUnitId, interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e("AdManager", "Ad failed to load: " + loadAdError.getMessage());
                adMap.remove(adUnitId); // Remove the failed ad ID
            }
        });
    }

    public void showAdIfAvailable(Activity activity, String adUnitId, AdCallback callback) {
        InterstitialAd ad = adMap.get(adUnitId);
        if (ad != null) {
            ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    adMap.remove(adUnitId); // Clear the ad once shown
                    callback.onAdDismissed();
                    Log.d("AdManager", "Ad dismissed: " + adUnitId);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    callback.onAdFailedToShow();
                    Log.e("AdManager", "Ad failed to show: " + adUnitId);
                }
            });
            ad.show(activity);
        } else {
            callback.onAdFailedToShow();
        }
    }


//    private final Map<String, RewardedAd> rewardedAdMap = new HashMap<>();

//    public interface AdCallbackRewardedAd {
//         void onAdDismissed(); // Called when the ad is dismissed
//         void onAdFailedToShow(); // Called when the ad fails to show
//    }
//
//    public void loadRewardedAd(Activity activity, String adUnitId) {
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        RewardedAd.load(activity, adUnitId, adRequest, new RewardedAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
//                Log.d("AdManager", "Rewarded Ad loaded successfully: " + adUnitId);
//                rewardedAdMap.put(adUnitId, rewardedAd);
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                Log.e("AdManager", "Rewarded Ad failed to load: " + loadAdError.getMessage());
//                rewardedAdMap.remove(adUnitId); // Remove the failed ad ID
//            }
//        });
//    }
//
//    public void showRewardedAdIfAvailable(Activity activity, String adUnitId, AdCallbackRewardedAd adCallbackRewardedAd) {
//        RewardedAd rewardedAd = rewardedAdMap.get(adUnitId);
//        if (rewardedAd != null) {
//            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                @Override
//                public void onAdDismissedFullScreenContent() {
//                    rewardedAdMap.remove(adUnitId); // Clear the ad once shown
//                    adCallbackRewardedAd.onAdDismissed();
//                    Log.d("AdManager", "Rewarded Ad dismissed: " + adUnitId);
//                }
//
//                @Override
//                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
//                    adCallbackRewardedAd.onAdFailedToShow();
//                    Log.e("AdManager", "Rewarded Ad failed to show: " + adUnitId);
//                }
//            });
//
//            rewardedAd.show(activity, rewardItem -> {
//                // Reward the user when they finish the ad
////                adCallbackRewardedAd.onAdDismissed();
//                Log.d("AdManager", "User earned reward: " + rewardItem.getAmount() + " " + rewardItem.getType());
//            });
//        } else {
//            // If no ad is available, proceed to the next activity immediately
//            Log.e("AdManager", "No rewarded ad available: " + adUnitId);
//            adCallbackRewardedAd.onAdFailedToShow();
//        }
//    }

    public interface AdCallbackRewardedAd {
        void onAdDismissed(); // Called when the ad is dismissed
        void onAdFailedToShow(); // Called when the ad fails to show
    }


        private final Map<String, RewardedAd> rewardedAdMap = new HashMap<>();
        private boolean isAdLoading = false; // Flag to track ad loading status

        /**
         * Quietly loads a Rewarded Ad in the background.
         */
        public void loadRewardedAd(Activity activity, String adUnitId) {
            if (isAdLoading) return; // Prevent multiple load calls
            isAdLoading = true;

            AdRequest adRequest = new AdRequest.Builder().build();

            RewardedAd.load(activity, adUnitId, adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    Log.d("AdManager", "Rewarded Ad loaded successfully: " + adUnitId);
                    rewardedAdMap.put(adUnitId, rewardedAd);
                    isAdLoading = false; // Reset loading flag
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e("AdManager", "Rewarded Ad failed to load: " + loadAdError.getMessage());
                    rewardedAdMap.remove(adUnitId); // Remove failed ad entry
                    isAdLoading = false; // Reset loading flag
                }
            });
        }

        /**
         * Shows the Rewarded Ad if available, otherwise loads and waits for it.
         */
        public void showRewardedAdIfAvailable(Activity activity, String adUnitId, ProgressDialog progressDialog, AdCallbackRewardedAd adCallbackRewardedAd) {
            RewardedAd rewardedAd = rewardedAdMap.get(adUnitId);

            if (rewardedAd != null) {
                // If Ad is ready, show it immediately
                showRewardedAd(activity, adUnitId, rewardedAd, adCallbackRewardedAd);
            } else {
                // If ad is still loading, show progress dialog and wait
                if (isAdLoading) {
                    if (progressDialog != null) {
                        progressDialog.setMessage("Loading Ad...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                }

                // Try reloading and wait for the ad
                loadRewardedAd(activity, adUnitId);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    RewardedAd delayedAd = rewardedAdMap.get(adUnitId);
                    if (delayedAd != null) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        showRewardedAd(activity, adUnitId, delayedAd, adCallbackRewardedAd);
                    } else {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Log.e("AdManager", "Ad failed to load after delay: " + adUnitId);
                        adCallbackRewardedAd.onAdFailedToShow();
                    }
                }, 4000); // Wait for 3 seconds to check again
            }
        }

        /**
         * Handles Rewarded Ad display logic.
         */
        private void showRewardedAd(Activity activity, String adUnitId, RewardedAd rewardedAd, AdCallbackRewardedAd adCallbackRewardedAd) {
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    rewardedAdMap.remove(adUnitId); // Clear the ad after being shown
                    adCallbackRewardedAd.onAdDismissed();
                    Log.d("AdManager", "Rewarded Ad dismissed: " + adUnitId);
                    loadRewardedAd(activity, adUnitId); // Preload next ad
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    rewardedAdMap.remove(adUnitId); // Remove failed ad
                    adCallbackRewardedAd.onAdFailedToShow();
                    Log.e("AdManager", "Rewarded Ad failed to show: " + adUnitId);
                    loadRewardedAd(activity, adUnitId); // Retry loading
                }
            });

            rewardedAd.show(activity, rewardItem -> {
                Log.d("AdManager", "User earned reward: " + rewardItem.getAmount() + " " + rewardItem.getType());
            });

            rewardedAdMap.remove(adUnitId); // Remove ad once shown
        }



}
