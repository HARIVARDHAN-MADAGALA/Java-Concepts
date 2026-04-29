package org.example.genrics;

// Generic class with type parameter T
public class Box<T> {

        private T content;

    public void set(T content) {
        this.content = content;
    }

    public T get() {
        return content;
    }
}

  class Test {
    public static void main(String[] args) {
        Box<Integer> intBox = new Box<>();
        intBox.set(123);
        System.out.println(intBox.get()); // 123

        Box<String> strBox = new Box<>();
        strBox.set("Hello");
        System.out.println(strBox.get()); // Hello
    }
}
