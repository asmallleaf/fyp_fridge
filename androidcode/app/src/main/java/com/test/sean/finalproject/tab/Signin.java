package com.test.sean.finalproject.tab;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.test.sean.finalproject.R;
import com.test.sean.finalproject.SupportTool;
import com.test.sean.finalproject.models.Users;
import com.test.sean.finalproject.tab.signlogin;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

public class Signin extends Fragment
    implements View.OnClickListener
{
    private EditText name;
    private EditText passwd;
    private EditText passwd2;
    private EditText fridgeCode;
    private SupportTool support=new SupportTool();
    private signlogin mainActivity = (signlogin)getActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_signin,container,false);
        ImageButton button = (ImageButton) v.findViewById(R.id.signin_button);
        name = (EditText)v.findViewById(R.id.signin_name);
        passwd = (EditText)v.findViewById(R.id.signin_passwd);
        passwd2 = (EditText)v.findViewById(R.id.signin_passwd2);
        fridgeCode = (EditText)v.findViewById(R.id.signin_code);
        button.setOnClickListener(this);

        SQLiteDatabase db = Connector.getDatabase();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signin_button:
                String mname = name.getText().toString();
                String mpasswd = passwd.getText().toString();
                String mpasswd2 = passwd2.getText().toString();
                String mcode = fridgeCode.getText().toString();
                support.askSignin(mname,mpasswd,mpasswd2,mcode);
                if (support.getState().equals("success")){
                    Toast.makeText(getContext(),support.getTab(),Toast.LENGTH_SHORT).show();
                    Users user = LitePal.select("name").where("name=?",mname).
                            findFirst(Users.class);
                    if(user == null) {
                        user = new Users();
                    }
                    user.setName(mname);
                    user.setPassword(mpasswd);
                    user.setFridge_num(mcode);
                    user.setItem_list_num(support.getListCode());
                    user.save();
                }
                else if(support.getState().equals("error")){
                    Toast.makeText(getContext(),support.getTab(),Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(getContext(),"error",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
