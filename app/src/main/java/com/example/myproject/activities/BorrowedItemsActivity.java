package com.example.myproject.activities;

import android.content.Intent;
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

public class BorrowedItemsActivity extends AppCompatActivity {

    private RecyclerView rvBorrowed;
    private TextView tvEmpty;

    private BorrowRequestAdapter adapter;
    private final List<BorrowRequest> borrowedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowed_items);

        rvBorrowed = findViewById(R.id.rvBorrowedItems);
        tvEmpty = findViewById(R.id.tvBorrowedItemsEmpty);

        rvBorrowed.setLayoutManager(new LinearLayoutManager(this));

        String uid = FirebaseHelper.getCurrentUserId();

        adapter = new BorrowRequestAdapter(this, borrowedList, uid, new BorrowRequestAdapter.OnRequestListener() {
            @Override
            public void onAcceptClick(BorrowRequest request) {}

            @Override
            public void onRejectClick(BorrowRequest request) {}

            @Override
            public void onMarkReturnedClick(BorrowRequest request) {
                markAsReturned(request);
            }

            @Override
            public void onRateOwnerClick(BorrowRequest request) {
                Intent intent = new Intent(BorrowedItemsActivity.this, RateOwnerActivity.class);
                intent.putExtra("ownerId", request.getOwnerId());
                intent.putExtra("resourceId", request.getResourceId());
                startActivity(intent);
            }
        });

        rvBorrowed.setAdapter(adapter);

        loadBorrowedItems();
    }

    private void loadBorrowedItems() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                .whereEqualTo("borrowerId", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    borrowedList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        BorrowRequest req = doc.toObject(BorrowRequest.class);
                        if (req != null && ("accepted".equalsIgnoreCase(req.getStatus()) || "returned".equalsIgnoreCase(req.getStatus()))) {
                            borrowedList.add(req);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(borrowedList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void markAsReturned(BorrowRequest request) {
        FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                .document(request.getRequestId())
                .update("status", "returned")
                .addOnSuccessListener(aVoid -> {
                    FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                            .document(request.getResourceId())
                            .update("availability", "available");

                    FirebaseHelper.sendNotification(
                            request.getOwnerId(),
                            "Item Returned",
                            request.getBorrowerName() + " has marked " + request.getResourceName() + " as returned.",
                            "returned"
                    );

                    Toast.makeText(this, "Item marked as returned!", Toast.LENGTH_SHORT).show();
                    loadBorrowedItems();
                });
    }
}
