package com.example.myproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.activities.BorrowingHistoryActivity;
import com.example.myproject.activities.FaqActivity;
import com.example.myproject.activities.LoginActivity;
import com.example.myproject.activities.NotificationsActivity;
import com.example.myproject.activities.ProfileActivity;
import com.example.myproject.model.User;
import com.example.myproject.utils.FirebaseHelper;
import com.example.myproject.utils.SampleDataHelper;
import com.example.myproject.utils.ThemeUtils;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePicture;
    private TextView tvProfileName, tvProfileEmail, tvProfilePhone, tvRatingScore;
    private RatingBar rbUserRating;
    private TextView tvStatShared, tvStatBorrowed, tvStatReturned, tvStatSaved, tvStatPending;
    private Button btnProfileEdit, btnProfileHistory, btnProfileNotifications, btnProfileFaq, btnProfileLoadSampleData, btnProfileLogout;
    private MaterialSwitch switchDarkMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfilePhone = view.findViewById(R.id.tvProfilePhone);
        tvRatingScore = view.findViewById(R.id.tvRatingScore);
        rbUserRating = view.findViewById(R.id.rbUserRating);

        tvStatShared = view.findViewById(R.id.tvStatShared);
        tvStatBorrowed = view.findViewById(R.id.tvStatBorrowed);
        tvStatReturned = view.findViewById(R.id.tvStatReturned);
        tvStatSaved = view.findViewById(R.id.tvStatSaved);
        tvStatPending = view.findViewById(R.id.tvStatPending);

        btnProfileEdit = view.findViewById(R.id.btnProfileEdit);
        btnProfileHistory = view.findViewById(R.id.btnProfileHistory);
        btnProfileNotifications = view.findViewById(R.id.btnProfileNotifications);
        btnProfileFaq = view.findViewById(R.id.btnProfileFaq);
        btnProfileLoadSampleData = view.findViewById(R.id.btnProfileLoadSampleData);
        btnProfileLogout = view.findViewById(R.id.btnProfileLogout);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        switchDarkMode.setChecked(ThemeUtils.isDarkModeEnabled(requireContext()));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeUtils.setDarkMode(requireContext(), isChecked);
        });

        btnProfileEdit.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProfileActivity.class));
        });

        btnProfileHistory.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), BorrowingHistoryActivity.class));
        });

        btnProfileNotifications.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), NotificationsActivity.class));
        });

        btnProfileFaq.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FaqActivity.class));
        });

        btnProfileLoadSampleData.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Adding sample campus resources...", Toast.LENGTH_SHORT).show();
            SampleDataHelper.seedSampleResources(requireContext(), new SampleDataHelper.OnDataSeededListener() {
                @Override
                public void onSuccess() {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Sample resources loaded successfully!", Toast.LENGTH_SHORT).show();
                        loadStats();
                    }
                }

                @Override
                public void onFailure(String error) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        btnProfileLogout.setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadProfileData();
        loadStats();
    }

    private void loadProfileData() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        FirebaseHelper.getFirestore().collection(FirebaseHelper.USERS_COLLECTION)
                .document(uid).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            tvProfileName.setText(user.getFullName());
                            tvProfileEmail.setText(user.getEmail());
                            tvProfilePhone.setText(user.getPhone() != null ? user.getPhone() : "No phone number");

                            double rating = user.getRating();
                            rbUserRating.setRating((float) rating);
                            tvRatingScore.setText(String.format(" (%.1f)", rating));

                            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                                Glide.with(requireContext())
                                        .load(user.getProfileImageUrl())
                                        .placeholder(R.drawable.ic_person)
                                        .into(ivProfilePicture);
                            }
                        }
                    }
                });
    }

    private void loadStats() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        // Shared count
        FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                .whereEqualTo("ownerId", uid).get().addOnSuccessListener(qs -> {
                    tvStatShared.setText("Shared: " + qs.size());
                });

        // Borrowed count & Returned count
        FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                .whereEqualTo("borrowerId", uid).get().addOnSuccessListener(qs -> {
                    int borrowed = 0;
                    int returned = 0;
                    int pending = 0;
                    for (DocumentSnapshot doc : qs) {
                        String st = doc.getString("status");
                        if ("accepted".equalsIgnoreCase(st)) borrowed++;
                        else if ("returned".equalsIgnoreCase(st)) returned++;
                        else if ("pending".equalsIgnoreCase(st)) pending++;
                    }
                    tvStatBorrowed.setText("Borrowed: " + borrowed);
                    tvStatReturned.setText("Returned: " + returned);
                    tvStatPending.setText("Pending Reqs: " + pending);
                });

        // Saved count
        FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION)
                .whereEqualTo("userId", uid).get().addOnSuccessListener(qs -> {
                    tvStatSaved.setText("Saved: " + qs.size());
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
        loadStats();
    }
}
