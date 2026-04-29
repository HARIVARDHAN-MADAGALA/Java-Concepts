package org.example.threads.Runnable;

public class demo implements Runnable{


    @Override
    public void run() {

        System.out.println("I m in Runnable");
    }


    public static void main(String[] args) {

        demo obj = new demo();
        Thread obj2 = new Thread(obj);

        obj2.start();
    }
}
