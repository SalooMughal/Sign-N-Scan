package com.pixelz360.docsign.imagetopdf.creator.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {
    private final int[] layouts;

    public OnboardingAdapter(int[] layouts) {
        this.layouts = layouts;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layouts[viewType], parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        // Handle screen-specific setup (if needed)
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
