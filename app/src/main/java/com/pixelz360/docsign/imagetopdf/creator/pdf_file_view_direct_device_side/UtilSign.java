package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

import com.pixelz360.docsign.imagetopdf.creator.R;

import java.io.File;

public class UtilSign {

    private static PopupWindow sSignaturePopUpMenu;
    private static View mSignatureLayout;
    public static ViewSign showFreeHandView(Context mCtx, File file) {

        ViewSign createFreeHandView = UtilsSign.createFreeHandView((((int) mCtx.getResources().getDimension(R.dimen.sign_menu_width)) - ((int) mCtx.getResources().getDimension(R.dimen.sign_left_offset))) - (((int) mCtx.getResources().getDimension(R.dimen.sign_right_offset)) * 3), ((int) mCtx.getResources().getDimension(R.dimen.sign_button_height)) - ((int) mCtx.getResources().getDimension(R.dimen.sign_top_offset)), file, mCtx);
        LayoutParams layoutParams = new LayoutParams(0, 0);
        layoutParams.addRule(9);
        layoutParams.setMargins((int) mCtx.getResources().getDimension(R.dimen.sign_left_offset), (int) mCtx.getResources().getDimension(R.dimen.sign_top_offset), 0, 0);
        createFreeHandView.setLayoutParams(layoutParams);
        return  createFreeHandView;


    }

    public static boolean isSignatureMenuOpen() {
        return sSignaturePopUpMenu != null && sSignaturePopUpMenu.isShowing();
    }
    public static void dismissSignatureMenu() {
        if (sSignaturePopUpMenu != null && sSignaturePopUpMenu.isShowing()) {
            sSignaturePopUpMenu.dismiss();
            mSignatureLayout = null;
        }
    }
}
