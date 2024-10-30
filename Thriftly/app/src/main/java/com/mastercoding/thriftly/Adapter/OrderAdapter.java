package com.mastercoding.thriftly.Adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mastercoding.thriftly.Models.Order;
import com.mastercoding.thriftly.R;
import com.mastercoding.thriftly.UI.ProductDetailActivity;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Mã hóa đơn: " + order.getOrderId());
        holder.tvCustomerName.setText("Tên sản phẩm: " + order.getProductName());
        holder.tvTotalPrice.setText("Thành tiền: " + order.getTotalAmount() + " VND");

        Glide.with(context)
                .load(order.getImageUrl()) // Ensure product image URL is in the Order model
                .placeholder(R.drawable.ic_launcher_background) // Placeholder while loading
                .into(holder.imgProduct);

        // Ẩn tất cả các nút trước khi điều chỉnh theo trạng thái
        holder.btnContactSeller.setVisibility(View.GONE);
        holder.btnCancelOrder.setVisibility(View.GONE);
        holder.btnConfirmCompletion.setVisibility(View.GONE);
        holder.btnReorder.setVisibility(View.GONE);

        // Điều chỉnh nút bấm dựa trên trạng thái của đơn hàng
        switch (order.getStatus()) {
            case "waiting_confirmation":
                holder.btnContactSeller.setVisibility(View.VISIBLE);
                holder.btnCancelOrder.setVisibility(View.VISIBLE);
                break;
            case "shipping":
                holder.btnContactSeller.setVisibility(View.VISIBLE);
                holder.btnCancelOrder.setVisibility(View.VISIBLE);
                break;
            case "delivered":
                holder.btnContactSeller.setVisibility(View.VISIBLE);
                holder.btnConfirmCompletion.setVisibility(View.VISIBLE);
                break;
            case "completed":
                holder.btnContactSeller.setVisibility(View.VISIBLE);
                holder.btnReorder.setVisibility(View.VISIBLE);
                break;
            case "canceled":
                holder.btnContactSeller.setVisibility(View.VISIBLE);
                holder.btnReorder.setVisibility(View.VISIBLE);
                break;
        }

        holder.btnCancelOrder.setOnClickListener(v -> {
            // Xác định ID đơn hàng
            String orderId = order.getOrderId();
            String productId = order.getProductId();

            // Cập nhật trạng thái đơn hàng thành "canceled"
            db.collection("Orders").document(orderId)
                    .update("status", "canceled")
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Order canceled successfully");

                        // Cập nhật trạng thái sản phẩm thành "available" (nếu cần)
                        db.collection("Products").document(productId)
                                .update("status", "available")
                                .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Product status updated to available"))
                                .addOnFailureListener(e -> Log.d("Firestore", "Error updating product status: ", e));
                    })
                    .addOnFailureListener(e -> Log.d("Firestore", "Error canceling order: ", e));
        });

        holder.btnConfirmCompletion.setOnClickListener(v -> {
            // Xác định ID đơn hàng
            String orderId = order.getOrderId();

            // Cập nhật trạng thái đơn hàng thành "completed"
            db.collection("Orders").document(orderId)
                    .update("status", "completed")
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Order completed successfully"))
                    .addOnFailureListener(e -> Log.d("Firestore", "Error completing order: ", e));
        });


        holder.btnReorder.setOnClickListener(v -> {
            // Kiểm tra các giá trị trước khi truyền vào Intent
            Intent intent = new Intent(context, ProductDetailActivity.class);

            // Truyền dữ liệu product_id qua Intent để hiển thị thông tin chi tiết sản phẩm
            intent.putExtra("product_id", order.getProductId());

            // Chuyển sang màn hình chi tiết sản phẩm
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvTotalPrice;
        ImageView imgProduct;
        Button btnContactSeller, btnCancelOrder, btnConfirmCompletion, btnReorder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvCustomerName = itemView.findViewById(R.id.tv_order_customer_name);
            tvTotalPrice = itemView.findViewById(R.id.tv_order_total_price);
            imgProduct = itemView.findViewById(R.id.img_product);
            btnContactSeller = itemView.findViewById(R.id.btn_contact_seller);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);
            btnConfirmCompletion = itemView.findViewById(R.id.btn_confirm_completion);
            btnReorder = itemView.findViewById(R.id.btn_reorder);
        }
    }
}
