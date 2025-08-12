package com.pixelz360.docsign.imagetopdf.creator.onboarding;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.pixelz360.docsign.imagetopdf.creator.HomeActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityOnboardingBinding;

public class OnboardingActivity extends AppCompatActivity {
    private LinearLayout dotIndicatorLayout;

    private int[] layouts = {
            R.layout.onboarding_screen_1,
            R.layout.onboarding_screen_2,
            R.layout.onboarding_screen_3,
            R.layout.onboarding_screen_4,
            R.layout.onboarding_screen_5
    };

    ActivityOnboardingBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(binding.getRoot());

        // Change the status bar color for this activity
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // Ensure system bar backgrounds can be modified
        window.setStatusBarColor(getResources().getColor(R.color.white)); // Set your custom color


        // Initialize views
        dotIndicatorLayout = findViewById(R.id.dotIndicatorLayout);

        // Set up the adapter
        OnboardingAdapter adapter = new OnboardingAdapter(layouts);
        binding.viewPager.setAdapter(adapter);

        // Initialize the dot indicators
        setupIndicators();
        setCurrentIndicator(0);

        // Handle page change
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);

                // Update the button text
                if (position == layouts.length - 1) {
                    binding.nextButton.setText("Continue");
                    binding.skipAllBtn.setText("");
                    Log.e("checkppositon","Continue  "+ String.valueOf(position));

                     binding.featureImage.setImageResource(R.drawable.convert_to_scan);
//                    binding.titleText.setText("Quick JPEG to PDF Converter");
                    binding.titleText.setText("Convert & Scan Images into PDF");
                    binding.descriptionText.setText("Your Complete PDF Toolkit");
//                    binding.descriptionText.setText("Turn Your JPEGs into High-Quality PDFs");





                    SpannableString oneText = new SpannableString("Convert ");
                    oneText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, oneText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.titleText.setText(oneText);

                    SpannableString SecondText = new SpannableString("& ");
                    SecondText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, SecondText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.titleText.append(SecondText);

                    SpannableString ThirdText = new SpannableString("Scan\n");
                    ThirdText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, ThirdText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.titleText.append(ThirdText);

//                    SpannableString ForthText = new SpannableString("PDF\n");
//                    ForthText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, ForthText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    binding.titleText.append(ForthText);

                    SpannableString FiveText = new SpannableString("Images");
                    FiveText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, FiveText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.titleText.append(FiveText);
                    SpannableString SixText = new SpannableString("into");
                    SixText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, SixText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.titleText.append(SixText);
                    SpannableString SevenText = new SpannableString("PDF");
                    SevenText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, SevenText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.titleText.append(SevenText);



                    SpannableString oneDisText = new SpannableString("Your ");
                    oneDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, oneDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.descriptionText.setText(oneDisText);

                    SpannableString SecondDisText = new SpannableString("Complete ");
                    SecondDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, SecondDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.descriptionText.append(SecondDisText);

                    SpannableString ThirdDisText = new SpannableString("PDF");
                    ThirdDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, ThirdDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.descriptionText.append(ThirdDisText);

                    SpannableString ForthDisText = new SpannableString("Toolkit ");
                    ForthDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one    , null)), 0, ForthDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.descriptionText.append(ForthDisText);




                } else {
                    binding.nextButton.setText("Next");
                    binding.skipAllBtn.setText(getResources().getString(R.string.skip_all));
                    Log.e("checkppositon", "Next  "+String.valueOf(position));

                    switch (position) {
                        case 0:
                            binding.featureImage.setImageResource(R.drawable.onboarding_screen_pdf_icon);
//                            binding.titleText.setText("All-in-one PDF Solutions");
//                            binding.descriptionText.setText("Your Complete PDF Toolkit");


                            SpannableString oneText = new SpannableString("All-in-one ");
                            oneText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, oneText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.setText(oneText);

                            SpannableString SecondText = new SpannableString("PDF\n");
                            SecondText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, SecondText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.append(SecondText);

                            SpannableString ThirdText = new SpannableString("Solutions");
                            ThirdText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, ThirdText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.append(ThirdText);

                            SpannableString oneDisText = new SpannableString("Your Complete ");
                            oneDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, oneDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.descriptionText.setText(oneDisText);

                            SpannableString SecondDisText = new SpannableString("PDF ");
                            SecondDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, SecondDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.descriptionText.append(SecondDisText);

                            SpannableString ThirdDisText = new SpannableString("Toolkit");
                            ThirdDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, ThirdDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.descriptionText.append(ThirdDisText);


//                            appendColoredText(binding.descriptionText, "Your Complete ", R.color.on_boarding_title_color_one);
//                            appendColoredText(binding.descriptionText, "PDF ", R.color.on_boarding_title_color_tow);
//                            appendColoredText(binding.descriptionText, "Toolkit", R.color.on_boarding_title_color_one);
//

                            break;
                        case 1:
                            binding.featureImage.setImageResource(R.drawable.onboarding_screen_signature_icon);
//                            binding.titleText.setText("Fast and Secure eSignatures");
//                            binding.descriptionText.setText("Quick and Secure Doc Signing");



                            SpannableString oneeSignaturesText = new SpannableString("Fast and Secure\n");
                            oneeSignaturesText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, oneeSignaturesText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.setText(oneeSignaturesText);

                            SpannableString SecondeSignaturesText = new SpannableString("eSignatures");
                            SecondeSignaturesText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, SecondeSignaturesText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.append(SecondeSignaturesText);


                            SpannableString oneeSignaturesDisText = new SpannableString("Quick and Secure ");
                            oneeSignaturesDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, oneeSignaturesDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.descriptionText.setText(oneeSignaturesDisText);

                            SpannableString SecondeSignaturesDisText = new SpannableString("Doc ");
                            SecondeSignaturesDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, SecondeSignaturesDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.descriptionText.append(SecondeSignaturesDisText);

                            SpannableString ThirdeSignaturesDisText = new SpannableString("Signing");
                            ThirdeSignaturesDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, ThirdeSignaturesDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.descriptionText.append(ThirdeSignaturesDisText);


                            break;
                        case 2:
                            binding.featureImage.setImageResource(R.drawable.onboarding_screen_optimize_icon);
//                            binding.titleText.setText("All-in-One PDF Optimization Tools");
//                            binding.descriptionText.setText("Manage PDFs: Split, Merge, Compress");


                            SpannableString oneeOptimizationText = new SpannableString("All-in-One ");
                            oneeOptimizationText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, oneeOptimizationText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.setText(oneeOptimizationText);

                            SpannableString SecondeOptimizationText = new SpannableString("PDF\n");
                            SecondeOptimizationText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, SecondeOptimizationText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.append(SecondeOptimizationText);

                            SpannableString ThridOptimizationText = new SpannableString("Optimization Tools");
                            ThridOptimizationText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, ThridOptimizationText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.append(ThridOptimizationText);


                            SpannableString oneeOptimizationDisText = new SpannableString("Manage ");
                            oneeOptimizationDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, oneeOptimizationDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.descriptionText.setText(oneeOptimizationDisText);

                            SpannableString SecondeOptimizationDisText = new SpannableString("PDFs:  ");
                            SecondeOptimizationDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, SecondeOptimizationDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.descriptionText.append(SecondeOptimizationDisText);

                            SpannableString ThirdeOptimizationDisText = new SpannableString("Split, Merge, Compress");
                            ThirdeOptimizationDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, ThirdeOptimizationDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.descriptionText.append(ThirdeOptimizationDisText);



                            break;

                        case 3:
                            binding.featureImage.setImageResource(R.drawable.document_conversion);
//                    binding.titleText.setText("Quick JPEG to PDF Converter");
                            binding.titleText.setText("Different Modes of Editing");
                            binding.descriptionText.setText("Enhance, and Perfect Your Images Easily");
//                    binding.descriptionText.setText("Turn Your JPEGs into High-Quality PDFs");





                            SpannableString oneDifferentText = new SpannableString("Different ");
                            oneDifferentText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, oneDifferentText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.setText(oneDifferentText);

                            SpannableString SecondDifferentText = new SpannableString("Modes ");
                            SecondDifferentText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, SecondDifferentText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.append(SecondDifferentText);

                            SpannableString ThirdDifferentText = new SpannableString("of\n");
                            ThirdDifferentText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, ThirdDifferentText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.append(ThirdDifferentText);

//                    SpannableString ForthText = new SpannableString("PDF\n");
//                    ForthText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, ForthText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    binding.titleText.append(ForthText);

                            SpannableString FiveDifferentText = new SpannableString("Editing");
                            FiveDifferentText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, FiveDifferentText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.titleText.append(FiveDifferentText);

//                    SpannableString oneDisText = new SpannableString("Turn Your ");
//                    oneDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, oneDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    binding.descriptionText.setText(oneDisText);
//
//                    SpannableString SecondDisText = new SpannableString("JPEGs ");
//                    SecondDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, SecondDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    binding.descriptionText.append(SecondDisText);
//
//                    SpannableString ThirdDisText = new SpannableString("into High-Quality");
//                    ThirdDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_one, null)), 0, ThirdDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    binding.descriptionText.append(ThirdDisText);
//
//                    SpannableString ForthDisText = new SpannableString("PDFs ");
//                    ForthDisText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.on_boarding_title_color_tow, null)), 0, ForthDisText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    binding.descriptionText.append(ForthDisText);



                            break;

                    }

                }
            }
        });


        binding.skipAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMainActivity();
            }
        });

        // Handle "Next/Continue" button click
        binding.nextButton.setOnClickListener(v -> {
            int currentPosition = binding.viewPager.getCurrentItem();
            if (currentPosition < layouts.length - 1) {
                binding.viewPager.setCurrentItem(currentPosition + 1);
            } else {

                if (!isInternetAvailable()) {
                    navigateToMainActivity();
                } else {


                    navigateToMainActivity();






                    // Show the ad or proceed if timeout
//                    adManager.showAdOrProceed(binding.progressBar, new Runnable() {
//                        @Override
//                        public void run() {
//                            goToNextActivity();
//                        }
//                    });



                }




            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void appendColoredText(TextView textView, String text, int colorResId) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(colorResId, null)),
                0,
                text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        textView.append(spannableString);
    }

    private CharSequence getColoredString(String mString, int colorId) {
        Spannable spannable = new SpannableString(mString);
        spannable.setSpan(new ForegroundColorSpan(colorId), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Log.d("checkppositon",spannable.toString());
        return spannable;
    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[layouts.length];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(3, 0, 3, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageResource(R.drawable.indicator_inactive);
            indicators[i].setLayoutParams(params);
            dotIndicatorLayout.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = dotIndicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) dotIndicatorLayout.getChildAt(i);
            if (i == index) {
                imageView.setImageResource(R.drawable.indicator_active);
            } else {
                imageView.setImageResource(R.drawable.indicator_inactive);
            }
        }
    }

    private void navigateToMainActivity() {
        // Navigate to the main activity
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
