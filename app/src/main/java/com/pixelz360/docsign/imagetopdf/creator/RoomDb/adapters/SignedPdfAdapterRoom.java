package com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters;

import static com.pixelz360.docsign.imagetopdf.creator.RoomDb.FileUtils.generatePdfThumbnail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignedPdfAdapterRoom extends RecyclerView.Adapter<SignedPdfAdapterRoom.ViewHolder> {
    public static List<PdfFile> pdfFileList;
    private static final ArrayList<Integer> selectedItems = new ArrayList<>();
    private final RvListenerPdfRoom rvListenerPdf;
    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final HashMap<String, ThumbnailResult> thumbnailCache = new HashMap<>(); // Cache for thumbnails

    public SignedPdfAdapterRoom(List<PdfFile> pdfFileList, Context context, RvListenerPdfRoom rvListenerPdf) {
        this.pdfFileList = pdfFileList;
        this.rvListenerPdf = rvListenerPdf;
        this.context = context;
        this.executorService = Executors.newFixedThreadPool(4);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PdfFile pdfFile = pdfFileList.get(position);
        File file = new File(pdfFile.filePath);

        // Set PDF name
        holder.pdfName.setText(file.getName());

        // Set modified date
        holder.date.setText(MainApplication.formatTimestamp(file.lastModified()));

        // Load file size
        loadFileSize(pdfFile, holder);

        // Load thumbnail
        loadThumbnail(pdfFile, holder);

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
        holder.checkBox.setVisibility(selectedItems.isEmpty() ? View.GONE : View.VISIBLE);

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
            holder.page.setText(cachedThumbnail.pageCount + " " + context.getResources().getString(R.string.pages));
        } else {
            executorService.execute(() -> {
                ThumbnailResult result = generatePdfThumbnail(pdfFile.filePath);
                if (result.thumbnail != null) {
                    thumbnailCache.put(pdfFile.filePath, result);  // Cache the result
                }

                mainHandler.post(() -> {
                    holder.thumbnailIv.setImageBitmap(result.thumbnail != null ? result.thumbnail : getDefaultThumbnail());
                    holder.page.setText(result.pageCount + " " + context.getResources().getString(R.string.pages));
                });
            });
        }
        }



//    private ThumbnailResult generatePdfThumbnail(String filePath) {
//        try (ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(new File(filePath), ParcelFileDescriptor.MODE_READ_ONLY);
//             PdfRenderer renderer = new PdfRenderer(descriptor)) {
//
//            int pageCount = renderer.getPageCount();
//            if (pageCount > 0) {
//                PdfRenderer.Page page = renderer.openPage(0);
//                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
//                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//                page.close();
//                return new ThumbnailResult(bitmap, pageCount);  // Return both bitmap and page count
//            }
//        } catch (Exception e) {
//            Log.e("PdfAdapterRoom", "Error generating thumbnail", e);
//        }
//        return new ThumbnailResult(null, 0);
//    }



//    private void loadThumbnail(PdfFile pdfFile, ViewHolder holder) {
//        Bitmap cachedThumbnail = thumbnailCache.get(pdfFile.filePath);
//        if (cachedThumbnail != null) {
//            holder.thumbnailIv.setImageBitmap(cachedThumbnail);
//        } else {
//            executorService.execute(() -> {
//                Bitmap thumbnail = generatePdfThumbnail(pdfFile.filePath);
//                if (thumbnail != null) {
//                    thumbnailCache.put(pdfFile.filePath, thumbnail);
//                }
//                mainHandler.post(() -> holder.thumbnailIv.setImageBitmap(thumbnail != null ? thumbnail : getDefaultThumbnail()));
//            });
//        }
//    }

//    private Bitmap generatePdfThumbnail(String filePath) {
//        try (ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(new File(filePath), ParcelFileDescriptor.MODE_READ_ONLY);
//             PdfRenderer renderer = new PdfRenderer(descriptor)) {
//
//            if (renderer.getPageCount() > 0) {
//                PdfRenderer.Page page = renderer.openPage(0);
//                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
//                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//                page.close();
//                return bitmap;
//            }
//        } catch (Exception e) {
//            Log.e("SignedPdfAdapterRoom", "Error generating thumbnail", e);
//        }
//        return null;
//    }

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
        public TextView pdfName, page, size, date;
        public ImageView favoriteIcon;
        ShapeableImageView thumbnailIv;
        public ImageButton moreBtn, shareBtn;
        public CheckBox checkBox;
        public LinearLayout sharemoreLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            pdfName = itemView.findViewById(R.id.pdf_name);
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
