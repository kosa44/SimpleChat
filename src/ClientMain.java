
public class ClientMain {
    public static void main(String[] args) {
        String ip = "127.0.0.1";
        String port = "5000";
        WindowChatClient wcc = new WindowChatClient(ip, Integer.parseInt(port));
        System.out.println("Type your name: ");

        wcc.setName();
        wcc.connectWithServer();
        wcc.setUpGUI();
    }
}

