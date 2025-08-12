package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.pdf.PdfRenderer;
import android.util.SizeF;



import java.util.ArrayList;

public class Page {
    private static final transient SizeF DEF_PAGE_SIZE = new SizeF(595.0f, 842.0f);
    private static final transient Object sSynchronizedObject = new Object();
    private ArrayList<SignElementPdf> mElements;
    private SizeF mPageSize = null;
    private Doc mPDFDoc;
    private int mpageNumber;
    private ViewerPage mviewer;

    public Page(int i, Doc fASPDFDoc) {
        this.mpageNumber = i;
        this.mPDFDoc = fASPDFDoc;
        this.mElements = new ArrayList();

    }

    public SizeF getPageSize() {
        if (this.mPageSize == null) {
            synchronized (Doc.getLockObject()) {
                synchronized (getDocument()) {
                    PdfRenderer.Page openPage = ((Doc) getDocument()).getRenderer().openPage(getNumber());
                    this.mPageSize = new SizeF((float) openPage.getWidth(), (float) openPage.getHeight());
                    openPage.close();
                }
            }
        }
        return this.mPageSize;
    }


    public void renderPage(Context context, Bitmap bitmap, boolean z, boolean z2) {
        int i = z2 ? 2 : 1;
        synchronized (Doc.getLockObject()) {
            synchronized (getDocument()) {

                PdfRenderer.Page openPage = ((Doc) getDocument()).getRenderer().openPage(getNumber());
                this.mPageSize = new SizeF((float) openPage.getWidth(), (float) openPage.getHeight());
                openPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                openPage.close();
            }
        }
    }

    public void setPageViewer(ViewerPage mviewer) {
        this.mviewer = mviewer;
    }

    public ViewerPage getPageViewer() {
        return mviewer;
    }

    public Doc getDocument() {
        return this.mPDFDoc;
    }

    public int getNumber() {
        return this.mpageNumber;
    }

    public void removeElement(SignElementPdf fASElement) {
        this.mElements.remove(fASElement);
    }

    public void addElement(SignElementPdf fASElement) {
        this.mElements.add(fASElement);
    }


    public int getNumElements() {
        return this.mElements.size();
    }

    public SignElementPdf getElement(int i) {
        return (SignElementPdf) this.mElements.get(i);
    }

    public ArrayList<SignElementPdf> getElements() {
        return this.mElements;
    }

    public void updateElement(SignElementPdf fASElement, RectF rectF, float f, float f2, float f3, float f4) {
        Object obj;
        if (rectF == null || rectF.equals(fASElement.getRect())) {
            obj = null;
        } else {
            fASElement.setRect(rectF);
            obj = 1;
        }
        if (!(f == 0.0f || f == fASElement.getSize())) {
            fASElement.setSize(f);
            obj = 1;
        }
        if (!(f2 == 0.0f || f2 == fASElement.getMaxWidth())) {
            fASElement.setMaxWidth(f2);
            obj = 1;
        }
        if (!(f3 == 0.0f || f3 == fASElement.getStrokeWidth())) {
            fASElement.setStrokeWidth(f3);
            obj = 1;
        }
        if (!(f4 == 0.0f || f4 == fASElement.getLetterSpace())) {
            fASElement.setLetterSpace(f4);
            obj = 1;
        }
        if (obj != null) {
//            this.mPDFDocument.setChanged();
        }
    }
}
