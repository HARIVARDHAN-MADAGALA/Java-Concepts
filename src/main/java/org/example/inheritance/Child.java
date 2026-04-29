package org.example.inheritance;

public class Child extends Parent{

    //static methods cant override

    //System have static out method, static out method is of type PrintStream,
    // and PrintStream having println method

      void greet(){
        System.out.println("Child");
    }

}
