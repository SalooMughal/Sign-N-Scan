package com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing;

import static com.pixelz360.docsign.imagetopdf.creator.pdf_utils.PDFUtils.displayMonthlyPrice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.pixelz360.docsign.imagetopdf.creator.HomeActivity;
import com.pixelz360.docsign.imagetopdf.creator.PrivacyPolicyActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityAddFreeBinding;
import com.pixelz360.docsign.imagetopdf.creator.pdf_tools_billing.AddFreeForPdfToolsActivity;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils;

public class AddFreeActivity extends AppCompatActivity {

    ActivityAddFreeBinding binding;

    PrefUtilForAppAdsFree prefUtilForAppAdsFree;





    int isPurchaseApp = 11;
    boolean isPurchaseAppIsChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityAddFreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Get color from resources
        int statusBarColor = ContextCompat.getColor(AddFreeActivity.this, R.color.white);
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor,AddFreeActivity.this);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white)); // here is your color


        prefUtilForAppAdsFree = new PrefUtilForAppAdsFree(this);

        Log.d("checkbilling","Checked "+ PrefUtilForAppAdsFree.isPremium(AddFreeActivity.this));
        Log.d("checkbilling","Checked getPremiumItemPrice "+ PrefUtilForAppAdsFree.Companion.getPremiumItemPrice(this,0));

        // Initialize BillingManagerSubs and pass the TextViews for price display
//        billingManagerSubs = new BillingManagerSubs(this, binding.weeklyTextPrice, binding.monthlyTextPrice, binding.yearlyTextPrice,binding.adsFreeTextPrice);
//        billingManagerSubs.startConnection();




        addForFree();


        // Check if ads should be removed
        if (shouldRemoveAds()) {
            // Ads removed, hide the adView
            Log.d("checkAd","Add Is Removed");
            binding.startedBtn.setEnabled(false);

        } else {
            // Show ads
            Log.d("checkAd","Add Is not Removed");

            binding.startedBtn.setEnabled(true);




        }



        binding.monthlyTextPrice.setText(prefUtilForAppAdsFree.getString("key1"));
        binding.weeklyTextPrice.setText(prefUtilForAppAdsFree.getString("key2"));
        binding.adsFreeTextPrice.setText(prefUtilForAppAdsFree.getString("ads_free_life_time_price"));
        binding.yearlyTextPrice.setText(prefUtilForAppAdsFree.getString("key3"));

        Log.d("checkannual",prefUtilForAppAdsFree.getString("key3"));

        Log.d("checkannual","checkannual Free Add "+displayMonthlyPrice(prefUtilForAppAdsFree.getString("key3")));

        binding.yearlyPriceDividePerMonth.setText(displayMonthlyPrice(prefUtilForAppAdsFree.getString("key3")));

//        displayMonthlyPrice(prefUtilForAppAdsFree.getString("key3"));


        binding.weeklyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.weeklyBtn.setBackground(getResources().getDrawable(R.drawable.plan_selected_bg));
                binding.monthlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
                binding.yearlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
                binding.adsFreeBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));



                binding.weeklyTextTitle.setTextColor(getColor(R.color.black));
                binding.monthlyTextTitle.setTextColor(getColor(R.color.black));
                binding.yearlyTextTitle.setTextColor(getColor(R.color.black));
                binding.adsFreeTextTitle.setTextColor(getColor(R.color.black));

                binding.weeklyTextPrice.setTextColor(getColor(R.color.black));
                binding.monthlyTextPrice.setTextColor(getColor(R.color.black));
                binding.yearlyTextPrice.setTextColor(getColor(R.color.black));
                binding.adsFreeTextPrice.setTextColor(getColor(R.color.black));




//                billingManager.initiatePurchase("weekly_subscription", true);

                isPurchaseApp = 2;
                isPurchaseAppIsChecked = true;

                binding.everyProductAccording.setText(prefUtilForAppAdsFree.getString("key2")+" (Billed every 7 days)");



            }
        });

        binding.monthlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.weeklyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
                binding.monthlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_selected_bg));
                binding.yearlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
                binding.adsFreeBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));



                binding.weeklyTextTitle.setTextColor(getColor(R.color.black));
                binding.monthlyTextTitle.setTextColor(getColor(R.color.black));
                binding.yearlyTextTitle.setTextColor(getColor(R.color.black));
                binding.adsFreeTextTitle.setTextColor(getColor(R.color.black));

                binding.weeklyTextPrice.setTextColor(getColor(R.color.black));
                binding.monthlyTextPrice.setTextColor(getColor(R.color.black));
                binding.yearlyTextPrice.setTextColor(getColor(R.color.black));
                binding.adsFreeTextPrice.setTextColor(getColor(R.color.black));


//                billingManager.initiatePurchase("monthly_subscription", true);

                isPurchaseApp = 1;
//                isPurchaseAppIsChecked = true;

                binding.everyProductAccording.setText(prefUtilForAppAdsFree.getString("key1")+" (Billed every month)");


            }
        });

        binding.yearlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.weeklyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
                binding.monthlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
                binding.yearlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_selected_bg));
                binding.adsFreeBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));



                binding.weeklyTextTitle.setTextColor(getColor(R.color.black));
                binding.monthlyTextTitle.setTextColor(getColor(R.color.black));
                binding.yearlyTextTitle.setTextColor(getColor(R.color.black));
                binding.adsFreeTextTitle.setTextColor(getColor(R.color.black));

                binding.weeklyTextPrice.setTextColor(getColor(R.color.black));
                binding.monthlyTextPrice.setTextColor(getColor(R.color.black));
                binding.yearlyTextPrice.setTextColor(getColor(R.color.black));
                binding.adsFreeTextPrice.setTextColor(getColor(R.color.black));


                isPurchaseApp = 3;

//                billingManager.initiatePurchase("permanent_unlock", true);


//                isPurchaseApp = "yearly_plan";
//                isPurchaseAppIsChecked = true;

                binding.everyProductAccording.setText(prefUtilForAppAdsFree.getString("key1")+" (Billed annually, equivalent to "+displayMonthlyPrice(prefUtilForAppAdsFree.getString("key3")+" per month)"));




            }
        });
        binding.adsFreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addForFree();
//                isPurchaseAppIsChecked = true;


//                billingManager.initiatePurchase("add_free_app", false);
            }
        });




        binding.startedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPurchaseApp == 11){
                    Log.d("checkbilling","not value select");
                }else {

//                    billingManagerSubs.initiatePurchase(isPurchaseApp, isPurchaseAppIsChecked);
//                    billingManager.initiatePurchase(isPurchaseApp, isPurchaseAppIsChecked);
                    Log.d("checkbilling"," select"+isPurchaseApp);

//                    BillingIASForAppAdsFree.launchPurchaseFlow(isPurchaseApp, AddFreeActivity.this);
//                    BillingIASForAppAdsFreeInApp.launchPurchaseFlow( AddFreeActivity.this);


                    if (isPurchaseApp==0){
                        BillingIASForAppAdsFreeInApp.launchPurchaseFlow( AddFreeActivity.this);
                        Log.d("checkbilling232","BillingIASForAppAdsFreeInApp");
                    }else {
                        BillingIASForAppAdsFreeSubs.launchPurchaseFlow(isPurchaseApp, AddFreeActivity.this);

                        PrefUtilForAppAdsFree.setCheckActivityPremium(AddFreeActivity.this, false);


                        Log.d("checkbilling232"," BillingIASForAppAdsFreeSubs");
                    }



                }



            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AddFreeActivity.this, MainActivity.class);
                Intent intent = new Intent(AddFreeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

            binding.privacyPolicyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddFreeActivity.this, PrivacyPolicyActivity.class);
                    startActivity(intent);
                }
            });

        binding.restoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlayStoreSubscriptions();

            }
        });





    }

    private void addForFree() {
        binding.weeklyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
        binding.monthlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
        binding.yearlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
        binding.adsFreeBtn.setBackground(getResources().getDrawable(R.drawable.plan_selected_bg));



        binding.weeklyTextTitle.setTextColor(getColor(R.color.black));
        binding.monthlyTextTitle.setTextColor(getColor(R.color.black));
        binding.yearlyTextTitle.setTextColor(getColor(R.color.black));
        binding.adsFreeTextTitle.setTextColor(getColor(R.color.black));

        binding.weeklyTextPrice.setTextColor(getColor(R.color.black));
        binding.monthlyTextPrice.setTextColor(getColor(R.color.black));
        binding.yearlyTextPrice.setTextColor(getColor(R.color.black));
        binding.adsFreeTextPrice.setTextColor(getColor(R.color.black));

        isPurchaseApp = 0;

        binding.everyProductAccording.setText(prefUtilForAppAdsFree.getString("ads_free_life_time_price")+" (One-time payment, no renewal)");


    }

    private void openPlayStoreSubscriptions() {
        Intent intent = new  Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/account/subscriptions"));
        intent.setPackage("com.android.vending"); // Ensures the Play Store app handles it
        startActivity(intent);


    }





    private boolean shouldRemoveAds() {
//        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
//        boolean isAdsRemoved = prefs.getBoolean("ads_removed", false);
//        String subscriptionType = prefs.getString("subscription_type", "");
//        long purchaseTime = prefs.getLong("subscription_end_time", 0);
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
//
//        // For weekly and monthly, calculate if the subscription has expired
//        long currentTime = System.currentTimeMillis();
//        if (subscriptionType.equals("weekly")) {
//            // Check if the week has passed since purchase
//            if ((currentTime - purchaseTime) < WEEK_IN_MILLIS) {
//                return true; // Weekly subscription is still valid
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
//        } else if (subscriptionType.equals("yearly")) {
//            // Check if the month has passed since purchase
//            if ((currentTime - purchaseTime) < YEAR_IN_MILLIS) {
//                return true; // Monthly subscription is still valid
//            } else {
//                // Subscription has expired, show ads again
//                prefs.edit().putBoolean("ads_removed", false).apply();
//                return false;
//            }
//        }




        // Default case: Ads should be shown if no valid subscription is found
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Verify subscription status when the activity resumes
//        BillingIAS.verifySubPurchase(AddFreeActivity.this); {
//
//        }
        PrefUtilForAppAdsFree.isPremium(AddFreeActivity.this);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(AddFreeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
