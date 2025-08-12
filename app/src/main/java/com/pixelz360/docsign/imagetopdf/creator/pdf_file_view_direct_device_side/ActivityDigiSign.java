package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.pixelz360.docsign.imagetopdf.creator.HomeActivity;
import com.pixelz360.docsign.imagetopdf.creator.PdfViewActivity;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.databinding.ActivityDigitalSignature112Binding;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
public class ActivityDigiSign extends AppCompatActivity implements MultiTouchListener.TouchCallbackListener {
    private static final int READ_REQUEST_CODE = 42;
    private static final int SIGNATURE_Request_CODE = 43;
    Uri pdfData = null;
    public static ViewPager mViewPager;
    AdapterPdsPageSign imageAdapter;
    public static Context mContext = null;
    private boolean mFirstTap = true;
    private int mVisibleWindowHt = 0;
    private Doc mDoc = null;
    private Menu mmenu = null;
    private final UIElementsHandler mUIElemsHandler = new UIElementsHandler(this);
    public boolean isSigned = false;
    public ProgressBar savingProgress;
    boolean value_sign=true;
    public static RelativeLayout sign;
    MultiTouchListener.TouchCallbackListener listener;
    public static ViewSign createFreeHandView;
    public static int page_number=0;
    public static int width_sign=0;
    public static int height_sign=0;
    public static  ImageView delete_sign;
    public static Bitmap bitmap_ss;
    public static ZoomLayout zoomLayout;
    public ActivityDigiSign setOnTouchCallbackListener(MultiTouchListener.TouchCallbackListener touchCallbackListener) {
        this.listener = touchCallbackListener;
        return this;
    }

    ActivityDigitalSignature112Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDigitalSignature112Binding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(binding.getRoot());
        mContext = getApplicationContext();
        mViewPager = findViewById(R.id.viewpager);
        delete_sign=findViewById(R.id.delete_sign);
        savingProgress = findViewById(R.id.savingProgress);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(Html.fromHtml("<font color='#ffffff'>PDF Signature</font>"));
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_color_red)));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        Log.d("PdfViewActivity11", "onCreate ActivityDigiSign");

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(PdfViewActivity.this,MainActivity.class);
                Intent intent = new Intent(ActivityDigiSign.this, HomeActivity.class);
                startActivity(intent);
            }
        });


        sign=findViewById(R.id.sign);
        Intent intent = getIntent();
        String message = intent.getStringExtra("ActivityAction");

        if (message!=null){

            if (message.equals("FileSearch")) {
                performFileSearch();

                Log.d("PdfViewActivity11", "performFileSearch() ActivityDigiSign");
            } else if (message.equals("PDFOpen")) {
                ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra("PDFOpen");
                if (imageUris != null) {
                    for (int i = 0; i < imageUris.size(); i++) {
                        Uri imageUri = imageUris.get(i);
                        OpenPDFViewer((imageUri));
                    }
                }

                Log.d("PdfViewActivity11", "message.equals(\"PDFOpen\"");
            }else if (message.equals("Preview")) {
                String preivew_uri = intent.getStringExtra("preview_file_path");
                if (preivew_uri != null) {
                    OpenPDFViewer(Uri.fromFile(new File(preivew_uri)));
                }

                Log.d("PdfViewActivity11", "message.equals(\"PDFOpen\"");
            }else {
                Log.d("PdfViewActivity11", "else nothing");
            }

        }else {
            Log.d("PdfViewActivity11", "messege is null");

            HandleExternalData();
        }

    }

    private void HandleExternalData() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Uri imageUri = null;

//        Log.d("PdfViewActivity11Others", "handleExternalData() " + imageUri.toString());

        if ((Intent.ACTION_SEND.equals(action) || Intent.ACTION_VIEW.equals(action)) && type != null) {
            if ("application/pdf".equals(type)) {
                if (Intent.ACTION_SEND.equals(action)) {
                    imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                } else if (Intent.ACTION_VIEW.equals(action)) {
                    imageUri = intent.getData();
                }

                if (imageUri != null) {
                    ArrayList<Uri> list = new ArrayList<>();
                    list.add(imageUri);
                    startSignatureActivity("PDFOpen", list);

                    Log.d("PdfViewActivity11", "handleExternalData() " + imageUri.toString());
                    Log.d("PdfViewActivity11", "handleExternalData() " + list.size());
                }
            }else {
                Log.d("PdfViewActivity11Others", "not pdf handleExternalData() ");
            }
        }
    }

    private void startSignatureActivity(String message, ArrayList<Uri> imageUris) {
//        Intent intent = new Intent(getApplicationContext(), ActivityDigiSign.class);
//        intent.putExtra("ActivityAction", message);
//        intent.putExtra("PDFOpen", imageUris);
//        startActivityForResult(intent, Merge_Request_CODE);
        Log.d("PdfViewActivity11", "startSignatureActivity");


        if (imageUris != null) {
            for (int i = 0; i < imageUris.size(); i++) {
                Uri imageUri = imageUris.get(i);
                OpenPDFViewer((imageUri));
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == READ_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    pdfData = result.getData();
                    OpenPDFViewer(pdfData);
                    Log.d("PdfViewActivity11", "onActivityResult "+pdfData);

                }
            } else {
                finish();
            }
        }

        if (requestCode == SIGNATURE_Request_CODE && resultCode == Activity.RESULT_OK) {
            String returnValue = result.getStringExtra("FileName");
            File fi = new File(returnValue);

            width_sign=200;
            height_sign=200;

            createFreeHandView = UtilsSign.createFreeHandView(width_sign,height_sign, fi, mContext);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            layoutParams.addRule(9);
            createFreeHandView.setLayoutParams(layoutParams);

            if(sign.getChildCount()>0){
                sign.removeAllViews();
            }

            sign.addView(createFreeHandView);
            createFreeHandView. setOnTouchListener(new MultiTouchListener().enableRotation(true).setOnTouchCallbackListener(this));
            invokeMenuButton(true);

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
            if(createFreeHandView!=null){
                createFreeHandView.clear();
                sign.setScaleX(1f);
                sign.setScaleY(1f);
                sign.setTranslationY(ViewGroup.LayoutParams.MATCH_PARENT/2);
                sign.setTranslationX(ViewGroup.LayoutParams.MATCH_PARENT/2);
            }
            sign.setVisibility(View.VISIBLE);


//            Intent intent = new Intent(getApplicationContext(), ActivitySign.class);
//            startActivityForResult(intent, SIGNATURE_Request_CODE);
            return true;
        }

        if (id == R.id.action_save) {
            if(value_sign){
                value_sign=false;
                zoomLayout=findViewById(R.id.frame_container);
                bitmap_ss=getScreenShot(zoomLayout);
                new Handler().postDelayed(() -> savePDFDocument(),1000);


            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Bitmap getScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new  Intent(ActivityDigiSign.this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

    private void OpenPDFViewer(Uri pdfData) {
        try {

//          Uri uri =  Uri.fromFile(new File("/storage/emulated/0/Android/data/com.pixelz360.docsign.imagetopdf.creator/files/IMAGE TO PDF/DOCUMENTS/watermarked_1735306629890.pdf"));


            Doc doc = new Doc(this, pdfData);
            doc.open();
            this.mDoc = doc;
            imageAdapter = new AdapterPdsPageSign(getSupportFragmentManager(), doc);
            updatePageNumber(1);
            mViewPager.setAdapter(imageAdapter);

            Log.d("PdfViewActivity11", "ActivityDigiSign   OpenPDFViewer() "+pdfData);

        } catch (Exception e) {
            e.printStackTrace();
                    Toast.makeText(ActivityDigiSign.this, "Cannot open PDF, either PDF is corrupted or password protected", Toast.LENGTH_LONG).show();
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

    public Doc getDocument() {
        return this.mDoc;
    }

    public void invokeMenuButton(boolean disableButtonFlag) {
        MenuItem saveItem = mmenu.findItem(R.id.action_save);
        saveItem.setEnabled(disableButtonFlag);
        MenuItem signPDF = mmenu.findItem(R.id.action_sign);
        //signPDF.setEnabled(!disableButtonFlag);
        isSigned = disableButtonFlag;
        if (disableButtonFlag) {
            saveItem.getIcon().setAlpha(255);
        } else {
            saveItem.getIcon().setAlpha(130);

        }
    }

    public void updatePageNumber(int i) {
        TextView textView =  findViewById(R.id.pageNumberTxt);
        findViewById(R.id.pageNumberOverlay).setVisibility(View.VISIBLE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i);
        stringBuilder.append("/");
        stringBuilder.append(this.mDoc.getNumPages());
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

    @Override
    public void onTouchCallback(View view) {

    }

    @Override
    public void onTouchUpCallback(View view) {
        if (this.listener != null) {
            this.listener.onTouchCallback(view);


            /////////////////////code////////////////////////////// delete




        }
    }

    private static class UIElementsHandler extends Handler {
        private final WeakReference<ActivityDigiSign> mActivity;

        public UIElementsHandler(ActivityDigiSign fASDocumentViewer) {
            this.mActivity = new WeakReference(fASDocumentViewer);
        }

        public void handleMessage(Message message) {
            ActivityDigiSign fASDocumentViewer = this.mActivity.get();
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
        final Dialog dialog = new Dialog(ActivityDigiSign.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        final View alertView = getLayoutInflater().inflate(R.layout.file_alert_dialog, null);
//        final EditText edittext = alertView.findViewById(R.id.editText2);
//        dialog.setContentView(alertView);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
//        (dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {
//            value_sign=true;
//            dialog.dismiss();
//        });
//        (dialog.findViewById(R.id.bt_save)).setOnClickListener(v -> {
//            value_sign=true;
//            String fileName = edittext.getText().toString();
//            if (fileName.length() == 0) {
//                Toast.makeText(ActivityDigiSign.this, "File name should not be empty", Toast.LENGTH_LONG).show();
//            } else {
//                AsyncTaskSavePdf task = new AsyncTaskSavePdf(ActivityDigiSign.this, fileName + ".pdf");
//                task.execute(new Void[0]);
//                dialog.dismiss();
//            }
//        });
    }


}
