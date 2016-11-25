package com.example.a52374.myapplication.avtivity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;

import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

public class AddfriendActivity extends AppCompatActivity {
    private EditText et;
    private  String account;
    private  ArrayList<String> accounts;
    private ActionBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
          initbar();
        et= (EditText) findViewById(R.id.addet1);
        accounts=new ArrayList<>();
    }

    private void initbar() {
        bar=getSupportActionBar();
        bar.setTitle("");
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void click(View view) {
        accounts.clear();
        account=et.getText().toString();
        accounts.add(account);
        //从服务器端获取好友信息
        NIMClient.getService(UserService.class).fetchUserInfo(accounts)
                .setCallback(  new RequestCallback<List<NimUserInfo>>() {
                    @Override
                    public void onSuccess(List<NimUserInfo> nimUserInfos) {
                       if(nimUserInfos.size()>0){
                        NimUserInfo user=nimUserInfos.get(0);

                        Intent i=new Intent(AddfriendActivity.this,PersondataActivity.class);
                        i.putExtra("user",user);
                        startActivity(i);}
                        else {
                           Toast.makeText(AddfriendActivity.this,"操作失败",Toast.LENGTH_SHORT).show();
                       }
                    }

                    @Override
                    public void onFailed(int i) {
                        Log.i("tmd","!!!!!!!!");
                        Toast.makeText(AddfriendActivity.this,"操作失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Log.i("tmd","..............");
                    }

                }    );

    }
}
