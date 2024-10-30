package com.mastercoding.thriftly.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mastercoding.thriftly.Chat.FirebaseUtil;
import com.mastercoding.thriftly.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText fullNameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button signUpButton;
    private Button signInButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        emailInput = findViewById(R.id.signup_email_input);
        passwordInput = findViewById(R.id.signup_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        signUpButton = findViewById(R.id.signup_button);
        signInButton = findViewById(R.id.sign_in_button);
        progressBar = findViewById(R.id.sign_up_pb);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }


    private void bindingAction() {

        signUpButton.setOnClickListener(this::onSignUpButtonClicked);

        signInButton.setOnClickListener(this::onSignInButtonClicked);
    }

    private void onSignInButtonClicked(View view) {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
    }

    private void onSignUpButtonClicked(View view) {
        craeteAccount();
    }

    private void craeteAccount() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (!validateForm(email, password, confirmPassword)) {
            return;
        }

        showProgressBar();

        createAccount(email, password);

    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                                            String userId = user.getUid();  // Lấy userId

                                            // Tạo FCM token
                                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                                @Override
                                                public void onComplete(@NonNull Task<String> tokenTask) {
                                                    if (tokenTask.isSuccessful()) {
                                                        String fcmToken = tokenTask.getResult();

                                                        Map<String, Object> userData = new HashMap<>();
                                                        userData.put("userId", userId);        // Lưu userId vào Firestore
                                                        userData.put("email", email);
                                                        userData.put("username", "");
                                                        userData.put("phone", "");
                                                        userData.put("status", true);
                                                        userData.put("image", "1");
                                                        userData.put("fcmToken", fcmToken);    // Lưu FCM token vào Firestore

                                                        firestore.collection("User").document(userId).set(userData)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    Log.d("Firestore", "Lưu dữ liệu thành công");
                                                                    dismissProgressBar();
                                                                    FirebaseAuth.getInstance().signOut();
                                                                    startActivity(new Intent(SignUpActivity.this, EmailVerifyActivity.class));
                                                                    finishAffinity();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    dismissProgressBar();
                                                                });
                                                    } else {
                                                        Toast.makeText(SignUpActivity.this, "Không thể tạo FCM token", Toast.LENGTH_SHORT).show();
                                                        dismissProgressBar();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Tạo tài khoản thất bại.";
                            Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            dismissProgressBar();
                        }
                    }
                });
    }



    private boolean validateForm(String email, String password, String confirmPassword) {

        if (email.isEmpty()) {
            emailInput.setError("Vui lòng nhập email!");
            emailInput.requestFocus();
            return false;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Email chưa hợp lệ!");
            emailInput.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Vui lòng nhập mật khẩu mới!");
            passwordInput.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Mật khẩu phải hơn 6 ký tự!");
            passwordInput.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError("Vui lòng nhập lại mật khẩu mới!");
            confirmPasswordInput.requestFocus();
            return false;
        }

        if (!confirmPassword.equals(password)) {
            confirmPasswordInput.setError("Không trùng với mật khẩu mới!");
            confirmPasswordInput.requestFocus();
            return false;
        }

        return true;
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.GONE);
    }

    private void dismissProgressBar() {
        progressBar.setVisibility(View.GONE);
        signUpButton.setVisibility(View.VISIBLE);
    }

}
