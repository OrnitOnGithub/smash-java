import java.awt.*;
import java.io.Serializable;
/**
 * Class for players
 */
public class Player implements Serializable {

  static int width = 50;
  static int height = 50;
  float posX;
  float posY;
  float speed;
  short health;
  short maxHealth;

  /*
   * This is a constructor, only called once. (on player join for instance)
   * Constructor for the `Player` object.
   * Only builds with information the client can provide.
   */
  public Player(float posX, float posY) {
    this.posX = posX - width/2;
    this.posY = posY - height/2;

    this.maxHealth = 100; // temporary

    this.health = maxHealth;

    this.speed = 0.00001f; // for testing
  }

  /*
   * Draws a card using the `Graphics` given as parameter.
   */
  public void drawPlayer(Graphics g) {
    Color healthBarColor = new Color(255, 0, 0);
    Color color = new Color(0, 0, 0); // temporary

    g.setColor(color);
    g.fillRect((int) posX, (int) posY, width, height); // Draw a rectangle (temporary)
    g.setColor(healthBarColor);
    g.fillRect((int) posX, (int) posY - 20, (this.health/this.maxHealth)*width, 10); // Draw a healthbar
  }

  public void damage(short damage) {
    this.health -= damage;
  }
}