
package com.pixelz360.docsign.imagetopdf.creator.editModule;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.pixelz360.docsign.imagetopdf.creator.R;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllEditImagesAdapter extends RecyclerView.Adapter<AllEditImagesAdapter.ViewHolder> {
    private final List<Uri> uriList;
    private final Context context;
    private ImageView deleteBtn;
    private TextView selectAllBtn;
    private ImageView backButton;
    private ImageView clearButton;
    private TextView toolbarTitle;
    RecyclerView recyclerView;
    static ArrayList<Integer> selectedItems = new ArrayList<>();

    private static final int CAMERA_ITEM = 0;
    private static final int IMAGE_ITEM = 1;
    private ItemTouchHelper itemTouchHelper;

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public AllEditImagesAdapter(List<Uri> uriList, Context context, ImageView deleteBtn, TextView selectAllBtn, TextView toolbarTitle,ImageView backButton,ImageView clearButton,RecyclerView recyclerView) {
        this.uriList = uriList;
        this.context = context;
        this.deleteBtn = deleteBtn;
        this.selectAllBtn = selectAllBtn;
        this.toolbarTitle = toolbarTitle;
        this.backButton = backButton;
        this.clearButton = clearButton;
        this.recyclerView = recyclerView;
    }


    private boolean allItemsSelected = false; // To track the selection state
    private boolean isFileValieOrNo = false;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Calculate the screen width and item width dynamically
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        int margin = context.getResources().getDimensionPixelSize(R.dimen.grid_item_margin); // margin between items
        int numberOfColumns = 3;

        // Calculate the item width based on the screen width, the number of columns, and the margins
        int itemWidth = (screenWidth - (margin * (numberOfColumns + 1))) / numberOfColumns;

//        // Inflate the layout and set item width
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_edit_images_item, parent, false);
//        ViewGroup.LayoutParams params = view.getLayoutParams();
//        params.width = itemWidth; // Set the width of the item dynamically
//        view.setLayoutParams(params);
//
//        return new ViewHolder(view);



        View view;
        if (viewType == CAMERA_ITEM) {
            // Inflate the camera icon layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_icon_item, parent, false);

            // Inflate the layout and set item width
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = itemWidth; // Set the width of the item dynamically
            view.setLayoutParams(params);


        } else {
            // Inflate the image item layout
            // Inflate the layout and set item width
             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_edit_images_item, parent, false);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = itemWidth; // Set the width of the item dynamically
            view.setLayoutParams(params);
        }
        return new ViewHolder(view);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (getItemViewType(position) == CAMERA_ITEM) {
            // Set camera icon and click listener
//            holder.thumbnailIv.setImageResource(R.drawable.ic_camera); // Set camera icon
            holder.itemView.setOnClickListener(v -> {
                if (cameraClickListener != null) {
                    cameraClickListener.onCameraClick();
                }
            });
        } else {
            // Handle image loading
            Uri uri = uriList.get(position- 1);


            try {
                Bitmap bitmap = getBitmapFromUri(uri);
                if (bitmap != null) {
                    holder.thumbnailIv.setImageBitmap(bitmap);
                } else {
                    Log.e("AllEditImagesAdapter", "Failed to load image from URI: " + uri.toString());
                }
            } catch (IOException e) {
                Log.e("AllEditImagesAdapter", "Error loading image: " + e.getMessage(), e);
            }



            // Set touch listener for drag
            holder.thumbnailIv.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && itemTouchHelper != null) {
                    itemTouchHelper.startDrag(holder);
                }
                return false;
            });

            holder.itemView.setOnClickListener(v -> {
                if (selectedItems.isEmpty()) {
                    // No items are selected, handle normal click
                } else {
                    // Items are selected, handle selection
                    toggleSelection(position);
                }



            });




            if (selectedItems.isEmpty()) {
                holder.checkBox.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
                clearButton.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
                toolbarTitle.setText(context.getResources() .getString(R.string.docsign));
            } else {
                holder.checkBox.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
                clearButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.GONE);
                toolbarTitle.setText(String.valueOf(selectedItems.size())+" Selected");
            }
            //  this below line use if user click button then selecte all checkbox
            holder.checkBox.setChecked(selectedItems.contains(position));

            holder.checkBox.setOnClickListener(v -> {
                toggleSelection(position);
            });



            // Check if the item is selected
            if (selectedItems.contains(position)) {
                // Selected state (highlighted border)
//                holder.thumbnailIv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
//                holder.thumbnailIv.setStrokeWidth(5);
//                holder.thumbnailIv.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));

                holder.thumbnailIv.setBackground(context.getResources().getDrawable(R.drawable.all_images_selected_bg));
            } else {
                // Unselected state (default border)
//                holder.thumbnailIv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey_10)));
//                holder.thumbnailIv.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_5));
//                holder.thumbnailIv.setStrokeWidth(0.5F);
                holder.thumbnailIv.setBackground(context.getResources().getDrawable(R.drawable.all_images_un_selected_bg));
            }

            if (selectedItems.contains(position)) {
                // Selected state (highlighted border)
                holder.thumbnailIv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                holder.thumbnailIv.setStrokeWidth(5);
                holder.thumbnailIv.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));

            } else {
                // Unselected state (default border)
                holder.thumbnailIv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey_10)));
                holder.thumbnailIv.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_5));
                holder.thumbnailIv.setStrokeWidth(0.5F);

            }




            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteSelectedItems();
                }
            });

        }





//        Uri uri = uriList.get(position);
//
//
//        try {
//            Bitmap bitmap = getBitmapFromUri(uri);
//            if (bitmap != null) {
//                holder.thumbnailIv.setImageBitmap(bitmap);
//            } else {
//                Log.e("AllEditImagesAdapter", "Failed to load image from URI: " + uri.toString());
//            }
//        } catch (IOException e) {
//            Log.e("AllEditImagesAdapter", "Error loading image: " + e.getMessage(), e);
//        }
//
//        holder.itemView.setOnClickListener(v -> {
//            if (selectedItems.isEmpty()) {
//                // No items are selected, handle normal click
//            } else {
//                // Items are selected, handle selection
//                toggleSelection(position);
//            }
//
//
//
//        });
//
//        final GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public void onLongPress(MotionEvent e) {
//                if (itemTouchHelper != null) {
//                    itemTouchHelper.startDrag(holder);
//
//                }
//                Log.d("checkselected11", "onLongPress");
//                toggleSelection(position);
//            }
//        });
//
//
//        if (selectedItems.isEmpty()) {
//            holder.checkBox.setVisibility(View.GONE);
//            deleteBtn.setVisibility(View.GONE);
//            clearButton.setVisibility(View.GONE);
//            backButton.setVisibility(View.VISIBLE);
//            toolbarTitle.setText(context.getResources() .getString(R.string.docsign));
//        } else {
//            holder.checkBox.setVisibility(View.VISIBLE);
//            deleteBtn.setVisibility(View.VISIBLE);
//            clearButton.setVisibility(View.VISIBLE);
//            backButton.setVisibility(View.GONE);
//            toolbarTitle.setText(String.valueOf(selectedItems.size())+" Selected");
//        }
//        //  this below line use if user click button then selecte all checkbox
//        holder.checkBox.setChecked(selectedItems.contains(position));
//
//        holder.checkBox.setOnClickListener(v -> {
//            toggleSelection(position);
//        });
//
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteSelectedItems();
//            }
//        });

    }



//    @Override
//    public int getItemViewType(int position) {
//        return (position == 0) ? 0 : 1; // 0 for camera icon, 1 for images
//    }
@Override
public int getItemViewType(int position) {
    return (position == 0) ? CAMERA_ITEM : IMAGE_ITEM;
}

    // Function to load Bitmap from URI using ContentResolver
// Function to load Bitmap from URI using ContentResolver
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        // Check for URI access permission using the correct approach for Android 13+
        if (checkUriPermission(uri)) {
            // Use ContentResolver to open the file descriptor and load the image
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                return image;
            }
        } else {
            Log.e("AllEditImagesAdapter", "No permission to access the URI: " + uri.toString());
        }
        return null;
    }

    // Helper function to check if your app has permission to access the URI



    // Helper method to check if the URI is from a source that requires persistable permissions
    private boolean isPersistableUri(Uri uri) {
        String authority = uri.getAuthority();
        return "com.android.externalstorage.documents".equals(authority) ||
                "com.android.providers.downloads.documents".equals(authority) ||
                "com.android.providers.media.documents".equals(authority);
    }



    // Helper function to check if your app has permission to access the URI
    private boolean checkUriPermission(Uri uri) {
        try {
            context.getContentResolver().openInputStream(uri).close();
            return true;
        } catch (SecurityException | IOException e) {
            Log.e("PermissionCheck", "Permission denied or error accessing URI: " + e.getMessage());
            return false;
        }
    }
    private void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(Integer.valueOf(position));
            Log.d("checkselected11", "unselect item " + position);
        } else {
            selectedItems.add(position);
            Log.d("checkselected11", "select item " + position);
        }
//        notifyItemChanged(position);
//        notifyDataSetChanged();
        notifyItemChanged(position, "selectionChanged");


    }

    private void deleteSelectedItems() {
        List<Uri> itemsToRemove = new ArrayList<>();
        for (Integer position : selectedItems) {
            itemsToRemove.add(uriList.get(position));
        }
        uriList.removeAll(itemsToRemove);
        selectedItems.clear();
        notifyDataSetChanged();
        Toast.makeText(context, "Selected items deleted successfully.", Toast.LENGTH_SHORT).show();
    }

    public void selectAllItems(String selectAll) {
        selectedItems.clear();

        if (!uriList.isEmpty()) {
            selectedItems.add(1); // Select only the first item
            Log.d("checksize", "only show one");
        }

//        for (int i = 0; i < uriList.size(); i++) {
//            if (selectAll.equals("Cancel")){
//                selectedItems.add(i);
//                Log.d("checksize", "selectAll.equals(\"Cancel\")");
//            }else {
//                if (i==0){
//                    selectedItems.add(i);
//                    Log.d("checksize", String.valueOf(i));
//
//                }
//            }
//
//            Log.d("checksize", "selectAllItems loop");
//        }
        notifyDataSetChanged();
        Log.d("checksize", "selectAllItems");
    }





    public void unselectAllItems() {
        selectedItems.clear();
        notifyDataSetChanged();
//        if (rvListenerPdf != null) {
//            rvListenerPdf.onItemSelectionChanged();
//            rvListenerPdf.onItemSelectionChangedCount(selectedItems.size());
//        }
    }

    @Override
    public int getItemCount() {
        return uriList.size()+1;
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition > 0 && toPosition > 0) { // Ensure the camera icon is not moved
            Collections.swap(uriList, fromPosition - 1, toPosition - 1);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public ArrayList<Integer> getSelectedItems() {
        return selectedItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView thumbnailIv;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnailIv = itemView.findViewById(R.id.thumbnailIv);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    // Interface to handle camera icon click
    public interface CameraClickListener {
        void onCameraClick();
    }

    private CameraClickListener cameraClickListener;

    public void setCameraClickListener(CameraClickListener listener) {
        this.cameraClickListener = listener;
    }
}











