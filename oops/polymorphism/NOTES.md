# Polymorphism (Runtime)

## Relate it to real life first

Think about a **universal remote control**. It has one "Power" button. Point it at the TV and press Power — the TV turns on/off. Point the exact same remote, press the exact same button, at the AC unit instead — the AC turns on/off. Point it at the sound system — the speakers turn on/off. Same button, same action name ("Power"), but the actual thing that physically happens depends entirely on *which device is actually receiving the signal* — not on the button itself.

`User user = new TA();` is that exact situation. `user` is your "remote control" — its label says `User`, so you can only press the buttons a `User` remote has (`changeEmail()`). But the actual device on the receiving end is really a `TA`. When you press that button, the `TA`-specific behavior runs — because Java looks at the *real device*, not the label on the remote.

## The actual concept

A variable's **declared type** (what you wrote when you declared it — `User user`) and the **actual type of the object it's pointing at** (what you built with `new` — `new TA()`) can be different, as long as the real object genuinely "is-a" the declared type (guaranteed by `extends`, as covered in [[Inheritance]]).

**Runtime polymorphism** means: when you call a method through that reference, and that method has been *overridden* somewhere along the chain of subclasses (see [[methodOverriding]] for exactly how overriding itself works), Java doesn't decide which version to run when compiling your code — it waits until the program is actually running, looks at the object's true, real type sitting in memory, and calls THAT type's version. This is also called **dynamic dispatch** — "dynamic" because it's decided while the program runs (dynamically), not fixed in advance.

## The code

```java
// User.java
public class User {
    String email;
    String password;
    void changeEmail(){
        System.out.println("Changing email in user");
    }
}
```

```java
// Student.java, Mentor.java, TA.java — each "extends User" AND overrides changeEmail()
public class Student extends User {
    String batch;
    int psp;
    String mentor;
    void changeBatch(){ System.out.println("batch changed"); }
    void changeEmail(){
        System.out.println("Changing email in student");
    }
}

public class Mentor extends User {
    String company;
    String description;
    double rating;
    void mentorCompany(){ System.out.println("mentors company is"+company); }
    void changeEmail(){
        System.out.println("Changing email in mentor");
    }
}

public class TA extends User {
    String expertise;
    String company;
    void takeHelpRequest(){ }
    void changeEmail(){
        System.out.println("Changing email in ta");
    }
}
```

```java
// main.java
public static void changePasswordForAllUsers(List<User> users){
    for (User user : users) {
        user.changeEmail();   // <- which version runs depends on the REAL object

        if (user instanceof Student) {
            Student student = (Student) user;
            student.changeBatch();
        }
        if (user instanceof Mentor) {
            Mentor mentor = (Mentor) user;
            mentor.company = "gogle";
            mentor.mentorCompany();
        }
    }
}

public static void main(String[] args) {
    User user  = new TA();
    User user1 = new Student();
    User user2 = new Mentor();
    User user3 = new User();

    List<User> users = List.of(user, user1, user2, new TA(), user3);
    changePasswordForAllUsers(users);
}
```

## Walking through the actual output, line by line

Focus closely on this one line inside the loop: `user.changeEmail();`. It's written ONCE, literally the same source code line, executed 5 times as the loop iterates. Here's what actually prints, and why, for each iteration:

```
Changing email in ta        <- this element is really a TA
Changing email in student   <- this element is really a Student
Changing email in mentor    <- this element is really a Mentor
Changing email in ta        <- new TA() built directly in the list
Changing email in user      <- this element is really a plain User
```

Five completely different outputs, from one single unchanging line of code. Nothing about the loop, the variable name, or the declared type (`User`) changed between iterations — the ONLY thing that changed was which real object happened to be sitting in that slot of the list at that moment. That's the entire concept of runtime polymorphism, made concrete: the method that actually executes is chosen based on the object's real identity, discovered fresh at each call, while the program runs.

## Why `List<User>` is even allowed to hold a mix of `TA`, `Student`, `Mentor`, `User`

Every single one of them genuinely *is-a* `User`, thanks to `extends` (see [[Inheritance]]). Java's type system allows a `List<User>` to store any object that is a `User` or any subtype of `User` — it doesn't require every element to be *exactly* a plain `User`. That's precisely why `changePasswordForAllUsers` can accept one single, generically-typed list and process every different kind of user with one shared loop, instead of needing separate code paths for `Student`s, separate code for `Mentor`s, and so on. This is the real, practical payoff of polymorphism: you write flexible code once, against the general type, and it correctly handles every specific subtype without modification — closely related to the Open/Closed idea in [[solidPrinciples]].

## Reaching methods that only exist on ONE specific subclass — the part that is NOT overriding

`changeBatch()` only exists on `Student`. It's not declared anywhere on `User`, so it's not being *overridden* at all — it's a method `User` never had in the first place, unique to `Student`. Because of that, you cannot write `user.changeBatch()` directly — the compiler only knows `user` as a `User`, and `User` has no `changeBatch()` method to call, full stop, regardless of what the real object turns out to be at runtime. To reach it, the code has to do this dance:
1. Check the real type first: `if (user instanceof Student)`.
2. Cast the reference: `Student student = (Student) user;` — this is you telling the compiler "trust me, I've verified this really is a `Student`, let me treat it as one now."
3. Only now can subclass-only members be reached: `student.changeBatch()`, or `mentor.company = "gogle"`.

Make sure this distinction is crystal clear: `changeEmail()` needed NO instanceof-check-and-cast at all, because `User` itself declares it (every subclass just supplies its own version — an override). `changeBatch()` DOES need the instanceof-check-and-cast, because `User` never declared it at all — there's nothing to override, it's purely additional, subclass-only behavior.

## Real use cases — why this matters outside toy examples

Runtime polymorphism is the backbone of most flexible, extensible software design. A payment processing system might have a `PaymentMethod` base type with `CreditCard`, `PayPal`, `BankTransfer` subclasses, each overriding a `process()` method with its own logic — checkout code just calls `payment.process()` on whatever the customer picked, without a giant `if/else` chain checking every possible payment type. A game engine might have a `GameObject` base type with `Player`, `Enemy`, `Obstacle` subclasses, each overriding `update()` and `render()` — the game loop calls `update()`/`render()` on every object in the scene uniformly, and correct, type-specific behavior happens automatically.

## Common mistakes to watch for
- Assuming fields work the same way as overridden methods do — they don't. Fields are resolved by declared type always (see [[Inheritance]]), never by runtime type. Only overridden *methods* get this dynamic, runtime-decided behavior.
- Forgetting that an `instanceof` check and cast is needed to reach genuinely subclass-only members, and trying to call them directly on a supertype-declared reference, which simply won't compile.
- Confusing this (runtime polymorphism, decided while the program runs) with [[methodOverloading]] (compile-time polymorphism, decided while compiling) — they're both technically "polymorphism," but work in opposite ways and are resolved at opposite times.

## The takeaway
- A `User`-typed reference can point at any object that is a `User` or any of its subtypes; which object it actually points at is only known while the program runs, not while it's being compiled.
- When a subclass overrides a method the parent already declares, calling that method through a parent-typed reference still runs the subclass's real, overridden version — every single time, decided fresh based on the actual object.
- `instanceof` + cast is only ever needed to reach methods the declared/reference type doesn't have at all — genuinely subclass-exclusive members, not overridden ones.
- This is the opposite mechanism from [[methodOverloading]]: overloading picks a method using the reference's declared type, at compile time; overriding/runtime polymorphism (this note) picks using the object's actual type, at runtime.
