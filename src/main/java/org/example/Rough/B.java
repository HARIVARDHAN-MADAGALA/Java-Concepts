package org.example.Rough;

import java.util.Comparator;
import java.util.Map;

public class B  {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public B(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
