package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.view.View.VISIBLE;

import static com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side.GetFilePathFromUriKt.getFilePathFromUri;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.MobileAds;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.MergeActivity;
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.PdfCompressionActivity;
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.PdfEnAndDeCorruptingActivity;
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.PdfSplitterActivity;
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.waterMark.WaterMarkHomeActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.CamActivity;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.GenerateCodeActivity;
import com.pixelz360.docsign.imagetopdf.creator.databinding.FragmentActionBinding;
import com.pixelz360.docsign.imagetopdf.creator.editModule.AllEditImagesActivity;
import com.pixelz360.docsign.imagetopdf.creator.editModule.EditMoudleActivity;
import com.pixelz360.docsign.imagetopdf.creator.pdf_tools_billing.AddFreeForPdfToolsActivity;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.DigitalSignatureActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActionFragment extends Fragment implements ToolbarSettings {

    LinearLayout convertPdfBtn,signBtn,scanBtn;

    private static final int Merge_Request_CODE = 43;
//    private AdManager adManager11;
    private AdManager adManager;
    private ProgressDialog progressDialog;

    private static final int PICK_PDF_COMPRESS_FILE = 1;
    private String selectedFilePath;

    private ActivityResultLauncher<IntentSenderRequest> scannerLauncher;

    // Name with extension of the image
    long timestamp = System.currentTimeMillis();
    String mainFileName = "Untitled_file_Doc24 " + timestamp;
    String renameFileName = mainFileName;
    GmsDocumentScanner scanner;

    FragmentActionBinding binding;

    private final ActivityResultLauncher<Intent> getContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    Intent data = result.getData();
//                    if (data != null) {
//                        String barcode = data.getStringExtra("BarcodeResult");
//                        binding.txtResult.setText(barcode);
//                    }
//                }
            }
    );

    ConstraintLayout homeToolbarTitle;
    TextView homeSelectAllButton;
    TextView homeToolbarSelectedItem;
    ImageButton homeSearchButton;
    ImageButton homeSortButton;
    ImageButton homeSettingButton;
    ImageButton homeDeleteButton;
    LinearLayout homeSearchLayout;
    Toolbar homeToolbar;
    ImageView homeSearchBackBtn;
    ImageView homeClearButton;
    EditText homeSearchEditText;


    private boolean isAdLoading = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;



    Norma norma = new Norma();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         binding = FragmentActionBinding.inflate(inflater, container, false);

        Log.d("checkfragmentselected","ActionFragment  onCreateView");

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("checkfragmentselected","ActionFragment  onViewCreated");



        MobileAds.initialize(requireActivity()); {}


        convertPdfBtn = view.findViewById(R.id.convertPdfBtn);
        signBtn = view.findViewById(R.id.signBtn);
        scanBtn = view.findViewById(R.id.scanBtn);

//// Get screen width
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        int screenWidth = displayMetrics.widthPixels;
//
//// Define the number of buttons (columns)
//        int numberOfColumns = 3;
//
//// Get margins (convert dp to pixels)
//        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
//        int totalMargin = margin * (numberOfColumns - 1);
//
//// Calculate available width per item
//        int itemWidth = (screenWidth - totalMargin) / numberOfColumns;
//
//
//
//        convertPdfBtn.getLayoutParams().width = itemWidth;
//        signBtn.getLayoutParams().width = itemWidth;
//        scanBtn.getLayoutParams().width = itemWidth;
//
//// Apply changes
//        convertPdfBtn.requestLayout();
//        signBtn.requestLayout();
//        scanBtn.requestLayout();




//        loadInterstitialAd();

//        adManager11 = new AdManager();
        adManager = new AdManager();
//        adManager11.loadAd(requireActivity(), "ca-app-pub-3097817755522298/5613637998");
//        adManager11.loadAd(requireActivity(), getString(R.string.convert_to_pdf_final_button_screen_intertial_ad));
//        adManager.loadAd(requireActivity(), getString(R.string.sign_documets_intertial_ad));



        // Load rewarded ads with different Ad Unit IDs
        adManager.loadRewardedAd(requireActivity(), getString(R.string.compress_main_button_reworded_ad));
        adManager.loadRewardedAd(requireActivity(), getString(R.string.merge_main_button_reworded_ad));
        adManager.loadRewardedAd(requireActivity(), getString(R.string.split_main_button_reworded_ad));
        adManager.loadRewardedAd(requireActivity(), getString(R.string.watermark_main_button_reworded_ad));
        adManager.loadRewardedAd(requireActivity(), getString(R.string.scan_documets_main_button_reworded_ad));
        adManager.loadRewardedAd(requireActivity(), getString(R.string.sign_main_button_reworded_ad));


        // Load the ad once in onCreate


        // Initialize RewardedAdManager







//        norma.newClss(binding.scanTextView,binding.horizontalScrollView);
//        norma.newClss(binding.signTextView,binding.signHorizontalScrollView);
//        norma.newClss(binding.imageToPdfTextView,binding.imageToPdfHorizontalScrollView);
//        norma.newClss(binding.barCodeTextView,binding.barCodeHorizontalScrollView);
//        norma.newClss(binding.qrCodeTextView,binding.qrCodeHorizontalScrollView);
//        norma.newClss(binding.qrCodeGeneratorTextView,binding.qrCodeGeneratorHorizontalScrollView);
//        norma.newClss(binding.splitPdfTextView,binding.splitPdfHorizontalScrollView);
//        norma.newClss(binding.mergePdfTextView,binding.mergePdfHorizontalScrollView);
//        norma.newClss(binding.compressPdfTextView,binding.compressPdfHorizontalScrollView);
//        norma.newClss(binding.waterPdfTextView,binding.waterPdfHorizontalScrollView);
//        norma.newClss(binding.lockPdfTextView,binding.lockPdfHorizontalScrollView);

        // Wait until the layout is drawn to calculate scroll range
//        binding.scanTextView.post(() -> {
//            int scrollRange = binding.scanTextView.getWidth() - binding.horizontalScrollView.getWidth();
//
//            if (scrollRange > 0) {
//                // Animate the scrolling
//                ObjectAnimator animator = ObjectAnimator.ofInt(
//                        binding.horizontalScrollView,
//                        "scrollX",
//                        0,
//                        scrollRange
//                );
//                animator.setDuration(5000); // Duration of animation in milliseconds
//                animator.setRepeatCount(ObjectAnimator.INFINITE); // Infinite loop
//                animator.setRepeatMode(ObjectAnimator.RESTART); // Restart when done
//                animator.start();
//            }
//        });



        convertPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                performNextActivityTask(); // No internet, proceed to the next activity



//                if (!isInternetAvailable()) {
//                    performNextActivityTask(); // No internet, proceed to the next activity
//                } else {
//
//                    if (PrefUtilForAppAdsFree.isPremium(requireActivity())){
//                        performNextActivityTask();
//
//                        Log.d("checkbilling"," add remove "+PrefUtilForAppAdsFree.isPremium(requireActivity()));
//
//                    }else{
//
//                        Log.d("checkbilling"," add not remove "+PrefUtilForAppAdsFree.isPremium(requireActivity()));
//
//                        // Show the ad or proceed if timeout
//
//                        adManager11.showAdIfAvailable(requireActivity(), new AdManager.AdCallback() {
//                            @Override
//                            public void onAdDismissed() {
//                                // Define your custom action here after the ad is dismissed
//                                performNextActivityTask();
//                            }
//
//                            @Override
//                            public void onAdFailedToShow() {
//                                // Define your custom action here if the ad fails to show
//                                performNextActivityTask();
//                                Log.d("AdManager", "onAdFailedToShow");
//
//                            }
//                        });
//
//
//                    }
//
//
//
//                }




            }


//
//                if (!isInternetAvailable()) {
//                    goToNextActivity(); // No internet, proceed to the next activity
//                } else {
//                    // Show the ad or proceed if timeout
//                    adManager.showAdOrProceed(binding.progressBar, new Runnable() {
//                        @Override
//                        public void run() {
//                            goToNextActivity();
//                        }
//                    });                }
//
//
//
//
//
//
//            }
//
//            private void goToNextActivity() {
//                pickImageFromGallery();
//            }
        });



//        convertPdfBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//                if (!isInternetAvailable()) {
//                    goToNextActivity(); // No internet, proceed to the next activity
//                } else {
//                    // Show the ad or proceed if timeout
//                    adManager.showAdOrProceed(binding.progressBar, new Runnable() {
//                        @Override
//                        public void run() {
//                            goToNextActivity();
//                        }
//                    });                }
//
//
//
//
//
//
//            }
//
//            private void goToNextActivity() {
//                pickImageFromGallery();
//            }
//        });


        binding.addWaterMarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new  Intent(requireActivity(), WaterMarkHomeActivity.class);
//                startActivity(i);



                if (!isInternetAvailable()) {
                    // No internet, proceed directly
                    openActivity("waterMarkBtn");
                } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
                    // User is premium, skip ad
                    openActivity("waterMarkBtn");
                }else {
                    showDailog(getString(R.string.watermark_main_button_reworded_ad),"waterMarkBtn");

                }


//                if (PrefUtilForPdfTools.isPremium(requireActivity())){
//
//                    Intent i = new  Intent(requireActivity(), WaterMarkHomeActivity.class);
//                    startActivity(i);
//
//                    Log.d("checkbilling","Checked "+ PrefUtilForPdfTools.isPremium(requireActivity())+"  allready purchase ");
//
//                }else {
//
//
//                    Intent intent = new Intent(requireActivity(), AddFreeForPdfToolsActivity.class);
//                    startActivity(intent);
//
//                    Log.d("checkbilling","Checked "+ PrefUtilForPdfTools.isPremium(requireActivity())+" not allready purchase ");
//
//                }

            }
        });

        binding.lockPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new  Intent(requireActivity(), PdfEnAndDeCorruptingActivity.class);
                startActivity(i);

//                Toast.makeText(requireActivity(),"Comming Soon",Toast.LENGTH_LONG).show();

//                Intent i = new  Intent(requireActivity(), PdfSelectionActivity.class);
//                startActivity(i);


            }
        });




        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!isInternetAvailable()) {
                    // No internet, proceed directly
                    openActivity("signBtn");
                } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
                    // User is premium, skip ad
                    openActivity("signBtn");
                }else {
                    showDailog(getString(R.string.sign_main_button_reworded_ad),"signBtn");

                }




//                if (!isInternetAvailable()) {
//                    // No internet, proceed to the next activity
//
//                    Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                    intent.putExtra("ActivityAction", "FileSearch");
//                    startActivityForResult(intent, Merge_Request_CODE);
//
//                } else {
//
//                    if (PrefUtilForAppAdsFree.isPremium(requireActivity()) || PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")){
//
//                        Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                        intent.putExtra("ActivityAction", "FileSearch");
//                        startActivityForResult(intent, Merge_Request_CODE);
//
//                        Log.d("checkbilling"," add remove "+PrefUtilForAppAdsFree.isPremium(requireActivity()));
//
//
//                    }else {
//
//                        Log.d("checkbilling"," add not remove "+PrefUtilForAppAdsFree.isPremium(requireActivity()));

                        // Show the ad or proceed if timeout



//                        if (!isInternetAvailable()) {
//                            // No internet, proceed directly
//                            Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                            intent.putExtra("ActivityAction", "FileSearch");
//                            startActivityForResult(intent, Merge_Request_CODE);
//                        } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
//                                PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
//                            // User is premium, skip ad
//                            Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                            intent.putExtra("ActivityAction", "FileSearch");
//                            startActivityForResult(intent, Merge_Request_CODE);
//                        } else {
//                            // Show ad first, then navigate
//                            adManager.showAdIfAvailable(requireActivity(), getString(R.string.sign_documets_intertial_ad), new AdManager.AdCallback() {
//                                @Override
//                                public void onAdDismissed() {
//                                    Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                                    intent.putExtra("ActivityAction", "FileSearch");
//                                    startActivityForResult(intent, Merge_Request_CODE);
//                                }
//
//                                @Override
//                                public void onAdFailedToShow() {
//                                    Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                                    intent.putExtra("ActivityAction", "FileSearch");
//                                    startActivityForResult(intent, Merge_Request_CODE);
//                                }
//                            });
//                        }



//                        adManager.showAdIfAvailable(requireActivity(), new AdManager.AdCallback() {
//                            @Override
//                            public void onAdDismissed() {
//                                // Define your custom action here after the ad is dismissed
//
//                                Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                                intent.putExtra("ActivityAction", "FileSearch");
//                                startActivityForResult(intent, Merge_Request_CODE);
//
//
//                            }
//
//                            @Override
//                            public void onAdFailedToShow() {
//                                // Define your custom action here if the ad fails to show
//
//                                Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                                intent.putExtra("ActivityAction", "FileSearch");
//                                startActivityForResult(intent, Merge_Request_CODE);
//
//                                Log.d("AdManager", "onAdFailedToShow");
//
//                            }
//                        });


//                    }




//                }



//                if (!isInternetAvailable()) {
//                    // No internet, proceed directly
//                    Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                    intent.putExtra("ActivityAction", "FileSearch");
//                    startActivityForResult(intent, Merge_Request_CODE);
//                } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
//                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
//                    // User is premium, skip ad
//                    Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                    intent.putExtra("ActivityAction", "FileSearch");
//                    startActivityForResult(intent, Merge_Request_CODE);
//                } else {
//                    // Show ad first, then navigate
//                    adManager.showAdIfAvailable(requireActivity(), getString(R.string.sign_documets_intertial_ad), new AdManager.AdCallback() {
//                        @Override
//                        public void onAdDismissed() {
//                            Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                            intent.putExtra("ActivityAction", "FileSearch");
//                            startActivityForResult(intent, Merge_Request_CODE);
//                        }
//
//                        @Override
//                        public void onAdFailedToShow() {
//                            Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
//                            intent.putExtra("ActivityAction", "FileSearch");
//                            startActivityForResult(intent, Merge_Request_CODE);
//                        }
//                    });
//                }





            }
        });

        binding.qrCodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new  Intent(requireActivity(), CamActivity.class);
                i.putExtra("title", "Example");
                i.putExtra("msg", "Scan Barcode");
                getContent.launch(i);
            }
        });

        binding.barCodeScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new  Intent(requireActivity(), CamActivity.class);
                i.putExtra("title", "Example");
                i.putExtra("msg", "Scan Barcode");
                getContent.launch(i);
            }
        });

        binding.qrCodeGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new  Intent(requireActivity(), GenerateCodeActivity.class);
                startActivity(i);
            }
        });


        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isInternetAvailable()) {
                    // No internet, proceed directly
                    openActivity("scanBtn");
                } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
                    // User is premium, skip ad
                    openActivity("scanBtn");
                }else {
                    showDailog(getString(R.string.scan_documets_main_button_reworded_ad),"scanBtn");

                }




//                if (!isInternetAvailable()) {
//                    // No internet, proceed directly
//                    scanner.getStartScanIntent(requireActivity()).addOnSuccessListener(intentSender -> {
//                        // Create an IntentSenderRequest and launch the scanner
//                        IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
//                        scannerLauncher.launch(request);
//                    }).addOnFailureListener(e -> {
//                        // Show an error message in case of failure
//                        Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.e("Scan Error", "Failed to start scan", e);
//                    });
//                } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
//                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
//                    // User is premium, skip ad
//                    scanner.getStartScanIntent(requireActivity()).addOnSuccessListener(intentSender -> {
//                        // Create an IntentSenderRequest and launch the scanner
//                        IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
//                        scannerLauncher.launch(request);
//                    }).addOnFailureListener(e -> {
//                        // Show an error message in case of failure
//                        Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.e("Scan Error", "Failed to start scan", e);
//                    });
//                } else {
//                    // Show ad first, then navigate
//                    adManager.showAdIfAvailable(requireActivity(), getString(R.string.scan_documets_intertial_ad), new AdManager.AdCallback() {
//                        @Override
//                        public void onAdDismissed() {
//                            scanner.getStartScanIntent(requireActivity()).addOnSuccessListener(intentSender -> {
//                                // Create an IntentSenderRequest and launch the scanner
//                                IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
//                                scannerLauncher.launch(request);
//                            }).addOnFailureListener(e -> {
//                                // Show an error message in case of failure
//                                Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                                Log.e("Scan Error", "Failed to start scan", e);
//                            });
//                        }
//
//                        @Override
//                        public void onAdFailedToShow() {
//                            scanner.getStartScanIntent(requireActivity()).addOnSuccessListener(intentSender -> {
//                                // Create an IntentSenderRequest and launch the scanner
//                                IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
//                                scannerLauncher.launch(request);
//                            }).addOnFailureListener(e -> {
//                                // Show an error message in case of failure
//                                Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                                Log.e("Scan Error", "Failed to start scan", e);
//                            });
//                        }
//                    });
//                }





//
//                scanner.getStartScanIntent(requireActivity()).addOnSuccessListener(intentSender -> {
//                    // Create an IntentSenderRequest and launch the scanner
//                    IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
//                    scannerLauncher.launch(request);
//                }).addOnFailureListener(e -> {
//                    // Show an error message in case of failure
//                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                    Log.e("Scan Error", "Failed to start scan", e);
//                });





//                scanner.getStartScanIntent(requireActivity()).addOnSuccessListener(intentSender -> {
//                    // Create an IntentSenderRequest and launch the scanner
//                    IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
//                    scannerLauncher.launch(request);
//                }).addOnFailureListener(e -> {
//                    // Show an error message in case of failure
//                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
//                    Log.e("Scan Error", "Failed to start scan", e);
//                });
            }
        });


        GmsDocumentScannerOptions options = new GmsDocumentScannerOptions.Builder()
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                .setPageLimit(100)
                .setGalleryImportAllowed(true)
                .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG, GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
                .build();


        scanner = GmsDocumentScanning.getClient(options);

        // Register the activity result launcher.
        scannerLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                GmsDocumentScanningResult scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.getData());

                if (scanningResult != null) {
                    // Handle image pages
                    List<Uri> uris = new ArrayList<>();
                    if (scanningResult.getPages() != null && !scanningResult.getPages().isEmpty()) {
                        for (int i = 0; i < scanningResult.getPages().size(); i++) {
                            Uri uri = scanningResult.getPages().get(i).getImageUri();
                            uris.add(uri);
                        }

                        Intent intent = new Intent(requireActivity(), AllEditImagesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putParcelableArrayListExtra("images", new ArrayList<>(uris));
                        intent.putExtra("renameFileName", renameFileName);
                        intent.putExtra("modulename", "Scanner_Module");
                        startActivity(intent);
                        requireActivity().finish();
                        requireActivity().overridePendingTransition(0, 0);  // Disable activity transition animation
                    }

                    // Handle PDF result
                    if (scanningResult.getPdf() != null) {
                        FileOutputStream fos = null;
                        try {
                            // Create a new file in the app's internal storage
                            File pdfFile = new File(requireActivity().getFilesDir(), "scan.pdf");

                            // Open a FileOutputStream to write the PDF content
                            fos = new FileOutputStream(pdfFile);

                            // Get the InputStream from the PDF Uri
                            Uri pdfUri = scanningResult.getPdf().getUri();
                            try (FileOutputStream finalFos = fos;
                                 InputStream inputStream = requireActivity().getContentResolver().openInputStream(pdfUri)) {

                                // Check if inputStream is not null and write the PDF file
                                if (inputStream != null) {
                                    byte[] buffer = new byte[1024];
                                    int len;
                                    while ((len = inputStream.read(buffer)) != -1) {
                                        finalFos.write(buffer, 0, len);
                                    }
                                    Log.d("PDF Save", "PDF saved successfully at " + pdfFile.getAbsolutePath());
                                } else {
                                    Log.e("PDF Save Error", "Failed to open InputStream from Uri.");
                                }
                            }
                        } catch (Exception e) {
                            Log.e("PDF Save Error", "Failed to save PDF", e);
                        } finally {
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (Exception e) {
                                    Log.e("PDF Save Error", "Failed to close FileOutputStream", e);
                                }
                            }
                        }
                    }

                }
            }
        });




        binding.margePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(requireActivity(), AllPDFsActivity.class);
//                Intent intent = new Intent(requireActivity(), MergeActivity.class);
//                startActivity(intent);


                if (!isInternetAvailable()) {
                    // No internet, proceed directly
                    openActivity("mergeBtn");
                } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
                    // User is premium, skip ad
                    openActivity("mergeBtn");
                }else {
                    showDailog(getString(R.string.merge_main_button_reworded_ad),"mergeBtn");

                }




//                if (PrefUtilForPdfTools.isPremium(requireActivity())){
//                    Intent intent = new Intent(requireActivity(), MergeActivity.class);
//                    startActivity(intent);
//
//                    Log.d("checkbilling","Checked "+ PrefUtilForPdfTools.isPremium(requireActivity())+"  allready purchase ");
//
//
//                }else {
//                    Intent intent = new Intent(requireActivity(), AddFreeForPdfToolsActivity.class);
//                    startActivity(intent);
//
//                    Log.d("checkbilling","Checked "+ PrefUtilForPdfTools.isPremium(requireActivity())+" not allready purchase ");
//
//                }








//                rewardedAdManager.showAd(
//                        MERGE_AD_UNIT_ID,
//                        () -> {
//                            // Reward earned: Navigate to AllPDFsActivity
//                            Intent intent1 = new Intent(requireActivity(), AllPDFsActivity.class);
//                            startActivity(intent1);
//                        },
//                        () -> {
//                            // No ad available: Notify and navigate
//                            Toast.makeText(requireActivity(), "No ad available. Proceeding without reward.", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(requireActivity(), AllPDFsActivity.class);
//                            startActivity(intent);
//                        }
//                );



            }
        });

        binding.splitPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                Intent intent = new Intent(requireActivity(), PdfSplitterActivity.class);
//                startActivity(intent);



                if (!isInternetAvailable()) {
                    // No internet, proceed directly
                    openActivity("splitBtn");
                } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
                    // User is premium, skip ad
                    openActivity("splitBtn");
                }else {
                    showDailog(getString(R.string.split_main_button_reworded_ad),"splitBtn");

                }




//                if (PrefUtilForPdfTools.isPremium(requireActivity())){
//                    Intent intent = new Intent(requireActivity(), PdfSplitterActivity.class);
//                    startActivity(intent);
//
//                    Log.d("checkbilling","Checked "+ PrefUtilForPdfTools.isPremium(requireActivity())+"  allready purchase ");
//
//
//                }else {
//                    Intent intent = new Intent(requireActivity(), AddFreeForPdfToolsActivity.class);
//                    startActivity(intent);
//
//                    Log.d("checkbilling","Checked "+ PrefUtilForPdfTools.isPremium(requireActivity())+" not allready purchase ");
//
//                }


//                rewardedAdManager.showAd(
//                        SPLIT_AD_UNIT_ID,
//                        () -> {
//                            // Reward earned: Navigate to PdfSplitterActivity
//                            Intent intent = new Intent(requireActivity(), PdfSplitterActivity.class);
//                         startActivity(intent);
//                        },
//                        () -> {
//                            // No ad available: Notify and navigate
//                            Toast.makeText(requireActivity(), "No ad available. Proceeding without reward.", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(requireActivity(), PdfSplitterActivity.class);
//                            startActivity(intent);
//                        }
//                );

            }
        });

        binding.compressPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(requireActivity(), PdfCompressionActivity.class);
//                startActivity(intent);



                if (!isInternetAvailable()) {
                    // No internet, proceed directly
                    openActivity("compressionBtn");
                } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
                        PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
                    // User is premium, skip ad
                    openActivity("compressionBtn");
                }else {
                    showDailog(getString(R.string.compress_main_button_reworded_ad),"compressionBtn");

                }





//                if (PrefUtilForPdfTools.isPremium(requireActivity())){
//                    selectPdfFile();
//
//                    Log.d("checkbilling","Checked "+ PrefUtilForPdfTools.isPremium(requireActivity())+"  allready purchase ");
//
//                }else {
//                    Intent intent = new Intent(requireActivity(), AddFreeForPdfToolsActivity.class);
//                    startActivity(intent);
//
//                    Log.d("checkbilling","Checked "+ PrefUtilForPdfTools.isPremium(requireActivity())+" not allready purchase ");
//
//                }

//                selectPdfFile();
            }
        });



//        if (PrefUtilForPdfTools.isPremium(requireActivity())) {
//            binding.splitIcon.setBackground(requireActivity().getResources().getDrawable(R.drawable.pdf_tools_un_lock_bg));
//            binding.mergeIcon.setBackground(requireActivity().getResources().getDrawable(R.drawable.pdf_tools_un_lock_bg));
//            binding.compressionIcon.setBackground(requireActivity().getResources().getDrawable(R.drawable.pdf_tools_un_lock_bg));
//            binding.waterMarkIcon.setBackground(requireActivity().getResources().getDrawable(R.drawable.pdf_tools_un_lock_bg));
//        } else {
//            binding.splitIcon.setBackground(requireActivity().getResources().getDrawable(R.drawable.pdf_tools_lock_bg));
//            binding.mergeIcon.setBackground(requireActivity().getResources().getDrawable(R.drawable.pdf_tools_lock_bg));
//            binding.compressionIcon.setBackground(requireActivity().getResources().getDrawable(R.drawable.pdf_tools_lock_bg));
//            binding.waterMarkIcon.setBackground(requireActivity().getResources().getDrawable(R.drawable.pdf_tools_lock_bg));
//        }



}

    private void showDailog(String adId, String ModuleStatus) {

        Dialog dialog = new Dialog(requireActivity(), R.style.FileSortingDialogStyle);
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
                Intent intent = new Intent(requireActivity(), AddFreeForPdfToolsActivity.class);
                startActivity(intent);
                requireActivity().finish();
                dialog.dismiss();
            }
        });

        saveBtntv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleRewardedAdClick(adId, ModuleStatus);

                dialog.dismiss();
            }
        });



        // Show dialog
        dialog.show();



    }

    private void handleRewardedAdClick(String adUnitId, String targetActivity) {

        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setMessage("Loading Ad...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        // Show rewarded ad first, then navigate
        adManager.showRewardedAdIfAvailable(requireActivity(), adUnitId,progressDialog, new AdManager.AdCallbackRewardedAd() {


            @Override
            public void onAdDismissed() {
                // If ad is skipped, still navigate
                openActivity(targetActivity);
                Log.d("checkrewordedads","onAdDismissed  "+targetActivity);
                dismissProgressDialog();
            }

            @Override
            public void onAdFailedToShow() {
                // If ad fails to show, proceed without ad
                openActivity(targetActivity);
                Log.d("checkrewordedads","onAdFailedToShow  "+targetActivity);
                dismissProgressDialog();
            }
        });




//        if (!isInternetAvailable()) {
//            // No internet, proceed directly
//            openActivity(targetActivity);
//            Log.d("checkrewordedads","isInternetAvailable  "+targetActivity);
//        } else if (PrefUtilForAppAdsFree.isPremium(requireActivity()) ||
//                PrefUtilForAppAdsFree.getAdsForLiftTimeString(requireActivity()).equals("ads_free_life_time")) {
//            // User is premium, skip ad
//            openActivity(targetActivity);
//            Log.d("checkrewordedads","PrefUtilForAppAdsFree  "+targetActivity);
//        } else {
//            // Show rewarded ad first, then navigate
//            adManager.showRewardedAdIfAvailable(requireActivity(), adUnitId,progressDialog, new AdManager.AdCallbackRewardedAd() {
//
//
//                @Override
//                public void onAdDismissed() {
//                    // If ad is skipped, still navigate
//                    openActivity(targetActivity);
//                    Log.d("checkrewordedads","onAdDismissed  "+targetActivity);
//                }
//
//                @Override
//                public void onAdFailedToShow() {
//                    // If ad fails to show, proceed without ad
//                    openActivity(targetActivity);
//                    Log.d("checkrewordedads","onAdFailedToShow  "+targetActivity);
//                }
//            });
//        }
    }

    // Helper method to dismiss progress dialog
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void openActivity(String targetActivity) {
//        Intent intent = new Intent(requireActivity(), targetActivity);
//        intent.putExtra("ActivityAction", "FileSearch");
//        startActivityForResult(intent, Merge_Request_CODE);

        if (targetActivity.equals("waterMarkBtn")){
            Intent intent = new Intent(requireActivity(), WaterMarkHomeActivity.class);
            startActivity(intent);
        }else if (targetActivity.equals("mergeBtn")){
            Intent intent = new Intent(requireActivity(), MergeActivity.class);
            startActivity(intent);
        }else if (targetActivity.equals("splitBtn")){
            Intent intent = new Intent(requireActivity(), PdfSplitterActivity.class);
            startActivity(intent);
        }else if (targetActivity.equals("compressionBtn")){
            selectPdfFile();
        }else if (targetActivity.equals("scanBtn")){
            scanner.getStartScanIntent(requireActivity()).addOnSuccessListener(intentSender -> {
                // Create an IntentSenderRequest and launch the scanner
                IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
                scannerLauncher.launch(request);
            }).addOnFailureListener(e -> {
                // Show an error message in case of failure
                Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Scan Error", "Failed to start scan", e);
            });
        }else if (targetActivity.equals("signBtn")){
            Intent intent = new Intent(requireActivity(), DigitalSignatureActivity.class);
            intent.putExtra("ActivityAction", "FileSearch");
            startActivityForResult(intent, Merge_Request_CODE);
        }


    }


    public List<String> getSkusFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SKU_Prefs", Context.MODE_PRIVATE);

        // Retrieve the set of SKUs from SharedPreferences
        Set<String> skuSet = sharedPreferences.getStringSet("skus", new HashSet<String>());

        // Convert the set to a list
        return new ArrayList<>(skuSet);
    }




    private void selectPdfFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_COMPRESS_FILE);

    }

    private void performNextActivityTask() {
        pickImageFromGallery();
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
//        InterstitialAd.load(requireActivity(), getString(R.string.language_intertial_ad), adRequest, new InterstitialAdLoadCallback() {
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
//
//    private void goToNextActivity() {
//
//        pickImageFromGallery();
//
//    }
//
//    private void showAdOrLoad() {
//        if (mInterstitialAd != null) {
//            mInterstitialAd.show(requireActivity());
//        } else if (isAdLoading) {
//            binding.progressBar.setVisibility(View.VISIBLE);
//        } else {
//            goToNextActivity();
//        }
//    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Merge_Request_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                CreateDataSource();
//                mAdapter.notifyItemInserted(items.size() - 1);
            }
        }else if (requestCode == PICK_PDF_COMPRESS_FILE && resultCode == RESULT_OK) {

            if (data != null) {

//                mAdapter.notifyItemInserted(items.size() - 1);

                Uri uri = data.getData();
                selectedFilePath = getFilePathFromUri(requireActivity(),uri);
                Toast.makeText(requireActivity(), "File Selected: " + selectedFilePath, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireActivity(), PdfCompressionActivity.class);
                intent.putExtra("selectedFilePath",selectedFilePath);
                startActivity(intent);


            }
        }
    }



    private void CreateDataSource() {
//        if (recyclerView != null) {
//            items = new ArrayList<File>();

        File root = requireActivity().getFilesDir();
        File myDir = new File(root + "/DigitalSignature");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File[] files = myDir.listFiles();

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                long result = file2.lastModified() - file1.lastModified();
                if (result < 0) {
                    return -1;
                } else if (result > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

    }


    private void pickImageFromGallery() {

        try {
            Intent intent = new Intent(requireActivity(), EditMoudleActivity.class);
            startActivity(intent);
        } catch (Exception e) {
        }




    }

    @Override
    public void setupToolbar(ToolbarController toolbarController) {




        // Set toolbar title
        if (toolbarController != null) {

            homeToolbarTitle = toolbarController.getToolbarTitle();
            homeSelectAllButton = toolbarController.getSelectAllButton();
            homeToolbarSelectedItem = toolbarController.getToolbarSelectedItem();
            homeSearchButton = toolbarController.getSearchButton();
            homeSortButton = toolbarController.getSortButton();
            homeDeleteButton = toolbarController.getDeleteButton();
            homeSearchLayout = toolbarController.getSearchLayout();
            homeToolbar = toolbarController.getToolbar();
            homeSearchBackBtn = toolbarController.getSearchBackBtn();
            homeSearchEditText = toolbarController.getSearchEditText();
            homeClearButton = toolbarController.getClearButton();
            homeSettingButton = toolbarController.getSettingBtn();



            // Set visibility to GONE for each item
            homeToolbarTitle.setVisibility(VISIBLE);
            homeSelectAllButton.setVisibility(View.GONE);
            homeToolbarSelectedItem.setVisibility(View.GONE);
            homeSearchButton.setVisibility(View.GONE);
            homeSortButton.setVisibility(View.GONE);
            homeDeleteButton.setVisibility(View.GONE);
//            homeSearchLayout.setVisibility(View.GONE);
//            homeSearchEditText.setVisibility(View.GONE);
//            homeSearchBackBtn.setVisibility(View.GONE);
            homeClearButton.setVisibility(View.GONE);
            homeSettingButton.setVisibility(View.GONE);

            Log.d("checkfragmentselected","Action Fragment toolbarController != null");



//            homeSettingButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new  Intent(requireActivity(), SettingActivity.class);
//                    startActivity(intent);
//                }
//            });


//            homeToolbarTitle.setText(R.string.docsign_non);
            homeToolbarTitle.setVisibility(VISIBLE);



        }




    }


//    checkTypeIntersial


    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.d("checkfragmentselected","ActionFragment onResume");

                    if (binding.convertPdfBtn.getVisibility() == VISIBLE){
                        Log.d("checkfragmentselected","ActionFragment binding.convertPdfBtn.getVisibility() == VISIBLE");

                        ////            // Set visibility to GONE for each item
                        homeToolbarTitle.setVisibility(VISIBLE);
                        homeSelectAllButton.setVisibility(View.GONE);
                        homeToolbarSelectedItem.setVisibility(View.GONE);
                        homeSearchButton.setVisibility(View.GONE);
                        homeSortButton.setVisibility(View.GONE);
                        homeDeleteButton.setVisibility(View.GONE);
//                        homeSearchLayout.setVisibility(View.GONE);
//                        homeSearchBackBtn.setVisibility(View.GONE);
//                        homeSearchEditText.setVisibility(View.GONE);
                        homeClearButton.setVisibility(View.GONE);
                        homeSettingButton.setVisibility(View.GONE);


                    }else {
                        Log.d("checkfragmentselected","ActionFragment not Visible");
                    }







                } catch (NullPointerException e) {
                }
            }
        },100);







    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("checkfragmentselected11","ActionFragment  onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("checkfragmentselected","ActionFragment onStop");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("checkfragmentselected","ActionFragment onPause");
    }
}