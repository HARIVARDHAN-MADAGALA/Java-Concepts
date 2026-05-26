package org.example.Rough;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public  class A {

    private static A a;

    private A(){};

    public static synchronized A getInstance(){

        if(a == null){
            a = new A();
            return a;
        }
        return a;
    }
}
