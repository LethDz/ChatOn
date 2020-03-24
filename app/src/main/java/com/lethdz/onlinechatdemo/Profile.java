package com.lethdz.onlinechatdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Profile extends AppCompatActivity {

    ImageView smallAvatar,largeAvatar;
    EditText editUsername,editEmail;
    TextView changePassword, logOut, changeAvatar;
    Button updateButton;
    private StorageReference mStorageRef;
    private FirebaseSingleton instance;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        smallAvatar = findViewById(R.id.imgSAva);
        largeAvatar = findViewById(R.id.imgLAva);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        changePassword = findViewById(R.id.textChangePassword);
        logOut = findViewById(R.id.textLogOut);
        changeAvatar = findViewById(R.id.textChangeAvater);
        updateButton = findViewById(R.id.buttonUpdate);
        instance = FirebaseSingleton.getInstance();
        user = instance.getmAuth().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        if(user!=null){
            editUsername.setText(user.getDisplayName());
            editEmail.setText(user.getEmail());
            smallAvatar.setImageURI(user.getPhotoUrl());
            largeAvatar.setImageURI(user.getPhotoUrl());
        }else{
            Toast.makeText(this, "Not found", Toast.LENGTH_LONG).show();
        }

    }

    private int REQUEST_CODE = 123;

    public void changeAvatarOnClick(View view){
        Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a file"),REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Uri selectedFile =  data.getData();
            final StorageReference filepath = mStorageRef.child("Images").child(selectedFile.getLastPathSegment());
            filepath.putFile(selectedFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest
                                    .Builder().setPhotoUri(uri).build();
                            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Profile.this, "Profile picture changed", Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile.this, "Upload picture failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void updateOnClick(View view){
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(editUsername.getText().toString())
                .build();
        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Profile.this, "Update profile successfully.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void changePasswordOnClick(View view){
        Intent intent = new Intent(this,ChangePassword.class);
        startActivity(intent);
    }

    public void logOutOnClick(View view){
        try{
            instance.getmAuth().signOut();
            Toast.makeText(this, "Sign out successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }catch(Exception e){
            Toast.makeText(this, "Sign out failed", Toast.LENGTH_LONG).show();
        }
    }


}
