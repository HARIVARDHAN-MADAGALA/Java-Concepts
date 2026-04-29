package org.example.Design_patterns.Singleton;

public class useSingleton {


public static void main(String[] args) {

    Singleton obj = Singleton.getinstance();
    Singleton obj2 = Singleton.getinstance();

    System.out.println(obj.equals(obj2));
    System.out.println(obj == obj2);

}
}