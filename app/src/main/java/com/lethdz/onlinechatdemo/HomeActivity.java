package com.lethdz.onlinechatdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.lethdz.onlinechatdemo.modal.User;
import com.lethdz.onlinechatdemo.viewpageradapter.ViewPagerHomeAdapter;

public class HomeActivity extends AppCompatActivity {
    private FirebaseSingleton instance;
    private ViewPager viewPagerHome;
    private ImageView btnAction;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = this;
        //Initialize Firebase SingleTon
        instance = FirebaseSingleton.getInstance();
        //Setup Pager
        setupTabPager();
        //Setup Action Menu
        setupActionMenu();
    }

    private void setupActionMenu() {
        btnAction = findViewById(R.id.btn_actionMenu);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(HomeActivity.this, btnAction);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.profile_popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle() == getString(R.string.action_signOut)) {
                            instance.getmAuth().signOut();
                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            HomeActivity.this.startActivity(intent);
                            finish();
                        } else if (item.getTitle() == getString(R.string.action_profile)) {
                            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                            HomeActivity.this.startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });
    }

    private void setupTabPager() {
        //Initialize the parameter
        TabLayout homeOption = findViewById(R.id.tl_HomeOption);
        viewPagerHome = findViewById(R.id.viewPagerHome);
        PagerAdapter adapter = new ViewPagerHomeAdapter(getSupportFragmentManager(), homeOption.getTabCount());
        //Set Adapter
        viewPagerHome.setAdapter(adapter);
        viewPagerHome.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(homeOption) {

        });
        //Add onSelect Listener
        homeOption.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerHome.setCurrentItem(tab.getPosition());
                Common.closeSoftKeyboard(HomeActivity.this);
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
        if (currentUser == null) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            finish();
        } else {
            if (currentUser.getPhotoUrl() == null || currentUser.getName().trim().equals("")) {
                Intent intent = new Intent(this, ProfileActivity.class);
                this.startActivity(intent);
                finish();
            } else {
                ImageView btnAction = findViewById(R.id.btn_actionMenu);
                Glide.with(this).load(currentUser.getPhotoUrl()).into(btnAction);
                TextView txtName = findViewById(R.id.txt_home_displayName);
                txtName.setText(currentUser.getEmail());
            }
        }
    }
}
