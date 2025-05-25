package com.oop10x.steddyvalley.model.locations;

import com.oop10x.steddyvalley.model.Player;

public class ForestRiver extends Location {
    public ForestRiver() {
        super("Forest River");
    }
    @Override
    public void handleVisit(Player player) {
        System.out.println("You are at the Forest River. You can fish here!");
    }
}
