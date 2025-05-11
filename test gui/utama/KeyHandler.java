package utama;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
    
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            // Move up
            upPressed = true;
        } else if (code == KeyEvent.VK_S) {
            // Move down
            downPressed = true;
        } else if (code == KeyEvent.VK_A) {
            // Move left
            leftPressed = true;
        } else if (code == KeyEvent.VK_D) {
            // Move right
            rightPressed = true;
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
