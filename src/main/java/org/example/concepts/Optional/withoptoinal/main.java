package org.example.concepts.Optional.withoptoinal;

import java.util.Optional;

public class main {

    public static void main(String[] args) {

        Adress a1 = new Adress("vizag");
        Person p1 = new Person(a1);
        Person p2 = new Person(null);

        String city = p1.getAdress()
                        .map(Adress::getCity)
                        .orElse("City not found");


        System.out.println(city);



    }
}



/// Family of Optional

//
