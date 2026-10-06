package com.example.myproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.model.Resource;

import java.util.List;

public class SavedResourceAdapter extends RecyclerView.Adapter<SavedResourceAdapter.SavedViewHolder> {

    public interface OnSavedClickListener {
        void onItemClick(Resource resource);
        void onRemoveClick(Resource resource);
    }

    private final Context context;
    private final List<Resource> savedList;
    private final OnSavedClickListener listener;

    public SavedResourceAdapter(Context context, List<Resource> savedList, OnSavedClickListener listener) {
        this.context = context;
        this.savedList = savedList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_resource, parent, false);
        return new SavedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedViewHolder holder, int position) {
        Resource resource = savedList.get(position);

        holder.tvSavedName.setText(resource.getResourceName());
        holder.tvSavedCategory.setText(resource.getCategory());
        holder.tvSavedStatus.setText(resource.getAvailability() != null ? resource.getAvailability().toUpperCase() : "AVAILABLE");

        if (resource.getImageUrl() != null && !resource.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(resource.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.ivSavedImage);
        } else {
            holder.ivSavedImage.setImageResource(R.drawable.ic_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(resource);
        });

        holder.btnRemoveSaved.setOnClickListener(v -> {
            if (listener != null) listener.onRemoveClick(resource);
        });
    }

    @Override
    public int getItemCount() {
        return savedList.size();
    }

    static class SavedViewHolder extends RecyclerView.ViewHolder {

        ImageView ivSavedImage;
        TextView tvSavedName, tvSavedCategory, tvSavedStatus;
        ImageButton btnRemoveSaved;

        public SavedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSavedImage = itemView.findViewById(R.id.ivSavedImage);
            tvSavedName = itemView.findViewById(R.id.tvSavedName);
            tvSavedCategory = itemView.findViewById(R.id.tvSavedCategory);
            tvSavedStatus = itemView.findViewById(R.id.tvSavedStatus);
            btnRemoveSaved = itemView.findViewById(R.id.btnRemoveSaved);
        }
    }
}
