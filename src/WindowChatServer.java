import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class WindowChatServer {
    private ArrayList<PrintWriter> clientOutputStreams;
    private ServerSocket serverSocket;

    public class ClientHandler implements Runnable {
        InputStreamReader inputReader;
        BufferedReader bufferedReader;
        Socket clientSocket;

        public ClientHandler(Socket socket) {
            try {
                clientSocket = socket;
                inputReader = new InputStreamReader(clientSocket.getInputStream());
                bufferedReader = new BufferedReader(inputReader);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println(message);
                    printMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {
        WindowChatServer server = new WindowChatServer();
        server.setUpServer();
    }

    public void setUpServer() {
        clientOutputStreams = new ArrayList<PrintWriter>();

        try {
            serverSocket = new ServerSocket(5000);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                new Thread(new ClientHandler(clientSocket)).start();
                System.out.println("Connection established");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printMessage(String s) {
        Iterator<PrintWriter> it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(s);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

