package org.example.concepts.shallow_deepcopy;

class Address implements Cloneable {
    String city;

    Address(String city) {
        this.city = city;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); // shallow copy of Address
    }
}

class Person implements Cloneable {
    String name;
    Address address;

    Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Person cloned = (Person) super.clone();
        cloned.address = (Address) address.clone(); // deep copy of Address
        return cloned;
    }
}

public class DeepCopyCloneable {
    public static void main(String[] args) throws CloneNotSupportedException {
        Address address = new Address("Chennai");
        Person p1 = new Person("Hari", address);

        Person p2 = (Person) p1.clone(); // deep copy

        p2.name = "Varun";
        p2.address.city = "Hyderabad";

        System.out.println(p1.name + " - " + p1.address.city); // Hari - Chennai
        System.out.println(p2.name + " - " + p2.address.city); // Varun - Hyderabad
    }
}
