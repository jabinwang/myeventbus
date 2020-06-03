package com.jabin.myeventbusref;

public interface Poster {
    void enqueue(MethodFinder methodFinder, Object event);
}
