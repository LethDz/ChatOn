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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        textChangeUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(downloadFile!=null && !textChangeUsername.getText().toString().trim().equals("")){
                    buttonSave.setVisibility(View.VISIBLE);
                    buttonCancel.setVisibility(View.VISIBLE);
                }
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
                buttonSave.setVisibility(View.VISIBLE);
                buttonCancel.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        updateUI(currentUser);

    }

    // ------ Log Out button -----
    public void logOutOnClick(View view) {
        instance.getmAuth().signOut();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        ProfileActivity.this.startActivity(intent);
        finish();
    }
    //------- Save button -------
    public void saveOnClick(View view) {
        if(updateUser.getPhotoUrl()==null || updateUser.getDisplayName().trim().equals("")) {
            UserProfileChangeRequest profileChangeRequest;
            //-----No Change to Profile Picture
            if (downloadFile == null) {
                profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(textChangeUsername.getText().toString())
                        .build();
            } else {
                //Upload picture to Firebase storage and update the profile picture
                Log.i("NOTE@@@", downloadFile.toString());
                profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(textChangeUsername.getText().toString())
                        .setPhotoUri(downloadFile)
                        .build();
            }

            //update to model.User
            updateUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Update profile successfully.", Toast.LENGTH_LONG).show();
                        currentUser = instance.getCurrentUserInformation();
                        startActivity(getIntent());
                    }
                }
            });
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
                        Toast.makeText(ProfileActivity.this, "Password update failed", Toast.LENGTH_LONG).show();
                    }
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            Glide.with(this).load(data.getData()).into(imageProfilePicture);
        }
        updateProfilePicture(bm);

    }
    //Upload the selected picture on Firebase Storage
    public void updateProfilePicture(Bitmap bm){
        final StorageReference ref = mStorageRef.child("images/"+"profilePicture");
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
                                if(!textChangeUsername.getText().toString().trim().equals("")){
                                    buttonSave.setVisibility(View.VISIBLE);
                                    buttonCancel.setVisibility(View.VISIBLE);
                                }
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
            if(updateUser.getPhotoUrl()!=null && !updateUser.getDisplayName().trim().equals("")){
                imageBack.setVisibility(View.VISIBLE);
                textChangeUsername.setEnabled(false);
                textChangeProfilePicture.setEnabled(false);
                buttonSave.setVisibility(View.INVISIBLE);
                buttonCancel.setVisibility(View.INVISIBLE);
                buttonLogOut.setVisibility(View.VISIBLE);
                textMidUsername.setText(updateUser.getDisplayName());
                textTopUsername.setText(updateUser.getDisplayName());
                textChangeUsername.setText(updateUser.getDisplayName());
                Glide.with(this).load(updateUser.getPhotoUrl()).into(imageProfilePicture);
            }
            else{
                buttonSave.setVisibility(View.INVISIBLE);
                buttonCancel.setVisibility(View.INVISIBLE);
                imageBack.setVisibility(View.INVISIBLE);
                buttonLogOut.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Please enter your username and select profile picture",
                        Toast.LENGTH_SHORT).show();
            }
            textChangeEmail.setText(user.getEmail());
        }
    }

    //Tap the back icon to get back to home screen.
    public void backOnClick(View view){
        Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
        ProfileActivity.this.startActivity(intent);
        finish();
    }

    public void cancelOnClick(View view){
        startActivity(getIntent());
    }

}



