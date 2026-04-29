package org.example.concepts.shallow_deepcopy;

public class Deep {

    private int value;

    public Deep(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    //Deep copy constructor
    public Deep(Deep deep) {
        this.value = deep.value;
    }
}
