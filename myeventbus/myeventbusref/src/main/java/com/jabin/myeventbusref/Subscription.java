package com.jabin.myeventbusref;

public class Subscription {
    final Object subscriber;
    final MethodFinder methodFinder;

    volatile boolean active;

    Subscription(Object subscriber, MethodFinder methodFinder) {
        this.subscriber = subscriber;
        this.methodFinder = methodFinder;
        active = true;
    }


}
