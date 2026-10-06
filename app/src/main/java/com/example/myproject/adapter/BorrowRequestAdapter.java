package com.example.myproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.model.BorrowRequest;

import java.util.List;

public class BorrowRequestAdapter extends RecyclerView.Adapter<BorrowRequestAdapter.RequestViewHolder> {

    public interface OnRequestListener {
        void onAcceptClick(BorrowRequest request);
        void onRejectClick(BorrowRequest request);
        void onMarkReturnedClick(BorrowRequest request);
        void onRateOwnerClick(BorrowRequest request);
    }

    private final Context context;
    private final List<BorrowRequest> requestList;
    private final String currentUserId;
    private final OnRequestListener listener;

    public BorrowRequestAdapter(Context context, List<BorrowRequest> requestList, String currentUserId, OnRequestListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrow_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        BorrowRequest request = requestList.get(position);

        holder.tvReqResourceName.setText(request.getResourceName());
        holder.tvReqBorrower.setText("Borrower: " + (request.getBorrowerName() != null ? request.getBorrowerName() : "Student"));

        String dateInfo = "Requested: " + (request.getRequestDate() != null ? request.getRequestDate() : "N/A");
        if (request.getReturnDate() != null && !request.getReturnDate().isEmpty()) {
            dateInfo += " | Return: " + request.getReturnDate();
        }
        holder.tvReqDates.setText(dateInfo);

        if (request.getNote() != null && !request.getNote().isEmpty()) {
            holder.tvReqNote.setVisibility(View.VISIBLE);
            holder.tvReqNote.setText("Note: " + request.getNote());
        } else {
            holder.tvReqNote.setVisibility(View.GONE);
        }

        String status = request.getStatus();
        if (status == null) status = "pending";
        holder.tvReqStatus.setText(status.toUpperCase());

        boolean isOwner = currentUserId != null && currentUserId.equals(request.getOwnerId());
        boolean isBorrower = currentUserId != null && currentUserId.equals(request.getBorrowerId());

        // Default hide all action buttons
        holder.layoutOwnerActions.setVisibility(View.GONE);
        holder.btnMarkReturned.setVisibility(View.GONE);
        holder.btnRateOwner.setVisibility(View.GONE);

        if ("pending".equalsIgnoreCase(status) && isOwner) {
            holder.layoutOwnerActions.setVisibility(View.VISIBLE);
        } else if ("accepted".equalsIgnoreCase(status)) {
            if (isBorrower || isOwner) {
                holder.btnMarkReturned.setVisibility(View.VISIBLE);
            }
        } else if ("returned".equalsIgnoreCase(status) && isBorrower) {
            holder.btnRateOwner.setVisibility(View.VISIBLE);
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAcceptClick(request);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onRejectClick(request);
        });

        holder.btnMarkReturned.setOnClickListener(v -> {
            if (listener != null) listener.onMarkReturnedClick(request);
        });

        holder.btnRateOwner.setOnClickListener(v -> {
            if (listener != null) listener.onRateOwnerClick(request);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView tvReqResourceName, tvReqStatus, tvReqBorrower, tvReqDates, tvReqNote;
        LinearLayout layoutOwnerActions;
        Button btnAccept, btnReject, btnMarkReturned, btnRateOwner;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReqResourceName = itemView.findViewById(R.id.tvReqResourceName);
            tvReqStatus = itemView.findViewById(R.id.tvReqStatus);
            tvReqBorrower = itemView.findViewById(R.id.tvReqBorrower);
            tvReqDates = itemView.findViewById(R.id.tvReqDates);
            tvReqNote = itemView.findViewById(R.id.tvReqNote);
            layoutOwnerActions = itemView.findViewById(R.id.layoutOwnerActions);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnMarkReturned = itemView.findViewById(R.id.btnMarkReturned);
            btnRateOwner = itemView.findViewById(R.id.btnRateOwner);
        }
    }
}
