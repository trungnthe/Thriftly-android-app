package com.mastercoding.thriftly.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mastercoding.thriftly.Authen.SignInActivity;
import com.mastercoding.thriftly.MainActivity;
import com.mastercoding.thriftly.R;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar, ivCameraIcon, ivEdit;
    private TextView tvFullname, tvEmail, tvPhone, tvLocation;
    private Button buttonSignOut;
    //private LogoutCallback logoutCallback;

    private void bindingView(View view) {
        // Tìm kiếm các view bằng findViewById
        ivAvatar = view.findViewById(R.id.imageViewAvatar);
        ivCameraIcon = view.findViewById(R.id.imageViewCameraIcon);
        ivEdit = view.findViewById(R.id.imageViewEdit);
        tvFullname = view.findViewById(R.id.textViewFullname);
        tvEmail = view.findViewById(R.id.textViewEmail);
        tvPhone = view.findViewById(R.id.textViewPhone);
        tvLocation = view.findViewById(R.id.textViewLocation);
        buttonSignOut = view.findViewById(R.id.buttonSignOut);
    }

//    public interface LogoutCallback {
//        void onLogout();
//    }


    private void bindingAction() {
        buttonSignOut.setOnClickListener(this::onSignOutClicked);
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
        return view;
    }


}