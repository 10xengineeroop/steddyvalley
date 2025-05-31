package com.oop10x.steddyvalley.model.locations;

import com.oop10x.steddyvalley.model.Player;

public class Ocean extends Location {
    public Ocean() {
        super("Ocean");
    }
    @Override
    public void handleVisit(Player player) {
        System.out.println("You are at the Ocean. You can fish here!");
    }
}
