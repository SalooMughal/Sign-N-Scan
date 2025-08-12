package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;



public class UtilViewSign {

    public static void constrainRectXY(RectF rectF, RectF rectF2) {
        if (rectF.left < rectF2.left) {
            rectF.left = rectF2.left;
        } else if (rectF.right > rectF2.right) {
            rectF.left = rectF2.right - rectF.width();
        }
        if (rectF.top < rectF2.top) {
            rectF.top = rectF2.top;
        } else if (rectF.bottom > rectF2.bottom) {
            rectF.top = rectF2.bottom - rectF.height();
        }
    }

    public static ViewSign createSignatureView(Context context, SignElementPdf fASElement, Matrix matrix) {
        ViewSign createFreeHandView;
        RectF rectF = new RectF(fASElement.getRect());
        float strokeWidth = fASElement.getStrokeWidth();
        if (matrix != null) {
            matrix.mapRect(rectF);
            strokeWidth = matrix.mapRadius(strokeWidth);
        }

        createFreeHandView = UtilsSign.createFreeHandView((int) rectF.height(), fASElement.getFile(), context);
        if (createFreeHandView != null) {
            createFreeHandView.setX(rectF.left);
            createFreeHandView.setY(rectF.top);
        }
        return createFreeHandView;
    }


}
