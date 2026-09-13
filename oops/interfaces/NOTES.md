# Interfaces

## The code

```java
// Carnivore.java
public interface Carnivore {
    void eatAnimal();
}

// Herbivore.java
public interface Herbivore {
    void eatPlant();
}

// Omnivore.java — an interface extending TWO interfaces at once
public interface Omnivore extends Herbivore, Carnivore {
}
```

```java
// Mammal.java — plain empty base class, no behavior of its own
public class Mammal {
}
```

```java
// Dog.java
public class Dog extends Mammal implements Herbivore {
    @Override
    public void eatPlant(){
        System.out.println("dog is eating a plant");
    }
}

// Cat.java
public class Cat extends Mammal implements Carnivore {
    public void eatAnimal(){
        System.out.println("Cat is eating animal");
    }
}

// Human.java — implements Omnivore, so it must supply BOTH methods
public class Human extends Mammal implements Omnivore {
    public void eatPlant(){
        System.out.println("human is eating a plant");
    }
    public void eatAnimal(){
        System.out.println("human is eating an animal");
    }
}
```

```java
// Main.java
List<Herbivore> herbivores = List.of(
    new Human(),
    new Dog()
);

for (Herbivore herbivore : herbivores) {
    herbivore.eatPlant();
}
```

## What's going on, in plain words

An interface is a pure contract — it says "anything that claims to be this must have these methods," but it doesn't write the methods itself. `Herbivore` just says: if you're a `Herbivore`, you must have `eatPlant()`. It gives zero implementation. Whoever `implements Herbivore` (`Dog`, `Human`) has to write the actual body themselves.

Compare with a class you `extends` (like `Mammal`) — a class can carry real fields and real method bodies that get inherited as-is. An interface carries no state and (in this code) no bodies at all, only names and signatures.

## Diamond problem — why interfaces dodge it

The classic problem: if a class could inherit from two parent *classes* that both implement the same method differently, the compiler wouldn't know whose version to use when you call it — that's the "diamond problem," and it's the reason Java only allows a class to `extends` one other class.

Interfaces sidestep this. Since (in this code) an interface method has no body at all — just a name and signature — there's nothing to be ambiguous about. When `Human implements Omnivore` (which itself is `Herbivore` + `Carnivore` combined), Java doesn't need to pick "whose `eatPlant()` implementation wins," because neither `Herbivore` nor `Carnivore` brought an implementation in the first place. `Human` is simply required to write its own `eatPlant()` and its own `eatAnimal()` — there's only ever one real implementation, the one the concrete class writes. No ambiguity possible.

*(Side note for later: Java 8+ lets interfaces have `default` methods — methods with an actual body. If two interfaces gave conflicting `default` implementations for the same method and one class implemented both, the diamond problem could resurface — Java forces you to override and manually resolve it in that case. This code doesn't use `default` methods, so it never hits that.)*

## One class, many interfaces — but only one parent class

`Dog extends Mammal implements Herbivore` — one `extends`, one `implements`. But `implements` can list as many interfaces as you want, comma-separated: a class can implement any number of interfaces, while it can only ever extend one class. This is exactly how Java fakes "multiple inheritance" safely — you can't inherit multiple classes' *state/implementation*, but you can promise to fulfill any number of *contracts*.

## Interfaces can extend multiple interfaces too

`Omnivore extends Herbivore, Carnivore` — this only works between interfaces, not classes. `Omnivore` doesn't add a new method of its own; it just merges the two contracts into one bigger contract. Anything that `implements Omnivore` (like `Human`) now must satisfy both `eatPlant()` and `eatAnimal()` — Java checks this at compile time, so `Human` couldn't get away with writing only one of them.

## Why `Cat.eatAnimal()` has to be `public` (the question left in the code)

Every method declared in an interface is implicitly `public abstract`, even though `Carnivore.java` doesn't spell that out — Java adds it silently. When `Cat` implements `Carnivore` and overrides `eatAnimal()`, it's overriding a method that's already `public`. A general Java rule: when you override a method, you're allowed to make its access **wider**, never **narrower**. Since the interface's method is already at the widest level (`public`), the implementing class's version must also be `public` — writing it as default/package access would be narrowing it, which doesn't compile.

## Why `List<Herbivore>` can hold both a `Human` and a `Dog`

`Human` and `Dog` aren't related to each other by class inheritance in any meaningful way (they both just extend the empty `Mammal`), but they both `implement Herbivore`. That shared contract is enough — `List<Herbivore>` only cares that every element can `eatPlant()`, not what class it actually is. The loop calls `herbivore.eatPlant()` once, and gets "dog is eating a plant" / "human is eating a plant" depending on the real object — same runtime-polymorphism idea as [[polymorphism]], just grouped by a shared interface instead of a shared superclass.

## The takeaway
- Interface = contract only (method names/signatures), no state, and (without `default`) no implementation — the implementing class supplies the real body.
- A class can `extends` only one class, but can `implements` any number of interfaces — this is how Java gets the benefits of "multiple inheritance" (many contracts) without the diamond-problem risk of multiple conflicting implementations.
- An interface can `extends` multiple other interfaces, merging their required methods into one bigger contract.
- Overriding can only widen access, never narrow it — since interface methods are always `public`, every implementing class's method must be `public` too.
- Grouping unrelated classes by shared *behaviour* (an interface) rather than shared *ancestry* (a superclass) is the main reason interfaces exist — see [[abstraction]] for the same "expose the what, hide the how" idea applied via `abstract class` instead.
