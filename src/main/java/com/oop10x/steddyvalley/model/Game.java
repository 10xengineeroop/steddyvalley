package com.oop10x.steddyvalley.model;

public class Game {
    private Player player = Player.getInstance();

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
