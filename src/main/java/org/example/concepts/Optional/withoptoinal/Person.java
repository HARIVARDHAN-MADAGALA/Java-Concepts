package org.example.concepts.Optional.withoptoinal;

import java.util.Optional;

public class Person {

    Adress adress;

    public Person(Adress adress) {
        this.adress = adress;
    }

    public Optional<Adress> getAdress() {               // here implementation
        return Optional.ofNullable(adress);
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }
}
