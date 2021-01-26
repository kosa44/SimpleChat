import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class WindowChatClient {
    private PrintWriter writer;
    private BufferedReader bufferedReader;
    private String ip;
    private int port;
    private String message;
    private String name;

    private JButton sendButton;
    private JTextArea incoming;
    private JTextArea outgoing;

    public WindowChatClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connectWithServer() {
        try {
            Socket socket = new Socket(ip, port);
            InputStreamReader reader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(reader);
            writer = new PrintWriter(socket.getOutputStream());
            writer.println(name + " connected");
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void send(String s) {
        try {
            message = s;
            writer.println(name + ": " + message);
            writer.flush();
            message = "";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String setName() {
        Input input = new Input();
        name = input.getUserInput();
        return name;
    }

    public void setUpGUI() {
        JFrame frame = new JFrame("Chat");
        JPanel panel = new JPanel();
        incoming = new JTextArea(10, 10);
        incoming.setLineWrap(true);
        outgoing = new JTextArea(10, 10);
        outgoing.setLineWrap(true);
        incoming.setEditable(false);
        JScrollPane incomingScroll = new JScrollPane(incoming);
        incomingScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        incomingScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane outgoingScroll = new JScrollPane(outgoing);
        outgoingScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outgoingScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JMenuBar bar = new JMenuBar();
        bar.setVisible(true);
        JMenu menu = new JMenu("Menu");
        bar.add(menu);
        JMenuItem showActiveUsers = new JMenuItem("Show active users");
        menu.add(showActiveUsers);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        sendButton.setMnemonic(KeyEvent.VK_ENTER);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setJMenuBar(bar);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(incomingScroll);
        panel.add(outgoingScroll);
        panel.add(sendButton);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Thread thread = new Thread(new IncomingReader());
        thread.start();

        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    class SendButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            message = outgoing.getText();
            send(message);
            outgoing.setText("");
            sendButton.setEnabled(true);
        }

    }

    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println("read " + message);
                    incoming.append(message + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}