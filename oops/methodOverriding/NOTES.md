# Method Overriding

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

## What's going on, in plain words

Overriding = the child class rewrites a method it inherited from the parent, using the exact same name and same parameter list. When you call it on a `Child` object, the `Child`'s version runs — the `Parent`'s version is completely replaced for that object.

Here, `Parent.print()` prints "printed from Parent". `Child` inherits that method but rewrites it to print "printed from Child" instead. `child.print()` runs `Child`'s version, not `Parent`'s.

## The rule this code is warning about: same signature, same return type

The commented-out attempt:
```java
int print(){
    // return -1;
}
```
This tries to override `print()` but changes the return type from `void` to `int`. Not allowed. To override a method, the new version must match the original's name, parameter list, **and** return type (or a subtype of it, for object return types — not relevant here since these are primitives). Change the return type and Java doesn't see it as an override anymore — it sees a broken clash on the same method name, and refuses to compile.

## Overriding vs Overloading — don't mix these up

- **Overriding** (this package): child rewrites a method it inherited, same signature, behavior picked at *runtime* based on the real object. See [[polymorphism]] — that's the exact mechanism runtime polymorphism relies on.
- **Overloading** ([[methodOverloading]]): same class, same method name, *different* parameter list, decided at *compile time*.

## The takeaway
- Overriding needs: same method name, same parameters, same (or covariant) return type, happens across parent → child.
- The return type can't just be changed freely — it must match (or be a subtype, for reference types).
- This is what makes `Parent p = new Child(); p.print();` print "printed from Child" — the actual object's class decides which version runs, not the reference type. That's runtime polymorphism in action, same idea as [[polymorphism]]'s `User`/`Student`/`Mentor` example, just with an actual override happening this time.
