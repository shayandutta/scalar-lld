# Method Overloading

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

## What's going on, in plain words

Method overloading = same method name, but different inputs (different number or type of parameters), in the same class. Java looks at what you're passing in and picks the matching version *while compiling* — before the program even runs. That's why it's called "compile-time" behavior.

Here, `print()` takes nothing, `print(String name)` takes one word. Calling `a.print()` vs `a.print("shayan")` picks a different method purely based on what you typed in the parentheses.

## The trap this code is warning you about

The commented-out block tries to add a *third* `print` method that also takes a `String` — same as the second one — but returns a `String` instead of `void`. That doesn't work. Java decides whether two methods are "different enough" to co-exist by looking only at the **name + parameter types**. It completely ignores the return type. So `void print(String)` and `String print(String)` look identical to Java and clash — duplicate method, won't compile. That's why it's commented out.

## The takeaway
- Overloading = same name, different parameter list (count, type, or order), same class.
- Return type is never part of a method's signature — you can't overload on return type alone.
- The compiler resolves which overload to call at compile time, using the argument types at the call site. No guessing at runtime needed.
- Overloading IS a form of polymorphism too — just the compile-time kind. One method name, many forms, picked by the compiler. Compare with [[polymorphism]] and [[methodOverriding]], where the actual method that runs is picked at runtime based on the real object.
