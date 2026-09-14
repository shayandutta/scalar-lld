# Abstraction (Principle of OOP)

## Relate it to real life first

Think about **driving a car**. You press the accelerator pedal, the car speeds up. You turn the steering wheel, the car turns. You have absolutely no idea (and don't need to know) whether it's a gasoline engine, a diesel engine, or an electric motor spinning the wheels underneath — the mechanism could be completely different between two different cars, and from the driver's seat, using them feels identical: pedal, wheel, done. The car manufacturer has decided exactly what a driver needs to interact with (pedals, wheel, gear stick) and hidden every messy internal detail (fuel injection timing, battery voltage regulation, torque curves) behind that simple interface.

That's abstraction: **expose only what something can DO, and hide the messy details of HOW it actually does it.**

## The actual concept, properly explained

Abstraction means designing your classes around the *essential, meaningful operations* something should support, without tying the caller to any specific way those operations get carried out internally. In Java, one of the main tools for doing this is the `abstract class` — a class that can declare methods with **no body at all** (`abstract` methods), forcing every real, concrete subclass to supply its own actual implementation, while still letting the abstract class provide ordinary, fully-working shared methods too.

An `abstract class` cannot be instantiated directly — you can never write `new Shape()` if `Shape` is `abstract`. This makes sense once you think about it: what would `area()` even return for a generic, unspecified "shape"? There's no sensible answer — only concrete shapes like a circle or a rectangle have an actual formula for their area. `abstract` is Java's way of enforcing, at compile time, "this concept only makes sense as a specific kind of itself, never as itself directly."

## The example code

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

## Walking through why this design is genuinely useful

`Shape` says: "I don't know or care how you calculate your area or perimeter — but if you want to call yourself a `Shape`, you absolutely must be able to produce both of those numbers." That's the entire content of the abstract methods `area()` and `perimeter()` — pure requirement, zero implementation.

`Circle` fulfills that requirement using the geometry formula for a circle. `Rectangle` fulfills it using a completely different formula for a rectangle. Two totally different internal calculations — but from the outside, calling `.area()` on either one looks exactly the same.

Now look at `describe()`. It's written ONCE, inside `Shape`, and it works correctly for every current and future subclass, without ever being told what kind of shape it's dealing with. It just calls `area()` and `perimeter()` — trusting that whatever concrete object is actually running this code has properly filled those in. This is abstraction paying off directly: the loop `for (Shape s : shapes) { s.describe(); }` doesn't contain a single `if` statement checking "is this a circle? is this a rectangle?" — it doesn't need to, because it's coded against the *abstraction* (`Shape`), not against any specific concrete implementation.

## Abstract class vs interface — both do abstraction, briefly

This example uses an `abstract class` because `describe()` needed to share real, working code across every subclass. Java also offers `interface` for pure abstraction with zero shared implementation at all (see [[interfaces]] for the full deep-dive, and [[abstractClasses]] for a direct side-by-side comparing exactly when to reach for which one).

## How this is different from encapsulation — a common point of confusion

These two sound similar and are often mixed up, but they solve genuinely different problems:
- **[[encapsulation]]** = hide the **data** — protect a class's internal fields from being changed incorrectly or directly from outside.
- **Abstraction** (this note) = hide the **implementation details of HOW something works** — expose only the essential operations, regardless of how they're carried out underneath.

`Shape` is abstraction: you never need to know the area formula to correctly use any shape. `BankAccount`'s private `balance` field (from [[encapsulation]]) is encapsulation: you're stopped from reaching in and corrupting a number directly. A single well-designed class often uses both principles together — hiding its data (encapsulation) AND exposing a clean, simple set of operations that hide how those operations actually work internally (abstraction).

## Real use cases — why this matters outside toy examples

Almost every extensible piece of software leans on this. A file-reading library might define an abstract `InputSource` with a `readNext()` method — with real implementations for reading from a file, from the network, or from an in-memory buffer, all completely different underneath, but all usable identically by any code written against `InputSource`. A payments system (same example as in [[polymorphism]]) with an abstract `PaymentMethod` and concrete `CreditCard`/`PayPal`/`BankTransfer` implementations is exactly this same pattern — checkout logic never needs to know or care how any specific payment method actually processes a charge.

## Common mistakes to watch for
- Trying to instantiate an abstract class directly (`new Shape()`) — won't compile, by design.
- Forgetting that a subclass of an abstract class MUST implement every abstract method it inherits, or the subclass itself must also be declared `abstract` (deferring the requirement further down the chain).
- Confusing abstraction with encapsulation — abstraction hides *how something works*, encapsulation hides *the data itself*. They usually show up together but are answering different questions.

## The takeaway
- Abstraction = expose a simple, essential set of operations; hide the messy implementation details behind it.
- `abstract class` + `abstract` methods force every concrete subclass to supply its own version of certain behavior, while still allowing the parent to share real, working code for everything else.
- Code written against the abstraction (`Shape`) automatically works correctly with any current or future subclass, without ever needing to know the specifics — this is exactly what makes [[polymorphism]] genuinely useful in real, growing codebases.
- See [[abstractClasses]] for a full breakdown of exactly when to reach for `abstract class` versus `interface` to achieve abstraction.
