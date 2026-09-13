package oops.Inheritance;

/**
 * Student
 */
public class Student extends User {

    double psp;
    String batch;
    int userid=1000;
    String name;
    
    void pauseBatch(){
        System.out.println("batch paused");
        batch=null;
    }

    void changeName(String newName){
        System.out.println("old name is");
        System.out.println(name);
        System.out.println("new name is ");
        name=newName;
        System.out.println(name);
    }

    void studentUserId(){
        System.out.println("the students separate userid is: " + userid);
    }

    void doSomething(){

    }

}