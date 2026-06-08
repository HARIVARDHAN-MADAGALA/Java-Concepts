package org.example.Design_patterns.prototype;

/// Prototype interface — every shape must be cloneable
public interface Shape {

    Shape clone();

    void draw();
}
