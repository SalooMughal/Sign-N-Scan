package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side.GetFilePathFromUriKt.getFilePathFromUri;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.MergeActivity;
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.PdfCompressionActivity;
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.PdfSplitterActivity;
import com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf.waterMark.WaterMarkHomeActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.SettingActivity;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForPdfTools;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.CamActivity;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.GenerateCodeActivity;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.InterstitialAdManager_Java;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.RewardedAdManager;
import com.pixelz360.docsign.imagetopdf.creator.databinding.FragmentActionBinding;
import com.pixelz360.docsign.imagetopdf.creator.databinding.FragmentPrrmiumBinding;
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
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PremiumFragment extends Fragment implements ToolbarSettings {


    FragmentPrrmiumBinding binding;


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





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         binding = FragmentPrrmiumBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



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
//            homeToolbarTitle.setVisibility(View.VISIBLE);
//            homeSelectAllButton.setVisibility(View.GONE);
//            homeToolbarSelectedItem.setVisibility(View.GONE);
//            homeSearchButton.setVisibility(View.GONE);
//            homeSortButton.setVisibility(View.GONE);
//            homeDeleteButton.setVisibility(View.GONE);
//            homeSearchLayout.setVisibility(View.GONE);
//            homeSearchBackBtn.setVisibility(View.GONE);
//            homeSearchEditText.setVisibility(View.GONE);
//            homeClearButton.setVisibility(View.GONE);
//            homeSettingButton.setVisibility(View.VISIBLE);

            Log.d("checkfragmentselected","toolbarController != null");



            homeSettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new  Intent(requireActivity(), SettingActivity.class);
                    startActivity(intent);
                }
            });


//            homeToolbarTitle.setText(R.string.docsign);



        }




    }


//    checkTypeIntersial


    @Override
    public void onResume() {
        super.onResume();

//        try {
//
//            Log.d("checkfragmentselected","ActionFragment onResume");
//
//////            // Set visibility to GONE for each item
//            homeToolbarTitle.setVisibility(View.VISIBLE);
//            homeSelectAllButton.setVisibility(View.GONE);
//            homeToolbarSelectedItem.setVisibility(View.GONE);
//            homeSearchButton.setVisibility(View.GONE);
//            homeSortButton.setVisibility(View.GONE);
//            homeDeleteButton.setVisibility(View.GONE);
//            homeSearchLayout.setVisibility(View.GONE);
//            homeSearchBackBtn.setVisibility(View.GONE);
//            homeSearchEditText.setVisibility(View.GONE);
//            homeClearButton.setVisibility(View.GONE);
//            homeSettingButton.setVisibility(View.VISIBLE);
//
//
//        } catch (NullPointerException e) {
//        }



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