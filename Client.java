import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.TrayIcon.MessageType;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Client extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    // declare components

    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Client() {
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection done.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            startReading();
            // startWriting();
            createGUI();
            handleEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // System.out.println("Key released"+e.getKeyCode());
                if (e.getKeyCode() == 10) {
                    // System.out.println("you have pressed enter button");
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();

                }
            }
        });
    }

    private void createGUI() {
        // gui code
        this.setTitle("Client Messenger[END]");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("client.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // layout for the frame
        this.setLayout(new BorderLayout());

        // adding the components to frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);

        // code to auto scroll
        jScrollPane.getVerticalScrollBar().addAdjustmentListener(
                e -> {
                    if ((e.getAdjustable().getValue() - e.getAdjustable().getMaximum()) > -jScrollPane.getHeight()
                            - 20) {
                        e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    }
                });

        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    private void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader Started..");
            try {
                while (true) {
                    String msg;

                    msg = br.readLine();
                    if (msg.equals("exit")) {
                        // System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        System.exit(0);
                        break;

                    }
                    // System.out.println("Server: " + msg);
                    messageArea.append("Server: " + msg + "\n");

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                System.out.println("Connection closed");

            }
        };
        new Thread(r1).start();
    }

    private void startWriting() {
        // --thread will read the data
        Runnable r2 = () -> {
            System.out.println("Writer Started..");
            try {
                while (!socket.isClosed()) {

                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }

                }
            } catch (Exception e) {
                // TODO: handle exception
                // e.printStackTrace();
                System.out.println("Connection closed");
            }
        };

        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is client");
        new Client();

    }
}
