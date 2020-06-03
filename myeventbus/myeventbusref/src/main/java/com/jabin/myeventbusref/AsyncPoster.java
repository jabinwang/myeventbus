package com.jabin.myeventbusref;

public class AsyncPoster implements Poster, Runnable {

    private final MyEventBus myEventBus;
    private final PendingPostQueue queue;

    AsyncPoster(MyEventBus myEventBus) {
        this.myEventBus = myEventBus;
        this.queue = new PendingPostQueue();
    }

    @Override
    public void enqueue(MethodFinder methodFinder, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(methodFinder, event);
        queue.enqueue(pendingPost);
        myEventBus.getExecutorService().execute(this);
    }

    @Override
    public void run() {
        PendingPost pendingPost = queue.poll();
        myEventBus.invokeSubscriber(pendingPost);
    }
}
