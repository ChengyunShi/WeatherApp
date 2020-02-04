package edu.cshi5131.forecastapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

class SummaryViewPagerAdapter extends FragmentPagerAdapter {

    private List<Bundle> mLocationList;
    private Context context;

    public SummaryViewPagerAdapter(FragmentManager manager, List<Bundle> mList, Context c) {
        super(manager);
        mLocationList = mList;
        context = c;
    }

    @Override
    public Fragment getItem(int position) {
        SummaryFragment fragment = new SummaryFragment();
        fragment.setArguments(mLocationList.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return mLocationList.size();
    }

    public void addTab(Bundle bundle) {
        mLocationList.add(bundle);
        notifyDataSetChanged();
    }

    public void removeTab(int position) {
        if (!mLocationList.isEmpty() && position<mLocationList.size()) {
            Log.i("SummaryViewPagerAdapter", "The removed tab content is " + mLocationList.get(position).toString());
            mLocationList.remove(position);
            notifyDataSetChanged();
        }
    }


}
