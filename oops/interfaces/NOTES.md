# Interfaces

## Relate it to real life first

Think about a **wall power socket**. The socket has a specific standard shape and voltage — that's the "contract." Any device you plug into it — a lamp, a phone charger, a laptop, a toaster — is designed to fit that exact standard. The socket doesn't care AT ALL what's on the other end of the plug, how the device works internally, or what it's actually going to do with the electricity. It only guarantees one thing: "if you fit this shape, you'll get power." Completely unrelated devices — a lamp and a toaster share nothing in common as objects — can both plug into the exact same socket, because they both honor the same *contract*, not because they're the same kind of thing.

That's an interface: a contract that says "anything claiming to fit this description must be able to do X" — without caring what the thing actually is, or how it does X internally.

## The actual concept, properly explained

An `interface` in Java declares method signatures (name + parameters + return type) with **no body at all** — pure requirement, zero implementation. Any class that says `implements SomeInterface` is making a binding promise to the compiler: "I will provide a real, working body for every single method that interface demands." If it doesn't, the code won't compile.

This is different from extending a class. When you `extends` a class, you inherit real fields and real working method bodies — actual reusable code. When you `implements` an interface, you inherit nothing executable at all (in the basic case shown in this code) — only an obligation to write the implementation yourself.

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

## Walking through the diamond problem — why it exists, and why interfaces avoid it

Here's the classic scenario that Java's designers were worried about. Imagine (hypothetically — Java doesn't actually allow this) a class could extend two parent *classes* at once, and both parents had their own, different, real, working implementations of a method with the same name — say both had a working `speak()` method that printed something different. If `Child` extends both and calls `speak()`, which parent's actual code should run? There's no sensible answer — this is called the "diamond problem" (the inheritance diagram, drawn out, looks like a diamond shape). Because of this exact ambiguity risk, Java made a firm rule: **a class can only ever `extends` ONE other class.** No exceptions.

Interfaces are able to sidestep this entirely. Why? Because (in the code shown here, without Java 8's newer `default` methods) an interface method has **no body whatsoever** — it's not "an implementation that might conflict with another implementation," it's just a bare requirement with nothing behind it. When `Human implements Omnivore` (which merges `Herbivore` + `Carnivore`'s requirements), there's no scenario where Java has to pick "whose actual code should run" — because neither `Herbivore` nor `Carnivore` brought any actual code to begin with. `Human` is simply required to write its own `eatPlant()` and its own `eatAnimal()`, from scratch. There's only ever one real implementation in existence: the one the concrete class itself writes. Ambiguity is structurally impossible.

*(Worth knowing for later: Java 8 introduced `default` methods, which let an interface provide an actual method body. If a class implemented two interfaces that both provided conflicting `default` implementations of the same method, the diamond problem could technically resurface — Java handles this by forcing the implementing class to explicitly override that method itself and resolve the conflict by hand, rather than silently picking one. This particular code doesn't use `default` methods, so it never runs into that situation.)*

## One class, many interfaces — but only one parent class, ever

Look closely: `Dog extends Mammal implements Herbivore`. One `extends`, but `implements` is allowed to list as many interfaces as needed, comma-separated (`Omnivore` itself is the example of combining two: `extends Herbivore, Carnivore`). A class can `implements` an unlimited number of interfaces, while it can only ever `extends` a single class. This is precisely how Java gives you the practical benefits people usually want from "multiple inheritance" (being many different things at once, capability-wise) without ever risking the diamond problem — you simply can't inherit multiple conflicting *implementations*, but you can freely promise to fulfill any number of *contracts*.

## Interfaces extending other interfaces

`Omnivore extends Herbivore, Carnivore` — note this only works interface-to-interface, never class-to-class. `Omnivore` doesn't add any new method of its own here; it just merges both existing contracts into one combined, bigger contract. Any class that then says `implements Omnivore` (like `Human`) must now satisfy BOTH `eatPlant()` and `eatAnimal()` — the compiler checks this and would refuse to compile `Human` if it only wrote one of the two methods.

## Why does `Cat.eatAnimal()` have to be declared `public`?

This is a real question left directly in the code as a comment, so it's worth answering properly. Every method declared inside an interface is *implicitly* `public abstract`, whether you write those words or not — `Carnivore.java` just writes `void eatAnimal();`, but Java silently treats it as `public abstract void eatAnimal();` behind the scenes.

Now, general Java rule for overriding ANY method (covered in more depth in [[methodOverriding]]): when you override a method, you're allowed to make its access level **wider**, but never **narrower**, than the original. Since an interface's method is already sitting at the widest possible access level (`public`), any implementing class's version of that method is required to also be `public` — writing it as default/package-private access would be an illegal narrowing, and simply won't compile.

## Why `List<Herbivore>` is allowed to hold both a `Human` and a `Dog`

`Human` and `Dog` have basically no meaningful relationship to each other via class inheritance — sure, they both technically extend the same empty `Mammal`, but that's not what's making this work. What actually makes it work is that both `implement Herbivore`. That shared contract alone is enough: `List<Herbivore>` only requires that every element in it can `eatPlant()` — it genuinely does not care what concrete class each element actually is. The loop's single call `herbivore.eatPlant()` correctly produces "dog is eating a plant" or "human is eating a plant" depending on the real object underneath — this is exactly the same runtime-polymorphism mechanism explained fully in [[polymorphism]], just grouped here by a shared *interface* instead of a shared *superclass*.

## Real use cases — why this matters outside toy examples

Interfaces are how Java code stays flexible and swappable in real systems. A `Comparable` interface lets totally unrelated classes (a `String`, an `Integer`, your own custom `Employee` class) all be sortable using the exact same sorting code, because they all promise a `compareTo()` method. A `Runnable` interface lets you hand any piece of code — regardless of what class it came from — to a thread scheduler, because it only demands a `run()` method exist. In application design, an interface like `PaymentGateway` with a `charge()` method lets you write checkout logic once, against the interface, and freely swap in `StripeGateway`, `PaypalGateway`, or a `FakeGatewayForTesting` — none of them need to be related to each other by inheritance at all, they only need to honor the same contract. This exact idea — depending on the contract, not a specific class — is formalized as the "Dependency Inversion Principle" in [[solidPrinciples]].

## Common mistakes to watch for
- Trying to reduce access when implementing an interface method (writing it without `public`) — won't compile, as explained above.
- Forgetting that a class implementing an interface with MULTIPLE required methods (like `Omnivore`) must implement every single one — missing even one is a compile error.
- Assuming an interface method can carry state/fields the way an abstract class can — interfaces can only hold `public static final` constants, never real per-object instance fields. If your design genuinely needs shared state, that's a sign you might actually want an `abstract class` instead — see [[abstractClasses]] for the full comparison.

## The takeaway
- An interface is a pure contract — method names, parameters, and return types only, with no state and (without `default` methods) no implementation — the implementing class must always supply the real, working body.
- A class can `extends` only one class, but `implements` any number of interfaces — this is how Java achieves the useful parts of "multiple inheritance" (many contracts) while completely avoiding the diamond-problem risk that comes from multiple conflicting real implementations.
- An interface can itself `extends` multiple other interfaces, merging all of their required methods into one bigger combined contract.
- Overriding rules only ever allow widening access, never narrowing it — since interface methods are always implicitly `public`, every implementing class's version must also be `public`.
- Grouping unrelated classes together by a shared *capability* (an interface) rather than shared *ancestry* (a superclass) is the core reason interfaces exist — see [[abstraction]] for the closely related "expose the what, hide the how" idea, achieved there via `abstract class` instead.
