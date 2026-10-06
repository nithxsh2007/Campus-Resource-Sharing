package com.example.myproject.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.example.myproject.model.BorrowRequest;
import com.example.myproject.model.User;
import com.example.myproject.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class BorrowRequestActivity extends AppCompatActivity {

    private TextView tvResourceTitle, tvOwner, tvReturnDate;
    private Button btnPickDate, btnSubmit;
    private TextInputEditText etNote;
    private ProgressBar pbLoading;

    private String resourceId, resourceName, ownerId, selectedReturnDate = "";
    private String borrowerName = "Student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_request);

        resourceId = getIntent().getStringExtra("resourceId");
        resourceName = getIntent().getStringExtra("resourceName");
        ownerId = getIntent().getStringExtra("ownerId");

        tvResourceTitle = findViewById(R.id.tvBorrowReqResourceTitle);
        tvOwner = findViewById(R.id.tvBorrowReqOwner);
        tvReturnDate = findViewById(R.id.tvSelectedReturnDate);
        btnPickDate = findViewById(R.id.btnPickReturnDate);
        btnSubmit = findViewById(R.id.btnSubmitBorrowRequest);
        etNote = findViewById(R.id.etBorrowReqNote);
        pbLoading = findViewById(R.id.pbBorrowReqLoading);

        if (resourceName != null) tvResourceTitle.setText(resourceName);

        loadBorrowerInfo();

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnSubmit.setOnClickListener(v -> submitBorrowRequest());
    }

    private void loadBorrowerInfo() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid != null) {
            FirebaseHelper.getFirestore().collection(FirebaseHelper.USERS_COLLECTION)
                    .document(uid).get().addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            User u = doc.toObject(User.class);
                            if (u != null) borrowerName = u.getFullName();
                        }
                    });
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedReturnDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tvReturnDate.setText(selectedReturnDate);
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void submitBorrowRequest() {
        if (TextUtils.isEmpty(selectedReturnDate)) {
            Toast.makeText(this, "Please select an expected return date", Toast.LENGTH_SHORT).show();
            return;
        }

        String borrowerId = FirebaseHelper.getCurrentUserId();
        if (borrowerId == null) {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        String note = etNote.getText() != null ? etNote.getText().toString().trim() : "";
        String requestId = FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION).document().getId();

        Calendar cal = Calendar.getInstance();
        String requestDate = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);

        BorrowRequest request = new BorrowRequest(
                requestId, resourceId, resourceName, ownerId, borrowerId, borrowerName,
                requestDate, selectedReturnDate, "pending", note
        );

        FirebaseHelper.getFirestore().collection(FirebaseHelper.BORROW_REQUESTS_COLLECTION)
                .document(requestId)
                .set(request)
                .addOnSuccessListener(aVoid -> {
                    // Update resource availability status to 'requested'
                    FirebaseHelper.getFirestore().collection(FirebaseHelper.RESOURCES_COLLECTION)
                            .document(resourceId)
                            .update("availability", "requested");

                    // Send notification to resource owner
                    FirebaseHelper.sendNotification(
                            ownerId,
                            "New Borrow Request",
                            borrowerName + " wants to borrow " + resourceName,
                            "request"
                    );

                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(BorrowRequestActivity.this, "Borrow request sent successfully!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    pbLoading.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(BorrowRequestActivity.this, "Failed to send request: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
