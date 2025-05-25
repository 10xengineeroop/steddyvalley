package com.oop10x.steddyvalley.model.locations;

import com.oop10x.steddyvalley.model.Player;

public class Store extends Location {
    public Store() {
        super("Store");
    }
    @Override
    public void handleVisit(Player player) {
        System.out.println("You are at the Store. You can buy and sell items here!");
    }
}
