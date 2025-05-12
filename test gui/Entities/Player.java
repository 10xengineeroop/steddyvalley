package Entities;
import utama.*;
import java.awt.* ;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        solidArea = new Rectangle(8, 16, 32, 32); // Set the size of the collision area
        solidAreaDefaultX = solidArea.x; // Set the default X position of the collision area
        solidAreaDefaultY = solidArea.y; // Set the default Y position of the collision area
        setDefaultValues(); // Set default values for player attributes
        getPlayerImage();
    }
    public void setDefaultValues() {
        x = 200;
        y = 200;
        speed = 4;
        direction = "down"; // Default direction
    }
    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/asset/player/boy_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/asset/player/boy_up_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/asset/player/boy_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/asset/player/boy_down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/asset/player/boy_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/asset/player/boy_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/asset/player/boy_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/asset/player/boy_right_2.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void update() {
        if (keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {
            if (keyH.upPressed == true) {
                direction = "up";
            }
            if (keyH.downPressed == true) {
                direction = "down";

            }
            if (keyH.leftPressed == true) {
                direction = "left";

            }
            if (keyH.rightPressed == true) {
                direction = "right";

            }
        } 

        collisionOn = false; // Reset collision status
        gp.cChecker.checkTile(this); // Check for collision with tiles
        int objIndex = gp.cChecker.checkObject(this, true); // Check for collision with objects
        
        if (collisionOn == false) {
            if (keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) { // If no collision, update position
                switch (direction) {
                    case "up":
                        y -= speed;
                        //prevent continuous movement
                        
                        break;
                    case "down":
                        y += speed;
                        break;
                    case "left":
                        x -= speed;
                        break;
                    case "right":
                        x += speed;
                        break;
                }
            }
        }
        spriteCounter++;
        if (spriteCounter > 10) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void objectAction(int i) {
        if (i != 999) {
            String objectName = gp.obj[i].name;
            switch (objectName) {
                case "Door":
                    if (gp.gameState == gp.playState) {
                        gp.gameState = gp.houseState ;
                    }
                    break;
                case "Chest":
                    // Do something with the chest
                    System.out.println("You opened the chest!");
                    break;
                
            }
        }
    }
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                }
                if (spriteNum == 2) {
                    image = up2;
                }
                break;
            case "down":
                if (spriteNum == 1) {
                    image = down1;
                }
                if (spriteNum == 2) {
                    image = down2;
                }
                break;
            case "left":
                if (spriteNum == 1) {
                    image = left1;
                }
                if (spriteNum == 2) {
                    image = left2;
                }
                break;
            case "right":
                if (spriteNum == 1) {
                    image = right1;
                }
                if (spriteNum == 2) {
                    image = right2;
                }
                break;
        }

        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }
}
