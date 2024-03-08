import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import java.time.*;

public class Client {

    static int windowHeight = 900;
    static int windowWidth = 500;

    static String card1Name = "Hog rider";
    static String card2Name = "Baby dragon";
    static String card3Name = "Cannon";
    static String card4Name = "Musketeer";
    static String selectedCard = card1Name; // Name of selected card

    static ArrayList<Card> playedCards = new ArrayList<Card>(); // list of cards on the board

    public static void main(String[] args) {

        mainMenu();

        game();
    }

    public static void mainMenu() {
        
    }

    public static void game() {

        JFrame frame = new JFrame("Hi");
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Card card : playedCards) {
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
                playedCards.add(newCard);

                // Refresh the panel to reflect the changes
                gamePanel.revalidate();
                gamePanel.repaint();

                for (Card card : playedCards) {
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

                int speed = 20;
                for (Card card : playedCards) {
                    card.posY = card.posY + speed * deltaTime;
                }

                timeAtFrameEnd = LocalTime.now();
                gamePanel.repaint(); // Repaint the panel to update the visuals
            }
        });
        timer.start(); // Start the timer
    }
}