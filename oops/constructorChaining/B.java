package oops.constructorChaining;

public class B extends A{
    private int age;
    B(int age){
        this.age=age;
    }
    void printAge(){
        System.out.println(age);
    }
}
