// ==================== INTERFACE ====================
public interface Shape {
double area();
}

// ==================== CIRCLE ====================
public class Circle implements Shape {
private final double radius;

public Circle(double radius) {
    this.radius = radius;
}

public double getRadius() { return radius; }

@Override
public double area() {
    return Math.PI * radius * radius;
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Circle)) return false;
    Circle c = (Circle) o;
    return Double.compare(c.radius, radius) == 0;
}

@Override
public int hashCode() {
    return Double.hashCode(radius);
}

@Override
public String toString() {
    return "Circle[radius=" + radius + "]";
}
}

// ==================== RECTANGLE ====================
public class Rectangle implements Shape {
private final double width;
private final double height;

public Rectangle(double width, double height) {
    this.width = width;
    this.height = height;
}

public double getWidth() { return width; }
public double getHeight() { return height; }

@Override
public double area() {
    return width * height;
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Rectangle)) return false;
    Rectangle r = (Rectangle) o;
    return Double.compare(r.width, width) == 0 &&
           Double.compare(r.height, height) == 0;
}

@Override
public int hashCode() {
    return Double.hashCode(width) + Double.hashCode(height);
}

@Override
public String toString() {
    return "Rectangle[width=" + width + ", height=" + height + "]";
}
}

// ==================== TRIANGLE ====================
public class Triangle implements Shape {
private final double base;
private final double height;

public Triangle(double base, double height) {
    this.base = base;
    this.height = height;
}

public double getBase() { return base; }
public double getHeight() { return height; }

@Override
public double area() {
    return 0.5 * base * height;
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Triangle)) return false;
    Triangle t = (Triangle) o;
    return Double.compare(t.base, base) == 0 &&
           Double.compare(t.height, height) == 0;
}

@Override
public int hashCode() {
    return Double.hashCode(base) + Double.hashCode(height);
}

@Override
public String toString() {
    return "Triangle[base=" + base + ", height=" + height + "]";
}
}

// ==================== MAIN / SERVICE ====================
public class ShapeService {

double getArea(Shape shape) {
    if (shape instanceof Circle) {
        Circle c = (Circle) shape;          // manual cast
        return Math.PI * c.getRadius() * c.getRadius();

    } else if (shape instanceof Rectangle) {
        Rectangle r = (Rectangle) shape;    // manual cast
        return r.getWidth() * r.getHeight();

    } else if (shape instanceof Triangle) {
        Triangle t = (Triangle) shape;      // manual cast
        return 0.5 * t.getBase() * t.getHeight();

    } else {
        throw new IllegalArgumentException("Unknown shape: " + shape);
    }
}

public static void main(String[] args) {
    ShapeService service = new ShapeService();

    Shape circle    = new Circle(5);
    Shape rectangle = new Rectangle(4, 6);
    Shape triangle  = new Triangle(3, 8);

    System.out.println(service.getArea(circle));      // 78.53
    System.out.println(service.getArea(rectangle));   // 24.0
    System.out.println(service.getArea(triangle));    // 12.0
}
}

*********************  AFTER USING JAVA 16,17 Features *******************************

public sealed interface Shape permits Circle, Rectangle, Triangle {}

public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
public record Triangle(double base, double height) implements Shape {}

public class ShapeService {

double getArea(Shape shape) {
    return switch (shape) {
        case Circle c    -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t  -> 0.5 * t.base() * t.height();
    };
}

public static void main(String[] args) {
    ShapeService service = new ShapeService();

    System.out.println(service.getArea(new Circle(5)));           // 78.53
    System.out.println(service.getArea(new Rectangle(4, 6)));     // 24.0
    System.out.println(service.getArea(new Triangle(3, 8)));      // 12.0
}
}

**\~120 lines → \~15 lines.** Same output. Same logic. Zero compromise.
