package com.test.sean.finalproject.logAndSign;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.Toast;

import com.test.sean.finalproject.R;
import com.test.sean.finalproject.SupportTool;
import com.test.sean.finalproject.models.Users;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

public class Signin extends AppCompatActivity
        implements View.OnClickListener
{
    private EditText name;
    private EditText passwd;
    private EditText passwd2;
    private EditText fridgeCode;
    private SupportTool support=new SupportTool();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ImageButton button = (ImageButton) findViewById(R.id.signin_button);
        name = (EditText)findViewById(R.id.signin_name);
        passwd = (EditText)findViewById(R.id.signin_passwd);
        passwd2 = (EditText)findViewById(R.id.signin_passwd2);
        fridgeCode = (EditText)findViewById(R.id.signin_code);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        button.setOnClickListener(this);

        SQLiteDatabase db = Connector.getDatabase();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signin_button:
                progressBar.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String mname = name.getText().toString();
                        String mpasswd = passwd.getText().toString();
                        String mpasswd2 = passwd2.getText().toString();
                        String mcode = fridgeCode.getText().toString();
                        support.askSignin(mname,mpasswd,mpasswd2,mcode);
                        try {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        String mstate = support.getState();
                        if (mstate!=null&&mstate.equals("success")){
                            Users user = LitePal.select("name").where("name=?",mname).
                                    findFirst(Users.class);
                            if(user == null) {
                                user = new Users();
                            }
                            user.setName(mname);
                            user.setPassword(mpasswd);
                            user.setFridge_num(mcode);
                            user.setItem_list_num(support.getListCode());
                            user.setIs_show_inf(true);
                            user.save();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Signin.this,support.getTab(),Toast.LENGTH_SHORT).show();
                                    Signin.this.finish();
                                }
                            });

                        }
                        else if(mstate!=null&&mstate.equals("error")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Signin.this,support.getTab(),Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).start();

                break;
        }
    }
}
