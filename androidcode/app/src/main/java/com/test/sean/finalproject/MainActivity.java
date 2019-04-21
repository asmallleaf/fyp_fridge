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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.test.sean.finalproject.item.Item;
import com.test.sean.finalproject.item.ItemAdapter;
import com.test.sean.finalproject.models.Items;
import com.test.sean.finalproject.models.SEMsg;
import com.test.sean.finalproject.models.Users;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

// this is main part of item list activity
public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener
{
    // these are part of components in item list layout
    private DrawerLayout drawerLayout;
    private NavigationView nav;
    private List<Item> itemList=new ArrayList<>();
    private List<Items> itemsList=new ArrayList<>();
    private ItemAdapter itemAdapter;
    private Item.flag item_flag=Item.flag.num;
    private RecyclerView recyclerView;
    private SupportTool support = new SupportTool();
    private Intent intent;
    private GridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout swipe;

    // onCreate will be called when creating the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // bind layout with this activity
        setContentView(R.layout.activity_main);
        // find components from layout
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe);
        nav = (NavigationView)findViewById(R.id.navigation);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        // set swipe listener on SwipeRefreshLayout
        swipe.setOnRefreshListener(this);

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

        // set the default checked item in navigation view
        nav.setCheckedItem(R.id.nav_list);
        // bind listener on navigation view
        nav.setNavigationItemSelectedListener(this);
        // get the header part in the nacigation view
        View headerView = nav.getHeaderView(0);
        // set the icon image and test in header part
        ImageView iconImg = (ImageView)headerView.findViewById(R.id.icon_img);
        TextView usrName = (TextView)headerView.findViewById(R.id.nav_name);
        usrName.setText(intent.getStringExtra("name"));
        Glide.with(this).load(R.drawable.port).into(iconImg);

        // ask for item list from web server and display them
        initList();

        // load the whole recycle view
        gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

    }

    // it is a function to initialize the whole recycle view
    public void initList(){
        // start a new thread to send the http request
        new Thread(new Runnable() {
            @Override
            public void run() {
                // inquire the item list number from database
                Users user = LitePal.where("name=?",intent.getStringExtra("name"))
                        .findFirst(Users.class);
                // send the http request
                support.askGetList(user.getItem_list_num(),intent.getStringExtra("token"));
                // sleep for 1 second to wait for response
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                // get the item list
                itemsList = support.getItemList();
                // get the SE message
                SEMsg seMsg = support.getSeMsg();
                // get the result of the response
                String state = seMsg.getState();
                // if error, show the error message
                if(state!=null&&state.equals("error")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,seMsg.getTab(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                // if item list is empty, means there is none item stored in the fridge
                // so just initial the itemLIst to be blank
                else if(state==null&&itemsList==null){
                    itemList=new ArrayList<>();
                }
                else {
                    // if the itemList has values, change it into list of Item class
                    itemList.clear();
                    for(Items items:itemsList){
                        Items temp = LitePal.where("itemName=?",items.getItemName())
                                .findFirst(Items.class);
                        if(temp==null){
                            items.save();
                        }
                        Item item = items.toItem();
                        item.setItem_flag(item_flag);
                        itemList.add(item);
                    }
                }
            }
        }).start();
    }

    // it is the click listener for menu icons in tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    // it is used to deal with the click events on menus in tool bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                // if home is selected, open the navigation view
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.number:
                // if the number flag is selected, display number of items
                item_flag = Item.flag.num;
                changeView();
                Toast.makeText(this,"show number information",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tab:
                // if the tab flag is selected, display tab of items
                item_flag = Item.flag.tab;
                changeView();
                Toast.makeText(this,"show tab information",Toast.LENGTH_SHORT).show();
                break;
            case R.id.time:
                // if time flag is selected, display the storage time of items
                item_flag = Item.flag.time;
                changeView();
                Toast.makeText(this,"show save time",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    // this is the click listener on navigation view
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //这里编写switch逻辑
        switch (item.getItemId()){
            case R.id.nav_inf:
                // if nav_inf is selected, the information activity should be opened
                Intent newIntent = new Intent(MainActivity.this,Main2Activity.class);
                newIntent.putExtra("name",intent.getStringExtra("name"));
                newIntent.putExtra("token",intent.getStringExtra("token"));
                drawerLayout.closeDrawers();
                startActivity(newIntent);
                MainActivity.this.finish();
                break;
            case R.id.nav_list:
                // if nav_list is selected, which is this activity, nothing should happen except
                // close the navigation view
                drawerLayout.closeDrawers();
                break;
        }
        return true;
    }


    // this is used to refresh the recycle view
    @Override
    public void onRefresh() {
        // refresh the item list
        refresh();
        // stop refreshing provess
        swipe.setRefreshing(false);
    }

    // this is used to refresh the UI display on screen
    public void refresh(){
        initList();
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
    }

    // this is used to change some of the UI display on screen
    public void changeView(){
        for (Item item:itemList){
            item.setItem_flag(item_flag);
        }
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
    }

}
