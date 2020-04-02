package com.lethdz.onlinechatdemo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lethdz.onlinechatdemo.dao.FirebaseDAO;
import com.lethdz.onlinechatdemo.modal.User;
import com.lethdz.onlinechatdemo.viewpageradapter.ViewPagerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 777;
    private FirebaseSingleton instance;
    private ViewPager viewPager;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseDAO firebaseDAO;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        progressBar = findViewById(R.id.toolbarprogress);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //Initialize Firebase SingleTon
        instance = FirebaseSingleton.getInstance();
        firebaseDAO = new FirebaseDAO();
        //Setup Pager
        setupTabPager();
    }

    protected void setupTabPager() {
        //Initialize the parameter
        TabLayout signInOption = findViewById(R.id.tl_SignInOption);
        viewPager = findViewById(R.id.viewPager);
        PagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), signInOption.getTabCount());
        //Set Adapter
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(signInOption));
        //Add onSelect Listener
        signInOption.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Common.closeSoftKeyboard(MainActivity.this);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Warning", "Google sign in failed" + e.getMessage(), e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Get Id", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(60, true);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        instance.getmAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success", "signInWithCredential:success");
                            Toast.makeText(MainActivity.this, "Authentication success.", Toast.LENGTH_SHORT)
                                    .show();
                            User currentUser = instance.getCurrentUserInformation();
                            Log.d("success", currentUser.toString());
                            firebaseDAO.registerGoogleAccountToDatabase(currentUser);
                            updateUI(currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("fail", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication fail.", Toast.LENGTH_SHORT)
                                    .show();
                            updateUI(null);
                        }
                        progressBar.setProgress(100, true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        User currentUser = instance.getCurrentUserInformation();
        updateUI(currentUser);
    }

    private void signIn() {
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    private void updateUI(User currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            this.startActivity(intent);
            finish();
        }
    }
}
