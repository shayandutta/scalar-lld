# Polymorphism (Runtime)

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

## What's going on, in plain words

`User user = new TA();` — the box (`user`) is labeled `User`, but the thing actually inside it is a `TA`. That's the whole trick: a variable's declared type and the real object it points to can be different, as long as the real object "is-a" the declared type.

Now every subclass **overrides** `changeEmail()` with its own version (see [[methodOverriding]] for the rules on how overriding works). So when the loop runs `user.changeEmail()` on each element, here's what actually prints, in order:

```
Changing email in ta        <- user is really a TA
Changing email in student   <- user1 is really a Student
Changing email in mentor    <- user2 is really a Mentor
Changing email in ta        <- new TA()
Changing email in user      <- user3 is really a plain User
```

Every single call in that loop is literally `user.changeEmail()` — same line of code, same declared type (`User`) — yet five different outputs. Nothing about the loop or the variable type changed between iterations; only the *real* object underneath changed. That's runtime polymorphism: Java looks at the object sitting in memory, not the label on the variable, to decide which `changeEmail()` body actually runs.

## Why `List<User>` can hold `TA`, `Student`, `Mentor`, `User` all mixed together

Every one of them *is-a* `User` (because of `extends`, see [[Inheritance]]). So a single `List<User>` can hold any mix of them, and a single loop can call `user.changeEmail()` on all of them without an `if`/`instanceof` in sight — polymorphism is exactly what makes that one line correct for every different kind of user.

## Reaching methods that only exist on the subclass

`changeBatch()` only exists on `Student`, not on `User` — this one is NOT overriding, it's a method `User` never had at all. So you can't call `user.changeBatch()` directly through the `User` reference. To get there:
1. Check the real type: `if (user instanceof Student)`.
2. Cast it: `Student student = (Student) user;` — tells the compiler "trust me, treat this as a Student now."
3. Now `student.changeBatch()` and `mentor.company = "gogle"` are reachable.

This is a different situation from `changeEmail()`: `changeEmail()` is reachable straight through the `User` reference because `User` itself declares it (just with each subclass giving its own version). `changeBatch()`/`mentorCompany()` aren't declared on `User` at all, so no override is happening there — you need the instanceof-check-then-cast to reach genuinely subclass-only behavior.

## The takeaway
- A `User` reference can point to any subclass object; which object it actually points to is decided when the program runs, not when it's compiled — that's runtime polymorphism.
- When a subclass overrides a method the parent already declares, calling that method through a parent-typed reference still runs the subclass's version — decided by the real object, every time.
- `instanceof` + cast is only needed for methods the parent type doesn't declare at all — genuinely subclass-only members.
- This is different from [[methodOverloading]] (compile-time): overloading picks a method based on the reference's declared type at compile time; overriding (what's happening here) picks based on the object's actual type at runtime.
