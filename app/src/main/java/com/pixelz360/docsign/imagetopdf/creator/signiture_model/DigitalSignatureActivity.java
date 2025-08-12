package com.pixelz360.docsign.imagetopdf.creator.signiture_model;


import static com.pixelz360.docsign.imagetopdf.creator.RoomDb.FileUtils.dpToPx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.AppDatabase;
import com.pixelz360.docsign.imagetopdf.creator.bar_and_qr_code.AdManager;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityDigitalSignatureBinding;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.Activity.SignatureActivity;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.Activity.SignatureUtils;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSElementViewer;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSPageViewer;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSSaveAsPDFAsyncTask;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document.PDSViewPager;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.PDF.PDSPDFDocument;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.PDSModel.PDSElement;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.imageviewer.PDSPageAdapter;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.KeyStore;
import java.security.Security;
import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DigitalSignatureActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private static final int SIGNATURE_Request_CODE = 43;
    private static final int IMAGE_REQUEST_CODE = 45;
    private static final int DIGITALID_REQUEST_CODE = 44;
    Uri pdfData = null;
    private PDSViewPager mViewPager;
    PDSPageAdapter imageAdapter;
    private Context mContext = null;
    private RecyclerView mRecyclerView;
    private boolean mFirstTap = true;
    private int mVisibleWindowHt = 0;
    private PDSPDFDocument mDocument = null;
    private Uri mdigitalID = null;
    public String mdigitalIDPassword = null;
    private Menu mmenu = null;
    private final UIElementsHandler mUIElemsHandler = new UIElementsHandler(this);
    AlertDialog passwordalertDialog;
    AlertDialog signatureOptionDialog;
    public KeyStore keyStore = null;
    public String alises = null;
    public boolean isSigned = false;
    public ProgressBar savingProgress;

    ActivityDigitalSignatureBinding binding;

//    private AppDatabase db;
         PdfFileViewModel pdfFileViewModel;


    final String[] renameFileName = new String[1];
    final String[] password = {null};


    private InterstitialAd mInterstitialAd = null;
    private boolean isAdLoading = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDigitalSignatureBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(binding.getRoot());
        this.mContext = getApplicationContext();
        mViewPager = findViewById(R.id.viewpager);
        savingProgress = findViewById(R.id.savingProgress);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        // Get color from resources
        int statusBarColor = ContextCompat.getColor(DigitalSignatureActivity.this, R.color.white);
        // Change status bar color
        FileUtils.changeStatusBarColor(statusBarColor,DigitalSignatureActivity.this);




//        loadInterstitialAd();

        pdfFileViewModel = new ViewModelProvider(this).get(PdfFileViewModel.class);







        // Scroll Up button functionality
        binding.btnScrollUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem > 0) {
                    mViewPager.setCurrentItem(currentItem - 1, true);
                }
            }
        });

        // Scroll Down button functionality
        binding.btnScrollDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem < mViewPager.getAdapter().getCount() - 1) {
                    mViewPager.setCurrentItem(currentItem + 1, true);
                }
            }
        });











        // Initialize the database
//        db = Room.databaseBuilder(DigitalSignatureActivity.this, AppDatabase.class, "pdf_database").build();

        Intent intent = getIntent();
        String message = intent.getStringExtra("ActivityAction");
        if (message.equals("FileSearch")) {
            performFileSearch();
        } else if (message.equals("PDFOpen")) {
            ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra("PDFOpen");
            if (imageUris != null) {
                for (int i = 0; i < imageUris.size(); i++) {
                    Uri imageUri = imageUris.get(i);
                    OpenPDFViewer((imageUri));
                }
            }
        }



        // Log a predefined event
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle1 = new Bundle();
        bundle1.putString("activity_name", "DigitalSignatureActivity");
        analytics.logEvent("activity_created", bundle1);
// Using predefined Firebase Analytics events
        bundle1.putString(FirebaseAnalytics.Param.ITEM_NAME, "DigitalSignatureActivity");
        bundle1.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle1);


//        binding.fromCollection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(DigitalSignatureActivity.this, SignatureActivity.class);
//                startActivityForResult(intent, SIGNATURE_Request_CODE);
//
//
//
//                binding.pickImageImageView.setColorFilter(ContextCompat.getColor(DigitalSignatureActivity.this, R.color.un_select_signature_color), PorterDuff.Mode.SRC_ATOP);
//                binding.fromCollcationImageView.setColorFilter(ContextCompat.getColor(DigitalSignatureActivity.this, R.color.red), PorterDuff.Mode.SRC_ATOP);
//
//                // Set text colors
//                binding.fromCollactionTextView.setTextColor(ContextCompat.getColor(DigitalSignatureActivity.this, R.color.red));
//                binding.fromImageTextView.setTextColor(ContextCompat.getColor(DigitalSignatureActivity.this, R.color.un_select_signature_color));
//
//
//
//
//            }
//        });
//
//
//
//        binding.fromImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.setType("image/jpeg");
//                String[] mimetypes = {"image/jpeg", "image/png"};
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                startActivityForResult(intent, IMAGE_REQUEST_CODE);
//
//
//
//                binding.pickImageImageView.setColorFilter(ContextCompat.getColor(DigitalSignatureActivity.this, R.color.red), PorterDuff.Mode.SRC_ATOP);
//                binding.fromCollcationImageView.setColorFilter(ContextCompat.getColor(DigitalSignatureActivity.this, R.color.un_select_signature_color), PorterDuff.Mode.SRC_ATOP);
//
//                // Set text colors
//                binding.fromCollactionTextView.setTextColor(ContextCompat.getColor(DigitalSignatureActivity.this, R.color.un_select_signature_color));
//                binding.fromImageTextView.setTextColor(ContextCompat.getColor(DigitalSignatureActivity.this, R.color.red));
//
//
//
//            }
//        });




        binding.actionSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(DigitalSignatureActivity.this, R.style.CustomDialogStyle);
                dialog.setContentView(R.layout.action_signiture_bottom_sheet_layout);
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setGravity(Gravity.BOTTOM);
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Ensures no background
                }


                LinearLayout signature = dialog.findViewById(R.id.fromCollection);
                LinearLayout image = dialog.findViewById(R.id.fromImage);
                LinearLayout mainLayout = dialog.findViewById(R.id.mainLayout);



                signature.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Intent intent = new Intent(DigitalSignatureActivity.this, SignatureActivity.class);
                        startActivityForResult(intent, SIGNATURE_Request_CODE);
                        dialog.dismiss();

                    }
                });



                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("image/jpeg");
                        String[] mimetypes = {"image/jpeg", "image/png"};
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                        startActivityForResult(intent, IMAGE_REQUEST_CODE);

                        dialog.dismiss();
                    }
                });



                dialog.show();
            }
        });


        binding.actionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePDFDocument();
            }
        });







        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardDailog();
            }
        });



    }



    private void goToNextActivity() {

        Log.d("checkopen","password[0]   "+password[0]);

        PDSSaveAsPDFAsyncTask task = new PDSSaveAsPDFAsyncTask(DigitalSignatureActivity.this, renameFileName[0] + ".pdf", password[0],pdfFileViewModel);
        task.execute(new Void[0]);

    }



    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }



    private void discardDailog() {




        Dialog dialogRename = new Dialog(DigitalSignatureActivity.this, R.style.renameDialogStyle);
        dialogRename.setContentView(R.layout.discard_dailog);

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
            int desiredWidth = screenWidth - 2 * dpToPx(DigitalSignatureActivity.this, 30);
            WindowManager.LayoutParams params = dialogRename.getWindow().getAttributes();
            params.width = desiredWidth;
            window.setAttributes(params);
        }



        TextView cancelBtn = dialogRename.findViewById(R.id.cancelBtn);
        TextView discardBtn = dialogRename.findViewById(R.id.discardBtn);



        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRename.dismiss();
            }
        });

        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new  Intent(DigitalSignatureActivity.this, HomeActivity.class);
                startActivity(intent);
                dialogRename.dismiss();



            }
        });

        dialogRename.show();



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == READ_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    pdfData = result.getData();
                    OpenPDFViewer(pdfData);
                    Log.d("checkpdf","pdf file");
                }
            } else {
                finish();
            }
        }
        if (requestCode == SIGNATURE_Request_CODE && resultCode == Activity.RESULT_OK) {
            String returnValue = result.getStringExtra("FileName");
            File fi = new File(returnValue);
            this.addElement(PDSElement.PDSElementType.PDSElementTypeSignature, fi, (float) SignatureUtils.getSignatureWidth((int) getResources().getDimension(R.dimen.sign_field_default_height), fi, getApplicationContext()), getResources().getDimension(R.dimen.sign_field_default_height));

        }
        if (requestCode == DIGITALID_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    mdigitalID = result.getData();
                    GetPassword();
                }
            } else {
                Toast.makeText(DigitalSignatureActivity.this, "Digital certificate not included in the signature.", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (result != null) {
                Uri imageData = result.getData();
                Bitmap bitmap = null;
                try {
                    InputStream input = this.getContentResolver().openInputStream(imageData);
                    bitmap = BitmapFactory.decodeStream(input);
                    input.close();
                    if (bitmap != null)
                        this.addElement(PDSElement.PDSElementType.PDSElementTypeImage, bitmap, getResources().getDimension(R.dimen.sign_field_default_height), getResources().getDimension(R.dimen.sign_field_default_height));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.mmenu = menu;
        MenuItem saveItem = mmenu.findItem(R.id.action_save);
        saveItem.getIcon().setAlpha(130);
        MenuItem signItem = mmenu.findItem(R.id.action_sign);
        signItem.getIcon().setAlpha(255);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_sign) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.optiondialog, null);
            dialogBuilder.setView(dialogView);

            Button signature = dialogView.findViewById(R.id.fromCollection);

            signature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignatureActivity.class);
                    startActivityForResult(intent, SIGNATURE_Request_CODE);
                    signatureOptionDialog.dismiss();
                }
            });


            Button image = dialogView.findViewById(R.id.fromImage);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("image/jpeg");
                    String[] mimetypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                    signatureOptionDialog.dismiss();
                }
            });

            signatureOptionDialog = dialogBuilder.create();
            signatureOptionDialog.show();
            return true;
        }

        if (id == R.id.action_save) {
            savePDFDocument();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        discardDailog();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        discardDailog();

//        if (isSigned) {
//            final AlertDialog alertDialog = new AlertDialog.Builder(this)
//                    .setTitle("Save Document")
//                    .setMessage("Want to save your changes to PDF document?")
//
//                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            savePDFDocument();
//                        }
//                    })
//                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    }).show();
//        } else {
//            finish();
//        }
    }

    private void OpenPDFViewer(Uri pdfData) {
        try {
            PDSPDFDocument document = new PDSPDFDocument(this, pdfData);
            document.open();
            this.mDocument = document;
            imageAdapter = new PDSPageAdapter(getSupportFragmentManager(), document);
            updatePageNumber(1);
            mViewPager.setAdapter(imageAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(DigitalSignatureActivity.this, "CCannot open the PDF. It may be corrupted or password protected", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/jpeg");
        String[] mimetypes = {"application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private int computeVisibleWindowHtForNonFullScreenMode() {
        return findViewById(R.id.docviewer).getHeight();
    }


    public boolean isFirstTap() {
        return this.mFirstTap;
    }

    public void setFirstTap(boolean z) {
        this.mFirstTap = z;
    }

    public int getVisibleWindowHeight() {
        if (this.mVisibleWindowHt == 0) {
            this.mVisibleWindowHt = computeVisibleWindowHtForNonFullScreenMode();
        }
        return this.mVisibleWindowHt;
    }

    public PDSPDFDocument getDocument() {
        return this.mDocument;
    }

    public void GetPassword() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.passworddialog, null);
        dialogBuilder.setView(dialogView);

        final EditText password = dialogView.findViewById(R.id.passwordText);
        Button submit = dialogView.findViewById(R.id.passwordSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.length() == 0) {
                    Toast.makeText(DigitalSignatureActivity.this, "Password cannot be blank", Toast.LENGTH_LONG).show();
                } else {
                    mdigitalIDPassword = password.getText().toString();
                    BouncyCastleProvider provider = new BouncyCastleProvider();
                    Security.addProvider(provider);
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(mdigitalID);
                        keyStore = KeyStore.getInstance("pkcs12", provider.getName());
                        keyStore.load(inputStream, mdigitalIDPassword.toCharArray());
                        alises = keyStore.aliases().nextElement();
                        passwordalertDialog.dismiss();
                        Toast.makeText(DigitalSignatureActivity.this, "Digital certificate is added with Signature", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        if (e.getMessage().contains("wrong password")) {
                            Toast.makeText(DigitalSignatureActivity.this, "Password is incorrect or certificate is corrupted", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(DigitalSignatureActivity.this, "Something went wrong while adding Digital certificate", Toast.LENGTH_LONG).show();
                            passwordalertDialog.dismiss();
                        }
                        e.printStackTrace();
                    }
                }
            }
        });

        passwordalertDialog = dialogBuilder.create();
        passwordalertDialog.show();
    }

    public void invokeMenuButton(boolean disableButtonFlag) {
//        MenuItem saveItem = mmenu.findItem(R.id.action_save);
//        saveItem.setEnabled(disableButtonFlag);
//        MenuItem signPDF = mmenu.findItem(R.id.action_sign);
//        //signPDF.setEnabled(!disableButtonFlag);
//        isSigned = disableButtonFlag;
//        if (disableButtonFlag) {
//            //signPDF.getIcon().setAlpha(130);
//            saveItem.getIcon().setAlpha(255);
//        } else {
//            //signPDF.getIcon().setAlpha(255);
//            saveItem.getIcon().setAlpha(130);
//
//        }
    }

    public void addElement(PDSElement.PDSElementType fASElementType, File file, float f, float f2) {

        Log.d("checksig","addElement");

        View focusedChild = this.mViewPager.getFocusedChild();
        if (focusedChild != null) {
            PDSPageViewer fASPageViewer = (PDSPageViewer) ((ViewGroup) focusedChild).getChildAt(0);
            if (fASPageViewer != null) {
                RectF visibleRect = fASPageViewer.getVisibleRect();
                float width = (visibleRect.left + (visibleRect.width() / 2.0f)) - (f / 2.0f);
                float height = (visibleRect.top + (visibleRect.height() / 2.0f)) - (f2 / 2.0f);
                PDSElementViewer lastFocusedElementViewer = fASPageViewer.getLastFocusedElementViewer();

                PDSElement.PDSElementType fASElementType2 = fASElementType;
                final PDSElement element = fASPageViewer.createElement(fASElementType2, file, width, height, f, f2);

                if (!isSigned) {
//                    AlertDialog dialog;
//                    AlertDialog.Builder builder = new AlertDialog.Builder(DigitalSignatureActivity.this);
//                    builder.setMessage("Do you want to add digital certificate with this Signature?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                                    intent.setType("application/keychain_access");
//                                    String[] mimetypes = {"application/x-pkcs12"};
//                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                                    startActivityForResult(intent, DIGITALID_REQUEST_CODE);
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    dialog = builder.create();
//                    dialog.show();
                }
            }
            invokeMenuButton(true);
        }
    }


    public void addElement(PDSElement.PDSElementType fASElementType, Bitmap bitmap, float f, float f2) {
        View focusedChild = this.mViewPager.getFocusedChild();
        if (focusedChild != null && bitmap != null) {
            PDSPageViewer fASPageViewer = (PDSPageViewer) ((ViewGroup) focusedChild).getChildAt(0);
            if (fASPageViewer != null) {
                RectF visibleRect = fASPageViewer.getVisibleRect();
                float width = (visibleRect.left + (visibleRect.width() / 2.0f)) - (f / 2.0f);
                float height = (visibleRect.top + (visibleRect.height() / 2.0f)) - (f2 / 2.0f);
                PDSElementViewer lastFocusedElementViewer = fASPageViewer.getLastFocusedElementViewer();

                PDSElement.PDSElementType fASElementType2 = fASElementType;
                final PDSElement element = fASPageViewer.createElement(fASElementType2, bitmap, width, height, f, f2);
                if (!isSigned) {
//                    AlertDialog dialog;
//                    AlertDialog.Builder builder = new AlertDialog.Builder(DigitalSignatureActivity.this);
//                    builder.setMessage("Do you want to add digital certificate with this Signature?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                                    intent.setType("application/keychain_access");
//                                    String[] mimetypes = {"application/x-pkcs12"};
//                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                                    startActivityForResult(intent, DIGITALID_REQUEST_CODE);
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    dialog = builder.create();
//                    dialog.show();
                }
            }
            invokeMenuButton(true);
        }
    }

    public void updatePageNumber(int i) {
        TextView textView = (TextView) findViewById(R.id.pageNumberTxt);
        findViewById(R.id.pageNumberOverlay).setVisibility(View.VISIBLE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i);
        stringBuilder.append("/");
        stringBuilder.append(this.mDocument.getNumPages());
        textView.setText(stringBuilder.toString());
        resetTimerHandlerForPageNumber(1000);
    }

    private void resetTimerHandlerForPageNumber(int i) {
        this.mUIElemsHandler.removeMessages(1);
        Message message = new Message();
        message.what = 1;
        this.mUIElemsHandler.sendMessageDelayed(message, (long) i);
    }

    private void fadePageNumberOverlay() {
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        View findViewById = findViewById(R.id.pageNumberOverlay);
        if (findViewById.getVisibility() == View.VISIBLE) {
            findViewById.startAnimation(loadAnimation);
            findViewById.setVisibility(View.INVISIBLE);
        }
    }

    private static class UIElementsHandler extends Handler {
        private final WeakReference<DigitalSignatureActivity> mActivity;

        public UIElementsHandler(DigitalSignatureActivity fASDocumentViewer) {
            this.mActivity = new WeakReference(fASDocumentViewer);
        }

        public void handleMessage(Message message) {
            DigitalSignatureActivity fASDocumentViewer = this.mActivity.get();
            if (fASDocumentViewer != null && message.what == 1) {
                fASDocumentViewer.fadePageNumberOverlay();
            }
            super.handleMessage(message);
        }
    }


    public void runPostExecution() {
        savingProgress.setVisibility(View.INVISIBLE);
        makeResult();

    }

    public void makeResult() {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    public void savePDFDocument() {



        Dialog dialog = new Dialog(DigitalSignatureActivity.this, R.style.FileSortingDialogStyle);
        dialog.setContentView(R.layout.convert_to_pdf_dialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        LinearLayout convertPdfBtn = dialog.findViewById(R.id.convertPdfBtn);
        CheckBox checkPssword = dialog.findViewById(R.id.checkPssword);
        RelativeLayout addpassword = dialog.findViewById(R.id.addpassword);
        RelativeLayout editRelativeLayout = dialog.findViewById(R.id.editRelativeLayout);
        TextView saveBtntv = dialog.findViewById(R.id.saveBtntv);
        TextView titletv = dialog.findViewById(R.id.titletv);
        TextView fileName = dialog.findViewById(R.id.fileName);

        saveBtntv.setText("Save Document");
        titletv.setText("Process to Proceed");

        //name with extension of the image
//        long timestamp = System.currentTimeMillis();
//        String mainFileName = "Untitled_file_Doc24 " + timestamp;


        long timestamp = System.currentTimeMillis();
        long seconds = (timestamp / 1000) % 60; // Extract seconds from timestamp
        String mainFileName = "Untitled Signed Doc " + seconds;

        renameFileName[0] = mainFileName;





        editRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = LayoutInflater.from(DigitalSignatureActivity.this).inflate(R.layout.rename_dailog, null);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextInputEditText pdfNewNameEt = view.findViewById(R.id.pdfNewNameEt);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView renameBtn = view.findViewById(R.id.renameBtn);
                TextView cancelBtn = view.findViewById(R.id.cancelBtn);
                ImageView clearTextIcon = view.findViewById(R.id.clearTextIcon);

                clearTextIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pdfNewNameEt.setText("");
                    }
                });


                pdfNewNameEt.setText(renameFileName[0]);


                AlertDialog.Builder builder = new AlertDialog.Builder(DigitalSignatureActivity.this);
                builder.setView(view);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                renameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newName = pdfNewNameEt.getText().toString().trim();

                        if (newName.isEmpty()) {
                            Toast.makeText(DigitalSignatureActivity.this, "Please enter a name for the PDF document. document.", Toast.LENGTH_SHORT).show();
                        } else {

                            renameFileName[0] = newName;
                            fileName.setText(renameFileName[0]);

                            alertDialog.dismiss();
                        }
                    }
                });




            }
        });

        fileName.setText(renameFileName[0]);




        addpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked;


                Dialog dialog = new Dialog(DigitalSignatureActivity.this, R.style.renameDialogStyle);
                dialog.setContentView(R.layout.new_lock_dialog);
                dialog.setCancelable(false);


                if (!checkPssword.isChecked()){
                    checkPssword.setChecked(true);




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
                        int desiredWidth = screenWidth - 2 * dpToPx(DigitalSignatureActivity.this, 0);
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.width = desiredWidth;
                        window.setAttributes(params);
                    }



                    EditText passwordEt = dialog.findViewById(R.id.passwordEt);
                    EditText confirmPasswordEt = dialog.findViewById(R.id.confirmPasswordEt);
                    TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
                    TextView saveBtn = dialog.findViewById(R.id.saveBtn);
                    TextView rateUsBtn = dialog.findViewById(R.id.rateUsBtn);


                    // Set up the input
                    passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            password[0] = passwordEt.getText().toString();
                            String confirmPassword = confirmPasswordEt.getText().toString();

                            if (password[0].isEmpty()) {
                                passwordEt.setError("Please Enter Password");
                            }

                            if (confirmPassword.isEmpty()) {
                                confirmPasswordEt.setError("Please Enter Confirm Password");
                            }

                            if (!password[0].isEmpty() && !confirmPassword.isEmpty()) {
                                if (password[0].equals(confirmPassword)) {
                                    // Passwords match, proceed with the submission

                                    dialog.dismiss();
                                } else {
                                    // Passwords do not match. Please try again.
                                    Toast.makeText(DigitalSignatureActivity.this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }




                        }
                    });


                    dialog.show();


                }else {
                    checkPssword.setChecked(false);
                    dialog.dismiss();
                }
            }
        });

//        checkPssword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//
//                if (checkPssword.isChecked()){
////
//                    Log.d("checkopen","open dailog");
//
//                    Dialog dialog = new Dialog(DigitalSignatureActivity.this, R.style.renameDialogStyle);
//                    dialog.setContentView(R.layout.new_lock_dialog);
//                    dialog.setCancelable(false);
//
//                    Window window = dialog.getWindow();
//                    if (window != null) {
//                        window.setGravity(Gravity.CENTER);
//                        window.setLayout(
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT
//                        );
//                        window.setBackgroundDrawableResource(android.R.color.transparent);
//                        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
//                        int screenWidth = metrics.widthPixels;
//                        int desiredWidth = screenWidth - 2 * dpToPx(DigitalSignatureActivity.this, 0);
//                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//                        params.width = desiredWidth;
//                        window.setAttributes(params);
//                    }
//
//
//
//                    EditText passwordEt = dialog.findViewById(R.id.passwordEt);
//                    EditText confirmPasswordEt = dialog.findViewById(R.id.confirmPasswordEt);
//                    TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
//                    TextView saveBtn = dialog.findViewById(R.id.saveBtn);
//                    TextView rateUsBtn = dialog.findViewById(R.id.rateUsBtn);
//
//
//                    // Set up the input
//                    passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//
//
//                    cancelBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//
//                    saveBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            password[0] = passwordEt.getText().toString();
//                            String confirmPassword = confirmPasswordEt.getText().toString();
//
//                            if (password[0].isEmpty()) {
//                                passwordEt.setError("Please Enter Password");
//                            }
//
//                            if (confirmPassword.isEmpty()) {
//                                confirmPasswordEt.setError("Please Enter Confirm Password");
//                            }
//
//                            if (!password[0].isEmpty() && !confirmPassword.isEmpty()) {
//                                if (password[0].equals(confirmPassword)) {
//                                    // Passwords match, proceed with the submission
//
//                                    dialog.dismiss();
//                                } else {
//                                    // Passwords do not match. Please try again.
//                                    Toast.makeText(DigitalSignatureActivity.this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//
//
//
//                        }
//                    });
//
//
//                    dialog.show();
//
//                }else {
////                    convertMultipleImagesToPdf(true,data, null,renameFileName[0] + ".pdf");
////                    dialog[0].dismiss();
//
//
//                }
//
//
//
//            }
//        });








        convertPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToNextActivity();
                dialog.dismiss();

                Log.d("checkopen","password[0]   "+password[0]);
//
//                PDSSaveAsPDFAsyncTask task = new PDSSaveAsPDFAsyncTask(DigitalSignatureActivity.this, renameFileName[0] + ".pdf", password[0],pdfFileViewModel);
//                task.execute(new Void[0]);
//                dialog.dismiss();


            }


        });


        dialog.show();





//        final Dialog dialog = new Dialog(DigitalSignatureActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        final View alertView = getLayoutInflater().inflate(R.layout.file_alert_dialog, null);
//        final EditText edittext = alertView.findViewById(R.id.editText2);
//        dialog.setContentView(alertView);
//        dialog.setCancelable(true);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);
//        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        (dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String fileName = edittext.getText().toString();
//                if (fileName.length() == 0) {
//                    Toast.makeText(DigitalSignatureActivity.this, "File name should not be empty", Toast.LENGTH_LONG).show();
//                } else {
////                    PDSSaveAsPDFAsyncTask task = new PDSSaveAsPDFAsyncTask(DigitalSignatureActivity.this, fileName + ".pdf");
////                    task.execute(new Void[0]);
////                    dialog.dismiss();
//
//
//
//
//
//
//
//
//
//                    Dialog dialog = new Dialog(DigitalSignatureActivity.this, R.style.BottomDialogAnimation);
//                    dialog.setContentView(R.layout.convert_to_pdf_dialog);
//                    dialog.getWindow().setGravity(Gravity.BOTTOM);
//                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//
//                    LinearLayout convertPdfBtn = dialog.findViewById(R.id.convertPdfBtn);
//                    CheckBox checkPssword = dialog.findViewById(R.id.checkPssword);
//
//
//
//                    convertPdfBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            if (checkPssword.isChecked()){
//
//                                ViewPasswordDialog alert = new ViewPasswordDialog(fileName + ".pdf",db);
//                                alert.showDialog(DigitalSignatureActivity.this);
//
//
//                            }else {
//
//
//                                PDSSaveAsPDFAsyncTask task = new PDSSaveAsPDFAsyncTask(DigitalSignatureActivity.this, fileName + ".pdf", null,db);
//                                task.execute(new Void[0]);
//                                dialog.dismiss();
//
//
//
//                            }
//
//
//
//
//                        }
//
//
//                    });
//
//
//                    dialog.show();
//
//
//
//
//
//
//                }
//            }
//        });
    }



    public static class ActionBottomSheetFragment extends BottomSheetDialogFragment {

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.action_signiture_bottom_sheet_layout, container, false);

            LinearLayout signature = view.findViewById(R.id.fromCollection);
            LinearLayout image = view.findViewById(R.id.fromImage);
            LinearLayout mainLayout = view.findViewById(R.id.mainLayout);



            signature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireActivity(), SignatureActivity.class);
                    startActivityForResult(intent, SIGNATURE_Request_CODE);
                }
            });



            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("image/jpeg");
                    String[] mimetypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
            });




//            stickerLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Launch EmojiMakerActivity
//                    Intent intent = new Intent( requireActivity(), EmojiMakerActivity.class);
//                    startActivity(intent);
//
//                    // Update preferences
//                    SharedPreferences preferences =  requireActivity().getSharedPreferences("Started_Emoji", AppCompatActivity.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putBoolean("isChecked_Emoji", true);
//                    editor.apply();
//
//                    // Dismiss the bottom sheet dialog
//                    dismiss();
//                }
//            });

            return view;
        }



    }


    private class ViewPasswordDialog {



        String fileName;
        AppDatabase db;
        public ViewPasswordDialog(String fileName, AppDatabase db) {
            this.fileName = fileName;
            this.db = db;
        }

        public void showDialog(Context activity) {
            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.new_lock_dialog);

            EditText passwordEt = dialog.findViewById(R.id.passwordEt);
            EditText confirmPasswordEt = dialog.findViewById(R.id.confirmPasswordEt);
            TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
            TextView saveBtn = dialog.findViewById(R.id.saveBtn);


            // Set up the input
            passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


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

//                            String password = passwordEt.getText().toString();

                            PDSSaveAsPDFAsyncTask task = new PDSSaveAsPDFAsyncTask(DigitalSignatureActivity.this, fileName + ".pdf",password, pdfFileViewModel);
                            task.execute(new Void[0]);

                            dialog.dismiss();
                        } else {
                            // Passwords do not match. Please try again.
                            Toast.makeText(DigitalSignatureActivity.this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }




                }
            });





//            saveBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String password = passwordEt.getText().toString();
//
//                    PDSSaveAsPDFAsyncTask task = new PDSSaveAsPDFAsyncTask(DigitalSignatureActivity.this, fileName + ".pdf",password, db);
//                    task.execute(new Void[0]);
//                }
//            });


            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
                int screenWidth = metrics.widthPixels;
                int desiredWidth = screenWidth - 2 * dpToPx(activity, 0);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = desiredWidth;
                dialog.getWindow().setAttributes(params);
            }

            dialog.show();
        }

        private void openPlayStore(Context context) {
            String packageName = context.getPackageName();
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (ActivityNotFoundException e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        }

        private int dpToPx(Context context, int dp) {
            return (int) (dp * context.getResources().getDisplayMetrics().density);
        }
    }
}





































//package com.absolute.my.imagetopdfconverterpixlez306.signiture_model;
//
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.RectF;
//import android.net.Uri;
//import android.os.Handler;
//import android.os.Message;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.FragmentManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.absolute.my.imagetopdfconverterpixlez306.Get_Image_List_Activity;
//import com.absolute.my.imagetopdfconverterpixlez306.MainActivity;
//import com.absolute.my.imagetopdfconverterpixlez306.R;
//import com.absolute.my.imagetopdfconverterpixlez306.SignitureActivity;
//import com.absolute.my.imagetopdfconverterpixlez306.databinding.ActivityDigitalSignatureBinding;
//import com.absolute.my.imagetopdfconverterpixlez306.signiture_model.Document.PDSElementViewer;
//import com.absolute.my.imagetopdfconverterpixlez306.signiture_model.Document.PDSPageViewer;
//import com.absolute.my.imagetopdfconverterpixlez306.signiture_model.Document.PDSSaveAsPDFAsyncTask;
//import com.absolute.my.imagetopdfconverterpixlez306.signiture_model.Document.PDSViewPager;
//import com.absolute.my.imagetopdfconverterpixlez306.signiture_model.PDF.PDSPDFDocument;
//import com.absolute.my.imagetopdfconverterpixlez306.signiture_model.PDSModel.PDSElement;
//import com.absolute.my.imagetopdfconverterpixlez306.signiture_model.Activity.SignatureActivity;
//import com.absolute.my.imagetopdfconverterpixlez306.signiture_model.Activity.SignatureUtils;
//import com.absolute.my.imagetopdfconverterpixlez306.signiture_model.imageviewer.PDSPageAdapter;
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.ref.WeakReference;
//import java.security.KeyStore;
//import java.security.Security;
//import java.util.ArrayList;
//
//
//public class DigitalSignatureActivity extends AppCompatActivity {
//    private static final int READ_REQUEST_CODE = 42;
//    private static final int SIGNATURE_Request_CODE = 43;
//    private static final int IMAGE_REQUEST_CODE = 45;
//    private static final int DIGITALID_REQUEST_CODE = 44;
//    Uri pdfData = null;
//    private PDSViewPager mViewPager;
//    PDSPageAdapter imageAdapter;
//    private Context mContext = null;
//    private RecyclerView mRecyclerView;
//    private boolean mFirstTap = true;
//    private int mVisibleWindowHt = 0;
//    private PDSPDFDocument mDocument = null;
//    private Uri mdigitalID = null;
//    public String mdigitalIDPassword = null;
//    private Menu mmenu = null;
//    private final UIElementsHandler mUIElemsHandler = new UIElementsHandler(this);
//    AlertDialog passwordalertDialog;
//    AlertDialog signatureOptionDialog;
//    public KeyStore keyStore = null;
//    public String alises = null;
//    public boolean isSigned = false;
//    public ProgressBar savingProgress;
//
//    ActivityDigitalSignatureBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityDigitalSignatureBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        this.mContext = getApplicationContext();
//        mViewPager = findViewById(R.id.viewpager);
//        savingProgress = findViewById(R.id.savingProgress);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        Intent intent = getIntent();
//        String message = intent.getStringExtra("ActivityAction");
//        if (message.equals("FileSearch")) {
//            performFileSearch();
//        } else if (message.equals("PDFOpen")) {
//            ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra("PDFOpen");
//            if (imageUris != null) {
//                for (int i = 0; i < imageUris.size(); i++) {
//                    Uri imageUri = imageUris.get(i);
//                    OpenPDFViewer((imageUri));
//                }
//            }
//        }
//
//
//
//
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent result) {
//        super.onActivityResult(requestCode, resultCode, result);
//        if (requestCode == READ_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (result != null) {
//                    pdfData = result.getData();
//                    OpenPDFViewer(pdfData);
//                }
//            } else {
//                finish();
//            }
//        }
//        if (requestCode == SIGNATURE_Request_CODE && resultCode == Activity.RESULT_OK) {
//            String returnValue = result.getStringExtra("FileName");
//            File fi = new File(returnValue);
//            this.addElement(PDSElement.PDSElementType.PDSElementTypeSignature, fi, (float) SignatureUtils.getSignatureWidth((int) getResources().getDimension(R.dimen.sign_field_default_height), fi, getApplicationContext()), getResources().getDimension(R.dimen.sign_field_default_height));
//
//        }
//        if (requestCode == DIGITALID_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (result != null) {
//                    mdigitalID = result.getData();
//                    GetPassword();
//                }
//            } else {
//                Toast.makeText(DigitalSignatureActivity.this, "Digital certificate not included in the signature.", Toast.LENGTH_LONG).show();
//            }
//        }
//
//        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            if (result != null) {
//                Uri imageData = result.getData();
//                Bitmap bitmap = null;
//                try {
//                    InputStream input = this.getContentResolver().openInputStream(imageData);
//                    bitmap = BitmapFactory.decodeStream(input);
//                    input.close();
//                    if (bitmap != null)
//                        this.addElement(PDSElement.PDSElementType.PDSElementTypeImage, bitmap, getResources().getDimension(R.dimen.sign_field_default_height), getResources().getDimension(R.dimen.sign_field_default_height));
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        this.mmenu = menu;
//        MenuItem saveItem = mmenu.findItem(R.id.action_save);
//        saveItem.getIcon().setAlpha(130);
//        MenuItem signItem = mmenu.findItem(R.id.action_sign);
//        signItem.getIcon().setAlpha(255);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//        if (id == R.id.action_sign) {
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//            LayoutInflater inflater = this.getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.optiondialog, null);
//            dialogBuilder.setView(dialogView);
//
//            Button signature = dialogView.findViewById(R.id.fromCollection);
//
//            signature.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(), SignatureActivity.class);
//                    startActivityForResult(intent, SIGNATURE_Request_CODE);
//                    signatureOptionDialog.dismiss();
//                }
//            });
//
//
//            Button image = dialogView.findViewById(R.id.fromImage);
//
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                    intent.setType("image/jpeg");
//                    String[] mimetypes = {"image/jpeg", "image/png"};
//                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
//                    signatureOptionDialog.dismiss();
//                }
//            });
//
//            signatureOptionDialog = dialogBuilder.create();
//            signatureOptionDialog.show();
//            return true;
//        }
//
//        if (id == R.id.action_save) {
//            savePDFDocument();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (isSigned) {
//            final AlertDialog alertDialog = new AlertDialog.Builder(this)
//                    .setTitle("Save Document")
//                    .setMessage("Want to save your changes to PDF document?")
//
//                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            savePDFDocument();
//                        }
//                    })
//                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    }).show();
//        } else {
//            finish();
//        }
//    }
//
//    private void OpenPDFViewer(Uri pdfData) {
//        try {
//            PDSPDFDocument document = new PDSPDFDocument(this, pdfData);
//            document.open();
//            this.mDocument = document;
//            imageAdapter = new PDSPageAdapter(getSupportFragmentManager(), document);
//            updatePageNumber(1);
//            mViewPager.setAdapter(imageAdapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(DigitalSignatureActivity.this, "CCannot open the PDF. It may be corrupted or password protected", Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }
//
//    public void performFileSearch() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("image/jpeg");
//        String[] mimetypes = {"application/pdf"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//        startActivityForResult(intent, READ_REQUEST_CODE);
//    }
//
//    private int computeVisibleWindowHtForNonFullScreenMode() {
//        return findViewById(R.id.docviewer).getHeight();
//    }
//
//
//    public boolean isFirstTap() {
//        return this.mFirstTap;
//    }
//
//    public void setFirstTap(boolean z) {
//        this.mFirstTap = z;
//    }
//
//    public int getVisibleWindowHeight() {
//        if (this.mVisibleWindowHt == 0) {
//            this.mVisibleWindowHt = computeVisibleWindowHtForNonFullScreenMode();
//        }
//        return this.mVisibleWindowHt;
//    }
//
//    public PDSPDFDocument getDocument() {
//        return this.mDocument;
//    }
//
//    public void GetPassword() {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.passworddialog, null);
//        dialogBuilder.setView(dialogView);
//
//        final EditText password = dialogView.findViewById(R.id.passwordText);
//        Button submit = dialogView.findViewById(R.id.passwordSubmit);
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (password.length() == 0) {
//                    Toast.makeText(DigitalSignatureActivity.this, "Password cannot be blank", Toast.LENGTH_LONG).show();
//                } else {
//                    mdigitalIDPassword = password.getText().toString();
//                    BouncyCastleProvider provider = new BouncyCastleProvider();
//                    Security.addProvider(provider);
//                    try {
//                        InputStream inputStream = getContentResolver().openInputStream(mdigitalID);
//                        keyStore = KeyStore.getInstance("pkcs12", provider.getName());
//                        keyStore.load(inputStream, mdigitalIDPassword.toCharArray());
//                        alises = keyStore.aliases().nextElement();
//                        passwordalertDialog.dismiss();
//                        Toast.makeText(DigitalSignatureActivity.this, "Digital certificate is added with Signature", Toast.LENGTH_LONG).show();
//                    } catch (Exception e) {
//                        if (e.getMessage().contains("wrong password")) {
//                            Toast.makeText(DigitalSignatureActivity.this, "Password is incorrect or certificate is corrupted", Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(DigitalSignatureActivity.this, "Something went wrong while adding Digital certificate", Toast.LENGTH_LONG).show();
//                            passwordalertDialog.dismiss();
//                        }
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        passwordalertDialog = dialogBuilder.create();
//        passwordalertDialog.show();
//    }
//
//    public void invokeMenuButton(boolean disableButtonFlag) {
//        MenuItem saveItem = mmenu.findItem(R.id.action_save);
//        saveItem.setEnabled(disableButtonFlag);
//        MenuItem signPDF = mmenu.findItem(R.id.action_sign);
//        //signPDF.setEnabled(!disableButtonFlag);
//        isSigned = disableButtonFlag;
//        if (disableButtonFlag) {
//            //signPDF.getIcon().setAlpha(130);
//            saveItem.getIcon().setAlpha(255);
//        } else {
//            //signPDF.getIcon().setAlpha(255);
//            saveItem.getIcon().setAlpha(130);
//
//        }
//    }
//
//    public void addElement(PDSElement.PDSElementType fASElementType, File file, float f, float f2) {
//        View focusedChild = this.mViewPager.getFocusedChild();
//        if (focusedChild != null) {
//            PDSPageViewer fASPageViewer = (PDSPageViewer) ((ViewGroup) focusedChild).getChildAt(0);
//            if (fASPageViewer != null) {
//                RectF visibleRect = fASPageViewer.getVisibleRect();
//                float width = (visibleRect.left + (visibleRect.width() / 2.0f)) - (f / 2.0f);
//                float height = (visibleRect.top + (visibleRect.height() / 2.0f)) - (f2 / 2.0f);
//                PDSElementViewer lastFocusedElementViewer = fASPageViewer.getLastFocusedElementViewer();
//
//                PDSElement.PDSElementType fASElementType2 = fASElementType;
//                final PDSElement element = fASPageViewer.createElement(fASElementType2, file, width, height, f, f2);
//
//                if (!isSigned) {
//                    AlertDialog dialog;
//                    AlertDialog.Builder builder = new AlertDialog.Builder(DigitalSignatureActivity.this);
//                    builder.setMessage("Do you want to add digital certificate with this Signature?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                                    intent.setType("application/keychain_access");
//                                    String[] mimetypes = {"application/x-pkcs12"};
//                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                                    startActivityForResult(intent, DIGITALID_REQUEST_CODE);
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    dialog = builder.create();
//                    dialog.show();
//                }
//            }
//            invokeMenuButton(true);
//        }
//    }
//
//
//    public void addElement(PDSElement.PDSElementType fASElementType, Bitmap bitmap, float f, float f2) {
//        View focusedChild = this.mViewPager.getFocusedChild();
//        if (focusedChild != null && bitmap != null) {
//            PDSPageViewer fASPageViewer = (PDSPageViewer) ((ViewGroup) focusedChild).getChildAt(0);
//            if (fASPageViewer != null) {
//                RectF visibleRect = fASPageViewer.getVisibleRect();
//                float width = (visibleRect.left + (visibleRect.width() / 2.0f)) - (f / 2.0f);
//                float height = (visibleRect.top + (visibleRect.height() / 2.0f)) - (f2 / 2.0f);
//                PDSElementViewer lastFocusedElementViewer = fASPageViewer.getLastFocusedElementViewer();
//
//                PDSElement.PDSElementType fASElementType2 = fASElementType;
//                final PDSElement element = fASPageViewer.createElement(fASElementType2, bitmap, width, height, f, f2);
//                if (!isSigned) {
//                    AlertDialog dialog;
//                    AlertDialog.Builder builder = new AlertDialog.Builder(DigitalSignatureActivity.this);
//                    builder.setMessage("Do you want to add digital certificate with this Signature?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                                    intent.setType("application/keychain_access");
//                                    String[] mimetypes = {"application/x-pkcs12"};
//                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                                    startActivityForResult(intent, DIGITALID_REQUEST_CODE);
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    dialog = builder.create();
//                    dialog.show();
//                }
//            }
//            invokeMenuButton(true);
//        }
//    }
//
//    public void updatePageNumber(int i) {
//        TextView textView = (TextView) findViewById(R.id.pageNumberTxt);
//        findViewById(R.id.pageNumberOverlay).setVisibility(View.VISIBLE);
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(i);
//        stringBuilder.append("/");
//        stringBuilder.append(this.mDocument.getNumPages());
//        textView.setText(stringBuilder.toString());
//        resetTimerHandlerForPageNumber(1000);
//    }
//
//    private void resetTimerHandlerForPageNumber(int i) {
//        this.mUIElemsHandler.removeMessages(1);
//        Message message = new Message();
//        message.what = 1;
//        this.mUIElemsHandler.sendMessageDelayed(message, (long) i);
//    }
//
//    private void fadePageNumberOverlay() {
//        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
//        View findViewById = findViewById(R.id.pageNumberOverlay);
//        if (findViewById.getVisibility() == View.VISIBLE) {
//            findViewById.startAnimation(loadAnimation);
//            findViewById.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    private static class UIElementsHandler extends Handler {
//        private final WeakReference<DigitalSignatureActivity> mActivity;
//
//        public UIElementsHandler(DigitalSignatureActivity fASDocumentViewer) {
//            this.mActivity = new WeakReference(fASDocumentViewer);
//        }
//
//        public void handleMessage(Message message) {
//            DigitalSignatureActivity fASDocumentViewer = this.mActivity.get();
//            if (fASDocumentViewer != null && message.what == 1) {
//                fASDocumentViewer.fadePageNumberOverlay();
//            }
//            super.handleMessage(message);
//        }
//    }
//
//
//    public void runPostExecution() {
//        savingProgress.setVisibility(View.INVISIBLE);
//        makeResult();
//
//    }
//
//    public void makeResult() {
//        Intent i = new Intent();
//        setResult(RESULT_OK, i);
//        finish();
//    }
//
//    public void savePDFDocument() {
//        final Dialog dialog = new Dialog(DigitalSignatureActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        final View alertView = getLayoutInflater().inflate(R.layout.file_alert_dialog, null);
//        final EditText edittext = alertView.findViewById(R.id.editText2);
//        dialog.setContentView(alertView);
//        dialog.setCancelable(true);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);
//        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        (dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String fileName = edittext.getText().toString();
//                if (fileName.length() == 0) {
//                    Toast.makeText(DigitalSignatureActivity.this, "File name should not be empty", Toast.LENGTH_LONG).show();
//                } else {
//                    PDSSaveAsPDFAsyncTask task = new PDSSaveAsPDFAsyncTask(DigitalSignatureActivity.this, fileName + ".pdf");
//                    task.execute(new Void[0]);
//                    dialog.dismiss();
//                }
//            }
//        });
//    }
//
//
//
//    public static class ActionBottomSheetFragment extends BottomSheetDialogFragment {
//
//        @Override
//        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            // Inflate the layout for this fragment
//            View view = inflater.inflate(R.layout.action_signiture_bottom_sheet_layout, container, false);
//
//            LinearLayout signature = view.findViewById(R.id.fromCollection);
//            LinearLayout image = view.findViewById(R.id.fromImage);
//            LinearLayout mainLayout = view.findViewById(R.id.mainLayout);
//
//
//
//            signature.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent( requireActivity(), SignatureActivity.class);
//                    startActivityForResult(intent, SIGNATURE_Request_CODE);
//                }
//            });
//
//
//
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                    intent.setType("image/jpeg");
//                    String[] mimetypes = {"image/jpeg", "image/png"};
//                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
//                }
//            });
//
//
//
//
////            stickerLayout.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    // Launch EmojiMakerActivity
////                    Intent intent = new Intent( requireActivity(), EmojiMakerActivity.class);
////                    startActivity(intent);
////
////                    // Update preferences
////                    SharedPreferences preferences =  requireActivity().getSharedPreferences("Started_Emoji", AppCompatActivity.MODE_PRIVATE);
////                    SharedPreferences.Editor editor = preferences.edit();
////                    editor.putBoolean("isChecked_Emoji", true);
////                    editor.apply();
////
////                    // Dismiss the bottom sheet dialog
////                    dismiss();
////                }
////            });
//
//            return view;
//        }
//
//
//
//    }
//
//
//}
