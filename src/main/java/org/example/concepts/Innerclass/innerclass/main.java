package org.example.concepts.Innerclass.innerclass;

public class main {
    public static void main(String[] args) {

        A obj = new A();
        A.B obj2 = obj.new B();

        obj.methodA();
        obj2.methodB();

    }
}

//✅ Inner class can access all members (even private) of the outer class.
//❌ Needs an instance of the outer class to be created.
