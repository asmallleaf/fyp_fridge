package com.test.sean.finalproject.logAndSign;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.test.sean.finalproject.MainActivity;
import com.test.sean.finalproject.R;
import com.test.sean.finalproject.SupportTool;
import com.test.sean.finalproject.models.UserMsg;
import com.test.sean.finalproject.models.Users;
import com.test.sean.finalproject.toolbox.DbTool;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

public class Login extends AppCompatActivity
    implements View.OnClickListener
{
    private EditText name;
    private EditText password;
    private SupportTool support=new SupportTool();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageButton bt_login = (ImageButton) findViewById(R.id.login_button);
        ImageButton bt_signin = (ImageButton)findViewById(R.id.signin2_button);
        name = (EditText)findViewById(R.id.login_name);
        password = (EditText)findViewById(R.id.login_passwd);
        progressBar = (ProgressBar)findViewById(R.id.login_pbar);
        bt_login.setOnClickListener(this);
        bt_signin.setOnClickListener(this);

        SQLiteDatabase db = Connector.getDatabase();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String mname = name.getText().toString();
                        String mpasswd = password.getText().toString();
                        support.askLogin(mname,mpasswd);
                        try {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        if (support.getState().equals("success")){
                            Users users = new Users();
                            users.setToken(support.getToken());
                            support.askGetUser(support.getToken());
                            try{
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            if(support.getState(SupportTool.Instance.UserMsg).equals("error")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Login.this,
                                                "write failed,please check the internet",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                Users user = LitePal.where("name = ? and password = ?",mname,mpasswd)
                                        .findFirst(Users.class);
                                UserMsg userMsg = support.getUserMsg();
                                if (user==null){
                                    users.setName(mname);
                                    users.setPassword(mpasswd);
                                    users.setIs_show_inf(DbTool.str2bool(userMsg.getIsShow()));
                                    users.setItem_list_num(userMsg.getListCode());
                                    users.setFridge_num(userMsg.getFridgeNum());
                                    users.save();
                                }
                                else{
                                    user.setToken(support.getToken());
                                    user.setIs_show_inf(DbTool.str2bool(userMsg.getIsShow()));
                                    user.save();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(Login.this,
                                                "welcome"+mname, Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(Login.this, MainActivity.class);
                                        intent.putExtra("name",mname);
                                        intent.putExtra("token",support.getToken());
                                        startActivity(intent);
                                        Login.this.finish();
                                    }
                                });
                            }
                        }
                        else if(support.getState().equals("error")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login.this,support.getTab(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
                break;
            case R.id.signin2_button:
                Intent intent = new Intent(this,Signin.class);
                Toast.makeText(this,"signin",Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
        }
    }

}
