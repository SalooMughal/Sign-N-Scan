package com.pixelz360.docsign.imagetopdf.creator.pdfToJpg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pixelz360.docsign.imagetopdf.creator.Constant;
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityFileResultBinding;
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FileResultActivity extends AppCompatActivity {

    private ImageAdapter imageAdapter;
    private ArrayList<String> imagePaths = new ArrayList<>();
    PdfFileViewModel pdfFileViewModel;

    ActivityFileResultBinding binding;
    String conversionType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFileResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        goToFileButton = findViewById(R.id.goToFileButton);

        binding.rv.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rv.setHasFixedSize(true);

        // Initialize ViewModel
        pdfFileViewModel = new ViewModelProvider(this).get(PdfFileViewModel.class);

        // Save images in PDF_FOLDER and get paths
//        List<String> savedPaths = saveImagesToPdfFolder(getIntent().getStringArrayListExtra("imagePaths"));
        List<String> savedPaths = getIntent().getStringArrayListExtra("imagePaths");
        conversionType = getIntent().getStringExtra("conversionType");


        if (savedPaths != null && !savedPaths.isEmpty()) {
            imagePaths.addAll(savedPaths);
            imageAdapter = new ImageAdapter(this, imagePaths);
            binding.rv.setAdapter(imageAdapter);
        } else {
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
        }

        binding.saveBtntv.setOnClickListener(v -> saveImagesToPdfFolder(imagePaths));
    }

    private void saveImagesToPdfFolder(List<String> originalPaths) {
        List<String> savedPaths = new ArrayList<>();
        if (originalPaths == null);
        File pdfFolder = new File(getExternalFilesDir(null), Constant.PDF_FOLDER);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }

        for (String originalPath : originalPaths) {
            try {
                File originalFile = new File(originalPath);
                long timestamp = System.currentTimeMillis();
                String newFileName = "Converted_" + timestamp + "_" + originalFile.getName();
                File newFile = new File(pdfFolder, newFileName);

                if (originalFile.renameTo(newFile)) {
                    savedPaths.add(newFile.getAbsolutePath());
                    saveImageToDatabase(newFile.getAbsolutePath(), newFileName, timestamp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void saveImageToDatabase(String filePath, String fileName, long timestamp) {
        Log.d("checkimagepath"+" path ",filePath);
        Log.d("checkimagepath"+" fileName ",fileName);


        PdfFile pdfFile = new PdfFile(filePath, fileName,"Converted JPG","#495D8D", null, 0f, timestamp, false,false,true,true,false,false,false,false,false, AccountsOrGuesHelper.checkAccountOrNot(FileResultActivity.this),true,"pdf");
//                        db.pdfFileDao().insert(pdfFile);
        pdfFileViewModel.insertPdfFile(pdfFile);


        if (conversionType.equals("pagesToJpg")){
            Toast.makeText(this, "Pages To Jpg Successfully! Saved to: ${outputPath.absolutePath}", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Extract Images Successfully! Saved to: ${outputPath.absolutePath}", Toast.LENGTH_SHORT).show();

        }



        Intent intent = new  Intent(FileResultActivity.this, HomeActivity.class);
        intent.putExtra("ScannerSide","PreviewSide");
        startActivity(intent);
        finish();

    }

//    private void openFileExplorer() {
//        if (!imagePaths.isEmpty()) {
//            File file = new File(imagePaths.get(0)); // Open first saved image
//            Uri fileUri = Uri.fromFile(file);
//
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(fileUri, "image/*");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            try {
//                startActivity(intent);
//            } catch (Exception e) {
//                Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "No files to open", Toast.LENGTH_SHORT).show();
//        }
//    }
}
