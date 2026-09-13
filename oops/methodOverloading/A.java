package oops.methodOverloading;

//method overloading happens when there are two or more methods with same name
//but different signatures 
//return time doesnt contribute to it.
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
