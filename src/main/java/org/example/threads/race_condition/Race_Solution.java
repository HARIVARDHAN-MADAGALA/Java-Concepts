package org.example.threads.race_condition;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Race_Solution extends Thread  {

// sol 1  ************** AtomicInteger ***************************
    static AtomicInteger count = new AtomicInteger(0);


    public  void  run (){

        for(int i=0; i < 20000; i++) {
            count.incrementAndGet();
        }
    }

//   sol 2 ************* Synchronized Block ************************************************************
//    static int count = 0;
//
//    public   void  run (){
//
//        for(int i=0; i < 20000; i++) {
//
//            synchronized (Race_Solution.class) {
//                count++;
//            }
//        }
//    }
//  sol 3  ***** Reentrant Lock ***********************************************************
//    static int count = 0;
//    static Lock lock1 = new ReentrantLock(); // should be static or else rach thread have this own lock and gain race condition will occur
//
//    public   void  run (){
//
//        for(int i=0; i < 20000; i++) {
//
//            lock1.lock();
//            try {
//                count++;
//            }
//            finally{
//                lock1.unlock();
//            }
//        }
//    }


    public static void main(String[] args) throws InterruptedException {

        Race_Solution obj1 = new Race_Solution();
        Race_Solution obj2 = new Race_Solution();

        obj1.start();
        obj2.start();

        obj1.join();  /// we give .join to make main thread to wait until .join() thread (obj1,obj2) to finish
        obj2.join();

        System.out.println(count);


    }
}
