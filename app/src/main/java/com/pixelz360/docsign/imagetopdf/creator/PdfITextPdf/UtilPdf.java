package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.WorkerThread;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.pixelz360.docsign.imagetopdf.creator.R;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UtilPdf {

    private final Activity mContext;
//    private final UtilFile mUtilFile;

    public UtilPdf(Activity context) {
        this.mContext = context;
//        this.mUtilFile = new UtilFile(mContext);
    }


    @SuppressLint("StringFormatInvalid")
    public void showDetails(File file) {
        String name = file.getName();
        String path = file.getPath();
//        String size = UtilFileDetail.getFormattedSize(file);
//        String lastModDate = UtilFileDetail.getFormattedSize(file);

        TextView message = new TextView(mContext);
        TextView title = new TextView(mContext);
//        message.setText(String.format(mContext.getResources().getString(R.string.file_info), name, path, size, lastModDate));
        message.setTextIsSelectable(true);
        title.setText(R.string.details);
        title.setPadding(20, 10, 10, 10);
        title.setTextSize(30);
        title.setTextColor(mContext.getResources().getColor(R.color.black));
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        builder.setView(message);
        builder.setCustomTitle(title);
        builder.setPositiveButton(mContext.getResources().getString(R.string.ok),
                (dialogInterface, i) -> dialog.dismiss());
        builder.create();
        builder.show();
    }


    @WorkerThread
    public boolean isPDFEncrypted(String path) {
        boolean isEncrypted;
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(path);
            isEncrypted = pdfReader.isEncrypted();
        } catch (IOException e) {
            isEncrypted = true;
        } finally {
            if (pdfReader != null) pdfReader.close();
        }
        return isEncrypted;
    }

//    public void compressPDF(@Nullable String inputPath, @Nullable String outputPath, int quality) {
//        new CompressPdfAsync(inputPath, outputPath, quality)
//                .execute();
//
//        Log.d("checksucesspath", " inputPath.2 "+inputPath);
//
//    }

    public static void compressPDF(@Nullable String inputPath, @Nullable String outputPath, int quality) {
        new CompressPdfAsync(inputPath, outputPath, quality)
                .execute();

        Log.d("checksucesspath", " inputPath.2 "+inputPath);
    }

    private static class CompressPdfAsync extends AsyncTask<String, String, String> {

        final int quality;
        final String inputPath;
        final String outputPath;
        boolean success;

        CompressPdfAsync(String inputPath, String outputPath, int quality) {
            this.inputPath = inputPath;
            this.outputPath = outputPath;
            this.quality = quality;
            success = false;

            Log.d("checksucesspath", " inputPath.3 "+inputPath);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                PdfReader reader = new PdfReader(inputPath);
                compressReader(reader);
                saveReader(reader);
                reader.close();
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            return null;
        }

        private void compressReader(PdfReader reader) throws IOException {
            int n = reader.getXrefSize();
            PdfObject object;
            PRStream stream;

            for (int i = 0; i < n; i++) {
                object = reader.getPdfObject(i);
                if (object == null || !object.isStream())
                    continue;
                stream = (PRStream) object;
                compressStream(stream);
            }

            reader.removeUnusedObjects();
        }


        private void compressStream(PRStream stream) throws IOException {
            PdfObject pdfSubType = stream.get(PdfName.SUBTYPE);
            System.out.println(stream.type());
            if (pdfSubType != null && pdfSubType.toString().equals(PdfName.IMAGE.toString())) {
                PdfImageObject image = new PdfImageObject(stream);
                byte[] imageBytes = image.getImageAsBytes();
                Bitmap bmp;
                bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (bmp == null) return;

                int width = bmp.getWidth();
                int height = bmp.getHeight();

                Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas outCanvas = new Canvas(outBitmap);
                outCanvas.drawBitmap(bmp, 0f, 0f, null);

                ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
                outBitmap.compress(Bitmap.CompressFormat.JPEG, quality, imgBytes);
                stream.clear();
                stream.setData(imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION);
                stream.put(PdfName.TYPE, PdfName.XOBJECT);
                stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
                stream.put(PdfName.FILTER, PdfName.DCTDECODE);
                stream.put(PdfName.WIDTH, new PdfNumber(width));
                stream.put(PdfName.HEIGHT, new PdfNumber(height));
                stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
            }
        }


        private void saveReader(PdfReader reader)  {
            try {
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPath));
                stamper.setFullCompression();
                stamper.close();
            }catch (Exception e){
                Log.e("error", "saveReader: " );
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("checksucesspath"," onPostExecute outputPath "+outputPath);

//            mPDFCompressedInterface.pdfCompressionEnded(outputPath, success);
        }
    }

//    public boolean reorderRemovePDF(String inputPath, String output, String pages) {
//        try {
//            PdfReader reader = new PdfReader(inputPath);
//            reader.selectPages(pages);
//            if (reader.getNumberOfPages() == 0) {
//                UtilString.getInstance().showSnackbar(mContext, R.string.remove_pages_error);
//                return false;
//            }
//            //if (reader.getNumberOfPages() )
//            PdfStamper pdfStamper = new PdfStamper(reader,
//                    new FileOutputStream(output));
//            pdfStamper.close();
//            UtilString.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
//                    .setAction(R.string.snackbar_viewAction, v ->
//                            mUtilFile.openFile(output, UtilFile.FileType.e_PDF)).show();
//            new HelperDB(mContext).insertRecord(output,
//                    mContext.getString(R.string.created));
//            return true;
//
//        } catch (IOException | DocumentException e) {
//            e.printStackTrace();
//            UtilString.getInstance().showSnackbar(mContext, R.string.remove_pages_error);
//            return false;
//        }
//    }


//    public void reorderPdfPages(Uri uri, String path, @NonNull InterfaceRearrange interfaceRearrange) {
//        new ReorderPdfPagesAsync(uri, path, mContext, interfaceRearrange).execute();
//    }

//    private class ReorderPdfPagesAsync extends AsyncTask<String, String, ArrayList<Bitmap>> {
//
//        private final Uri mUri;
//        private final String mPath;
////        private final InterfaceRearrange mInterfaceRearrange;
//        private final Activity mActivity;
//
//
//        ReorderPdfPagesAsync(Uri uri,
//                             String path,
//                             Activity activity,
//                             InterfaceRearrange interfaceRearrange) {
//            this.mUri = uri;
//            this.mPath = path;
//            this.mInterfaceRearrange = interfaceRearrange;
//            this.mActivity = activity;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mInterfaceRearrange.onPdfReorderStarted();
//        }
//
//        @Override
//        protected ArrayList<Bitmap> doInBackground(String... strings) {
//            ArrayList<Bitmap> bitmaps = new ArrayList<>();
//            ParcelFileDescriptor fileDescriptor = null;
//            try {
//                if (mUri != null)
//                    fileDescriptor = mActivity.getContentResolver().openFileDescriptor(mUri, "r");
//                else if (mPath != null)
//                    fileDescriptor = ParcelFileDescriptor.open(new File(mPath), MODE_READ_ONLY);
//                if (fileDescriptor != null) {
//                    PdfRenderer renderer = new PdfRenderer(fileDescriptor);
//                    bitmaps = getBitmaps(renderer);
//                    // close the renderer
//                    renderer.close();
//                }
//            } catch (IOException | SecurityException | IllegalArgumentException | OutOfMemoryError e) {
//                e.printStackTrace();
//            }
//            return bitmaps;
//        }
//
//
//        private ArrayList<Bitmap> getBitmaps(PdfRenderer renderer) {
//            ArrayList<Bitmap> bitmaps = new ArrayList<>();
//            final int pageCount = renderer.getPageCount();
//            for (int i = 0; i < pageCount; i++) {
//                PdfRenderer.Page page = renderer.openPage(i);
//                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
//                        Bitmap.Config.ARGB_8888);
//                // say we render for showing on the screen
//                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//
//                // do stuff with the bitmap
//                bitmaps.add(bitmap);
//                // close the page
//                page.close();
//            }
//            return bitmaps;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
//            super.onPostExecute(bitmaps);
//            if (bitmaps != null && !bitmaps.isEmpty()) {
//                mInterfaceRearrange.onPdfReorderCompleted(bitmaps);
//            } else {
//                mInterfaceRearrange.onPdfReorderFailed();
//            }
//        }
//    }

}
