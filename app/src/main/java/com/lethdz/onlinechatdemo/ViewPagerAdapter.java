package com.lethdz.onlinechatdemo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
                System.out.println("Hello");
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
