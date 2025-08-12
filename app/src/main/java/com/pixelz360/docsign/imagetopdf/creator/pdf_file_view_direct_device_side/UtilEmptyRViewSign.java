package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class UtilEmptyRViewSign extends RecyclerView {

    private View emptyView;

    private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            showEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            showEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            showEmptyView();
        }
    };

    public UtilEmptyRViewSign(Context context) {
        super(context);
    }


    public UtilEmptyRViewSign(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UtilEmptyRViewSign(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void showEmptyView() {

        Adapter<?> adapter = getAdapter();
        if (adapter != null && emptyView != null) {
            if (adapter.getItemCount() == 0) {
                emptyView.setVisibility(VISIBLE);
                UtilEmptyRViewSign.this.setVisibility(GONE);
            } else {
                emptyView.setVisibility(GONE);
                UtilEmptyRViewSign.this.setVisibility(VISIBLE);
            }
        }

    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
            observer.onChanged();
        }
    }

    public void setEmptyView(View v) {
        emptyView = v;
    }
}

