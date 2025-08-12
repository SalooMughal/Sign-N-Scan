package com.pixelz360.docsign.imagetopdf.creator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivitySplasBinding
import com.pixelz360.docsign.imagetopdf.creator.language.LanguageHelper

class UsageActivity : AppCompatActivity(){

    lateinit var binding: ActivitySplasBinding
    var handler = Handler()

    lateinit var imageView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplasBinding.inflate(layoutInflater)
        setContentView(binding.root)


        imageView = findViewById(R.id.icon)

        // Log a predefined event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("activity_name", "RoomListFragment")
        analytics.logEvent("activity_created", bundle)

        // Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "RoomListFragment")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen")
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

    }

    private fun GoNextActivity() {
        handler.postDelayed({

            if (isFirstTime()) {
                val intent = Intent(this@UsageActivity, GetStartedActivity::class.java)
                startActivity(intent)
                finish()

            }else{
                val intent = Intent(this@UsageActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }

        },3000)
    }

    private fun isFirstTime(): Boolean {
        val preferences = getPreferences(MODE_PRIVATE)
        val ranBefore = preferences.getBoolean("RanBefore", false)
        if (!ranBefore) {
            // first time
            val editor = preferences.edit()
            editor.putBoolean("RanBefore", true)
            editor.apply()
        }
        return !ranBefore
    }
//    private void createAppOpenAd() {
//        // Ensure CAS manager is available
//        if (CAS.getManager() == null) {
//            startNextActivity();
//            Log.d("CAS111111", "ad no load");
//
//            return;
//        }
//;
//        // Create App Open Ad
//        CASAppOpen appOpenAd = CASAppOpen.create(CAS.getManager());
//
//        // Handle ad events
//        appOpenAd.setContentCallback(new AdCallback() {
//            @Override
//            public void onShown(@NotNull AdStatusHandler adStatusHandler) {
//                Log.d("CAS_AD", "App Open Ad shown");
//            }
//
//            @Override
//            public void onShowFailed(@NotNull String message) {
//                Log.d("CAS_AD", "App Open Ad show failed: " + message);
//                isVisibleAppOpenAd = false;
//                startNextActivity();
//            }
//
//            @Override
//            public void onClosed() {
//                Log.d("CAS_AD", "App Open Ad closed");
//                isVisibleAppOpenAd = false;
//                startNextActivity();
//            }
//
//            @Override
//            public void onClicked() {
//                // Handle ad clicked
//            }
//
//            @Override
//            public void onComplete() {
//                // Handle ad complete
//            }
//        });
//
//        // Load Ad
//        appOpenAd.loadAd(this, new LoadAdCallback() {
//            @Override
//            public void onAdLoaded() {
//                Log.d("CAS_AD", "App Open Ad loaded");
//                if (isLoadingAppResources) {
//                    isVisibleAppOpenAd = true;
//                    appOpenAd.show(FirstActivity.this);
//                }
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NotNull AdError adError) {
//                Log.e("CAS_AD", "App Open Ad failed to load: " + adError.getMessage());
//                startNextActivity();
//            }
//        });
//    }


}
