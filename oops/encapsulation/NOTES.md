# Encapsulation (Pillar of OOP)

This concept doesn't have its own package in the actual code, but you already saw it in action: `User.userid` in [[Inheritance]] is `private` — hidden, only reachable through `User`'s own methods. This note explains the concept fully with a clearer example.

## Example

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

// acc.balance = -5000;   // won't compile: balance is private

acc.deposit(50);      // balance = 150
acc.withdraw(9999);   // rejected, balance untouched
System.out.println(acc.getBalance());
```

## What's going on, in plain words

Encapsulation means two things happening together:
1. **Bundling** — the data (`balance`) and the code that's allowed to change it (`deposit`, `withdraw`) live inside the same class.
2. **Hiding** — the data itself (`balance`) is marked `private`, so nothing outside the class can reach in and change it directly. The only way in is through the methods the class chooses to expose.

## Why this actually matters (not just a style rule)

If `balance` were `public`, anyone could write `acc.balance = -5000;` and instantly put the account in a broken, invalid state — no checks, no rules, nothing stopping it.

Because `balance` is `private`, the only two doors in are `deposit()` and `withdraw()` — and both doors have a guard standing in front (`amount <= 0` check, `amount > balance` check). The class can *guarantee* balance never goes negative and never gets a bogus value, because it's the only one allowed to touch it.

## Why there's no `setBalance()`

A common mistake is thinking encapsulation just means "private field + auto-generated getter and setter for everything." That defeats the purpose — a setter that lets you assign any number back to `balance` is exactly as unsafe as making the field public. This example gives a getter (reading balance is harmless) but no direct setter — the only legitimate ways to change balance are `deposit` and `withdraw`, because those are the only operations that make real-world sense for a bank account.

## The takeaway
- Encapsulation = keep data private, only reachable through methods the class controls.
- The point isn't "make everything private and add getters/setters" — it's exposing exactly the operations that should be allowed, and nothing more.
- This lets the class protect its own rules (invariants) — no outside code can ever put the object into an invalid state, because outside code never touches the raw data.
- Access modifiers used for this, widest to narrowest: `public` → `protected` → default (package) → `private`. Pick the narrowest one that still works.
