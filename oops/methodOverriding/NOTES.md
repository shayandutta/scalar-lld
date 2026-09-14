# Method Overriding

## Relate it to real life first

Think about a **company's standard onboarding process** defined by head office: "give the new hire a laptop, an email account, and a welcome packet." Every regional branch is expected to have an onboarding process — that's the shared expectation. But the branch in Japan might do onboarding slightly differently to fit local customs, and the branch in Germany might do it differently to comply with local labor law. Same *name* for the process ("onboarding"), same general *purpose*, but each branch has rewritten the actual steps to fit its own situation. When someone at the Japan office runs "onboarding," Japan's version runs — not head office's generic version — even though on paper, Japan's process is officially "the same" onboarding process every branch has.

That's method overriding: a subclass takes a method it inherited from its parent and **completely rewrites** its actual behavior, while keeping the exact same name and inputs — so that from the outside, it still looks like "the same operation," but what actually happens underneath is specific to that particular subclass.

## The actual concept

Overriding happens when a child class provides its **own implementation** of a method that its parent class already declared — using the **exact same method name and exact same parameter list**. Once that's done, calling that method on an object of the child class runs the child's version, completely replacing the parent's version for that object — even if you're holding the object through a variable *typed* as the parent class. Java figures out which version to run by looking at the object's **real, actual type**, and it does this while the program is actually running — not while it's being compiled. This is why overriding is the mechanism behind "**runtime polymorphism**" (see [[polymorphism]] for the full picture of that).

This is different from [[methodOverloading]] in a way that's very easy to mix up if you're new to this — overloading is same class, different parameter lists, resolved at compile time. Overriding is parent-vs-child, identical parameter list, resolved at runtime. Same-sounding names, opposite mechanisms.

## The code

```java
// Parent.java
public class Parent {
    public void print(){
        System.out.println("printed from Parent");
    }
}
```

```java
// Child.java
public class Child extends Parent {
    // int print(){
    // -> can't change the return type during method overriding
    // because Child inherited Parent's print() with a void return type,
    // and here we'd be trying to change it to int
    public void print(){
        System.out.println("printed from Child");
        // return -1;
    }
}
```

```java
// Main.java
Child child = new Child();
child.print();   // "printed from Child"
```

## Walking through what's happening

`Parent` declares `print()`, and it prints "printed from Parent". `Child extends Parent`, so ordinarily, `Child` would just inherit that exact `print()` method unchanged, and calling `print()` on a `Child` object would print "printed from Parent" too, unmodified.

But `Child` doesn't just inherit it here — it **redeclares** `print()` with the exact same name (`print`) and exact same parameter list (none), and gives it a brand new body. This is overriding, not shadowing (contrast with [[Inheritance]], where `Student.name` shadowed `User.name` — that was two *separate fields*; here, there's only ONE `print()` "slot" as far as any code calling it through a `Parent`-shaped reference is concerned, and `Child`'s version has completely taken over that slot for `Child` objects).

So `child.print()` runs `Child`'s version, printing "printed from Child". The `Parent` version still exists in memory, technically, but for any `Child` object, it's been fully replaced.

## The rule this code is specifically warning about: matching signatures, matching return types

The commented-out attempt:
```java
int print(){
    // return -1;
}
```
tries to override `print()` while changing its return type from `void` to `int`. This is not allowed, and the reasoning matters: to Java, this isn't "Child's new version of print()" — it looks like an attempt to declare a *second, unrelated* method that happens to also be named `print`, with zero parameters, but a conflicting return type versus the one `Child` already inherited from `Parent`. Since overloading also requires different parameter lists (see [[methodOverloading]]) and this has the *same* (empty) parameter list, Java can't treat it as an overload either. It's stuck in the middle — not a valid override (return type doesn't match), not a valid overload (parameters don't differ) — so it's simply a compile error.

The actual rule: to properly override a method, the new version in the child class must match the parent's version in **name**, **parameter list**, and **return type** (or, for methods that return objects rather than primitives, the child's return type is allowed to be a more specific subtype of the parent's return type — called a "covariant return type" — but that's not relevant to this `void`/`int` example, since primitives don't have that flexibility at all).

## Real use cases — why this matters outside toy examples

Overriding is everywhere in real object-oriented design. Almost every Java class implicitly overrides `toString()` from `Object` to describe itself sensibly (e.g. a `Point` class overriding `toString()` to print `"(3, 4)"` instead of some meaningless default). A drawing app might have a base `Shape` class with a `draw()` method, and every specific shape (`Circle`, `Square`, `Triangle`) overrides `draw()` to actually render its own particular shape — calling code just calls `shape.draw()` without caring which specific shape it's holding, and the correct drawing logic runs automatically because of overriding (this exact idea is worked through fully in [[abstraction]]'s `Shape` example).

## Common mistakes to watch for
- Trying to change the return type when overriding (this exact code's warning) — only allowed if the new return type is a subtype of the original, never a totally unrelated type.
- Forgetting `@Override` — it's not required by the compiler, but leaving it off means a typo in the method name or parameter list silently creates a brand new, unrelated method instead of overriding anything, and you won't find out until your program behaves unexpectedly at runtime. Always use `@Override` — it forces the compiler to check you're actually overriding something real.
- Trying to *reduce* the access level while overriding (e.g. parent's method is `public`, child tries to make it `protected` or package-private) — not allowed, same rule mentioned in [[interfaces]] for why interface method implementations must stay `public`.

## The takeaway
- Overriding = child class rewrites a method it inherited from its parent, keeping the exact same name, parameters, and return type (or a covariant subtype of it).
- Which version actually runs is decided by the real, actual type of the object at runtime — not by the type of the variable/reference you're using to access it.
- The return type can't be freely changed during an override — primitive return types must match exactly.
- This is the actual mechanism underneath runtime polymorphism — see [[polymorphism]] for a bigger, more realistic example of exactly this idea in action across multiple sibling subclasses at once.
