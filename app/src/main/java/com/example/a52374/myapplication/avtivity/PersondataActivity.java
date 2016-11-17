package com.example.a52374.myapplication.avtivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a52374.myapplication.R;
import com.example.a52374.myapplication.fragment.fragment_TXL;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
//个人资料页面
public class PersondataActivity extends AppCompatActivity {
    private Button but;
    private ImageView iv;
    private TextView tv1,tv2;
    private Intent  intent;
    private  NimUserInfo user;
    private boolean isMyFriend; //判断是否是你的好友

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
       isMyFriend = NIMClient.getService(FriendService.class).isMyFriend(user.getAccount());
        if(isMyFriend){
          but.setText("删除好友");
        }
    }

    private void initview() {
       iv= (ImageView) findViewById(R.id.pdiv1);
       tv1= (TextView) findViewById(R.id.pdname);
        tv2= (TextView) findViewById(R.id.pdaccount);
        but= (Button) findViewById(R.id.pdbut2);
    }

    public void click(View view) {
       switch (view.getId()){
           case R.id.pdbut1:


               break;
           case R.id.pdbut2:


              if(isMyFriend){
                 // Toast.makeText(this,"对方已经是你的好友了",Toast.LENGTH_SHORT).show();


                  //删除好友
                  NIMClient.getService(FriendService.class).deleteFriend(user.getAccount())
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
                      fragment_TXL.delete(user);
                      isMyFriend=false;
                     but.setText("加为好友");
              }else {
               final VerifyType verifyType = VerifyType.DIRECT_ADD; // 直接加为好友
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
                    isMyFriend=true;
                  but.setText("删除好友");
                  fragment_TXL.change(user.getAccount());
              }
               break;
       }

    }
}
