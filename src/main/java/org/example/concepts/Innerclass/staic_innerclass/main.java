package org.example.concepts.Innerclass.staic_innerclass;

public class main {
    public static void main(String[] args) {

       A obj = new A();
       A.B obj2 = new A.B();

       obj.methodA();
       obj2.methodB();

    }
}


//✅ Does not need an instance of outer class.
//❌ Can only access static members of outer class.
