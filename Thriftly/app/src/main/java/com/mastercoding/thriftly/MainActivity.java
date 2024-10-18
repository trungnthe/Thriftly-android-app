package com.mastercoding.thriftly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mastercoding.thriftly.Authen.SignInActivity;
import com.mastercoding.thriftly.R;
import com.mastercoding.thriftly.UI.HomeFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private HomeFragment homeFragment;



    private void bindingView(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        homeFragment = new HomeFragment();
    }

    private void bindingAction(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindingView();
        setSupportActionBar(toolbar);
        bottomNavigationView.setBackground(null);
        setupBottomNavigation();

    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                switchFragment(homeFragment);
            } else if (id == R.id.menu_shorts) {
                Log.d("MainActivity", "menu_shorts");
                // replaceFragment(new ShortsFragment());
            } else if (id == R.id.menu_subscriptions) {
                Log.d("MainActivity", "menu_subscriptions");
                // replaceFragment(new SubscriptionFragment());
            } else if (id == R.id.menu_library) {
                logout();
                Log.d("MainActivity", "menu_library");
                // replaceFragment(new LibraryFragment());
            }
            return true;
        });
    }



    private void logout() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        // Navigate to the SignInActivity and clear back stack
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // Finish the current activity to prevent going back to the main screen after logout
        finish();
    }


    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

}