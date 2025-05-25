package com.oop10x.steddyvalley.model.locations;

import com.oop10x.steddyvalley.model.Player;

public class MountainLake extends Location {
    public MountainLake() {
        super("Mountain Lake");
    }
    @Override
    public void handleVisit(Player player) {
        System.out.println("You are at the Mountain Lake. You can fish here!");
    }
}
