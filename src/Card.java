import java.awt.*;
import java.io.Serializable;
/**
 * class for basic card. Used both for cards played by players
 * and for things like the king towers.
 */
public class Card implements Serializable {

    static int width = 50;
    static int height = 50;

    String name;
    float posX;
    float posY;
    short health;
    short maxHealth;
    boolean fromRedTeam; // True: played by team red, False: played by team blue

    /*
     * Constructor for the `Card` object.
     * Only builds with information the client can provide.
     * Only to be used by client.
     */
    public Card(String name, float posX, float posY) {
        this.name = name;
        this.posX = posX - width/2;
        this.posY = posY - height/2;

        /*
        static String card1Name = "Hog rider";
        static String card2Name = "Baby dragon";
        static String card3Name = "Cannon";
        static String card4Name = "Musketeer";
        */

        if        (name.equals(Client.card1Name)) {
            maxHealth = 150;
        } else if (name.equals(Client.card2Name)) {
            maxHealth = 250;
        } else if (name.equals(Client.card3Name)) {
            maxHealth = 500;
        } else if (name.equals(Client.card4Name)) {
            maxHealth = 200;
        } else if (name.equals("King tower")) {
            maxHealth = 2000;
        } else {
            System.err.println("Not an existing card type");
        }
        this.health = maxHealth;
    }

    /*
     * Draws a card using the `Graphics` given as parameter.
     */
    public void drawCard(Graphics g) {
        Color healthBarColor = new Color(0, 0, 255); // Team BLUE
        if (fromRedTeam) {
            healthBarColor = new Color(255, 0, 0);   // Team RED
        }

        Color color = Color.BLACK;
        if (this.name.equals(Client.card2Name)) {
            color = Color.GREEN;
        } else if (this.name.equals(Client.card3Name)) {
            color = Color.GRAY;
        } else if (this.name.equals(Client.card4Name)) {
            color = new Color(128, 0, 255);
        } else if (this.name.equals("King tower")) {
            if (this.fromRedTeam == true) {
                color = new Color(255, 0, 0);
            } else {
                color = new Color(0, 0, 255);
            }
        }
    
        g.setColor(color);
        g.fillRect((int) posX, (int) posY, width, height);
        g.setColor(healthBarColor);
        g.fillRect((int) posX, (int) posY - 20, (this.health/this.maxHealth)*width, 10);
    }

    public void damage(short damage) {
        this.health -= damage;
    }
}