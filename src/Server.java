import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

    static ArrayList<Card> cardList = new ArrayList<Card>();
    static ArrayList<InetAddress> redPlayers = new ArrayList<InetAddress>();
    static ArrayList<InetAddress> bluePlayers = new ArrayList<InetAddress>();

    public static void main(String[] args) {
        System.out.println("Server starting...");

        Thread pingThead = new Thread(new Runnable() {
            @Override
            public void run() {
                try (DatagramSocket socket = new DatagramSocket(Client.pingPort)) {
                    System.out.println("Thread 1 is running. Waiting for ping requests...");
        
                    while (true) {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(receivePacket);
        
                        InetAddress clientAddress = receivePacket.getAddress();
                        int clientPort = receivePacket.getPort();
        
                        System.out.println("Received ping request from: " + clientAddress.getHostAddress() + ":" + clientPort);
        
                        // Respond to the ping
                        byte[] responseData = "Pong".getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                        socket.send(sendPacket);
        
                        System.out.println("Sent pong response to:      "
                            + clientAddress.getHostAddress()
                            + ":" + clientPort
                        );

                        if (redPlayers.size() < bluePlayers.size()) {
                            redPlayers.add(clientAddress);
                        }
                        else {
                            bluePlayers.add(clientAddress);
                        }

                        System.out.println(bluePlayers.get(0));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread recieveCardThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (ServerSocket serverSocket = new ServerSocket(Client.sendCardPort)) {
                    System.out.println("Thread 2 is running. Waiting to recieve cards...");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        InetAddress playerAddress = serverSocket.getInetAddress();
                        ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

                        // Receive the `Card` object from the client
                        Card recievedCard = (Card) objectInputStream.readObject();

                        boolean redContainsAddress = false;
                        for (InetAddress address : redPlayers) {
                            if (address.equals(playerAddress)) {
                                redContainsAddress = true;
                                break;
                            }
                        }
                        if (redContainsAddress && (recievedCard.posY > Client.windowHeight/2)) {
                            cardList.add(recievedCard);
                        }
                        if (!redContainsAddress && !(recievedCard.posY > Client.windowHeight/2)) {
                            cardList.add(recievedCard);
                        }

                        // Process the received Card object
                        System.out.println("Received Card object from client:");
                        System.out.println("Card Name: " + recievedCard.name);
                    }            
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread returnCardListThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (ServerSocket serverSocket = new ServerSocket(Client.requestCardsPort)) {
                    System.out.println("Thread 3 is running. Waiting for card list requests...");

                    while (true) {
                        try (Socket clientSocket = serverSocket.accept();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {

                            // Send the `Card` list to the client
                            objectOutputStream.writeObject(cardList);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Startup

        // Set up the king towers
        Card kingTowerBlue = new Card("King tower", Client.windowWidth/2, 50);
        kingTowerBlue.fromRedTeam = false;
        Card kingTowerRed = new Card("King tower", Client.windowWidth/2, Client.windowHeight-100);
        kingTowerRed.fromRedTeam = true;
        cardList.add(kingTowerBlue);
        cardList.add(kingTowerRed);


        pingThead.start();
        recieveCardThread.start();
        returnCardListThread.start();

        while (true) {
            for (Card card : cardList) {
                ;
            }
        }

    }
}

