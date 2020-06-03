package com.jabin.myeventbusref;

import java.util.ArrayList;
import java.util.List;

/**
 *flyweight模式，较少对象分配
 */
final class PendingPost {
    private final static List<PendingPost> pendingPostPool = new ArrayList<>();

    Object event;
    MethodFinder methodFinder;
    PendingPost next;

    private PendingPost(Object event, MethodFinder methodFinder) {
        this.event = event;
        this.methodFinder = methodFinder;
    }

    static PendingPost obtainPendingPost(MethodFinder methodFinder, Object event) {
        synchronized (pendingPostPool) {
            int size = pendingPostPool.size();
            if (size > 0) {
                PendingPost pendingPost = pendingPostPool.remove(size - 1);
                pendingPost.event = event;
                pendingPost.methodFinder = methodFinder;
                pendingPost.next = null;
                return pendingPost;
            }
        }
        return new PendingPost(event, methodFinder);
    }

    static void releasePendingPost(PendingPost pendingPost) {
        pendingPost.event = null;
        pendingPost.methodFinder = null;
        pendingPost.next = null;

        synchronized (pendingPostPool) {
            if (pendingPostPool.size() < 10000) {
                pendingPostPool.add(pendingPost);
            }
        }
    }
}
