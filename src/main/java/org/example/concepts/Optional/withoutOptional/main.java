package org.example.concepts.Optional.withoutOptional;

import java.util.Optional;

public class main {

    public static void main(String[] args) {

        Person p1 = new Person(null);

        if(p1 != null && p1.getAddress() != null){

            System.out.println("City is available");
        }

        else {
            System.out.println("City is not available");
        }

        /// with using Optional
        Optional.ofNullable(p1)
                .map(p -> p.getAddress())
                .ifPresentOrElse(
                        addr -> System.out.println("City is available"),
                        () -> System.out.println("City is not available")
                );

    }
}
