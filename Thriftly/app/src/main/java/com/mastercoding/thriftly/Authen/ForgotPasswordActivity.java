package com.mastercoding.thriftly.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.mastercoding.thriftly.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button btnReset, btnBack;
    EditText edtEmail;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String strEmail;

    public void bindingView(){
        btnBack = findViewById(R.id.btnForgotPasswordBack);
        btnReset = findViewById(R.id.btnReset);
        edtEmail = findViewById(R.id.edtForgotPasswordEmail);
        progressBar = findViewById(R.id.forgetPasswordProgressbar);
        mAuth = FirebaseAuth.getInstance();
    }

    private void bindingAction(){
        btnBack.setOnClickListener(this::onBackClick);
        btnReset.setOnClickListener(this::onResetClick);
    }

    private void onResetClick(View view) {
        strEmail = edtEmail.getText().toString().trim();
        if(!validateEmail(strEmail)){
            return;
        };
        resetPassword(strEmail);
    }

    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ForgotPasswordActivity.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPasswordActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        btnReset.setVisibility(View.VISIBLE);
                    }
                });
    }

    private boolean validateEmail(String strEmail) {
        if(strEmail.isEmpty()){
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
            edtEmail.setError("Invalid Email");
            edtEmail.requestFocus();
            return false;
        }
        return true;
    }

    private void onBackClick(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        bindingView();
        bindingAction();
    }
}