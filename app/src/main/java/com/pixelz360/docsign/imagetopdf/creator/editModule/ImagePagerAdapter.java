package com.pixelz360.docsign.imagetopdf.creator.editModule;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.pixelz360.docsign.imagetopdf.creator.R;
import com.pixelz360.docsign.imagetopdf.creator.RoomDb.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {

    private EditMoudleActivity context;
    private List<Uri> imageUris;
    private List<Bitmap> filteredBitmaps;
    private List<Bitmap> originalBitmaps;
    private List<Uri> filteredImageUris;
    ImageView deleteBtn;

    public ImagePagerAdapter(EditMoudleActivity context, List<Uri> imageUris, ImageView deleteBtn) {
        this.deleteBtn = deleteBtn;
        this.context = context;
        this.imageUris = imageUris;
        this.filteredBitmaps = new ArrayList<>(Collections.nCopies(imageUris.size(), null));
        this.originalBitmaps = new ArrayList<>(Collections.nCopies(imageUris.size(), null));
        this.filteredImageUris = new ArrayList<>(Collections.nCopies(imageUris.size(), null));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= filteredBitmaps.size() || position >= imageUris.size()) {
            Log.e("ImagePagerAdapter", "Invalid position: " + position);
            return; // Ensure we don't access out-of-bounds elements
        }

        Bitmap bitmap = filteredBitmaps.get(position);
        if (bitmap == null) {
            bitmap = getBitmapFromUri(imageUris.get(position));
            originalBitmaps.set(position, bitmap);
            filteredBitmaps.set(position, bitmap);
        }
        holder.imageView.setImageBitmap(bitmap);

        deleteBtn.setOnClickListener(v -> {
            if (isFirstTime()) {
                showDeleteDialog(position);
            } else {
                deleteImage(position);
            }
        });
    }

    // Function to delete image
    private void deleteImage(int position) {
        if (position < 0 || position >= filteredBitmaps.size()) {
            Log.e("ImagePagerAdapter", "Invalid position: " + position + " while deleting image.");
            return; // Prevent deletion at invalid positions
        }

        filteredBitmaps.remove(position);
        imageUris.remove(position);
        originalBitmaps.remove(position);
        filteredImageUris.remove(position);
        notifyDataSetChanged();

        Toast.makeText(context, "Image deleted successfully.", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteDialog(int position) {
        Dialog dialogRename = new Dialog(context, R.style.renameDialogStyle);
        dialogRename.setContentView(R.layout.delete_dailog);

        Window window = dialogRename.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int desiredWidth = screenWidth - 2 * FileUtils.dpToPx(context, 30);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = desiredWidth;
            window.setAttributes(params);
        }

        TextView cancelBtn = dialogRename.findViewById(R.id.cancelBtn);
        TextView deleteBtn = dialogRename.findViewById(R.id.deleteBtn);
        TextView subTitleTextView = dialogRename.findViewById(R.id.subTitleTextView);
        TextView title = dialogRename.findViewById(R.id.title);

        title.setText("Delete Image");
        subTitleTextView.setText("Are you sure you want to delete this image?");

        cancelBtn.setOnClickListener(v -> dialogRename.dismiss());

        deleteBtn.setOnClickListener(v -> {
            deleteImage(position);
            dialogRename.dismiss();
        });

        dialogRename.show();
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = context.getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
        }
        return !ranBefore;
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public Bitmap getBitmap(int position) {
        if (position >= 0 && position < filteredBitmaps.size()) {
            return filteredBitmaps.get(position);
        }
        Log.e("ImagePagerAdapter", "Invalid position: " + position + " in getBitmap");
        return null;
    }

    public void setBitmap(int position, Bitmap bitmap) {
        if (position >= 0 && position < filteredBitmaps.size()) {
            filteredBitmaps.set(position, bitmap);
            Uri filteredUri = saveBitmapToCache(bitmap);
            filteredImageUris.set(position, filteredUri);
            notifyItemChanged(position);
        } else {
            Log.e("ImagePagerAdapter", "Invalid position: " + position + " in setBitmap");
        }
    }

    public void setOriginalBitmap(int position, Bitmap bitmap) {
        if (position >= 0 && position < originalBitmaps.size()) {
            originalBitmaps.set(position, bitmap);
            filteredBitmaps.set(position, bitmap);
            filteredImageUris.set(position, null);
            notifyItemChanged(position);
        } else {
            Log.e("ImagePagerAdapter", "Invalid position: " + position + " in setOriginalBitmap");
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("ImagePagerAdapter", "Error getting bitmap from URI: " + uri, e);
            return null;
        }
    }

    public Uri getUri(int position) {
        if (position >= 0 && position < filteredImageUris.size()) {
            return filteredImageUris.get(position) != null ? filteredImageUris.get(position) : imageUris.get(position);
        }
        Log.e("ImagePagerAdapter", "Invalid position: " + position + " in getUri");
        return null;
    }

    public Bitmap getOriginalBitmap(int position) {
        if (position >= 0 && position < originalBitmaps.size()) {
            return originalBitmaps.get(position);
        }
        Log.e("ImagePagerAdapter", "Invalid position: " + position + " in getOriginalBitmap");
        return null;
    }

    public Uri getUriFromBitmap(Bitmap bitmap) {
        return saveBitmapToCache(bitmap);
    }

    private Uri saveBitmapToCache(Bitmap bitmap) {
        File file = new File(context.getCacheDir(), "filtered_image_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } catch (Exception e) {
            Log.e("ImagePagerAdapter", "Error saving bitmap to cache", e);
            return null;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
