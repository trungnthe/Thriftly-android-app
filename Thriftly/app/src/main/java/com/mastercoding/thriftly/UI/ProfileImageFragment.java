package com.mastercoding.thriftly.UI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImageFragment extends Fragment {

    private CircleImageView imgProfile;
    private Button btnChangeImage;
    private Button btnSaveImage;
    private Uri imageUri;

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 101;

    private String[] cameraPermission;
    private String[] storagePermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_image, container, false);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        bindingView(view);
        bindingAction();

        return view;
    }

    private void bindingAction() {
        btnChangeImage.setOnClickListener(this::changeImage);
        btnSaveImage.setOnClickListener(this::saveImage);
    }

    private void saveImage(View view) {
        // Xử lý lưu ảnh đại diện nếu cần
    }

    private void changeImage(View view) {
        CharSequence[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pick Image From");

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Camera
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    takePhotoWithCamera();
                }
            } else if (which == 1) {
                // Gallery
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    selectImageFromGallery();
                }
            }
        });

        builder.show();
    }

    // Kiểm tra quyền truy cập camera
    private boolean checkCameraPermission() {
        boolean cameraAccepted = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean writeStorageAccepted = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return cameraAccepted && writeStorageAccepted;
    }

    // Yêu cầu quyền truy cập camera
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // Kiểm tra quyền truy cập bộ nhớ
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Yêu cầu quyền truy cập bộ nhớ
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void takePhotoWithCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(imgProfile);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                Picasso.get().load(imageUri).into(imgProfile);
                showImageConfirmationDialog();
            }
        }
    }

    private void showImageConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Use this photo?");
        builder.setPositiveButton("Use", (dialog, which) -> {
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted && writeStorageAccepted) {
                    takePhotoWithCamera();
                } else {
                    // Quyền bị từ chối
                }
            }
        } else if (requestCode == STORAGE_REQUEST) {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    selectImageFromGallery();
                } else {
                    // Quyền bị từ chối
                }
            }
        }
    }

    private void bindingView(View view) {
        imgProfile = view.findViewById(R.id.imgProfile);
        btnChangeImage = view.findViewById(R.id.btnChangeImage);
        btnSaveImage = view.findViewById(R.id.btnSaveImage);
    }
}
