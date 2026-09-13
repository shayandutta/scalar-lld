package oops.polymorphism;

import java.util.List;


public class main {

    public static void changePasswordForAllUsers(List<User> users){
        for(User user:users){
            user.changeEmail();

            if(user instanceof Student){
                Student student=(Student) user;//typecast type to Student in a student variable
                student.changeBatch(); //changeBatch is a Student method
            }

            if(user instanceof Mentor){
                Mentor mentor=(Mentor) user;
                mentor.company="gogle";
                mentor.mentorCompany();
            }
        }
    }

    
    public static void main(String[] args) {
        //run-time polymorphism -> not the compile time, but the run-time knows which type user belongs too -> User/Student/Mentor etc type
        User user = new TA();
        User user1 = new Student();
        User user2 = new Mentor();
        User user3 = new User();

        List<User> users=List.of(
            user,
            user1,
            user2,
            // new Student(),
            new TA(),
            user3
        );

        changePasswordForAllUsers(users);
    }
}
