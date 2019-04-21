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

// it is the main codes of login activity
public class Login extends AppCompatActivity
    implements View.OnClickListener
{
    // part of the components in login layout
    // the support tool is a implement of tool box
    private EditText name;
    private EditText password;
    private SupportTool support=new SupportTool();
    private ProgressBar progressBar;

    // onCreate will be called when creating the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // bind the layout of login to this activity
        setContentView(R.layout.activity_login);
        // find the elements in the login layout
        ImageButton bt_login = (ImageButton) findViewById(R.id.login_button);
        ImageButton bt_signin = (ImageButton)findViewById(R.id.signin2_button);
        name = (EditText)findViewById(R.id.login_name);
        password = (EditText)findViewById(R.id.login_passwd);
        progressBar = (ProgressBar)findViewById(R.id.login_pbar);
        // set the onCLickListener to listen the Click event on some components
        bt_login.setOnClickListener(this);
        bt_signin.setOnClickListener(this);

        // create the database if not existed
        SQLiteDatabase db = Connector.getDatabase();
    }

    // deal with the click events
    @Override
    public void onClick(View v) {
        // get the position of Click event
        switch (v.getId()){
            // if login button is clicked
            case R.id.login_button:
                // set the progress bar appeared to be more user-friendly
                progressBar.setVisibility(View.VISIBLE);
                // since there will be a HTTP request, start a new thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // get the input user name and password
                        String mname = name.getText().toString();
                        String mpasswd = password.getText().toString();
                        // send a request to login API
                        support.askLogin(mname,mpasswd);
                        // sleep for 1 second to wait for response
                        try {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        // if the response is successful
                        if (support.getState().equals("success")){
                            // send a request to ask for user information to store in local memory
                            support.askGetUser(support.getToken());
                            // sleep for 1 second to wait for response
                            try{
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            // if the response of user information is failed
                            if(support.getState(SupportTool.Instance.UserMsg).equals("error")){
                                // run on main thread to send a UI message
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // show the error message
                                        Toast.makeText(Login.this,
                                                "write failed,please check the internet",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            // if the response of user information is successful
                            else{
                                // write into the database
                                Users user = LitePal.where("name = ? and password = ?",mname,mpasswd)
                                        .findFirst(Users.class);
                                UserMsg userMsg = support.getUserMsg();
                                // if the user is first to use the application on this device
                                // create a new raw in users table
                                if (user==null){
                                    Users users = new Users();
                                    users.setToken(support.getToken());
                                    users.setName(mname);
                                    users.setPassword(mpasswd);
                                    users.setIs_show_inf(DbTool.str2bool(userMsg.getIsShow()));
                                    users.setItem_list_num(userMsg.getListCode());
                                    users.setFridge_num(userMsg.getFridgeNum());
                                    users.save();
                                }
                                // if not, update the user information
                                else{
                                    user.setToken(support.getToken());
                                    user.setIs_show_inf(DbTool.str2bool(userMsg.getIsShow()));
                                    user.save();
                                }
                                // switch to the main thread to show some UI changes
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // close the progress bar
                                        progressBar.setVisibility(View.GONE);
                                        // show success message
                                        Toast.makeText(Login.this,
                                                "welcome"+mname, Toast.LENGTH_SHORT).show();
                                        // transfer a intent to the next activity
                                        Intent intent=new Intent(Login.this, MainActivity.class);
                                        intent.putExtra("name",mname);
                                        intent.putExtra("token",support.getToken());
                                        // start another activity
                                        startActivity(intent);
                                        // destroy this activity
                                        Login.this.finish();
                                    }
                                });
                            }
                        }
                        // if the response of login is failed
                        else if(support.getState().equals("error")){
                            // switch into main thread and show the error message
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
            // if the signin button is clicked
            case R.id.signin2_button:
                // open the sign in activity
                Intent intent = new Intent(this,Signin.class);
                Toast.makeText(this,"signin",Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
        }
    }

}
