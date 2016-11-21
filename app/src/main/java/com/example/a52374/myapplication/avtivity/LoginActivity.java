package com.example.a52374.myapplication.avtivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.datamanagement.DemoCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.util.prefs.Preferences;

/**
 * Created by 52374 on 2016/11/13.
 */
public class LoginActivity extends Activity {
    private EditText ed1,ed2;
    private String account,password;
    private Context context;
    private boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initview();
    }

    private void initview() {
       ed1= (EditText) findViewById(R.id.lget1);
       ed2= (EditText) findViewById(R.id.lget2);
    }

    public void click(View view) {
        switch (view.getId()){
            case R.id.but_login:
               doLogin();



                break;
        }
    }
     // 登录的方法
    public void doLogin() {
        account=ed1.getText().toString().toLowerCase();
        password=ed2.getText().toString();
        LoginInfo info = new LoginInfo(account,password); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        Log.i("tmd","登陆成功!!!!! ");
                        //DemoCache.setContext(LoginActivity.this);
                        com.example.a52374.myapplication.datamanagement.Preferences.saveUserAccount(account);
                        com.example.a52374.myapplication.datamanagement.Preferences.saveUserToken(password);
                        flag=true;
                        //getApplication().onCreate();
                        //DemoCache.setAccount(account);
                         startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailed(int i) {
                        Log.i("tmd","登陆失败 !!!!"+i);
                        Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Log.i("tmd","登陆异常！！！！！");
                        Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    }
                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }




}
