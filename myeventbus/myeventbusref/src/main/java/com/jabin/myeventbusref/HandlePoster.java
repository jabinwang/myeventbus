package com.jabin.myeventbusref;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;

public class HandlePoster extends Handler implements Poster {

    private MyEventBus myEventBus;
    private int maxMillisInsideHandleMessage;
    private final PendingPostQueue queue;
    //判断是否已经发送消息了
    private boolean handlerActive;

    protected HandlePoster(MyEventBus myEventBus, Looper looper, int time) {
        super(looper);
        this.myEventBus = myEventBus;
        this.maxMillisInsideHandleMessage = time;
        this.queue = new PendingPostQueue();
    }

    @Override
    public void enqueue(MethodFinder methodFinder, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(methodFinder, event);
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!handlerActive) {
                handlerActive = true;
                sendMessage(obtainMessage());
            }
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean rescheduled = false;
        try {
            long started = SystemClock.uptimeMillis();
            while (true) {
                PendingPost pendingPost = queue.poll();
                if (pendingPost == null) {
                    synchronized (this) {
                        pendingPost = queue.poll();
                        if (pendingPost == null) {
                            handlerActive = false;
                            return;
                        }
                    }
                }
                myEventBus.invokeSubscriber(pendingPost);
                long timeInMethod = SystemClock.uptimeMillis() - started;
                if (timeInMethod >= maxMillisInsideHandleMessage) {
                    if (!sendMessage(obtainMessage())) {
                        throw new IllegalStateException("cannot send mesg");
                    }
                    rescheduled = true;
                    return;
                }
            }
        } finally {
            handlerActive = rescheduled;
        }

    }
}
