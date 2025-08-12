package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixelz360.docsign.imagetopdf.creator.NavigationLayoutProvider;
import com.pixelz360.docsign.imagetopdf.creator.PdfViewActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.RvListenerPdfRoom;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters.FavoritePdfAdapterRoom;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters.MargePdfAdapterRoom;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters.PdfAdapterRoom;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters.ScannerAdapterRoom;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters.SignedPdfAdapterRoom;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters.SplitPdfAdapterRoom;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityScannedDocsListBinding;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RoomScannerFragment extends Fragment implements ToolbarSettings {

    private static final String ARG_LAYOUT_ID = "layout_id";
    private static final String BANNER_AD_ISCHECKED = "banner_ad_checked";

    private ScannerAdapterRoom pdfAdapter;
    private PdfFileViewModel pdfFileViewModel;
    private ActivityScannedDocsListBinding binding;
    private boolean bannerAdIsLoaded;
    private int frameLayout;
    private List<PdfFile> pdfFiles;
    private boolean singleItemsSelected = false;
    private boolean allItemsSelected = false;
    private boolean isFileValieOrNo = false;

    public RoomScannerFragment() {
    }
    private NavigationLayoutProvider navigationLayoutProvider;

    public static RoomScannerFragment newInstance(int layoutId, boolean bannerAdIsLoaded) {
        RoomScannerFragment fragment = new RoomScannerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        args.putBoolean(BANNER_AD_ISCHECKED, bannerAdIsLoaded);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof NavigationLayoutProvider) {
//            navigationLayoutProvider = (NavigationLayoutProvider) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement NavigationLayoutProvider");
//        }
//    }



    ConstraintLayout homeToolbarTitle;
    TextView homeSelectAllButton;
    TextView homeToolbarSelectedItem;
    ImageButton homeSearchButton;
    ImageButton homeSortButton;
    ImageButton homeDeleteButton;
    ImageButton homeSettingButton;
    LinearLayout homeSearchLayout;
    Toolbar homeToolbar;
    ImageView homeSearchBackBtn;
    ImageView homeClearButton;
    EditText homeSearchEditText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityScannedDocsListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Log.d("checkfiles123", "onCreateView 1");

        pdfFileViewModel = new ViewModelProvider(this).get(PdfFileViewModel.class);



        if (getArguments() != null) {
            frameLayout = getArguments().getInt(ARG_LAYOUT_ID);
            bannerAdIsLoaded = getArguments().getBoolean(BANNER_AD_ISCHECKED);
        }

        Log.d("checkfiles123", "onCreateView 2");


        // Log a predefined event
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(requireActivity());
        Bundle bundle = new Bundle();
        bundle.putString("activity_name", "RoomScannerFragment");
        analytics.logEvent("activity_created", bundle);
// Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "RoomScannerFragment");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        Log.d("checkfiles123", "onCreateView 3");


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (pdfFileViewModel!=null){
            Log.d("checkfiles123", "pdfFileViewModel Inisized " );

        }else {
            Log.d("checkfiles123", "pdfFileViewModel not Inisized " );
        }

        // Observe the list of files
        pdfFileViewModel.getIdCardPdfFiles().observe(getViewLifecycleOwner(), files -> {
            if (files != null && !files.isEmpty()) {
                Log.d("checkfiles123", "Files retrieved: " + files.size());
                pdfFiles = files;

                updateRecyclerView(files);
            } else {
                Log.d("checkfiles123", "No files retrieved");
                // Handle empty case
            }
        });


        Log.d("checkfiles123", "onCreateView 4");

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

        homeSearchLayout.setVisibility(View.GONE);
//        binding.childLayout.setVisibility(View.GONE);
        homeToolbar.setBackground(getResources().getDrawable(R.drawable.toolbar_updated_bg_2));
        binding.emptyLayout.setVisibility(View.GONE);
        binding.emptyLayoutRecyclerviewItem.setVisibility(View.VISIBLE);


    }

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
            homeToolbarTitle.setVisibility(View.VISIBLE);

            homeDeleteButton.setVisibility(View.GONE);
            homeClearButton.setVisibility(View.GONE);
            homeToolbarSelectedItem.setVisibility(View.GONE);

//            homeToolbarTitle.setText(getResources().getString(R.string.docsign));

        }


    }

    private void showSearchLayout() {

//        binding.searchLayout.setVisibility(View.VISIBLE);
        binding.childLayout.setVisibility(View.GONE);
//        binding.toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.emptyLayout.setVisibility(View.GONE);
        binding.emptyLayoutRecyclerviewItem.setVisibility(View.VISIBLE);

        if (navigationLayoutProvider != null) {
            LinearLayout navigationLayout = navigationLayoutProvider.getNavigationLayout();
            if (navigationLayout != null) {
                navigationLayout.setVisibility(View.GONE);
            }
        }

        //Home Activity Elements

        homeSearchLayout.setVisibility(View.VISIBLE);
//        binding.childLayout.setVisibility(View.GONE);
        homeToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.emptyLayout.setVisibility(View.GONE);
        binding.emptyLayoutRecyclerviewItem.setVisibility(View.VISIBLE);



    }

    private void filter(String text) {
        // Check if the pdfFiles list is null or empty
        if (pdfFiles == null || pdfFiles.isEmpty()) {
            Log.e("RoomOnlyScannedDocsFragment", "pdfFiles is null or empty, cannot filter");
            return; // Exit early if the list is null or empty
        }

        // Create a new array list to hold the filtered data
        ArrayList<PdfFile> filteredNames = new ArrayList<>();

        // Loop through existing elements
        for (PdfFile s : pdfFiles) {
            // Check if the file path is not null
            if (s.filePath != null) {
                File file = new File(s.filePath);
                String filename = file.getName();

                // If the filename contains the search input, add it to the filtered list
                if (filename.toLowerCase().contains(text.toLowerCase())) {
                    filteredNames.add(s);
                }
            }
        }

        // Call the adapter method to pass the filtered list
        pdfAdapter.filterList(filteredNames);
    }


    private void updateRecyclerView(List<PdfFile> pdfFileList) {
        if (pdfFileList == null || pdfFileList.isEmpty()) {
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.idCardRv.setVisibility(View.GONE);
            isFileValieOrNo = false;
        } else {
            binding.emptyLayout.setVisibility(View.GONE);
            binding.idCardRv.setVisibility(View.VISIBLE);
            isFileValieOrNo = true;
        }

//        binding.filesBtn.setText(getResources().getString(R.string.all_files) + "(" + pdfFileList.size() + ")");
        binding.scacannerCountFiles.setText(pdfFileList.size() + " Files");

        pdfAdapter = new ScannerAdapterRoom(pdfFileList, requireContext(), new RvListenerPdfRoom() {
            @Override
            public void onPdfClick(PdfFile pdfFile, int position) {
                if (pdfFile.password != null && !pdfFile.password.isEmpty()) {
                    ViewPasswordDialog alert = new ViewPasswordDialog(pdfFile);
                    alert.showDialog(requireContext());
                } else {
                    openPdfFile(pdfFile.filePath);
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
            }

        });


        binding.idCardRv.setHasFixedSize(true);
        binding.idCardRv.setItemViewCacheSize(20);
        binding.idCardRv.setDrawingCacheEnabled(true);
        binding.idCardRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        binding.idCardRv.setAdapter(pdfAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        layoutManager.setInitialPrefetchItemCount(4);  // Prefetch the first few items
        binding.idCardRv.setLayoutManager(layoutManager);    }



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

            homeSortButton = toolbarController.getSortButton();






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

                    homeSearchEditText.setText("");
                    hideSearchLayout();

                }
            });

            homeSearchEditText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {

                    filter(editable.toString());

                    Log.d("checktext"," afterTextChanged "+editable.toString());
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence query, int start, int before, int count) {

                    query = query.toString().toLowerCase();

                    Log.d("checktext"," onTextChanged "+query);

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


            toolbarController.getSelectAllButton().setOnClickListener(v -> {
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

                        toolbarController.getSelectAllButton().setText("Select");



                        Toast.makeText(requireActivity(),"No File to Select",Toast.LENGTH_SHORT).show();

                        toolbarController.getSelectAllButton().setBackground(getResources().getDrawable(R.drawable.btn_un_select_bg));
                        toolbarController.getSelectAllButton().setTextColor(ContextCompat.getColor(requireActivity(), R.color.select_text_un_selected));

                    } else {

                        if (toolbarController.getSelectAllButton().getText().equals("DeAll items selected successfully")){
                            Log.d("checkfileyet111", "click DeAll items selected successfully");

                            pdfAdapter.unselectAllItems();
                            toolbarController.getSelectAllButton().setBackground(getResources().getDrawable(R.drawable.btn_un_select_bg));
                            toolbarController.getSelectAllButton().setTextColor(ContextCompat.getColor(requireActivity(), R.color.select_text_un_selected));

                            singleItemsSelected = false; // To track the selection state

                            allItemsSelected = false; // To track the selection state


                            toolbarController.getSelectAllButton().setText("Select");


                        }else{
                            pdfAdapter.selectAllItems("Single All items selected successfully");

                            Log.d("checkfileyet111", "else singleItemsSelected  "+singleItemsSelected);

                            toolbarController.getSelectAllButton().setText("All items selected successfully");
                            Toast.makeText(requireActivity(), "All items selected successfully", Toast.LENGTH_SHORT).show();
                            toolbarController.getSelectAllButton().setBackground(getResources().getDrawable(R.drawable.btn_select_bg));
                            toolbarController.getSelectAllButton().setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));

                            if (!allItemsSelected) {

                                Log.d("checkfileyet111", "if allItemsSelected  "+allItemsSelected);

                                allItemsSelected = true;

                            } else {
                                pdfAdapter.selectAllItems("All items selected successfully");
                                allItemsSelected = false;
                                toolbarController.getSelectAllButton().setText("DeAll items selected successfully");

                                Log.d("checkfileyet111", "else allItemsSelected  "+allItemsSelected);

                            }
                        }

                    }



                }






            });


            toolbarController.getToolbarSelectedItem().setText(R.string.docsign);

        }



    }




//    private void handleSelectAllItems() {
//        if (!isFileValieOrNo){
//            Log.d("checkfileyet","not File Avalibele 1");
//            Toast.makeText(requireActivity(),"No files selected",Toast.LENGTH_SHORT).show();
//
//
//        }else {
//            Log.d("checkfileyet", "File Avalibele 1");
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
////                        pdfAdapter.selectAllItems("Single All items selected successfully");
////
////
////                        binding.allSelctedItemBtn.setText("All items selected successfully");
////
////                        Toast.makeText(requireActivity(),"All items selected successfully",Toast.LENGTH_SHORT).show();
////
////                        binding.allSelctedItemBtn.setBackground(getResources().getDrawable(R.drawable.btn_select_bg));
////                        binding.allSelctedItemBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
////
////                        if (!allItemsSelected){
////
////                            allItemsSelected = true;
////                        }else {
////                            pdfAdapter.selectAllItems("All items selected successfully");
////                            allItemsSelected = false;
////                            binding.allSelctedItemBtn.setText("DeAll items selected successfully");
////
////
////                        }
//
//
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
//
//                        allItemsSelected = true;
//
//
//
//
//
//                    } else {
//                        pdfAdapter.selectAllItems("All items selected successfully");
//                        allItemsSelected = false;
//                        binding.allSelctedItemBtn.setText("DeAll items selected successfully");
//
//
//                        Log.d("checkfileyet111", "else allItemsSelected  "+allItemsSelected);
//
//
//                    }
//                }
//
//
//
//            }
//
//
//        }
//    }

//    private void resetSelectionState() {
//        singleItemsSelected = false;
//        allItemsSelected = false;
//        binding.allSelctedItemBtn.setText("Select");
//        binding.allSelctedItemBtn.setBackground(getResources().getDrawable(R.drawable.btn_un_select_bg));
//        binding.allSelctedItemBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.select_text_un_selected));
//    }
//
//    private void updateSelectionState() {
//        singleItemsSelected = true;
//        binding.allSelctedItemBtn.setText("All items selected successfully");
//        binding.allSelctedItemBtn.setBackground(getResources().getDrawable(R.drawable.btn_select_bg));
//        binding.allSelctedItemBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
//    }

    private void onDeleteSelectedItems() {
        List<PdfFile> selectedFiles = pdfAdapter.getSelectedFiles();
        for (PdfFile pdfFile : selectedFiles) {
            File file = new File(pdfFile.filePath);
            if (file.exists() && file.delete()) {
                pdfFileViewModel.deletePdfFile(pdfFile);
                Toast.makeText(requireActivity(), "PDF file deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Delete Operation", "Failed to delete file: " + file.getPath());
            }
        }
        pdfAdapter.clearSelection();
        getActivity().invalidateOptionsMenu();
//        binding.toolbarTitle.setText(getResources().getString(R.string.scanned_docs));



        //Home Activity Elment
//        homeToolbarTitle.setText(getResources().getString(R.string.docsign));
        homeToolbarTitle.setVisibility(View.VISIBLE);
    }

    private void showSortingDialog() {
        Dialog dialog = new Dialog(requireActivity(), R.style.FileSortingDialogStyle);
        dialog.setContentView(R.layout.file_sorting_sheet_layout);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Ensures no background
        }


        RelativeLayout fileSizeBtn = dialog.findViewById(R.id.fileSizeBtn);
        RelativeLayout createdDateBtn = dialog.findViewById(R.id.createdDateBtn);
        RelativeLayout ascendingBtn = dialog.findViewById(R.id.ascendingBtn);
        RelativeLayout decendingBtn = dialog.findViewById(R.id.decendingBtn);
        TextView doneBtn = dialog.findViewById(R.id.doneBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);

        final String[] fileSortingName = {"FileSize"};
        final int[] sortDirection = {1};

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
            sortDirection[0] = 1;
            decendingBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
            ascendingBtn.setBackground(getResources().getDrawable(R.drawable.selected_main_icon_bg));
        });

        decendingBtn.setOnClickListener(v -> {
            sortDirection[0] = 0;
            decendingBtn.setBackground(getResources().getDrawable(R.drawable.selected_main_icon_bg));
            ascendingBtn.setBackground(getResources().getDrawable(R.drawable.un_selected_main_icon_bg));
        });

        doneBtn.setOnClickListener(v -> {
            pdfFileViewModel.loadIdCardSortedPdfFiles(sortDirection[0]);

            pdfFileViewModel.getIdCardSortedPdfFiles().observe(getViewLifecycleOwner(), pdfFileList -> {
                pdfFiles = pdfFileList;
                updateRecyclerView(pdfFileList);
            });

            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
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


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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





        } else {
//            binding.toolbarTitle.setText(getResources().getString(R.string.docsign));

            //Home Activity Element
//            homeToolbarTitle.setText(getResources().getString(R.string.docsign));
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



        }




    }

    private void openPdfFile(String filePath) {
        Intent intent = new Intent(getContext(), PdfViewActivity.class);
        intent.putExtra("pdfUri", Uri.fromFile(new File(filePath)).toString());
        startActivity(intent);
    }

    private class ViewPasswordDialog {

        private final PdfFile pdfFile;

        public ViewPasswordDialog(PdfFile pdfFile) {
            this.pdfFile = pdfFile;
        }

        public void showDialog(Context context) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.lock_dialog);
            TextInputEditText passwordEt = dialog.findViewById(R.id.passwordEt);
            TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
            TextView saveBtn = dialog.findViewById(R.id.saveBtn);

            passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            cancelBtn.setOnClickListener(v -> dialog.dismiss());
            saveBtn.setOnClickListener(v -> {
                String enteredPassword = passwordEt.getText().toString();
                if (enteredPassword.equals(pdfFile.password)) {
                    openPdfFile(pdfFile.filePath);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();
        }
    }
}

