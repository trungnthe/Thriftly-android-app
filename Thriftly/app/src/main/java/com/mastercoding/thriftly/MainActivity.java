package com.mastercoding.thriftly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mastercoding.thriftly.Chat.ChatFragment;
import com.mastercoding.thriftly.UI.HomeFragment;
import com.mastercoding.thriftly.UI.MainProfileFragment;
import com.mastercoding.thriftly.UI.ProfileFragment;
import com.mastercoding.thriftly.UI.AddProductActivity;
import com.mastercoding.thriftly.UI.SalesHistoryFragment;
import com.mastercoding.thriftly.UI.ShoppingSiteFragement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    //private Toolbar toolbar;
    private HomeFragment homeFragment;

    private ShoppingSiteFragement shoppingSiteFragement;
    private SalesHistoryFragment salesHistoryFragment;

    ChatFragment chatFragment;

    private MainProfileFragment mainProfileFragment;


    private void bindingView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        homeFragment = new HomeFragment();
        chatFragment= new ChatFragment();
        shoppingSiteFragement = new ShoppingSiteFragement();

        mainProfileFragment = new MainProfileFragment();
        salesHistoryFragment = new SalesHistoryFragment();

        fab = findViewById(R.id.fab);  // Tham chiếu FAB

    }

    private void bindingAction() {
        // Sự kiện khi nhấn vào FAB để chuyển sang AddProductActivity
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindingView();
        bindingAction();  // Thêm sự kiện vào FAB
//        if (getIntent().hasExtra("showFragment")) {
//            String fragmentToShow = getIntent().getStringExtra("showFragment");
//            if ("homeFragment".equals(fragmentToShow)) {
//                switchFragment(new HomeFragment());
//            }
//        }


        Intent intent = getIntent();
        int navigateValue = intent.getIntExtra("navigate_to", -1);
        Log.d("MainActivity", "Intent received in onCreate with navigate_to: " + navigateValue);

        // Kiểm tra giá trị của navigate_to để chuyển đến SalesHistoryFragment
        if (navigateValue == 1) {
            switchFragment(new SalesHistoryFragment());  // Điều hướng đến SalesHistoryFragment
        } else {
            switchFragment(new ShoppingSiteFragement());  // Fragment mặc định khi vào Activity
        }

        //setSupportActionBar(toolbar);
        bottomNavigationView.setBackground(null);
        setupBottomNavigation();
        bottomNavigationView.setBackground(null);
        setupBottomNavigation();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Log.d("MainActivity", "No user is signed in");
        } else {
            Log.d("MainActivity", "User is signed in");
        }

    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                switchFragment(shoppingSiteFragement);
            } else if (id == R.id.menu_shorts) {

                switchFragment(homeFragment);

            } else if (id == R.id.menu_library) {
                switchFragment(mainProfileFragment);
            }
            return true;
        });
    }
    private void switchFragment(Fragment fragment) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Cập nhật Intent mới

        int navigateValue = intent.getIntExtra("navigate_to", -1);
        Log.d("MainActivity", "Intent received in onNewIntent with navigate_to: " + navigateValue);

        // Kiểm tra giá trị của navigate_to để chuyển đến SalesHistoryFragment
        if (navigateValue == 1) {
            switchFragment(new SalesHistoryFragment());
        }
    }


}
