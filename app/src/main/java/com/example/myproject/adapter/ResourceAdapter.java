package com.example.myproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.model.Resource;
import com.example.myproject.model.SavedResource;
import com.example.myproject.utils.FirebaseHelper;

import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder> {

    public interface OnResourceClickListener {
        void onResourceClick(Resource resource);
    }

    private final Context context;
    private final List<Resource> resourceList;
    private final OnResourceClickListener listener;

    public ResourceAdapter(Context context, List<Resource> resourceList, OnResourceClickListener listener) {
        this.context = context;
        this.resourceList = resourceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resource, parent, false);
        return new ResourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        Resource resource = resourceList.get(position);

        holder.tvResourceName.setText(resource.getResourceName());
        holder.tvCategoryCondition.setText(resource.getCategory() + " | " + resource.getCondition());
        holder.tvLocation.setText("Location: " + (resource.getLocation() != null ? resource.getLocation() : "Campus"));
        holder.tvOwnerName.setText("Owner: " + (resource.getOwnerName() != null ? resource.getOwnerName() : "Student"));

        String availability = resource.getAvailability();
        if (availability == null) availability = "available";

        switch (availability.toLowerCase()) {
            case "requested":
                holder.tvAvailabilityStatus.setText("Requested");
                holder.tvAvailabilityStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_requested));
                break;
            case "borrowed":
                holder.tvAvailabilityStatus.setText("Borrowed");
                holder.tvAvailabilityStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_borrowed));
                break;
            case "unavailable":
                holder.tvAvailabilityStatus.setText("Unavailable");
                holder.tvAvailabilityStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_unavailable));
                break;
            case "available":
            default:
                holder.tvAvailabilityStatus.setText("Available");
                holder.tvAvailabilityStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_available));
                break;
        }

        if (resource.getImageUrl() != null && !resource.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(resource.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.ivResourceImage);
        } else {
            holder.ivResourceImage.setImageResource(R.drawable.ic_placeholder);
        }

        holder.btnCardCall.setOnClickListener(v -> {
            String phone = resource.getOwnerPhone();
            if (phone != null && !phone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Owner phone number not provided", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnCardSms.setOnClickListener(v -> {
            String phone = resource.getOwnerPhone();
            if (phone != null && !phone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + phone));
                intent.putExtra("sms_body", "Hi " + resource.getOwnerName() + ", I saw your item '" + resource.getResourceName() + "' on Campus Share!");
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Owner phone number not provided", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnCardSave.setOnClickListener(v -> {
            String uid = FirebaseHelper.getCurrentUserId();
            if (uid == null) {
                Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }
            String savedId = FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION).document().getId();
            SavedResource sr = new SavedResource(savedId, uid, resource.getResourceId());
            FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION)
                    .document(savedId).set(sr)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Saved to your list!", Toast.LENGTH_SHORT).show());
        });

        holder.btnCardDetails.setOnClickListener(v -> {
            if (listener != null) listener.onResourceClick(resource);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onResourceClick(resource);
        });
    }

    @Override
    public int getItemCount() {
        return resourceList.size();
    }

    static class ResourceViewHolder extends RecyclerView.ViewHolder {

        ImageView ivResourceImage;
        TextView tvResourceName, tvAvailabilityStatus, tvCategoryCondition, tvLocation, tvOwnerName;
        Button btnCardCall, btnCardSms, btnCardSave, btnCardDetails;

        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivResourceImage = itemView.findViewById(R.id.ivResourceImage);
            tvResourceName = itemView.findViewById(R.id.tvResourceName);
            tvAvailabilityStatus = itemView.findViewById(R.id.tvAvailabilityStatus);
            tvCategoryCondition = itemView.findViewById(R.id.tvCategoryCondition);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvOwnerName = itemView.findViewById(R.id.tvOwnerName);

            btnCardCall = itemView.findViewById(R.id.btnCardCall);
            btnCardSms = itemView.findViewById(R.id.btnCardSms);
            btnCardSave = itemView.findViewById(R.id.btnCardSave);
            btnCardDetails = itemView.findViewById(R.id.btnCardDetails);
        }
    }
}
