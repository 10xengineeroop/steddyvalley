package com.oop10x.steddyvalley.controller;

import java.awt.event.KeyListener;
import com.oop10x.steddyvalley.model.GameState;
import java.awt.event.KeyEvent;

public class KeyHandler implements KeyListener {
    private PlayerInputActions inputActionsDelegate;
    private GameController gameController; 
    private GameState gameStateModel;

    public KeyHandler(GameController controller) {
        this.inputActionsDelegate = controller;
        this.gameController = controller;
    }

    public KeyHandler(GameController controller, GameState gameState) {
        this.inputActionsDelegate = controller;
        this.gameController = controller;
        this.gameStateModel = gameState;
    }


    @Override
    public void keyTyped(KeyEvent e) {
        if (gameStateModel != null && gameController != null) { 
            if (gameStateModel.isPlayerNameInputState() || gameStateModel.isPlayerFavItemInputState()) {
                char keyChar = e.getKeyChar();
                if (Character.isLetterOrDigit(keyChar) || keyChar == KeyEvent.VK_SPACE) {
                    gameController.appendCharacterToInputBuffer(keyChar);
                    if (gameController.getGamePanel() != null) gameController.getGamePanel().repaint();
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int keyCode = e.getKeyCode();

        if (gameStateModel.isPlayerNameInputState() || gameStateModel.isPlayerFavItemInputState()) {
            if (keyCode == KeyEvent.VK_BACK_SPACE) {
                gameController.backspaceInputBuffer();
                if (gameController.getGamePanel() != null) gameController.getGamePanel().repaint();
            }
        }

        switch (keyCode) {
            case KeyEvent.VK_W:
                if (!gameStateModel.isPlayerNameInputState() && !gameStateModel.isPlayerFavItemInputState()) {
                    inputActionsDelegate.setMoveUp(true);
                }
                break;
            case KeyEvent.VK_S:
                if (!gameStateModel.isPlayerNameInputState() && !gameStateModel.isPlayerFavItemInputState()) {
                    inputActionsDelegate.setMoveDown(true);
                }
                break;
            case KeyEvent.VK_A:
                if (!gameStateModel.isPlayerNameInputState() && !gameStateModel.isPlayerFavItemInputState()) {
                    inputActionsDelegate.setMoveLeft(true);
                }
                break;
            case KeyEvent.VK_D:
                if (!gameStateModel.isPlayerNameInputState() && !gameStateModel.isPlayerFavItemInputState()) {
                    inputActionsDelegate.setMoveRight(true);
                }
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
            case KeyEvent.VK_V:
                inputActionsDelegate.toggleVisit();
                break;

            case KeyEvent.VK_UP:
                if (gameStateModel.isEndGame()) {
                    inputActionsDelegate.scrollDisplay(-1);
                } else if (!gameStateModel.isPlayerNameInputState() && !gameStateModel.isPlayerFavItemInputState()) {
                     inputActionsDelegate.setMoveUp(true);
                }
                break;
            case KeyEvent.VK_DOWN:
                 if (gameStateModel.isEndGame()) {
                    inputActionsDelegate.scrollDisplay(1);
                } else if (!gameStateModel.isPlayerNameInputState() && !gameStateModel.isPlayerFavItemInputState()) {
                    inputActionsDelegate.setMoveDown(true);
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (! (gameStateModel != null && (gameStateModel.isPlayerNameInputState() || gameStateModel.isPlayerFavItemInputState())) ) {
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
}