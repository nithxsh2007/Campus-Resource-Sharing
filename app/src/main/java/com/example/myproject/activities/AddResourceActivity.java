package com.example.myproject.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.example.myproject.model.Resource;
import com.example.myproject.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class AddResourceActivity extends AppCompatActivity {

    private ImageView ivAddImage;
    private Button btnSelectImage, btnSubmitResource;
    private TextInputEditText etName, etDescription, etLocation, etPhone;
    private Spinner spinnerCategory, spinnerCondition;
    private ProgressBar pbLoading;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivAddImage.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource);

        ivAddImage = findViewById(R.id.ivAddImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmitResource = findViewById(R.id.btnSubmitResource);
        etName = findViewById(R.id.etAddName);
        etDescription = findViewById(R.id.etAddDescription);
        etLocation = findViewById(R.id.etAddLocation);
        etPhone = findViewById(R.id.etAddPhone);
        spinnerCategory = findViewById(R.id.spinnerAddCategory);
        spinnerCondition = findViewById(R.id.spinnerAddCondition);
        pbLoading = findViewById(R.id.pbAddLoading);

        setupSpinners();

        btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnSubmitResource.setOnClickListener(v -> submitResource());
    }

    private void setupSpinners() {
        String[] categories = {"Books", "Electronics", "Calculators", "Lab Equipment", "Stationery", "Accessories", "Other"};
        String[] conditions = {"New", "Good", "Used", "Needs Repair"};

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<String> condAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conditions);
        condAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(condAdapter);
    }

    private void submitResource() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "";
        String condition = spinnerCondition.getSelectedItem() != null ? spinnerCondition.getSelectedItem().toString() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Resource name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        String ownerId = FirebaseHelper.getCurrentUserId();
        if (ownerId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        btnSubmitResource.setEnabled(false);

        // First load owner name from user profile
        FirebaseHelper.getFirestore().collection(FirebaseHelper.USERS_COLLECTION)
                .document(ownerId).get().addOnSuccessListener(userDoc -> {
                    String ownerName = userDoc.exists() && userDoc.getString("fullName") != null
                            ? userDoc.getString("fullName") : "Student";

                    if (selectedImageUri != null) {
                        uploadImageAndSave(name, category, condition, description, location, phone, ownerId, ownerName);
                    } else {
                        saveToFirestore(name, category, condition, description, location, phone, ownerId, ownerName, "");
                    }
                }).addOnFailureListener(e -> {
                    pbLoading.setVisibility(View.GONE);
                    btnSubmitResource.setEnabled(true);
                    Toast.makeText(AddResourceActivity.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageAndSave(String name, String category, String condition, String description,
                                    String location, String phone, String ownerId, String ownerName) {
        String resId = FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();
        StorageReference ref = FirebaseHelper.getStorage().getReference().child("resource_images/" + resId + ".jpg");

        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToFirestore(name, category, condition, description, location, phone, ownerId, ownerName, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    // Fallback save without image if upload fails
                    saveToFirestore(name, category, condition, description, location, phone, ownerId, ownerName, "");
                });
    }

    private void saveToFirestore(String name, String category, String condition, String description,
                                 String location, String phone, String ownerId, String ownerName, String imageUrl) {
        FirebaseFirestore db = FirebaseHelper.getFirestore();
        String resourceId = db.collection(FirebaseHelper.RESOURCES_COLLECTION).document().getId();

        Resource resource = new Resource(
                resourceId, name, category, description, condition, location, imageUrl,
                ownerId, ownerName, phone, "available"
        );

        db.collection(FirebaseHelper.RESOURCES_COLLECTION)
                .document(resourceId)
                .set(resource)
                .addOnSuccessListener(aVoid -> {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(AddResourceActivity.this, "Resource posted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    pbLoading.setVisibility(View.GONE);
                    btnSubmitResource.setEnabled(true);
                    Toast.makeText(AddResourceActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
