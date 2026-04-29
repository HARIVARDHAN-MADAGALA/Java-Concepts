package org.example.Design_patterns.factorypattern;

public class NotifiactionFactory {

    public static Notification getNotifaction(String notification){

        if(notification == null){
            return null;
        }

        switch (notification.toUpperCase()) {
            case "EMAIL":
                return new EmailNotification();

            case "SMS":
                return new SmsNotification();

            case "PUSH":
                return new PushNotification();

            default:
                throw new IllegalArgumentException("Unknown Notification type: " + notification);
        }
    }
}
