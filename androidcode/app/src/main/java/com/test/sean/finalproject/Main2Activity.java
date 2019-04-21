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

// this is the main part of information activity
public class Main2Activity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener
    , CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
    // these are part of components in item list layout
    private Intent intent;
    private DrawerLayout drawerLayout;
    private NavigationView nav;
    private TextView infTemp,infList,infName;
    private Switch infSwitch;
    private SupportTool support = new SupportTool();
    private String mname,code,fridgeNum,temp;
    private SwipeRefreshLayout infSwipe;

    // onCreate will be called when creating the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // bind layout with this activity
        setContentView(R.layout.activity_inf);
        // find components from layout
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        nav = (NavigationView)findViewById(R.id.navigation);
        ImageView infProtrait = (ImageView)findViewById(R.id.portrait);
        infName = (TextView)findViewById(R.id.inf_name);
        infList = (TextView)findViewById(R.id.inf_listnum);
        infTemp = (TextView)findViewById(R.id.inf_temp);
        infSwitch = (Switch)findViewById(R.id.inf_switch);
        // set swipe listener on SwipeRefreshLayout
        infSwitch.setOnCheckedChangeListener(this);
        infTemp.setOnClickListener(this);

        // it will set a toolbar in the activity since the theme is no action bar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            // show home menu in the toolbar
            actionBar.setDisplayHomeAsUpEnabled(true);
            // set the home menu icon
            actionBar.setHomeAsUpIndicator(R.mipmap.menu_icon);
        }
        // build the sqlite database
        SQLiteDatabase db = Connector.getDatabase();
        // get intent from the last activity
        intent = getIntent();
        // load the picture into circle image
        Glide.with(this).load(R.drawable.port).into(infProtrait);
        // start the initialization to show information
        init();

        // set the default selected item in navigation view
        nav.setCheckedItem(R.id.nav_inf);
        // set click listener on navigation view
        nav.setNavigationItemSelectedListener(this);
        // get the header part rom navigation view
        View headerView = nav.getHeaderView(0);
        // set the image and text in header part
        ImageView iconImg = (ImageView)headerView.findViewById(R.id.icon_img);
        TextView usrName = (TextView)headerView.findViewById(R.id.nav_name);
        usrName.setText(intent.getStringExtra("name"));
        Glide.with(this).load(R.drawable.port).into(iconImg);

    }

    // deal with the click event on home menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    // deal with the click event on navigation view
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //这里编写switch逻辑
        switch (item.getItemId()){
            case R.id.nav_inf:
                // if nav_inf is selected, which is this activity, close the navigation view
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_list:
                // if nav_list is selected, open the other activity
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

    // it is the click listener on switcher and deal with the change of switcher
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // inquire the current state of switcher from database
        Users user = LitePal.where("name=?",intent.getStringExtra("name"))
                .findFirst(Users.class);
        // if the switcher is in true value
        if(buttonView.isChecked()){
            // set the text of temperature information and show message
            user.setIs_show_inf(true);
            infTemp.setVisibility(View.VISIBLE);
            Toast.makeText(this,"show information",Toast.LENGTH_SHORT).show();
            // start a new thread to send a http request
            new Thread(new Runnable() {
                @Override
                public void run() {
                    support.askSendInf(intent.getStringExtra("token"),fridgeNum,true);
                    // sleep for 1 second to wait for the response
                    try{
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
            // if the data in response shown failed
            if(support.getState().equals("error")){
                // display error message
                SEMsg seMsg = support.getSeMsg();
                Toast.makeText(this,seMsg.getTab(),Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // if the switcher has false value
            // set the temperature information to be invisible
            user.setIs_show_inf(false);
            infTemp.setVisibility(View.GONE);
            Toast.makeText(this,"hide information",Toast.LENGTH_SHORT).show();
            // start a new thread to send the http request
            new Thread(new Runnable() {
                @Override
                public void run() {
                    support.askSendInf(intent.getStringExtra("token"),fridgeNum,false);
                    // sleep for 1 second to wait for response
                    try{
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
            // if the response is error, show the error message
            if(support.getState().equals("error")){
                SEMsg seMsg = support.getSeMsg();
                Toast.makeText(this,seMsg.getTab(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    // this is the function to initialize the state of switcher and load the value in user nanme
    // text and picture in user portrait
    private void init(){
        // start a new thread to send the http request
        new Thread(new Runnable() {
            @Override
            public void run() {
                // set the text of username
                mname = "Name: ";
                mname = mname+intent.getStringExtra("name").toUpperCase();
                Users user = LitePal.where("name=?",intent.getStringExtra("name"))
                        .findFirst(Users.class);
                // set the text of item list code
                code = "Item List Code: ";
                code = code + user.getItem_list_num();
                fridgeNum=user.getFridge_num();
                // decide weather to show the temperature information
                // if it is necessary to show information
                if(user.getIs_show_inf()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            infSwitch.setChecked(true);
                            infTemp.setVisibility(View.VISIBLE);
                        }
                    });
                    // send a http request to getinf API route
                    refreshInf();
                }
                // if there is no need to show information
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            infSwitch.setChecked(false);
                            infTemp.setVisibility(View.GONE);
                        }
                    });
                }
                // load the values into elements
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

    // this is a function to ask for information from wen server
    private void refreshInf(){
        // send a http request
        support.askTemp(intent.getStringExtra("token"),fridgeNum);
        // sleep for 1 second waited for the response
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        // get the result from the support intent
        SEMsg seMsg = support.getSeMsg();
        // if success, get the temperature(serialized name) from json file
        if (seMsg.getState().equals("success"))
            temp = seMsg.getListCode();
        else{
            // if failed, report the error message
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Main2Activity.this,"can not get information",Toast.LENGTH_SHORT).show();
                    infSwitch.setChecked(false);
                }
            });
        }
        // load the value of temperature information
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infTemp.setText(temp);
            }
        });
    }

    // deal with click event on temperature text
    // it will refresh the temperature value
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
