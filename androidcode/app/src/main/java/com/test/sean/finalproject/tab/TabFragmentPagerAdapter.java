package com.test.sean.finalproject.tab;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"Login","Signin"};
    private HashMap<Integer,Fragment> hashMap =new HashMap<>();

    public TabFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return createFragment(position);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    private Fragment createFragment(int pos){
        Fragment fragment = hashMap.get(pos);
        if(fragment==null){
            switch (pos){
                case 0:
                    fragment = new Login();
                    break;
                case 1:
                    fragment = new Signin();
                    break;
            }
            hashMap.put(pos,fragment);
        }
        return fragment;
    }
}
