package org.example.Design_patterns.Singleton;

public class Singleton {

    private static Singleton instance;

    private Singleton(){}

    public static Singleton getinstance(){

        if(instance == null){

            instance = new Singleton();
        }

        return instance;
    }
}
