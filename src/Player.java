import java.awt.*;
import java.io.Serializable;
/*
 * Class for players
 */
public class Player implements Serializable {

  static int width = 50;
  static int windowWidth = 1000;
  static int height = 50;
  float posX;
  float posY;
  float speed;
  short health;
  short maxHealth;


  /*
   * Sets up directions, so that game knows when player moves left or right, makes the movement less bugged (basically instead of moving like "a  aaaaaa" it moves like "aaaaaaaa")
   */
  public enum Direction {
      LEFT, RIGHT, NONE
  }
  private Direction direction = Direction.NONE; // Default to no movement when game launches or idk
  public void setDirection(Direction direction) {
      this.direction = direction;
      System.out.println("Player direction set to: " + direction); // Debug print, leave it here for now
  }

  /*
   * Basic attack mechanism, press mouse button 1 to flash green the square (change how it works later)
   */
  private Color color = new Color(0, 0, 0); // Default color is black
  private Color attackColor = new Color(0, 255, 0); // Attack color (green)
  private Color healthBarColor = new Color(255, 0, 0); // Default health bar color (red)
  private long attackStartTime;
  private long attackDuration = 500; // in milliseconds

  public void attack() {
    color = attackColor;
    attackStartTime = System.currentTimeMillis();
  }

  public void updateColor() {
    if (System.currentTimeMillis() - attackStartTime > attackDuration) {
        color = new Color(0, 0, 0); // Reset to default color after the attack duration
    }
  }

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


    this.speed = 10f; // for testing, 10 is ok
  }



  
  /*
   * Draws a card using the `Graphics` given as parameter.
   */
  public void drawPlayer(Graphics g) {
    g.setColor(color);
    g.fillRect((int) posX, (int) posY, width, height); // Draw a rectangle
    g.setColor(healthBarColor);
    g.fillRect((int) posX, (int) posY - 20, (this.health/this.maxHealth)*width, 10); // Draw a healthbar
  }

  public void damage(short damage) {
    this.health -= damage;
  }


  /*
   * THE function that makes the player move in x direction when a or d is pressed
   */
  public void updatePosition(double deltaTime) {
    if (direction == Direction.LEFT) {
      posX -= speed;
    } else if (direction == Direction.RIGHT) {
      posX += speed;
    }

    // later when jump is added just add another field here to make square go boing boing
    // very basic and shitty movement mechanism, it is very snappy but we change this later after thinking out multiplayer
  }
} 
