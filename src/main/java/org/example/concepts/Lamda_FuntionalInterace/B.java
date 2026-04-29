package org.example.concepts.Lamda_FuntionalInterace;

public class B {

    public static void main(String[] args) {

        A obj = (a,b) -> {
            System.out.println("1");
            System.out.println("2");
        };

//        A obj = new A()  {
//
//            public void method(int a, int b){
//                System.out.println("1");
//                System.out.println("2");}
//        };



        obj.method(3,3);

        /// without lambda

        ImplementedClass obj2 = new ImplementedClass();

        obj2.method(3,3);

    }
}
