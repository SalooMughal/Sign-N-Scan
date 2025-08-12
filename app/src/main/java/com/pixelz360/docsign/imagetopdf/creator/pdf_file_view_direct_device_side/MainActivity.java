package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.pixelz360.docsign.imagetopdf.creator.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_PDF_FILE = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private EditText editTextSearch;
    private EditText editTextReplace;
    private File selectedPdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        setContentView(R.layout.activity_main);

        // Request storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

        Button loadPdfButton = findViewById(R.id.load_pdf_button);
        Button savePdfButton = findViewById(R.id.save_pdf_button);
        editTextSearch = findViewById(R.id.edit_text_search);
        editTextReplace = findViewById(R.id.edit_text_replace);

        loadPdfButton.setOnClickListener(view -> openFileChooser());
        savePdfButton.setOnClickListener(view -> {
            if (selectedPdfFile != null) {
                updatePdfText(editTextSearch.getText().toString(), editTextReplace.getText().toString());
            } else {
                Toast.makeText(this, "Please load a PDF first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            loadPdf(uri);
        }
    }

    private void loadPdf(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("temp", ".pdf", getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            selectedPdfFile = tempFile;
            Toast.makeText(this, "PDF loaded successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePdfText(String searchText, String replaceText) {
        try {
            // Create a file in the Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs(); // Ensure the Downloads directory exists
            }
            File editedFile = new File(downloadsDir, "edited.pdf");

            // Read the existing PDF
            PdfReader reader = new PdfReader(selectedPdfFile.getAbsolutePath());
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(editedFile));

            // Get all form fields (if available)
            AcroFields formFields = stamper.getAcroFields();
            Map<String, AcroFields.Item> fields = formFields.getFields();

            // Loop through all fields and replace text
            boolean replaced = false;
            for (String fieldName : fields.keySet()) {
                String value = formFields.getField(fieldName);
                if (value != null && value.contains(searchText)) {
                    formFields.setField(fieldName, value.replace(searchText, replaceText));
                    replaced = true;
                }
            }

            // Close the stamper and reader
            stamper.setFormFlattening(true); // Ensure the fields are flattened (no longer editable)
            stamper.close();
            reader.close();

            if (replaced) {
                Toast.makeText(this, "PDF saved to: " + editedFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Text not found in the PDF.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to update PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
