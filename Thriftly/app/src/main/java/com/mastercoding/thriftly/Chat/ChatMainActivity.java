package com.mastercoding.thriftly.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mastercoding.thriftly.R;
import com.mastercoding.thriftly.UI.ProfileFragment;

public class ChatMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageButton searchButton;

    private ChatFragment chatFragment;
    private  ImageButton btnBack;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        bindingView();
        bindingAction();

    }

    private void bindingView() {
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);
        btnBack=findViewById(R.id.back_btn);
    }

    private void bindingAction() {
        searchButton.setOnClickListener(this::onClickSearchButton);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);  // Set default selected item
        btnBack.setOnClickListener(this::onClickBack);
    }

    private void onClickBack(View view) {
        onBackPressed();
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_chat) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
            return true;

        }
        return false;
    }

    private void onClickSearchButton(View view) {
        startActivity(new Intent(ChatMainActivity.this, SearchUserActivity.class));
    }


}
