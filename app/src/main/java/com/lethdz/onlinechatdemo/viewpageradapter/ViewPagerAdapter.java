package com.lethdz.onlinechatdemo.viewpageradapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lethdz.onlinechatdemo.auth.LoginFragment;
import com.lethdz.onlinechatdemo.auth.SignUpFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int tabCount;

    public ViewPagerAdapter(FragmentManager fm, int numberOfTab) {
        super(fm);
        tabCount = numberOfTab;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LoginFragment loginFragment = new LoginFragment();
                return loginFragment;
            case 1:
                SignUpFragment signUpFragment = new SignUpFragment();
                return signUpFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
