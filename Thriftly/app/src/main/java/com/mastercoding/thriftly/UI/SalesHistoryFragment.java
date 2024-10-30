package com.mastercoding.thriftly.UI;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mastercoding.thriftly.Adapter.SalesAdapter;
import com.mastercoding.thriftly.Models.Order;
import com.mastercoding.thriftly.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import android.util.Log;

public class SalesHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private SalesAdapter salesAdapter;
    private Spinner spinnerOrderStatus;
    private List<Order> salesOrderList;
    private List<Order> filteredSalesOrderList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public SalesHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Khởi tạo Firebase Firestore
        auth = FirebaseAuth.getInstance();    // Khởi tạo Firebase Auth
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_history, container, false);
        bindingView(view);
        bindingAction(view);
        initRcv();
        fetchSalesOrdersFromFirestore(); // Lấy dữ liệu từ Firestore
        return view;
    }

    private void bindingView(View view) {
        spinnerOrderStatus = view.findViewById(R.id.spinner_sale_status);
        recyclerView = view.findViewById(R.id.recycler_view_sales_history);
    }

    private void bindingAction(View view) {
        // Thêm bất kỳ hành động nào nếu cần
    }

    private void initRcv() {
        salesOrderList = new ArrayList<>();
        filteredSalesOrderList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        salesAdapter = new SalesAdapter(getContext(), filteredSalesOrderList); // Dùng danh sách đã lọc
        recyclerView.setAdapter(salesAdapter);

        // Adapter cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.order_status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderStatus.setAdapter(adapter);

        // Cài đặt lựa chọn mặc định và bộ lọc ban đầu
        spinnerOrderStatus.setSelection(adapter.getPosition("Đang chờ xác nhận"));
        filterSalesOrdersByStatus("Đang chờ xác nhận");

        // Lắng nghe lựa chọn của Spinner
        spinnerOrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                filterSalesOrdersByStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không thực hiện gì
            }
        });
    }

    // Lấy dữ liệu từ Firestore và lắng nghe thay đổi
    private void fetchSalesOrdersFromFirestore() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("Orders")
                .whereEqualTo("sellerId", userId) // Lọc dữ liệu theo userId
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        salesOrderList.clear();
                        for (QueryDocumentSnapshot document : snapshots) {
                            Order order = document.toObject(Order.class);
                            order.setOrderId(document.getId());

                            // Lấy thông tin sản phẩm dựa vào productId
                            String productId = order.getProductId();
                            db.collection("Products").document(productId)
                                    .get()
                                    .addOnCompleteListener(productTask -> {
                                        if (productTask.isSuccessful() && productTask.getResult() != null) {
                                            String productName = productTask.getResult().getString("name");
                                            String imageUrl = productTask.getResult().getString("imageUrl");

                                            // Cài đặt thông tin sản phẩm trong order
                                            order.setProductName(productName);
                                            order.setImageUrl(imageUrl);

                                            // Lấy thông tin sellerId từ bảng Users
                                            String buyerId = order.getBuyerId();
                                            db.collection("User").document(buyerId)
                                                    .get()
                                                    .addOnCompleteListener(userTask -> {
                                                        if (userTask.isSuccessful() && userTask.getResult() != null) {
                                                            String buyerName = userTask.getResult().getString("username");

                                                            // Cài đặt username trong order
                                                            order.setBuyerName(buyerName); // Giả sử Order có phương thức setUsername()

                                                            salesOrderList.add(order);

                                                            // Sắp xếp danh sách theo ngày đặt hàng
                                                            Collections.sort(salesOrderList, (o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));

                                                            // Lọc danh sách theo trạng thái đơn hàng
                                                            filterSalesOrdersByStatus(spinnerOrderStatus.getSelectedItem().toString());
                                                        } else {
                                                            Log.d("Firestore", "Error getting user details: ", userTask.getException());
                                                        }
                                                    });
                                        } else {
                                            Log.d("Firestore", "Error getting product details: ", productTask.getException());
                                        }
                                    });
                        }
                    }
                });
    }

    // Lọc đơn hàng dựa trên trạng thái
    private void filterSalesOrdersByStatus(String status) {
        filteredSalesOrderList.clear();

        // Lọc danh sách salesOrderList và thêm vào filteredSalesOrderList
        filteredSalesOrderList.addAll(salesOrderList.stream()
                .filter(order -> mapStatusToReadable(order.getStatus()).equals(status))
                .collect(Collectors.toList()));

        // Thông báo adapter về sự thay đổi dữ liệu
        salesAdapter.notifyDataSetChanged();
    }

    // Chuyển mã trạng thái thành chuỗi đọc được
    private String mapStatusToReadable(String status) {
        switch (status) {
            case "waiting_confirmation":
                return "Đang chờ xác nhận";
            case "shipping":
                return "Đang giao";
            case "delivered":
                return "Đã giao";
            case "completed":
                return "Đã hoàn thành";
            case "canceled":
                return "Đã hủy";
            default:
                return "Không rõ";
        }
    }
}