package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;



public class AdapterPdsPageSign extends FragmentStatePagerAdapter {

    private Doc mDoc;

    public AdapterPdsPageSign(FragmentManager fragmentManager, Doc fASDoc) {
        super(fragmentManager);
        this.mDoc = fASDoc;
    }

    public int getCount() {
        return this.mDoc.getNumPages();
    }

    public Fragment getItem(int i) {
        return FragmentPdf.newInstance(i);
    }

}
