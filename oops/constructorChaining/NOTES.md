# Constructor Chaining

## Relate it to real life first

Think about **building a house**. Before you can put up walls, you need a foundation. Before you can paint the walls, they need to actually exist. Before you can install furniture, the paint needs to be dry. Each stage depends on the previous stage being fully, properly finished first — you can't skip ahead. Nobody hangs a picture frame on a wall that doesn't exist yet.

Object construction across a chain of classes (`A` → `B` → `C` → `D`, where each `extends` the previous) works the same way. Before `D`'s own setup can run, `C`'s setup has to be fully done. Before `C`'s setup can run, `B`'s has to be fully done. Before `B`'s, `A`'s. This guarantees that by the time you're holding a finished `D` object in your hands, every single layer underneath it (`C`, `B`, `A`, and even the built-in `Object` class every Java class ultimately extends) has already been properly initialized — no object ever exists in some "half-built" in-between state where the child part exists but the foundational parent part doesn't.

## The actual concept

Every constructor's very first action — before its own body even starts running — is to call the constructor of its immediate parent class. You either write this explicitly (`super(...)` as the literal first line of the constructor), or, if you don't write anything, Java silently inserts a no-argument `super()` call for you as if you'd typed it. This happens at every single level, all the way up to `Object` (Java's ultimate ancestor class that every other class implicitly extends, even if you never write `extends Object` yourself).

The word "chaining" refers to this whole sequence linking together — one constructor call triggers the next one up, which triggers the next one up, forming an unbroken chain from the object you're actually creating all the way up to `Object`.

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

## Walking through `new D()` one step at a time — don't skip this part

1. You call `new D()`. `D` doesn't have any constructor written in its source code at all — so Java automatically gives it a free, empty one: `D() { }`. Even though you can't see it in the source, that free constructor still secretly starts with an invisible `super();` call as its very first line — Java inserts this for every constructor that doesn't explicitly call `super(...)` or `this(...)` itself.
2. That invisible `super();` inside `D()` calls `C()`.
3. `C()` is NOT empty — it explicitly writes `super(10);` as its first line. That calls `B(10)`.
4. Inside `B(10)`, no `super(...)` or `this(...)` is written at the top, so Java again silently inserts an invisible `super();` as the true first line. That calls `A()`.
5. `A` also has no constructor written — free implicit `A() { }` is generated, which itself has an invisible `super();` calling all the way up to `Object`'s constructor.
6. `Object`'s constructor runs (does its own internal setup, nothing visible to us) and returns.
7. Control comes back down to `A()` — there's nothing else in its body, so it finishes immediately.
8. Control comes back down to `B(10)` — NOW, after its `super()` call has fully returned, the rest of `B`'s constructor body runs: `this.age = 10;`. This is the actual real work happening in this whole chain.
9. Control comes back down to `C()` — nothing left in its body after the `super(10)` line, so it finishes.
10. Control comes back down to `D()` — nothing in its body either, so it finishes.
11. `new D()` is now done. You have a fully-built `D` object, and buried inside it, `age` is `10`.
12. `d.printAge()` — this method is defined on `B`, inherited all the way down to `D`. It prints `age`, which is `10`.

The key insight: **construction runs top-down (parent finishes before child continues), but each parent's meaningful work happens right after ITS OWN `super()` call returns** — so the actual order real code executes in is: `Object`'s setup, then `A`'s body (empty), then `B`'s body (`this.age=10`), then `C`'s body (empty), then `D`'s body (empty).

## The important rule this code is specifically built to demonstrate

Look closely: `B` has exactly ONE constructor, `B(int age)`. It has NO plain empty `B()` constructor. This is deliberate, and it's the whole point of this example.

Because `B` has no no-argument constructor, **any class that directly extends `B` cannot rely on Java's automatic invisible `super()` insertion** — there is no matching no-arg constructor on `B` for Java to silently call. This means `C`, which extends `B`, is *forced* to explicitly write a `super(...)` call that matches one of `B`'s real, actual constructors — supplying an actual integer. That's exactly why `C()` contains `super(10);`. If you deleted that line and left `C(){ }` empty, it would NOT compile — Java would try to insert an implicit `super()`, fail to find a no-arg constructor on `B`, and throw a compile error.

`D`, on the other hand, extends `C` — and `C()` (the constructor `D`'s implicit constructor calls into) is a complete, fully-working, callable-with-no-arguments constructor. So `D` doesn't need to do anything special at all; the free implicit constructor Java generates for it works out of the box.

## Real use cases — why this matters outside toy examples

Any time a parent class needs certain information to set itself up correctly and can't have a sensible default (a database connection object needs a URL and credentials, a `Shape` needs at least a color, a `Vehicle` needs at least a max speed), the parent simply won't offer a no-argument constructor — and that forces every subclass, at every level, to explicitly supply that required information via `super(...)`. This is a deliberate design tool: it makes it *impossible* to accidentally create a subclass object without the required setup, because the compiler itself won't let you skip it.

## Common mistakes to watch for
- Trying to access `this` or any instance field inside the arguments passed to `super(...)` — not allowed, because the object isn't considered to exist yet until `super(...)` finishes running. You can only pass constructor parameters, literals, or static values.
- Writing both `super(...)` and `this(...)` in the same constructor — illegal, you can only have one, and it must be the very first line.
- Assuming constructors are inherited like methods are — they are NOT. A subclass never "has" its parent's constructor; it can only call it via chaining. Every class must define its own constructors (or accept the one free implicit one).

## The takeaway
- Every object construction runs a straight chain of parent constructors, from the topmost ancestor down to the class actually being instantiated, before that class's own body finishes running.
- `super(...)`, if written, must be the very first statement in a constructor. If you don't write it (or `this(...)`), Java inserts a no-arg `super()` automatically.
- If a parent class has no plain no-argument constructor, every class that directly extends it MUST explicitly call one of the parent's real constructors via `super(...)`, or the code won't compile.
- Constructors are never inherited — only ever chained. This is a completely separate mechanism from method overriding, covered in [[methodOverriding]].
