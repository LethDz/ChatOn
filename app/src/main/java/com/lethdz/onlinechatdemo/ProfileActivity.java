package com.lethdz.onlinechatdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lethdz.onlinechatdemo.dao.FirebaseDAO;
import com.lethdz.onlinechatdemo.modal.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    TextView textTopUsername, textMidUsername, textChangeProfilePicture;
    EditText textChangeUsername, textChangePassword, textChangeEmail;
    private FirebaseSingleton instance;
    User currentUser;
    private FirebaseUser updateUser;
    Button buttonLogOut,buttonSave,buttonCancel;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    Uri downloadFile = null;
    ImageView imageProfilePicture,imageBack;
    ByteArrayOutputStream baos;
    Bitmap bm = null;
    ProgressBar progressBar;
    String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressBar = findViewById(R.id.toolbarProgressProfile);
        textTopUsername = findViewById(R.id.textTopUsername);
        textMidUsername = findViewById(R.id.textMidUsername);
        textChangeUsername = findViewById(R.id.textChangeUsername);
        textChangePassword = findViewById(R.id.textChangePassword);
        textChangeEmail = findViewById(R.id.textChangeEmail);
        textChangeProfilePicture = findViewById(R.id.textChangeProfilePicture);
        buttonLogOut = findViewById(R.id.buttonLogOut);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        imageProfilePicture = findViewById(R.id.imageProfilePicture);
        imageBack = findViewById(R.id.imageBack);
        instance = FirebaseSingleton.getInstance();
        updateUser = instance.getmAuth().getCurrentUser();
        currentUser = instance.getCurrentUserInformation();
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        baos = new ByteArrayOutputStream();

        updateUI(currentUser);

    }

    public void checkRequiredField() {
        if (downloadFile == null || updateUser.getDisplayName().equals("")) {
            if(bm != null && !textChangeUsername.getText().toString().trim().equals("")){
                buttonSave.setVisibility(View.VISIBLE);
                buttonCancel.setVisibility(View.VISIBLE);
            } else if (bm == null || textChangeUsername.getText().toString().trim().equals("")) {
                buttonCancel.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.INVISIBLE);
            }
        } else if (!textChangePassword.getText().toString().equals("")) {
                buttonCancel.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
        } else if (textChangePassword.getText().toString().equals("")) {
            buttonCancel.setVisibility(View.INVISIBLE);
            buttonSave.setVisibility(View.INVISIBLE);
        }
    }

    // ------ Log Out button -----
    public void logOutOnClick(View view) {
        instance.getmAuth().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
    //------- Save button -------
    public void saveOnClick(View view) {
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(60, true);
        if(updateUser.getPhotoUrl() == null || updateUser.getDisplayName().trim().equals("")) {
                //Upload picture to Firebase storage and update the profile picture
                updateProfile(bm);
        }

        //Update password
        String newPassword = textChangePassword.getText().toString();
        if(!newPassword.trim().equals("")){
            updateUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this, "Password changed", Toast.LENGTH_LONG).show();
                    }else{
                        Log.d("Update Profile Fail", task.getException().getMessage());
                        Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setProgress(100, true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }

    }

    //Select a picture to upload from internal storage
    private int REQUEST_CODE = 101;
    public void changePPOnClick(View view){
        Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent,"Select a file"),REQUEST_CODE);
    }

    //temporarily display the image to profile picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                checkRequiredField();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Glide.with(this).load(data.getData()).into(imageProfilePicture);
        }
    }

    //Upload the selected picture on Firebase Storage
    public void updateProfile(Bitmap bm){
        final StorageReference ref = mStorageRef.child("images/" + updateUser.getUid() + "profilePicture");
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] image = baos.toByteArray();
        final UploadTask upload = ref.putBytes(image);
        upload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadTask) {
                if(uploadTask.isSuccessful()) {
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> urlTask) {
                            if (urlTask.getResult() != null) {
                                downloadFile = urlTask.getResult();
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(textChangeUsername.getText().toString())
                                        .setPhotoUri(downloadFile)
                                        .build();

                                //update to model.User
                                updateUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Update profile successfully.", Toast.LENGTH_LONG).show();
                                            FirebaseDAO firebaseDAO = new FirebaseDAO();
                                            currentUser = instance.getCurrentUserInformation();
                                            firebaseDAO.updateProfile(currentUser.getName(), currentUser.getPhotoUrl(), currentUser.getUid());
                                            onBackPressed();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Update profile failed!", Toast.LENGTH_LONG).show();
                                            Log.d("Update Profile Fail", task.getException().getMessage());
                                        }
                                        progressBar.setProgress(100, true);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                                Log.i("NOTE",downloadFile.toString());
                            }
                        }
                    });
                }
            }
        });
    }

    public void updateUI(User user){
        if (user == null) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            finish();
        } else {
            if(updateUser.getPhotoUrl() != null && !updateUser.getDisplayName().trim().equals("")){
                downloadFile = updateUser.getPhotoUrl();
                imageBack.setVisibility(View.VISIBLE);
                textChangeUsername.setEnabled(false);
                textChangeProfilePicture.setVisibility(View.INVISIBLE);
                buttonSave.setVisibility(View.INVISIBLE);
                buttonCancel.setVisibility(View.INVISIBLE);
                buttonLogOut.setVisibility(View.VISIBLE);
                textMidUsername.setText(updateUser.getDisplayName());
                textTopUsername.setText(updateUser.getDisplayName());
                textChangeUsername.setText(updateUser.getDisplayName());
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), downloadFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(this).load(downloadFile).into(imageProfilePicture);
            }
            else{
                buttonSave.setVisibility(View.INVISIBLE);
                buttonCancel.setVisibility(View.INVISIBLE);
                imageBack.setVisibility(View.INVISIBLE);
                buttonLogOut.setVisibility(View.INVISIBLE);
                textTopUsername.setText("Please update profile information");
                textChangePassword.setEnabled(false);
                Toast.makeText(this, "Please enter your username and select profile picture",
                        Toast.LENGTH_SHORT).show();
            }
            textChangeEmail.setText(user.getEmail());
        }

        textChangeUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkRequiredField();
                setTextUserChange(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textChangePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkRequiredField();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setTextUserChange(CharSequence input) {
        if (textChangeUsername.getText().toString().equals("")) {
            textTopUsername.setText("Please update profile information");
            textMidUsername.setText("username");
        } else {
            textTopUsername.setText(input);
            textMidUsername.setText(input);
        }
    }

    //Tap the back icon to get back to home screen.
    public void backOnClick(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public void cancelOnClick(View view){
        finish();
        startActivity(getIntent());
    }

}



