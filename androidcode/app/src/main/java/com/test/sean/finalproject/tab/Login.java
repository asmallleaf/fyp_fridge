package com.test.sean.finalproject.tab;

import android.content.Intent;
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

import com.test.sean.finalproject.MainActivity;
import com.test.sean.finalproject.R;
import com.test.sean.finalproject.SupportTool;
import com.test.sean.finalproject.models.Users;

import org.litepal.crud.LitePalSupport;
import org.litepal.tablemanager.Connector;

public class Login extends Fragment implements View.OnClickListener {
    private EditText name;
    private EditText password;
    private SupportTool support=new SupportTool();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_login,container,false);
        ImageButton button = (ImageButton) v.findViewById(R.id.login_button);
        name = (EditText)v.findViewById(R.id.login_name);
        password = (EditText)v.findViewById(R.id.login_passwd);
        button.setOnClickListener(this);

        SQLiteDatabase db = Connector.getDatabase();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                String mname = name.getText().toString();
                String mpasswd = password.getText().toString();
                support.askLogin(mname,mpasswd);
                if (support.getState().equals("success")){
                    Users users = new Users();
                    users.setToken(support.getToken());
                    users.updateAll("name = ? and password = ?",mname,mpasswd);
                    Intent intent=new Intent(getContext(),MainActivity.class);
                    startActivity(intent);
                }
                else if(support.getState().equals("error")){
                    Toast.makeText(getContext(),support.getTab(),Toast.LENGTH_SHORT).show();
                }
                else {
                }
                break;
        }
    }
}
