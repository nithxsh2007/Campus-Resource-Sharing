package com.example.myproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.activities.AddResourceActivity;
import com.example.myproject.activities.MainActivity;
import com.example.myproject.activities.MyResourcesActivity;
import com.example.myproject.activities.RequestedResourcesActivity;
import com.example.myproject.activities.ResourceDetailsActivity;
import com.example.myproject.adapter.ResourceAdapter;
import com.example.myproject.model.Resource;
import com.example.myproject.utils.FirebaseHelper;
import com.example.myproject.utils.SampleDataHelper;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvHomeWelcome, tvHomeEmpty, tvHomeSeeAll;
    private Button btnQuickAdd, btnQuickBrowse, btnQuickNeeded, btnQuickMyResources, btnHomeSeedData;
    private ChipGroup chipGroupCategories;
    private RecyclerView rvHomeRecentResources;

    private ResourceAdapter adapter;
    private final List<Resource> resourceList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHomeWelcome = view.findViewById(R.id.tvHomeWelcome);
        tvHomeEmpty = view.findViewById(R.id.tvHomeEmpty);
        tvHomeSeeAll = view.findViewById(R.id.tvHomeSeeAll);

        btnQuickAdd = view.findViewById(R.id.btnQuickAdd);
        btnQuickBrowse = view.findViewById(R.id.btnQuickBrowse);
        btnQuickNeeded = view.findViewById(R.id.btnQuickNeeded);
        btnQuickMyResources = view.findViewById(R.id.btnQuickMyResources);
        btnHomeSeedData = view.findViewById(R.id.btnHomeSeedData);

        chipGroupCategories = view.findViewById(R.id.chipGroupHomeCategories);
        rvHomeRecentResources = view.findViewById(R.id.rvHomeRecentResources);

        rvHomeRecentResources.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ResourceAdapter(getContext(), resourceList, resource -> {
            Intent intent = new Intent(getContext(), ResourceDetailsActivity.class);
            intent.putExtra("resourceId", resource.getResourceId());
            startActivity(intent);
        });
        rvHomeRecentResources.setAdapter(adapter);

        setupClickListeners();
        loadUserData();
        loadRecentResources("All");
    }

    private void setupClickListeners() {
        btnQuickAdd.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddResourceActivity.class));
        });

        btnQuickBrowse.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectTab(R.id.nav_explore);
            }
        });

        btnQuickNeeded.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), RequestedResourcesActivity.class));
        });

        btnQuickMyResources.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyResourcesActivity.class));
        });

        tvHomeSeeAll.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectTab(R.id.nav_explore);
            }
        });

        btnHomeSeedData.setOnClickListener(v -> seedData());

        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipBooks) loadRecentResources("Books");
            else if (id == R.id.chipElectronics) loadRecentResources("Electronics");
            else if (id == R.id.chipCalculators) loadRecentResources("Calculators");
            else if (id == R.id.chipLab) loadRecentResources("Lab Equipment");
            else if (id == R.id.chipStationery) loadRecentResources("Stationery");
            else loadRecentResources("All");
        });
    }

    private void seedData() {
        btnHomeSeedData.setEnabled(false);
        Toast.makeText(getContext(), "Adding sample campus resources...", Toast.LENGTH_SHORT).show();

        SampleDataHelper.seedSampleResources(requireContext(), new SampleDataHelper.OnDataSeededListener() {
            @Override
            public void onSuccess() {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Sample resources loaded successfully!", Toast.LENGTH_SHORT).show();
                    loadRecentResources("All");
                }
            }

            @Override
            public void onFailure(String error) {
                if (getContext() != null) {
                    btnHomeSeedData.setEnabled(true);
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadUserData() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid != null) {
            FirebaseHelper.getFirestore().collection(FirebaseHelper.USERS_COLLECTION)
                    .document(uid).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("fullName");
                            if (name != null && !name.isEmpty()) {
                                tvHomeWelcome.setText("Welcome, " + name);
                            }
                        }
                    });
        }
    }

    private void loadRecentResources(String categoryFilter) {
        Query query = FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            resourceList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Resource resource = doc.toObject(Resource.class);
                if (resource != null) {
                    if ("All".equalsIgnoreCase(categoryFilter) || categoryFilter.equalsIgnoreCase(resource.getCategory())) {
                        resourceList.add(resource);
                    }
                }
            }
            adapter.notifyDataSetChanged();

            boolean isEmpty = resourceList.isEmpty();
            tvHomeEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            btnHomeSeedData.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }).addOnFailureListener(e -> {
            tvHomeEmpty.setVisibility(View.VISIBLE);
            btnHomeSeedData.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentResources("All");
    }
}
