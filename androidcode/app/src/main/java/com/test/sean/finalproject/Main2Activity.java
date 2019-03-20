package com.test.sean.finalproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.test.sean.finalproject.models.SEMsg;
import com.test.sean.finalproject.models.Users;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.concurrent.TimeoutException;

public class Main2Activity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener
    , CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
    private Intent intent;
    private DrawerLayout drawerLayout;
    private NavigationView nav;
    private TextView infTemp,infList,infName;
    private Switch infSwitch;
    private SupportTool support = new SupportTool();
    private String mname,code,fridgeNum,temp;
    private SwipeRefreshLayout infSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        nav = (NavigationView)findViewById(R.id.navigation);
        ImageView infProtrait = (ImageView)findViewById(R.id.portrait);
        infName = (TextView)findViewById(R.id.inf_name);
        infList = (TextView)findViewById(R.id.inf_listnum);
        infTemp = (TextView)findViewById(R.id.inf_temp);
        infSwitch = (Switch)findViewById(R.id.inf_switch);
        infSwitch.setOnCheckedChangeListener(this);
        infTemp.setOnClickListener(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.menu_icon);
        }

        SQLiteDatabase db = Connector.getDatabase();

        intent = getIntent();

        Glide.with(this).load(R.drawable.hjpg).into(infProtrait);
        init();

        nav.setCheckedItem(R.id.nav_inf);
        nav.setNavigationItemSelectedListener(this);
        View headerView = nav.getHeaderView(0);
        ImageView iconImg = (ImageView)headerView.findViewById(R.id.icon_img);
        TextView usrName = (TextView)headerView.findViewById(R.id.nav_name);
        usrName.setText(intent.getStringExtra("name"));
        Glide.with(this).load(R.drawable.hjpg).into(iconImg);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //这里编写switch逻辑
        switch (item.getItemId()){
            case R.id.nav_inf:
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_list:
                Intent newIntent = new Intent(Main2Activity.this,MainActivity.class);
                newIntent.putExtra("name",intent.getStringExtra("name"));
                newIntent.putExtra("token",intent.getStringExtra("token"));
                drawerLayout.closeDrawers();
                startActivity(newIntent);
                Main2Activity.this.finish();
                drawerLayout.closeDrawers();
                break;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Users user = LitePal.where("name=?",intent.getStringExtra("name"))
                .findFirst(Users.class);
        if(buttonView.isChecked()){
            user.setIs_show_inf(true);
            infTemp.setVisibility(View.VISIBLE);
            Toast.makeText(this,"show information",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    support.askSendInf(intent.getStringExtra("token"),fridgeNum,true);
                    try{
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
            if(support.getState().equals("error")){
                SEMsg seMsg = support.getSeMsg();
                Toast.makeText(this,seMsg.getTab(),Toast.LENGTH_SHORT).show();
            }
        }
        else {
            user.setIs_show_inf(false);
            infTemp.setVisibility(View.GONE);
            Toast.makeText(this,"hide information",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    support.askSendInf(intent.getStringExtra("token"),fridgeNum,false);
                    try{
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
            if(support.getState().equals("error")){
                SEMsg seMsg = support.getSeMsg();
                Toast.makeText(this,seMsg.getTab(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mname = "Name: ";
                mname = mname+intent.getStringExtra("name").toUpperCase();
                Users user = LitePal.where("name=?",intent.getStringExtra("name"))
                        .findFirst(Users.class);
                code = "Item List Code: ";
                code = code + user.getItem_list_num();
                fridgeNum=user.getFridge_num();
                if(user.getIs_show_inf()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            infSwitch.setChecked(true);
                            infTemp.setVisibility(View.VISIBLE);
                        }
                    });
                    refreshInf();
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            infSwitch.setChecked(false);
                            infTemp.setVisibility(View.GONE);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infName.setText(mname);
                        infList.setText(code);
                    }
                });

            }
        }).start();
    }

    private void refreshInf(){
        support.askTemp(intent.getStringExtra("token"),fridgeNum);
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        SEMsg seMsg = support.getSeMsg();
        if (seMsg.getState().equals("success"))
            temp = seMsg.getListCode();
        else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Main2Activity.this,"can not get information",Toast.LENGTH_SHORT).show();
                    infSwitch.setChecked(false);
                }
            });
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infTemp.setText(temp);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.inf_temp:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshInf();
                    }
                }).start();
        }
    }
}
