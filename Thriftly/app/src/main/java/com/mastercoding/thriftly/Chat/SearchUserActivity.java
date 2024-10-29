package com.mastercoding.thriftly.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mastercoding.thriftly.Adapter.RecentChatRecyclerAdapter;
import com.mastercoding.thriftly.Adapter.SearchUserRecyclerAdapter;
import com.mastercoding.thriftly.Models.UserModel;
import com.mastercoding.thriftly.R;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageButton searchButton;
    private ImageButton backButton;
    private RecyclerView recyclerView;

    private SearchUserRecyclerAdapter adapter;
    private List<UserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        bindingViews();
        bindingAction();
        setupRecyclerView();
        fetchAllUsers();


    }

    private void bindingViews() {
        searchInput = findViewById(R.id.seach_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        userList = new ArrayList<>();
    }
    private  void bindingAction(){
        backButton.setOnClickListener(this::onClickBackButton);
        searchButton.setOnClickListener(this::performSearch);
    }

    private void onClickBackButton(View view) {
         onBackPressed();
    }

    private void setupRecyclerView() {
        adapter = new SearchUserRecyclerAdapter(this, userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void fetchAllUsers() {
        FirebaseFirestore.getInstance().collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot snapshots = task.getResult();
                        List<UserModel> users = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots) {
                            UserModel user = doc.toObject(UserModel.class);
                            users.add(user);
                        }
                        adapter.updateUsers(users);
                    } else {
                        // Handle the error
                    }
                });
    }

    private void performSearch(View view) {
        String searchQuery = searchInput.getText().toString().trim();
        if (searchQuery.isEmpty()) {
            fetchAllUsers();
            return;
        }

        FirebaseFirestore.getInstance().collection("User")
                .orderBy("username") // Change field if needed
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<UserModel> filteredUsers = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            UserModel user = doc.toObject(UserModel.class);
                            filteredUsers.add(user);
                        }
                        adapter.updateUsers(filteredUsers);
                    } else {

                    }
                });
    }
}
