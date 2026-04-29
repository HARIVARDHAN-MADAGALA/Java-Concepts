package org.example.threads.race_condition;


import java.util.concurrent.atomic.AtomicInteger;

public class Race extends Thread  {

    static int count = 0;

    public  void  run (){

        for(int i=0; i < 20000; i++) {
            count++;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Race obj1 = new Race();
        Race obj2 = new Race();

        obj1.start();
        obj2.start();

        obj1.join();
        obj2.join();

       System.out.println(count);


    }
}
