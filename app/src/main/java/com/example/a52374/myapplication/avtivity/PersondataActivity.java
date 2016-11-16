package com.example.a52374.myapplication.avtivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a52374.myapplication.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

public class PersondataActivity extends AppCompatActivity {
    private ImageView iv;
    private TextView tv1,tv2;
    private Intent  intent;
    private  NimUserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persondata);
        initview();
        intent=getIntent();
        initdata();

    }

    private void initdata() {
         user= (NimUserInfo) intent.getSerializableExtra("user");
       tv1.setText(user.getName());
        tv2.setText("账号:"+user.getAccount());

    }

    private void initview() {
       iv= (ImageView) findViewById(R.id.pdiv1);
       tv1= (TextView) findViewById(R.id.pdname);
        tv2= (TextView) findViewById(R.id.pdaccount);
    }

    public void click(View view) {
       switch (view.getId()){
           case R.id.pdbut1:
               break;
           case R.id.pdbut2:
               final VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
               String msg = "好友请求附言";
               NIMClient.getService(FriendService.class).addFriend(new AddFriendData(user.getAccount(), verifyType, msg))
                        .setCallback(new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                        }

                            @Override
                          public void onFailed(int i) {
                            }

                            @Override
                            public void onException(Throwable throwable) {

                            }
                        });
               break;
       }

    }
}
