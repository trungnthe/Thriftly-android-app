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
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mastercoding.thriftly.Authen.SignInActivity;
import com.mastercoding.thriftly.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

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
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    private void bindingView(View view) {
        imgProfile = view.findViewById(R.id.imgProfile);
        btnChangeImage = view.findViewById(R.id.btnChangeImage);
        btnSaveImage = view.findViewById(R.id.btnSaveImage);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_image, container, false);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
                        String image = document.getString("image");
                        if (image != null && !image.isEmpty() && !image.equals("1")) {
                            Picasso.get().load(image).into(imgProfile);
                        } else {
                            imgProfile.setImageResource(R.drawable.ic_noimage); // Thiết lập ảnh mặc định nếu không có ảnh
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

    private void bindingAction() {
        btnChangeImage.setOnClickListener(this::changeImage);
        btnSaveImage.setOnClickListener(this::saveImage);
    }

    private void saveImage(View view) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String filepathname = "avatars/" + firebaseUser.getUid() + ".png";
        StorageReference storageReference1 = storageReference.child(filepathname);

        storageReference1.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());

                final Uri downloadUri = uriTask.getResult();
                if (uriTask.isSuccessful()) {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("image", downloadUri.toString());

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("User").document(firebaseUser.getUid())
                            .update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Toast hoặc hiển thị thông báo thành công
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.popBackStack();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Toast hoặc hiển thị thông báo thất bại
                                }
                            });
                } else {
                    // Toast hoặc thông báo nếu không lấy được URL của ảnh
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast hoặc hiển thị thông báo lỗi khi upload ảnh
            }
        });

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
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Hiển thị một thông báo giải thích và yêu cầu quyền lại
                        showPermissionExplanationDialog(CAMERA_REQUEST);
                    } else {
                        showGoToSettingsDialog();
                    }
                }
            }
        } else if (requestCode == STORAGE_REQUEST) {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    selectImageFromGallery();
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showPermissionExplanationDialog(STORAGE_REQUEST);
                    } else {
                        showGoToSettingsDialog();
                    }
                }
            }
        }
    }

    private void showPermissionExplanationDialog(int requestCode) {
        new AlertDialog.Builder(getContext())
                .setTitle("Permission Required")
                .setMessage("This permission is required to use this feature. Please allow it to continue.")
                .setPositiveButton("OK", (dialog, which) -> {
                    if (requestCode == CAMERA_REQUEST) {
                        requestCameraPermission();
                    } else if (requestCode == STORAGE_REQUEST) {
                        requestStoragePermission();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showGoToSettingsDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Permission Denied")
                .setMessage("You have permanently denied the permission. Please enable it in settings to use this feature.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getActivity().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }



}
