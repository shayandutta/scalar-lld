package oops.polymorphism;

public class Mentor extends User{
    String company;
    String description;
    double rating;

    void mentorCompany(){
        System.out.println("mentors company is"+company);
    }
    void changeEmail(){
        System.out.println("Changing email in mentor");
    }
}
