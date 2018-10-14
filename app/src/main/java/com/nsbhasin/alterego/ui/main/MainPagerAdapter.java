package com.nsbhasin.alterego.ui.main;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 3;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ProfileFragment.newInstance();
            case 1:
                return MatchFragment.newInstance();
            case 2:
                return ChatFragment.newInstance();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Profile";
            case 1:
                return "Match";
            case 2:
                return "Chat";
            default:
                return "";
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
