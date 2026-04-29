package org.example.Design_patterns.factorypattern;

public class SmsNotification implements Notification{

    public void send(String message){
        System.out.println(message);
    }
}
