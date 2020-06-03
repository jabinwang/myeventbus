package com.jabin.myeventbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jabin.myeventbusref.MyEventBus;


public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void eventBus(View view) {
        new Thread() {
            @Override
            public void run() {
                Log.e("EventBus >>2>> ", "thread = " + Thread.currentThread().getName());
                MyEventBus.getInstance().post(new EventBean("EventBus"));
            }
        }.start();

        Log.e("EventBus >>2>> ", "thread = " + Thread.currentThread().getName());
        MyEventBus.getInstance().post(new EventBean("simon"));
        finish();
    }

}
