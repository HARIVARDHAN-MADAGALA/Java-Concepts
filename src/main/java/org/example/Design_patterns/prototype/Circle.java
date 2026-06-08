package org.example.Design_patterns.prototype;

public class Circle implements Shape {

    private String color;
    private int radius;

    public Circle(String color, int radius) {
        this.color = color;
        this.radius = radius;
    }

    // copy constructor — used by clone()
    private Circle(Circle source) {
        this.color  = source.color;
        this.radius = source.radius;
    }

    public void setColor(String color)   { this.color  = color;  }
    public void setRadius(int radius)    { this.radius = radius; }

    @Override
    public Circle clone() {
        return new Circle(this);
    }

    @Override
    public void draw() {
        System.out.println("Circle  -> color=" + color + ", radius=" + radius);
    }
}
