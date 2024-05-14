import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.BorderLayout;

class Server extends JFrame{

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    // declare components

    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Server() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connection");
            System.out.println("waiting");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            startReading();
            // startWriting();
            createGUI();
            handleEvents();

        } catch (IOException e) {
            // TODO Auto-generated catch block
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
                    if (contentToSend.equals("exit")){
                        // System.out.println("Server terminated the chat");
                        messageArea.append("Connection Closed\n");
                        messageInput.setEnabled(false);
                        try {
                            System.exit(0);
                            socket.close();
                                                    } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        
                    }


                }
            }
        });
    }

    private void createGUI() {
        // gui code
        this.setTitle("Sever Messenger[END]");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("server.png"));
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
                        // System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    // System.out.println("Client: " + msg);
                    messageArea.append("Client: "+msg+"\n");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                System.out.println(("Connection CLosed"));

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
                    while (socket.isClosed()) {

                        break;
                    }

                }
                System.out.println("Connection closed");
            } catch (Exception e) {
                // TODO: handle exception
                // e.printStackTrace();
                System.out.println(("Connection CLosed"));
            }
        };

        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is server...going to start server...");
        new Server();
    }

}