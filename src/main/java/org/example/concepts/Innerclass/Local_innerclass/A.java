package org.example.concepts.Innerclass.Local_innerclass;

public class A {

    void methodA() {
        System.out.println("in a A");

        class B {
            void methodB(){
                System.out.println("in a B");
            }
        }

        B obj2 = new B();
        obj2.methodB();
    }

}
