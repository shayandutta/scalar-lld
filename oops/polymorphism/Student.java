package oops.polymorphism;

public class Student extends User {
    String batch;
    int psp;
    String mentor;

    void changeBatch(){
        System.out.println("batch changed");
    }
}
