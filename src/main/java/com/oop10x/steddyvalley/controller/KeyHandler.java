package com.oop10x.steddyvalley.controller;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyHandler implements KeyListener{
    private PlayerInputActions inputActionsDelegate;

    public KeyHandler(PlayerInputActions inputActionsDelegate) {
        this.inputActionsDelegate = inputActionsDelegate;
    }
    public void keyTyped(KeyEvent e) {
        // Not used
    }
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                inputActionsDelegate.setMoveUp(true);
                break;
            case KeyEvent.VK_S:
                inputActionsDelegate.setMoveDown(true);
                break;
            case KeyEvent.VK_A:
                inputActionsDelegate.setMoveLeft(true);
                break;
            case KeyEvent.VK_D:
                inputActionsDelegate.setMoveRight(true);
                break;
            case KeyEvent.VK_ESCAPE:
                inputActionsDelegate.togglePause();
                break;
            case KeyEvent.VK_I:
                inputActionsDelegate.toggleInventory();
                break;
            case KeyEvent.VK_E:
                inputActionsDelegate.performPrimaryAction();
                break;
            case KeyEvent.VK_F:
                inputActionsDelegate.openShopForTesting();
                break;
        }
    }
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode() ;

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            inputActionsDelegate.setMoveUp(false);
        } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            inputActionsDelegate.setMoveDown(false);
        } else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            inputActionsDelegate.setMoveLeft(false);
        } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            inputActionsDelegate.setMoveRight(false);
        }
    }

}
