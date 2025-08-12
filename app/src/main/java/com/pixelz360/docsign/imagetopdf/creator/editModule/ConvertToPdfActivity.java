package com.pixelz360.docsign.imagetopdf.creator.editModule;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixelz360.docsign.imagetopdf.creator.Constant;
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity;
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.AddFreeActivity;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityConvertToPdfBinding;
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper;
import com.pixelz360.docsign.imagetopdf.creator.models.ModelImage;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConvertToPdfActivity extends AppCompatActivity {

    ActivityConvertToPdfBinding binding;
//    private ProgressDialog progressDialog;
    LayoutInflater inflater;
    AlertDialog.Builder builder;
//    AlertDialog progressDialog;
//    private AppDatabase db;
     PdfFileViewModel pdfFileViewModel;
    private ArrayList<ModelImage> allImageArrayList;

    List<Uri> uriList;
    String mainFileName;
    String modulename;

//    private InterstitialAd mInterstitialAd = null;
//    private boolean isAdLoading = false;
//    private final Handler handler = new Handler(Looper.getMainLooper());
//    private Runnable timeoutRunnable;

    final String[] password = {null};
    final String[] pageSize = {"Fit"};
    final String[] pageMargin = {"No Margin"};
    final String[] pageCompression = {"Original"};
    final String[] renameFileName = new String[1];


//    InterstitialAdManager_Java adManager;

    private AdManager adManager;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConvertToPdfBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(binding.getRoot());

        MobileAds.initialize(this); {}



        // Get color from resources
        int statusBarColor = ContextCompat.getColor(ConvertToPdfActivity.this, R.color.white);
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor,ConvertToPdfActivity.this);


//        loadInterstitialAd();
//
//        adManager = new  InterstitialAdManager_Java(ConvertToPdfActivity.this, getString(R.string.language_intertial_ad));
//
//        // Load the ad once in onCreate
//        adManager.loadAd();


        adManager = new AdManager();

        adManager.loadRewardedAd(ConvertToPdfActivity.this, getString(R.string.when_select_user_5_images_or_above_select_button_reworded_ad));


        pdfFileViewModel = new ViewModelProvider(this).get(PdfFileViewModel.class);


//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Please wait");
//        progressDialog.setCanceledOnTouchOutside(false);


        inflater = LayoutInflater.from(this);
        View customLayout = inflater.inflate(R.layout.custom_progress_dialog, null);
// Initialize the ProgressDialog
         builder = new AlertDialog.Builder(this);
        builder.setView(customLayout);
//         progressDialog = builder.create();
//        progressDialog.setCanceledOnTouchOutside(false);

//        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "pdf_database").build();

        uriList = getIntent().getParcelableArrayListExtra("images");
        mainFileName = getIntent().getStringExtra("renameFileName");
        modulename = getIntent().getStringExtra("modulename");

        Log.d("checkimage"," 3   "+mainFileName);
        Log.d("checkimage", " ConvertToPdfActivity   " + modulename);




        // Log a predefined event
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("activity_name", "ConvertToPdfActivity");
        analytics.logEvent("activity_created", bundle);
// Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "ConvertToPdfActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                discardDialog();
                onBackPressed();
            }
        });

        //name with extension of the image
//        long timestamp = System.currentTimeMillis();
//        String mainFileName = "PDF " + timestamp;


        renameFileName[0] = mainFileName+".pdf";
//        renameFileName[0] = Arrays.toString(renameFileName);

        binding.pageSizeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Dialog dialog = new Dialog(ConvertToPdfActivity.this, R.style.FileSortingDialogStyle);
                dialog.setContentView(R.layout.page_size_dailog);


                Window window = dialog.getWindow();
                if (window != null) {
                    window.setGravity(Gravity.BOTTOM);
                    window.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }

                RadioButton fitRadioBtn = dialog.findViewById(R.id.fitRadioBtn);
                RadioButton a4RadioBtn = dialog.findViewById(R.id.a4RadioBtn);
                RadioButton usRadioBtn = dialog.findViewById(R.id.usRadioBtn);

                fitRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                        if (isChecked){

                            a4RadioBtn.setChecked(false);
                            usRadioBtn.setChecked(false);

                            pageSize[0] = "Fit";
                        }
                    }
                });

                a4RadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                        if (isChecked){

                            fitRadioBtn.setChecked(false);
                            usRadioBtn.setChecked(false);

                            pageSize[0] = "A4";
                        }
                    }
                });


                usRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                        if (isChecked){

                            a4RadioBtn.setChecked(false);
                            fitRadioBtn.setChecked(false);

                            pageSize[0] = "US Letter";
                        }
                    }
                });

                dialog.show();

            }
        });

        binding.checkMargin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (binding.checkMargin.isChecked()){
//
                    Log.d("checkopen","open dailog");

                    Dialog dialog = new Dialog(ConvertToPdfActivity.this, R.style.FileSortingDialogStyle);
                    dialog.setContentView(R.layout.margin_setting_dailog);


                    Window window = dialog.getWindow();
                    if (window != null) {
                        window.setGravity(Gravity.BOTTOM);
                        window.setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                    }

                    RadioButton noRadioBtn = dialog.findViewById(R.id.noRadioBtn);
                    RadioButton smallMarginRadioBtn = dialog.findViewById(R.id.smallMarginRadioBtn);
                    RadioButton bigMarginRadioBtn = dialog.findViewById(R.id.bigMarginRadioBtn);

                    noRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            if (isChecked){

                                smallMarginRadioBtn.setChecked(false);
                                bigMarginRadioBtn.setChecked(false);

                                pageMargin[0] = "No Margin";
                                dialog.dismiss();
                            }
                        }
                    });

                    smallMarginRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            if (isChecked){

                                noRadioBtn.setChecked(false);
                                bigMarginRadioBtn.setChecked(false);

                                pageMargin[0] = "Small Margin";
                                dialog.dismiss();
                            }
                        }
                    });


                    bigMarginRadioBtn.setOnCheckedChangeListener((buttonView1, isChecked1) -> {


                        if (isChecked1){

                            noRadioBtn.setChecked(false);
                            smallMarginRadioBtn.setChecked(false);

                            pageMargin[0] = "Big Margin";
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }else {
//                    convertMultipleImagesToPdf(true,data, null,renameFileName[0] + ".pdf");
//                    dialog[0].dismiss();


                }



            }
        });


        binding.compressionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Dialog dialog = new Dialog(ConvertToPdfActivity.this, R.style.FileSortingDialogStyle);
                dialog.setContentView(R.layout.page_compression_dailog);


                Window window = dialog.getWindow();
                if (window != null) {
                    window.setGravity(Gravity.BOTTOM);
                    window.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }

                RadioButton mediumRadioBtn = dialog.findViewById(R.id.mediumRadioBtn);
                RadioButton lowRadioBtn = dialog.findViewById(R.id.lowRadioBtn);
                RadioButton orignalRadioBtn = dialog.findViewById(R.id.orignalRadioBtn);

                mediumRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                        if (isChecked){

                            lowRadioBtn.setChecked(false);
                            orignalRadioBtn.setChecked(false);

                            pageCompression[0] = "Medium";
                        }
                    }
                });

                lowRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                        if (isChecked){

                            mediumRadioBtn.setChecked(false);
                            orignalRadioBtn.setChecked(false);

                            pageCompression[0] = "Low";
                        }
                    }
                });


                orignalRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                        if (isChecked){

                            lowRadioBtn.setChecked(false);
                            mediumRadioBtn.setChecked(false);

                            pageCompression[0] = "Original";
                        }
                    }
                });

                dialog.show();

                Log.d("checkpageCompression[0]",pageCompression[0]);

                if (pageCompression[0].equals("Original")){
                    lowRadioBtn.setChecked(false);
                    mediumRadioBtn.setChecked(false);
                    orignalRadioBtn.setChecked(true);
                }else if (pageCompression[0].equals("Low")){
                    lowRadioBtn.setChecked(true);
                    mediumRadioBtn.setChecked(false);
                    orignalRadioBtn.setChecked(false);
                }else if (pageCompression[0].equals("Medium")){
                    lowRadioBtn.setChecked(false);
                    mediumRadioBtn.setChecked(true);
                    orignalRadioBtn.setChecked(false);
                }


            }
        });

    // Declare a class-level variable for the dialog
            final Dialog[] renameDialog = {null};

            binding.editTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if the dialog is already showing
                    if (renameDialog[0] != null && renameDialog[0].isShowing()) {
                        return; // Do nothing if the dialog is already open
                    }

                    // Create and set up the dialog
                    renameDialog[0] = new Dialog(ConvertToPdfActivity.this, R.style.renameDialogStyle);
                    renameDialog[0].setContentView(R.layout.rename_dailog);
                    Log.d("checkdouble", "dialog show");

                    Window window = renameDialog[0].getWindow();
                    renameDialog[0].setCancelable(false);
                    if (window != null) {
                        window.setGravity(Gravity.CENTER);
                        window.setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        window.setBackgroundDrawableResource(android.R.color.transparent);
                        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
                        int screenWidth = metrics.widthPixels;
                        int desiredWidth = screenWidth - 2 * dpToPx(ConvertToPdfActivity.this, 30);
                        WindowManager.LayoutParams params = window.getAttributes();
                        params.width = desiredWidth;
                        window.setAttributes(params);
                    }

                    TextInputEditText pdfNewNameEt = renameDialog[0].findViewById(R.id.pdfNewNameEt);
                    TextView renameBtn = renameDialog[0].findViewById(R.id.renameBtn);
                    TextView cancelBtn = renameDialog[0].findViewById(R.id.cancelBtn);
                    ImageView clearTextIcon = renameDialog[0].findViewById(R.id.clearTextIcon);

                    // Clear text functionality
                    clearTextIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pdfNewNameEt.setText("");
                        }
                    });

                    // Set initial text
                    pdfNewNameEt.setText(renameFileName[0]);

                    // Cancel button resets the dialog reference
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            renameDialog[0].dismiss();
                            renameDialog[0] = null;
                            Log.d("checkdouble", "dialog dismissed, ready to open again");
                        }
                    });

                    // Rename button functionality
                    renameBtn.setOnClickListener(view -> {
                        String newName = pdfNewNameEt.getText().toString().trim();
                        if (newName.isEmpty()) {
                            Toast.makeText(ConvertToPdfActivity.this,
                                    "Please enter a name for the PDF document.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            renameFileName[0] = newName + ".pdf";
                            binding.fileName.setText(renameFileName[0]);
                            renameDialog[0].dismiss();
                            renameDialog[0] = null;
                        }
                    });

                    // Show the dialog
                    renameDialog[0].show();
                }
            });



        binding.fileName.setText(renameFileName[0]);



        binding.checkPssword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (binding.checkPssword.isChecked()){
//
                    Log.d("checkopen","open dailog");

                    Dialog dialog = new Dialog(ConvertToPdfActivity.this, R.style.renameDialogStyle);
                    dialog.setContentView(R.layout.new_lock_dialog);
                    dialog.setCancelable(false);

                    Window window = dialog.getWindow();
                    if (window != null) {
                        window.setGravity(Gravity.CENTER);
                        window.setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        window.setBackgroundDrawableResource(android.R.color.transparent);
                        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
                        int screenWidth = metrics.widthPixels;
                        int desiredWidth = screenWidth - 2 * dpToPx(ConvertToPdfActivity.this, 0);
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.width = desiredWidth;
                        window.setAttributes(params);
                    }



                    EditText passwordEt = dialog.findViewById(R.id.passwordEt);
                    EditText confirmPasswordEt = dialog.findViewById(R.id.confirmPasswordEt);
                    TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
                    TextView saveBtn = dialog.findViewById(R.id.saveBtn);
                    TextView rateUsBtn = dialog.findViewById(R.id.rateUsBtn);


                    // Set up the input
                    passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            password[0] = passwordEt.getText().toString();
                            String confirmPassword = confirmPasswordEt.getText().toString();

                            if (password[0].isEmpty()) {
                                passwordEt.setError("Please Enter Password");
                            }

                            if (confirmPassword.isEmpty()) {
                                confirmPasswordEt.setError("Please Enter Confirm Password");
                            }

                            if (!password[0].isEmpty() && !confirmPassword.isEmpty()) {
                                if (password[0].equals(confirmPassword)) {
                                    // Passwords match, proceed with the submission

                                    dialog.dismiss();
                                } else {
                                    // Passwords do not match. Please try again.
                                    Toast.makeText(ConvertToPdfActivity.this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }




                        }
                    });


                    dialog.show();

                }else {
//                    convertMultipleImagesToPdf(true,data, null,renameFileName[0] + ".pdf");
//                    dialog[0].dismiss();


                }



            }
        });



        binding.convertPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                            Log.d("checksize", String.valueOf(uriList.size()));

                if (!isInternetAvailable()) {
                    // No internet, proceed directly
                    goToNextActivity();
                } else if (PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this) ||
                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(ConvertToPdfActivity.this).equals("ads_free_life_time")) {
                    // User is premium, skip ad
                    goToNextActivity();
                }else {


                    if (uriList.size()>=6){

                        showUnLockDailog();
//                                showDailog();
                    }else {
                        goToNextActivity();
                    }

                }









//                if (!isInternetAvailable()) {
//                    goToNextActivity(); // No internet, proceed to the next activity
//                } else {
//
//                    if (PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this)|| PrefUtilForAppAdsFree.getAdsForLiftTimeString(ConvertToPdfActivity.this).equals("ads_free_life_time")){
//                        goToNextActivity();
//
//                        Log.d("checkbilling"," add remove "+PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this));
//
//                    }else{
//
//                        Log.d("checkbilling"," add not remove "+PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this));
//
//                        // Show the ad or proceed if timeout
//
////                        adManager.showAdIfAvailable(ConvertToPdfActivity.this, new AdManager.AdCallback() {
////                            @Override
////                            public void onAdDismissed() {
////                                // Define your custom action here after the ad is dismissed
////                                goToNextActivity();
////                            }
////
////                            @Override
////                            public void onAdFailedToShow() {
////                                // Define your custom action here if the ad fails to show
////                                goToNextActivity();
////                                Log.d("AdManager", "onAdFailedToShow");
////
////                            }
////                        });
//
//
//                        if (!isInternetAvailable()) {
//                            // No internet, proceed directly
//                            goToNextActivity();
//                        } else if (PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this) ||
//                                PrefUtilForAppAdsFree.getAdsForLiftTimeString(ConvertToPdfActivity.this).equals("ads_free_life_time")) {
//                            // User is premium, skip ad
//                            goToNextActivity();
//                        } else {
//                            // Show ad first, then navigate
//                            adManager.showAdIfAvailable(ConvertToPdfActivity.this, getString(R.string.convert_to_pdf_final_button_screen_intertial_ad), new AdManager.AdCallback() {
//                                @Override
//                                public void onAdDismissed() {
//                                    goToNextActivity();
//                                }
//
//                                @Override
//                                public void onAdFailedToShow() {
//                                    goToNextActivity();
//                                }
//                            });
//                        }
//
//
//
//                    }
//
//
//
//                }














//                Log.d("checkopen","password[0]   "+password[0]);
//                Log.d("checkopen","pageSize[0]  "+pageSize[0]);
//                Log.d("checkopen","pageMargin[0]  "+pageMargin[0]);
//                Log.d("checkopen","pageCompression[0]  "+pageCompression[0]);
//
//                convertMultipleImagesToPdf(true, uriList, password[0], renameFileName[0],false,pageSize[0],pageCompression[0],pageMargin[0]);
//


            }



        });


    }

    private void goToNextActivity() {

        Log.d("checkopen","password[0]   "+password[0]);
        Log.d("checkopen","pageSize[0]  "+pageSize[0]);
        Log.d("checkopen","pageMargin[0]  "+pageMargin[0]);
        Log.d("checkopen","pageCompression[0]  "+pageCompression[0]);

//                convertMultipleImagesToPdf(true, uriList, password[0], renameFileName[0],false,pageSize[0],pageCompression[0],pageMargin[0]);


        Intent intent = new Intent(ConvertToPdfActivity.this, NextActivity.class);
        intent.putExtra("password", password[0]);
        intent.putExtra("fileName", renameFileName[0]);
        intent.putExtra("pageSize", pageSize[0]);
        intent.putExtra("pageMargin", pageMargin[0]);
        intent.putExtra("pageCompression", pageCompression[0]);
        intent.putExtra("isLandscap", false);
        intent.putExtra("modulename", modulename);

        intent.putParcelableArrayListExtra("images", new ArrayList(uriList));

        startActivity(intent);

    }


    private void showDailog() {

        Dialog dialog = new Dialog(ConvertToPdfActivity.this, R.style.FileSortingDialogStyle);
        dialog.setContentView(R.layout.if_show_reworded_ads_dailog);


        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Ensures no background
        }


        // Dialog UI elements

        TextView saveBtntv = dialog.findViewById(R.id.saveBtntv);
        TextView goProBtntv = dialog.findViewById(R.id.goProBtntv);

        goProBtntv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConvertToPdfActivity.this, AddFreeActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        saveBtntv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showUnLockDailog();
                dialog.dismiss();
            }
        });



        // Show dialog
        dialog.show();



    }

    private void showUnLockDailog() {

        Dialog dialog = new Dialog(ConvertToPdfActivity.this, R.style.FileSortingDialogStyle);
        dialog.setContentView(R.layout.un_lock_pdf_reworded_ads_dailog);


        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Ensures no background
        }


        // Dialog UI elements

        TextView saveBtntv = dialog.findViewById(R.id.saveBtntv);
        TextView disCardBtn = dialog.findViewById(R.id.disCardBtn);

        disCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConvertToPdfActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        saveBtntv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show progress dialog while checking ad availability
                progressDialog = new ProgressDialog(ConvertToPdfActivity.this);
                progressDialog.setMessage("Loading Ad...");
                progressDialog.setCancelable(false);
                progressDialog.show();


                handleRewardedAdClick(getString(R.string.when_select_user_5_images_or_above_select_button_reworded_ad), "5Images");
                dialog.dismiss();
            }
        });



        // Show dialog
        dialog.show();



    }

    private void handleRewardedAdClick(String adUnitId, String targetActivity) {



        if (!isInternetAvailable()) {
            // No internet, proceed directly
            goToNextActivity();
            Log.d("checkrewordedads","isInternetAvailable  "+targetActivity);
        } else if (PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this) ||
                PrefUtilForAppAdsFree.getAdsForLiftTimeString(ConvertToPdfActivity.this).equals("ads_free_life_time")) {
            // User is premium, skip ad
            goToNextActivity();
            Log.d("checkrewordedads","PrefUtilForAppAdsFree  "+targetActivity);
        } else {
            // Show rewarded ad first, then navigate


            adManager.showRewardedAdIfAvailable(ConvertToPdfActivity.this, adUnitId,progressDialog, new AdManager.AdCallbackRewardedAd() {


                @Override
                public void onAdDismissed() {
                    // If ad is skipped, still navigate
                    goToNextActivity();
                    dismissProgressDialog();
                    Log.d("checkrewordedads","onAdDismissed  "+targetActivity);
                }

                @Override
                public void onAdFailedToShow() {
                    // If ad fails to show, proceed without ad
                    goToNextActivity();
                    dismissProgressDialog();
                    Log.d("checkrewordedads","onAdFailedToShow  "+targetActivity);
                }
            });




        }
    }

    // Helper method to dismiss progress dialog
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void discardDialog() {

            Dialog dialogRename = new Dialog(this, R.style.renameDialogStyle);
            dialogRename.setContentView(R.layout.discard_dailog_images_list);

            Window window = dialogRename.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);

                DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
                int screenWidth = metrics.widthPixels;
                int desiredWidth = screenWidth - 2 * dpToPx(this, 30);

                WindowManager.LayoutParams params = window.getAttributes();
                params.width = desiredWidth;
                window.setAttributes(params);
            }

            TextView disCardBtn = dialogRename.findViewById(R.id.disCardBtn);
            TextView continueBtn = dialogRename.findViewById(R.id.continueBtn);

            disCardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogRename.dismiss();
                    Intent intent = new Intent(ConvertToPdfActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogRename.dismiss();
                }
            });

            dialogRename.show();



//        Dialog dialogRename = new Dialog(ConvertToPdfActivity.this, R.style.renameDialogStyle);
//        dialogRename.setContentView(R.layout.discard_dailog);
//
//        Window window = dialogRename.getWindow();
//        if (window != null) {
//            window.setGravity(Gravity.CENTER);
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            window.setBackgroundDrawableResource(android.R.color.transparent);
//
//            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
//            int screenWidth = metrics.widthPixels;
//            int desiredWidth = screenWidth - 2 * dpToPx(ConvertToPdfActivity.this, 30);
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = desiredWidth;
//            window.setAttributes(params);
//        }
//
//        TextView cancelBtn = dialogRename.findViewById(R.id.cancelBtn);
//        TextView discardBtn = dialogRename.findViewById(R.id.discardBtn);
//
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogRename.dismiss();
//            }
//        });
//
//        discardBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogRename.dismiss();
//
//                Intent intent = new Intent(ConvertToPdfActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        dialogRename.show();
    }


//    private void loadInterstitialAd() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        isAdLoading = true;
//        binding.progressBar.setVisibility(View.GONE);
//
//        timeoutRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (isAdLoading) {
//                    isAdLoading = false;
//                    binding.progressBar.setVisibility(View.GONE);
//                    goToNextActivity();
//                }
//            }
//        };
//        handler.postDelayed(timeoutRunnable, 10000);
//
//        InterstitialAd.load(this, getString(R.string.language_intertial_ad), adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(InterstitialAd interstitialAd) {
//                mInterstitialAd = interstitialAd;
//                isAdLoading = false;
//                binding.progressBar.setVisibility(View.GONE);
//                handler.removeCallbacks(timeoutRunnable);
//                setAdCallbacks();
//            }
//
//            @Override
//            public void onAdFailedToLoad(LoadAdError loadAdError) {
//                mInterstitialAd = null;
//                isAdLoading = false;
//                binding.progressBar.setVisibility(View.GONE);
//                handler.removeCallbacks(timeoutRunnable);
//                goToNextActivity();
//            }
//        });
//    }
//
//    private void setAdCallbacks() {
//        if (mInterstitialAd != null) {
//            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                @Override
//                public void onAdDismissedFullScreenContent() {
//                    mInterstitialAd = null;
//                    loadInterstitialAd();
//                    goToNextActivity();
//                }
//
//                @Override
//                public void onAdFailedToShowFullScreenContent(AdError adError) {
//                    mInterstitialAd = null;
//                    loadInterstitialAd();
//                    goToNextActivity();
//                }
//            });
//        }
//    }

//    private void goToNextActivity() {
//
//        Log.d("checkopen","password[0]   "+password[0]);
//        Log.d("checkopen","pageSize[0]  "+pageSize[0]);
//        Log.d("checkopen","pageMargin[0]  "+pageMargin[0]);
//        Log.d("checkopen","pageCompression[0]  "+pageCompression[0]);
//
//        convertMultipleImagesToPdf(true, uriList, password[0], renameFileName[0],false,pageSize[0],pageCompression[0],pageMargin[0]);
//
//    }

//    private void showAdOrLoad() {
//        if (mInterstitialAd != null) {
//            mInterstitialAd.show(this);
//        } else if (isAdLoading) {
//            binding.progressBar.setVisibility(View.VISIBLE);
//        } else {
//            goToNextActivity();
//        }
//    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }



    private int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

//    @Override
//    public void onBackPressed() {
////        discardDialog();
//        onBackPressed();
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        discardDialog();
    }

    private void convertMultipleImagesToPdf(boolean convertAll, List<Uri> data, String password, String fileName, boolean isLandscape, String pageSize, String compressionLevel, String marginSetting) {
        Log.d(TAG, "convertImagesToPdf: convertAll " + convertAll);
//        progressDialog.setMessage("Converting to PDF...");
//        progressDialog.show();

//        Dialog progressDialog = showloadingDailog(data);
//        progressDialog.show();


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: BG work start.....");

                ArrayList<ModelImage> imagesToPdfList = new ArrayList<>();
                if (convertAll) {
                    imagesToPdfList = allImageArrayList;
                } else {
                    for (int i = 0; i < allImageArrayList.size(); i++) {
                        if (allImageArrayList.get(i).isChecked()) {
                            imagesToPdfList.add(allImageArrayList.get(i));
                        }
                    }
                }

                try {
                    File root = new File(getExternalFilesDir(null), Constant.PDF_FOLDER);
                    root.mkdirs();

                    File file = new File(root, fileName);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    PdfDocument pdfDocument = new PdfDocument();

//                    ClipData mClipData = data.getClipData();

                    for (int i = 0; i < data.size(); i++) {
//                        ClipData.Item item = mClipData.getItemAt(i);

                        Uri imageToAddInPdfUri = uriList.get(i);

                        try {
                            Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageToAddInPdfUri));
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageToAddInPdfUri);
                            }

                            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);

                            int pageWidth;
                            int pageHeight;
                            int margin = getMargin(marginSetting);

                            switch (pageSize) {
                                case "Fit":
                                    pageWidth = isLandscape ? bitmap.getHeight() : bitmap.getWidth();
                                    pageHeight = isLandscape ? bitmap.getWidth() : bitmap.getHeight();
                                    break;
                                case "A4":
                                    pageWidth = isLandscape ? (int) (297 / 25.4 * 72) : (int) (210 / 25.4 * 72);
                                    pageHeight = isLandscape ? (int) (210 / 25.4 * 72) : (int) (297 / 25.4 * 72);
                                    break;
                                case "US Letter":
                                    pageWidth = isLandscape ? (int) (279.4 / 25.4 * 72) : (int) (215.9 / 25.4 * 72);
                                    pageHeight = isLandscape ? (int) (215.9 / 25.4 * 72) : (int) (279.4 / 25.4 * 72);
                                    break;
                                default:
                                    throw new IllegalArgumentException("Invalid page size: " + pageSize);
                            }

                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i + 1).create();
                            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                            Bitmap scaledBitmap = scaleBitmapToFitPage(bitmap, pageWidth - 2 * margin, pageHeight - 2 * margin);
                            scaledBitmap = compressBitmap(scaledBitmap, compressionLevel);

                            int offsetX = (pageWidth - scaledBitmap.getWidth()) / 2;
                            int offsetY = (pageHeight - scaledBitmap.getHeight()) / 2;

                            page.getCanvas().drawBitmap(scaledBitmap, offsetX, offsetY, null);

                            pdfDocument.finishPage(page);
                            bitmap.recycle();

                        } catch (Exception e) {
                            Log.d(TAG, "run: ", e);
                        }
                    }

                    pdfDocument.writeTo(fileOutputStream);
                    pdfDocument.close();

                    File fileDate = new File(file.getAbsolutePath());
                    long fileSizeBytes = fileDate.length();
                    long timestamp = file.lastModified();

//                    PdfFile pdfFile = new PdfFile(file.getAbsolutePath(), fileName, password, fileSizeBytes, timestamp, false,false,false,false,false,false);
//                    db.pdfFileDao().insert(pdfFile);




                    if (modulename.equals("Scanner_Module")){
                        PdfFile pdfFile = new PdfFile(file.getAbsolutePath(), fileName,"Scanned Doc","#495D8D", password, fileSizeBytes, timestamp, false,false,true,true,false,false,false,false,false,AccountsOrGuesHelper.checkAccountOrNot(ConvertToPdfActivity.this),false,"pdf");
//                        db.pdfFileDao().insert(pdfFile);
                        pdfFileViewModel.insertPdfFile(pdfFile);


                    }else {
                        PdfFile pdfFile = new PdfFile(file.getAbsolutePath(), fileName,"PDF Doc","#FF2F2F",  password, fileSizeBytes, timestamp, false,false,true,false,false,false,false,false,false,AccountsOrGuesHelper.checkAccountOrNot(ConvertToPdfActivity.this),false,"pdf");
//                        db.pdfFileDao().insert(pdfFile);
                        pdfFileViewModel.insertPdfFile(pdfFile);

                    }




                    Intent intent = new Intent(ConvertToPdfActivity.this, Prview_Screen.class);
                    intent.putExtra("pdffilePath", "" + file.getAbsolutePath());
                    intent.putExtra("fileName", fileName);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
//                    progressDialog.dismiss();
                    Log.d(TAG, "run: ", e);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: Converted...");
//                        progressDialog.dismiss();
                        Toast.makeText(ConvertToPdfActivity.this, "Conversion completed successfully.", Toast.LENGTH_SHORT).show();



                        if (!isInternetAvailable()) {
                            goToNextActivity(); // No internet, proceed to the next activity
                        } else {

                            if (PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this)|| PrefUtilForAppAdsFree.getAdsForLiftTimeString(ConvertToPdfActivity.this).equals("ads_free_life_time")){
                                goToNextActivity();

                                Log.d("checkbilling"," add remove "+PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this));

                            }else{

                                Log.d("checkbilling"," add not remove "+PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this));

                                // Show the ad or proceed if timeout

//                        adManager.showAdIfAvailable(ConvertToPdfActivity.this, new AdManager.AdCallback() {
//                            @Override
//                            public void onAdDismissed() {
//                                // Define your custom action here after the ad is dismissed
//                                goToNextActivity();
//                            }
//
//                            @Override
//                            public void onAdFailedToShow() {
//                                // Define your custom action here if the ad fails to show
//                                goToNextActivity();
//                                Log.d("AdManager", "onAdFailedToShow");
//
//                            }
//                        });


                                if (!isInternetAvailable()) {
                                    // No internet, proceed directly
                                    goToNextActivity();
                                } else if (PrefUtilForAppAdsFree.isPremium(ConvertToPdfActivity.this) ||
                                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(ConvertToPdfActivity.this).equals("ads_free_life_time")) {
                                    // User is premium, skip ad
                                    goToNextActivity();
                                } else {
                                    // Show ad first, then navigate
                                    adManager.showAdIfAvailable(ConvertToPdfActivity.this, getString(R.string.convert_to_pdf_final_button_screen_intertial_ad), new AdManager.AdCallback() {
                                        @Override
                                        public void onAdDismissed() {
                                            goToNextActivity();
                                        }

                                        @Override
                                        public void onAdFailedToShow() {
                                            goToNextActivity();
                                        }
                                    });
                                }



                            }



                        }



                    }
                });
            }
        });
    }

    private Dialog showloadingDailog(List<Uri> data) {

//        Dialog dialog = new Dialog(ConvertToPdfActivity.this, R.style.renameDialogStyle);
//        Dialog dialog = new Dialog(ConvertToPdfActivity.this, R.style.FullScreenDialog);
//        dialog.setContentView(R.layout.show_image_to_pdf_loding_dailog);
//
//        Window window = dialog.getWindow();
//        if (window != null) {
//            window.setGravity(Gravity.CENTER);
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            window.setBackgroundDrawableResource(android.R.color.transparent);
//
//            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
//            int screenWidth = metrics.widthPixels;
//            int desiredWidth = screenWidth - 2 * dpToPx(ConvertToPdfActivity.this, 30);
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = desiredWidth;
//            window.setAttributes(params);
//        }

        Dialog dialog = new Dialog(ConvertToPdfActivity.this, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.show_image_to_pdf_loding_dailog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);
        }

        TextView selectedMediaCancelBtn = dialog.findViewById(R.id.selectedMediaCancelBtn);
        TextView contentMedia = dialog.findViewById(R.id.contentMedia);

 contentMedia.setText("1 of "+data.size()+" Ready");

        selectedMediaCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (dialog.getWindow() != null) {
            // Set the layout parameters to match parent width and wrap content in height
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // Set the background to be transparent
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Apply additional custom styling or layout parameters here
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int desiredWidth = screenWidth - 2 * dpToPx(ConvertToPdfActivity.this, 30); // For example
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = desiredWidth;
            dialog.getWindow().setAttributes(params);
        }

        return dialog;

//        dialog.show();


    }


    private int getMargin(String marginSetting) {
        switch (marginSetting) {
            case "No Margin":
                return 0;
            case "Small Margin":
                return 30; // 10 points margin (you can adjust this value)
            case "Big Margin":
                return 50; // 20 points margin (you can adjust this value)
            default:
                throw new IllegalArgumentException("Invalid margin setting: " + marginSetting);
        }
    }

    private Bitmap scaleBitmapToFitPage(Bitmap originalBitmap, int pageWidth, int pageHeight) {
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

        float scale = Math.min((float) pageWidth / originalWidth, (float) pageHeight / originalHeight);

        int scaledWidth = Math.round(originalWidth * scale);
        int scaledHeight = Math.round(originalHeight * scale);

        return Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);
    }

    private Bitmap compressBitmap(Bitmap bitmap, String compressionLevel) {
        Bitmap compressedBitmap = bitmap;

        switch (compressionLevel) {
            case "Medium":
                compressedBitmap = compressBitmapQuality(bitmap, 50); // 50% quality
                break;
            case "Low":
                compressedBitmap = compressBitmapQuality(bitmap, 20); // 20% quality
                break;
            case "Original":
                break;
            default:
                throw new IllegalArgumentException("Invalid compression level: " + compressionLevel);
        }

        return compressedBitmap;
    }

    private Bitmap compressBitmapQuality(Bitmap bitmap, int quality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}