package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class ViewPager extends VerticalViewPager {
    private Context mActivityContext = null;
    private boolean mDownReceieved = true;

    public ViewPager(Context context) {
        super(context);
        this.mActivityContext = context;
        init();
    }

    public ViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mActivityContext = context;
        init();
    }

    private void init() {
        setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
                View focusedChild = ViewPager.this.getFocusedChild();
                if (focusedChild != null) {
                    ViewerPage pDSPageViewerPage = (ViewerPage) ((ViewGroup) focusedChild).getChildAt(0);
                    if (pDSPageViewerPage != null) {
                        pDSPageViewerPage.resetScale();
                    }
                }
                if (ViewPager.this.mActivityContext != null) {
                    ActivityDigiSign.page_number=i+1;
                    ((ActivityDigiSign) ViewPager.this.mActivityContext).updatePageNumber(i + 1);
                }
            }
        });
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
            this.mDownReceieved = true;
        }
        if (motionEvent.getPointerCount() <= 1 && this.mDownReceieved) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        this.mDownReceieved = false;
        return false;

    }
}
