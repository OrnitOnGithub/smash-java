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
        
                        System.out.println("Received ping request from " + clientAddress.getHostAddress() + ":" + clientPort);
        
                        // Respond to the ping
                        byte[] responseData = "Pong".getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                        socket.send(sendPacket);
        
                        System.out.println("Sent pong response to " + clientAddress.getHostAddress() + ":" + clientPort);
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
                        ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

                        // Receive the `Card` object from the client
                        Card recievedCard = (Card) objectInputStream.readObject();
                        cardList.add(recievedCard);

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

        pingThead.start();
        recieveCardThread.start();
        returnCardListThread.start();
    }
}

