package com.example.myproject.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.model.NotificationModel;

import java.util.Calendar;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private final Context context;
    private final List<NotificationModel> notificationList;

    public NotificationAdapter(Context context, List<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        NotificationModel notif = notificationList.get(position);

        holder.tvNotifTitle.setText(notif.getTitle());
        holder.tvNotifMessage.setText(notif.getMessage());

        if (notif.getTimestamp() > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(notif.getTimestamp());
            String dateStr = DateFormat.format("dd MMM, hh:mm a", cal).toString();
            holder.tvNotifDate.setText(dateStr);
        } else {
            holder.tvNotifDate.setText("Recently");
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {

        TextView tvNotifTitle, tvNotifMessage, tvNotifDate;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotifTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvNotifMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvNotifDate = itemView.findViewById(R.id.tvNotifDate);
        }
    }
}
