# Abstract Classes vs Interfaces

You've already met an abstract class (`Shape` in [[abstraction]]) and interfaces (`Herbivore`/`Carnivore`/`Omnivore` in [[interfaces]]). This note puts them side by side so the difference actually sticks, and gives a rule of thumb for picking one over the other.

## Quick recap of each, with the code you've already seen

```java
// abstract class — from your abstraction package
public abstract class Shape {
    abstract double area();       // no body — subclass MUST fill this in
    abstract double perimeter();

    void describe() {             // HAS a body — shared by every subclass for free
        System.out.println("area=" + area() + ", perimeter=" + perimeter());
    }
}
```

```java
// interface — from your interfaces package
public interface Herbivore {
    void eatPlant();   // no body, and CAN'T have one here (unless it's a `default` method)
}
```

## The actual differences

| | Abstract class | Interface |
|---|---|---|
| Keyword | `abstract class` | `interface` |
| A class can have... | only ONE parent (`extends` one abstract class) | MANY interfaces (`implements` A, B, C...) |
| Can hold real fields (state)? | Yes — e.g. `private double radius` in `Circle` came from being a normal class underneath | No real instance fields (only `public static final` constants, rarely used) |
| Can have a constructor? | Yes | No |
| Can have methods WITH a body? | Yes, as many as you want (`describe()` above) | Only `default`/`static` methods (a newer Java feature) — plain methods are body-less |
| Relationship it models | "is-a", usually within one real family of related things | "can-do", a capability that unrelated things can all promise |

The single biggest practical difference: **you only get one `extends`, but you can `implements` as many interfaces as you like.** That single fact drives almost every decision about which one to reach for.

## Why the "one parent only" rule for abstract classes exists

This connects straight back to the diamond problem you already read about in [[interfaces]]. An abstract class can carry real method bodies (actual working code, like `describe()`). If Java let a class inherit from two abstract classes that both had their own working version of the same method, Java wouldn't know which body to use — ambiguous. So Java just disallows it: one class, one parent, full stop.

Interfaces don't have this problem (mostly) because their methods have no body — nothing to be ambiguous about, as your [[interfaces]] notes already cover in detail.

## When to reach for which — the actual decision

**Use an abstract class when:** the subclasses are genuinely the same *kind* of thing, and they share real state or real working code you don't want to copy-paste into every subclass.

Example: if you look at [[Inheritance]]'s `User` class — `Student`, `Mentor`, `TA` all extend it, and `User` was never really meant to be created on its own (would you ever really make a plain generic `new User()` in a real app, or always a specific kind?). If `User` should never be instantiated by itself, that's usually a sign it should have been:
```java
public abstract class User {
    String name;
    int age;
    // shared real fields + maybe some shared logic here

    abstract void performPrimaryAction();  // Student studies, Mentor mentors — different per subtype
}
```
Making it `abstract` would have stopped anyone writing `new User()` directly — enforcing "you must be a specific kind of user" at compile time, not just by convention.

**Use an interface when:** classes that AREN'T naturally related still need to promise the same capability.

Your `Herbivore` example is exactly this: `Dog` and `Human` aren't part of the same specialized family (a `Dog` isn't a specialized `Human`), but both can eat plants. An interface lets you group them by *what they can do*, not by *what they are*.

## Can you use both together? Yes — you already are

```java
public class Dog extends Mammal implements Herbivore {
    public void eatPlant(){ System.out.println("dog is eating a plant"); }
}
```
`Dog` gets its "is-a Mammal" identity from `extends` (one parent, shared real class), AND its "can eat plants" capability from `implements` (as many interfaces as needed). This combo — one abstract/concrete parent class for identity and shared state, plus interfaces for extra capabilities — is extremely common in real Java code.

## The takeaway
- Abstract class: shared identity + shared real code/state, but only one parent allowed.
- Interface: shared capability/contract, no state, but a class can pick up as many as it needs.
- Pick abstract class for "these are all fundamentally the same kind of thing and share real code."
- Pick interface for "these are different kinds of things that all need to be able to do X."
- A single class is free to do both at once: one `extends` (identity) + multiple `implements` (capabilities) — see [[interfaces]] and [[abstraction]] for the pieces this note connects.
