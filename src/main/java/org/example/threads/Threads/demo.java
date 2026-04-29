package org.example.threads.Threads;

public class demo extends Thread{

    @Override
    public void run(){
        System.out.println("I am in class of extends Threads");
    }





    public static void main(String[] args) {

        demo obj = new demo();

        obj.start();

    }
}
