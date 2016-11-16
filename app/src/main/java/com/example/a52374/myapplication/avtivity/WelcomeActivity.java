package com.example.a52374.myapplication.avtivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.a52374.myapplication.R;

/**
 * Created by 52374 on 2016/11/13.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
       // ActionBar bar=getActionBar();
       // bar.hide();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
