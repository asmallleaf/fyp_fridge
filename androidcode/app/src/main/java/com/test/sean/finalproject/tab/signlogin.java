package com.test.sean.finalproject.tab;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.ColorSpace;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.test.sean.finalproject.R;
import com.test.sean.finalproject.tab.TabFragmentPagerAdapter;
import com.test.sean.finalproject.toolbox.FCallback;

import org.litepal.tablemanager.Connector;

import java.math.BigInteger;

public class signlogin extends AppCompatActivity
{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabFragmentPagerAdapter tabAdapter;
    private TabLayout.Tab tab_login;
    private TabLayout.Tab tab_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signlogin);

        initViews();

    }

    private void initViews(){
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tab_login = tabLayout.getTabAt(0);
        tab_sign = tabLayout.getTabAt(1);
        tab_login.select();

    }

}
