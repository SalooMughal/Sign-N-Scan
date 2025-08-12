package com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters;

import static com.pixelz360.docsign.imagetopdf.creator.RoomDb.FileUtils.generatePdfThumbnail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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

import com.google.android.material.imageview.ShapeableImageView;
import com.pixelz360.docsign.imagetopdf.creator.BuildConfig;
import com.pixelz360.docsign.imagetopdf.creator.MainApplication;
import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.PdfFile;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.RvListenerPdfRoom;
import com.pixelz360.docsign.imagetopdf.creator.databinding.PdfItemBinding;
import com.pixelz360.docsign.imagetopdf.creator.databinding.PdfItemGridBinding;
import com.pixelz360.docsign.imagetopdf.creator.editModule.AllEditImagesAdapter;
import com.pixelz360.docsign.imagetopdf.creator.viewmodel.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class PdfAdapterRoom extends RecyclerView.Adapter<PdfAdapterRoom.ViewHolder> {
    public static List<PdfFile> pdfFileList;
    private static final ArrayList<Integer> selectedItems = new ArrayList<>();
    private final RvListenerPdfRoom rvListenerPdf;
    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final HashMap<String, ThumbnailResult> thumbnailCache = new HashMap<>(); // Cache for thumbnails
    PdfItemBinding binding;
    PdfItemGridBinding binding_grid;
    boolean isCheckedListOrGrid;
    public PdfAdapterRoom(List<PdfFile> pdfFileList, Context context, boolean isCheckedListOrGrid, RvListenerPdfRoom rvListenerPdf) {
        this.pdfFileList = pdfFileList;
        this.rvListenerPdf = rvListenerPdf;
        this.context = context;
        this.isCheckedListOrGrid = isCheckedListOrGrid;
        this.executorService = Executors.newFixedThreadPool(4);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isCheckedListOrGrid){


            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;

            int margin = context.getResources().getDimensionPixelSize(R.dimen.grid_item_main_margin); // margin between items
            int numberOfColumns = 3;

            // Calculate the item width based on the screen width, the number of columns, and the margins
            int itemWidth = (screenWidth - (margin * (numberOfColumns + 1))) / numberOfColumns;



            binding_grid = PdfItemGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            ViewGroup.LayoutParams params = binding_grid.getRoot().getLayoutParams();
            params.width = itemWidth; // Set the width of the item dynamically
            binding_grid.getRoot().setLayoutParams(params);
            Log.d("checkview","GridLayoutManager adapter side");
            return new ViewHolder(binding_grid.getRoot());



        }else {

            binding = PdfItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            Log.d("checkview","LinearLayout adapter side");
            return new ViewHolder(binding.getRoot());
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PdfFile pdfFile = pdfFileList.get(position);
        File file = new File(pdfFile.filePath);

        // Set PDF name
        String previousName = file.getName();
        holder.pdfName.setText(previousName);
        holder.fileTag.setText(pdfFile.fileTag);




        // Create a GradientDrawable
        GradientDrawable shape = new GradientDrawable();

// Set the shape type (rectangle)
        shape.setShape(GradientDrawable.RECTANGLE);

// Set the corner radius (in pixels)
        shape.setCornerRadius(6f); // Example: 16 pixels

// Set the solid color

        float strokePt = 0.6f;
        float strokePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PT,
                strokePt,
                context.getResources().getDisplayMetrics()
        );

        shape.setStroke((int) strokePx, Color.parseColor(pdfFile.fileTagBgColor));

//        shape.setColor(Color.parseColor(pdfFile.fileTagBgColor)); // Replace with your desired color
        holder.fileTag.setBackground(shape);
        holder.fileTag.setTextColor(Color.parseColor(pdfFile.fileTagBgColor));

        Log.d("checkkjer",pdfFile.fileTagBgColor);

        Log.d("checkkjerpath",pdfFile.filePath);

        // Set modified date
        holder.date.setText(MainApplication.formatTimestamp(file.lastModified()));

        // Load file size
        loadFileSize(pdfFile, holder);

        String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));

        Log.d("checkimagepath"+"extension  ",extension);

        if (extension.equals(".jpg")){
            Bitmap bitmap = BitmapFactory.decodeFile(pdfFile.filePath);
            holder.thumbnailIv.setImageBitmap(bitmap);

            holder.page.setText("JPG File");
            holder.page.setTextColor(context.getColor(R.color.red));

        }else if (extension.equals(".pdf")){

            // Load thumbnail
            loadThumbnail(pdfFile, holder);
        }


        // Favorite icon setup
        holder.favoriteIcon.setImageResource(pdfFile.isFavorite ? R.drawable.favrarte_selected_icon : R.drawable.favrarte_un_selected_icon);
        holder.favoriteIcon.setOnClickListener(v -> {
            pdfFile.isFavorite = !pdfFile.isFavorite;
            rvListenerPdf.onFavoriteClick(pdfFile);
            notifyItemChanged(position);
        });

        // Handle item clicks and long clicks
        holder.itemView.setOnClickListener(v -> handleItemClick(holder, pdfFile, position));
        holder.itemView.setOnLongClickListener(v -> {
            toggleSelection(position);
            return true;
        });
        holder.checkBox.setChecked(selectedItems.contains(position));
//        holder.checkBox.setVisibility(selectedItems.isEmpty() ? View.GONE : View.VISIBLE);
        if (selectedItems.isEmpty()){
            holder.checkBox.setVisibility(View.GONE);
            holder.sharemoreLayout.setVisibility(View.VISIBLE);
        }else {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.sharemoreLayout.setVisibility(View.GONE);
        }

        // Share button
        holder.shareBtn.setOnClickListener(v -> {
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent target = ShareCompat.IntentBuilder.from((Activity) context).setStream(contentUri).getIntent();
            target.setData(contentUri);
            target.setType("application/pdf");
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (target.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(target);
            }
        });

        // More button
        holder.moreBtn.setOnClickListener(v -> rvListenerPdf.onPdfMoreClick(pdfFile, position));
    }

    private void loadFileSize(PdfFile modelPdf, ViewHolder holder) {
        File file = new File(modelPdf.filePath);
        double bytes = file.length();
        double kb = bytes / 1024;
        double mb = kb / 1024;
        String size = mb >= 1 ? String.format("%.2f MB", mb) : kb >= 1 ? String.format("%.2f KB", kb) : String.format("%.2f bytes", bytes);
        holder.size.setText(size);
    }

    private void loadThumbnail(PdfFile pdfFile, ViewHolder holder) {

        ThumbnailResult cachedThumbnail = thumbnailCache.get(pdfFile.filePath);
        if (cachedThumbnail != null && cachedThumbnail.thumbnail != null) {
            holder.thumbnailIv.setImageBitmap(cachedThumbnail.thumbnail);
            holder.page.setText(cachedThumbnail.pageCount + " " + context.getResources().getString(R.string.pages)+" "+ FileUtils.loadFileDate(pdfFile));
        } else {
            executorService.execute(() -> {
                ThumbnailResult result = generatePdfThumbnail(pdfFile.filePath);
                if (result.thumbnail != null) {
                    thumbnailCache.put(pdfFile.filePath, result);  // Cache the result
                }

                mainHandler.post(() -> {
                    holder.thumbnailIv.setImageBitmap(result.thumbnail != null ? result.thumbnail : getDefaultThumbnail());
                    holder.page.setText(result.pageCount + " " + context.getResources().getString(R.string.pages)+" "+ FileUtils.loadFileDate(pdfFile));
                });
            });
        }


    }



    private Bitmap getDefaultThumbnail() {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    private void handleItemClick(ViewHolder holder, PdfFile pdfFile, int position) {
        if (selectedItems.isEmpty()) {
            rvListenerPdf.onPdfClick(pdfFile, position);
        } else {
            toggleSelection(position);
        }
    }

    private void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(Integer.valueOf(position));
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
        rvListenerPdf.onItemSelectionChangedCount(selectedItems.size());
    }

    @Override
    public int getItemCount() {
        return pdfFileList.size();
    }

    public void filterList(ArrayList<PdfFile> filteredList) {
        this.pdfFileList = filteredList;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedFilePaths() {
        ArrayList<String> selectedFilePaths = new ArrayList<>();
        for (Integer index : selectedItems) {
            selectedFilePaths.add(pdfFileList.get(index).filePath);
        }
        return selectedFilePaths;
    }

    public List<PdfFile> getSelectedFiles() {
        List<PdfFile> selectedFiles = new ArrayList<>();
        for (Integer index : selectedItems) {
            selectedFiles.add(pdfFileList.get(index));
        }
        return selectedFiles;
    }

    public ArrayList<Integer> getSelectedItems() {
        return selectedItems;
    }

    public void selectAllItems(String selectAll) {
        selectedItems.clear();
        for (int i = 0; i < pdfFileList.size(); i++) {
            if (selectAll.equals("All items selected successfully")) {
                selectedItems.add(i);
            } else {
                if (i == 0) {
                    selectedItems.add(i);
                }
            }
        }
        notifyDataSetChanged();
        rvListenerPdf.onItemSelectionChangedCount(selectedItems.size());
    }

    public void unselectAllItems() {
        selectedItems.clear();
        notifyDataSetChanged();
        rvListenerPdf.onItemSelectionChangedCount(selectedItems.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pdfName, page, size, date,fileTag;
        public ImageView favoriteIcon;
        ShapeableImageView thumbnailIv;
        public ImageButton moreBtn, shareBtn;
        public CheckBox checkBox;
        public LinearLayout sharemoreLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            pdfName = itemView.findViewById(R.id.pdf_name);
            fileTag = itemView.findViewById(R.id.fileTag);
            thumbnailIv = itemView.findViewById(R.id.thumbnailIv);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
            sharemoreLayout = itemView.findViewById(R.id.sharemoreLayout);
            page = itemView.findViewById(R.id.pagesTv);
            size = itemView.findViewById(R.id.sizeTv);
            date = itemView.findViewById(R.id.dateTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }


}
