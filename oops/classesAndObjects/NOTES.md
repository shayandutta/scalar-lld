# Classes & Objects

There's no dedicated package for this in the repo — every single other package already uses classes and objects nonstop (`Student st = new Student();` etc). This note zooms into just that one line and explains what's actually happening, from the ground up.

## Relate it to real life first

Think of a **cookie cutter**. The cutter has a shape — say, a star. It's not a cookie. You can't eat it. It just *defines* what shape a cookie made from it will have.

Now you press it into dough, again and again. Each press makes an actual, real, edible cookie. All the cookies share the same star shape (because they came from the same cutter), but each one is a separate physical object — you can eat one and the other three are untouched. Drop one on the floor, the rest are fine.

- **The cutter = the class.** It's the design, the template. Writing the class doesn't create anything you can actually use yet.
- **Each cookie = an object.** A real, individual thing that exists in memory, made *from* the class, with its own separate existence.

This is the single most important idea in all of object-oriented programming, because literally everything else in this repo (`Inheritance`, `polymorphism`, `encapsulation`...) is built on top of "class = template, object = the real thing."

## Now the actual concept

A **class** is a blueprint you write once. It describes two things:
1. **What data does this thing hold?** (called *fields* or *instance variables* — e.g. a car has a `speed`, a `make`, a `model`)
2. **What can this thing do?** (called *methods* — e.g. a car can `accelerate()`)

Writing `class Car { ... }` doesn't make a car exist. No memory gets used for an actual car's speed or model until you do this:

```java
Car myCar = new Car("Honda", "Civic");
```

The `new` keyword is the important part — it's the instruction "go make an actual object from this blueprint, right now, and give me a reference to it." This is called **instantiation** — you're creating an *instance* of the class. "Object" and "instance" mean the same thing in this context.

## The example code

```java
public class Car {
    // instance fields -> every object gets its own copy of these
    String make;
    String model;
    int speed;

    // static field -> shared across ALL objects, lives on the class itself
    static int carCount = 0;

    Car(String make, String model) {
        this.make = make;
        this.model = model;
        this.speed = 0;
        carCount++;
    }

    void accelerate(int by) {
        speed += by;
        System.out.println(make + " " + model + " now at " + speed + " km/h");
    }

    static int getCarCount() {
        return carCount;
    }
}
```

```java
Car car1 = new Car("Honda", "Civic");
Car car2 = new Car("Tesla", "Model3");

car1.accelerate(30);   // car1.speed becomes 30
car2.accelerate(80);   // car2.speed becomes 80, car1 untouched

System.out.println(Car.getCarCount());   // 2, shared by both cars
```

## Walking through what actually happens in memory

1. `Car car1 = new Car("Honda", "Civic");` — Java allocates a fresh block of memory big enough to hold one `Car`'s worth of fields (`make`, `model`, `speed`). It runs the constructor, filling in `make="Honda"`, `model="Civic"`, `speed=0`. The variable `car1` doesn't hold the object itself — it holds a reference (think: an address, a pointer) to where that memory lives.
2. `Car car2 = new Car("Tesla", "Model3");` — a *completely separate* block of memory gets allocated. `car2` points to that different block. `car1` and `car2` don't know about each other and don't share anything (except `carCount`, explained below).
3. `car1.accelerate(30);` — Java goes to whatever `car1` is pointing at, and runs `accelerate` using *that* object's `speed` field. Only `car1`'s copy of `speed` changes. `car2`'s `speed` is a totally different piece of memory — untouched.

That's why the output shows `car1` at 30 and `car2` at 80, never mixed up or summed. Each object is its own island.

## The `this` keyword — why it's needed

Look at the constructor again:
```java
Car(String make, String model) {
    this.make = make;
    ...
}
```
The parameter is called `make`. The field is *also* called `make`. If you just wrote `make = make;`, Java would assign the parameter to itself and the field would never get touched — the object would end up with `make = null`. Writing `this.make` explicitly means "the field belonging to the object being built right now," which resolves the naming clash. `this` always refers to "whichever object is currently running this code" — inside `car1`'s call to `accelerate()`, `this` means `car1`; inside `car2`'s call, `this` means `car2`.

## `static` — the one thing that does NOT belong to any one object

`static int carCount` is different. It doesn't live inside any individual `Car` object — it lives on the `Car` *class* itself, in one single shared spot. Every single `new Car(...)` call, no matter which object it creates, bumps that one shared number. That's why `Car.getCarCount()` — called on the class, not on any specific car — correctly reports the true total across every car ever made.

Rule of thumb: if the data is something like "this specific object's state" (a car's own current speed) → instance field. If the data is "a fact about the whole category, shared by everyone" (how many cars have ever been made) → `static` field.

## Real use cases — why this matters outside of toy examples

Every real application you'll ever build is modeled as classes and objects: a `User` class with objects for each signed-up person, a `Product` class with objects for each item in a store, an `Order` class with objects for each purchase. The class defines the *shape* everything of that kind must have; each object is one actual real-world thing being tracked in the running program. Databases even mirror this: a database table is basically a class (columns = fields), and each row is basically an object.

## Common mistakes to watch for
- Confusing the class with an object: you cannot call `Car.speed` — `speed` doesn't exist until an object is made. You also cannot call `car1.getCarCount()` and expect it to behave differently for `car1` vs `car2` — it's static, it's the same for everyone, calling it through an instance (`car1.getCarCount()`) is legal in Java but misleading, since it doesn't actually belong to `car1`.
- Forgetting `this` when a parameter name matches a field name — leads to silently broken constructors where fields stay at default values (`null`, `0`).

## The takeaway
- Class = blueprint, written once, uses no memory for actual data until instantiated.
- Object = one specific instance, made with `new`, with its own independent copy of every instance field.
- `this` = "the object currently running this code" — used to resolve naming clashes and refer to the current object's own data.
- `static` = shared once across the whole class, not duplicated per object.
- Every other topic here builds directly on this foundation: [[Inheritance]] is classes extending classes, [[encapsulation]] is about controlling access to an object's own data, [[abstraction]] is about hiding a class's implementation behind a simple public shape, and [[polymorphism]] is about one reference type pointing at different real objects.
