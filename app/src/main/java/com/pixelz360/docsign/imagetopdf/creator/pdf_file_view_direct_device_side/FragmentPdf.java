package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.pixelz360.docsign.imagetopdf.creator.R;


public class FragmentPdf extends Fragment {

    ViewerPage mPageViewer = null;

    public static FragmentPdf newInstance(int i) {
        FragmentPdf fASFragment = new FragmentPdf();
        Bundle bundle = new Bundle();
        bundle.putInt("pageNum", i);
        fASFragment.setArguments(bundle);
        return fASFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_layout, viewGroup, false);
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.fragment);
        try {
            ViewerPage fASPageViewer = new ViewerPage(viewGroup.getContext(),(ActivityDigiSign) getActivity(),((ActivityDigiSign) getActivity()).getDocument().getPage(getArguments().getInt("pageNum")));
            this.mPageViewer = fASPageViewer;
            linearLayout.addView(fASPageViewer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inflate;
    }

    public void onDestroyView() {
        if (this.mPageViewer != null) {
            this.mPageViewer.cancelRendering();
            this.mPageViewer = null;
        }
        super.onDestroyView();
    }
}
