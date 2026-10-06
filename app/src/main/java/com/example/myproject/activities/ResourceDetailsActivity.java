package com.example.myproject.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.model.Resource;
import com.example.myproject.model.SavedResource;
import com.example.myproject.utils.FirebaseHelper;

public class ResourceDetailsActivity extends AppCompatActivity {

    private ImageView ivImage;
    private TextView tvName, tvAvailability, tvCategory, tvCondition, tvLocation, tvOwner, tvDescription;
    private LinearLayout layoutUserActions, layoutOwnerActions;
    private Button btnRequestBorrow, btnCallOwner, btnSmsOwner, btnSave, btnReport, btnOwnerViewRequests, btnEdit, btnDelete;

    private String resourceId;
    private Resource currentResource;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_details);

        resourceId = getIntent().getStringExtra("resourceId");

        ivImage = findViewById(R.id.ivDetailImage);
        tvName = findViewById(R.id.tvDetailName);
        tvAvailability = findViewById(R.id.tvDetailAvailability);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvCondition = findViewById(R.id.tvDetailCondition);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvOwner = findViewById(R.id.tvDetailOwner);
        tvDescription = findViewById(R.id.tvDetailDescription);

        layoutUserActions = findViewById(R.id.layoutUserActions);
        layoutOwnerActions = findViewById(R.id.layoutOwnerResourceActions);

        btnRequestBorrow = findViewById(R.id.btnRequestBorrow);
        btnCallOwner = findViewById(R.id.btnCallOwner);
        btnSmsOwner = findViewById(R.id.btnSmsOwner);
        btnSave = findViewById(R.id.btnSaveResource);
        btnReport = findViewById(R.id.btnReportResource);

        btnOwnerViewRequests = findViewById(R.id.btnOwnerViewRequests);
        btnEdit = findViewById(R.id.btnEditResource);
        btnDelete = findViewById(R.id.btnDeleteResource);

        loadResourceDetails();
        checkIfSaved();

        setupListeners();
    }

    private void setupListeners() {
        btnRequestBorrow.setOnClickListener(v -> {
            if (currentResource == null) return;
            Intent intent = new Intent(ResourceDetailsActivity.this, BorrowRequestActivity.class);
            intent.putExtra("resourceId", currentResource.getResourceId());
            intent.putExtra("resourceName", currentResource.getResourceName());
            intent.putExtra("ownerId", currentResource.getOwnerId());
            startActivity(intent);
        });

        btnCallOwner.setOnClickListener(v -> {
            if (currentResource != null && currentResource.getOwnerPhone() != null && !currentResource.getOwnerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + currentResource.getOwnerPhone()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Owner phone number not provided", Toast.LENGTH_SHORT).show();
            }
        });

        btnSmsOwner.setOnClickListener(v -> {
            if (currentResource != null && currentResource.getOwnerPhone() != null && !currentResource.getOwnerPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + currentResource.getOwnerPhone()));
                intent.putExtra("sms_body", "Hi " + currentResource.getOwnerName() + ", I saw your item '" + currentResource.getResourceName() + "' on Campus Share!");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Owner phone number not provided", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> toggleSaveResource());

        btnReport.setOnClickListener(v -> {
            if (currentResource == null) return;
            Intent intent = new Intent(ResourceDetailsActivity.this, ReportResourceActivity.class);
            intent.putExtra("resourceId", currentResource.getResourceId());
            startActivity(intent);
        });

        btnOwnerViewRequests.setOnClickListener(v -> {
            Intent intent = new Intent(ResourceDetailsActivity.this, BorrowRequestsOwnerActivity.class);
            intent.putExtra("resourceId", resourceId);
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ResourceDetailsActivity.this, EditResourceActivity.class);
            intent.putExtra("resourceId", resourceId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> deleteResource());
    }

    private void loadResourceDetails() {
        if (resourceId == null) return;

        FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                .document(resourceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentResource = doc.toObject(Resource.class);
                        if (currentResource != null) {
                            tvName.setText(currentResource.getResourceName());
                            tvCategory.setText("Category: " + currentResource.getCategory());
                            tvCondition.setText("Condition: " + currentResource.getCondition());
                            tvLocation.setText("Location: " + currentResource.getLocation());
                            tvOwner.setText("Posted by: " + currentResource.getOwnerName() + " (" + currentResource.getOwnerPhone() + ")");
                            tvDescription.setText(currentResource.getDescription());

                            String avail = currentResource.getAvailability() != null ? currentResource.getAvailability() : "available";
                            tvAvailability.setText(avail.toUpperCase());

                            if ("available".equalsIgnoreCase(avail)) {
                                tvAvailability.setBackgroundColor(ContextCompat.getColor(this, R.color.status_available));
                            } else if ("borrowed".equalsIgnoreCase(avail)) {
                                tvAvailability.setBackgroundColor(ContextCompat.getColor(this, R.color.status_borrowed));
                            } else {
                                tvAvailability.setBackgroundColor(ContextCompat.getColor(this, R.color.status_requested));
                            }

                            if (currentResource.getImageUrl() != null && !currentResource.getImageUrl().isEmpty()) {
                                Glide.with(this)
                                        .load(currentResource.getImageUrl())
                                        .placeholder(R.drawable.ic_placeholder)
                                        .into(ivImage);
                            }

                            String currentUid = FirebaseHelper.getCurrentUserId();
                            if (currentUid != null && currentUid.equals(currentResource.getOwnerId())) {
                                layoutOwnerActions.setVisibility(View.VISIBLE);
                                layoutUserActions.setVisibility(View.GONE);
                            } else {
                                layoutOwnerActions.setVisibility(View.GONE);
                                layoutUserActions.setVisibility(View.VISIBLE);

                                if (!"available".equalsIgnoreCase(avail)) {
                                    btnRequestBorrow.setEnabled(false);
                                    btnRequestBorrow.setText("Not Available");
                                }
                            }
                        }
                    }
                });
    }

    private void checkIfSaved() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null || resourceId == null) return;

        FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION)
                .whereEqualTo("userId", uid)
                .whereEqualTo("resourceId", resourceId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    isSaved = !querySnapshot.isEmpty();
                    btnSave.setText(isSaved ? "Saved" : "Save");
                });
    }

    private void toggleSaveResource() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null || resourceId == null) return;

        if (isSaved) {
            FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION)
                    .whereEqualTo("userId", uid)
                    .whereEqualTo("resourceId", resourceId)
                    .get()
                    .addOnSuccessListener(qs -> {
                        for (var doc : qs) doc.getReference().delete();
                        isSaved = false;
                        btnSave.setText("Save");
                        Toast.makeText(this, "Removed from saved resources", Toast.LENGTH_SHORT).show();
                    });
        } else {
            String savedId = FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION).document().getId();
            SavedResource sr = new SavedResource(savedId, uid, resourceId);
            FirebaseHelper.getFirestore().collection(FirebaseHelper.SAVED_RESOURCES_COLLECTION)
                    .document(savedId).set(sr)
                    .addOnSuccessListener(aVoid -> {
                        isSaved = true;
                        btnSave.setText("Saved");
                        Toast.makeText(this, "Saved to your list!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void deleteResource() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Resource")
                .setMessage("Are you sure you want to delete this resource?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                            .document(resourceId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ResourceDetailsActivity.this, "Resource deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadResourceDetails();
    }
}
