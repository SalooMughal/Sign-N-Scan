package com.pixelz360.docsign.imagetopdf.creator.signiture_model.Activity;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixelz360.docsign.imagetopdf.creator.R;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private final Context context;
    private final List<Integer> colors;
    private int selectedPosition = -1;
    SignatureView signatureView;
    public ColorAdapter(Context context, List<Integer> colors, SignatureView signatureView) {
        this.context = context;
        this.colors = colors;
        this.signatureView = signatureView;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        int color = colors.get(position);

        // Create an oval drawable programmatically
        GradientDrawable ovalDrawable = new GradientDrawable();
        ovalDrawable.setShape(GradientDrawable.OVAL); // Set shape to oval
        ovalDrawable.setColor(color); // Set the fill color

        // Set selected border if the position is selected
        if (position == selectedPosition) {
            ovalDrawable.setStroke(6, context.getResources().getColor(R.color.yallow_color)); // Add a border for the selected item
        } else {
            ovalDrawable.setStroke(0, color); // No border for unselected items
        }

        // Apply the drawable as the background for the color view
        holder.colorView.setBackground(ovalDrawable);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = position;

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            // Set the selected color in the SignatureView
            signatureView.setStrokeColor(color);

            // Show a toast with the selected color
//            Toast.makeText(context, "Selected Color: " + color, Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return colors.size();
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        View colorView;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.color_view);
        }
    }
}

