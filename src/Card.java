import java.awt.*;
/**
 * class for basic card.
 */
public class Card {

    String name;
    double posX;
    double posY;

    byte health;

    public Card(String name, double posX, double posY) {
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.health = 100;
    }

    public void drawCard(Graphics g) {
        int width = 50;
        int height = 50;
        Color color = Color.BLACK;
        if (this.name == Client.card2Name) {
            color = Color.GREEN;
        }
        if (this.name == Client.card3Name) {
            color = Color.GRAY;
        }
        if (this.name == Client.card4Name) {
            color = new Color(128, 0, 255);
        }
    
        g.setColor(color);
        g.fillRect((int)posX, (int)posY, width, height);
    }


}