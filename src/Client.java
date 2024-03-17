import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.*;
/*
 * STEP 1:
 * Ping the server. If a ping is returned, call the game() function.
 * STEP 2:
 * game() function is called, start the game loop.
 */
public class Client {

  static int windowHeight = 500;
  static int windowWidth = 900;

  static String serverIP = "";  // Empty string for server IP, gets updated after successful ping.
  static int pingPort = 12345;  // Port for pinging the server.

  static ArrayList<Player> playerList = new ArrayList<Player>();  // List of players (Returned by server, drawn by client)

  public static void main(String[] args) {

    // Set up a menu for entering the server's IP.
    JFrame ipEntryFrame = new JFrame("IP Entry Menu");
    ipEntryFrame.setSize(500, 200);
    ipEntryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ipEntryFrame.setLayout(new BorderLayout());
    JTextField ipField = new JTextField();
    ipField.setText("localhost"); //sets localhost as default, easier for testing
    JButton connectButton = new JButton("Connect");
    ipEntryFrame.add(ipField, BorderLayout.CENTER);
    ipEntryFrame.add(connectButton, BorderLayout.SOUTH);
    connectButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String ipAddress = ipField.getText().trim();

        if (!ipAddress.isEmpty()) {
          try {
            // PING THE SERVER
            if (pingServer(ipAddress)) {
              serverIP = ipAddress;   // The server responded, ipdate the static IP variable
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

  /*
   * Function that pings the server, returns a boolean
   * True -> Ping successful
   */
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

  /*
   * Main game function
   */
  static void game() {

    JFrame frame = new JFrame("smash of the hog rida ");

    // No idea how this works, but it paints everything in playerList using
    // the Player class' drawPlayer() function.
    JPanel gamePanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Player player : playerList) {
          player.drawPlayer(g);
        }
      }
    };
    gamePanel.setLayout(null);
    // Set BorderLayout for the frame
    frame.setLayout(new BorderLayout());
    frame.setResizable(false);
    // Add the panel to the center of the frame
    frame.add(gamePanel, BorderLayout.CENTER);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(windowWidth, windowHeight);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    gamePanel.requestFocusInWindow();

    float initialX = windowWidth / 2; 
    float initialY = 200; // initial player positions

    Player player = new Player(initialX, initialY);
    playerList.add(player); // Add the player to the player list


    /*
     * The listeners check if a certain key or mouse button is pressed, and if it is released
     */
    gamePanel.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed: " + e.getKeyCode()); // Debug print, leave it for now, 65 is A and 68 is D i guess
        if (e.getKeyCode() == KeyEvent.VK_A) {
          playerList.get(0).setDirection(Player.Direction.LEFT); // Set direction to left
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
          playerList.get(0).setDirection(Player.Direction.RIGHT); // Set direction to right
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        System.out.println("Key released: " + e.getKeyCode()); // Debug print, leave it for now
        if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
          playerList.get(0).setDirection(Player.Direction.NONE); // Stop moving
        }
      }
    });

    gamePanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
          if (e.getButton() == MouseEvent.BUTTON1) {
              playerList.get(0).attack(); // Assuming the local player (for now), at position 0 in the player list is going to attack
          }
      }
  });

    // MAIN GAME LOOP
    Timer timer = new Timer(33, new ActionListener() { // roughly 30fps
      LocalTime timeAtFrameEnd = LocalTime.now();
      @Override
      public void actionPerformed(ActionEvent e) {
        LocalTime timeAtFrameStart = LocalTime.now();
        Duration duration = Duration.between(timeAtFrameStart, timeAtFrameEnd);
        double deltaTime = (-1 * duration.toNanos()) / 1_000_000_000.0; // to make deltatime positive and not fuck up everything else
        System.out.println("Delta time: " + deltaTime); // Debug print, leave it for now
        // deltaTime = time between frames

        timeAtFrameEnd = LocalTime.now();
        gamePanel.repaint(); // Repaint the panel to update the visuals

        for (Player player : playerList) {
          player.updateColor(); // Update the player's color based on the attack state
          player.updatePosition(deltaTime);
        }
      }
    });
    timer.start();
  }
}
