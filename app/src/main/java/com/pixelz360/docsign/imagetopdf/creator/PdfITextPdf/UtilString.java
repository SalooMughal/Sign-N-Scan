package com.pixelz360.docsign.imagetopdf.creator.PdfITextPdf;


import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
//import static swati4star.createpdf.util.Constants.pdfDirectory;

public class UtilString {

    private UtilString() {
    }

    private static class SingletonHolder {
        static final UtilString INSTANCE = new UtilString();
    }

    public static UtilString getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean isEmpty(CharSequence s) {
        return s == null || s.toString().trim().equals("");
    }

    public boolean isNotEmpty(CharSequence s) {
        return s != null && !s.toString().trim().equals("");
    }

    public void showSnackbar(Activity context, int resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public void showSnackbar(Activity context, String resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public Snackbar showIndefiniteSnackbar(Activity context, String resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG);
    }

    public Snackbar getSnackbarwithAction(Activity context, int resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//    public String getDefaultStorageLocation() {
//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
//                pdfDirectory);
//        if (!dir.exists()) {
//            boolean isDirectoryCreated = dir.mkdir();
//            if (!isDirectoryCreated) {
//                Log.e("Error", "Directory could not be created");
//            }
//        }
//        return dir.getAbsolutePath() + PATH_SEPERATOR;
//    }


    public int parseIntOrDefault(CharSequence text, int def) throws NumberFormatException {
        if (isEmpty(text))
            return def;
        else
            return Integer.parseInt(text.toString());
    }
}
