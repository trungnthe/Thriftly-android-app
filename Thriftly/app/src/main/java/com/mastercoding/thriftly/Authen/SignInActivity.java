package com.mastercoding.thriftly.Authen;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mastercoding.thriftly.Chat.FirebaseUtil;
import com.mastercoding.thriftly.MainActivity;
import com.mastercoding.thriftly.R;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button signInButton, signUpButton;
    private ImageButton ibFacebook, ibGoogle;
    private TextView forgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private GoogleSignInClient googleSignInClient;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        bindingView();
        bindingAction();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
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
        ibGoogle = findViewById(R.id.google_button);
        progressBar = findViewById(R.id.sign_in_pb);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }
    
    private void bindingAction() {
        signInButton.setOnClickListener(this::onSignInButtonClicked);
        forgotPassword.setOnClickListener(this::onForgotPasswordClicked);
        signUpButton.setOnClickListener(this::onSignUpButtonClicked);
        ibGoogle.setOnClickListener(this::onGoogleButtonClicked);
    }

    private void onGoogleButtonClicked(View view) {
        signInWithGoogle();
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            String email = user.getEmail();
                            String fullName = user.getDisplayName();
                            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
                            String username = email.split("@")[0];

                            Map<String, Object> map = new HashMap<>();
                            map.put("fullname", fullName);
                            map.put("email", email);
                            map.put("image", photoUrl);
                            map.put("username", username);
                            map.put("status", true);
                            map.put("address", "");
                            map.put("phone", "");

                            firestore.collection("User").document(userId).set(map)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Lưu dữ liệu thành công");
                                        getUserId();
                                        getFCMToken();
                                        dismissProgressBar();
                                        updateUI(user);
                                    })
                                    .addOnFailureListener(e -> {
                                        dismissProgressBar();
                                    });
                        }
                        else{
                            Toast.makeText(SignInActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
        if (!validateInput(strEmail, strPassword)) return;
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

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        });
    }

    private void getUserId() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            FirebaseUtil.currentUserDetails().update("userId", userId);
        }
    }
}
