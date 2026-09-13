package oops.interfaces;

public class Dog extends Mammal implements Herbivore{
    @Override 
    public void eatPlant(){
        System.out.println("dog is eating a plant");
    }
}
