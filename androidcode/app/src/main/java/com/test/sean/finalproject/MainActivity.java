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
import java.util.Random;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener
{
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe);
        nav = (NavigationView)findViewById(R.id.navigation);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        swipe.setOnRefreshListener(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.menu_icon);
        }

        SQLiteDatabase db = Connector.getDatabase();
        intent = getIntent();

        nav.setCheckedItem(R.id.nav_list);
        nav.setNavigationItemSelectedListener(this);
        View headerView = nav.getHeaderView(0);
        ImageView iconImg = (ImageView)headerView.findViewById(R.id.icon_img);
        TextView usrName = (TextView)headerView.findViewById(R.id.nav_name);
        usrName.setText(intent.getStringExtra("name"));
        Glide.with(this).load(R.drawable.hjpg).into(iconImg);

        initList();

        gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

    }


    public void initList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Users user = LitePal.select("item_list_num").
                        where("name=?",intent.getStringExtra("name"))
                        .findFirst(Users.class);
                support.askGetList(user.getItem_list_num(),intent.getStringExtra("token"));
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                itemsList = support.getItemList();
                SEMsg seMsg = support.getSeMsg();
                String state = seMsg.getState();
                if(state!=null&&state.equals("error")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,seMsg.getTab(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(state==null&&itemsList==null){
                    itemList=new ArrayList<>();
                }
                else {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.number:
                item_flag = Item.flag.num;
                changeView();
                Toast.makeText(this,"show number information",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tab:
                item_flag = Item.flag.tab;
                changeView();
                Toast.makeText(this,"show tab information",Toast.LENGTH_SHORT).show();
                break;
            case R.id.time:
                item_flag = Item.flag.time;
                changeView();
                Toast.makeText(this,"show save time",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //这里编写switch逻辑
        switch (item.getItemId()){
            case R.id.nav_inf:
                Intent newIntent = new Intent(MainActivity.this,Main2Activity.class);
                newIntent.putExtra("name",intent.getStringExtra("name"));
                newIntent.putExtra("token",intent.getStringExtra("token"));
                drawerLayout.closeDrawers();
                startActivity(newIntent);
                MainActivity.this.finish();
                break;
            case R.id.nav_list:
                drawerLayout.closeDrawers();
                break;
        }
        return true;
    }



    @Override
    public void onRefresh() {
        refresh();
        swipe.setRefreshing(false);
    }

    public void refresh(){
        initList();
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
    }

    public void changeView(){
        for (Item item:itemList){
            item.setItem_flag(item_flag);
        }
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
    }

}
