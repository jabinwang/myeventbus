package com.jabin.myeventbusref;

import java.lang.reflect.Method;

public class MethodFinder {
    private Object subscriber;
    //subscriber 的参数类型
    private Class<?> type;

    private ThreadMode threadMode;

    private Method method;

    public MethodFinder(Object subscriber, Class<?> type, ThreadMode threadMode, Method method) {
        this.subscriber = subscriber;
        this.type = type;
        this.threadMode = threadMode;
        this.method = method;
    }


    public Object getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Object subscriber) {
        this.subscriber = subscriber;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
