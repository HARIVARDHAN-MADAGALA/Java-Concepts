package org.example.Design_patterns.prototype;

public class Rectangle implements Shape {

    private String color;
    private int width;
    private int height;

    public Rectangle(String color, int width, int height) {
        this.color  = color;
        this.width  = width;
        this.height = height;
    }

    // copy constructor — used by clone()
    private Rectangle(Rectangle source) {
        this.color  = source.color;
        this.width  = source.width;
        this.height = source.height;
    }

    public void setColor(String color)   { this.color  = color;  }
    public void setWidth(int width)      { this.width  = width;  }
    public void setHeight(int height)    { this.height = height; }

    @Override
    public Rectangle clone() {
        return new Rectangle(this);
    }

    @Override
    public void draw() {
        System.out.println("Rectangle -> color=" + color + ", width=" + width + ", height=" + height);
    }
}
