package oops.methodOverloading;

//method overloading happens when there are two or more methods with same name
//but different signatures  (return type is not a part of signature).
//return time doesnt contribute to it.
//method-overloading is also a type of polymorphism (compile time type)
public class A {
    //compile time polymorphism -> during compile time only, the compiler knows its polymorphism here (many forms of the same function)
    
    public void print(){
        System.out.println("hello");
    }
    public void print(String name){
        System.out.println("hello "+ name);
    }

    //compile time issue (return type isnt considered in making a thing to many forms)
    // public String print(String name){
        //print("hello"+name);
    //}    
    
}
