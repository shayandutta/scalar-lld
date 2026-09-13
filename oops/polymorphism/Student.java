package oops.polymorphism;

public class Student extends User {
    String batch;
    int psp;
    String mentor;

    void changeBatch(){
        System.out.println("batch changed");
    }
    void changeEmail(){
        System.out.println("Changing email in student");
    }
}
