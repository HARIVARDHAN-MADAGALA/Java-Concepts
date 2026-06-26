package org.example.concepts.immutable_class;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

    public final class Person {
    private final String name;
    private final Date dob;             // mutable type
    private final List<String> roles;   // mutable collection

    public Person(String name, Date dob, List<String> roles) {
        if (name == null || dob == null || roles == null) throw new NullPointerException();
        this.name = name;
        // defensive copy of mutable Date
        this.dob = new Date(dob.getTime());
        // defensive copy and make unmodifiable
        this.roles = Collections.unmodifiableList(new ArrayList<>(roles));
//        this.roles = List.copyOf(roles);  this also fine

    }

    public String getName() {
        return name;
    }

    public Date getDob() {
        // return a defensive copy
        return new Date(dob.getTime());
    }

    public List<String> getRoles() {
        return roles; // already unmodifiable
    }
}

