package com.mastercoding.thriftly.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mastercoding.thriftly.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText edtOldPassword;
    private TextInputEditText edtNewPassword;
    private TextInputEditText edtConfirmNewPassword;
    private Button btnChangePassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindingView();
        bindAction();
        checkUserLogin();
    }

    private void bindingView() {
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        mAuth = FirebaseAuth.getInstance();
    }

    private void bindAction(){
        btnChangePassword.setOnClickListener(this::onChangePass);
    }

    private void onChangePass(View view) {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmNewPassword = edtConfirmNewPassword.getText().toString().trim();

        if (validatePasswordInput(oldPassword, newPassword, confirmNewPassword)) {
            changePassword(oldPassword, newPassword);
        }
    }

    private void changePassword(String oldPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getEmail() != null) {
            // Xác thực lại người dùng trước khi đổi mật khẩu
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_LONG).show();
                                    logout();
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this, "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Old password is incorrect", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void logout() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean validatePasswordInput(String oldPassword, String newPassword, String confirmNewPassword) {
        if (oldPassword.isEmpty()) {
            edtOldPassword.setError("Old password is required");
            return false;
        }

        if (newPassword.isEmpty()) {
            edtNewPassword.setError("New password is required");
            return false;
        }

        if (newPassword.length() < 6) {
            edtNewPassword.setError("New password must be at least 6 characters");
            return false;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            edtConfirmNewPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void checkUserLogin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(ChangePasswordActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

}