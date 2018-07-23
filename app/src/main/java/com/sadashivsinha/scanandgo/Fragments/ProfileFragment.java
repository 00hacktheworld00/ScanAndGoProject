package com.sadashivsinha.scanandgo.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sadashivsinha.scanandgo.Activities.LoginActivity;
import com.sadashivsinha.scanandgo.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by sadashivsinha on 21/07/18.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    EditText editTextName;
    TextInputEditText editTextEmail, editTextContactNo;
    CircleImageView circleImageViewProfile;
    ImageView uploadImage;
    TextView btnSignOut;
    ProgressBar progressBar;

    final int PERMISSION_IMAGE_CODE = 100;
    final int PICK_IMAGE_REQUEST = 101;

    Uri filePath;

    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findViewsByIds(view);
        getFirebaseData();
        return view;
    }

    public void findViewsByIds(View view) {
        editTextName = view.findViewById(R.id.text_name);

        editTextEmail = view.findViewById(R.id.text_email);
        editTextContactNo = view.findViewById(R.id.text_contact_no);

        circleImageViewProfile = view.findViewById(R.id.profile_image);

        uploadImage = view.findViewById(R.id.upload_image_btn);

        btnSignOut = view.findViewById(R.id.btn_sign_out);

        progressBar = view.findViewById(R.id.progress_bar);


        uploadImage.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
    }

    public void getFirebaseData() {
        auth = FirebaseAuth.getInstance();
        editTextName.setText(auth.getCurrentUser().getDisplayName());
        editTextEmail.setText(auth.getCurrentUser().getEmail());
        editTextContactNo.setText(auth.getCurrentUser().getPhoneNumber());

        progressBar.setVisibility(View.VISIBLE);

        Log.d("IMAGE URL : ", String.valueOf(auth.getCurrentUser().getPhotoUrl()));
        Picasso.get().load(auth.getCurrentUser().getPhotoUrl()).into(circleImageViewProfile, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void checkForPermissions() {
        String[] IMAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!LoginActivity.hasPermissions(getContext(), IMAGE_PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), IMAGE_PERMISSIONS, PERMISSION_IMAGE_CODE);
        } else {
            chooseImage();
        }
    }

    public void uploadImage(final Uri profileImageUri) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        if (filePath != null) {

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserProfileChangeRequest.Builder profileChangeRequest;
                                    profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri);

                                    auth.getCurrentUser().updateProfile(profileChangeRequest.build())
                                            .addOnCompleteListener((new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Profile Picture Updated.", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "User details updated.");
                                                    } else {
                                                        Toast.makeText(getView().getContext(), "Something went wrong. Try Again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }));
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(uploadImage)) {
            checkForPermissions();
        } else if (view.equals(btnSignOut)) {
            auth.signOut();
            startActivity(new Intent(view.getContext(), LoginActivity.class));
            getActivity().finish();
            Toast.makeText(view.getContext(), "User logged out.", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_IMAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage();
            } else {
                Toast.makeText(getContext(), "Storage Permissions Denied. Try Again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                circleImageViewProfile.setImageBitmap(bitmap);
                uploadImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
