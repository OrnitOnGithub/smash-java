import java.awt.*;
import java.io.Serializable;
/**
 * class for basic card.
 */
public class Card implements Serializable {

    String name;
    float posX;
    float posY;
    byte health;

    public Card(String name, float posX, float posY) {
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.health = 100;
    }

    public void drawCard(Graphics g) {
        int width = 50;
        int height = 50;
        Color healthBarColor = new Color(255, 0 ,0);
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
        g.setColor(healthBarColor);
        g.fillRect((int)posX, (int)posY-20, this.health/2, 10);
    }
}