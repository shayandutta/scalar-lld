# Encapsulation (Pillar of OOP)

No dedicated code package in the repo for this one either — but you've already touched it: `User.userid` being `private` in [[Inheritance]] is this exact idea in action. This note slows down and explains it properly.

## Relate it to real life first

Think about a **pill capsule**. The medicine inside is the actual "data" that matters. But you never touch the raw medicine powder directly with your fingers — it's sealed inside a capsule shell. The shell controls exactly how and when the medicine gets released (when it dissolves in your stomach). You interact with the *capsule*, not the raw powder.

Or think of an **ATM machine**. The cash inside the machine is real money sitting in a vault. You, the customer, never open the vault door and grab cash yourself. You interact through a strictly controlled interface — a screen and a few buttons: "withdraw", "check balance." The machine internally checks things (do you have enough balance? is your PIN correct?) before it lets any cash move. You get *controlled*, *safe* access to something valuable, without ever touching the raw thing directly.

That's the whole idea of encapsulation: **wrap the real data in a protective layer, and only allow interaction through a small set of controlled, sensible operations** — never direct, unrestricted access.

## The actual concept, properly explained

Encapsulation is really two things happening at once, and people often only remember one of them:

1. **Bundling** — the data and the code that operates on that data live together, inside one class. A bank account's balance number and the logic for changing it safely (deposit/withdraw rules) belong together, not scattered across the codebase.
2. **Data hiding** — the data itself is marked `private`, meaning literally no other class, anywhere, can reach in and touch it directly. The *only* way to interact with it is through methods the class itself chooses to expose.

The keyword doing the actual hiding in Java is `private`. When a field or method is `private`, it's invisible to every other class — not just "impolite to touch," but a genuine compiler-enforced wall. Try to access it from outside and your code won't even compile.

## The example code

```java
public class BankAccount {
    // private -> nobody outside this class can touch balance directly
    private double balance;

    BankAccount(double openingBalance) {
        this.balance = openingBalance;
    }

    void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("rejected: amount must be positive");
            return;
        }
        balance += amount;
    }

    void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("rejected: amount must be positive");
            return;
        }
        if (amount > balance) {
            System.out.println("rejected: insufficient funds");
            return;
        }
        balance -= amount;
    }

    // read access is fine, just no direct write access
    double getBalance() {
        return balance;
    }

    // deliberately NO setBalance(double) method
}
```

```java
BankAccount acc = new BankAccount(100.0);

// acc.balance = -5000;   // <- literally will not compile: balance is private

acc.deposit(50);      // balance = 150
acc.withdraw(9999);   // rejected, balance stays 150, untouched
System.out.println(acc.getBalance());
```

## Walking through why the "no direct access" part actually matters

Imagine, hypothetically, that `balance` was declared `public double balance;` instead. Now anyone holding an `acc` reference could do:

```java
acc.balance = -5000;
```

No check happens. No rule enforced. The account is now permanently, silently broken — negative money, something that should never be possible in a working bank system. This isn't a hypothetical edge case; it's exactly the kind of bug that happens in real, badly-designed code — some other part of a large codebase, written by someone else, months later, touches a field it "shouldn't" and quietly corrupts state, and nobody notices until it's a serious problem.

Because `balance` is `private`, the *only* two doors into changing it are `deposit()` and `withdraw()` — and both doors have a guard standing in front of them (the `if (amount <= 0)` checks, the `if (amount > balance)` check). The class can now *guarantee*, permanently, that `balance` never becomes invalid — because it is physically the only code allowed to touch it, and it always checks first.

## Why is there a `getBalance()` but deliberately NO `setBalance()`?

This is the part beginners usually get wrong. A common (bad) habit is: "private field → auto-generate a public getter AND a public setter for it, always, as a pair." That completely defeats the purpose here. A `setBalance(double newBalance)` method that just does `this.balance = newBalance;` with no checks is **exactly as unsafe as making the field public** — you've just added an extra method call in front of the same unrestricted access.

Encapsulation isn't "hide the field, then immediately hand back full access through a setter." It's "expose *only* the operations that make real sense." For a bank account, reading the balance is harmless (`getBalance()` is fine), but directly assigning an arbitrary number to it is never a legitimate real-world operation — the only legitimate ways balance should ever change are through a deposit or a withdrawal. So those are the only two doors that exist.

## Real use cases — why this matters outside toy examples

Any time a class has an internal value that needs to obey rules, encapsulation is what makes those rules unbreakable: a `Temperature` class that should never allow below absolute zero, a `Percentage` class that should always stay between 0-100, a `ShoppingCart` where the total should always match the sum of its items. If any of these had public fields, some far-away piece of code could set them to nonsense and there'd be no way to prevent it. Encapsulation also lets you *change your mind later* about how something is stored internally — e.g. swap `balance` from a `double` to a more precise `BigDecimal` — without breaking any code outside the class, because outside code never touched the field directly, only the stable `deposit()`/`withdraw()`/`getBalance()` methods.

## Common mistakes to watch for
- Adding a setter for every private field "just in case" — ask first whether unrestricted external writes to that field would ever actually make sense.
- Making fields `private` but then writing a getter that returns a mutable object by reference (e.g. returning an internal `List` directly) — the caller can then mutate the internals anyway through that returned reference. True encapsulation sometimes means returning a copy instead.
- Forgetting that `private` is at the *class* level, not the *object* level: two different `BankAccount` objects CAN see and touch each other's private fields, as long as the code doing so lives inside the `BankAccount` class itself (e.g. a `transferTo(BankAccount other)` method inside `BankAccount` could legally touch `other.balance` directly).

## The takeaway
- Encapsulation = bundle data + the logic that changes it, and make the data `private` so nothing outside the class can reach in directly.
- The goal isn't hiding for its own sake — it's letting the class *guarantee* its own rules (invariants) can never be broken by outside code.
- Don't blindly pair every private field with a public setter — expose only the operations that genuinely make sense.
- Java's access levels, widest to narrowest: `public` → `protected` → default/package-private → `private`. Always reach for the narrowest one that still lets your code work correctly.
- This connects directly to [[Inheritance]] (`User.userid` is private there for the same reason) and contrasts with [[abstraction]] — encapsulation hides *data*, abstraction hides *implementation details/how something works*.
