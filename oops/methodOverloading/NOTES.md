# Method Overloading

## Relate it to real life first

Think about the word **"order"** at a coffee shop. You can say:
- "I'd like to order a coffee." (one thing)
- "I'd like to order a coffee and a croissant." (two things)
- "I'd like to order a coffee, size large, oat milk." (specific details)

Same word, "order", every time. But the barista understands something different depending on *what exactly you handed them along with that word*. You didn't need three different words for three slightly different requests — the same word adapts based on what's given to it.

Method overloading is that same idea, applied to a method name in a class: one name, several versions, each expecting different information handed to it (different parameters). Java looks at exactly what you're "handing it" (the arguments) at the point where you call the method, and picks the matching version.

## The actual concept

**Overloading** = defining two or more methods **in the same class** that share the exact same name, but differ in their **parameter list** — meaning a different number of parameters, different parameter types, or a different order of parameter types. Java decides which one you meant purely by looking at what you typed inside the parentheses when you called it — and it does this decision-making **while compiling your code**, before the program has even started running. This is why overloading is often called "**compile-time polymorphism**" — "poly" (many) "morph" (forms) of the same method name, resolved at compile time rather than while the program is actually executing.

This is genuinely useful because it lets you offer a natural, flexible-feeling API without inventing awkward separate names like `printNothing()` and `printWithName()` — you just call `print()` or `print("shayan")` and it reads naturally either way.

## The code

```java
// A.java
public class A {
    public void print(){
        System.out.println("hello");
    }
    public void print(String name){
        System.out.println("hello "+ name);
    }

    // this would NOT compile if uncommented:
    // public String print(String name){
    //     print("hello"+name);
    // }
}
```

```java
// Main.java
A a = new A();
a.print();           // "hello"
a.print("shayan");   // "hello shayan"
```

## Walking through what the compiler is doing

When the compiler sees `a.print();`, it looks at `A`'s class definition and asks: "is there a `print` method that takes ZERO arguments?" Yes — the first one. It locks that call in.

When it sees `a.print("shayan");`, it asks: "is there a `print` method that takes exactly ONE argument, of type `String` (or something convertible to `String`)?" Yes — the second one. It locks that call in too.

This lookup and decision happens entirely at compile time, baked directly into the compiled bytecode — there's no runtime guessing, no checking the actual object's type while the program runs (contrast this hard with [[polymorphism]] and [[methodOverriding]], where the *actual object's real type*, discovered only while the program is running, decides which method body executes).

## The trap this code is specifically warning you about

The commented-out block tries to sneak in a *third* version of `print`:
```java
public String print(String name){
    print("hello"+name);
}
```
This one also takes a single `String` parameter — exactly the same parameter list as the second `print(String name)` method that already exists. The only difference is the return type: `String` instead of `void`.

This does not work, and understanding *why* is the whole point of this example. Java identifies a method's **signature** using only its **name plus its parameter types** — the return type is never part of that identity. So as far as Java is concerned, `void print(String)` and `String print(String)` are trying to declare the exact same method twice, just with conflicting return types — which is a straightforward duplicate-method compile error, not a valid overload. This is precisely why real code comments this block out: it's a broken attempt, kept around as a lesson, not something meant to actually run.

## Real use cases — why this matters outside toy examples

Overloading shows up constantly in real Java code you'll use every day. `System.out.println()` itself is massively overloaded — there's a version for `String`, one for `int`, one for `double`, one for `boolean`, one for `char[]`, and more — all named `println`, letting you just call it naturally with whatever you have, without memorizing a different method name per type. `StringBuilder.append()` is the same story. When you design your own classes, overloading a constructor is extremely common too — e.g. a `Point` class might offer `Point()` (defaults to origin `0,0`), `Point(int x, int y)` (a specific location), and `Point(Point other)` (copy an existing point) — all named `Point`, each convenient for a different situation.

## Common mistakes to watch for
- Trying to overload purely by changing the return type while keeping identical parameters — as this code's commented-out block demonstrates, this simply doesn't compile.
- Assuming overload resolution can always tell things apart perfectly — with autoboxing and varargs involved, ambiguous-looking calls can sometimes fail to compile with an "ambiguous method call" error, because more than one overload matches equally well.
- Confusing overloading (same class, different parameters, resolved at compile time) with overriding (parent/child classes, identical signature, resolved at runtime) — they sound similar but are opposite mechanisms. See [[methodOverriding]] for the full contrast.

## The takeaway
- Overloading = same method name, different parameter list (count, type, or order), within the same class.
- A method's "signature," for the purpose of telling overloads apart, is its name + parameter types ONLY — the return type is never part of it, so you cannot overload on return type alone.
- The compiler resolves which overload gets called at compile time, purely from the argument types visible at the call site — no runtime type-checking involved.
- Overloading is itself a form of polymorphism — the "compile-time" kind — distinct from the "runtime" kind you'll meet in [[polymorphism]] and [[methodOverriding]].
