package oops.methodOverriding;

//method-overriding is a run time polymorphism
public class Main {
    public static void main(String[] args) {
        Child child = new Child();
        child.print(); //o/p -> printed from child (parent print fx overriden by child print fx)
    }
}
