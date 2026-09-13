# Abstraction (Principle of OOP)

This concept doesn't have its own package in the actual code either. This note explains it with a small example.

## Example

```java
// abstract class -> can't do "new Shape()" directly
public abstract class Shape {

    // abstract method -> no body, every real shape MUST fill this in
    abstract double area();
    abstract double perimeter();

    // concrete method -> shared logic that doesn't care HOW area/perimeter
    // are actually calculated, just that they exist
    void describe() {
        System.out.println("area=" + area() + ", perimeter=" + perimeter());
    }
}
```

```java
public class Circle extends Shape {
    private double radius;
    Circle(double radius) { this.radius = radius; }

    double area() { return Math.PI * radius * radius; }
    double perimeter() { return 2 * Math.PI * radius; }
}

public class Rectangle extends Shape {
    private double length, breadth;
    Rectangle(double length, double breadth) {
        this.length = length;
        this.breadth = breadth;
    }

    double area() { return length * breadth; }
    double perimeter() { return 2 * (length + breadth); }
}
```

```java
Shape[] shapes = { new Circle(5), new Rectangle(4, 6) };
for (Shape s : shapes) {
    s.describe();   // don't care if it's a circle or rectangle
}
```

## What's going on, in plain words

Abstraction means: show *what* something does, hide *how* it does it. `Shape` says "every shape must be able to give me an area and a perimeter" — it never says how. `Circle` computes area with `Math.PI * radius * radius`, `Rectangle` computes it with `length * breadth` — completely different formulas, but from outside, both are just "a `Shape` that has an `area()`."

The loop `for (Shape s : shapes) { s.describe(); }` doesn't know or care which formula ran. It just knows every `Shape` can `describe()` itself. That's abstraction — dealing with the *idea* of a shape, not the specific details of each kind.

## Abstract class vs interface (both do abstraction)

This example uses an **abstract class** (`abstract class Shape`) — good when subclasses share some real code too (`describe()` is written once, reused by everyone). Java also has **interfaces**, which describe *only* what a class must do, with no shared code at all (until `default` methods, a newer addition). Interfaces are used when unrelated classes need to promise the same behavior without sharing an inheritance tree — e.g. both a `Duck` and an `RC Car` could implement a `Movable` interface even though they're nothing alike otherwise. This example doesn't use one, but it's worth knowing both exist for the same purpose.

## How this is different from encapsulation

They sound similar but solve different problems:
- **Encapsulation** ([[encapsulation]]) = hide the *data* (protect it from being changed wrongly).
- **Abstraction** = hide the *implementation details* (the how), expose only the essential behavior (the what).

`Shape` is abstraction: you don't need to know the area formula to use a shape. `BankAccount`'s private `balance` is encapsulation: you can't reach in and corrupt the number directly.

## The takeaway
- Abstraction = expose a simple, essential interface; hide the messy implementation behind it.
- `abstract class` + `abstract` methods force every subclass to provide its own version of certain behavior, while still letting the parent share real code.
- Code written against the abstraction (`Shape`) works with any current or future subclass, without ever needing to know the specifics — this is also what makes [[polymorphism]] useful in practice.
