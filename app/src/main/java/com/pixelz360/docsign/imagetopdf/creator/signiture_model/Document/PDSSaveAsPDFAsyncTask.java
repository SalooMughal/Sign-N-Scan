package com.pixelz360.docsign.imagetopdf.creator.signiture_model.Document;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.pixelz360.docsign.imagetopdf.creator.Prview_Screen;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile;
import com.pixelz360.docsign.imagetopdf.creator.language.AccountsOrGuesHelper;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.DigitalSignatureActivity;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.PDF.PDSPDFDocument;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.PDF.PDSPDFPage;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.PDSModel.PDSElement;
import com.pixelz360.docsign.imagetopdf.creator.signiture_model.utils.ViewUtils;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.PdfFileViewModel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Locale;

public class PDSSaveAsPDFAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private String mfileName;
    private String password;
    DigitalSignatureActivity mCtx;

//    AppDatabase db;

    PdfFileViewModel pdfFileViewModel;
    public PDSSaveAsPDFAsyncTask(DigitalSignatureActivity context, String str, String password, PdfFileViewModel pdfFileViewModel) {
        this.mCtx = context;
        this.mfileName = str;
        this.password = password;
//        this.db = db;
        this.pdfFileViewModel = pdfFileViewModel;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCtx.savingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public Boolean doInBackground(Void... voidArr) {

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        PDSPDFDocument document = mCtx.getDocument();

        Security.addProvider(new BouncyCastleProvider());
        File root = mCtx.getFilesDir();
        File myDir = new File(root + "/DigitalSignature");

        if (!myDir.exists() && !myDir.mkdirs()) {
            return false;
        }

        File file = new File(myDir.getAbsolutePath(), mfileName);
        if (file.exists() && !file.delete()) {
            return false;
        }

        try {
            InputStream stream = document.stream;
            FileOutputStream os = new FileOutputStream(file);
            PdfReader reader = new PdfReader(stream);
            PdfStamper signer = null;

            for (int i = 0; i < document.getNumPages(); i++) {
                Rectangle mediabox = reader.getPageSize(i + 1);

                for (int j = 0; j < document.getPage(i).getNumElements(); j++) {
                    PDSPDFPage page = document.getPage(i);
                    PDSElement element = page.getElement(j);
                    RectF bounds = element.getRect();
                    Bitmap createBitmap;

                    if (element.getType() == PDSElement.PDSElementType.PDSElementTypeSignature) {
                        PDSElementViewer viewer = element.mElementViewer;
                        View dummy = viewer.getElementView();
                        View view = ViewUtils.createSignatureView(mCtx, element, viewer.mPageViewer.getToViewCoordinatesMatrix());

                        // Create a high-resolution bitmap
                        int width = dummy.getWidth() * 2; // Adjust scale factor as needed
                        int height = dummy.getHeight() * 2;
                        createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(createBitmap);
                        canvas.scale(2f, 2f); // Adjust scale factor as needed
                        view.draw(canvas);
                    } else {
                        // Ensure element bitmap is of high resolution
                        Bitmap originalBitmap = element.getBitmap();
                        int width = originalBitmap.getWidth() * 2; // Adjust scale factor as needed
                        int height = originalBitmap.getHeight() * 2;
                        createBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
                    }

                    ByteArrayOutputStream saveBitmap = new ByteArrayOutputStream();
                    createBitmap.compress(Bitmap.CompressFormat.PNG, 100, saveBitmap);
                    byte[] byteArray = saveBitmap.toByteArray();
                    createBitmap.recycle();

                    Image sigimage = Image.getInstance(byteArray);

                    if (mCtx.alises != null && mCtx.keyStore != null && mCtx.mdigitalIDPassword != null) {
                        KeyStore ks = mCtx.keyStore;
                        String alias = mCtx.alises;
                        PrivateKey pk = (PrivateKey) ks.getKey(alias, mCtx.mdigitalIDPassword.toCharArray());
                        Certificate[] chain = ks.getCertificateChain(alias);

                        if (signer == null) {
                            signer = PdfStamper.createSignature(reader, os, '\0');
                        }

                        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
                        float top = mediabox.getHeight() - (bounds.top + bounds.height());
                        appearance.setVisibleSignature(new Rectangle(bounds.left, top, bounds.left + bounds.width(), top + bounds.height()), i + 1, "sig" + j);
                        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
                        appearance.setSignatureGraphic(sigimage);

                        ExternalDigest digest = new BouncyCastleDigest();
                        ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, null);
                        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CADES);
                    } else {
                        if (signer == null) {
                            signer = new PdfStamper(reader, os, '\0');
                        }

                        PdfContentByte contentByte = signer.getOverContent(i + 1);
                        sigimage.setAlignment(Image.ALIGN_UNDEFINED);
                        sigimage.scaleToFit(bounds.width(), bounds.height());
                        sigimage.setAbsolutePosition(bounds.left - (sigimage.getScaledWidth() - bounds.width()) / 2, mediabox.getHeight() - (bounds.top + bounds.height()));
                        contentByte.addImage(sigimage);
                    }
                }
            }

            if (signer != null) {
                signer.close();
            }
            reader.close();
            os.close();

            // Log file information for debugging
            long fileSizeBytes = file.length();
            long timestamp = file.lastModified();
            Log.d("PDF Generation", String.format(Locale.ENGLISH, "PDF generated: %s (Size: %d bytes, Last Modified: %d)", file.getAbsolutePath(), fileSizeBytes, timestamp));

            // Save the file path and password to the database
            PdfFile pdfFile = new PdfFile(file.getAbsolutePath(), mfileName,"Signed Doc","#03B078", password, fileSizeBytes, timestamp, false, true, true,false, false, false, false,false,false,AccountsOrGuesHelper.checkAccountOrNot(mCtx),false,"pdf");
//            db.pdfFileDao().insert(pdfFile);
            pdfFileViewModel.insertPdfFile(pdfFile);

            Intent intent = new Intent(mCtx, Prview_Screen.class);
            intent.putExtra("pdffilePath", file.getAbsolutePath());
            intent.putExtra("fileName", mfileName);
            intent.putExtra("fileType", "signature");
            mCtx.startActivity(intent);
            mCtx.finish();

        } catch (Exception e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            return false;
        }

        return true;







//        if (Looper.myLooper() == null) {
//            Looper.prepare();
//        }
//        PDSPDFDocument document = mCtx.getDocument();
//        File root = mCtx.getFilesDir();
//
//        File myDir = new File(root + "/DigitalSignature");
//        if (!myDir.exists()) {
//            myDir.mkdirs();
//        }
//        File file = new File(myDir.getAbsolutePath(), mfileName);
//        if (file.exists())
//            file.delete();
//        try {
//            InputStream stream = document.stream;
//            FileOutputStream os = new FileOutputStream(file);
//            PdfReader reader = new PdfReader(stream);
//            PdfStamper signer = null;
//            Bitmap createBitmap = null;
//            for (int i = 0; i < document.getNumPages(); i++) {
//                Rectangle mediabox = reader.getPageSize(i + 1);
//                for (int j = 0; j < document.getPage(i).getNumElements(); j++) {
//                    PDSPDFPage page = document.getPage(i);
//                    PDSElement element = page.getElement(j);
//                    RectF bounds = element.getRect();
//                    if (element.getType() == PDSElement.PDSElementType.PDSElementTypeSignature) {
//                        PDSElementViewer viewer = element.mElementViewer;
//                        View dummy = viewer.getElementView();
//                        View view = ViewUtils.createSignatureView(mCtx, element, viewer.mPageViewer.getToViewCoordinatesMatrix());
//                        createBitmap = Bitmap.createBitmap(dummy.getWidth(), dummy.getHeight(), Bitmap.Config.ARGB_8888);
//                        view.draw(new Canvas(createBitmap));
//                    } else {
//                        createBitmap = element.getBitmap();
//                    }
//                    ByteArrayOutputStream saveBitmap = new ByteArrayOutputStream();
//                    createBitmap.compress(Bitmap.CompressFormat.PNG, 100, saveBitmap);
//                    byte[] byteArray = saveBitmap.toByteArray();
//                    createBitmap.recycle();
//
//
//                    // File Size
//                    File fileDate = new File(file.getAbsolutePath());
//                    long fileSizeBytes = fileDate.length(); // Store size in bytes
//
//                    // File Date
//                    long timestamp = file.lastModified(); // Store timestamp directly
//
//
//                    // Save the file path and password to the database
//                        PdfFile pdfFile = new PdfFile(file.getAbsolutePath(), mfileName, password, fileSizeBytes,timestamp,false,true,false,false,false,false);
//                        db.pdfFileDao().insert(pdfFile);
//
//                    Intent intent  = new Intent(mCtx, Prview_Screen.class);
//                    intent.putExtra("pdffilePath", "" + file.getAbsolutePath());
//                    intent.putExtra("fileName", mfileName);
//                    mCtx.startActivity(intent);
//
//
//
//
//                    Image sigimage = Image.getInstance(byteArray);
//                    if (mCtx.alises != null && mCtx.keyStore != null && mCtx.mdigitalIDPassword != null) {
//                        KeyStore ks = mCtx.keyStore;
//                        String alias = mCtx.alises;
//                        PrivateKey pk = (PrivateKey) ks.getKey(alias, mCtx.mdigitalIDPassword.toCharArray());
//                        Certificate[] chain = ks.getCertificateChain(alias);
//                        if (signer == null)
//                            signer = PdfStamper.createSignature(reader, os, '\0');
//
//                        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
//
//                        float top = mediabox.getHeight() - (bounds.top + bounds.height());
//                        appearance.setVisibleSignature(new Rectangle(bounds.left, top, bounds.left + bounds.width(), top + bounds.height()), i + 1, "sig" + j);
//                        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
//                        appearance.setSignatureGraphic(sigimage);
//                        ExternalDigest digest = new BouncyCastleDigest();
//                        ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, null);
//                        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CADES);
//
//
//
//
//
//                    } else {
//                        if (signer == null)
//                            signer = new PdfStamper(reader, os, '\0');
//                        PdfContentByte contentByte = signer.getOverContent(i + 1);
//                        sigimage.setAlignment(Image.ALIGN_UNDEFINED);
//                        sigimage.scaleToFit(bounds.width(), bounds.height());
//                        sigimage.setAbsolutePosition(bounds.left - (sigimage.getScaledWidth() - bounds.width()) / 2, mediabox.getHeight() - (bounds.top + bounds.height()));
//                        contentByte.addImage(sigimage);
//                    }
//                }
//            }
//            if (signer != null)
//                signer.close();
//            if (reader != null)
//                reader.close();
//            if (os != null)
//                os.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (file.exists()) {
//                file.delete();
//            }
//            return false;
//        }
//        return true;
    }

    @Override
    public void onPostExecute(Boolean result) {
        // Execute post-signing tasks in the context
        mCtx.runPostExecution();

        // Safely show toast messages based on the result
        if (!result) {
            Toast.makeText(mCtx, "An error occurred while signing the PDF document. Please try again.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mCtx, "PDF document saved successfully.", Toast.LENGTH_LONG).show();
        }
    }

}



