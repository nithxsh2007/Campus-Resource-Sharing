package com.example.myproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class BorrowingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEmpty;

    private BorrowRequestAdapter adapter;
    private final List<BorrowRequest> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowing_history);

        rvHistory = findViewById(R.id.rvBorrowingHistory);
        tvEmpty = findViewById(R.id.tvHistoryEmpty);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        String uid = FirebaseHelper.getCurrentUserId();

        adapter = new BorrowRequestAdapter(this, historyList, uid, new BorrowRequestAdapter.OnRequestListener() {
            @Override
            public void onAcceptClick(BorrowRequest request) {}

            @Override
            public void onRejectClick(BorrowRequest request) {}

            @Override
            public void onMarkReturnedClick(BorrowRequest request) {}

            @Override
            public void onRateOwnerClick(BorrowRequest request) {}
        });

        rvHistory.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                .whereEqualTo("borrowerId", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    historyList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        BorrowRequest req = doc.toObject(BorrowRequest.class);
                        if (req != null) {
                            historyList.add(req);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }
}
