package com.lethdz.onlinechatdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private FirebaseSingleton instance;
    private FirebaseUser user;
    TextView newPassword, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        instance = FirebaseSingleton.getInstance();
        user = instance.getmAuth().getCurrentUser();
        newPassword = findViewById(R.id.textNewPassword);
        confirmPassword = findViewById(R.id.textConfirmPassword);
    }

    public void saveOnClick(View view){
        String np = newPassword.getText().toString();
        String cp = confirmPassword.getText().toString();
        if(np.equals(cp)&&!np.equals("")){
            user.updatePassword(np);
            Toast.makeText(this, "Change password successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,Profile.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Password not matched", Toast.LENGTH_LONG).show();
        }
    }
}
