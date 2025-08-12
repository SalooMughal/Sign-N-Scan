package com.pixelz360.docsign.imagetopdf.creator.viewmodel;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.pixelz360.docsign.imagetopdf.creator.Constant;
import com.pixelz360.docsign.imagetopdf.creator.MainApplication;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtils {

    // Load file size method
    public static CharSequence loadFileSize(PdfFile modelPdf) {
        File file = new File(modelPdf.filePath);
        double bytes = file.length();
        double kb = bytes / 1024;
        double mb = kb / 1024;

        return (mb >= 1) ? String.format("%.2f MB", mb) : (kb >= 1) ? String.format("%.2f KB", kb) : String.format("%.2f bytes", bytes);
    }

    // Load file date method
    public static CharSequence loadFileDate(PdfFile pdfFile) {
        File file = new File(pdfFile.filePath);
        long timestamp = file.lastModified();
        return MainApplication.formatTimestamp(timestamp);
    }

    // Load PDF thumbnail method
    public static void loadThumbnailFromPdfFile(PdfFile modelPdf, ShapeableImageView thumbnailIv, FragmentActivity fragmentActivity) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.myLooper());
        executorService.execute(() -> {
            Bitmap thumbnailBitmap = null;
            int pageCount = 0;
            try {
                ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(new File(modelPdf.filePath), ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                pageCount = pdfRenderer.getPageCount();
                if (pageCount > 0) {
                    PdfRenderer.Page currentPage = pdfRenderer.openPage(0);
                    thumbnailBitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
                    currentPage.render(thumbnailBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                }
            } catch (Exception e) {
                Log.d(TAG, "loadThumbnailFromPdfFile run: ", e);
            }
            Bitmap finalThumbnailBitmap = thumbnailBitmap;
            handler.post(() -> {
                Glide.with(fragmentActivity).load(finalThumbnailBitmap).fitCenter().placeholder(R.drawable.ic_pdf_black).into(thumbnailIv);
            });
        });
    }

    // Three-dot dialog method using ViewModel
    public static void threeDotDailog(PdfFile pdfFile, FragmentActivity fragmentActivity, PdfFileViewModel pdfFileViewModel) {
        Dialog dialog = new Dialog(fragmentActivity, R.style.FileSortingDialogStyle);
        dialog.setContentView(R.layout.three_dot_dailog);


        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Ensures no background
        }


        // Dialog UI elements

        ShapeableImageView thumbnailIv = dialog.findViewById(R.id.thumbnailIv);
        TextView pdf_name = dialog.findViewById(R.id.pdf_name);
        TextView sizeTv = dialog.findViewById(R.id.sizeTv);
        TextView fileTag = dialog.findViewById(R.id.fileTag);
        TextView dateTv = dialog.findViewById(R.id.dateTv);
        ImageView closeDailogBtn = dialog.findViewById(R.id.closeDailogBtn);
        ImageView favoriteIcon = dialog.findViewById(R.id.favoriteIcon);
        ImageView savedIcon = dialog.findViewById(R.id.savedIcon);
        RelativeLayout renamePdfBtn = dialog.findViewById(R.id.renamePdfBtn);
        RelativeLayout editPdfBtn = dialog.findViewById(R.id.editPdfBtn);
        RelativeLayout setPasswordBtn = dialog.findViewById(R.id.setPasswordBtn);
        RelativeLayout addFavoriteBtn = dialog.findViewById(R.id.addFavoriteBtn);
        RelativeLayout fileSavedBtn = dialog.findViewById(R.id.fileSavedBtn);
        RelativeLayout deletePdfBtn = dialog.findViewById(R.id.deletePdfBtn);

        // Set UI values
        pdf_name.setText(pdfFile.fileName);
        sizeTv.setText(FileUtils.loadFileSize(pdfFile));
        dateTv.setText(FileUtils.loadFileDate(pdfFile));

        String extension = pdfFile.filePath.substring(pdfFile.filePath.lastIndexOf("."));

        Log.d("checkimagepath"+"extension  ",extension);

        if (extension.equals(".jpg")){
            Bitmap bitmap = BitmapFactory.decodeFile(pdfFile.filePath);
            thumbnailIv.setImageBitmap(bitmap);

            dateTv.setText("JPG File");
            dateTv.setTextColor(fragmentActivity.getColor(R.color.red));

        }else if (extension.equals(".pdf")){

            // Load thumbnail
            FileUtils.loadThumbnailFromPdfFile(pdfFile, thumbnailIv, fragmentActivity);

        }

        favoriteIcon.setImageResource(pdfFile.isFavorite ? R.drawable.favrarte_selected_icon : R.drawable.favrarte_un_selected_icon);
        savedIcon.setImageResource(pdfFile.isFileSavedInInternalStorage ? R.drawable.download_pdf_file_selected_icon : R.drawable.download_pdf_file_un_selected_icon);



        fileTag.setText(pdfFile.fileTag);


        // Create a GradientDrawable
        GradientDrawable shape = new GradientDrawable();

// Set the shape type (rectangle)
        shape.setShape(GradientDrawable.RECTANGLE);

// Set the corner radius (in pixels)
        shape.setCornerRadius(6f); // Example: 16 pixels

// Set the solid color

        float strokePt = 0.6f;
        float strokePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PT,
                strokePt,
                fragmentActivity.getResources().getDisplayMetrics()
        );

        shape.setStroke((int) strokePx, Color.parseColor(pdfFile.fileTagBgColor));
//        shape.setColor(Color.parseColor(pdfFile.fileTagBgColor)); // Replace with your desired color
        fileTag.setBackground(shape);
        fileTag.setTextColor(Color.parseColor(pdfFile.fileTagBgColor));




        closeDailogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        renamePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                renameDailog(pdfFile,fragmentActivity,pdfFileViewModel);

                dialog.dismiss();

            }
        });


        setPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                setPasswordDialog( requireActivity());
                setPasswordDialog(pdfFile,fragmentActivity,pdfFileViewModel);


            }
        });



        // Handle favorite action
        addFavoriteBtn.setOnClickListener(v -> {
            pdfFile.isFavorite = !pdfFile.isFavorite;
            pdfFileViewModel.updatePdfFile(pdfFile); // Update using ViewModel
            if (pdfFile.isFavorite) {
                favoriteIcon.setImageResource(R.drawable.favrarte_selected_icon);
                Toast.makeText(fragmentActivity, "Favorite added successfully", Toast.LENGTH_SHORT).show();
            } else {
                favoriteIcon.setImageResource(R.drawable.favrarte_un_selected_icon);
                Toast.makeText(fragmentActivity, "Favorite removed successfully", Toast.LENGTH_SHORT).show();
            }
        });

        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfFile.isFavorite = !pdfFile.isFavorite;
                pdfFileViewModel.updatePdfFile(pdfFile); // Update using ViewModel
                if (pdfFile.isFavorite) {
                    favoriteIcon.setImageResource(R.drawable.favrarte_selected_icon);
                    Toast.makeText(fragmentActivity, "Favorite added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteIcon.setImageResource(R.drawable.favrarte_un_selected_icon);
                    Toast.makeText(fragmentActivity, "Favorite removed successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Handle save file internal storage action
        fileSavedBtn.setOnClickListener(v -> {
            if (!pdfFile.isFileSavedInInternalStorage) {
                // File is NOT saved, so save it
                pdfFile.isFileSavedInInternalStorage = true;

                // Update the UI and ViewModel
                savedIcon.setImageResource(R.drawable.download_pdf_file_selected_icon);
                pdfFileViewModel.updatePdfFile(pdfFile); // Update the file status

                // Generate a unique filename and save to public folder
                String destinationFileName = "SigNscan_" + UUID.randomUUID().toString() + ".pdf";
                String savedFilePath = saveFileToPublicDocuments(fragmentActivity, pdfFile.filePath, destinationFileName);

                // Show toast message with complete path
                if (savedFilePath != null) {
                    Toast.makeText(fragmentActivity, "File Downloaded successfully: " + savedFilePath, Toast.LENGTH_LONG).show();
                    Log.d("checkfilepath", "File path: " + pdfFile.filePath);
                    Log.d("checkfilepath", "Destination file: " + savedFilePath);
                } else {
                    Toast.makeText(fragmentActivity, "Failed to Downloaded file", Toast.LENGTH_SHORT).show();
                }

            } else {
                // File is ALREADY saved
                Toast.makeText(fragmentActivity, "File is already Downloaded", Toast.LENGTH_SHORT).show();
            }
        });


        // Handle save file internal storage action
        savedIcon.setOnClickListener(v -> {
            if (!pdfFile.isFileSavedInInternalStorage) {
                // File is NOT saved, so save it
                pdfFile.isFileSavedInInternalStorage = true;

                // Update the UI and ViewModel
                savedIcon.setImageResource(R.drawable.download_pdf_file_selected_icon);
                pdfFileViewModel.updatePdfFile(pdfFile); // Update the file status

                // Generate a unique filename and save to public folder
                String destinationFileName = "SigNscan_" + UUID.randomUUID().toString() + ".pdf";
                String savedFilePath = saveFileToPublicDocuments(fragmentActivity, pdfFile.filePath, destinationFileName);

                // Show toast message with complete path
                if (savedFilePath != null) {
                    Toast.makeText(fragmentActivity, "File Downloaded successfully: " + savedFilePath, Toast.LENGTH_LONG).show();
                    Log.d("checkfilepath", "File path: " + pdfFile.filePath);
                    Log.d("checkfilepath", "Destination file: " + savedFilePath);
                } else {
                    Toast.makeText(fragmentActivity, "Failed to save file", Toast.LENGTH_SHORT).show();
                }

            } else {
                // File is ALREADY saved
                Toast.makeText(fragmentActivity, "File is already Downloaded", Toast.LENGTH_SHORT).show();
            }
        });






        // Handle delete action
        deletePdfBtn.setOnClickListener(v -> showDeleteDialog(pdfFile, fragmentActivity, pdfFileViewModel, dialog));



        // Show dialog
        dialog.show();
    }



    public static String saveFileToPublicDocuments(Context context, String sourcePath, String fileName) {
        String savedFilePath = null; // Variable to store the saved file path

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore to save in Downloads folder
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            // Insert into MediaStore
            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            try {
                FileInputStream in = new FileInputStream(sourcePath);
                OutputStream out = context.getContentResolver().openOutputStream(uri);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                // Close streams
                in.close();
                out.close();

                savedFilePath = uri.toString(); // Save the file URI as a string

                System.out.println("File successfully saved to Downloads: " + savedFilePath);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error copying file: " + e.getMessage());
            }
        } else {
            // Fallback for Android 9 (API 28) and below
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File destinationFile = new File(downloadsDir, fileName);
            try {
                FileInputStream in = new FileInputStream(sourcePath);
                FileOutputStream out = new FileOutputStream(destinationFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                in.close();
                out.close();

                savedFilePath = destinationFile.getAbsolutePath(); // Save the absolute file path

                System.out.println("File successfully saved to: " + savedFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return savedFilePath; // Return the saved file path or URI
    }



    public static void copyFileToPublicDocuments(String sourcePath, String destinationFileName) {
        // Get Public Documents Directory
        File publicDocumentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!publicDocumentsDir.exists()) {
            publicDocumentsDir.mkdirs(); // Create folder if it does not exist
        }

        File sourceFile = new File(sourcePath);
        File destinationFile = new File(publicDocumentsDir, destinationFileName);

        // Move or Copy the File
        try {
            FileInputStream in = new FileInputStream(sourceFile);
            FileOutputStream out = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            // Close streams
            in.close();
            out.close();

//            // Optional: Delete source file if needed
//            sourceFile.delete();

            Log.d("checkfilepath","File moved successfully to: " + destinationFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("checkfilepath","Error moving file: " + e.getMessage());

        }
    }



    // Show delete confirmation dialog
    public static void showDeleteDialog(PdfFile pdfFile, FragmentActivity fragmentActivity, PdfFileViewModel pdfFileViewModel, Dialog parentDialog) {
        Dialog dialog = new Dialog(fragmentActivity, R.style.renameDialogStyle);
        dialog.setContentView(R.layout.delete_dailog);

//        Window window = dialog.getWindow();
//        if (window != null) {
//            window.setGravity(Gravity.CENTER);
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            window.setBackgroundDrawableResource(android.R.color.transparent);
//        }



        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int desiredWidth = screenWidth - 2 * dpToPx(fragmentActivity, 30);

            WindowManager.LayoutParams params = window.getAttributes();
            params.width = desiredWidth;
            window.setAttributes(params);
        }




        Log.d("checkdelete","check delete side file  "+pdfFile.fileName);

        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        TextView deleteBtn = dialog.findViewById(R.id.deleteBtn);

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        deleteBtn.setOnClickListener(v -> {
            // Delete file from storage and database
            deletePdfFile(pdfFile, fragmentActivity, pdfFileViewModel);
            dialog.dismiss();
            parentDialog.dismiss(); // Close the parent dialog as well
        });

        dialog.show();
    }

    // Delete a PDF file from the database and filesystem
    public static void deletePdfFile(PdfFile pdfFile, FragmentActivity fragmentActivity, PdfFileViewModel pdfFileViewModel) {
        File file = new File(pdfFile.filePath);

        if (file.exists() && file.delete()) {
            pdfFileViewModel.deletePdfFile(pdfFile); // Delete from ViewModel (database)
            Toast.makeText(fragmentActivity, "PDF file deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Delete Operation", "Failed to delete file: " + file.getPath());
            Toast.makeText(fragmentActivity, "Failed to delete file", Toast.LENGTH_SHORT).show();
        }
    }
    // Rename dialog method using ViewModel
    public static void renameDailog(PdfFile pdfFile, FragmentActivity fragmentActivity, PdfFileViewModel pdfFileViewModel) {
        Dialog dialogRename = new Dialog(fragmentActivity, R.style.renameDialogStyle);
        dialogRename.setContentView(R.layout.rename_dailog);

        Window window = dialogRename.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawableResource(android.R.color.transparent);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int desiredWidth = screenWidth - 2 * dpToPx(fragmentActivity, 30);
            WindowManager.LayoutParams params = dialogRename.getWindow().getAttributes();
            params.width = desiredWidth;
            window.setAttributes(params);
        }

        Log.d("checkdelete","check renameDailog side file  "+pdfFile.fileName);


        TextInputEditText pdfNewNameEt = dialogRename.findViewById(R.id.pdfNewNameEt);
        TextView cancelBtn = dialogRename.findViewById(R.id.cancelBtn);
        TextView renameBtn = dialogRename.findViewById(R.id.renameBtn);
        ImageView clearTextIcon = dialogRename.findViewById(R.id.clearTextIcon);



        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRename.dismiss();
            }
        });

        // Clear text button
        clearTextIcon.setOnClickListener(v -> pdfNewNameEt.setText(""));

        // Set current name in EditText
        File file = new File(pdfFile.filePath);
        //get current name of the pdf document to show in the pdfNewNameEt EditText
        String previousName = "" + file.getName();
        Log.d(TAG, "pdfRename: previous Name" + previousName);

        pdfNewNameEt.setText(previousName);

        renameBtn.setOnClickListener(v -> {
            String newName = pdfNewNameEt.getText().toString().trim();
            Log.d(TAG, "onClick: newName: " + newName);

            if (newName.isEmpty()) {
                Toast.makeText(fragmentActivity, "Please enter a name for the PDF document.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    String folderName1 = getFolderName(file.getPath());
                    File newFile = null;

                    // Create the new file path based on the folder
                    switch (folderName1) {
                        case "ID Card":
                            newFile = new File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/ID Card" + "/" + newName + ".pdf");

                            Log.d("checknamefolder", "ID Card" + newFile);

                            break;
                        case "Business Card":
                            newFile = new File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/Business Card" + "/" + newName + ".pdf");

                            Log.d("checknamefolder", "Business Card" + newFile);

                            break;
                        case "Academic Docs":
                            newFile = new File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/Academic Docs" + "/" + newName + ".pdf");

                            Log.d("checknamefolder", "Academic Docs" + newFile);

                            break;
                        case "Personal Tag":
                            newFile = new File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/Personal Tag" + "/" + newName + ".pdf");

                            Log.d("checknamefolder", "Personal Tag" + newFile);

                            break;
                        case "DOCUMENTS":
                            String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));

                            if (extension.equals(".jpg")){
                                newFile = new File(fragmentActivity.getExternalFilesDir(null), Constant.PDF_FOLDER + "/" + newName + ".jpg");

                            }else {
                                newFile = new File(fragmentActivity.getExternalFilesDir(null), Constant.PDF_FOLDER + "/" + newName + ".pdf");

                            }


                            Log.d("checknamefolder", "DOCUMENTS" + newFile);

                            break;
                        case "DigitalSignature":
                            File root = fragmentActivity.getFilesDir();
                            newFile = new File(root + "/DigitalSignature", newName + ".pdf");

                            Log.d("checknamefolder", "DigitalSignature" + newFile);

                            break;
                    }

                    // Rename the file in the filesystem
                    if (newFile != null && file.renameTo(newFile)) {
                        // Update the file path in the database via ViewModel
                        pdfFileViewModel.renamePdfFile(pdfFile.filePath, newFile.getPath());
                        Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show();
                        dialogRename.dismiss();
                    } else {
                        throw new Exception("Failed to rename file");
                    }
                } catch (Exception e) {
                    Log.d(TAG, "renameBtn onClick: ", e);
                    Toast.makeText(fragmentActivity, "Failed to rename due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialogRename.show();
    }

    public static String getFolderName(String path) {
        File file = new File(path);
        return file.getParentFile().getName();
    }

    // Password dialog method using ViewModel
    public static void setPasswordDialog(PdfFile pdfFile, FragmentActivity fragmentActivity, PdfFileViewModel pdfFileViewModel) {
        Dialog dialog = new Dialog(fragmentActivity, R.style.renameDialogStyle);
        dialog.setContentView(R.layout.new_lock_dialog);


        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int desiredWidth = screenWidth - 2 * dpToPx(fragmentActivity, 0);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = desiredWidth;
            window.setAttributes(params);
        }

        Log.d("checkdelete","check setPasswordDialog side file  "+pdfFile.fileName);


        EditText passwordEt = dialog.findViewById(R.id.passwordEt);
        EditText confirmPasswordEt = dialog.findViewById(R.id.confirmPasswordEt);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        TextView saveBtn = dialog.findViewById(R.id.saveBtn);

        passwordEt.setText(pdfFile.password);
        confirmPasswordEt.setText(pdfFile.password);

//        saveBtn.setOnClickListener(v -> {
//            String password = passwordEt.getText().toString();
//            String confirmPassword = confirmPasswordEt.getText().toString();
//
//            if (password.equals(confirmPassword)) {
//                pdfFile.password = password;
//                pdfFileViewModel.updateTest(pdfFile.id,pdfFile.filePath, previousName, password, pdfFile.fileSize,pdfFile.createdDate,pdfFile.isFavorite,pdfFile.isSignedFiles); // Update using ViewModel
//                Toast.makeText(fragmentActivity, "Password set successfully", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//            } else {
//                Toast.makeText(fragmentActivity, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
//            }
//        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEt.getText().toString();
                String confirmPassword = confirmPasswordEt.getText().toString();

                if (password.isEmpty()) {
                    passwordEt.setError("Please Enter Password");
                }

                if (confirmPassword.isEmpty()) {
                    confirmPasswordEt.setError("Please Enter Confirm Password");
                }

                if (!password.isEmpty() && !confirmPassword.isEmpty()) {
                    if (password.equals(confirmPassword)) {
                        // Passwords match, proceed with the submission
//                        convertMultipleImagesToPdf(true, data, password, fileName);

                        File fileName = new File(pdfFile.filePath);
                        String previousName = "" + fileName.getName();
//                        PdfFile pdfFileUpdate = new PdfFile(pdfFile.filePath, previousName, password, pdfFile.fileSize,pdfFile.createdDate,pdfFile.isFavorite,pdfFile.isSignedFiles,false,false,false,false);


                        pdfFileViewModel.updateTest(pdfFile.id,pdfFile.filePath, previousName, password, pdfFile.fileSize,pdfFile.createdDate,pdfFile.isFavorite,pdfFile.isSignedFiles); // Update using ViewModel



                        dialog.dismiss();
                    } else {
                        // Passwords do not match. Please try again.
                        Toast.makeText(fragmentActivity, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }




            }
        });



        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Utility to convert dp to pixels
    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }


    public static void changeStatusBarColor(int color, FragmentActivity fragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = fragmentActivity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}
