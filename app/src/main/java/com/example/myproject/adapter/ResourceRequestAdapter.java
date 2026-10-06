package com.example.myproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.model.ResourceRequest;

import java.util.List;

public class ResourceRequestAdapter extends RecyclerView.Adapter<ResourceRequestAdapter.WantedViewHolder> {

    private final Context context;
    private final List<ResourceRequest> requestList;

    public ResourceRequestAdapter(Context context, List<ResourceRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public WantedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wanted_resource, parent, false);
        return new WantedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WantedViewHolder holder, int position) {
        ResourceRequest request = requestList.get(position);

        holder.tvTitle.setText(request.getTitle());
        holder.tvCategory.setText(request.getCategory());
        holder.tvDescription.setText(request.getDescription());
        holder.tvLocationDate.setText("Needed By: " + (request.getNeededByDate() != null ? request.getNeededByDate() : "ASAP")
                + " | Location: " + (request.getLocation() != null ? request.getLocation() : "Campus"));

        holder.tvRequester.setText("Requested by: " + (request.getRequesterName() != null ? request.getRequesterName() : "Student")
                + " (" + (request.getRequesterPhone() != null ? request.getRequesterPhone() : "No Contact") + ")");

        holder.btnOffer.setOnClickListener(v -> {
            String phone = request.getRequesterPhone();
            if (phone != null && !phone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Contact information not provided", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class WantedViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCategory, tvDescription, tvLocationDate, tvRequester;
        Button btnOffer;

        public WantedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvWantedTitle);
            tvCategory = itemView.findViewById(R.id.tvWantedCategory);
            tvDescription = itemView.findViewById(R.id.tvWantedDescription);
            tvLocationDate = itemView.findViewById(R.id.tvWantedLocationDate);
            tvRequester = itemView.findViewById(R.id.tvWantedRequester);
            btnOffer = itemView.findViewById(R.id.btnOfferItem);
        }
    }
}
