# Inheritance

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

`Mentor` and `TA` also `extends User` but add nothing — they just get whatever `User` has for free.

```java
// Client.java — using it
Student st = new Student();
st.psp=81.0;
st.name="xyz";
// st.userid=1;      // <-- this line is commented out, won't compile

st.pauseBatch();
st.changeName("Dutta");
st.userId();          // inherited from User
st.changeUserid(12);  // inherited from User
st.studentUserId();
st.name="crazy";
System.out.println(st.name);   // prints "crazy"
st.printName();                 // prints "shayan" (!)
```

## What's going on, in plain words

Inheritance = one class (`Student`) picks up the fields and methods of another class (`User`) using `extends`, so you don't have to rewrite the same stuff for `Student`, `Mentor`, `TA` — they're all "a kind of" `User`.

## The weird part this code is teaching: two `userid`s, two `name`s

`User` has `private int userid`. `Student` *also* declares its own `int userid=1000`. These are **two completely different fields** sitting inside the same `Student` object — not one shared field.

- `User`'s `userid` is `private` — nobody outside `User`, not even `Student`, can touch it directly. It's only reachable through `User`'s own methods: `userId()` (prints it) and `changeUserid()` (sets it). That's why `st.userId()` and `st.changeUserid(12)` work in `Client.java` — they go through `User`'s methods.
- `Student.userid` is a totally different field (default access, starts at `1000`). `st.studentUserId()` prints *that* one. The commented-out line `st.userid=1;` is left there as a flag: it's a reminder that `st.userid` on its own is ambiguous-looking but Java always resolves it to `Student`'s own field (the one closer to the object's declared type), never reaching into `User`'s private one.
- Same story with `name`: `User.name = "shayan"`, `Student.name` is a fresh field. `st.name="xyz"` then later `st.name="crazy"` only ever touches `Student`'s own copy. When you print `st.name` directly, you get `Student`'s copy: `"crazy"`. But when `st.printName()` runs — that method is written *inside* `User`, so it prints `User`'s own `name` field: `"shayan"`, completely unaffected by anything `Student` did.

**Lesson:** fields don't get "overridden" the way methods do. If parent and child both declare a field with the same name, they're two separate slots in memory. Which one you see depends on *where the code reading it lives* (inside `User` vs inside `Student`), not on the actual object.

## `private` blocks inheritance-level access, not existence

`User.printOk()` is private — `Student` can't call it, `Client` can't call it. It still exists in memory as part of every `User`/`Student` object, it's just unreachable from outside `User`.

## The takeaway
- `extends` = reuse fields/methods, model an "is-a" relationship.
- `private` in the parent = truly hidden, not even subclasses can touch it directly.
- Same-named fields in parent and child = two different fields, resolved by which class's code you're standing in, not by the object's real type. (Methods behave differently — see [[polymorphism]].)
