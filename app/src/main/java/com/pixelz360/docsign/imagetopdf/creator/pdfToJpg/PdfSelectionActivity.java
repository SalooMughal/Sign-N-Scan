package com.pixelz360.docsign.imagetopdf.creator.pdfToJpg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.pixelz360.docsign.imagetopdf.creator.R;

public class PdfSelectionActivity extends AppCompatActivity {

    private Button selectPdfButton, nextButton;
    private TextView selectedPdfText;
    private Uri selectedPdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_pdf_selection);

        selectPdfButton = findViewById(R.id.selectPdfButton);
        nextButton = findViewById(R.id.nextButton);
        selectedPdfText = findViewById(R.id.selectedPdfText);

        selectPdfButton.setOnClickListener(v -> selectPdf());

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(PdfSelectionActivity.this, PdfConvertOptionsActivity.class);
            intent.putExtra("pdfUri", selectedPdfUri.toString());
            startActivity(intent);
        });
    }

    private void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pdfPickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pdfPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handlePdfSelection
    );

    private void handlePdfSelection(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            selectedPdfUri = result.getData().getData();
            selectedPdfText.setText("Selected: " + selectedPdfUri.getLastPathSegment());
            nextButton.setVisibility(View.VISIBLE);
        }
    }
}
