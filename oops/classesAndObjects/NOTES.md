# Classes & Objects

This concept doesn't have its own package in the actual code — every other package (`Inheritance`, `constructorChaining`, etc.) already uses classes and objects everywhere. This note explains the base concept itself, with a small example.

## Example

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

## What's going on, in plain words

**Class** = a blueprint. It just describes what a car *has* (`make`, `model`, `speed`) and what it *can do* (`accelerate()`). Writing the class doesn't create any actual car — no memory is used for a real car's fields until you make one.

**Object** = an actual car, made with `new Car(...)`. Every time you write `new Car(...)`, Java allocates a fresh chunk of memory holding its own `make`, `model`, `speed`. `car1` and `car2` are two separate objects — separate memory, separate state.

That's why calling `car1.accelerate(30)` only changes `car1`'s speed. `car2`'s speed stays whatever it was. Each object is independent.

## `this` keyword

Inside the constructor: `this.make = make;`. The parameter is also called `make`, so without `this`, writing `make = make;` would just assign the parameter to itself and do nothing useful. `this.make` means "the field on the object being built right now," which fixes the naming clash.

## `static` — the one thing that ISN'T per-object

`static int carCount` doesn't belong to any one car — it belongs to the `Car` class itself. There's exactly one `carCount` in memory total, no matter how many cars you make. Every `new Car(...)` call bumps that single shared number. That's why `Car.getCarCount()` shows the true total across every car ever created, not just one car's count.

Rule of thumb: instance fields = data that's different for each object (a car's own speed). Static fields = data shared by the whole class (how many cars exist in total).

## The takeaway
- Class = blueprint, no memory for its fields until instantiated.
- Object = actual instance, made with `new`, has its own independent copy of every instance field.
- `this` refers to the specific object currently running the code.
- `static` fields/methods belong to the class as a whole, shared by every object, not duplicated per object.
- Every other concept here builds on this: [[Inheritance]] is classes extending classes, [[encapsulation]] is about hiding a class's own data, [[abstraction]] is about hiding a class's implementation behind a simple public shape.
