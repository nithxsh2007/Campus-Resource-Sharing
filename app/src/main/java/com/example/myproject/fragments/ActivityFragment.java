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

import com.example.myproject.R;
import com.example.myproject.activities.RateOwnerActivity;
import com.example.myproject.adapter.BorrowRequestAdapter;
import com.example.myproject.adapter.ResourceAdapter;
import com.example.myproject.model.BorrowRequest;
import com.example.myproject.model.Resource;
import com.example.myproject.utils.FirebaseHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {

    private TabLayout tabLayout;
    private RecyclerView rvActivityList;
    private TextView tvActivityEmpty;

    private ResourceAdapter resourceAdapter;
    private BorrowRequestAdapter requestAdapter;

    private final List<Resource> myResourceList = new ArrayList<>();
    private final List<BorrowRequest> requestList = new ArrayList<>();

    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabLayoutActivity);
        rvActivityList = view.findViewById(R.id.rvActivityList);
        tvActivityEmpty = view.findViewById(R.id.tvActivityEmpty);

        rvActivityList.setLayoutManager(new LinearLayoutManager(getContext()));

        String uid = FirebaseHelper.getCurrentUserId();

        resourceAdapter = new ResourceAdapter(getContext(), myResourceList, resource -> {
            // Clicked resource
        });

        requestAdapter = new BorrowRequestAdapter(getContext(), requestList, uid, new BorrowRequestAdapter.OnRequestListener() {
            @Override
            public void onAcceptClick(BorrowRequest request) {
                // Not used in this list directly
            }

            @Override
            public void onRejectClick(BorrowRequest request) {
                // Not used in this list directly
            }

            @Override
            public void onMarkReturnedClick(BorrowRequest request) {
                markAsReturned(request);
            }

            @Override
            public void onRateOwnerClick(BorrowRequest request) {
                Intent intent = new Intent(getContext(), RateOwnerActivity.class);
                intent.putExtra("ownerId", request.getOwnerId());
                intent.putExtra("resourceId", request.getResourceId());
                startActivity(intent);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                loadDataForCurrentTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadDataForCurrentTab();
    }

    private void loadDataForCurrentTab() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        if (currentTab == 0) {
            // My Listed Items
            rvActivityList.setAdapter(resourceAdapter);
            FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                    .whereEqualTo("ownerId", uid)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        myResourceList.clear();
                        for (DocumentSnapshot doc : querySnapshot) {
                            Resource r = doc.toObject(Resource.class);
                            if (r != null) myResourceList.add(r);
                        }
                        resourceAdapter.notifyDataSetChanged();
                        tvActivityEmpty.setVisibility(myResourceList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
        } else if (currentTab == 1) {
            // Borrowed Items
            rvActivityList.setAdapter(requestAdapter);
            FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                    .whereEqualTo("borrowerId", uid)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        requestList.clear();
                        for (DocumentSnapshot doc : querySnapshot) {
                            BorrowRequest req = doc.toObject(BorrowRequest.class);
                            if (req != null && ("accepted".equalsIgnoreCase(req.getStatus()) || "returned".equalsIgnoreCase(req.getStatus()))) {
                                requestList.add(req);
                            }
                        }
                        requestAdapter.notifyDataSetChanged();
                        tvActivityEmpty.setVisibility(requestList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
        } else {
            // Pending Requests
            rvActivityList.setAdapter(requestAdapter);
            FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                    .whereEqualTo("borrowerId", uid)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        requestList.clear();
                        for (DocumentSnapshot doc : querySnapshot) {
                            BorrowRequest req = doc.toObject(BorrowRequest.class);
                            if (req != null && "pending".equalsIgnoreCase(req.getStatus())) {
                                requestList.add(req);
                            }
                        }
                        requestAdapter.notifyDataSetChanged();
                        tvActivityEmpty.setVisibility(requestList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
        }
    }

    private void markAsReturned(BorrowRequest request) {
        // Update request status to 'returned'
        FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                .document(request.getRequestId())
                .update("status", "returned")
                .addOnSuccessListener(aVoid -> {
                    // Update resource availability to 'available'
                    FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                            .document(request.getResourceId())
                            .update("availability", "available");

                    // Notify owner
                    FirebaseHelper.sendNotification(
                            request.getOwnerId(),
                            "Item Returned",
                            request.getBorrowerName() + " has returned " + request.getResourceName(),
                            "returned"
                    );

                    Toast.makeText(getContext(), "Item marked as returned", Toast.LENGTH_SHORT).show();
                    loadDataForCurrentTab();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataForCurrentTab();
    }
}
