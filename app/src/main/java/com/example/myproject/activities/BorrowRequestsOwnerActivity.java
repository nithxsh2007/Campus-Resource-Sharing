package com.example.myproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.adapter.BorrowRequestAdapter;
import com.example.myproject.model.BorrowRequest;
import com.example.myproject.utils.FirebaseHelper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BorrowRequestsOwnerActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private TextView tvEmpty;

    private BorrowRequestAdapter adapter;
    private final List<BorrowRequest> requestList = new ArrayList<>();
    private String resourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowed_items); // reuse list layout

        resourceId = getIntent().getStringExtra("resourceId");

        rvRequests = findViewById(R.id.rvBorrowedItems);
        tvEmpty = findViewById(R.id.tvBorrowedItemsEmpty);

        TextView tvTitle = findViewById(android.R.id.text1);
        if (tvTitle != null) tvTitle.setText("Borrow Requests for Item");

        rvRequests.setLayoutManager(new LinearLayoutManager(this));

        String uid = FirebaseHelper.getCurrentUserId();

        adapter = new BorrowRequestAdapter(this, requestList, uid, new BorrowRequestAdapter.OnRequestListener() {
            @Override
            public void onAcceptClick(BorrowRequest request) {
                acceptRequest(request);
            }

            @Override
            public void onRejectClick(BorrowRequest request) {
                rejectRequest(request);
            }

            @Override
            public void onMarkReturnedClick(BorrowRequest request) {}

            @Override
            public void onRateOwnerClick(BorrowRequest request) {}
        });

        rvRequests.setAdapter(adapter);

        loadOwnerRequests();
    }

    private void loadOwnerRequests() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        var query = FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                .whereEqualTo("ownerId", uid);

        if (resourceId != null) {
            query = query.whereEqualTo("resourceId", resourceId);
        }

        query.get().addOnSuccessListener(querySnapshot -> {
            requestList.clear();
            for (DocumentSnapshot doc : querySnapshot) {
                BorrowRequest req = doc.toObject(BorrowRequest.class);
                if (req != null) {
                    requestList.add(req);
                }
            }
            adapter.notifyDataSetChanged();
            tvEmpty.setVisibility(requestList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void acceptRequest(BorrowRequest request) {
        FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                .document(request.getRequestId())
                .update("status", "accepted")
                .addOnSuccessListener(aVoid -> {
                    // Update resource availability to borrowed
                    FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                            .document(request.getResourceId())
                            .update("availability", "borrowed");

                    // Notify borrower
                    FirebaseHelper.sendNotification(
                            request.getBorrowerId(),
                            "Borrow Request Accepted",
                            "Your request to borrow " + request.getResourceName() + " was accepted!",
                            "accepted"
                    );

                    Toast.makeText(this, "Request accepted", Toast.LENGTH_SHORT).show();
                    loadOwnerRequests();
                });
    }

    private void rejectRequest(BorrowRequest request) {
        FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                .document(request.getRequestId())
                .update("status", "rejected")
                .addOnSuccessListener(aVoid -> {
                    // Update resource availability back to available
                    FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                            .document(request.getResourceId())
                            .update("availability", "available");

                    // Notify borrower
                    FirebaseHelper.sendNotification(
                            request.getBorrowerId(),
                            "Borrow Request Rejected",
                            "Your request to borrow " + request.getResourceName() + " was rejected.",
                            "rejected"
                    );

                    Toast.makeText(this, "Request rejected", Toast.LENGTH_SHORT).show();
                    loadOwnerRequests();
                });
    }
}
