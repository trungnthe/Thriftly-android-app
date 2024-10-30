package com.mastercoding.thriftly.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.firebase.auth.FirebaseUser;
import com.mastercoding.thriftly.R;

import android.content.Intent;

import androidx.annotation.NonNull;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mastercoding.thriftly.Authen.SignInActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class MainProfileFragment extends Fragment {
    private FirebaseUser user;
    private ProfileFragment profileFragment;
    private OrderHistoryFragment orderHistoryFragment;
    private SalesHistoryFragment salesHistoryFragment;
    private ImageView imvAvatar;
    private TextView tvUsername;
    private Button btnLogout;
    private Button btneditProfile;
    private LinearLayout lnlOrderStatus;
    private LinearLayout lnlSellHistory;
    private LinearLayout personalInfo;

    public MainProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_profile, container, false);
        bindingView(view);
        bindingAction(view);
        displayProfile();
        return view;
    }

    private void bindingView(View view) {
        // Bắt sự kiện click cho các nút ở đây
        personalInfo = view.findViewById(R.id.personal_info);
        btneditProfile = view.findViewById(R.id.edit_profile_button);
        lnlOrderStatus = view.findViewById(R.id.order_status);
        lnlSellHistory = view.findViewById(R.id.sales_history);
        imvAvatar = view.findViewById(R.id.imvAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnLogout = view.findViewById(R.id.logout_button);
        profileFragment = new ProfileFragment();
        orderHistoryFragment = new OrderHistoryFragment();
        salesHistoryFragment = new SalesHistoryFragment();
    }

    private void bindingAction(View view) {
        personalInfo.setOnClickListener(this::onClickPersonalInfo);
        btneditProfile.setOnClickListener(this::onClickPersonalInfo);
        lnlOrderStatus.setOnClickListener(this::onClickOrderStatus);
        lnlSellHistory.setOnClickListener(this::onClickSellHistory);
        btnLogout.setOnClickListener(this::onClickLogout);
    }

    private void onClickLogout(View view) {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void onClickSellHistory(View view) {
        switchFragment(salesHistoryFragment);
    }

    private void onClickOrderStatus(View view) {
        switchFragment(orderHistoryFragment);
    }

    private void onClickPersonalInfo(View view) {
        switchFragment(profileFragment);
    }

    private void switchFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void displayProfile() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("User").document(user.getUid());

        docRef.get().addOnCompleteListener(this::onCompleteDisplayProfile);
    }

    private void onCompleteDisplayProfile(@NonNull Task<DocumentSnapshot> documentSnapshotTask) {
        if (documentSnapshotTask.isSuccessful()) {
            DocumentSnapshot document = documentSnapshotTask.getResult();
            if (document != null && document.exists()) {
                String fullname = document.getString("fullname");
                String image = document.getString("image");

                // Hiển thị tên
                tvUsername.setText(fullname != null ? fullname : "Fullname");

                // Tải ảnh từ URL với Picasso
                if (image != null && !image.isEmpty() && !image.equals("1")) {
                    Picasso.get().load(image).into(imvAvatar);
                } else {
                    imvAvatar.setImageResource(R.drawable.ic_noimage);  // Ảnh mặc định
                }
            } else {
                Toast.makeText(getActivity(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            }
        } else {
            String errorMessage = documentSnapshotTask.getException() != null ? documentSnapshotTask.getException().getMessage() : "Unknown error occurred!";
            Toast.makeText(getActivity(), "Error loading data: " + errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
