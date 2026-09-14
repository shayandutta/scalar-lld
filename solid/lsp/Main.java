package solid.lsp;

public class Main {
    public static void main(String[] args) {
        // Notification notification=new Notification();
        // Notification notification=new WpNotification();
        Notification notification=new TextNotification();
        notification.sendNotification();
    }
}
