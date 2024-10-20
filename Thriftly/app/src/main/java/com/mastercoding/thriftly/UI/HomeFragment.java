package com.mastercoding.thriftly.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mastercoding.thriftly.Adapter.ProductAdapter;
import com.mastercoding.thriftly.Authen.SignInActivity;
import com.mastercoding.thriftly.Models.Product;
import com.mastercoding.thriftly.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;

    private void bindingView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();  // Firestore database
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bindingView(view);
        checkUserLoginAndEmailVerification();

        loadProducts();

        return view;
    }

    private void checkUserLoginAndEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            if (!user.isEmailVerified()) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
                getActivity().finish();
                Toast.makeText(getActivity(), "Please verify your email before proceeding", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        CollectionReference productsRef = db.collection("Products");

        productsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        productList.add(product);
                    }

                    productAdapter = new ProductAdapter(productList);
                    recyclerView.setAdapter(productAdapter);
                } else {
                    Toast.makeText(getActivity(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
