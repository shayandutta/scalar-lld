package oops.methodOverriding;

public class Child extends Parent {
    //method overriding (parent also has print fxn but child class overrides the patient class)
    // int print(){ 
    //-> cant change the return type during method overriding
    //because technically Child has the Parents print method with void return type and now we are trying to change the return type to int
    // @Override (just to validate if parameters and return type match to the parent method for overriding)
    public void print(){
        super.print(); //super.methodName (if want to call method of parent(super-keyword))
        System.out.println("printed from Child");
        // return -1;
    }
}
