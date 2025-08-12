package com.pixelz360.docsign.imagetopdf.creator.pdf_tools_billing;

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
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.AddFreeActivity;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.BillingIASForPdfTools;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForPdfTools;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityAddFreeForPdfToolsBinding;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils;

public class AddFreeForPdfToolsActivity extends AppCompatActivity {

    ActivityAddFreeForPdfToolsBinding binding;

    PrefUtilForPdfTools prefUtilForPdfTools;





    int isPurchaseApp = 11;
    boolean isPurchaseAppIsChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFreeForPdfToolsBinding.inflate(getLayoutInflater());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        setContentView(binding.getRoot());


        // Get color from resources
        int statusBarColor = ContextCompat.getColor(AddFreeForPdfToolsActivity.this, R.color.white);
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor,AddFreeForPdfToolsActivity.this);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white)); // here is your color


        prefUtilForPdfTools = new PrefUtilForPdfTools(this);

        yearllyAdsLayout();

        Log.d("checkbilling","Checked "+ PrefUtilForPdfTools.isPremium(AddFreeForPdfToolsActivity.this));

        // Initialize BillingManagerSubs and pass the TextViews for price display
//        billingManagerSubs = new BillingManagerSubs(this, binding.weeklyTextPrice, binding.monthlyTextPrice, binding.yearlyTextPrice,binding.adsFreeTextPrice);
//        billingManagerSubs.startConnection();


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



        binding.monthlyTextPrice.setText(prefUtilForPdfTools.getString("key_new1"));
        binding.weeklyTextPrice.setText(prefUtilForPdfTools.getString("key_new2"));
        binding.yearlyTextPrice.setText(prefUtilForPdfTools.getString("key_new0"));

        Log.d("checkAd","price. 1 "+prefUtilForPdfTools.getString("key_new1"));
        Log.d("checkAd","price 2 "+prefUtilForPdfTools.getString("key_new2"));
        Log.d("checkAd","price. 3 "+prefUtilForPdfTools.getString("key_new3"));
        Log.d("checkAd","price. 4 "+prefUtilForPdfTools.getString("key_new0"));


        Log.d("checkannual","checkannual Free Tools "+displayMonthlyPrice(prefUtilForPdfTools.getString("key_new0")));

        binding.yearlyPriceDividePerMonth.setText(displayMonthlyPrice(prefUtilForPdfTools.getString("key_new0")));


        binding.weeklyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.weeklyBtn.setBackground(getResources().getDrawable(R.drawable.plan_new_selected_bg));
                binding.monthlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
                binding.yearlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));



                binding.weeklyTextTitle.setTextColor(getColor(R.color.black));
                binding.monthlyTextTitle.setTextColor(getColor(R.color.black));
                binding.yearlyTextTitle.setTextColor(getColor(R.color.black));

                binding.weeklyTextPrice.setTextColor(getColor(R.color.black));
                binding.monthlyTextPrice.setTextColor(getColor(R.color.black));
                binding.yearlyTextPrice.setTextColor(getColor(R.color.black));




//                billingManager.initiatePurchase("weekly_subscription", true);

                isPurchaseApp = 2;
                isPurchaseAppIsChecked = true;


                binding.everyProductAccording.setText(prefUtilForPdfTools.getString("key_new2")+" (Billed every 7 days)");



            }
        });

        binding.monthlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.weeklyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
                binding.monthlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_new_selected_bg));
                binding.yearlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));



                binding.weeklyTextTitle.setTextColor(getColor(R.color.black));
                binding.monthlyTextTitle.setTextColor(getColor(R.color.black));
                binding.yearlyTextTitle.setTextColor(getColor(R.color.black));

                binding.weeklyTextPrice.setTextColor(getColor(R.color.black));
                binding.monthlyTextPrice.setTextColor(getColor(R.color.black));
                binding.yearlyTextPrice.setTextColor(getColor(R.color.black));


//                billingManager.initiatePurchase("monthly_subscription", true);

                isPurchaseApp = 1;
//                isPurchaseAppIsChecked = true;


                binding.everyProductAccording.setText(prefUtilForPdfTools.getString("key_new1")+" (Billed every month)");



            }
        });

        binding.yearlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              yearllyAdsLayout();

//                billingManager.initiatePurchase("permanent_unlock", true);


//                isPurchaseApp = "yearly_plan";
//                isPurchaseAppIsChecked = true;





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
                    Log.d("checkbilling"," Pdf Tools Main Button select"+isPurchaseApp);

                    BillingIASForPdfTools.launchPurchaseFlow(isPurchaseApp, AddFreeForPdfToolsActivity.this);

                    PrefUtilForAppAdsFree.setCheckActivityPremium(AddFreeForPdfToolsActivity.this, true);


                }



            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AddFreeActivity.this, MainActivity.class);
                Intent intent = new Intent(AddFreeForPdfToolsActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

            binding.privacyPolicyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddFreeForPdfToolsActivity.this, PrivacyPolicyActivity.class);
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

    private void yearllyAdsLayout() {

        binding.weeklyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
        binding.monthlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_un_selected_bg));
        binding.yearlyBtn.setBackground(getResources().getDrawable(R.drawable.plan_new_selected_bg));



        binding.weeklyTextTitle.setTextColor(getColor(R.color.black));
        binding.monthlyTextTitle.setTextColor(getColor(R.color.black));
        binding.yearlyTextTitle.setTextColor(getColor(R.color.black));

        binding.weeklyTextPrice.setTextColor(getColor(R.color.black));
        binding.monthlyTextPrice.setTextColor(getColor(R.color.black));
        binding.yearlyTextPrice.setTextColor(getColor(R.color.black));


        isPurchaseApp = 0;

        binding.everyProductAccording.setText("Billed once per year, equivalent to "+displayMonthlyPrice(prefUtilForPdfTools.getString("key_new0")));


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
        PrefUtilForPdfTools.isPremium(AddFreeForPdfToolsActivity.this);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(AddFreeForPdfToolsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
