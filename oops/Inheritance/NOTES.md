# Inheritance

## Relate it to real life first

Think about **family traits**. A child inherits their parents' surname, maybe their eye color, maybe a knack for music — automatically, just by being born into that family. The child didn't have to "learn" the surname; they got it for free. But the child can also have their own things going on that neither parent has — their own hobbies, their own friends, their own personality quirks layered on top of what they inherited.

That's exactly the deal here: `Student`, `Mentor`, `TA` are all fundamentally "a kind of" `User` — they all have a name, an age, a gender, they can all get and change their user ID. Instead of writing all that stuff three separate times (once for `Student`, once for `Mentor`, once for `TA` — copy-pasted, easy to get out of sync, painful to maintain), you write it ONE time, in `User`, and every subclass gets it automatically through `extends`. Then each subclass adds whatever is uniquely its own on top — `Student` has a `batch` and a `psp` score, things a generic `User` has no business having.

## The actual concept

`class Student extends User` means: "`Student` is a `User`, plus maybe some extra stuff." This is called an **is-a relationship** — a `Student` *is a* `User`. (Contrast this with a *has-a* relationship — a `Car` *has a* `Engine`, but a `Car` is not a kind of `Engine` — that's a different design pattern called composition, not inheritance. Inheritance is specifically for "this is a specialized version of that.")

Two big wins from this:
1. **Code reuse** — write shared logic once, in the parent, get it for free in every child.
2. **A common type to treat them all the same way** — this sets up [[polymorphism]] later, where a single `List<User>` can hold a mix of `Student`, `Mentor`, `TA` objects.

## The code

```java
// User.java — parent class
public class User {
    String name="shayan";
    int age;
    String gender;
    private int userid;

    void userId(){
        System.out.println("the userid is: " + userid);
    }
    void changeUserid(int newId){
        userid=newId;
        System.out.println("new userId is: " + userid);
    }
    void printName(){
        System.out.println(name);
    }
    private void printOk(){
        System.out.println("print ok");
    }
}
```

```java
// Student.java — child class
public class Student extends User {
    double psp;
    String batch;
    int userid=1000;   // its own separate field, same name as parent's
    String name;        // its own separate field, same name as parent's

    void pauseBatch(){
        System.out.println("batch paused");
        batch=null;
    }
    void changeName(String newName){
        name=newName;
    }
    void studentUserId(){
        System.out.println("the students separate userid is: " + userid);
    }
}
```

`Mentor` and `TA` also `extends User` but add nothing new — they simply get everything `User` has, for free, with zero extra code.

```java
// Client.java — using it
Student st = new Student();
st.psp=81.0;
st.name="xyz";
// st.userid=1;

st.pauseBatch();
st.changeName("Dutta");
st.userId();          // inherited from User
st.changeUserid(12);  // inherited from User
st.studentUserId();
st.name="crazy";
System.out.println(st.name);   // prints "crazy"
st.printName();                 // prints "shayan" (!)
```

## Walking through the confusing part slowly: two `userid`s, two `name`s living in ONE object

This is the part that trips people up the most, so slow down here. `User` declares `private int userid;`. `Student` *also* declares `int userid=1000;`. These are not the same field being shared — they are **two entirely separate fields**, both physically existing inside every single `Student` object at the same time. Same story for `name` — `User` has one `name` field, `Student` declares its own separate `name` field too.

Why does Java allow this instead of raising an error? Because Java doesn't try to merge or unify same-named fields between a parent and child — it just keeps both, and decides *which one you're talking about* based on **where the code asking for it physically lives**, not based on any clever runtime lookup.

- Code written inside `User` (like `printName()`, which does `System.out.println(name);`) will always use `User`'s own `name` field — it has no idea `Student` even declared its own `name`. As far as `User`'s code is concerned, there's only ever been one `name`, its own.
- Code written inside `Student` (like `studentUserId()`) uses `Student`'s own `userid` field.
- Code written outside both, like `Client.java`, accessing `st.name` — Java resolves this to `Student`'s own `name` field, because `st`'s *declared* type is `Student`.

So trace through what actually happens: `st.name="xyz"` sets `Student`'s own `name` to `"xyz"`. Later `st.name="crazy"` sets it to `"crazy"`. `System.out.println(st.name)` reads `Student`'s own field → prints `"crazy"`. But then `st.printName()` runs — and that method's *code* lives inside `User`, so when it does `System.out.println(name)`, it's reading `User`'s own `name` field, which was never touched by anything `Student` did — it's still sitting at its original value, `"shayan"`.

**This is the single most important lesson of this whole example: fields do NOT get "overridden" the way methods do.** There's no such thing as a `Student` "overriding" `User`'s `name` field. They're just two unrelated fields that happen to share a name, and Java resolves which one you mean by looking at the *declared type* of the reference (or, if you're writing code inside a class, by which class that code lives in) — never by looking at the object's real runtime type. This is very different from how method overriding works — see [[methodOverriding]] and [[polymorphism]], where the object's *real* type absolutely does decide which method body runs. Fields are the one place in Java where that dynamic, "ask the real object" behavior does NOT happen.

## Why is `st.userid=1;` commented out in the code?

`User.userid` is `private` — genuinely unreachable from anywhere outside the `User` class, including from `Student` even though `Student` is `User`'s own child. The only way to read or change `User`'s `userid` is through `User`'s own methods: `userId()` (prints it) and `changeUserid(int)` (sets it) — which is exactly why `Client.java` calls those methods instead of touching a field directly (this is also [[encapsulation]] in action, showing up here).

`Student.userid`, meanwhile, is a totally different field — no access modifier written, meaning default/package access, readable and writable from anywhere in the same package. So actually, `st.userid = 1;` *would* compile fine and would just set `Student`'s own field. It's left commented out here as a deliberate pause, a signal to make you stop and realize: "wait, which `userid` does this even refer to?" — and the answer, again, is always the one belonging to `st`'s declared type, `Student`.

## Why `private void printOk()` matters

`User.printOk()` is private. Not only can `Client` not call it — `Student` can't call it either, even though `Student extends User`. It's not "inherited" in any usable sense; it exists somewhere in memory as part of every `User`/`Student` object, but there's no way to invoke it from outside `User`'s own code. `private` methods are the strictest form of hiding Java offers — stricter than default/package access, stricter than `protected`.

## Real use cases — why this matters outside toy examples

Almost every real system has a hierarchy like this: an e-commerce app might have a base `Product` class with `Book`, `Electronics`, `Clothing` all extending it, sharing common fields like `price` and `name`, while each adds its own specifics (`Book` has an `author`, `Electronics` has a `warrantyMonths`). A company payroll system might have `Employee` as a base, with `Manager` and `Engineer` extending it, sharing `salary`/`employeeId` logic but each having its own bonus calculation. Inheritance is how you avoid writing the shared 80% of the logic three separate times.

## Common mistakes to watch for
- Accidentally shadowing a field by reusing a parent's field name in a child class (like this example deliberately does, to teach the concept) — usually this is a bug, not intentional, and leads to exactly this kind of confusing "why did my change not show up" situation.
- Assuming a child can reach a parent's `private` members just because it's the child — it cannot. Only `protected`, default (package), or `public` members are reachable from a subclass.
- Overusing inheritance for things that aren't really "is-a" relationships — if the relationship is closer to "has-a" (a `Car` has an `Engine`, doesn't extend one), prefer composition instead of forcing an `extends`.

## The takeaway
- `extends` = reuse fields/methods from a parent, model a genuine "is-a" relationship.
- `private` members in a parent are truly hidden — not even a direct subclass can touch them, only the parent's own methods can.
- If a parent and child both declare a field with the same name, they become two separate fields living in the same object — resolved by declared type / which class's code you're standing in, never by the object's real runtime type.
- Methods behave completely differently from fields here — see [[polymorphism]] and [[methodOverriding]] for how overriding actually does use the object's real type.
