package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;


import static com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side.ActivityDigiSign.createFreeHandView;
import static com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side.ActivityDigiSign.delete_sign;
import static com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side.ActivityDigiSign.mContext;
import static com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side.ActivityDigiSign.sign;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.icu.text.DecimalFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.app.pdfconverter.compresspdf.mergepdffiles.fillandsign.pdfsigner.docusign.touchlistner.ScaleGesture;
import com.app.pdfconverter.compresspdf.mergepdffiles.fillandsign.pdfsigner.docusign.touchlistner.Vector2D;
import com.pixelz360.docsign.imagetopdf.creator.R;


public class MultiTouchListener implements OnTouchListener {
    private static final int INVALID_POINTER_ID = -1;
    Bitmap bitmap;
    boolean bt = false;
    private boolean disContinueHandleTransparecy = true;
    GestureDetector gd = null;
    private boolean handleTransparecy = false;
    public boolean isRotateEnabled = true;
    public boolean isRotationEnabled = false;
    public boolean isScaleEnabled = true;
    public boolean isTranslateEnabled = true;
    private TouchCallbackListener listener = null;
    private int mActivePointerId = -1;
    private float mPrevX;
    private float mPrevY;
    private ScaleGesture mScaleGesture = new ScaleGesture(new ScaleGestureListener());
    public float maximumScale = 8.0f;
    public float minimumScale = 0.5f;
    float scaleFactor;
    float old=0f;
    public static float x_sign=0f;
    public static float y_sign=0f;
    public static float x_coordinate=0f;
    public static float y_coordinate=0f;
    boolean  value_sign_check=false;
    private class ScaleGestureListener extends ScaleGesture.SimpleOnScaleGestureListener {
        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector;

        private ScaleGestureListener() {
            this.mPrevSpanVector = new Vector2D();
        }

        public boolean onScaleBegin(View view, ScaleGesture scaleGesture) {
            this.mPivotX = scaleGesture.getFocusX();
            this.mPivotY = scaleGesture.getFocusY();
            this.mPrevSpanVector.set(scaleGesture.getCurrentSpanVector());
            TransformInfo transformInfo = new TransformInfo();
            transformInfo.deltaScale = MultiTouchListener.this.isScaleEnabled ? scaleGesture.getScaleFactor() : 1.0f;
            old =  Math.max(transformInfo.minimumScale, Math.min(transformInfo.maximumScale, view.getScaleX() * transformInfo.deltaScale));
            DecimalFormat df = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                df = new DecimalFormat("#.##");
                old= Float.parseFloat(df.format(old));
            }
            return true;
        }

        public boolean onScale(View view, ScaleGesture scaleGesture) {
            TransformInfo transformInfo = new TransformInfo();
            transformInfo.deltaScale = MultiTouchListener.this.isScaleEnabled ? scaleGesture.getScaleFactor() : 1.0f;
            float f = 0.0f;
            transformInfo.deltaAngle = MultiTouchListener.this.isRotateEnabled ? Vector2D.getAngle(this.mPrevSpanVector, scaleGesture.getCurrentSpanVector()) : 0.0f;
            transformInfo.deltaX = MultiTouchListener.this.isTranslateEnabled ? scaleGesture.getFocusX() - this.mPivotX : 0.0f;
            if (MultiTouchListener.this.isTranslateEnabled) {
                f = scaleGesture.getFocusY() - this.mPivotY;
            }
            transformInfo.deltaY = f;
            transformInfo.pivotX = this.mPivotX;
            transformInfo.pivotY = this.mPivotY;
            transformInfo.minimumScale = MultiTouchListener.this.minimumScale;
            transformInfo.maximumScale = MultiTouchListener.this.maximumScale;
//            MultiTouchListener.this.move(view, transformInfo);
            MultiTouchListener.this.move((View) view.getParent(),transformInfo);
            return false;
        }
    }

    public interface TouchCallbackListener {
        void onTouchCallback(View view);
        void onTouchUpCallback(View view);
    }

    private class TransformInfo {
        public float deltaAngle;
        public float deltaScale;
        public float deltaX;
        public float deltaY;
        public float maximumScale;
        public float minimumScale;
        public float pivotX;
        public float pivotY;
        private TransformInfo() {
        }
    }

    private static float adjustAngle(float f) {
        return f > 180.0f ? f - 360.0f : f < -180.0f ? f + 360.0f : f;
    }

    public MultiTouchListener setHandleTransparecy(boolean z) {
        this.handleTransparecy = z;
        return this;
    }

    public MultiTouchListener setGestureListener(GestureDetector gestureDetector) {
        this.gd = gestureDetector;
        return this;
    }

    public MultiTouchListener setOnTouchCallbackListener(TouchCallbackListener touchCallbackListener) {
        this.listener = touchCallbackListener;
        return this;
    }

    public MultiTouchListener enableRotation(boolean z) {
        this.isRotationEnabled = z;
        return this;
    }

    public MultiTouchListener setMinScale(float f) {
        this.minimumScale = f;
        return this;
    }

    public void move(View view, TransformInfo transformInfo) {
        computeRenderOffset(view, transformInfo.pivotX, transformInfo.pivotY);
        adjustTranslation(view, transformInfo.deltaX, transformInfo.deltaY);
        float max =  Math.max(transformInfo.minimumScale, Math.min(transformInfo.maximumScale, view.getScaleX() * transformInfo.deltaScale));
        DecimalFormat df = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new DecimalFormat("#.##");
            max= Float.parseFloat(df.format(max));
        }
        view.setScaleX(max);
        view.setScaleY(max);
//        if(max>=old){
//            height_sign= (int) (height_sign+ max);
//            width_sign= (int) (width_sign+ max);
//        }
//        else {
//            height_sign= (int) (height_sign- max);
//            width_sign= (int) (width_sign- max);
//        }
        old=max;
        if (this.isRotationEnabled) {
            view.setRotation(adjustAngle(view.getRotation() + transformInfo.deltaAngle));
        }
    }


    private static void adjustTranslation(View view, float f, float f2) {
        float[] fArr = {f, f2};
        view.getMatrix().mapVectors(fArr);
        view.setTranslationX(view.getTranslationX() + fArr[0]);
        view.setTranslationY(view.getTranslationY() + fArr[1]);
        x_coordinate=view.getTranslationX() + fArr[0];
        y_coordinate=view.getTranslationY() + fArr[1];
//        Log.e("coordinates", "onTouch: "+(view.getTranslationX() + fArr[0])+"--------"+(view.getTranslationY() + fArr[1] ));

    }

    private static void computeRenderOffset(View view, float f, float f2) {
        if (view.getPivotX() != f || view.getPivotY() != f2) {
            float[] fArr = {0.0f, 0.0f};
            view.getMatrix().mapPoints(fArr);
            view.setPivotX(f);
            view.setPivotY(f2);
            float[] fArr2 = {0.0f, 0.0f};
            view.getMatrix().mapPoints(fArr2);
            float f3 = fArr2[1] - fArr[1];
            view.setTranslationX(view.getTranslationX() - (fArr2[0] - fArr[0]));
            view.setTranslationY(view.getTranslationY() - f3);
        }
    }
    public boolean handleTransparency(View view, MotionEvent motionEvent) {
        try {
            boolean z = true;
            if (motionEvent.getAction() == 2) {
                Log.i("MOVE_TESTs", "ACTION_MOVE");
                if (this.bt) {
                    return true;
                }
            }
            if (motionEvent.getAction() == 1) {
                Log.i("MOVE_TESTs", "ACTION_UP");
                if (this.bt) {
                    this.bt = false;
                    if (this.bitmap != null) {
                        this.bitmap.recycle();
                    }
                    return true;
                }
            }
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            int rawX = (int) (motionEvent.getRawX() - ((float) iArr[0]));
            int rawY = (int) (motionEvent.getRawY() - ((float) iArr[1]));
            float rotation = view.getRotation();
            Matrix matrix = new Matrix();
            matrix.postRotate(-rotation);
            float[] fArr = {(float) rawX, (float) rawY};
            matrix.mapPoints(fArr);
            int i = (int) fArr[0];
            int i2 = (int) fArr[1];
            if (motionEvent.getAction() == 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("View Width/height ");
                sb.append(view.getWidth());
                sb.append(" / ");
                sb.append(view.getHeight());
                Log.i("MOVE_TESTs", sb.toString());
                this.bt = false;
                view.setDrawingCacheEnabled(true);
                this.bitmap = Bitmap.createBitmap(view.getDrawingCache());
                i = (int) (((float) i) * (((float) this.bitmap.getWidth()) / (((float) this.bitmap.getWidth()) * view.getScaleX())));
                i2 = (int) (((float) i2) * (((float) this.bitmap.getHeight()) / (((float) this.bitmap.getHeight()) * view.getScaleX())));
                view.setDrawingCacheEnabled(false);
            }
            if (i >= 0 && i2 >= 0 && i <= this.bitmap.getWidth()) {
                if (i2 <= this.bitmap.getHeight()) {
                    if (this.bitmap.getPixel(i, i2) != 0) {
                        z = false;
                    }
                    if (motionEvent.getAction() != 0) {
                        return z;
                    }
                    this.bt = z;
                    return z;
                }
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }
@Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

   /* int t=location[1]-250;
    if((t) <= y1){
        delbtn.setBackgroundResource(R.drawable.dialog_img);
    }*/

    this.mScaleGesture.onTouchEvent(view, motionEvent);

        int i = 0;
        if (this.handleTransparecy && this.disContinueHandleTransparecy) {
            if (handleTransparency(view, motionEvent)) {
                return false;
            }
            this.disContinueHandleTransparecy = false;
        }
        if (this.gd != null) {
            this.gd.onTouchEvent(motionEvent);
        }
        if (!this.isTranslateEnabled) {
            return true;
        }
        int action = motionEvent.getAction();
        int actionMasked = motionEvent.getActionMasked() & action;
        Log.e("naeem","-----"+actionMasked );

    if (actionMasked != 6) {
            switch (actionMasked) {
                case 0:
                    if (this.listener != null) {
                        this.listener.onTouchCallback(view);
                    }
//                    if (view instanceof AutofitTextRel) {
//                        ((AutofitTextRel) view).setBorderVisibility(true);
//                    }
                    this.mPrevX = motionEvent.getX();
                    this.mPrevY = motionEvent.getY();
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    return true;
                case 1:
                    this.mActivePointerId = -1;
                    this.disContinueHandleTransparecy = true;
                    if (this.listener == null) {
                        return true;
                    }
                    this.listener.onTouchUpCallback(view);
                    if(value_sign_check){
                        sign.setVisibility(View.GONE);
//                        delete_sign.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary)); // Add tint color

                        delete_sign.setVisibility(View.INVISIBLE);
                        createFreeHandView.clear();
                    }
                    return true;
                case 2:
                    int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (findPointerIndex == -1) {
                        return true;
                    }
                    float x = motionEvent.getX(findPointerIndex);
                    float y = motionEvent.getY(findPointerIndex);
                    x_sign=x-mPrevX;
                    y_sign=y-mPrevY;

                    if (this.mScaleGesture.isInProgress()) {
                        return true;
                    }
                    adjustTranslation((View) view.getParent(), x - this.mPrevX, y - this.mPrevY);

/////////////////////////////// visibility of delete button and signature ////////////////////////////////////////////////////////
                    sign.setVisibility(View.VISIBLE);
                    int y1 = (int) motionEvent.getRawY();
                    int[] location = new int[2];
                    delete_sign.getLocationOnScreen(location);
                    int currentvalue=location[1]-100;
                    int currentvalue1=location[1]-200;

                    if(currentvalue <= y1){
                        delete_sign.setColorFilter(mContext.getResources().getColor(R.color.red)); // Add tint color
                        value_sign_check=true;
                        sign.setVisibility(View.GONE);
                    }
                    else
                    {
                        value_sign_check=false;
                        if(currentvalue1<=y1){
                            sign.setVisibility(View.VISIBLE);
                            delete_sign.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary)); // Add tint color
                            delete_sign.setVisibility(View.VISIBLE);
                        }
                        else {
                            sign.setVisibility(View.VISIBLE);
                            delete_sign.setVisibility(View.INVISIBLE);
                        }
                    }
                    return true;
                case 3:
                    this.mActivePointerId = -1;
                    return true;
                default:
                    return true;
            }
        }
        else {
            int i2 = (65280 & action) >> 8;
            if (motionEvent.getPointerId(i2) != this.mActivePointerId) {
                return true;
            }
            if (i2 == 0) {
                i = 1;
            }
            this.mPrevX = motionEvent.getX(i);
            this.mPrevY = motionEvent.getY(i);
            this.mActivePointerId = motionEvent.getPointerId(i);
            return true;
        }
    }
}
