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
import com.mastercoding.thriftly.Adapter.OrderAdapter;
import com.mastercoding.thriftly.Models.Order;
import com.mastercoding.thriftly.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import android.util.Log;

public class OrderHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private Spinner spinnerOrderStatus;
    private List<Order> purchaseOrderList;
    private List<Order> filteredOrderList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firebase Firestore
        auth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        bindingView(view);
        initRcv();
        fetchOrdersFromFirestore(); // Fetch data from Firestore
        return view;
    }

    private void bindingView(View view) {
        spinnerOrderStatus = view.findViewById(R.id.spinner_order_status);
        recyclerView = view.findViewById(R.id.recycler_view_purchase_history);
    }

    private void initRcv() {
        purchaseOrderList = new ArrayList<>();
        filteredOrderList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext(), filteredOrderList); // Update to use filteredOrderList
        recyclerView.setAdapter(orderAdapter);

        // Spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.order_status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderStatus.setAdapter(adapter);

        // Set default selection and initial filter
        spinnerOrderStatus.setSelection(adapter.getPosition("Đang chờ xác nhận"));
        filterOrdersByStatus("Đang chờ xác nhận");

        // Spinner selection listener
        spinnerOrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                filterOrdersByStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Fetch data from Firestore and listen for changes
    private void fetchOrdersFromFirestore() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("Orders")
                .whereEqualTo("buyerId", userId) // Filter data by userId
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        purchaseOrderList.clear();
                        for (QueryDocumentSnapshot document : snapshots) {
                            Order order = document.toObject(Order.class);
                            order.setOrderId(document.getId()); // Set the ID if needed

                            // Fetch the product details based on productId
                            String productId = order.getProductId();
                            db.collection("Products").document(productId)
                                    .get()
                                    .addOnCompleteListener(productTask -> {
                                        if (productTask.isSuccessful() && productTask.getResult() != null) {
                                            String productName = productTask.getResult().getString("name");
                                            String imageUrl = productTask.getResult().getString("imageUrl");

                                            // Set product details in the order
                                            order.setProductName(productName);
                                            order.setImageUrl(imageUrl);

                                            purchaseOrderList.add(order);

                                            // Sort and filter the orders after adding new ones
                                            Collections.sort(purchaseOrderList, (o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
                                            filterOrdersByStatus(spinnerOrderStatus.getSelectedItem().toString());
                                        } else {
                                            Log.d("Firestore", "Error getting product details: ", productTask.getException());
                                        }
                                    });
                        }
                    }
                });
    }

    // Filter orders based on status
    private void filterOrdersByStatus(String status) {
        filteredOrderList.clear();

        // Filter purchaseOrderList and add to filteredOrderList
        filteredOrderList.addAll(purchaseOrderList.stream()
                .filter(order -> mapStatusToReadable(order.getStatus()).equals(status))
                .collect(Collectors.toList()));

        // Notify adapter about data change
        orderAdapter.notifyDataSetChanged();
    }

    // Map status codes to readable text
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
