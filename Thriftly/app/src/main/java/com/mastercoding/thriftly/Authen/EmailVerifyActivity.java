package com.mastercoding.thriftly.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mastercoding.thriftly.R;

public class EmailVerifyActivity extends AppCompatActivity {

    private Button verifyBtn;

    private void bindingView(){
        verifyBtn = findViewById(R.id.sign_in_button);
    }

    private void bindingAction(){
        verifyBtn.setOnClickListener(this::changeScreen);
    }

    private void changeScreen(View view) {
        startActivity(new Intent(EmailVerifyActivity.this, SignInActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindingView();
        bindingAction();

    }
}