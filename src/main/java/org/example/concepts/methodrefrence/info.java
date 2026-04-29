package org.example.concepts.methodrefrence;

public class info {


    //1️⃣ Static Method Reference
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


    //2️⃣ Instance Method Reference (of a particular object)

    //class MessagePrinter {
    //    void print(String msg) {
    //        System.out.println(msg);
    //    }
    //}
    //
    //public class Example2 {
    //    public static void main(String[] args) {
    //        MessagePrinter printer = new MessagePrinter();
    //
    //        java.util.function.Consumer<String> c = printer::print;
    //        c.accept("Instance method called!");
    //    }
    //}

    //3️⃣ Instance Method of Arbitrary Object (of a Type)

    //import java.util.Arrays;
    //import java.util.List;
    //
    //public class Example3 {
    //    public static void main(String[] args) {
    //        List<String> names = Arrays.asList("Hari", "Varun", "Ravi");
    //
    //        // Lambda: names.forEach(name -> System.out.println(name));
    //        names.forEach(System.out::println); // 🔹 method reference
    //    }
    //}

    //4️⃣ Constructor Reference

    //import java.util.function.Supplier;
    //
    //class Person {
    //    Person() {
    //        System.out.println("Person object created!");
    //    }
    //}
    //
    //public class Example4 {
    //    public static void main(String[] args) {
    //        Supplier<Person> supplier = Person::new; // Constructor reference
    //        Person p = supplier.get();
    //    }
    //}



}
