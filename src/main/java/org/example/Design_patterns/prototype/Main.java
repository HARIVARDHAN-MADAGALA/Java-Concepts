package org.example.Design_patterns.prototype;

/// Prototype Pattern — Clone objects instead of creating from scratch

public class Main {

    public static void main(String[] args) {

        // ── Original objects
        Circle    originalCircle    = new Circle("Red", 10);
        Rectangle originalRectangle = new Rectangle("Blue", 20, 30);

        System.out.println("── Originals ──");
        originalCircle.draw();
        originalRectangle.draw();

        // ── Cloned objects — no 'new' with constructor args, just clone()
        Circle    clonedCircle    = originalCircle.clone();
        Rectangle clonedRectangle = originalRectangle.clone();

        System.out.println("\n── Clones (before modification) ──");
        clonedCircle.draw();
        clonedRectangle.draw();

        // ── Modify clones — originals are NOT affected
        clonedCircle.setColor("Green");
        clonedCircle.setRadius(50);

        clonedRectangle.setColor("Yellow");
        clonedRectangle.setWidth(100);

        System.out.println("\n── Clones (after modification) ──");
        clonedCircle.draw();
        clonedRectangle.draw();

        System.out.println("\n── Originals unchanged ──");
        originalCircle.draw();
        originalRectangle.draw();

        // ── Verify they are different objects
        System.out.println("\n── Same reference? ──");
        System.out.println("originalCircle == clonedCircle : " + (originalCircle == clonedCircle));
    }
}
