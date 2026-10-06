package com.example.myproject.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.example.myproject.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnResetPassword;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etResetEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        pbLoading = findViewById(R.id.pbResetLoading);

        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter your registered email");
            etEmail.requestFocus();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        btnResetPassword.setEnabled(false);

        FirebaseHelper.getAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    pbLoading.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Reset email sent! Check your inbox.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String err = task.getException() != null ? task.getException().getMessage() : "Error sending reset email";
                        Toast.makeText(ForgotPasswordActivity.this, "Error: " + err, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
