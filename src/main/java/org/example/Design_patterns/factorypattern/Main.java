package org.example.Design_patterns.factorypattern;

public class Main {
    public static void main(String[] args) {

        Notification email = NotifiactionFactory.getNotifaction("Email");
        Notification sms = NotifiactionFactory.getNotifaction("sms");
        Notification push = NotifiactionFactory.getNotifaction("push");


        email.send("from email");
        sms.send("from sms");
        push.send("from push");


    }
}
