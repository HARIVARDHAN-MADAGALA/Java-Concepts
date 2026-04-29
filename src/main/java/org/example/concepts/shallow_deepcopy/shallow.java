package org.example.concepts.shallow_deepcopy;

public class shallow {

    private int value;

    shallow(int i){
        this.value = i;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
