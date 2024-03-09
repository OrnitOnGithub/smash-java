import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.*;

//  AAAA SSSS SSSS
//  A  A S    S  
//  AAAA SSSS SSSS
//  A  A    S    S
//  A  A SSSS SSSS

public class Client {

    static int windowHeight = 900;
    static int windowWidth = 500;

    // Later: Make this an ArrayList, and generate buttons accordingly
    static String card1Name = "Hog rider";
    static String card2Name = "Baby dragon";
    static String card3Name = "Cannon";
    static String card4Name = "Musketeer";
    static String selectedCard = card1Name; // Name of selected card

    static String serverIP = "";
    static int pingPort = 12345;
    static int sendCardPort = 12346;
    static int requestCardsPort = 12347;

    static ArrayList<Card> cardsToRender = new ArrayList<Card>(); // list of cards on the board

    public static void main(String[] args) {

        JFrame ipEntryFrame = new JFrame("IP Entry Menu");
        ipEntryFrame.setSize(500, 200);
        ipEntryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ipEntryFrame.setLayout(new BorderLayout());

        JTextField ipField = new JTextField();
        JButton connectButton = new JButton("Connect");

        ipEntryFrame.add(ipField, BorderLayout.CENTER);
        ipEntryFrame.add(connectButton, BorderLayout.SOUTH);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ipAddress = ipField.getText().trim();

                if (!ipAddress.isEmpty()) {
                    try {
                        // Ping the server
                        if (pingServer(ipAddress)) {
                            serverIP = ipAddress; // The server responded, ipdate the static IP variable
                            ipEntryFrame.dispose(); // Close the IP entry menu
                            game(); // Start the game

                        } else {
                            JOptionPane.showMessageDialog(
                                ipEntryFrame, "Server not reachable.",
                                "Error", JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(
                            ipEntryFrame, "Error connecting to the server.",
                            "Error", JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else {
                    // Might want to change this later to something more friendly
                    JOptionPane.showMessageDialog(
                        ipEntryFrame, "Please enter an IP address.",
                        "Error", JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        ipEntryFrame.setLocationRelativeTo(null);
        ipEntryFrame.setVisible(true);
    }

    static boolean pingServer(String ipAddress) throws IOException {
        // Dummy method to simulate sending a packet to the specified IP and checking for a response
        DatagramSocket socket = new DatagramSocket();
        byte[] data = "Ping".getBytes();
        InetAddress serverAddress = InetAddress.getByName(ipAddress);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, pingPort);
        socket.send(sendPacket);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.setSoTimeout(2000); // Timeout for response
        try {
            socket.receive(receivePacket);
            return true; // Received a response
        } catch (IOException e) {
            return false; // No response received
        } finally {
            socket.close();
        }
    }

    static ArrayList<Card> fetchCardList() throws IOException, ClassNotFoundException {
        ArrayList<Card> cards = new ArrayList<>();
    
        // Dummy method to simulate sending a packet to the specified IP and checking for a response
        try (Socket clientSocket = new Socket(serverIP, requestCardsPort);
             ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream())) {
    
            // Receive the `Card` object from the server
            ArrayList<Card> receivedCardList = (ArrayList<Card>) objectInputStream.readObject();
    
            // Process the received Card object
            System.out.println("Received Card list from server");
            cards = receivedCardList;
    
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception (log, print, or rethrow)
        }
        return cards;
    }

    static void game() {
        JFrame frame = new JFrame("Hi");
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Card card : cardsToRender) {
                    card.drawCard(g);
                }
            }
        };
        
        gamePanel.setLayout(null);


        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.println("Mouse clicked at: " + x + ", " + y);
                Card newCard = new Card(selectedCard, x, y);

                // cardsToRender.add(newCard); // Instead, send this to server

                try (Socket socket = new Socket(serverIP, sendCardPort)) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                    // Send the `Card` to the server
                    objectOutputStream.writeObject(newCard);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // gamePanel.revalidate();
                // gamePanel.repaint();

                for (Card card : cardsToRender) {
                    System.out.println(card.name + " " + card.posX+ " " + card.posY);
                }
            }
        });


        ActionListener buttonAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton sourceButton = (JButton) e.getSource();
                String buttonText = sourceButton.getText();
                selectedCard = buttonText;
                System.out.println("Selected card: " + selectedCard);
            }
        }; 
        JButton button1 = new JButton(card1Name);
        button1.addActionListener(buttonAction);
        JButton button2 = new JButton(card2Name);
        button2.addActionListener(buttonAction);
        JButton button3 = new JButton(card3Name);
        button3.addActionListener(buttonAction);
        JButton button4 = new JButton(card4Name);
        button4.addActionListener(buttonAction);


        // Set BorderLayout for the frame
        frame.setLayout(new BorderLayout());
        // Add the panel to the center of the frame
        frame.add(gamePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);
        buttonPanel.add(button4);


        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(windowWidth, windowHeight);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer(33, new ActionListener() { // roughly 30fps
            LocalTime timeAtFrameEnd = LocalTime.now();
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalTime timeAtFrameStart = LocalTime.now();
                Duration duration = Duration.between(timeAtFrameStart, timeAtFrameEnd);
                double deltaTime = duration.toNanos() / 1_000_000_000.0;

                System.out.println(deltaTime);
                /*
                int speed = 20;
                for (Card card : cardsToRender) {
                    card.posY = card.posY + speed * deltaTime;
                }
                */

                // Fetch card list from server
                try {
                    cardsToRender = fetchCardList();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                timeAtFrameEnd = LocalTime.now();
                gamePanel.repaint(); // Repaint the panel to update the visuals
            }
        });
        timer.start();
    }
}