# Design Patterns in Java

Design patterns are reusable solutions to commonly occurring problems in software design.
There are **23 classic design patterns** introduced by the **Gang of Four (GoF)**, grouped into 3 categories.

---

## 1. Creational Patterns (5)
Deal with object creation mechanisms.

| Pattern | Description |
|---|---|
| **Singleton** | Ensures only one instance of a class exists |
| **Factory Method** | Delegates object creation to subclasses |
| **Abstract Factory** | Creates families of related objects |
| **Builder** | Constructs complex objects step by step |
| **Prototype** | Creates objects by cloning an existing instance |

---

## 2. Structural Patterns (7)
Deal with object composition and relationships.

| Pattern | Description |
|---|---|
| **Adapter** | Makes incompatible interfaces work together |
| **Bridge** | Separates abstraction from implementation |
| **Composite** | Treats individual objects and compositions uniformly |
| **Decorator** | Adds behavior to objects dynamically |
| **Facade** | Provides a simplified interface to a subsystem |
| **Flyweight** | Shares common state to support large numbers of objects |
| **Proxy** | Controls access to another object |

---

## 3. Behavioral Patterns (11)
Deal with communication and responsibility between objects.

| Pattern | Description |
|---|---|
| **Chain of Responsibility** | Passes requests along a chain of handlers |
| **Command** | Encapsulates a request as an object |
| **Interpreter** | Defines a grammar and interpreter for a language |
| **Iterator** | Provides a way to sequentially access elements |
| **Mediator** | Reduces direct dependencies between objects |
| **Memento** | Captures and restores an object's state |
| **Observer** | Notifies dependents when an object changes state |
| **State** | Alters object behavior when its state changes |
| **Strategy** | Defines a family of interchangeable algorithms |
| **Template Method** | Defines skeleton of an algorithm in a base class |
| **Visitor** | Adds operations to objects without modifying them |

---

## Summary

| Category | Count |
|---|---|
| Creational | 5 |
| Structural | 7 |
| Behavioral | 11 |
| **Total** | **23** |

---

## Patterns Already Implemented in This Project

| Pattern | Category | Package |
|---|---|---|
| Singleton | Creational | `Design_patterns/Singleton` |
| Factory Method | Creational | `Design_patterns/factorypattern` |
| Builder | Creational | `Design_patterns/builder` |
