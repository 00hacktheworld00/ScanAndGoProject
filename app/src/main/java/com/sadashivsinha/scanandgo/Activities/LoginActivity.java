package com.sadashivsinha.scanandgo.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sadashivsinha.scanandgo.R;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    final String TAG = getClass().getName();

    final int PERMISSION_ALL_CODE = 100;
    final int PERMISSION_IMAGE_CODE = 101;
    final int PERMISSION_CAMERA_CODE = 102;

    final int SMS_PERMISSIONS = 1, CAMERA_PERMISSION = 2, IMAGE_PERMISSION = 3;

    private final int PICK_IMAGE_REQUEST = 102;

    final int LAYOUT_MAIN = 1, LAYOUT_OTP = 2, LAYOUT_PROFILE = 3;

    Uri filePath, firebaseImageUri = null;

    BroadcastReceiver receiver;

    String mVerificationId;

    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;

    PhoneAuthProvider.ForceResendingToken mResendToken;

    RelativeLayout mainLayout, otpLayout, profileLayout;
    Button continueBtn, doneBtn, saveBtn;
    EditText mobileNumberInputEt;
    EditText otpTextOne, otpTextTwo, otpTextThree, otpTextFour, otpTextFive, otpTextSix;
    TextView resendOtpText, changeNumberText, otpTitleText;
    TextInputEditText textInputName, textInputContactNo, textInputEmail;
    ImageView editImageBtn;
    CircleImageView profileImage;

    RelativeLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setStatusBarColor();
        setupFirebase();
        setUpBroadcastReceiver();
        findViewsByIds();
        setupListeners();
    }

    public void setupFirebase() {

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (auth.getCurrentUser() != null)
            checkForPermissions(CAMERA_PERMISSION);
    }

    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorBackgroundLogin));
        }
    }

    public void setUpBroadcastReceiver() {

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase("otp")) {
                    final String message = intent.getStringExtra("message");

                    String otp = message.replaceAll("\\D+", "");

                    Log.d("OTP ", otp);

                    if (otp.length() == 6) {
                        otpTextOne.setText(String.valueOf(otp.charAt(0)));
                        otpTextTwo.setText(String.valueOf(otp.charAt(1)));
                        otpTextThree.setText(String.valueOf(otp.charAt(2)));
                        otpTextFour.setText(String.valueOf(otp.charAt(3)));
                        otpTextFive.setText(String.valueOf(otp.charAt(4)));
                        otpTextSix.setText(String.valueOf(otp.charAt(5)));
                    }
                }
            }
        };
    }

    public void findViewsByIds() {
        mainLayout = findViewById(R.id.layout_main);
        otpLayout = findViewById(R.id.layout_otp);
        profileLayout = findViewById(R.id.layout_profile);

        mobileNumberInputEt = findViewById(R.id.text_mobile_number);

        otpTitleText = findViewById(R.id.title_otp);
        resendOtpText = findViewById(R.id.text_resend_otp);
        changeNumberText = findViewById(R.id.text_change_number);

        editImageBtn = findViewById(R.id.upload_image_btn);

        loadingLayout = findViewById(R.id.layout_loading);

        otpTextOne = findViewById(R.id.edittext_otp_one);
        otpTextTwo = findViewById(R.id.edittext_otp_two);
        otpTextThree = findViewById(R.id.edittext_otp_three);
        otpTextFour = findViewById(R.id.edittext_otp_four);
        otpTextFive = findViewById(R.id.edittext_otp_five);
        otpTextSix = findViewById(R.id.edittext_otp_six);

        profileImage = findViewById(R.id.profile_image);

        textInputName = findViewById(R.id.text_name);
        textInputEmail = findViewById(R.id.text_email);
        textInputContactNo = findViewById(R.id.text_contact_no);

        continueBtn = findViewById(R.id.btn_continue);
        doneBtn = findViewById(R.id.btn_done);
        saveBtn = findViewById(R.id.btn_save);
    }

    public void setupListeners() {
        continueBtn.setOnClickListener(this);
        resendOtpText.setOnClickListener(this);
        doneBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        changeNumberText.setOnClickListener(this);
        editImageBtn.setOnClickListener(this);
    }

    public void loginUsingMobileNumber(String number) {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks
                = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);


                loadingScreen(false);
                Log.d("Success Credentials :", credential.toString());
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                Toast.makeText(LoginActivity.this, "Error.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                loadingScreen(false);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);

                mVerificationId = verificationId;
                mResendToken = token;

                navigateLayout(LAYOUT_OTP);
                loadingScreen(false);
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void signInWithPhoneAndOtp(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        loadingScreen(false);

                        if (task.isSuccessful()) {
                            if (task.getResult().getUser().getEmail() == null) {
                                navigateLayout(LAYOUT_PROFILE);
                            } else {
                                checkForPermissions(CAMERA_PERMISSION);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void moveToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void loadingScreen(Boolean isLoading) {
        if (isLoading) {
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            loadingLayout.setVisibility(View.GONE);
        }

        continueBtn.setEnabled(!isLoading);
        doneBtn.setEnabled(!isLoading);
        otpTextOne.setEnabled(!isLoading);
        otpTextTwo.setEnabled(!isLoading);
        otpTextThree.setEnabled(!isLoading);
        otpTextFour.setEnabled(!isLoading);
        otpTextFive.setEnabled(!isLoading);
        otpTextSix.setEnabled(!isLoading);
    }

    public void navigateLayout(int whichLayout) {
        switch (whichLayout) {
            case LAYOUT_OTP:
                otpLayout.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                break;

            case LAYOUT_PROFILE:
                profileLayout.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
                otpLayout.setVisibility(View.GONE);
                break;

            default:
                mainLayout.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                otpLayout.setVisibility(View.GONE);
                break;
        }

    }

    public void checkForPermissions(int whichPermission) {

        switch (whichPermission) {

            case IMAGE_PERMISSION:
                String[] IMAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};

                if (!hasPermissions(this, IMAGE_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, IMAGE_PERMISSIONS, PERMISSION_IMAGE_CODE);
                } else {
                    chooseImage();
                }
                break;

            case CAMERA_PERMISSION:
                String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA};

                if (!hasPermissions(this, CAMERA_PERMISSION)) {
                    ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, PERMISSION_CAMERA_CODE);
                } else {
                    moveToMainActivity();
                }
                break;

            case SMS_PERMISSIONS:
                String[] SMS_PERMISSIONS = {Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS};

                if (!hasPermissions(this, SMS_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, SMS_PERMISSIONS, PERMISSION_ALL_CODE);
                } else {
                    otpTitleText.setText("Enter the OTP sent on " + "+91" + mobileNumberInputEt.getText().toString());
                    loadingScreen(true);
                    loginUsingMobileNumber("+91" + mobileNumberInputEt.getText().toString());
                }
                break;
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_ALL_CODE) {
            Boolean permissionsDenied = false;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    permissionsDenied = true;
            }
            if (permissionsDenied) {
                Toast.makeText(this, "SMS Permissions Denied. OTP will not get entered automatically.", Toast.LENGTH_LONG).show();

            }

            loadingScreen(true);
            loginUsingMobileNumber("+91" + mobileNumberInputEt.getText().toString());
        } else if (requestCode == PERMISSION_IMAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage();
            } else {
                Toast.makeText(this, "Storage Permissions Denied. Try Again.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                moveToMainActivity();
            } else {
                Toast.makeText(this, "Camera Permissions Denied. Try Again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {

        if (view.equals(continueBtn)) {
            if (mobileNumberInputEt.getText().toString().isEmpty() ||
                    mobileNumberInputEt.getText().toString().length() < 10) {
                mobileNumberInputEt.setError("Please enter correct mobile number.");
            } else {
                checkForPermissions(SMS_PERMISSIONS);
            }
        } else if (view.equals(doneBtn)) {
            textInputContactNo.setText("+91" + mobileNumberInputEt.getText().toString());
            if (otpTextOne.getText().toString().isEmpty() ||
                    otpTextTwo.getText().toString().isEmpty() ||
                    otpTextThree.getText().toString().isEmpty() ||
                    otpTextFour.getText().toString().isEmpty() ||
                    otpTextFive.getText().toString().isEmpty() ||
                    otpTextSix.getText().toString().isEmpty()) {

                Toast.makeText(this, "Incorrect OTP Format. Try again.", Toast.LENGTH_SHORT).show();
            } else {
                loadingScreen(true);
                String otp = otpTextOne.getText().toString() +
                        otpTextTwo.getText().toString() +
                        otpTextThree.getText().toString() +
                        otpTextFour.getText().toString() +
                        otpTextFive.getText().toString() +
                        otpTextSix.getText().toString();

                Log.d("OTP :", otp);

                if (otp.length() != 6) {
                    Toast.makeText(this, "Incorrect OTP Format. Try again.", Toast.LENGTH_SHORT).show();
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                    signInWithPhoneAndOtp(credential);
                }
            }
        } else if (view.equals(saveBtn)) {
            if (textInputName.getText().toString().isEmpty())
                Toast.makeText(this, "Enter valid name.", Toast.LENGTH_SHORT).show();

            else if (textInputEmail.getText().toString().isEmpty())
                Toast.makeText(this, "Enter valid email id.", Toast.LENGTH_SHORT).show();

            else {
                loadingScreen(true);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.updateEmail(textInputEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");
                                    if (firebaseImageUri == null)
                                        updateProfileDetails(user, textInputName.getText().toString(), false, firebaseImageUri);
                                    else
                                        updateProfileDetails(user, textInputName.getText().toString(), true, firebaseImageUri);
                                } else {
                                    loadingScreen(true);
                                    Toast.makeText(LoginActivity.this, "Not Successful", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        } else if (view.equals(editImageBtn)) {
            checkForPermissions(IMAGE_PERMISSION);
        } else if (view.equals(resendOtpText)) {
            //resend otp code
        } else if (view.equals(changeNumberText)) {
            navigateLayout(LAYOUT_MAIN);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void updateProfileDetails(FirebaseUser user, String profileName, Boolean imageUpload, Uri profileImageUri) {
        UserProfileChangeRequest.Builder profileChangeRequest;

        if (imageUpload) {
            profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(profileName)
                    .setPhotoUri(profileImageUri);
        } else {
            profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(profileName);
        }

        user.updateProfile(profileChangeRequest.build())
                .addOnCompleteListener((new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingScreen(false);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User details updated.");
                            checkForPermissions(CAMERA_PERMISSION);
                        } else {
                            Toast.makeText(LoginActivity.this, "Something went wrong. Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    public void uploadImage(Uri filePath) {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    firebaseImageUri = uri;
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImage.setImageBitmap(bitmap);
                uploadImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

}
