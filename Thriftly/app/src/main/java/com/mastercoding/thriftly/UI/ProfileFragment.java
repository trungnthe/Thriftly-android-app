package com.mastercoding.thriftly.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mastercoding.thriftly.Authen.ChangePasswordActivity;
import com.mastercoding.thriftly.Authen.SignInActivity;
import com.mastercoding.thriftly.MainActivity;
import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar, ivCameraIcon, ivEdit;
    private TextView tvFullname;
    private TextInputEditText edt_fullname;
    private TextInputEditText edt_username, edt_mail, edt_phone, edt_address;
    private Button btnSignOut, btnSaveProfile;
    private FirebaseUser user;
    private ProfileImageFragment profileImageFragment;
    private Button btnChangePassword;
    //private LogoutCallback logoutCallback;

    private void bindingView(View view) {
        // Tìm kiếm các view bằng findViewById
        ivAvatar = view.findViewById(R.id.imageViewAvatar);
        ivCameraIcon = view.findViewById(R.id.imageViewCameraIcon);
        ivEdit = view.findViewById(R.id.imageViewEdit);
        tvFullname = view.findViewById(R.id.textViewFullname);
        edt_fullname = view.findViewById(R.id.edtFullname);
        edt_username = view.findViewById(R.id.edtUsername);
        edt_mail = view.findViewById(R.id.edtEmail);
        edt_phone = view.findViewById(R.id.edtPhone);
        edt_address = view.findViewById(R.id.edtAddress);
        btnSignOut = view.findViewById(R.id.buttonSignOut);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        profileImageFragment = new ProfileImageFragment();
        btnChangePassword = view.findViewById(R.id.buttonChangePass);
    }

//    public interface LogoutCallback {
//        void onLogout();
//    }


    private void bindingAction() {
        btnSignOut.setOnClickListener(this::onSignOutClicked);
        ivEdit.setOnClickListener(this::onEditClicked);
        btnSaveProfile.setOnClickListener(this::onSaveProfileClicked);
        ivCameraIcon.setOnClickListener(this::onUpdateAvatarClicked);
        btnChangePassword.setOnClickListener(this::onChangePasswordClicked);
    }

    private void onChangePasswordClicked(View view) {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void onUpdateAvatarClicked(View view) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, profileImageFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void onSaveProfileClicked(View view) {
        String fullname = edt_fullname.getText().toString();
        String username = edt_username.getText().toString();
        String phone = edt_phone.getText().toString();
        String address = edt_address.getText().toString();

        if(!validateInput(fullname, username, phone, address)){
            return;
        }

        updateProfile(fullname, username, phone, address);

        btnSaveProfile.setVisibility(View.GONE);
        edt_fullname.setVisibility(View.GONE);
        edt_username.setEnabled(false);
        edt_phone.setEnabled(false);
        edt_address.setEnabled(false);

    }

    private void updateProfile(String fullname, String username, String phone, String address) {
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

        String userId = user.getUid();
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("fullname", fullname);
        userUpdates.put("username", username);
        userUpdates.put("phone", phone);
        userUpdates.put("address", address);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").document(userId)
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    replaceFragment();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Lỗi khi cập nhật thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private boolean validateInput(String fullname, String username, String phone, String address) {

        if (fullname.isEmpty()) {
            edt_fullname.setError("Fullname is required");
            edt_fullname.requestFocus();
            return false;
        }
        if (username.isEmpty()) {
            edt_username.setError("Username is required");
            edt_username.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            edt_phone.setError("Phone is required");
            edt_phone.requestFocus();
            return false;
        }
        if (phone.length() != 10) {
            edt_phone.setError("Phone number should be exactly 10 digits");
            edt_phone.requestFocus();
            return false;
        }

        if (!phone.matches("\\d+")) {
            edt_phone.setError("Phone number should contain only digits");
            edt_phone.requestFocus();
            return false;
        }

        if (!phone.startsWith("0")) {
            edt_phone.setError("Phone number should start with 0");
            edt_phone.requestFocus();
            return false;
        }
        if (address.isEmpty()) {
            edt_address.setError("Address is required");
            edt_address.requestFocus();
            return false;
        }


        return true;
    }

    private void onEditClicked(View view) {
        btnSaveProfile.setVisibility(View.VISIBLE);
        edt_fullname.setVisibility(View.VISIBLE);
        edt_username.setEnabled(true);
        edt_phone.setEnabled(true);
        edt_address.setEnabled(true);
        edt_fullname.setEnabled(true);
    }

    private void onSignOutClicked(View view) {
        logout();
    }

    private void logout() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        bindingView(view);
        bindingAction();
        displayProfile();
        return view;
    }

    private void displayProfile() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        String email = document.getString("email");
                        String phone = document.getString("phone");
                        String username = document.getString("username");
                        String image = document.getString("image");
                        String address = document.getString("address");
                        String fullname = document.getString("fullname");

                        edt_mail.setText(email != null ? email : "Email");
                        edt_phone.setText(phone != null ? phone : "Phone");
                        edt_username.setText(username != null ? username : "Username");
                        edt_address.setText(address != null ? address : "Address");
                        edt_fullname.setText(fullname != null ? fullname : "Fullname");
                        tvFullname.setText(fullname != null ? fullname : "Fullname");

                        if (image != null && !image.isEmpty() && !image.equals("1")) {
                            Picasso.get().load(image).into(ivAvatar);
                        } else {
                            ivAvatar.setImageResource(R.drawable.ic_noimage); // Thiết lập ảnh mặc định nếu không có ảnh
                        }
                    } else {
                        Log.d("FirebaseUserId", "User ID: " + user.getUid());
                        Toast.makeText(getActivity(), "Failed to load user information!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred!";
                    Toast.makeText(getActivity(), "Error loading data: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void replaceFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new ProfileFragment());  // Thay R.id.fragment_container bằng ID container của Fragment trong layout của bạn
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}