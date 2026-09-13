package oops.Inheritance;

public class Client {
    public static void main(String[] args) {
        Student st = new Student();
        st.psp=81.0;
        st.name="xyz";
        st.batch="xyz";
        // st.userid=1;

        st.pauseBatch();
        st.changeName("Dutta");
        st.userId();
        st.changeUserid(12);
        st.studentUserId();
        st.name="crazy";
        System.out.println(st.name);
        st.printName();
    }
}
