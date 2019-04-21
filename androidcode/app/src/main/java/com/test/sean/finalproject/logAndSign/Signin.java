package com.test.sean.finalproject.logAndSign;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.test.sean.finalproject.R;
import com.test.sean.finalproject.SupportTool;
import com.test.sean.finalproject.models.Users;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

// this is the main class of signin activity
public class Signin extends AppCompatActivity
        implements View.OnClickListener
{
    // part of the components in signin layout
    private EditText name;
    private EditText passwd;
    private EditText passwd2;
    private EditText fridgeCode;
    private SupportTool support=new SupportTool();
    private ProgressBar progressBar;

    // onCreate will be called when creating the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // bind the signin layout with this activity
        setContentView(R.layout.activity_signin);
        // find elements in signin layout
        ImageButton button = (ImageButton) findViewById(R.id.signin_button);
        name = (EditText)findViewById(R.id.signin_name);
        passwd = (EditText)findViewById(R.id.signin_passwd);
        passwd2 = (EditText)findViewById(R.id.signin_passwd2);
        fridgeCode = (EditText)findViewById(R.id.signin_code);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        // set click listener on button
        button.setOnClickListener(this);

        // create the database if not existed
        SQLiteDatabase db = Connector.getDatabase();
    }

    // deal with click events
    @Override
    public void onClick(View v) {
        // get the position of click events
        switch (v.getId()){
            // if signin button is clicked
            case R.id.signin_button:
                // set the progress bar appeared to be more user-friendly
                progressBar.setVisibility(View.VISIBLE);
                // since there will be a HTTP request, start a new thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // get string value from the editText
                        String mname = name.getText().toString();
                        String mpasswd = passwd.getText().toString();
                        String mpasswd2 = passwd2.getText().toString();
                        String mcode = fridgeCode.getText().toString();
                        // send a post request to web server of signin route
                        support.askSignin(mname,mpasswd,mpasswd2,mcode);
                        // sleep for 1 second to wait for response
                        try {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        // get the result from support tool
                        String mstate = support.getState();
                        // if the response is successful
                        if (mstate!=null&&mstate.equals("success")){
                            // add the new account to local database
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
                            // switch to main thread to show UI information
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // close the progress bar
                                    progressBar.setVisibility(View.GONE);
                                    // show the success message
                                    Toast.makeText(Signin.this,support.getTab(),Toast.LENGTH_SHORT).show();
                                    // destroy this activity and return to login activity
                                    Signin.this.finish();
                                }
                            });
                        }
                        // if the response is failed
                        else if(mstate!=null&&mstate.equals("error")){
                            // switch to the main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // close the progress bar
                                    progressBar.setVisibility(View.GONE);
                                    // show the error message
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
