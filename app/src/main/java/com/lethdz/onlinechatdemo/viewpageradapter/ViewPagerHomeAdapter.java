package com.lethdz.onlinechatdemo.viewpageradapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.lethdz.onlinechatdemo.home.FindFriendFragment;
import com.lethdz.onlinechatdemo.home.MessageHomeFragment;

public class ViewPagerHomeAdapter extends ViewPagerAdapter{
    int tabCount;

    public ViewPagerHomeAdapter(FragmentManager fm, int numberOfTab) {
        super(fm, numberOfTab);
        tabCount = numberOfTab;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MessageHomeFragment messageHomeFragment = new MessageHomeFragment();
                return messageHomeFragment;
            case 1:
                FindFriendFragment signUpFragment = new FindFriendFragment();
                return signUpFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return  tabCount;
    }
}
