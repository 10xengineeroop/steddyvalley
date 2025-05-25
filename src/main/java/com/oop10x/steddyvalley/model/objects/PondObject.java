package com.oop10x.steddyvalley.model.objects;

import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.map.Actionable;

public class PondObject extends DeployedObject implements Actionable {
    public static final int POND_WIDTH = 4;
    public static final int POND_HEIGHT =3;

    public PondObject(int tileX, int tileY) {
        super(tileX, tileY, POND_WIDTH , POND_HEIGHT, "Pond", true);
    }

    @Override
    public void onPlayerAction(Player player) {
        // Only print debug message. Game state change is handled in GameController.
        System.out.println("Player interacted with the Pond!");
    }
}
