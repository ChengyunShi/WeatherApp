package edu.cshi5131.forecastapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class ViewPagerAdapter extends FragmentPagerAdapter {

    private Bundle bundle;

    public ViewPagerAdapter(FragmentManager manager, Bundle b) {
        super(manager);
        bundle = b;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            TodayFragment fragment = new TodayFragment();
            fragment.setArguments(bundle);
            return fragment;
        }
        else if(position == 1) {
            WeeklyFragment fragment = new WeeklyFragment();
            fragment.setArguments(bundle);
            return fragment;
        }
        else if(position == 2) {
            PhotosFragment fragment =  new PhotosFragment();
            fragment.setArguments(bundle);
            return fragment;
        }
        else {
            return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "TODAY";
            case 1:
                return "WEEKLY";
            case 2:
                return "PHOTOS";
            default:
                return null;
        }
    }

}