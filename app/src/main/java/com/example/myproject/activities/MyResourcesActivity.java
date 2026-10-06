package com.example.myproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.adapter.ResourceAdapter;
import com.example.myproject.model.Resource;
import com.example.myproject.utils.FirebaseHelper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyResourcesActivity extends AppCompatActivity {

    private RecyclerView rvMyResources;
    private TextView tvEmpty;

    private ResourceAdapter adapter;
    private final List<Resource> myResourceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_resources);

        rvMyResources = findViewById(R.id.rvMyResources);
        tvEmpty = findViewById(R.id.tvMyResourcesEmpty);

        rvMyResources.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ResourceAdapter(this, myResourceList, resource -> {
            Intent intent = new Intent(MyResourcesActivity.this, ResourceDetailsActivity.class);
            intent.putExtra("resourceId", resource.getResourceId());
            startActivity(intent);
        });

        rvMyResources.setAdapter(adapter);

        loadMyResources();
    }

    private void loadMyResources() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                .whereEqualTo("ownerId", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    myResourceList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Resource r = doc.toObject(Resource.class);
                        if (r != null) {
                            myResourceList.add(r);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(myResourceList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyResources();
    }
}
