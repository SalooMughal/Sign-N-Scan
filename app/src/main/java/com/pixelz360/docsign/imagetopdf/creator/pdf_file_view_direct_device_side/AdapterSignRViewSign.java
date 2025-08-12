package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixelz360.docsign.imagetopdf.creator.R;

import java.io.File;
import java.util.List;

public class AdapterSignRViewSign extends RecyclerView.Adapter<AdapterSignRViewSign.MyViewHolder> {
    private List<File> signatures;

    public AdapterSignRViewSign(List<File> myDataset) {
        signatures = myDataset;
    }

    private OnItemClickListener onClickListener = null;

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.signature_item, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(viewGroup.getContext(), v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {

        ViewSign viewSign = (ViewSign) myViewHolder.layout.getChildAt(0);
        if (viewSign != null) {
            myViewHolder.layout.removeViewAt(0);
        }

        viewSign = UtilSign.showFreeHandView(myViewHolder.context, signatures.get(i));
        myViewHolder.layout.addView(viewSign);

        myViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, signatures.get(i), myViewHolder.getAdapterPosition());
            }

        });
        viewSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, signatures.get(i), myViewHolder.getAdapterPosition());
            }

        });

        myViewHolder.deleteSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onDeleteItemClick(v, signatures.get(i), myViewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return signatures.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout layout;
        public Context context;
        public ImageButton deleteSignature;
        public ViewSign viewSign;

        public MyViewHolder(Context cont, View v) {
            super(v);
            context = cont;
            layout = v.findViewById(R.id.freehanditem);
            deleteSignature = v.findViewById(R.id.deleteSignature);
//            viewSign = v.findViewById(R.id.signatureview11);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, File obj, int pos);

        void onDeleteItemClick(View view, File obj, int pos);
    }

}
