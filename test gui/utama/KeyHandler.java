package utama;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }
    
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        int index = gp.cChecker.checkObject(gp.player, true);
        if (code == KeyEvent.VK_W) {
            // Move up
            upPressed = true;
        }else if (code == KeyEvent.VK_S) {
            // Move down
            downPressed = true;
        }else if (code == KeyEvent.VK_A) {
            // Move left
            leftPressed = true;
        }else if (code == KeyEvent.VK_D) {
            // Move right
            rightPressed = true;
        }else if (code == KeyEvent.VK_ENTER && gp.obj[index].name.equals("Key")) {
            if (gp.gameState == gp.playState) {
                gp.gameState = gp.houseState; // Change game state to house state
            }
        }
        else if (code == KeyEvent.VK_ESCAPE && gp.gameState == gp.houseState) {
            gp.gameState = gp.playState; // Change game state to play state
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            // Stop moving up
            upPressed = false;
        } else if (code == KeyEvent.VK_S) {
            // Stop moving down
            downPressed = false;
        } else if (code == KeyEvent.VK_A) {
            // Stop moving left
            leftPressed = false;
        } else if (code == KeyEvent.VK_D) {
            // Stop moving right
            rightPressed = false;
        }
    }
    
}
