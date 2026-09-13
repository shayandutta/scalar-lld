package oops.interfaces;

public class Cat extends Mammal implements Carnivore{
    public void eatAnimal(){ //need to use public here? why? why not keep it default?
        System.out.println("Cat is eating animal");
    }
}
