package org.example.threads.Callable.Callable_using_FutureTask;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Demo implements Callable<String> {

    @Override
    public String call() {

        return "10";
    }

    public static void main(String[] args) throws Exception{

        Demo obj1 = new Demo();

        // Create Future task object by giving the Demo object
        FutureTask<String> obj2 = new FutureTask<>(obj1);

        // Give that work to thread
        Thread obj3 = new Thread(obj2);

        obj3.start();

        // Seeing output for that task
        String str = obj2.get();

        System.out.println(str);

    }
}
