package org.example.concepts.reflection;

/// A simple class used as the target for all reflection examples
public class Employee {

    public    int    id;
    public    String name;
    private   double salary;   // private — normally not accessible from outside
    protected String department;

    public Employee() {
        this.id         = 1;
        this.name       = "Hari";
        this.salary     = 75000.0;
        this.department = "Engineering";
    }

    public Employee(int id, String name, double salary, String department) {
        this.id         = id;
        this.name       = name;
        this.salary     = salary;
        this.department = department;
    }

    public String getName()   { return name;   }
    public double getSalary() { return salary; }

    private void applyBonus(double percent) {
        this.salary += this.salary * (percent / 100);
        System.out.println("Bonus applied! New salary: " + this.salary);
    }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name=" + name
                + ", salary=" + salary + ", department=" + department + "}";
    }
}
