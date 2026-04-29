package org.example.concepts.Innerclass.Anonymous_innerclass;

public class main {
    public static void main(String[] args) {

        A obj = new A(){

            @Override
            void greet(){

                System.out.println("greeting from anonymous inner class");
            }



        };


        obj.greet();
    }
}

