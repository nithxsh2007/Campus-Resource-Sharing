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
import com.example.myproject.adapter.SavedResourceAdapter;
import com.example.myproject.model.Resource;
import com.example.myproject.utils.FirebaseHelper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedResourcesActivity extends AppCompatActivity {

    private RecyclerView rvSaved;
    private TextView tvEmpty;

    private SavedResourceAdapter adapter;
    private final List<Resource> savedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_resources);

        rvSaved = findViewById(R.id.rvSavedResources);
        tvEmpty = findViewById(R.id.tvSavedEmpty);

        rvSaved.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SavedResourceAdapter(this, savedList, new SavedResourceAdapter.OnSavedClickListener() {
            @Override
            public void onItemClick(Resource resource) {
                Intent intent = new Intent(SavedResourcesActivity.this, ResourceDetailsActivity.class);
                intent.putExtra("resourceId", resource.getResourceId());
                startActivity(intent);
            }

            @Override
            public void onRemoveClick(Resource resource) {
                removeSaved(resource);
            }
        });

        rvSaved.setAdapter(adapter);

        loadSaved();
    }

    private void loadSaved() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION)
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    savedList.clear();
                    if (querySnapshot.isEmpty()) {
                        adapter.notifyDataSetChanged();
                        tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    int total = querySnapshot.size();
                    final int[] loaded = {0};

                    for (DocumentSnapshot doc : querySnapshot) {
                        String resId = doc.getString("resourceId");
                        if (resId != null) {
                            FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                                    .document(resId).get().addOnSuccessListener(resDoc -> {
                                        if (resDoc.exists()) {
                                            Resource r = resDoc.toObject(Resource.class);
                                            if (r != null) savedList.add(r);
                                        }
                                        loaded[0]++;
                                        if (loaded[0] == total) {
                                            adapter.notifyDataSetChanged();
                                            tvEmpty.setVisibility(savedList.isEmpty() ? View.VISIBLE : View.GONE);
                                        }
                                    });
                        }
                    }
                });
    }

    private void removeSaved(Resource resource) {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION)
                .whereEqualTo("userId", uid)
                .whereEqualTo("resourceId", resource.getResourceId())
                .get()
                .addOnSuccessListener(qs -> {
                    for (DocumentSnapshot doc : qs) doc.getReference().delete();
                    savedList.remove(resource);
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(savedList.isEmpty() ? View.VISIBLE : View.GONE);
                    Toast.makeText(this, "Removed from saved resources", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSaved();
    }
}
