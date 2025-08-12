package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments;


import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.itextpdf.text.pdf.PdfReader;
import com.pixelz360.docsign.imagetopdf.creator.NavigationLayoutProvider;
import com.pixelz360.docsign.imagetopdf.creator.PdfViewActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.RvListenerPdfRoom;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters.MargePdfAdapterRoom;
import com.pixelz360.docsign.imagetopdf.creator.databinding.FragmentMargeBinding;
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper;
import com.pixelz360.docsign.imagetopdf.creator.pdfToJpg.JpgViewActivity;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecentFragment extends Fragment implements ToolbarSettings {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 111;
    private AppUpdateManager appUpdateManager;
    private static final int IMMEDIATE_UPDATE_REQUEST_CODE = 100;
    private ActivityResultLauncher<IntentSenderRequest> updateActivityResultLauncher;
    private MargePdfAdapterRoom pdfAdapter;
    private PdfFileViewModel pdfFileViewModel;
    List<PdfFile> pdfFiles;
    private boolean bannerAdIsLoaded;
    private boolean isFileValieOrNo = false;
    private static final String BANNER_AD_ISCHECKED = "banner_ad_checked";
    private static final String ARG_LAYOUT_ID = "layout_id";
    private static final String NAVIGATION_LAYOUT_ID = "navigation_layout_id";
    private boolean singleItemsSelected = false;
    private boolean allItemsSelected = false;
    private FragmentMargeBinding binding;
    private NavigationLayoutProvider navigationLayoutProvider;
    int frameLayout;

    public static RecentFragment newInstance(int layoutId, boolean bannerAdIsLoaded) {
        RecentFragment fragment = new RecentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        args.putBoolean(BANNER_AD_ISCHECKED, bannerAdIsLoaded);
        fragment.setArguments(args);
        return fragment;
    }


//    ToolbarController toolbarController;
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//
//        if (context instanceof ToolbarController) {
//            toolbarController = (ToolbarController) context;
//        } else {
//            throw new ClassCastException("Activity must implement ToolbarController");
//        }
//
//
////        if (context instanceof NavigationLayoutProvider) {
////            navigationLayoutProvider = (NavigationLayoutProvider) context;
////        } else {
////            throw new RuntimeException(context.toString()
////                    + " must implement NavigationLayoutProvider");
////        }
//    }


    //Home Activity Below Elements


    ConstraintLayout homeToolbarTitle;
    TextView homeSelectAllButton;
    TextView homeToolbarSelectedItem;
    ImageButton homeSearchButton;
    ImageButton homeSortButton;
    ImageButton homeFileListOrGridButtonButton;
    ImageButton homeDeleteButton;
    LinearLayout homeSearchLayout;
    Toolbar homeToolbar;
    ImageView homeSearchBackBtn;
    ImageView homeClearButton;
    EditText homeSearchEditText;
    ImageButton homeSettingButton;
    LinearLayout homeBannerAdsLayout;
    LinearLayout homeNavigationLayout;

    RelativeLayout hometabsMainLayout;


    String receivedStringHomeScreenBackPress = "not";


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("com.example.MY_CUSTOM_ACTION_RecentFragment")) {
                receivedStringHomeScreenBackPress = intent.getStringExtra("onBackPressed_RecentFragment");
//                Toast.makeText(context, "Received in Fragment: " + receivedStringHomeScreenBackPress, Toast.LENGTH_SHORT).show();
                hideSearchLayout();

            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMargeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        IntentFilter filter = new IntentFilter("com.example.MY_CUSTOM_ACTION_RecentFragment");
        ContextCompat.registerReceiver(requireActivity(), myReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);

        pdfFileViewModel = new ViewModelProvider(this).get(PdfFileViewModel.class);

        if (getArguments() != null) {
            frameLayout = getArguments().getInt(ARG_LAYOUT_ID);
            bannerAdIsLoaded = getArguments().getBoolean(BANNER_AD_ISCHECKED);
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }

        appUpdateManager = AppUpdateManagerFactory.create(requireActivity());

        updateActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                    if (result.getResultCode() != RESULT_OK) {
                        Log.e("checkupdate", "Update flow failed! Result code: " + result.getResultCode());
                    }
                });



        // Log a predefined event
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance( requireActivity());
        Bundle bundle = new Bundle();
        bundle.putString("activity_name", "RoomListFragment");
        analytics.logEvent("activity_created", bundle);
// Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "RoomListFragment");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        binding.lottieAnimationView.loop(false);


      // Observe LiveData for PDF files
//        pdfFileViewModel.getIdRecentPdfFiles().observe(getViewLifecycleOwner(), pdfFileList -> {
            pdfFileViewModel.getRecentPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(requireActivity()),"pdf").observe(getViewLifecycleOwner(), pdfFileList -> {
            pdfFiles = pdfFileList;



            boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());

            setupRecyclerView();
            updateRecyclerView(pdfFileList, isCheckedListOrGrid);

        });



//        binding.allSelctedItemBtn.setOnClickListener(v -> handleSelectAllItems());
//        binding.deleteBtn.setOnClickListener(v -> showDeleteDialog());
//        binding.clearButton.setOnClickListener(v -> clearSelection());
//        binding.searchBtn.setOnClickListener(v -> showSearchLayout());
//        binding.searchBackBtn.setOnClickListener(v -> hideSearchLayout());



//        binding.fileSortingBtn.setOnClickListener(v -> showSortingDialog());

//        binding.searchEditText.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable editable) {
//                filter(editable.toString());
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            public void onTextChanged(CharSequence query, int start, int before, int count) {
//                filter(query.toString());
//            }
//        });







    }



    private void setupRecyclerView() {
//        binding.pdfRv.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        binding.pdfRv.setHasFixedSize(true);
//        binding.pdfRv.setItemViewCacheSize(20);

        binding.pdfRv.setHasFixedSize(true);
        binding.pdfRv.setItemViewCacheSize(20);
        binding.pdfRv.setDrawingCacheEnabled(true);
        binding.pdfRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());

        if (isCheckedListOrGrid){

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireActivity(), 3);


//            layoutManager.setInitialPrefetchItemCount(4);  // Prefetch the first few items
            binding.pdfRv.setLayoutManager(layoutManager);
            ListGridItemStorage.setMargins(binding.pdfRv, 28, 0, 0, 8);


            Log.d("checkview","GridLayoutManager Fragment side");

        }else {



            LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());

            layoutManager.setInitialPrefetchItemCount(4);  // Prefetch the first few items
            binding.pdfRv.setLayoutManager(layoutManager);
            ListGridItemStorage.setMargins(binding.pdfRv, 0, 0, 0, 8);

            Log.d("checkview","LinearLayout Fragment side");
        }




//        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
//        layoutManager.setInitialPrefetchItemCount(4);  // Prefetch the first few items
//        binding.pdfRv.setLayoutManager(layoutManager);

    }

    private void updateRecyclerView(List<PdfFile> pdfFileList, boolean isCheckedListOrGrid) {
        if (pdfFileList.isEmpty()) {
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.pdfRv.setVisibility(View.GONE);
            isFileValieOrNo = false;

//            homeSortButton.setVisibility(View.GONE);
//            homeSearchButton.setVisibility(View.GONE);
//            homeSelectAllButton.setVisibility(View.GONE);

        } else {
            binding.emptyLayout.setVisibility(View.GONE);
            binding.pdfRv.setVisibility(View.VISIBLE);
            isFileValieOrNo = true;

            binding.allCountFiles.setText(pdfFileList.size() + " Files");


//            homeSortButton.setVisibility(View.VISIBLE);
//            homeSearchButton.setVisibility(View.VISIBLE);
//            homeSelectAllButton.setVisibility(View.VISIBLE);


            Log.d("checkfiles","Split File size   "+pdfFileList.size());


            pdfAdapter = new MargePdfAdapterRoom(pdfFileList, requireActivity(),isCheckedListOrGrid, new RvListenerPdfRoom() {
                @Override
                public void onPdfClick(PdfFile pdfFile, int position) {
                    if (pdfFile.password != null && !pdfFile.password.isEmpty()) {
                        ViewPasswordDialog alert = new ViewPasswordDialog(pdfFile);
                        alert.showDialog(requireActivity());
                    } else {

                        String extension = pdfFile.filePath.substring(pdfFile.filePath.lastIndexOf("."));
                        Log.d("checkimagepath"+"extension  ",extension);
                        if (extension.equals(".jpg")){
                            openJpgFile(pdfFile.filePath);
                        }else if (extension.equals(".pdf")){
                            openPdfFile(pdfFile.filePath,pdfFile);

                        }


                    }
                }

                @Override
                public void onItemSelectionChanged() {
                    requireActivity().invalidateOptionsMenu();
                }

                @Override
                public void onItemSelectionChangedCount(int selectionCount) {
                    updateToolbarTitle(selectionCount);
                }

                @Override
                public void onPdfMoreClick(PdfFile pdfFile, int position) {
                    FileUtils.threeDotDailog(pdfFile, requireActivity(), pdfFileViewModel);

                }

                @Override
                public void onFavoriteClick(PdfFile pdfFile) {
                    pdfFileViewModel.updatePdfFile(pdfFile);

                    // Notify the adapter to update the UI for this item
                    int position = pdfFiles.indexOf(pdfFile);
                    if (position != -1) {
                        pdfAdapter.notifyItemChanged(position);
                    }

                }
            });
            binding.pdfRv.setAdapter(pdfAdapter);
        }
    }

    private void openJpgFile(String filePath) {
        Intent intent = new Intent(requireActivity(), JpgViewActivity.class);
        intent.putExtra("jpgfilePath", filePath);
        startActivity(intent);
    }

    private void filter(String text) {
        if (pdfFiles == null || pdfFiles.isEmpty()) return;

        ArrayList<PdfFile> filteredNames = new ArrayList<>();
        for (PdfFile s : pdfFiles) {
            if (s.filePath != null) {
                File file = new File(s.filePath);
                String filename = file.getName();
                if (filename.toLowerCase().contains(text.toLowerCase())) {
                    filteredNames.add(s);
                }
            }
        }
        pdfAdapter.filterList(filteredNames);


        if (text.isEmpty()){
            binding.pdfRv.setVisibility(GONE);

        }else {
            binding.pdfRv.setVisibility(VISIBLE);

        }

    }



    //Home Activity Interface

    @Override
    public void setupToolbar(ToolbarController toolbarController) {



            // Set toolbar title
            if (toolbarController != null) {

                homeToolbarTitle = toolbarController.getToolbarTitle();
                homeSelectAllButton = toolbarController.getSelectAllButton();
                homeToolbarSelectedItem = toolbarController.getToolbarSelectedItem();
                homeSearchButton = toolbarController.getSearchButton();
                homeSortButton = toolbarController.getSortButton();
                homeFileListOrGridButtonButton = toolbarController.getfileListOrGridButton();
                homeDeleteButton = toolbarController.getDeleteButton();
                homeSearchLayout = toolbarController.getSearchLayout();
                homeToolbar = toolbarController.getToolbar();
                homeSearchBackBtn = toolbarController.getSearchBackBtn();
                homeSearchEditText = toolbarController.getSearchEditText();
                homeClearButton = toolbarController.getClearButton();
                homeSettingButton = toolbarController.getSettingBtn();
                homeBannerAdsLayout = toolbarController.getBannerAdsLayout();
                homeNavigationLayout = toolbarController.getnavigationLayout();
                hometabsMainLayout = toolbarController.gettabsMainLayout();

                if (isAdded()) {
                    boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());
                    if (isCheckedListOrGrid){
                        homeFileListOrGridButtonButton.setImageResource(R.drawable.grid_view_icon);
                    }else {
                        homeFileListOrGridButtonButton.setImageResource(R.drawable.list_view_icon);
                    }
                } else {
                    Log.e("RecentFragment", "Fragment is not attached to an activity.");
                }




                // Observe LiveData for PDF files
//                pdfFileViewModel.getIdRecentPdfFiles().observe(this, pdfFileList -> {
//
//                    if (pdfFileList.isEmpty()){
//                        Log.d("checkfragmentselected"," RecentFragment is Empty");
//
//
//                        homeSortButton.setVisibility(View.GONE);
//                        homeSearchButton.setVisibility(View.GONE);
//                        homeSelectAllButton.setVisibility(View.GONE);
//
//                    }else {
//                        Log.d("checkfragmentselected","RecentFragment not empty");
//
//                        homeSortButton.setVisibility(View.VISIBLE);
//                        homeSearchButton.setVisibility(View.VISIBLE);
//                        homeSelectAllButton.setVisibility(View.VISIBLE);
//
//                    }
//
//                });



                Log.d("checkfragmentselected"," 2 toolbarController != null");



                // Set visibility to GONE for each item
//            homeToolbarTitle.setVisibility(View.VISIBLE);
//            homeSelectAllButton.setVisibility(View.VISIBLE);
//            homeToolbarSelectedItem.setVisibility(View.GONE);
//            homeSearchButton.setVisibility(View.VISIBLE);
//            homeSortButton.setVisibility(View.VISIBLE);
//            homeDeleteButton.setVisibility(View.GONE);
//            homeSearchLayout.setVisibility(View.GONE);
//            homeToolbar.setVisibility(View.VISIBLE);
//            homeSearchBackBtn.setVisibility(View.GONE);
//            homeSearchEditText.setVisibility(View.GONE);
//            homeClearButton.setVisibility(View.GONE);





                if (pdfAdapter!=null){
                    clearSelection();
                }





                homeClearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearSelection();
                    }
                });

                homeDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog();
                    }
                });

                homeSearchBackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        hideSearchLayout();

                    }
                });

                homeSearchEditText.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable editable) {
                        filter(editable.toString());
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence query, int start, int before, int count) {
                        filter(query.toString());
                    }
                });



                // Set up click listeners for toolbar buttons
                homeSearchButton.setOnClickListener(v -> {
                    // Handle search action specific to RoomListFragment
                    Log.d("checktoolbar","search icon click One");
                    showSearchLayout();

                });

                homeSortButton.setOnClickListener(v -> {
                    // Handle sort action specific to RoomListFragment
                    showSortingDialog();
                });

                homeFileListOrGridButtonButton.setOnClickListener(v -> {
                    // Handle sort action specific to RoomListFragment
//                    showSetListOrGridViewItemDialog();

                    //Open popup window
                    showPopup(requireActivity(),v);
                });


                homeSelectAllButton.setOnClickListener(v -> {
                    // Handle select all action specific to RoomListFragment


                    if (!isFileValieOrNo){
                        Log.d("checkfileyet","not File Avalibele 1");
                        Toast.makeText(requireActivity(),"No files selected",Toast.LENGTH_SHORT).show();


                    }else {
                        Log.d("checkfileyet","File Avalibele 1");



                        if (singleItemsSelected) {
                            pdfAdapter.unselectAllItems();
                            singleItemsSelected = false;

                            allItemsSelected = false;

                            homeSelectAllButton.setText("Select");



                            Toast.makeText(requireActivity(),"No File to Select",Toast.LENGTH_SHORT).show();

                            homeSelectAllButton.setBackground(getResources().getDrawable(R.drawable.btn_un_select_bg));
                            homeSelectAllButton.setTextColor(ContextCompat.getColor(requireActivity(), R.color.select_text_un_selected));

                        } else {

                            if (homeSelectAllButton.getText().equals("Cancel")){
                                Log.d("checkfileyet111", "click DeAll items selected successfully");

                                pdfAdapter.unselectAllItems();
                                homeSelectAllButton.setBackground(getResources().getDrawable(R.drawable.btn_un_select_bg));
                                homeSelectAllButton.setTextColor(ContextCompat.getColor(requireActivity(), R.color.select_text_un_selected));

                                singleItemsSelected = false; // To track the selection state

                                allItemsSelected = false; // To track the selection state


                                homeSelectAllButton.setText("Select");


                            }else{
//                                pdfAdapter.selectAllItems("Single All items selected successfully");
                                pdfAdapter.selectAllItems("Single All items selected successfully");

                                Log.d("checkfileyet111", "else singleItemsSelected  "+singleItemsSelected);

                                homeSelectAllButton.setText("Cancel");
                                Toast.makeText(requireActivity(), "All items selected successfully", Toast.LENGTH_SHORT).show();
                                homeSelectAllButton.setBackground(getResources().getDrawable(R.drawable.btn_select_bg));
                                homeSelectAllButton.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));

                                allItemsSelected = false;

//                                if (!allItemsSelected) {
//
//                                    Log.d("checkfileyet111", "if allItemsSelected  "+allItemsSelected);
//
//                                    allItemsSelected = true;
//
//                                } else {
//                                    pdfAdapter.selectAllItems("All items selected successfully");
//                                    allItemsSelected = false;
//                                    homeSelectAllButton.setText("DeAll items selected successfully");
//
//                                    Log.d("checkfileyet111", "else allItemsSelected  "+allItemsSelected);
//
//                                }
                            }

                        }



                    }


                });



            }







    }

    private void showPopup(FragmentActivity fragmentActivity, View anchorView) {
        // Create a PopupWindow instance
        PopupWindow popup = new PopupWindow(requireActivity());
        View layout = getLayoutInflater().inflate(R.layout.file_list_or_grid_layout, null);
        popup.setContentView(layout);

        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        // Close the popup window when touch outside of it
        popup.setOutsideTouchable(true);
        popup.setFocusable(false);

        // Set the background of the popup to be transparent (if needed)
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Show the popup anchored to the anchorView (button or any view)
        popup.showAsDropDown(anchorView);

        // Create and add a gray overlay to the activity layout
        final View grayOverlay = new View(requireActivity());
        grayOverlay.setBackgroundColor(getResources().getColor(R.color.black)); // Light gray
        grayOverlay.setAlpha(0.5f); // Set the transparency of the overlay (0.0f to 1.0f)
        grayOverlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Save the original background before applying overlay
        ViewGroup rootView = requireActivity().findViewById(android.R.id.content);
        Drawable originalBackgroundDrawable = rootView.getBackground();

        // If the background is null, set a default fallback color
        int originalBackgroundColor = Color.WHITE; // Default color
        if (originalBackgroundDrawable instanceof ColorDrawable) {
            originalBackgroundColor = ((ColorDrawable) originalBackgroundDrawable).getColor();
        } else {
            originalBackgroundColor = Color.TRANSPARENT;
        }

        // Add overlay to root view
        rootView.addView(grayOverlay);

        // Handle popup dismiss to restore background
        int finalOriginalBackgroundColor = originalBackgroundColor;
        popup.setOnDismissListener(() -> restoreBackgroundColor(rootView, finalOriginalBackgroundColor, grayOverlay));

        // Button click handlers
        RelativeLayout setListViewBtn = layout.findViewById(R.id.setListViewBtn);
        RelativeLayout setGridViewBtn = layout.findViewById(R.id.setGridViewBtn);
        LinearLayout mainLayout = layout.findViewById(R.id.mainLayout);

        ListGridItemStorage.setMargins(mainLayout, 0, 0, 130, 0);

        setListViewBtn.setOnClickListener(v -> {
            ListGridItemStorage.setListOrGrid(requireActivity(), false);
            observePdfFiles();
            popup.dismiss(); // Automatically restores background on dismiss
        });

        setGridViewBtn.setOnClickListener(v -> {
            ListGridItemStorage.setListOrGrid(requireActivity(), true);
            observePdfFiles();
            popup.dismiss(); // Automatically restores background on dismiss
        });
    }

    /**
     * Restore the original background color when the popup is dismissed.
     */
    private void restoreBackgroundColor(ViewGroup rootView, int originalBackgroundColor, View overlay) {
        rootView.removeView(overlay); // Remove the gray overlay
        rootView.setBackgroundColor(originalBackgroundColor); // Restore original background
    }

//    private void restoreBackgroundColor(ViewGroup rootView, int originalBackgroundColor, View grayOverlay) {
//        // Remove the gray overlay when the popup is dismissed
//        rootView.removeView(grayOverlay);
//
//        // Restore the original background color of the root view
//        rootView.setBackgroundColor(originalBackgroundColor);
//
//
//
//    }

    private void observePdfFiles() {
        // Observe LiveData for PDF files
        pdfFileViewModel.getPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(requireActivity()),"pdf")
                .observe(getViewLifecycleOwner(), pdfFileList -> {
                    pdfFiles = pdfFileList;

                    boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());

                    setupRecyclerView();
                    updateRecyclerView(pdfFileList, isCheckedListOrGrid);

                    // Update the button icon based on the view type (list or grid)
                    if (isCheckedListOrGrid) {
                        homeFileListOrGridButtonButton.setImageResource(R.drawable.grid_view_icon);
                    } else {
                        homeFileListOrGridButtonButton.setImageResource(R.drawable.list_view_icon);
                    }
                });
    }



//    private void showPopup(FragmentActivity fragmentActivity, View anchorView) {
//
//
//        PopupWindow popup = new PopupWindow(requireActivity());
//        View layout = getLayoutInflater().inflate(R.layout.file_list_or_grid_layout, null);
//        popup.setContentView(layout);
//        // Set content width and height
//        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
//        // Closes the popup window when touch outside of it - when looses focus
//        popup.setOutsideTouchable(true);
//        popup.setFocusable(true);
//        // Show anchored to button
//        popup.setBackgroundDrawable(new BitmapDrawable());
//        popup.showAsDropDown(anchorView);
//
//
//        RelativeLayout setListViewBtn = layout.findViewById(R.id.setListViewBtn);
//        RelativeLayout setGridViewBtn = layout.findViewById(R.id.setGridViewBtn);
//
//        LinearLayout mainLayout = layout.findViewById(R.id.mainLayout);
//
//        ListGridItemStorage.setMargins(mainLayout, 0, 0, 130, 0);
//
//        setListViewBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//                ListGridItemStorage.setListOrGrid(requireActivity(),false);
//
//
//
//
//
//
//                // Observe LiveData for PDF files
////                pdfFileViewModel.getIdRecentPdfFiles().observe(getViewLifecycleOwner(), pdfFileList -> {
//                    pdfFileViewModel.getRecentPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(requireActivity())).observe(getViewLifecycleOwner(), pdfFileList -> {
//                    pdfFiles = pdfFileList;
//
//                    boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());
//
//                    setupRecyclerView();
//                    updateRecyclerView(pdfFileList,isCheckedListOrGrid);
//
////
//                    if (isCheckedListOrGrid){
//                        homeFileListOrGridButtonButton.setImageResource(R.drawable.grid_view_icon);
//                    }else {
//                        homeFileListOrGridButtonButton.setImageResource(R.drawable.list_view_icon);
//                    }
//
//
//
//                });
//
//
//                popup.dismiss();
//
//            }
//        });
//
//        setGridViewBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//                ListGridItemStorage.setListOrGrid(requireActivity(),true);
//
//
//
//
//
//
//                // Observe LiveData for PDF files
////                pdfFileViewModel.getIdRecentPdfFiles().observe(getViewLifecycleOwner(), pdfFileList -> {
//                    pdfFileViewModel.getRecentPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(requireActivity())).observe(getViewLifecycleOwner(), pdfFileList -> {
//                    pdfFiles = pdfFileList;
//
//                    boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());
//
//                    setupRecyclerView();
//                    updateRecyclerView(pdfFileList,isCheckedListOrGrid);
//
//                    if (isCheckedListOrGrid){
//                        homeFileListOrGridButtonButton.setImageResource(R.drawable.grid_view_icon);
//                    }else {
//                        homeFileListOrGridButtonButton.setImageResource(R.drawable.list_view_icon);
//                    }
//
//
//
//                });
//                popup.dismiss();
//
//            }
//        });
//
//
//
//    }

    private void showSetListOrGridViewItemDialog() {
        Dialog dialog = new Dialog(requireActivity(), R.style.FileSortingDialogStyle);
        dialog.setContentView(R.layout.file_list_or_grid_layout);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        RelativeLayout setListViewBtn = dialog.findViewById(R.id.setListViewBtn);
        RelativeLayout setGridViewBtn = dialog.findViewById(R.id.setGridViewBtn);

        setListViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ListGridItemStorage.setListOrGrid(requireActivity(),false);






                // Observe LiveData for PDF files
//                pdfFileViewModel.getIdRecentPdfFiles().observe(getViewLifecycleOwner(), pdfFileList -> {
                    pdfFileViewModel.getRecentPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(requireActivity()),"pdf").observe(getViewLifecycleOwner(), pdfFileList -> {
                    pdfFiles = pdfFileList;

                    boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());

                    setupRecyclerView();
                    updateRecyclerView(pdfFileList,isCheckedListOrGrid);

//
                    if (isCheckedListOrGrid){
                        homeFileListOrGridButtonButton.setImageResource(R.drawable.grid_view_icon);
                    }else {
                        homeFileListOrGridButtonButton.setImageResource(R.drawable.list_view_icon);
                    }



                });


                dialog.dismiss();

            }
        });

        setGridViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ListGridItemStorage.setListOrGrid(requireActivity(),true);






                // Observe LiveData for PDF files
//                pdfFileViewModel.getIdRecentPdfFiles().observe(getViewLifecycleOwner(), pdfFileList -> {
                    pdfFileViewModel.getRecentPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(requireActivity()),"pdf").observe(getViewLifecycleOwner(), pdfFileList -> {
                    pdfFiles = pdfFileList;

                    boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());

                    setupRecyclerView();
                    updateRecyclerView(pdfFileList,isCheckedListOrGrid);

                    if (isCheckedListOrGrid){
                        homeFileListOrGridButtonButton.setImageResource(R.drawable.grid_view_icon);
                    }else {
                        homeFileListOrGridButtonButton.setImageResource(R.drawable.list_view_icon);
                    }



                });
                dialog.dismiss();

            }
        });



        dialog.show();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d("checkselected", "onCreateOptionsMenu");
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        try {
            boolean hasSelectedItems = pdfAdapter.getSelectedItems().size() > 0;
            Log.d("checkselected", "onPrepareOptionsMenu   " + hasSelectedItems);
            if (hasSelectedItems) {
//                binding.deleteBtn.setVisibility(View.VISIBLE);
//                binding.toolbarSelectedItem.setVisibility(View.VISIBLE);
//                binding.clearButton.setVisibility(View.VISIBLE);
//                binding.searchBtn.setVisibility(View.GONE);
//                binding.toolbarTitle.setVisibility(View.GONE);
//                binding.fileSortingBtn.setVisibility(View.GONE);
                binding.childLayout.setVisibility(View.GONE);


                // Home Activity elements blow

                homeDeleteButton.setVisibility(View.VISIBLE);
                        homeToolbarSelectedItem.setVisibility(View.VISIBLE);
                homeClearButton.setVisibility(View.VISIBLE);
                homeSearchButton.setVisibility(View.GONE);
                        homeToolbarTitle.setVisibility(View.GONE);
                homeSortButton.setVisibility(View.GONE);
                homeFileListOrGridButtonButton.setVisibility(View.GONE);







            } else {
//                binding.deleteBtn.setVisibility(View.GONE);
//                binding.toolbarSelectedItem.setVisibility(View.GONE);
//                binding.clearButton.setVisibility(View.GONE);
//                binding.searchBtn.setVisibility(View.VISIBLE);
//                binding.toolbarTitle.setVisibility(View.VISIBLE);
//                binding.fileSortingBtn.setVisibility(View.VISIBLE);
                binding.childLayout.setVisibility(View.VISIBLE);




                // Home Activity elements blow

                homeDeleteButton.setVisibility(View.GONE);
                homeToolbarSelectedItem.setVisibility(View.GONE);
                homeClearButton.setVisibility(View.GONE);
                homeSearchButton.setVisibility(View.VISIBLE);
                homeToolbarTitle.setVisibility(View.VISIBLE);
                homeSortButton.setVisibility(View.VISIBLE);
                homeFileListOrGridButtonButton.setVisibility(View.VISIBLE);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            onDeleteSelectedItems();
            Log.d("checkselected", "onOptionsItemSelected Click");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void handleSelectAllItems() {
//
//        if (!isFileValieOrNo){
//            Log.d("checkfileyet","not File Avalibele 1");
//            Toast.makeText(requireActivity(),"No files selected",Toast.LENGTH_SHORT).show();
//
//
//        }else {
//            Log.d("checkfileyet","File Avalibele 1");
//
//
//
//            if (singleItemsSelected) {
//                pdfAdapter.unselectAllItems();
//                singleItemsSelected = false;
//
//                allItemsSelected = false;
//
//                binding.allSelctedItemBtn.setText("Select");
//
//
//
//                Toast.makeText(requireActivity(),"No File to Select",Toast.LENGTH_SHORT).show();
//
//                binding.allSelctedItemBtn.setBackground(getResources().getDrawable(R.drawable.btn_un_select_bg));
//                binding.allSelctedItemBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.select_text_un_selected));
//
//            } else {
//
//                if (binding.allSelctedItemBtn.getText().equals("DeAll items selected successfully")){
//                    Log.d("checkfileyet111", "click DeAll items selected successfully");
//
//                    pdfAdapter.unselectAllItems();
//                    binding.allSelctedItemBtn.setBackground(getResources().getDrawable(R.drawable.btn_un_select_bg));
//                    binding.allSelctedItemBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.select_text_un_selected));
//
//                    singleItemsSelected = false; // To track the selection state
//
//                    allItemsSelected = false; // To track the selection state
//
//
//                    binding.allSelctedItemBtn.setText("Select");
//
//
//                }else{
//                    pdfAdapter.selectAllItems("Single All items selected successfully");
//
//                    Log.d("checkfileyet111", "else singleItemsSelected  "+singleItemsSelected);
//
//                    binding.allSelctedItemBtn.setText("All items selected successfully");
//                    Toast.makeText(requireActivity(), "All items selected successfully", Toast.LENGTH_SHORT).show();
//                    binding.allSelctedItemBtn.setBackground(getResources().getDrawable(R.drawable.btn_select_bg));
//                    binding.allSelctedItemBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
//
//                    if (!allItemsSelected) {
//
//                        Log.d("checkfileyet111", "if allItemsSelected  "+allItemsSelected);
//
//                        allItemsSelected = true;
//
//                    } else {
//                        pdfAdapter.selectAllItems("All items selected successfully");
//                        allItemsSelected = false;
//                        binding.allSelctedItemBtn.setText("DeAll items selected successfully");
//
//                        Log.d("checkfileyet111", "else allItemsSelected  "+allItemsSelected);
//
//                    }
//                }
//
//            }
//
//
//
//        }
//
//
//    }

    private void clearSelection() {
        Activity activity = getActivity();

        if(activity!= null && isAdded()){
            pdfAdapter.clearSelection();
            requireActivity().invalidateOptionsMenu();
//            binding.allSelctedItemBtn.setBackground(getResources().getDrawable(R.drawable.btn_un_select_bg));
//            binding.allSelctedItemBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.select_text_un_selected));
            singleItemsSelected = false;
            allItemsSelected = false;
//            binding.allSelctedItemBtn.setText("Select");


            //Home Activity Elements
            homeSelectAllButton.setBackground(getResources().getDrawable(R.drawable.btn_un_select_bg));
            homeSelectAllButton.setTextColor(ContextCompat.getColor(requireActivity(), R.color.select_text_un_selected));
            singleItemsSelected = false;
            allItemsSelected = false;
            homeSelectAllButton.setText("Select");
            homeSearchEditText.setText("");



            homeSettingButton.setVisibility(View.GONE);
            homeSelectAllButton.setVisibility(View.VISIBLE);
            homeSearchButton.setVisibility(View.VISIBLE);
            homeSortButton.setVisibility(View.VISIBLE);
            homeFileListOrGridButtonButton.setVisibility(View.VISIBLE);
            homeToolbarTitle.setVisibility(View.VISIBLE);

            hideSearchLayout();

            homeDeleteButton.setVisibility(View.GONE);
            homeClearButton.setVisibility(View.GONE);
            homeToolbarSelectedItem.setVisibility(View.GONE);

//            homeToolbarTitle.setText(getResources().getString(R.string.docsign_non));
            homeToolbarTitle.setVisibility(View.VISIBLE);
        }



    }

    private void showDeleteDialog() {
        Dialog dialog = new Dialog(requireActivity(), R.style.renameDialogStyle);
        dialog.setContentView(R.layout.delete_dailog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int desiredWidth = screenWidth - 2 * dpToPx(requireActivity(), 30);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = desiredWidth;
            window.setAttributes(params);
        }

        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        TextView deleteBtn = dialog.findViewById(R.id.deleteBtn);

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        deleteBtn.setOnClickListener(v -> {
            onDeleteSelectedItems();
            dialog.dismiss();
        });
        dialog.show();
    }

    private int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    private void onDeleteSelectedItems() {
        List<PdfFile> selectedFiles = pdfAdapter.getSelectedFiles();
        for (PdfFile pdfFile : selectedFiles) {
            File file = new File(pdfFile.filePath);
            if (file.exists() && file.delete()) {
                pdfFileViewModel.deletePdfFile(pdfFile);
                Toast.makeText(requireActivity(), "PDF file deleted successfully", Toast.LENGTH_SHORT).show();

                clearSelection();

            } else {
                Log.d("Delete Operation", "Failed to delete file: " + file.getPath());
            }
        }
        pdfAdapter.clearSelection();
        getActivity().invalidateOptionsMenu();
//        binding.toolbarTitle.setText(getResources().getString(R.string.docsign));



        //Home Activity Elment
//        homeToolbarTitle.setText(getResources().getString(R.string.docsign_non));

        homeToolbarTitle.setVisibility(View.VISIBLE);

    }

    private void showSortingDialog() {
        Dialog dialog = new Dialog(requireActivity(), R.style.FileSortingDialogStyle);
        dialog.setContentView(R.layout.file_sorting_sheet_layout);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        RelativeLayout createdDateBtn = dialog.findViewById(R.id.createdDateBtn);
        RelativeLayout fileSizeBtn = dialog.findViewById(R.id.fileSizeBtn);
        RelativeLayout ascendingBtn = dialog.findViewById(R.id.ascendingBtn);
        RelativeLayout decendingBtn = dialog.findViewById(R.id.decendingBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        TextView doneBtn = dialog.findViewById(R.id.doneBtn);

        final String[] fileSortingName = {"FileSize"};
        final int[] getAscandingDescending = {1};

        fileSizeBtn.setOnClickListener(v -> {
            fileSortingName[0] = "FileSize";
            createdDateBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
            fileSizeBtn.setBackground(getResources().getDrawable(R.drawable.selected_main_icon_bg));
            decendingBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
            ascendingBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
        });

        createdDateBtn.setOnClickListener(v -> {
            fileSortingName[0] = "FileDate";
            createdDateBtn.setBackground(getResources().getDrawable(R.drawable.selected_main_icon_bg));
            fileSizeBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
            decendingBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
            ascendingBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
        });

        ascendingBtn.setOnClickListener(v -> {
            if (fileSortingName[0].equals("FileSize")) {
                getAscandingDescending[0] = 0;
            } else if (fileSortingName[0].equals("FileDate")) {
                getAscandingDescending[0] = 3;
            }
            decendingBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
            ascendingBtn.setBackground(getResources().getDrawable(R.drawable.selected_main_icon_bg));
        });

        decendingBtn.setOnClickListener(v -> {
            if (fileSortingName[0].equals("FileSize")) {
                getAscandingDescending[0] = 1;
            } else if (fileSortingName[0].equals("FileDate")) {
                getAscandingDescending[0] = 2;
            }
            decendingBtn.setBackground(getResources().getDrawable(R.drawable.selected_main_icon_bg));
            ascendingBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
        });

        doneBtn.setOnClickListener(v -> {
//            pdfFileViewModel.loadSortedPdfFiles(getAscandingDescending[0]);
//            pdfFileViewModel.getSortedPdfFiles().observe(getViewLifecycleOwner(), pdfFileList -> {
            pdfFileViewModel.getAscendingDescendingByUserType(AccountsOrGuesHelper.checkAccountOrNot(requireActivity()),getAscandingDescending[0]).observe(getViewLifecycleOwner(), pdfFileList -> {

                pdfFiles = pdfFileList;

                boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());


                updateRecyclerView(pdfFileList, isCheckedListOrGrid);
            });
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showSearchLayout() {
//        binding.searchLayout.setVisibility(View.VISIBLE);
        binding.childLayout.setVisibility(View.GONE);
//        binding.toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.emptyLayout.setVisibility(View.GONE);
        binding.emptyLayoutRecyclerviewItem.setVisibility(View.VISIBLE);

        binding.pdfRv.setVisibility(GONE);

        if (navigationLayoutProvider != null) {
            LinearLayout navigationLayout = navigationLayoutProvider.getNavigationLayout();
            if (navigationLayout != null) {
                navigationLayout.setVisibility(View.GONE);
            }
        }

        //Home Activity Elements

        homeBannerAdsLayout.setVisibility(View.INVISIBLE);
        homeNavigationLayout.setVisibility(View.INVISIBLE);
        hometabsMainLayout.setVisibility(View.INVISIBLE);


        homeSearchLayout.setVisibility(View.VISIBLE);
//        binding.childLayout.setVisibility(View.GONE);
        homeToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.emptyLayout.setVisibility(View.GONE);
        binding.emptyLayoutRecyclerviewItem.setVisibility(View.VISIBLE);

        homeSearchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(homeSearchEditText, InputMethodManager.SHOW_IMPLICIT);


        // Get color from resources
        int statusBarColor = ContextCompat.getColor(requireActivity(), R.color.white);
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor,requireActivity());


    }

    private void hideSearchLayout() {
//        binding.searchLayout.setVisibility(View.GONE);
        binding.childLayout.setVisibility(View.VISIBLE);
//        binding.toolbar.setBackground(getResources().getDrawable(R.drawable.toolbar_updated_bg_2));
        binding.emptyLayout.setVisibility(View.VISIBLE);
        binding.emptyLayoutRecyclerviewItem.setVisibility(View.GONE);

        if (navigationLayoutProvider != null) {
            LinearLayout navigationLayout = navigationLayoutProvider.getNavigationLayout();
            if (navigationLayout != null) {
                navigationLayout.setVisibility(View.VISIBLE);
            }
        }

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(homeSearchBackBtn.getWindowToken(), 0);


        //Home Activity Elements

        homeBannerAdsLayout.setVisibility(View.VISIBLE);
        homeNavigationLayout.setVisibility(View.VISIBLE);
        hometabsMainLayout.setVisibility(View.VISIBLE);


        homeSearchLayout.setVisibility(View.GONE);
//        binding.childLayout.setVisibility(View.GONE);
        homeToolbar.setBackground(getResources().getDrawable(R.drawable.toolbar_updated_bg_2));
        binding.emptyLayout.setVisibility(View.GONE);
        binding.emptyLayoutRecyclerviewItem.setVisibility(View.VISIBLE);


        if (pdfFiles.isEmpty()){
            binding.emptyLayout.setVisibility(VISIBLE);
//            binding.weMadeTextview.setVisibility(View.GONE);
//            binding.convertSignScanActionLayout.setVisibility(View.GONE);
            binding.pdfRv.setVisibility(GONE);
        }else {
            binding.emptyLayout.setVisibility(GONE);
            binding.emptyLayoutRecyclerviewItem.setVisibility(GONE);
//            binding.weMadeTextview.setVisibility(View.GONE);
//            binding.convertSignScanActionLayout.setVisibility(View.GONE);
            binding.pdfRv.setVisibility(VISIBLE);
        }


        // Get color from resources
        int statusBarColor = ContextCompat.getColor(requireActivity(), R.color.home_toolbar_color);
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor,requireActivity());

    }

    private void openPdfFile(String filePath, PdfFile pdfFile) {
        if (pdfFile.fileTag.equals("protect doc")){

            File file = new File(filePath); // Replace with your PDF file path
            Uri uri = FileProvider.getUriForFile(requireActivity(), requireActivity().getPackageName() + ".provider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                requireActivity().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireActivity(), "No PDF viewer found", Toast.LENGTH_SHORT).show();
            }



        }else {
            Intent intent = new Intent(requireActivity(), PdfViewActivity.class);
            intent.putExtra("pdfUri", "" + Uri.fromFile(new File(filePath)));
            startActivity(intent);
        }

        Log.d("PdfViewActivity11","filePath  "+filePath);


//        Intent intent = new  Intent(requireActivity(), ActivityDigiSign.class);
//        intent.putExtra("ActivityAction", "Preview");
//        intent.putExtra("preview_file_path", filePath);
//        startActivity(intent);



//        if (checkIfFileIsEncrypted(filePath)) {
//            Toast.makeText(requireActivity(), "The PDF file is encrypted", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(requireActivity(), "The PDF file is not encrypted", Toast.LENGTH_SHORT).show();
//        }
//
//
//        try {
//
//            File file = new File(filePath);
//
//            // Get URI for the file using FileProvider
//            Uri pdfUri = FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName() + ".provider", file);
//            // Create an intent
//            // Create an intent
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(pdfUri, "application/pdf");
//            intent.setPackage("com.google.android.apps.docs"); // Package name for Google Drive
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            // Verify that Google Drive is installed
//            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                Toast.makeText(requireActivity(), "Google Drive is not installed", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(requireActivity()    , "Failed to open PDF with Drive", Toast.LENGTH_SHORT).show();
//        }

    }

    private boolean checkIfFileIsEncrypted(String filePath) {

        boolean isEncrypted = false;
        PdfReader reader = null;

        try {
            reader = new PdfReader(filePath);
            isEncrypted = reader.isEncrypted();
        } catch (Exception e) {
            // If an exception occurs, it's likely due to the file being encrypted
            isEncrypted = true;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return isEncrypted;

    }

    private void updateToolbarTitle(int selectionCount) {
        if (selectionCount > 0) {
//            binding.toolbarSelectedItem.setText(selectionCount + " selected");

            //Home Activity Element
            homeToolbarSelectedItem.setText(selectionCount + " selected");

            Log.d("checkselected",selectionCount + " selected");


//            binding.deleteBtn.setVisibility(View.VISIBLE);
//            binding.toolbarSelectedItem.setVisibility(View.VISIBLE);
//            binding.clearButton.setVisibility(View.VISIBLE);
//            binding.searchBtn.setVisibility(View.GONE);
//            binding.toolbarTitle.setVisibility(View.GONE);
//            binding.fileSortingBtn.setVisibility(View.GONE);
            binding.childLayout.setVisibility(View.GONE);


            // Home Activity elements blow

            homeDeleteButton.setVisibility(View.VISIBLE);
            homeToolbarSelectedItem.setVisibility(View.VISIBLE);
            homeClearButton.setVisibility(View.VISIBLE);
            homeSearchButton.setVisibility(View.GONE);
            homeToolbarTitle.setVisibility(View.GONE);
            homeSortButton.setVisibility(View.GONE);
            homeFileListOrGridButtonButton.setVisibility(View.GONE);




        } else {
//            binding.toolbarTitle.setText(getResources().getString(R.string.docsign));

            //Home Activity Element
//            homeToolbarTitle.setText(getResources().getString(R.string.docsign_non));

            homeToolbarTitle.setVisibility(View.VISIBLE);

//            binding.deleteBtn.setVisibility(View.GONE);
//            binding.toolbarSelectedItem.setVisibility(View.GONE);
//            binding.clearButton.setVisibility(View.GONE);
//            binding.searchBtn.setVisibility(View.VISIBLE);
//            binding.toolbarTitle.setVisibility(View.VISIBLE);
//            binding.fileSortingBtn.setVisibility(View.VISIBLE);
            binding.childLayout.setVisibility(View.VISIBLE);




            // Home Activity elements blow

            homeDeleteButton.setVisibility(View.GONE);
            homeToolbarSelectedItem.setVisibility(View.GONE);
            homeClearButton.setVisibility(View.GONE);
            homeSearchButton.setVisibility(View.VISIBLE);
            homeToolbarTitle.setVisibility(View.VISIBLE);
            homeSortButton.setVisibility(View.VISIBLE);
            homeFileListOrGridButtonButton.setVisibility(View.VISIBLE);


        }









    }



    private class ViewPasswordDialog {

        PdfFile pdfFile;

        public ViewPasswordDialog(PdfFile pdfFile) {
            this.pdfFile = pdfFile;
        }

        public void showDialog(Context activity) {
            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.lock_dialog);

            TextInputEditText passwordEt = dialog.findViewById(R.id.passwordEt);
            TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
            TextView saveBtn = dialog.findViewById(R.id.saveBtn);

            // Set up the input
            passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            cancelBtn.setOnClickListener(v -> dialog.dismiss());

            saveBtn.setOnClickListener(v -> {
                String enteredPassword = passwordEt.getText().toString();
                if (enteredPassword.equals(pdfFile.password)) {
                    openPdfFile(pdfFile.filePath, pdfFile);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireActivity(), "Incorrect password", Toast.LENGTH_SHORT).show();
                }
            });

            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
                int screenWidth = metrics.widthPixels;
                int desiredWidth = screenWidth - 2 * dpToPx(activity, 0);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = desiredWidth;
                dialog.getWindow().setAttributes(params);
            }

            dialog.show();
        }

        private int dpToPx(Context context, int dp) {
            return (int) (dp * context.getResources().getDisplayMetrics().density);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        toolbarController = null;
        Log.d("checktoolbar","onDetach");
    }

    @Override
    public void onResume() {
        super.onResume();


        try {

//            pdfFileViewModel.getIdRecentPdfFiles().observe(this, pdfFileList -> {
                pdfFileViewModel.getRecentPdfFilesByUserType(AccountsOrGuesHelper.checkAccountOrNot(requireActivity()),"pdf").observe(getViewLifecycleOwner(), pdfFileList -> {
//
                if (pdfFileList.isEmpty()){
                    Log.d("checkfragmentselected"," RecentFragment is Empty 1");


                    homeSortButton.setVisibility(View.GONE);
                    homeFileListOrGridButtonButton.setVisibility(View.GONE);
                    homeSearchButton.setVisibility(View.GONE);
                    homeSelectAllButton.setVisibility(View.GONE);

                }else {
                    Log.d("checkfragmentselected","RecentFragment not empty 2");

                    homeSortButton.setVisibility(View.VISIBLE);
                    homeFileListOrGridButtonButton.setVisibility(View.VISIBLE);
                    homeSearchButton.setVisibility(View.VISIBLE);
                    homeSelectAllButton.setVisibility(View.VISIBLE);

                }


                boolean isCheckedListOrGrid = ListGridItemStorage.isCheckedListOrGrid(requireActivity());

                if (isCheckedListOrGrid){
                    homeFileListOrGridButtonButton.setImageResource(R.drawable.grid_view_icon);
                }else {
                    homeFileListOrGridButtonButton.setImageResource(R.drawable.list_view_icon);
                }

                setupRecyclerView();
                updateRecyclerView(pdfFileList, isCheckedListOrGrid);



            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Log.d("checkfragmentselected11","RecentFragment  onResume");


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("checkfragmentselected11","RecentFragment  onPause");
    }
}



