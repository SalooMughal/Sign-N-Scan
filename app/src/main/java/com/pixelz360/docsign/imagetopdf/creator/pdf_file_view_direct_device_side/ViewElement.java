package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import static android.view.MotionEvent.INVALID_POINTER_ID;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.view.ViewCompat;

import com.app.pdfconverter.compresspdf.mergepdffiles.fillandsign.pdfsigner.docusign.touchlistner.ScaleGesture;
import com.app.pdfconverter.compresspdf.mergepdffiles.fillandsign.pdfsigner.docusign.touchlistner.Vector2D;
import com.pixelz360.docsign.imagetopdf.creator.R;


public class ViewElement {

    public static int MOTION_THRESHOLD_LONG_PRESS = 12;
    public static boolean mBorderShown = false;
    public static   RelativeLayout mContainerView = null;
    public static Context mContext = null;
    public static SignElementPdf mElement = null;
    public static View mElementView = null;
    public static boolean mHasDragStarted = false;
    public static float mLastMotionX = 0.0f;
    public static float mLastMotionY = 0.0f;
    public static boolean mLongPress = false;
    public static ViewerPage mPageViewer = null;
    public static ScaleGesture mScaleGesture;
    public static float mScaleFactor = 1.0f;
    public static int MOTION_THRESHOLD = 3;
    public static float mResizeInitialPos = 0.0f;
    private final boolean isRotateEnabled = true;
    private final boolean isScaleEnabled = true;
    private final GestureDetector mGestureListener;
    private OnGestureControl mOnGestureControl = null;
/////////////////////
    private final boolean isTranslateEnabled = true;
    private final float minimumScale = 0.5f;
    private final float maximumScale = 10.0f;
    private final boolean mIsPinchScalable=true;
    private float mPrevX = 0f;
    private float mPrevY = 0f;
    public static SignElementPdf mref;
    private int mActivePointerId = INVALID_POINTER_ID;
    public static boolean isInProgress = false;

    class CustomDragShadowBuilder extends View.DragShadowBuilder {
        int mX;
        int mY;

        public void onDrawShadow(Canvas canvas) {
        }
        public CustomDragShadowBuilder(View view, int i, int i2) {
            super(view);
            this.mX = i;
            this.mY = i2;
        }
        public void onProvideShadowMetrics(Point point, Point point2) {
            super.onProvideShadowMetrics(point, point2);
            point2.set((int) (((float) this.mX) * mPageViewer.getScaleFactor()), (int) (((float) this.mY) * mPageViewer.getScaleFactor()));
            point.set((int) (((float) getView().getWidth()) * mPageViewer.getScaleFactor()), (int) (((float) getView().getHeight()) * mPageViewer.getScaleFactor()));
        }
    }

    class DragEventData {
        public ViewElement viewer;
        public float x;
        public float y;

        public DragEventData(ViewElement fASElementViewer, float f, float f2) {
            this.viewer = fASElementViewer;
            this.x = f;
            this.y = f2;
        }
    }

    public ViewElement(Context context, ViewerPage fASPageViewer, SignElementPdf fASElement, ActivityDigiSign activity) {
        mContext = context;
        mPageViewer = fASPageViewer;
        mElement = fASElement;
        fASElement.mElementViewer = this;
        mScaleGesture = new ScaleGesture(new ScaleGestureListener());

        mGestureListener = new GestureDetector(new GestureListener());
        mOnGestureControl=buildGestureController();
        createElement(fASElement);
    }

    protected OnGestureControl buildGestureController() {
        return new OnGestureControl() {

            @Override
            public void onLongClick() {

            }

            @Override
            public boolean onClick() {
                // Change the in-focus view
                return true;
            }


        };
    }

    private  class GestureListener extends GestureDetector.SimpleOnGestureListener  {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mOnGestureControl.onClick();

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            mOnGestureControl.onLongClick();
        }
    }

     interface OnGestureControl {
        boolean onClick();
        void onLongClick();
    }
    public ViewerPage getPageViewer() {
        return mPageViewer;
    }

    public SignElementPdf getElement() {
        return mElement;
    }

    public View getElementView() {
        return mElementView;
    }

    public RelativeLayout getContainerView() {
        return mContainerView;
    }

//    public ImageButton getImageButton() {
//        return this.mImageButton;
//    }

    private void createElement(SignElementPdf fASElement) {
        mElementView = createElementView(fASElement);
        mPageViewer.getPageView().addView(mElementView);
        mElementView.setTag(fASElement);
        if (!isElementInModel()) {
            addElementInModel(fASElement);
        }
        setListeners();
    }

    public void removeElement() {
        if (mElementView.getParent() != null) {
            mPageViewer.getPage().removeElement((SignElementPdf) mElementView.getTag());
            mPageViewer.hideElementPropMenu();
            mPageViewer.getPageView().removeView(mElementView);
        }
    }

    private View createElementView(SignElementPdf fASElement) {
        switch (fASElement.getType()) {
            case PDSElementTypeSignature:
                mref=fASElement;
                ViewSign createSignatureView = UtilViewSign.createSignatureView(mContext, fASElement, mPageViewer.getToViewCoordinatesMatrix());
                fASElement.setRect(new RectF(fASElement.getRect().left, fASElement.getRect().top, fASElement.getRect().left + mPageViewer.mapLengthToPDFCoordinates((float) createSignatureView.getSignatureViewWidth()), fASElement.getRect().bottom));
                fASElement.setStrokeWidth(mPageViewer.mapLengthToPDFCoordinates(createSignatureView.getStrokeWidth()));
                createSignatureView.setFocusable(true);
                createSignatureView.setFocusableInTouchMode(true);
                createSignatureView.setClickable(true);
                createSignatureView.setLongClickable(true);
                createResizeButton(createSignatureView);
                return createSignatureView;
         
            default:
                return null;
        }
    }


    private void addElementInModel(SignElementPdf fASElement) {
        mPageViewer.getPage().addElement(fASElement);
    }

    private boolean isElementInModel() {
        for (int i = 0; i < mPageViewer.getPage().getNumElements(); i++) {
            if (mPageViewer.getPage().getElement(i) == mElementView.getTag()) {
                return true;
            }
        }
        return false;
    }


    public void setListeners() {
        setTouchListener();
        setFocusListener();
    }


    private void setTouchListener() {

        mElementView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                view.requestFocus();
                mLongPress = true;
                return true;
            }
        });
        mElementView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                mScaleGesture.onTouchEvent(view, motionEvent,mPageViewer);
                mGestureListener.onTouchEvent(motionEvent);
                int action = motionEvent.getAction();

                switch (action & 255) {
                    case 0:
                        mPageViewer.setResizeInOperation(true);
//                        Log.e("naeem","eee");

                        mPrevX = motionEvent.getX();
                        mPrevY = motionEvent.getY();
                        mHasDragStarted = false;
                        mLongPress = false;
                        mLastMotionX = motionEvent.getX();
                        mLastMotionY = motionEvent.getY();
                        mActivePointerId = motionEvent.getPointerId(0);
                        view.bringToFront();
                        break;
                    case 1:
                        mPageViewer.setResizeInOperation(false);
                        Log.e("naeem","fff");

                        mHasDragStarted = false;
                        mPageViewer.setElementAlreadyPresentOnTap(true);
                        if (!(view instanceof ViewSign)) {
                            view.setVisibility(View.VISIBLE);
                            break;
                        }
                        mContainerView.setVisibility(View.VISIBLE);
                        break;
                    case 2:

                        if (!mHasDragStarted) {
//                            Log.e("naeem","ggg");

                            action = Math.abs((int) (motionEvent.getX() - mLastMotionX));
                            int abs = Math.abs((int) (motionEvent.getY() - mLastMotionY));
                            int access$700;
                            if (mLongPress) {
                                access$700 = ViewElement.MOTION_THRESHOLD_LONG_PRESS;
                            } else {
                                access$700 = ViewElement.MOTION_THRESHOLD;
                            }
                            if (motionEvent.getX() >= 0.0f && motionEvent.getY() >= 0.0f && mBorderShown && (action > access$700 || abs > access$700)) {
                                float x = motionEvent.getX();
                                float y = motionEvent.getY();
                                try{
                                    view.startDrag(ClipData.newPlainText("pos", String.format("%d %d", Integer.valueOf(Math.round(x)), Integer.valueOf(Math.round(y)))), new CustomDragShadowBuilder(view, Math.round(x), Math.round(y)), new DragEventData(ViewElement.this, x, y), 0);
                                }catch (Exception e){

                                }
                                mPageViewer.setResizeInOperation(true);

                                mHasDragStarted = true;
                                Log.e("naeem","hhh");

                            }
                            return true;
                        }
                        Log.e("naeem","ggg");

                        break;
                    case 3:
                        mPageViewer.setResizeInOperation(false);
                        break;
                    case 6:
                        Log.e("naeem","iiii");

                        mPageViewer.setResizeInOperation(false);

                        int pointerIndexPointerUp =
                                (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                        int pointerId = motionEvent.getPointerId(pointerIndexPointerUp);
                        if (pointerId == mActivePointerId) {
                            int newPointerIndex ;
                            if (pointerIndexPointerUp == 0)
                                 newPointerIndex =  1;
                            else
                                 newPointerIndex = 0;
                            try{
                                mPrevX = motionEvent.getX(newPointerIndex);
                                mPrevY = motionEvent.getY(newPointerIndex);

                            }catch (Exception w){

                            }
                            mActivePointerId = motionEvent.getPointerId(newPointerIndex);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void setFocusListener() {
        mElementView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {

                if (z) {
                    assignFocus();
                }
            }
        });
    }

    public void assignFocus() {
        mPageViewer.showElementPropMenu(this);
    }

    public static void createResizeButton(View view) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(11);
        layoutParams.addRule(15);
        mContainerView = new RelativeLayout(mContext);
        mContainerView.setFocusable(false);
        mContainerView.setFocusableInTouchMode(false);
    }

    public void showBorder() {
        changeColor(true);
        if (mContainerView.getParent() == null) {
            int signatureViewWidth;
            int signatureViewHeight;
            if (mElementView.getParent() == mPageViewer.getPageView()) {
                mElementView.setOnFocusChangeListener(null);
                mPageViewer.getPageView().removeView(mElementView);
                mContainerView.addView(mElementView);
            }
            mContainerView.setX(mElementView.getX());
            mContainerView.setY(mElementView.getY());
            mElementView.setX(0.0f);
            mElementView.setY(0.0f);
            if (mElementView instanceof ViewSign) {
                signatureViewWidth = ((ViewSign) mElementView).getSignatureViewWidth() ;
                signatureViewHeight = ((ViewSign) mElementView).getSignatureViewHeight();
            } else {
                // this.mElementView.measure(Math.round(-2.0f), Math.round(-2.0f));
                // this.mElementView.layout(0, 0, this.mElementView.getMeasuredWidth(), this.mElementView.getMeasuredHeight());
                signatureViewWidth = mElementView.getLayoutParams().width ;
                signatureViewHeight = mElementView.getLayoutParams().height;
            }
            mContainerView.setLayoutParams(new RelativeLayout.LayoutParams(signatureViewWidth, signatureViewHeight));
            mPageViewer.getPageView().addView(mContainerView);
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(2, mContext.getResources().getColor(R.color.colorAccent));
        mElementView.setBackground(gradientDrawable);
        mBorderShown = true;
    }

    public void hideBorder() {
        changeColor(false);
        if (mContainerView.getParent() == mPageViewer.getPageView()) {
            mElementView.setX(mContainerView.getX());
            mElementView.setY(mContainerView.getY());
            mPageViewer.getPageView().removeView(mContainerView);
//            this.mContainerView.removeView(this.mImageButton);
            if (mElementView.getParent() == mContainerView) {
                mContainerView.removeView(mElementView);
                mPageViewer.getPageView().addView(mElementView);
                setFocusListener();
            }
        }
        mElementView.setBackground(null);
        mBorderShown = false;
    }

    public void changeColor(boolean z) {
        int color = z ? mContext.getResources().getColor(R.color.colorAccent) : ViewCompat.MEASURED_STATE_MASK;
        if (mElementView instanceof ViewSign) {
            color = ((ViewSign) mElementView).getActualColor();
            ((ViewSign) mElementView).setStrokeColor(color);
        } else if (mElementView instanceof ImageView) {
            //((ImageView) this.mElementView).setColorFilter(color);
        }
    }

    public boolean isBorderShown() {
        return mBorderShown;
    }

    private  class ScaleGestureListener extends ScaleGesture.SimpleOnScaleGestureListener {
        private float mPivotX = 0f;
        private float mPivotY = 0f;
        private final Vector2D mPrevSpanVector = new Vector2D();

        @Override
        public boolean onScaleBegin(View view, ScaleGesture detector) {
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            mPrevSpanVector.set(detector.getCurrentSpanVector());
            return mIsPinchScalable;
        }

        @Override
        public boolean onScale(View view, ScaleGesture detector) {
            TransformInfo info = new TransformInfo();

            if (isScaleEnabled){
                info.deltaScale =    detector.getScaleFactor();
            } else{ info.deltaScale = 1.0f;}

//            if (isRotateEnabled) { info.deltaAngle = Vector2D.getAngle(
//                    mPrevSpanVector,
//                    detector.getCurrentSpanVector()
//            );
//            }else { info.deltaAngle = 0.0f;}

        if (isTranslateEnabled)
        {
            info.deltaX =detector.getFocusX() - mPivotX ;
        }
        else{
            info.deltaX =0.0f;
        }

        if (isTranslateEnabled) {
            info.deltaY = detector.getFocusY() - mPivotY   ;
        } else {info.deltaY =0.0f;}
            info.pivotX = mPivotX;
            info.pivotY = mPivotY;
            info.minimumScale = minimumScale;
            info.maximumScale = maximumScale;
            move(view, info);
            return !mIsPinchScalable;
        }
    }

    private  class TransformInfo {
        float deltaX = 0f;
        float deltaY = 0f;
        float deltaScale = 0f;
        float deltaAngle = 0f;
        float pivotX = 0f;
        float pivotY = 0f;
        float minimumScale = 0f;
        float maximumScale = 0f;
    }

    private void move(View view, TransformInfo info) {
        computeRenderOffset(view, info.pivotX, info.pivotY);
        adjustTranslation(view, info.deltaX, info.deltaY);
        float scale = view.getScaleX() * info.deltaScale;
        scale = max(info.minimumScale, min(info.maximumScale, scale));
        try{
            view.setScaleX(scale);
        }catch (Exception e){

        }
        try{
            view.setScaleY(scale);
        }catch (Exception e){

        }

    }

    private void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = new float[]{deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY( view.getTranslationY() + deltaVector[1]);
    }

    private void computeRenderOffset(View view,float pivotX,float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }
        float[] prevPoint = new float[]{0.0f, 0.0f};

        view.getMatrix().mapPoints(prevPoint);
        view.setPivotX(pivotX);
        view.setPivotY(pivotY);
        float[] currPoint = new float[]{0.0f, 0.0f};
        view.getMatrix().mapPoints(currPoint);
        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];
        view.setTranslationX(view.getTranslationX() - offsetX);
        view.setTranslationY(view.getTranslationY() - offsetY);
    }

}
