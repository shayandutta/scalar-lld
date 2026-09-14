# SOLID Principles

No code package for this in the repo — SOLID isn't a Java feature like `interface` or `extends`. It's just 5 rules of thumb for writing classes that are easy to change later without breaking things. Compiler doesn't check any of this for you — it's on you as the designer.

SOLID = 5 letters, 5 rules. Going one by one, using examples close to code you've already seen.

---

## S — Single Responsibility Principle

**Rule, in plain English:** a class should have only ONE job. Only one reason it would ever need to change.

Remember `BankAccount` from [[encapsulation]]? It only does money stuff: `deposit()`, `withdraw()`, `getBalance()`. Imagine someone adds this to it:

```java
public class BankAccount {
    private double balance;

    void deposit(double amount) { balance += amount; }
    void withdraw(double amount) { balance -= amount; }

    // BAD: now this class also does emailing
    void sendReceiptEmail(String toAddress) {
        // connect to mail server, format email, send it...
    }
}
```

Now `BankAccount` has TWO jobs: managing money, and sending emails. If the email service changes (new provider, new format), you have to touch `BankAccount` — a class that's supposed to be about money. Two unrelated reasons to change = violates SRP.

**Fix:** split it.
```java
public class BankAccount {
    void deposit(double amount) { /* ... */ }
    void withdraw(double amount) { /* ... */ }
}

public class ReceiptMailer {
    void sendReceiptEmail(String toAddress) { /* ... */ }
}
```
Now each class has exactly one reason to ever change.

---

## O — Open/Closed Principle

**Rule, in plain English:** you should be able to ADD new behavior without EDITING old, already-working code.

Remember `Shape`, `Circle`, `Rectangle` from [[abstraction]]? Say you want to add a `Triangle`:

```java
public class Triangle extends Shape {
    private double base, height;
    Triangle(double base, double height) { this.base = base; this.height = height; }

    double area() { return 0.5 * base * height; }
    double perimeter() { return /* ... */ 0; }
}
```

You never touched `Shape.java`, `Circle.java`, or `Rectangle.java` to add this. You just added a new file. `Shape` is "closed" for modification (you don't need to edit it) but "open" for extension (you can always add a new subclass). That's the whole principle — good abstractions let you grow the system by adding, not editing.

**Bad version, for contrast:** if instead of subclasses you had one giant method with `if (shape.type == "circle") ... else if (shape.type == "rectangle") ...`, adding a triangle means going back and editing that big if-chain — every new shape means editing old code. That's what OCP says not to do.

---

## L — Liskov Substitution Principle

**Rule, in plain English:** if `Student` and `Mentor` extend `User`, then anywhere your code expects a `User`, handing it a `Student` or `Mentor` instead should just work — no surprises, no crashes.

You've already used this correctly in [[polymorphism]]:
```java
List<User> users = List.of(new TA(), new Student(), new Mentor(), new User());
for (User user : users) {
    user.changeEmail();   // works fine no matter which real subtype it is
}
```
Every subclass behaves like a proper `User` when treated as one. That's LSP working correctly.

**How you'd break it (classic textbook example, not in your code):**
```java
class Bird {
    void fly() { System.out.println("flying"); }
}
class Ostrich extends Bird {
    void fly() { throw new UnsupportedOperationException("ostriches can't fly!"); }
}
```
Now, any code that does `for (Bird b : birds) b.fly();` — which is totally correct for `Bird` in general — crashes the moment an `Ostrich` sneaks into that list. `Ostrich` LOOKS like a valid `Bird` (compiles fine, "is-a" relationship on paper) but breaks the promise a `Bird` makes ("I can fly"). That's an LSP violation: a subclass technically extends the parent but doesn't honor its behavior.

---

## I — Interface Segregation Principle

**Rule, in plain English:** don't force a class to implement methods it doesn't actually need. Prefer several small, focused interfaces over one giant one.

You've actually already got a textbook-correct example of this in [[interfaces]]! Instead of one fat interface:
```java
// BAD — imagine this instead
interface Animal {
    void eatPlant();
    void eatAnimal();
    void fly();
    void swim();
}
```
A `Dog` would be forced to write a body for `fly()` and `swim()` even though dogs don't do either — pointless, misleading methods just to satisfy the interface.

Your actual code avoids this by splitting into small interfaces:
```java
interface Herbivore { void eatPlant(); }
interface Carnivore { void eatAnimal(); }
```
`Dog implements Herbivore` only signs up for what it actually needs. `Human implements Omnivore` (which combines both) only because a human genuinely needs both. Nobody is stuck implementing a method that makes no sense for them. That's ISP.

---

## D — Dependency Inversion Principle

**Rule, in plain English:** depend on an interface (an abstraction), not on one specific concrete class. Let the concrete class be swapped out easily.

```java
// BAD — tightly locked to one specific class
class NotificationService {
    GmailSender sender = new GmailSender();
    void notifyUser(String msg) { sender.send(msg); }
}
```
If you ever want to switch to SMS or Slack instead of Gmail, you have to go edit `NotificationService` itself.

```java
// GOOD — depends on the contract, not the specific implementation
interface MessageSender {
    void send(String msg);
}
class GmailSender implements MessageSender {
    public void send(String msg) { /* send via gmail */ }
}
class SmsSender implements MessageSender {
    public void send(String msg) { /* send via sms */ }
}
class NotificationService {
    MessageSender sender;
    NotificationService(MessageSender sender) { this.sender = sender; }
    void notifyUser(String msg) { sender.send(msg); }
}
```
Now `new NotificationService(new SmsSender())` or `new NotificationService(new GmailSender())` — swapping the real sender needs zero changes to `NotificationService`. It only ever talks to the `MessageSender` interface, never to a specific class. Same idea as your `Herbivore` list in [[interfaces]] — code written against the interface doesn't care which real class shows up.

---

## The takeaway
- SOLID isn't enforced by Java — it's a design habit, judged by "will this be easy to change later," not by whether it compiles.
- S: one class, one job.
- O: add new code, don't rewrite old code.
- L: a subclass must behave like a proper version of its parent, not just compile as one.
- I: small focused interfaces > one giant interface with unused methods.
- D: depend on interfaces, not concrete classes, so swapping implementations is cheap.
- Notice I/D are basically *why* [[interfaces]] exist as a language feature in the first place — SOLID is the reasoning, interfaces are the tool.
