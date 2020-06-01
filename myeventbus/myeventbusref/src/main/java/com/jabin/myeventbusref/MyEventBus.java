package com.jabin.myeventbusref;

import android.os.Handler;
import android.os.Looper;

import com.jabin.myeventbusref.annotation.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyEventBus {
    private static volatile MyEventBus instance;

    private Map<Object , List<MethodFinder>> cacheMap;
    private Handler handler;
    private ExecutorService executorService;

    private MyEventBus(){
        cacheMap = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }

    public static MyEventBus getInstance(){
        if (instance == null) {
            synchronized (MyEventBus.class) {
                if (instance ==null) {
                    instance = new MyEventBus();
                }
            }
        }
        return instance;
    }

    public void register(Object subscriber) {
        List<MethodFinder> methodList = cacheMap.get(subscriber);
        if (methodList == null) {
            methodList = findAnnotationMethod(subscriber);
            cacheMap.put(subscriber, methodList);
        }
    }

    public void post(final Object event){
        Set<Object> set = cacheMap.keySet();
        for (final Object obj : set) {
            List<MethodFinder> methodFinderList = cacheMap.get(obj);
            if (methodFinderList != null && !methodFinderList.isEmpty()){
                for (final MethodFinder methodFinder : methodFinderList) {
                    if (methodFinder.getType().isAssignableFrom(event.getClass())) {
                        switch (methodFinder.getThreadMode()){
                            case MAIN:
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    invoke(methodFinder, obj, event);
                                }else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            invoke(methodFinder, obj, event);
                                        }
                                    });
                                }
                                break;
                            case POSTING:
                                invoke(methodFinder, obj, event);
                                break;
                            case BACKGROUND:
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            invoke(methodFinder, obj, event);
                                        }
                                    });
                                }else {
                                    invoke(methodFinder, obj, event);
                                }
                                break;
                            case ASYNC:

                                break;
                        }
                    }
                }
            }
        }
    }

    private void invoke(MethodFinder methodFinder, Object subscriber, Object event) {
        Method execute = methodFinder.getMethod();

        try {
            execute.invoke(subscriber, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    //从注解中查找方法列表
    private List<MethodFinder> findAnnotationMethod(Object subscriber) {
        List<MethodFinder> methodFinderList = new ArrayList<>();
        Class<?> clazz = subscriber.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        while(clazz != null) {
            String clazzName = clazz.getName();
            if (clazzName.startsWith("java.") || clazzName.startsWith("javax.") ||
            clazzName.startsWith("android.") || clazzName.startsWith("androidx.")){
                break;
            }
            for (Method method : methods) {
                //获取注解
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (subscribe == null) {
                    continue;
                }
                Type returnType = method.getGenericReturnType();
                if (!"void".equals(returnType.toString())) {
                    throw new RuntimeException(method.getName()+ "方法返回必须void");
                }
                Class<?>[] parameterType = method.getParameterTypes();
                if (parameterType.length != 1) {
                    throw new RuntimeException(method.getName()+ "方法只有一个参数");
                }
                MethodFinder methodFinder = new MethodFinder(parameterType[0], subscribe.threadMode(), method);
                methodFinderList.add(methodFinder);
            }
            clazz = clazz.getSuperclass();
        }

        return methodFinderList;
    }
}
