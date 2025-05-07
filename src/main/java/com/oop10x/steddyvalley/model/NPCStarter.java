package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.Fish;

import java.util.Set;

public class NPCStarter {
    public void start() {
        // TODO Finish after setting all items
        NPC mayorTadi = new NPC(
                "Mayor Tadi",
                Set.of(Fish.getFishbyName("Legend"));
        )
    }
}
