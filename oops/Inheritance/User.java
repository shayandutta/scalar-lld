package oops.Inheritance;

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
}