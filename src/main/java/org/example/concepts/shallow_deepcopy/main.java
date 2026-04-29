package org.example.concepts.shallow_deepcopy;

public class main {

    public static void main(String[] args) {


        //Shallow
        shallow a = new shallow(20);

        shallow b = a;
        b.setValue(30); // here a.value also changing SHALLOW COPY

        System.out.println(b.getValue());//30

        System.out.println(a.getValue());//30




        //Deep
        Deep c = new Deep(50);

        Deep d = new Deep(c); // from this new keyword the object is created with new refrence.
        d.setValue(60); // here a.value also changing SHALLOW COPY

        System.out.println(c.getValue());//50

        System.out.println(d.getValue());//60


    }
}



//info

// cloneable
//class Address implements Cloneable {
//    String city;
//
//    Address(String city) {
//        this.city = city;
//    }
//
//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        return super.clone(); // normal shallow copy for Address
//    }
//}
//
//class Person implements Cloneable {
//    String name;
//    Address address;
//
//    Person(String name, Address address) {
//        this.name = name;
//        this.address = address;
//    }
//
//    // Deep copy
//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        Person cloned = (Person) super.clone();
//        cloned.address = (Address) address.clone(); // 🔹 deep copy of Address object
//        return cloned;
//    }
//}
//
//public class DeepCopyExample {
//    public static void main(String[] args) throws CloneNotSupportedException {
//        Address address = new Address("Chennai");
//        Person p1 = new Person("Hari", address);
//
//        Person p2 = (Person) p1.clone(); // deep copy
//
//        p2.name = "Varun";
//        p2.address.city = "Hyderabad"; // change city in cloned person
//
//        System.out.println(p1.name + " - " + p1.address.city);
//        System.out.println(p2.name + " - " + p2.address.city);
//    }
//}