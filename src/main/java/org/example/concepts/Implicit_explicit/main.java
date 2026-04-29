package org.example.concepts.Implicit_explicit;

public class main {
    public static void main(String[] args) {

        int i= 10;
        double j = i; // implicit converstion(a.k.a. Type Conversion or Type Promotion)

        long k = 100;
        int l = (int) k;  // explicit converstion , type casting

    }
}




//info

//Parent p = new Child(); // upcasting



//class Parent {
//    void show() {
//        System.out.println("Parent show()");
//    }
//}
//
//class Child extends Parent {
//    void greet() {
//        System.out.println("Child greet()");
//    }
//}
//
//public class Demo {
//    public static void main(String[] args) {
//        Parent p = new Child();   // ✅ Upcasting (implicit)
//        p.show();                 // works
//        // p.greet();             // ❌ Not allowed (Parent reference doesn’t know Child methods)
//
//        // 🔽 Downcasting
//        Child c = (Child) p;      // ✅ Explicit downcasting
//        c.greet();                // ✅ Works
//    }
//}