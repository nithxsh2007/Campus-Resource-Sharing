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
import com.example.myproject.model.Resource;
import com.example.myproject.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class EditResourceActivity extends AppCompatActivity {

    private TextInputEditText etName, etDescription, etLocation, etPhone;
    private Spinner spinnerCategory, spinnerCondition;
    private Button btnSave;
    private ProgressBar pbLoading;

    private String resourceId;
    private final String[] categories = {"Books", "Electronics", "Calculators", "Lab Equipment", "Stationery", "Accessories", "Other"};
    private final String[] conditions = {"New", "Good", "Used", "Needs Repair"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource); // Reuse add_resource layout

        resourceId = getIntent().getStringExtra("resourceId");

        etName = findViewById(R.id.etAddName);
        etDescription = findViewById(R.id.etAddDescription);
        etLocation = findViewById(R.id.etAddLocation);
        etPhone = findViewById(R.id.etAddPhone);
        spinnerCategory = findViewById(R.id.spinnerAddCategory);
        spinnerCondition = findViewById(R.id.spinnerAddCondition);
        btnSave = findViewById(R.id.btnSubmitResource);
        pbLoading = findViewById(R.id.pbAddLoading);

        btnSave.setText("Update Resource");

        setupSpinners();
        loadResourceDetails();

        btnSave.setOnClickListener(v -> updateResource());
    }

    private void setupSpinners() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<String> condAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conditions);
        condAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(condAdapter);
    }

    private void loadResourceDetails() {
        if (resourceId == null) return;

        pbLoading.setVisibility(View.VISIBLE);
        FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                .document(resourceId)
                .get()
                .addOnSuccessListener(doc -> {
                    pbLoading.setVisibility(View.GONE);
                    if (doc.exists()) {
                        Resource r = doc.toObject(Resource.class);
                        if (r != null) {
                            etName.setText(r.getResourceName());
                            etDescription.setText(r.getDescription());
                            etLocation.setText(r.getLocation());
                            etPhone.setText(r.getOwnerPhone());

                            setSpinnerSelection(spinnerCategory, categories, r.getCategory());
                            setSpinnerSelection(spinnerCondition, conditions, r.getCondition());
                        }
                    }
                });
    }

    private void setSpinnerSelection(Spinner spinner, String[] array, String value) {
        if (value == null) return;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void updateResource() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "";
        String condition = spinnerCondition.getSelectedItem() != null ? spinnerCondition.getSelectedItem().toString() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Name and description are required", Toast.LENGTH_SHORT).show();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                .document(resourceId)
                .update(
                        "resourceName", name,
                        "category", category,
                        "condition", condition,
                        "description", description,
                        "location", location,
                        "ownerPhone", phone,
                        "updatedAt", System.currentTimeMillis()
                )
                .addOnSuccessListener(aVoid -> {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(EditResourceActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    pbLoading.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(EditResourceActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
