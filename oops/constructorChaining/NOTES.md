# Constructor Chaining

## The code

```java
// A.java
public class A {
}
```

```java
// B.java
public class B extends A {
    private int age;
    B(int age){
        this.age=age;
    }
    void printAge(){
        System.out.println(age);
    }
}
```

```java
// C.java
public class C extends B {
    C(){
        super(10);
    }
}
```

```java
// D.java
public class D extends C {
}
```

```java
// Client.java
D d = new D();
d.printAge();   // prints 10
```

## What's going on, in plain words

Before any constructor runs its own body, it first has to run the constructor of its parent class. This happens every time, all the way up to `Object`. That's the "chain" — a straight line from the topmost parent down to the class you're actually creating.

You write this explicitly with `super(...)` as the first line of a constructor. If you don't write it, Java quietly adds `super()` (no arguments) for you.

## Walking through `new D()` step by step

1. `D` has no constructor written, so Java gives it a free empty one: `D() { }`. That free one still secretly starts with `super()`, calling `C()`.
2. `C()` explicitly calls `super(10)` — that calls `B(10)`.
3. `B(10)` doesn't write `super(...)` itself, so Java adds an invisible `super()` at the top — that calls `A()`.
4. `A()` has no constructor written either, so it gets a free `A() { }`, which itself invisibly calls `Object()`'s constructor.
5. `Object()` finishes, control comes back down: `A()` finishes (nothing in it), then `B(10)` runs its real line — `this.age = 10;` — then `C()` finishes (nothing left), then `D()` finishes (nothing left).
6. Now the `D` object fully exists, with `age` set to `10`. `d.printAge()` prints `10`.

## The important rule this code is showing

`B` only has one constructor: `B(int age)`. It has **no** empty `B()` constructor. That means anything extending `B` — here, `C` — cannot rely on the automatic invisible `super()`, because there's no matching no-argument constructor to call. `C` is *forced* to write `super(10)` itself, picking one of `B`'s real constructors and supplying the number. If `C()` were left empty, it wouldn't compile — Java would try to insert `super()` and fail to find one.

`D`, on the other hand, doesn't need to do anything special, because `C()` (the one `D` calls into automatically) is a complete, working constructor by itself.

## The takeaway
- Every object construction runs a straight chain of parent constructors, top to bottom, before its own body runs.
- `super(...)` must be the first line of a constructor if you write it at all.
- If a parent class has no plain empty constructor, every direct child MUST explicitly call one of the parent's real constructors with `super(...)`, or it won't compile.
- This is completely separate from method overriding — constructors are never inherited, only chained.
