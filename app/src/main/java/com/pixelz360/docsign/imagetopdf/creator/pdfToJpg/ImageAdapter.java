    package com.pixelz360.docsign.imagetopdf.creator.pdfToJpg;

    import android.content.Context;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.CheckBox;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.android.material.imageview.ShapeableImageView;
    import com.pixelz360.docsign.imagetopdf.creator.R;

    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        private final Context context;
        private final List<String> imagePaths;
        private static ArrayList<Integer> selectedItems = new ArrayList<>();
        private boolean allItemsSelected = false;

        public ImageAdapter(Context context, List<String> imagePaths) {
            this.context = context;
            this.imagePaths = imagePaths;

        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.pdf_jpg_images_item, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            Bitmap bitmap = BitmapFactory.decodeFile(new File(imagePaths.get(position)).getAbsolutePath());
            holder.thumbnailIv.setImageBitmap(bitmap);

            holder.itemView.setOnClickListener(v -> {
                if (selectedItems.isEmpty()) {
                    // Normal click action
                } else {
                    // Handle selection
                    toggleSelection(position);
                }
            });


            // Checkbox selection
            holder.checkBox.setChecked(selectedItems.contains(position));
            holder.checkBox.setOnClickListener(v -> toggleSelection(position));

            // Delete Button Action
    //        deleteBtn.setOnClickListener(v -> deleteSelectedItems());
        }

        private void toggleSelection(int position) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(Integer.valueOf(position));
            } else {
                selectedItems.add(position);
            }
            notifyItemChanged(position, "selectionChanged");
        }

        private void deleteSelectedItems() {
            List<String> itemsToRemove = new ArrayList<>();
            for (Integer position : selectedItems) {
                itemsToRemove.add(imagePaths.get(position));
            }
            imagePaths.removeAll(itemsToRemove);
            selectedItems.clear();
            notifyDataSetChanged();
            Toast.makeText(context, "Selected items deleted successfully.", Toast.LENGTH_SHORT).show();
        }

        public void selectAllItems() {
            selectedItems.clear();
            for (int i = 0; i < imagePaths.size(); i++) {
                selectedItems.add(i);
            }
            notifyDataSetChanged();
        }

        public void unselectAllItems() {
            selectedItems.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return imagePaths.size();
        }

        public static class ImageViewHolder extends RecyclerView.ViewHolder {
            ShapeableImageView thumbnailIv;
            CheckBox checkBox;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                thumbnailIv = itemView.findViewById(R.id.thumbnailIv);
                checkBox = itemView.findViewById(R.id.checkBox);
            }
        }
    }
