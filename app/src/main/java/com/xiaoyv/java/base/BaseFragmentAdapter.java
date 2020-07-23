package com.xiaoyv.java.base;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2016 on 2017/12/24.
 */

public class BaseFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> names = new ArrayList<>();

    public BaseFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(String title, Fragment fragment) {
        names.add(title);
        fragments.add(fragment);
    }

    public void clear() {
        fragments.clear();
        names.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return fragments.hashCode();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return names.get(position);
    }
}