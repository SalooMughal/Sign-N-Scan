package com.pixelz360.docsign.imagetopdf.creator;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixelz360.docsign.imagetopdf.creator.adapters.AdapterImage;
import com.pixelz360.docsign.imagetopdf.creator.models.ModelImage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Get_Image_List_Activity extends AppCompatActivity {

    private static final String TAG = "IMAGE_LIST_TAG";
    private static final int STORAGE_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 101;

    private Uri imageUri = null;
    private FloatingActionButton addImageFab;
    private RecyclerView imagesRv;

    private ArrayList<ModelImage> allImageArrayList;
    private AdapterImage adapterImage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_image_list);

        addImageFab = findViewById(R.id.addImageFab);
        imagesRv = findViewById(R.id.imagesRv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadImages();

        addImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputImageDialog();
            }
        });




        // Log a predefined event
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString("activity_name", "Get_Image_List_Activity");
        analytics.logEvent("activity_created", bundle);

// Using predefined Firebase Analytics events
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Get_Image_List_Activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.images_item_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Get_Image_List_Activity.this);
            builder.setTitle("Delete Image")
                    .setMessage("Are you want to sure delete All/Selected images?")
                    .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteImages(true);
                        }
                    })
                    .setNeutralButton("Delete Selected", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteImages(false);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } else if (itemId == R.id.images_item_pdf) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Get_Image_List_Activity.this);
            builder.setTitle("Make PDF")
                    .setMessage("Convert All/Selected images to PDF")
                    .setPositiveButton("Convert All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            convertImagesToPdf(true);

                        }
                    })
                    .setNeutralButton("Convert Selected", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            convertImagesToPdf(false);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteImageOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Image")
                .setMessage("Are you want to sure delete All/Selected images?")
                .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteImages(true);
                    }
                })
                .setNeutralButton("Delete Selected", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteImages(false);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void pdfConversionOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make PDF")
                .setMessage("Convert All/Selected images to PDF")
                .setPositiveButton("Convert All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        convertImagesToPdf(true);
                    }
                })
                .setNeutralButton("Convert Selected", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        convertImagesToPdf(false);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void convertImagesToPdf(boolean convertAll) {
        Log.d(TAG, "convertImagesToPdf: convertAll " + convertAll);
        progressDialog.setMessage("Converting to PDF...");
        progressDialog.show();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            Log.d(TAG, "run: BG work start.....");
            ArrayList<ModelImage> imagesToPdfList = new ArrayList<>();
            if (convertAll) {
                imagesToPdfList = allImageArrayList;
            } else {
                //convert selected image only
                for (ModelImage image : allImageArrayList) {
                    if (image.isChecked()) {
                        imagesToPdfList.add(image);
                    }
                }
            }
            Log.d(TAG, "run: imagesToPdfList size: " + imagesToPdfList.size());
            try {
                File root = new File(getExternalFilesDir(null), Constant.PDF_FOLDER);
                if (!root.exists()) {
                    root.mkdirs();
                }
                long timestamp = System.currentTimeMillis();
                String fileName = "PDF_" + timestamp + ".pdf";
                File file = new File(root, fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PdfDocument pdfDocument = new PdfDocument();

                for (ModelImage modelImage : imagesToPdfList) {
                    Uri imageUri = modelImage.getImageUri();
                    Bitmap bitmap;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
                    } else {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    }
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    pdfDocument.finishPage(page);
                    bitmap.recycle();
                }
                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();
                fileOutputStream.close();
                handler.post(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(Get_Image_List_Activity.this, "PDF Created: " + fileName, Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                Log.e(TAG, "PDF creation error: ", e);
                handler.post(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(Get_Image_List_Activity.this, "Failed to create PDF", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deleteImages(boolean deleteAll) {
        Log.d(TAG, "deleteImages: ");
        ArrayList<ModelImage> imagesToDeleteList = new ArrayList<>();
        if (deleteAll) {
            imagesToDeleteList = allImageArrayList;
        } else {
            for (ModelImage image : allImageArrayList) {
                if (image.isChecked()) {
                    imagesToDeleteList.add(image);
                }
            }
        }
        for (ModelImage modelImage : imagesToDeleteList) {
            File file = new File(modelImage.getImageUri().getPath());
            if (file.exists() && file.delete()) {
                Log.d(TAG, "Deleted: " + file.getPath());
            } else {
                Log.d(TAG, "Failed to delete: " + file.getPath());
            }
        }
        loadImages();
        Toast.makeText(this, "Images deleted", Toast.LENGTH_SHORT).show();
    }

    private void loadImages() {
        Log.d(TAG, "loadImages: ");
        allImageArrayList = new ArrayList<>();
        adapterImage = new AdapterImage(this, allImageArrayList);
        imagesRv.setAdapter(adapterImage);

        File folder = new File(getExternalFilesDir(null), Constant.IMAGES_FOLDER);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    Uri uri = Uri.fromFile(file);
                    allImageArrayList.add(new ModelImage(uri, false));
                }
                adapterImage.notifyDataSetChanged();
            }
        }
    }

    private void showInputImageDialog() {
        PopupMenu popupMenu = new PopupMenu(this, addImageFab);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                // Handle camera option
            } else if (item.getItemId() == 2) {
                pickImageFromGallery();
            }
            return true;
        });
        popupMenu.show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select images"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
//                ClipData mClipData = data.getClipData();
//                for (int i = 0; i < mClipData.getItemCount(); i++) {
//                    ClipData.Item item = mClipData.getItemAt(i);
//                    Uri uri = item.getUri();
//                    // Process each URI as needed
//                    Log.d("imageToAddInPdfUri", "Selected image URI: " + uri.toString());

//                    convertMultipleImagesToPdf(true, data);
//                }

                convertMultipleImagesToPdf(true,data);
                Toast.makeText(Get_Image_List_Activity.this, "Multiple Images", Toast.LENGTH_SHORT).show();
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                // Process single URI
                Log.d(TAG, "Selected image URI: " + uri.toString());
                Toast.makeText(Get_Image_List_Activity.this, "One Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void convertMultipleImagesToPdf(boolean convertAll, Intent data) {

        Log.d(TAG, "convertImagesToPdf: convertAll " + convertAll);
        progressDialog.setMessage("Converting to PDF...");
        progressDialog.show();

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
                    //convert selected image only
                    for (int i = 0; i < allImageArrayList.size(); i++) {
                        if (allImageArrayList.get(i).isChecked()) {
                            imagesToPdfList.add(allImageArrayList.get(i));
                        }
                    }
                }
                Log.d(TAG, "run: imagesToPdfList size: " + imagesToPdfList.size());

                try {

                    //1) create folder where we will save the pdf
                    File root = new File(getExternalFilesDir(null), Constant.PDF_FOLDER);
                    root.mkdirs();

                    //name with extension of the image
                    long timestamp = System.currentTimeMillis();
                    String fileName = "PDF " + timestamp + ".pdf";

                    Log.d(TAG, "run: fileName " + fileName);


                    File file = new File(root, fileName);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    PdfDocument pdfDocument = new PdfDocument();


                    ClipData mClipData = data.getClipData();


//                    for (int i = 0; i < imagesToPdfList.size(); i++) {
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
//                        Uri imageToAddInPdfUri = imagesToPdfList.get(i).getImageUri();

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri imageToAddInPdfUri = item.getUri();
                        // Process each URI as needed


                        Log.d("imageToAddInPdfUri", "run: imageToAddInPdfUri " + imageToAddInPdfUri);

                        try {

                            Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageToAddInPdfUri));
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageToAddInPdfUri);
                            }

                            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), i + 1).create();

                            PdfDocument.Page page = pdfDocument.startPage(pageInfo);


                            Paint paint = new Paint();
                            paint.setColor(Color.WHITE);

                            Canvas canvas = page.getCanvas();
                            canvas.drawPaint(paint);
                            canvas.drawBitmap(bitmap, 0f, 0f, null);

                            pdfDocument.finishPage(page);
                            bitmap.recycle();


                        } catch (Exception e) {
                            Log.d(TAG, "run: ", e);

                        }
                    }

                    pdfDocument.writeTo(fileOutputStream);
                    pdfDocument.close();

                } catch (Exception e) {
                    progressDialog.dismiss();
                    Log.d(TAG, "run: ", e);

                }

                //
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: Converted...");
                        progressDialog.dismiss();
                        Toast.makeText(Get_Image_List_Activity.this, "Conversion completed successfully.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
