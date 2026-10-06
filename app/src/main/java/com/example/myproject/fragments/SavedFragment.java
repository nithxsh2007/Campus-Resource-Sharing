package com.example.myproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myproject.R;
import com.example.myproject.activities.ResourceDetailsActivity;
import com.example.myproject.adapter.SavedResourceAdapter;
import com.example.myproject.model.Resource;
import com.example.myproject.utils.FirebaseHelper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshSaved;
    private RecyclerView rvSavedResources;
    private TextView tvSavedEmpty;

    private SavedResourceAdapter adapter;
    private final List<Resource> savedResourceList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshSaved = view.findViewById(R.id.swipeRefreshSaved);
        rvSavedResources = view.findViewById(R.id.rvSavedResources);
        tvSavedEmpty = view.findViewById(R.id.tvSavedEmpty);

        rvSavedResources.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SavedResourceAdapter(getContext(), savedResourceList, new SavedResourceAdapter.OnSavedClickListener() {
            @Override
            public void onItemClick(Resource resource) {
                Intent intent = new Intent(getContext(), ResourceDetailsActivity.class);
                intent.putExtra("resourceId", resource.getResourceId());
                startActivity(intent);
            }

            @Override
            public void onRemoveClick(Resource resource) {
                removeSavedResource(resource);
            }
        });
        rvSavedResources.setAdapter(adapter);

        swipeRefreshSaved.setOnRefreshListener(this::loadSavedResources);
        loadSavedResources();
    }

    private void loadSavedResources() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        swipeRefreshSaved.setRefreshing(true);
        FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION)
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    savedResourceList.clear();
                    if (querySnapshot.isEmpty()) {
                        swipeRefreshSaved.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                        tvSavedEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    int total = querySnapshot.size();
                    final int[] loaded = {0};

                    for (DocumentSnapshot doc : querySnapshot) {
                        String resId = doc.getString("resourceId");
                        if (resId != null) {
                            FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                                    .document(resId).get()
                                    .addOnSuccessListener(resDoc -> {
                                        if (resDoc.exists()) {
                                            Resource r = resDoc.toObject(Resource.class);
                                            if (r != null) savedResourceList.add(r);
                                        }
                                        loaded[0]++;
                                        if (loaded[0] == total) {
                                            swipeRefreshSaved.setRefreshing(false);
                                            adapter.notifyDataSetChanged();
                                            tvSavedEmpty.setVisibility(savedResourceList.isEmpty() ? View.VISIBLE : View.GONE);
                                        }
                                    });
                        } else {
                            loaded[0]++;
                            if (loaded[0] == total) {
                                swipeRefreshSaved.setRefreshing(false);
                                adapter.notifyDataSetChanged();
                                tvSavedEmpty.setVisibility(savedResourceList.isEmpty() ? View.VISIBLE : View.GONE);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    swipeRefreshSaved.setRefreshing(false);
                    tvSavedEmpty.setVisibility(View.VISIBLE);
                });
    }

    private void removeSavedResource(Resource resource) {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION)
                .whereEqualTo("userId", uid)
                .whereEqualTo("resourceId", resource.getResourceId())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        doc.getReference().delete();
                    }
                    savedResourceList.remove(resource);
                    adapter.notifyDataSetChanged();
                    tvSavedEmpty.setVisibility(savedResourceList.isEmpty() ? View.VISIBLE : View.GONE);
                    Toast.makeText(getContext(), "Removed from saved resources", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedResources();
    }
}
