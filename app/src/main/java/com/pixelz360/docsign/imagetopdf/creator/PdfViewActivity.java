package com.pixelz360.docsign.imagetopdf.creator;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixelz360.docsign.imagetopdf.creator.adapters.AdapterPdfView;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityPdfViewBinding;
import com.pixelz360.docsign.imagetopdf.creator.editModule.ConvertToPdfActivity;
import com.pixelz360.docsign.imagetopdf.creator.models.ModelPdfView;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PdfViewActivity extends AppCompatActivity {

    private RecyclerView pdfViewRv;
    private String pdfUri;
    private static final String TAG = "PDF_VIEW_TAG";
    private ProgressBar progressBar;

    ActivityPdfViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityPdfViewBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(binding.getRoot());

        //change actionbar title
//        getSupportActionBar().setTitle("PDF Viewer");
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);

        pdfViewRv = findViewById(R.id.pdfViewRv);


        // Get color from resources
        int statusBarColor = ContextCompat.getColor(PdfViewActivity.this, R.color.white);
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor,PdfViewActivity.this);



        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
//                Log.d("PdfViewActivity11", "Received URI: " + uri.toString());


                pdfUri =  getDocumentFilePath(PdfViewActivity.this, uri);

                Log.d("PdfViewActivity11", "this External Side "+pdfUri);

            } else {
                Log.d("PdfViewActivity11", "No URI received");
            }
        }else {
            pdfUri = getIntent().getStringExtra("pdfUri");


            Log.d("PdfViewActivity11", "No External Side "+pdfUri);


        }




        loadPdfPages();


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(PdfViewActivity.this,MainActivity.class);
                Intent intent = new Intent(PdfViewActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });





        // Log a predefined event
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("activity_name", "PdfViewActivity");
        analytics.logEvent("activity_created", bundle);
// Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "PdfViewActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);



    }

//    public String getRealPathFromURI(Context context, Uri contentUri) {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            String filePath = cursor.getString(columnIndex);
//            cursor.close();
//            return filePath;
//        }
//        return null;
//    }

    public String getDocumentFilePath(Context context, Uri uri) {
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
        if (documentFile != null && documentFile.exists()) {
            return documentFile.getUri().getPath();
        }
        return null;
    }



    private PdfRenderer.Page mCurrentPage = null;

    private void loadPdfPages() {

        Log.d(TAG, "loadPdfPages: ");

        ArrayList<ModelPdfView> pdfViewArrayList = new ArrayList<>();

        AdapterPdfView adapterPdfView = new AdapterPdfView(this, pdfViewArrayList);

        pdfViewRv.setAdapter(adapterPdfView);

        // Show the ProgressBar before starting the task
        progressBar.setVisibility(View.VISIBLE);

        File file = new File(Uri.parse(pdfUri).getPath());

        try {
            getSupportActionBar().setSubtitle(file.getName());
        } catch (Exception e) {
            Log.d(TAG, "loadPdfPages:  ", e);
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                    PdfRenderer mPdfRenderer = new PdfRenderer(parcelFileDescriptor);

                    int pageCount = mPdfRenderer.getPageCount();

                    if (pageCount <= 0) {
                        Log.d(TAG, "run: No pages in PDF File");
                    } else {
                        Log.d(TAG, "run: Have pages in PDF file");

                        for (int i = 0; i < pageCount; i++) {
                            if (mCurrentPage != null) {
                                mCurrentPage.close();
                            }

                            mCurrentPage = mPdfRenderer.openPage(i);

                            Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(), Bitmap.Config.ARGB_8888);

                            mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                            pdfViewArrayList.add(new ModelPdfView(Uri.parse(pdfUri), (i + 1), pageCount, bitmap));
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "run:  ", e);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Hide the ProgressBar after PDF loading is done
                        progressBar.setVisibility(View.GONE);
                        adapterPdfView.notifyDataSetChanged();
                    }
                });
            }
        });
    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }



}