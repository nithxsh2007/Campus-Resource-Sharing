package com.example.myproject.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.example.myproject.model.ResourceRequest;
import com.example.myproject.model.User;
import com.example.myproject.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class PostResourceRequestActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription, etLocation, etNeededDate;
    private Spinner spinnerCategory;
    private Button btnSubmit;
    private ProgressBar pbLoading;

    private String requesterName = "Student";
    private String requesterPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_resource_request);

        etTitle = findViewById(R.id.etReqTitle);
        etDescription = findViewById(R.id.etReqDescription);
        etLocation = findViewById(R.id.etReqLocation);
        etNeededDate = findViewById(R.id.etReqNeededDate);
        spinnerCategory = findViewById(R.id.spinnerReqCategory);
        btnSubmit = findViewById(R.id.btnSubmitResourceRequest);
        pbLoading = findViewById(R.id.pbReqLoading);

        setupSpinner();
        loadRequesterInfo();

        btnSubmit.setOnClickListener(v -> submitNeededRequest());
    }

    private void setupSpinner() {
        String[] categories = {"Books", "Electronics", "Calculators", "Lab Equipment", "Stationery", "Accessories", "Other"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);
    }

    private void loadRequesterInfo() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid != null) {
            FirebaseHelper.getFirestore().collection(FirebaseHelper.USERS_COLLECTION)
                    .document(uid).get().addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            User u = doc.toObject(User.class);
                            if (u != null) {
                                requesterName = u.getFullName();
                                if (u.getPhone() != null) requesterPhone = u.getPhone();
                            }
                        }
                    });
        }
    }

    private void submitNeededRequest() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
        String date = etNeededDate.getText() != null ? etNeededDate.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Other";

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        String requesterId = FirebaseHelper.getCurrentUserId();
        if (requesterId == null) return;

        pbLoading.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        String requestId = FirebaseHelper.getFirestore().collection("neededResources").document().getId();

        ResourceRequest req = new ResourceRequest(
                requestId, title, category, description, date, location,
                requesterId, requesterName, requesterPhone, "open"
        );

        FirebaseHelper.getFirestore().collection("neededResources")
                .document(requestId)
                .set(req)
                .addOnSuccessListener(aVoid -> {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(PostResourceRequestActivity.this, "Resource request posted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    pbLoading.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(PostResourceRequestActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
