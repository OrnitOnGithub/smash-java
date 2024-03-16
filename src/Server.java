import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
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
    
            System.out.println("Received ping request from: "
              + clientAddress.getHostAddress()
              + ":" + clientPort);
    
            // Respond to the ping
            byte[] responseData = "Pong".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
            socket.send(sendPacket);
    
            System.out.println("Sent pong response to:      "
              + clientAddress.getHostAddress()
              + ":" + clientPort
            );
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    pingThead.start();
  }
}