package com.pixelz360.docsign.imagetopdf.creator.RoomDb;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters.ThumbnailResult;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtils {

    public static CharSequence loadFileSize(PdfFile modelPdf) {
        File file = new File(modelPdf.filePath);
        double bytes = file.length();
        double kb = bytes / 1024;
        double mb = kb / 1024;

        String size = (mb >= 1) ? String.format("%.2f MB", mb) : (kb >= 1) ? String.format("%.2f KB", kb) : String.format("%.2f bytes", bytes);
        Log.d(TAG, "loadFileSize: File Size: " + size);
        return size;
    }


    public static CharSequence loadFileDate(PdfFile pdfFile) {
        File file = new File(pdfFile.filePath);
        long timestamp = file.lastModified();
        String formattedDate = MainApplication.formatTimestamp(timestamp);
        return formattedDate;
    }



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
            int finalPageCount = pageCount;
            handler.post(() -> {
                Glide.with(fragmentActivity).load(finalThumbnailBitmap).fitCenter().placeholder(R.drawable.ic_pdf_black).into(thumbnailIv);
//                holder.page.setText("" + finalPageCount + "  Pages");
            });
        });


    }

    public static void threeDotDailog(PdfFile pdfFile, FragmentActivity fragmentActivity, AppDatabase db) {


        Dialog dialog = new Dialog(fragmentActivity, R.style.FileSortingDialogStyle);
        dialog.setContentView(R.layout.three_dot_dailog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Ensures no background
        }

        ShapeableImageView thumbnailIv = dialog.findViewById(R.id.thumbnailIv);
        TextView pdf_name = dialog.findViewById(R.id.pdf_name);
        TextView sizeTv = dialog.findViewById(R.id.sizeTv);
        TextView dateTv = dialog.findViewById(R.id.dateTv);
        ImageView closeDailogBtn = dialog.findViewById(R.id.closeDailogBtn);
        ImageView favoriteIcon = dialog.findViewById(R.id.favoriteIcon);
        RelativeLayout renamePdfBtn = dialog.findViewById(R.id.renamePdfBtn);
        RelativeLayout editPdfBtn = dialog.findViewById(R.id.editPdfBtn);
        RelativeLayout setPasswordBtn = dialog.findViewById(R.id.setPasswordBtn);
        RelativeLayout addFavoriteBtn = dialog.findViewById(R.id.addFavoriteBtn);
        RelativeLayout deletePdfBtn = dialog.findViewById(R.id.deletePdfBtn);

        pdf_name.setText(pdfFile.fileName);
        sizeTv.setText(FileUtils.loadFileSize(pdfFile));
        dateTv.setText(FileUtils.loadFileDate(pdfFile));
        FileUtils.loadThumbnailFromPdfFile(pdfFile,thumbnailIv,fragmentActivity);
        favoriteIcon.setImageResource(pdfFile.isFavorite ? R.drawable.favrarte_selected_icon : R.drawable.favrarte_un_selected_icon);

        renamePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                renameDailog(pdfFile,fragmentActivity,db);

                dialog.dismiss();

            }
        });

        setPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                setPasswordDialog( requireActivity());
                setPasswordDialog(pdfFile,fragmentActivity,db);


            }
        });


        addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pdfFile.isFavorite = !pdfFile.isFavorite;
                new Thread(() -> db.pdfFileDao().updatePdfFile(pdfFile)).start();
//                favoriteIcon.setImageResource(pdfFile.isFavorite ? R.drawable.favrarte_selected_icon : R.drawable.favrarte_un_selected_icon);

                if (pdfFile.isFavorite){
                    favoriteIcon.setImageResource(R.drawable.favrarte_selected_icon);

                    Toast.makeText(fragmentActivity,"Favorite added successfully",Toast.LENGTH_SHORT).show();
                }else {
                    favoriteIcon.setImageResource(R.drawable.favrarte_un_selected_icon);
                    Toast.makeText(fragmentActivity,"Favorite removed successfully.",Toast.LENGTH_SHORT).show();
                }


            }
        });

        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfFile.isFavorite = !pdfFile.isFavorite;
                new Thread(() -> db.pdfFileDao().updatePdfFile(pdfFile)).start();
//                favoriteIcon.setImageResource(pdfFile.isFavorite ? R.drawable.favrarte_selected_icon : R.drawable.favrarte_un_selected_icon);

                if (pdfFile.isFavorite){
                    favoriteIcon.setImageResource(R.drawable.favrarte_selected_icon);

                    Toast.makeText(fragmentActivity,"Favorite added successfully",Toast.LENGTH_SHORT).show();
                }else {
                    favoriteIcon.setImageResource(R.drawable.favrarte_un_selected_icon);
                    Toast.makeText(fragmentActivity,"Favorite removed successfully.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        deletePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialogRename = new Dialog(fragmentActivity, R.style.renameDialogStyle);
                dialogRename.setContentView(R.layout.delete_dailog);

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



                TextView cancelBtn = dialogRename.findViewById(R.id.cancelBtn);
                TextView deleteBtn = dialogRename.findViewById(R.id.deleteBtn);



                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogRename.dismiss();
                    }
                });

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db.pdfFileDao().deletePdfFile(pdfFile);

                                fragmentActivity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
//                                Intent intent = new Intent( requireActivity(), MainActivity.class);
//                                startActivity(intent);

                                        // Toast.makeText(Prview_Screen.this,"File Deleted Successfully",Toast.LENGTH_SHORT).show()

                                        File file = new File(pdfFile.filePath);
                                        if (file.exists()) {
                                            if (file.delete()) {
                                                Log.d("Delete Operation1", "File deleted successfully: " + pdfFile.filePath);

                                                Toast.makeText(fragmentActivity,"File deleted successfully",Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                dialogRename.dismiss();
                                            } else {
                                                Log.d("Delete Operation1", "Failed to delete file: " + pdfFile.filePath);

                                                Toast.makeText(fragmentActivity,"File deleted successfully",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();




                    }
                });

                dialogRename.show();











            }
        });


        closeDailogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        dialog.show();







    }

    private static void setPasswordDialog(PdfFile pdfFile, FragmentActivity fragmentActivity, AppDatabase db) {
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

        EditText passwordEt = dialog.findViewById(R.id.passwordEt);
        EditText confirmPasswordEt = dialog.findViewById(R.id.confirmPasswordEt);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        TextView saveBtn = dialog.findViewById(R.id.saveBtn);


        // Set up the input
        passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        passwordEt.setText(pdfFile.password);
        confirmPasswordEt.setText(pdfFile.password);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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
//                        PdfFile pdfFileUpdate = new PdfFile(pdfFile.filePath, previousName,pdfFile.getFileTag(), password, pdfFile.fileSize,pdfFile.createdDate,pdfFile.isFavorite,pdfFile.isSignedFiles,false,false,false,false);


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                Log .d("checkfile1","Please Enter Confirm Password  "+pdfFileUpdate.password);
//                                db.pdfFileDao().updatePdfFilePassword(pdfFileUpdate);
                                db.pdfFileDao().updateTest(pdfFile.id,pdfFile.filePath, previousName, password, pdfFile.fileSize,pdfFile.createdDate,pdfFile.isFavorite,pdfFile.isSignedFiles);

                            }
                        }).start();


                        dialog.dismiss();
                    } else {
                        // Passwords do not match. Please try again.
                        Toast.makeText(fragmentActivity, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }




            }
        });

        dialog.show();
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static void renameDailog(PdfFile pdfFile, FragmentActivity fragmentActivity, AppDatabase db) {

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



        TextInputEditText pdfNewNameEt = dialogRename.findViewById(R.id.pdfNewNameEt);
        TextView cancelBtn = dialogRename.findViewById(R.id.cancelBtn);
        TextView renameBtn = dialogRename.findViewById(R.id.renameBtn);
        ImageView clearTextIcon = dialogRename.findViewById(R.id.clearTextIcon);

        clearTextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfNewNameEt.setText("");
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRename.dismiss();
            }
        });

        File file = new File(pdfFile.filePath);
        //get current name of the pdf document to show in the pdfNewNameEt EditText
        String previousName = "" + file.getName();
        Log.d(TAG, "pdfRename: previous Name" + previousName);

        pdfNewNameEt.setText(previousName);


        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newName = pdfNewNameEt.getText().toString().trim();
                Log.d(TAG, "onClick: newName: " + newName);

                if (newName.isEmpty()) {
                    Toast.makeText(fragmentActivity, "Please enter a name for the PDF document. document.", Toast.LENGTH_SHORT).show();
                } else {
                    try {

                        // Extract folder names
                        String folderName1 = getFolderName(file.getPath());

                        if (folderName1.equals("ID Card")){
                            File newFile = new File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/ID Card" + "/" + newName + ".pdf");
                            file.renameTo(newFile);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db.pdfFileDao().renameFile(pdfFile.filePath , newFile.getPath());

                                }
                            }).start();


                            Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show();
                            dialogRename.dismiss();

                        }else if (folderName1.equals("Business Card")){
                            File newFile = new File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/Business Card" + "/" + newName + ".pdf");
                            file.renameTo(newFile);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db.pdfFileDao().renameFile(pdfFile.filePath , newFile.getPath());

                                }
                            }).start();


                            Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show();
                            dialogRename.dismiss();

                        }else if (folderName1.equals("Academic Docs")){
                            File newFile = new File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/Academic Docs" + "/" + newName + ".pdf");
                            file.renameTo(newFile);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db.pdfFileDao().renameFile(pdfFile.filePath , newFile.getPath());

                                }
                            }).start();


                            Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show();
                            dialogRename.dismiss();

                        }else if (folderName1.equals("Personal Tag")){
                            File newFile = new File(fragmentActivity.getExternalFilesDir(null), "IMAGE TO PDF/Personal Tag" + "/" + newName + ".pdf");
                            file.renameTo(newFile);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db.pdfFileDao().renameFile(pdfFile.filePath , newFile.getPath());

                                }
                            }).start();


                            Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show();
                            dialogRename.dismiss();

                        }
                        else  if (folderName1.equals("DOCUMENTS")){
                            File newFile = new File(fragmentActivity.getExternalFilesDir(null), Constant.PDF_FOLDER + "/" + newName + ".pdf");
                            file.renameTo(newFile);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db.pdfFileDao().renameFile(pdfFile.filePath , newFile.getPath());

                                }
                            }).start();


                            Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show();
                            dialogRename.dismiss();

                        }
                        else if (folderName1.equals("DigitalSignature")){

                            File root = fragmentActivity.getFilesDir();
                            File newFile = new File(root + "/DigitalSignature", newName+ ".pdf");
                            file.renameTo(newFile);


                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db.pdfFileDao().renameFile(pdfFile.filePath , newFile.getPath());

                                }
                            }).start();


                            Toast.makeText(fragmentActivity, "Rename Successfully...", Toast.LENGTH_SHORT).show();
                            dialogRename.dismiss();
                        }

                        Log.d(TAG, "11sfwerr112 " + folderName1);











                    } catch (Exception e) {
                        Log.d(TAG, "renameBtn onClick: ", e);
                        Toast.makeText(fragmentActivity, "Failed to rename due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        dialogRename.show();


    }

    public static String getFolderName(String path) {
        File file = new File(path);
        return file.getParentFile().getName();
    }



    public static ThumbnailResult generatePdfThumbnail(String filePath) {
        try (ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(new File(filePath), ParcelFileDescriptor.MODE_READ_ONLY);
             PdfRenderer renderer = new PdfRenderer(descriptor)) {

            int pageCount = renderer.getPageCount();
            if (pageCount > 0) {
                PdfRenderer.Page page = renderer.openPage(0);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                return new ThumbnailResult(bitmap, pageCount);  // Return both bitmap and page count
            }
        } catch (Exception e) {
            Log.e("PdfAdapterRoom", "Error generating thumbnail", e);
        }
        return new ThumbnailResult(null, 0);
    }




}
