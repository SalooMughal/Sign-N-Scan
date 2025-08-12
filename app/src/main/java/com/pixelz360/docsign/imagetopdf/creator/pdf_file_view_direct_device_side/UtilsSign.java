package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;

public class UtilsSign {

    private static int EXTRA_WIDTH_PADDING = 0;

    public static class ViewHolder {
        public RectF boundingBox;
        public int inkColor;
        public float strokeWidth;
        public ArrayList<ArrayList<Float>> inkList;
    }

    public static void saveSignature(Context context, ViewSign viewSign) {
        ArrayList<ArrayList<Float>> arrayList = viewSign.mInkList;
        RectF rectF = viewSign.getBoundingBox();
        if (arrayList.size() != 0) {
            OutputStream openFileOutput;
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.inkList = arrayList;
            viewHolder.boundingBox = rectF;
            viewHolder.inkColor = viewSign.mStrokeColor;
            viewHolder.strokeWidth = viewSign.getStrokeWidth();
            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF Converter";
            File myDir = new File(fullPath + "/FreeHand");
            String uniqueString = UUID.randomUUID().toString();
            File file = new File(myDir.getAbsolutePath(), uniqueString);
            Gson gson = new Gson();

            try {
                openFileOutput = new FileOutputStream(file);
                writeToStream(openFileOutput, gson.toJson(viewHolder));
                openFileOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static ViewSign createFreeHandView(int i, File file, Context context) {
        int i2 = i - 30;
        ViewSign viewSign = null;
        try {
            ViewHolder readSignatureHolder = readSignatureHolder(context, file);
            if (readSignatureHolder != null) {
                if (((float) i) > readSignatureHolder.boundingBox.height()) {
                    EXTRA_WIDTH_PADDING = 30;
                    return createFreeHandView(i, i, file, context);
                }
                RectF rectF = readSignatureHolder.boundingBox;
                float height = ((float) i2) / readSignatureHolder.boundingBox.height();
                int width = (((int) (readSignatureHolder.boundingBox.width() * height)) + 30) + 30;
                ArrayList arrayList = readSignatureHolder.inkList;
                float f = (float) 15;
                viewSign = createFreeHandView(width, i, arrayList, rectF, height, height, (rectF.left * height) - f, (rectF.top * height) - f, readSignatureHolder.strokeWidth, readSignatureHolder.inkColor, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewSign;
    }

    public static ImageView createImageView(int i, Bitmap file, Context context) {
        int i2 = i - 30;
        ImageView signatureView = null;
        try {
            float height = ((float) i2) / file.getHeight();
            int width = (((int) (file.getWidth() * height)) + 30) + 30;
            signatureView = new ImageView(context);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, i);
            signatureView.setLayoutParams(layoutParams);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return signatureView;
    }

    private static ViewSign createFreeHandView(int i, int i2, ArrayList<ArrayList<Float>> arrayList, RectF rectF, float f, float f2, float f3, float f4, float f5, int strokeColor, Context context) {
        int i3 = i;
        int i4 = i2;
        ViewSign viewSign = new ViewSign(context, i, i2);
        viewSign.setStrokeWidth(f5);
        viewSign.setStrokeColor(strokeColor);
        viewSign.setmActualColor(strokeColor);
        viewSign.setEditable(false);
        ArrayList<ArrayList<Float>> arrayList2 = arrayList;
        viewSign.initializeInkList(arrayList);
        viewSign.fillColor();
        viewSign.scaleAndTranslatePath(arrayList2, rectF, f, f2, f3, f4);
        viewSign.invalidate();
        return viewSign;
    }

    public static ViewSign createFreeHandView(int i, int i2, File file, Context context) {
        Exception e;
        ViewSign viewSign = null;
        try {
            ViewHolder readSignatureHolder = readSignatureHolder(context, file);
            if (readSignatureHolder != null) {
                RectF rectF = readSignatureHolder.boundingBox;
                float fitXYScale = (rectF.height() > 1.0f || rectF.width() > 1.0f) ? getFitXYScale(i, i2, file, context) : 1.0f;
                float f = (float) i2;
                int i3 = 15;
                int height = f >= readSignatureHolder.boundingBox.height() * fitXYScale ? (int) ((f - (readSignatureHolder.boundingBox.height() * fitXYScale)) / 2.0f) : 15;
                float f2 = (float) i;
                if (f2 >= readSignatureHolder.boundingBox.width() * fitXYScale) {
                    i3 = (int) ((f2 - (readSignatureHolder.boundingBox.width() * fitXYScale)) / 2.0f);
                }
                ViewSign createFreeHandView = createFreeHandView(EXTRA_WIDTH_PADDING + i, i2, readSignatureHolder.inkList, rectF, fitXYScale, fitXYScale, (rectF.left * fitXYScale) - ((float) i3), (rectF.top * fitXYScale) - ((float) height), readSignatureHolder.strokeWidth, readSignatureHolder.inkColor, context);
                try {
                    EXTRA_WIDTH_PADDING = 0;
                    return createFreeHandView;
                } catch (Exception e2) {
                    viewSign = createFreeHandView;
                    e = e2;
                }
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return viewSign;
        }
        return viewSign;
    }


    public static void writeToStream(OutputStream outputStream, String str) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        outputStreamWriter.write(str);
        outputStreamWriter.close();
    }

    public static ViewHolder readSignatureHolder(Context context, File fileStreamPath) {

        if (fileStreamPath.exists()) {
            InputStream openFileInput = null;

            try {
                openFileInput = new FileInputStream(fileStreamPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                return (ViewHolder) new Gson().fromJson(getStringFromStream(openFileInput), new TypeToken<ViewHolder>() {
                }.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getStringFromStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                stringBuilder.append(readLine);
            } else {
                bufferedReader.close();
                return stringBuilder.toString();
            }
        }
    }

    private static float getFitXYScale(int i, int i2, File file, Context context) {
        ViewHolder readSignatureHolder = readSignatureHolder(context, file);
        if (readSignatureHolder != null) {
            float f = 0.0f;
            if (readSignatureHolder.boundingBox.height() != 0.0f) {
                float width = readSignatureHolder.boundingBox.width() / readSignatureHolder.boundingBox.height();
                Object obj = 1;
                int i3 = i - 15;
                int i4 = i2 - 15;
                while (obj != null) {
                    if (width > ((float) (i3 / i4))) {
                        f = ((float) i3) / readSignatureHolder.boundingBox.width();
                    } else {
                        f = ((float) i4) / readSignatureHolder.boundingBox.height();
                    }
                    if (((float) i2) <= readSignatureHolder.boundingBox.height() * f) {
                        i4 -= 7;
                    } else if (((float) i) > readSignatureHolder.boundingBox.width() * f) {
                        obj = null;
                    } else {
                        i3 -= 7;
                    }
                }
                return f;
            }
        }
        return 1.0f;
    }


}
