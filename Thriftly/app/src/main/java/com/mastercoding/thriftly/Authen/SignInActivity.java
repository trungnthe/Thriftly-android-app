package com.mastercoding.thriftly.Authen;

import static android.content.ContentValues.TAG;

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
import com.mastercoding.thriftly.MainActivity;
import com.mastercoding.thriftly.R;

public class SignInActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button signInButton, signUpButton;
    private TextView forgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindingView();
        bindingAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void bindingView() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        signInButton = findViewById(R.id.sign_in_button);
        signUpButton = findViewById(R.id.sign_up_button);
        forgotPassword = findViewById(R.id.forgot_password);
        progressBar = findViewById(R.id.sign_in_pb);
        mAuth = FirebaseAuth.getInstance();
    }
    
    private void bindingAction() {
        signInButton.setOnClickListener(this::onSignInButtonClicked);
        forgotPassword.setOnClickListener(this::onForgotPasswordClicked);
        signUpButton.setOnClickListener(this::onSignUpButtonClicked);
    }

    private void onSignUpButtonClicked(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }

    private void onForgotPasswordClicked(View view) {
        startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
    }

    private void onSignInButtonClicked(View view) {
        checkUser();
    }

    private void checkUser() {
        String strEmail = emailInput.getText().toString().trim();
        String strPassword = passwordInput.getText().toString().trim();

        // Kiểm tra đầu vào
        if (!validateInput(strEmail, strPassword)) return;

        // Hiển thị thông báo chờ
        showProgressBar();

        signIn(strEmail, strPassword);
    }



    private boolean validateInput(String strEmail, String strPassword) {
        if (strEmail.isEmpty()) {
            emailInput.setError("Vui lòng nhập email!");
            emailInput.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            emailInput.setError("Vui lòng nhập email hợp lệ");
            emailInput.requestFocus();
            return false;
        }

        if (strPassword.isEmpty()) {
            passwordInput.setError("Vui lòng nhập mật khẩu!");
            passwordInput.requestFocus();
            return false;
        }

        if (strPassword.length() < 6) {
            passwordInput.setError("Mật khẩu phải hơn 6 ký tự!");
            passwordInput.requestFocus();
            return false;
        }
        return true;
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            dismissProgressBar();
                            updateUI(user);
                        } else {
                            // If sign in fails
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this,
                                    "Đăng nhập thất bại! Hãy kiểm tra lại thông tin đăng nhập!",
                                    Toast.LENGTH_LONG).show();
                            dismissProgressBar();
                            updateUI(null);
                        }
                    }
                });
    }



    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "User is not signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.GONE);
    }

    private void dismissProgressBar() {
        progressBar.setVisibility(View.GONE);
        signInButton.setVisibility(View.VISIBLE);
    }
}
