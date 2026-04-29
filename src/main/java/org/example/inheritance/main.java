package org.example.inheritance;

public class main {
    public static void main(String[] args) {

        Parent p = new Parent();
        Child c = new Child();
        Parent pc = new Child();

        p.greet();                //Parent
        c.greet();                //Child
        pc.greet();               //Child

    }
}
