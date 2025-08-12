package com.pixelz360.docsign.imagetopdf.creator.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.pixelz360.docsign.imagetopdf.creator.MainApplication;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.RvListenerPdf;
import com.pixelz360.docsign.imagetopdf.creator.models.ModelPdf;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdapterPdfDeviceFolder extends RecyclerView.Adapter<AdapterPdfDeviceFolder.HolderPdf> {

    private Context context;
    public ArrayList<ModelPdf> pdfArrayList;
    public ArrayList<ModelPdf> pdfArrayListSelected;
    private RvListenerPdf rvListenerPdf;

    private static final String TAG = "ADAPTER_PDF";

    ArrayList<Integer> selectedItems = new ArrayList<>();



    public AdapterPdfDeviceFolder(Context context, ArrayList<ModelPdf> pdfArrayList, RvListenerPdf rvListenerPdf) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.rvListenerPdf = rvListenerPdf;
    }

    @NonNull
    @Override
    public HolderPdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_row_pdf, parent, false);
        return new HolderPdf(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdf holder, @SuppressLint("RecyclerView") int position) {
        ModelPdf modelPdf = pdfArrayList.get(position);

        String name = modelPdf.getFile().getName();
        long timestamp = modelPdf.getFile().lastModified();

        String formattedDate = MainApplication.formatTimestamp(timestamp);

        loadFileSize(modelPdf, holder);
        loadThumbnailFromPdfFile(modelPdf, holder);

        holder.name.setText(name);
        holder.date.setText(formattedDate);

//        holder.checkBox.setVisibility(selectedItems.isEmpty() ? View.GONE : View.VISIBLE);
        holder.itemView.setAlpha(selectedItems.contains(position) ? 0.5f : 1.0f);

        if (selectedItems.isEmpty()){
            holder.checkBox.setVisibility(View.GONE);
            holder.sharemoreLayout.setVisibility(View.VISIBLE);
        }else {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.sharemoreLayout.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListenerPdf.onPdfClick(modelPdf, position);
            }
        });

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvListenerPdf.onPdfMoreClick(modelPdf, position, holder);
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", modelPdf.getFile());
                Intent target = ShareCompat.IntentBuilder.from((Activity) context).setStream(contentUri).getIntent();
                target.setData(contentUri);
                target.setType("application/pdf");
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (target.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(target);
                }


            }
        });

    }

    private void loadFileSize(ModelPdf modelPdf, HolderPdf holder) {
        double bytes = modelPdf.getFile().length();

        double kb = bytes / 1024;
        double mb = kb / 1024;

        String size = "";

        if (mb >= 1) {
            size = String.format("%.2f", mb) + " MB";
        } else if (kb >= 1) {
            size = String.format("%.2f", kb) + " KB";
        } else {
            size = String.format("%.2f", bytes) + " bytes";
        }
        Log.d(TAG, "loadFileSize: File Size: " + size);

        holder.size.setText(size);
    }


    private void loadThumbnailFromPdfFile(ModelPdf modelPdf, HolderPdf holder) {

        Log.d(TAG, "loadThumbnailFromPdfFile: ");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.myLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap thumbnailBitmap = null;
                int pageCount = 0;

                try {
                    ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(modelPdf.getFile(), ParcelFileDescriptor.MODE_READ_ONLY);

                    PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);

                    pageCount = pdfRenderer.getPageCount();
                    if (pageCount <= 0) {
                        Log.d(TAG, "loadThumbnailFromPdfFile run: No Pages");
                    } else {
                        PdfRenderer.Page currentPage = pdfRenderer.openPage(0);
                        thumbnailBitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);


                        currentPage.render(thumbnailBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "loadThumbnailFromPdfFile run: ", e);
                }

                Bitmap finalThumbnailBitmap = thumbnailBitmap;
                int finalPageCount = pageCount;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "loadThumbnailFromPdfFile run: Setting thumbnail ");

                        Glide.with(context)
                                .load(finalThumbnailBitmap)
                                .fitCenter()
                                .placeholder(R.drawable.ic_pdf_black)
                                .into(holder.thumbnailIv);
                        //set pages count to pagesTv
                        holder.page.setText("" + finalPageCount + "  Pages");
                    }
                });
            }

        });


    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    public int getItemViewType(int position)
    {
        return position;
    }

    public class HolderPdf extends RecyclerView.ViewHolder {

        public TextView name, page, size, date;
        public ImageView thumbnailIv;
        public  ImageButton moreBtn,shareBtn;
        CheckBox checkBox;
        LinearLayout sharemoreLayout;

        public HolderPdf(@NonNull View itemView) {
            super(itemView);

            sharemoreLayout = itemView.findViewById(R.id.sharemoreLayout);
            name = itemView.findViewById(R.id.nameTv);
            page = itemView.findViewById(R.id.pagesTv);
            size = itemView.findViewById(R.id.sizeTv);
            date = itemView.findViewById(R.id.dateTv);
            thumbnailIv = itemView.findViewById(R.id.thumbnailIv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
                checkBox = itemView.findViewById(R.id.checkBox);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (selectedItems.contains(position)) {
                    selectedItems.remove(Integer.valueOf(position));
                } else {
                    selectedItems.add(position);
                }
                notifyItemChanged(position);
                if (rvListenerPdf != null) {
                    rvListenerPdf.onItemSelectionChanged();
                    rvListenerPdf.onItemSelectionChangedCount(selectedItems.size());
                }
                notifyDataSetChanged();  // To update all items for checkbox visibility
                return true;
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int position = getAdapterPosition();
                    if (selectedItems.contains(position)) {
                        selectedItems.remove(Integer.valueOf(position));
                    } else {
                        selectedItems.add(position);
                    }
                    notifyItemChanged(position);
                    if (rvListenerPdf != null) {
                        rvListenerPdf.onItemSelectionChanged();
                        rvListenerPdf.onItemSelectionChangedCount(selectedItems.size());
                    }
                    notifyDataSetChanged();  // To update all items for checkbox visibility


                }
            });

        }
    }

    public ArrayList<String> getSelectedFilePaths() {
        ArrayList<String> selectedFilePaths = new ArrayList<>();
        for (Integer index : selectedItems) {
            selectedFilePaths.add(pdfArrayList.get(index).getFile().getAbsolutePath());
        }
        return selectedFilePaths;
    }


    public ArrayList<Integer> getSelectedItems() {
        return selectedItems;
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }
}
