package com.pixelz360.docsign.imagetopdf.creator.pdfToJpg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.pixelz360.docsign.imagetopdf.creator.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ProcessingActivity extends AppCompatActivity {

    private TextView processingText;
    private String conversionType;
    private Uri pdfUri;
    private ArrayList<String> imagePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_processing);

        processingText = findViewById(R.id.processingText);
        conversionType = getIntent().getStringExtra("conversionType");
        pdfUri = Uri.parse(getIntent().getStringExtra("pdfUri"));

        processingText.setText("Processing...");

        new Handler().postDelayed(() -> {
            if (conversionType.equals("pagesToJpg")) {
                convertPagesToJpg(pdfUri);
            } else if (conversionType.equals("extractImages")) {
                extractImagesFromPdf(pdfUri);
            }
        }, 500);
    }

    private void convertPagesToJpg(Uri pdfUri) {
        try {
            ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(pdfUri, "r");
            if (fileDescriptor != null) {
                PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);

                for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                    PdfRenderer.Page page = pdfRenderer.openPage(i);
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    page.close();

                    File jpgFile = saveBitmapAsJpg(bitmap, i);
                    if (jpgFile != null) {
                        imagePaths.add(jpgFile.getAbsolutePath());
                    }
                }
                pdfRenderer.close();
                fileDescriptor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to convert PDF pages to JPG", Toast.LENGTH_SHORT).show();
        }
        navigateToFileResult();
    }

    private void extractImagesFromPdf(Uri pdfUri) {
        String outputDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/ExtractedImages";
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        PdfImageExtractor.extractImagesFromPdf(this, pdfUri, outputDir, new PdfImageExtractor.ImageExtractionCallback() {
            @Override
            public void onExtractionComplete(ArrayList<String> extractedImagePaths) {
                imagePaths.addAll(extractedImagePaths);
                navigateToFileResult();
            }

            @Override
            public void onExtractionFailed(String errorMessage) {
                Toast.makeText(ProcessingActivity.this, "Failed to extract images: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File saveBitmapAsJpg(Bitmap bitmap, int index) {
        try {
            File jpgFile = new File(getExternalFilesDir(null), "converted_page_" + index + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(jpgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return jpgFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void navigateToFileResult() {
        Intent intent = new Intent(ProcessingActivity.this, FileResultActivity.class);
        intent.putStringArrayListExtra("imagePaths", imagePaths);
        intent.putExtra("conversionType", conversionType);
        startActivity(intent);
        finish();
    }
}
