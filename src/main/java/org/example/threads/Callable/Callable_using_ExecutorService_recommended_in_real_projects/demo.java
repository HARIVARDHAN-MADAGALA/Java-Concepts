package org.example.threads.Callable.Callable_using_ExecutorService_recommended_in_real_projects;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class demo implements Callable<String> {

    @Override
    public String call(){

        return "10";
    }


    public static void main(String[] args) throws Exception {

        demo obj1 = new demo();

        ExecutorService obj2 = Executors.newSingleThreadExecutor();

        Future<String> obj3 = obj2.submit(obj1);

        String str = obj3.get();

        System.out.println(str);
    }
}
//🧠 What’s happening here
//You submit a Callable
//Executor runs it in a thread
//It returns result
//Result stored in Future
//You fetch it using get()