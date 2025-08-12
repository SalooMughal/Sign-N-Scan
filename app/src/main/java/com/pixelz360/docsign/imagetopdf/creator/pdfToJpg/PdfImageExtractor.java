package com.pixelz360.docsign.imagetopdf.creator.pdfToJpg;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.parser.*;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;


import java.util.ArrayList;

public class PdfImageExtractor implements IEventListener {

    private final String outputDir;
    private final ArrayList<String> extractedImagePaths;
    private final ImageExtractionCallback callback;

    public PdfImageExtractor(String outputDir, ImageExtractionCallback callback) {
        this.outputDir = outputDir;
        this.extractedImagePaths = new ArrayList<>();
        this.callback = callback;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_IMAGE)) {
            try {
                ImageRenderInfo imageRenderInfo = (ImageRenderInfo) data;
                PdfImageXObject imageObject = imageRenderInfo.getImage();
                if (imageObject != null) {
                    File savedFile = saveImage(imageObject.getImageBytes(), outputDir, "image_" + System.currentTimeMillis() + ".jpg");
                    if (savedFile != null) {
                        extractedImagePaths.add(savedFile.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                Log.e("PdfImageExtractor", "Error extracting image: " + e.getMessage());
            }
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return Collections.singleton(EventType.RENDER_IMAGE);
    }

    private File saveImage(byte[] imageBytes, String directory, String fileName) {
        try {
            File outputFile = new File(directory, fileName);
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(imageBytes);
            fos.close();
            Log.d("PdfImageExtractor", "Saved image to: " + outputFile.getAbsolutePath());
            return outputFile;
        } catch (IOException e) {
            Log.e("PdfImageExtractor", "Error saving image: " + e.getMessage());
            return null;
        }
    }

    public static void extractImagesFromPdf(Context context, Uri pdfUri, String outputDir, ImageExtractionCallback callback) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(pdfUri);
            if (inputStream == null) {
                callback.onExtractionFailed("Unable to open PDF file.");
                return;
            }

            PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputStream));
            PdfImageExtractor extractor = new PdfImageExtractor(outputDir, callback);
            PdfCanvasProcessor parser = new PdfCanvasProcessor(extractor);
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
                parser.processPageContent(pdfDocument.getPage(i));
            }
            pdfDocument.close();

            callback.onExtractionComplete(extractor.extractedImagePaths);

        } catch (IOException e) {
            callback.onExtractionFailed(e.getMessage());
        }
    }

    public interface ImageExtractionCallback {
        void onExtractionComplete(ArrayList<String> extractedImagePaths);
        void onExtractionFailed(String errorMessage);
    }
}

