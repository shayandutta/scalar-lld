package oops.interfaces;

public class Human extends Mammal implements Omnivore{
    public void eatPlant(){
        System.out.println("human is eating a plant");
    }
        public void eatAnimal(){
        System.out.println("human is eating an animal");
    }
}
