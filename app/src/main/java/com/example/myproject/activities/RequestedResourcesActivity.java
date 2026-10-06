package com.example.myproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.adapter.ResourceRequestAdapter;
import com.example.myproject.model.ResourceRequest;
import com.example.myproject.utils.FirebaseHelper;
import com.example.myproject.utils.SampleDataHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class RequestedResourcesActivity extends AppCompatActivity {

    private RecyclerView rvWanted;
    private LinearLayout layoutEmpty;
    private Button btnPostNewRequest, btnLoadSampleNeeded;

    private ResourceRequestAdapter adapter;
    private final List<ResourceRequest> requestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_resources);

        rvWanted = findViewById(R.id.rvWantedResources);
        layoutEmpty = findViewById(R.id.layoutWantedEmpty);
        btnPostNewRequest = findViewById(R.id.btnPostNewNeededRequest);
        btnLoadSampleNeeded = findViewById(R.id.btnLoadSampleNeededRequests);

        rvWanted.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ResourceRequestAdapter(this, requestList);
        rvWanted.setAdapter(adapter);

        btnPostNewRequest.setOnClickListener(v -> {
            startActivity(new Intent(RequestedResourcesActivity.this, PostResourceRequestActivity.class));
        });

        btnLoadSampleNeeded.setOnClickListener(v -> {
            btnLoadSampleNeeded.setEnabled(false);
            Toast.makeText(this, "Loading sample requested items...", Toast.LENGTH_SHORT).show();

            SampleDataHelper.seedSampleNeededRequests(this, new SampleDataHelper.OnDataSeededListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(RequestedResourcesActivity.this, "Sample requests loaded successfully!", Toast.LENGTH_SHORT).show();
                    loadRequestedResources();
                }

                @Override
                public void onFailure(String error) {
                    btnLoadSampleNeeded.setEnabled(true);
                    Toast.makeText(RequestedResourcesActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        loadRequestedResources();
    }

    private void loadRequestedResources() {
        FirebaseHelper.getFirestore().collection("neededResources")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    requestList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        ResourceRequest req = doc.toObject(ResourceRequest.class);
                        if (req != null) {
                            requestList.add(req);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    layoutEmpty.setVisibility(requestList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> layoutEmpty.setVisibility(View.VISIBLE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRequestedResources();
    }
}
