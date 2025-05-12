package Object;
import utama.GamePanel;
import java.awt.image.BufferedImage;
import java.awt.* ;



public class SuperObject {
    public BufferedImage image;
    public String name;
    public boolean collision = false;

    public int worldX, worldY;
    public Rectangle solidArea = new Rectangle(0, 0, 32, 32); // Set the size of the collision area
    public int solidAreaDefaultX = 0; // Set the default X position of the collision area
    public int solidAreaDefaultY = 0; // Set the default Y position of the collision area
    
    public void draw(Graphics2D g2, GamePanel gp) {
        g2.drawImage(image, worldX, worldY, gp.tileSize, gp.tileSize, null);
    }
}
