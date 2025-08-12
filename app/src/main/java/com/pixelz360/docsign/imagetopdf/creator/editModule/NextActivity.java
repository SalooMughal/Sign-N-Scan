package com.pixelz360.docsign.imagetopdf.creator.editModule;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.pixelz360.docsign.imagetopdf.creator.Constant;
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile;
import com.pixelz360.docsign.imagetopdf.creator.ads_free_app_billing.PrefUtilForAppAdsFree;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager;
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper;
import com.pixelz360.docsign.imagetopdf.creator.models.ModelImage;
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
public class NextActivity extends AppCompatActivity {

    private ArrayList<ModelImage> allImageArrayList;
    List<Uri> uriList;
    PdfFileViewModel pdfFileViewModel;
    private AdManager adManager;

    File newFile;

    String modulename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        pdfFileViewModel = new ViewModelProvider(this).get(PdfFileViewModel.class);

        adManager = new AdManager();
        adManager.loadAd(NextActivity.this, getString(R.string.convert_to_pdf_final_button_screen_intertial_ad));



        Intent intent = getIntent();

        String password = intent.getStringExtra("password");
        String fileName = intent.getStringExtra("fileName");
         modulename = intent.getStringExtra("modulename");
        String pageSize = intent.getStringExtra("pageSize");
        String pageMargin = intent.getStringExtra("pageMargin");
        String pageCompression = intent.getStringExtra("pageCompression");
        boolean isLandscap = intent.getBooleanExtra("isLandscap",false);

        // Retrieving URI List
        uriList = getIntent().getParcelableArrayListExtra("images");

        convertMultipleImagesToPdf(true, uriList, password, fileName,isLandscap,pageSize,pageCompression,pageMargin);


        // Now you can use these values in your NextActivity
        Log.d("NextActivity", "Password: " + password);
        Log.d("NextActivity", "File Name: " + fileName);
        Log.d("NextActivity", "Page Size: " + pageSize);
        Log.d("NextActivity", "Page Margin: " + pageMargin);
        Log.d("NextActivity", "Page Compression: " + pageCompression);
        Log.d("NextActivity", "URI List Size: " + uriList.size());
    }


    private void convertMultipleImagesToPdf(boolean convertAll, List<Uri> data, String password, String fileName, boolean isLandscape, String pageSize, String compressionLevel, String marginSetting) {
        Log.d(TAG, "convertImagesToPdf: convertAll " + convertAll);
//        progressDialog.setMessage("Converting to PDF...");
//        progressDialog.show();

//        Dialog progressDialog = showloadingDailog(data);
//        progressDialog.show();

        TextView selectedMediaCancelBtn = findViewById(R.id.selectedMediaCancelBtn);
        TextView contentMedia = findViewById(R.id.contentMedia);

        contentMedia.setText("1 of "+data.size()+" Ready");

        selectedMediaCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



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
                        PdfFile pdfFile = new PdfFile(file.getAbsolutePath(), fileName,"Scanned Doc","#495D8D", password, fileSizeBytes, timestamp, false,false,true,true,false,false,false,false,false, AccountsOrGuesHelper.checkAccountOrNot(NextActivity.this),false,"pdf");
//                        db.pdfFileDao().insert(pdfFile);
                        pdfFileViewModel.insertPdfFile(pdfFile);


                    }else {
                        PdfFile pdfFile = new PdfFile(file.getAbsolutePath(), fileName,"PDF Doc","#FF2F2F",  password, fileSizeBytes, timestamp, false,false,true,false,false,false,false,false,false,AccountsOrGuesHelper.checkAccountOrNot(NextActivity.this),false,"pdf");
//                        db.pdfFileDao().insert(pdfFile);
                        pdfFileViewModel.insertPdfFile(pdfFile);

                    }




//                    Intent intent = new Intent(NextActivity.this, Prview_Screen.class);
//                    intent.putExtra("pdffilePath", "" + file.getAbsolutePath());
//                    intent.putExtra("fileName", fileName);
//                    startActivity(intent);
//                    finish();


                    newFile = file;




                } catch (Exception e) {
//                    progressDialog.dismiss();
                    Log.d(TAG, "run: ", e);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        adManager.showAdIfAvailable(NextActivity.this, getString(R.string.convert_to_pdf_final_button_screen_intertial_ad), new AdManager.AdCallback() {
                            @Override
                            public void onAdDismissed() {
                                goToNextActivity(newFile, fileName);
                            }

                            @Override
                            public void onAdFailedToShow() {
                                goToNextActivity(newFile, fileName);
                            }
                        });




//                        Log.d(TAG, "run: Converted...");
//                        Toast.makeText(NextActivity.this, "Conversion completed successfully.", Toast.LENGTH_SHORT).show();
//
//
//
//
//                        if (!isInternetAvailable()) {
//                            goToNextActivity(newFile,fileName); // No internet, proceed to the next activity
//                        } else {
//
//                            if (PrefUtilForAppAdsFree.isPremium(NextActivity.this)|| PrefUtilForAppAdsFree.getAdsForLiftTimeString(NextActivity.this).equals("ads_free_life_time")){
//                                goToNextActivity(newFile, fileName);
//
//                                Log.d("checkbilling"," add remove "+PrefUtilForAppAdsFree.isPremium(NextActivity.this));
//
//                            }else{
//
//                                Log.d("checkbilling"," add not remove "+PrefUtilForAppAdsFree.isPremium(NextActivity.this));
//
//                                // Show the ad or proceed if timeout
//
//
//                                adManager.showAdIfAvailable(NextActivity.this, getString(R.string.convert_to_pdf_final_button_screen_intertial_ad), new AdManager.AdCallback() {
//                                    @Override
//                                    public void onAdDismissed() {
//                                        goToNextActivity(newFile, fileName);
//                                    }
//
//                                    @Override
//                                    public void onAdFailedToShow() {
//                                        goToNextActivity(newFile, fileName);
//                                    }
//                                });
//
//
//
//                            }
//
//
//
//                        }



                    }
                });
            }
        });
    }

    private void goToNextActivity(File file, String fileName) {
        Intent intent = new Intent(NextActivity.this, Prview_Screen.class);
        intent.putExtra("pdffilePath", "" + file.getAbsolutePath());
        intent.putExtra("fileName", fileName);
        startActivity(intent);
        finish();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
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

    private int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

}