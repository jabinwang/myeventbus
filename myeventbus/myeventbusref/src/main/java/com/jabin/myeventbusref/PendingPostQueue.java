package com.jabin.myeventbusref;

final class PendingPostQueue {

    private PendingPost head;
    private PendingPost tail;

    synchronized void enqueue(PendingPost pendingPost) {
        if (pendingPost == null) {
            throw new NullPointerException("null cannot be enqueued");
        }

        if (tail != null) {
            tail.next = pendingPost;
            tail = pendingPost;
        }else if (head == null) {
            head = tail = pendingPost;
        }else {
            throw new IllegalStateException("error");
        }

        notifyAll();
    }


    synchronized PendingPost poll() {
        PendingPost pendingPost = head;
        if (head != null) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
        }

        return pendingPost;
    }



    synchronized PendingPost poll(long waitTime) throws InterruptedException{
        if (head == null){
            wait(waitTime);
        }
        return poll();
    }
}
