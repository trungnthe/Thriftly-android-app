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
import android.widget.TextView;
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
    private TextView emptyMessage, emptyTextView;

    private void bindingView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_products);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyTextView = view.findViewById(R.id.emptyTextView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bindingView(view);
        checkUserLoginAndEmailVerification();

        // Lấy userId của người dùng hiện tại
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserId = (currentUser != null) ? currentUser.getUid() : null;
        if (currentUserId == null) {
            Toast.makeText(getContext(), "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
        }

        // Khởi tạo adapter một lần
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, currentUserId);

        recyclerView.setAdapter(productAdapter);

        // Tải các sản phẩm của người dùng hiện tại
        loadProducts(currentUserId);

        return view;
    }

    // Kiểm tra trạng thái đăng nhập và xác minh email
    private void checkUserLoginAndEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Nếu người dùng chưa đăng nhập, chuyển hướng đến màn hình đăng nhập
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

    // Tải danh sách sản phẩm từ Firestore
    private void loadProducts(String currentUserId) {

        db.collection("Products").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear(); // Xóa danh sách trước khi thêm mới
                        // Lọc và chỉ thêm các sản phẩm của người dùng hiện tại
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());

                            // Chỉ thêm sản phẩm của người dùng hiện tại
                            if (product.getUserId().equals(currentUserId)) {
                                String status = document.getString("status");
                                product.setStatus(status);
                                productList.add(product);
                            }
                        }

                        // Kiểm tra nếu danh sách sản phẩm rỗng
                        if (productList.isEmpty()) {
                            emptyTextView.setVisibility(View.VISIBLE); // Hiển thị TextView thông báo
                            emptyTextView.setText("Không có sản phẩm nào của bạn.");
                        } else {
                            emptyTextView.setVisibility(View.GONE); // Ẩn TextView nếu có sản phẩm
                        }

                        // Cập nhật Adapter
                        productAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi tải sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
}
}

