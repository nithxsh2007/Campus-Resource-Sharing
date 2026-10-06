package com.example.myproject.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.example.myproject.model.Report;
import com.example.myproject.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class ReportResourceActivity extends AppCompatActivity {

    private Spinner spinnerReason;
    private TextInputEditText etDescription;
    private Button btnSubmit;

    private String resourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_resource);

        resourceId = getIntent().getStringExtra("resourceId");

        spinnerReason = findViewById(R.id.spinnerReportReason);
        etDescription = findViewById(R.id.etReportDescription);
        btnSubmit = findViewById(R.id.btnSubmitReport);

        String[] reasons = {"Fake information", "Incorrect information", "Inappropriate content", "Damaged resource", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reasons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReason.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void submitReport() {
        String reason = spinnerReason.getSelectedItem() != null ? spinnerReason.getSelectedItem().toString() : "";
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (TextUtils.isEmpty(desc)) {
            etDescription.setError("Please describe the issue");
            return;
        }

        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null || resourceId == null) return;

        btnSubmit.setEnabled(false);

        String reportId = FirebaseHelper.getFirestore().collection(FirebaseHelper.REPORTS_COLLECTION).document().getId();
        Report report = new Report(reportId, resourceId, uid, reason, desc);

        FirebaseHelper.getFirestore().collection(FirebaseHelper.REPORTS_COLLECTION)
                .document(reportId)
                .set(report)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ReportResourceActivity.this, "Report submitted. Thank you!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(ReportResourceActivity.this, "Failed to submit report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
