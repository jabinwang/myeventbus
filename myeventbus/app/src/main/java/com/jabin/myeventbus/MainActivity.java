package com.jabin.myeventbus;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 163) {
                UserInfo user = (UserInfo) msg.obj;
                tv.setText(user.toString());
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);
//        EventBus.getDefault().addIndex(new EventBusIndex());
        EventBus.getDefault().register(this);
    }

    // 跳转按钮
    public void jump(View view) {
        startActivity(new Intent(this, TestActivity.class));
    }

    // 粘性按钮
    public void sticky(View view) {
        EventBus.getDefault().postSticky(new UserInfo("simon", 35));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void abc(UserInfo user) {
//        tv.setText(user.toString());
//        Log.e("abc", user.toString());
        Message msg = new Message();
        msg.obj = user;
        msg.what = 163;
        handler.sendMessage(msg);
        Log.e("abc", user.toString() + "thread name " + Thread.currentThread().getName());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 1)
    public void abc2(UserInfo user) {
        //tv.setText(user.toString());
        Log.e("abc2", user.toString() + " thread name " + Thread.currentThread().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        EventBus.clearCaches();
    }
}
