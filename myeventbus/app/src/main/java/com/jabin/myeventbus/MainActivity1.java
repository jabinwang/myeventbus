package com.jabin.myeventbus;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jabin.myeventbusref.MyEventBus;
import com.jabin.myeventbusref.ThreadMode;
import com.jabin.myeventbusref.annotation.Subscribe;

public class MainActivity1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        MyEventBus.getInstance().register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND) // 默认不填线程
    public void getMessage(EventBean bean) {
        Log.e("EventBus >>1>> ", "thread = " + Thread.currentThread().getName());
        Log.e("EventBus >>1>> ", "" + bean.getName());
    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void getMessage1(EventBean bean) {
        Log.e("EventBus >>1>> ", "thread = " + Thread.currentThread().getName());
        Log.e("EventBus >>1>> ", "" + bean.getName());
    }
    public void click(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
}
