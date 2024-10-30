package com.mastercoding.thriftly.Adapter;

import android.content.Context;
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

import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {

    private Context context;
    private List<Order> salesList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SalesAdapter(Context context, List<Order> salesList) {
        this.context = context;
        this.salesList = salesList;
    }

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sell_history, parent, false);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder holder, int position) {
        Order sale = salesList.get(position);

        holder.tvOrderId.setText("Người mua: " + sale.getBuyerName()) ;
        holder.tvCustomerName.setText("Tên sản phẩm:  " + sale.getProductName());
        holder.tvTotalPrice.setText("Thành tiền: " + sale.getTotalAmount() + " VND");

        Glide.with(context)
                .load(sale.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgProduct);

        // Đặt trạng thái hiển thị của các nút dựa trên trạng thái đơn hàng
        holder.btnContact.setVisibility(View.GONE);
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
        holder.btndelivered.setVisibility(View.GONE);

        switch (sale.getStatus()) {
            case "waiting_confirmation":
                holder.btnConfirm.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
            case "shipping":
                holder.btndelivered.setVisibility(View.VISIBLE);
                holder.btnContact.setVisibility(View.VISIBLE);
                break;
            case "delivered":
                holder.btnContact.setVisibility(View.VISIBLE);
                break;
            case "completed":
                holder.btnContact.setVisibility(View.VISIBLE);
                break;
            case "canceled":
                holder.btnContact.setVisibility(View.VISIBLE);
                break;
        }

        // Xử lý sự kiện cho các nút
        holder.btnConfirm.setOnClickListener(v -> confirmOrder(sale.getOrderId()));
        holder.btnCancel.setOnClickListener(v -> cancelOrder(sale.getOrderId(), sale.getProductId()));
        holder.btnContact.setOnClickListener(v -> contactCustomer(sale.getBuyerId()));
        holder.btndelivered.setOnClickListener(v -> confirmDelivered(sale.getOrderId()));
    }



    @Override
    public int getItemCount() {
        return salesList.size();
    }
    private void confirmDelivered(String orderId) {
        db.collection("Orders").document(orderId)
                .update("status", "delivered")
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Order confirmed successfully");
                })
                .addOnFailureListener(e -> Log.d("Firestore", "Error confirming order: ", e));

    }
    private void confirmOrder(String orderId) {
        db.collection("Orders").document(orderId)
                .update("status", "shipping")
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Order confirmed successfully");
                })
                .addOnFailureListener(e -> Log.d("Firestore", "Error confirming order: ", e));

    }

    private void cancelOrder(String orderId, String productId) {
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
    }

    private void contactCustomer(String customerName) {
        // Thực hiện logic liên hệ với khách hàng
        Log.d("SalesAdapter", "Liên hệ với người mua: " + customerName);
    }

    public static class SalesViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvTotalPrice;
        ImageView imgProduct;
        Button btnContact, btnConfirm, btnCancel, btndelivered;

        public SalesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvCustomerName = itemView.findViewById(R.id.tv_order_customer_name);
            tvTotalPrice = itemView.findViewById(R.id.tv_order_total_price);
            imgProduct = itemView.findViewById(R.id.img_product);
            btnContact = itemView.findViewById(R.id.btn_contact_seller);
            btnConfirm = itemView.findViewById(R.id.btn_confirm_completion);
            btnCancel = itemView.findViewById(R.id.btn_cancel_order);
            btndelivered = itemView.findViewById(R.id.btn_confirm_delivered);
        }

    }
}
