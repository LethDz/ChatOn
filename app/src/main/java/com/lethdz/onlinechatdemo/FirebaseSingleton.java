package com.lethdz.onlinechatdemo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lethdz.onlinechatdemo.modal.User;

public class FirebaseSingleton {
    // Create an object of SingleObject
    private static FirebaseSingleton instance;
    private FirebaseAuth mAuth;

    // Get the only object available
    public static FirebaseSingleton getInstance() {
        if (instance == null) {
            instance = new FirebaseSingleton();
        }

        return instance;
    }

    // Make the constructor private so that this class cannot be instantiated
    private FirebaseSingleton() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public User getCurrentUserInformation() {
        if (mAuth != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                return new User(user.getDisplayName(), user.getEmail(), user.getPhotoUrl(), user.isEmailVerified());
            }
        }
        return null;
    }
}
