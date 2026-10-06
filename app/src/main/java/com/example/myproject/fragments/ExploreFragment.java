package com.example.myproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myproject.R;
import com.example.myproject.activities.RequestedResourcesActivity;
import com.example.myproject.activities.ResourceDetailsActivity;
import com.example.myproject.adapter.ResourceAdapter;
import com.example.myproject.model.Resource;
import com.example.myproject.utils.FirebaseHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private SearchView searchViewExplore;
    private Spinner spinnerCategory, spinnerCondition;
    private Button btnClearFilters, btnWantedBoard;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvExploreResources;
    private TextView tvExploreEmpty;

    private ResourceAdapter adapter;
    private final List<Resource> fullList = new ArrayList<>();
    private final List<Resource> filteredList = new ArrayList<>();

    private String selectedCategory = "All";
    private String selectedCondition = "All";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchViewExplore = view.findViewById(R.id.searchViewExplore);
        spinnerCategory = view.findViewById(R.id.spinnerCategoryFilter);
        spinnerCondition = view.findViewById(R.id.spinnerConditionFilter);
        btnClearFilters = view.findViewById(R.id.btnClearFilters);
        btnWantedBoard = view.findViewById(R.id.btnExploreWantedBoard);
        swipeRefresh = view.findViewById(R.id.swipeRefreshExplore);
        rvExploreResources = view.findViewById(R.id.rvExploreResources);
        tvExploreEmpty = view.findViewById(R.id.tvExploreEmpty);

        setupSpinners();

        rvExploreResources.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ResourceAdapter(getContext(), filteredList, resource -> {
            Intent intent = new Intent(getContext(), ResourceDetailsActivity.class);
            intent.putExtra("resourceId", resource.getResourceId());
            startActivity(intent);
        });
        rvExploreResources.setAdapter(adapter);

        btnWantedBoard.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), RequestedResourcesActivity.class));
        });

        searchViewExplore.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query != null ? query.trim() : "";
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText != null ? newText.trim() : "";
                applyFilters();
                return true;
            }
        });

        btnClearFilters.setOnClickListener(v -> {
            spinnerCategory.setSelection(0);
            spinnerCondition.setSelection(0);
            searchViewExplore.setQuery("", false);
            selectedCategory = "All";
            selectedCondition = "All";
            searchQuery = "";
            applyFilters();
        });

        swipeRefresh.setOnRefreshListener(this::loadResources);

        loadResources();
    }

    private void setupSpinners() {
        String[] categories = {"All Categories", "Books", "Electronics", "Calculators", "Lab Equipment", "Stationery", "Accessories", "Other"};
        String[] conditions = {"All Conditions", "New", "Good", "Used", "Needs Repair"};

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<String> condAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, conditions);
        condAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(condAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = position == 0 ? "All" : categories[position];
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCondition = position == 0 ? "All" : conditions[position];
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadResources() {
        swipeRefresh.setRefreshing(true);
        FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    fullList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Resource r = doc.toObject(Resource.class);
                        if (r != null) {
                            fullList.add(r);
                        }
                    }
                    applyFilters();
                    swipeRefresh.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    swipeRefresh.setRefreshing(false);
                    tvExploreEmpty.setVisibility(View.VISIBLE);
                });
    }

    private void applyFilters() {
        filteredList.clear();
        for (Resource r : fullList) {
            boolean matchesCat = "All".equalsIgnoreCase(selectedCategory) || selectedCategory.equalsIgnoreCase(r.getCategory());
            boolean matchesCond = "All".equalsIgnoreCase(selectedCondition) || selectedCondition.equalsIgnoreCase(r.getCondition());

            boolean matchesSearch = searchQuery.isEmpty() ||
                    (r.getResourceName() != null && r.getResourceName().toLowerCase().contains(searchQuery.toLowerCase())) ||
                    (r.getCategory() != null && r.getCategory().toLowerCase().contains(searchQuery.toLowerCase())) ||
                    (r.getLocation() != null && r.getLocation().toLowerCase().contains(searchQuery.toLowerCase()));

            if (matchesCat && matchesCond && matchesSearch) {
                filteredList.add(r);
            }
        }
        adapter.notifyDataSetChanged();
        tvExploreEmpty.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
