package com.pixelz360.docsign.imagetopdf.creator.pdfToJpg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.pixelz360.docsign.imagetopdf.creator.R;

public class PdfConvertOptionsActivity extends AppCompatActivity {

    private Button pagesToJpgButton, extractImagesButton;
    private Uri pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_pdf_convert_options);

        pagesToJpgButton = findViewById(R.id.pagesToJpgButton);
        extractImagesButton = findViewById(R.id.extractImagesButton);
        pdfUri = Uri.parse(getIntent().getStringExtra("pdfUri"));

        pagesToJpgButton.setOnClickListener(v -> startProcessing("pagesToJpg"));
        extractImagesButton.setOnClickListener(v -> startProcessing("extractImages"));
    }

    private void startProcessing(String conversionType) {
        Intent intent = new Intent(PdfConvertOptionsActivity.this, ProcessingActivity.class);
        intent.putExtra("pdfUri", pdfUri.toString());
        intent.putExtra("conversionType", conversionType);
        startActivity(intent);
    }
}
