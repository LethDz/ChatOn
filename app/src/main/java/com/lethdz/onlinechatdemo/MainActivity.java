package com.lethdz.onlinechatdemo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.lethdz.onlinechatdemo.modal.User;
import com.lethdz.onlinechatdemo.viewpageradapter.ViewPagerAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.View;

public class MainActivity extends AppCompatActivity {
    private FirebaseSingleton instance;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Initialize Firebase SingleTon
        instance = FirebaseSingleton.getInstance();
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
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        User currentUser = instance.getCurrentUserInformation();
        updateUI(currentUser);
    }

    private void updateUI(User currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            this.startActivity(intent);
            finish();
        }
    }
}
