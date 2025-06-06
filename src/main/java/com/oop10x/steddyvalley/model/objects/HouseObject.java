package com.oop10x.steddyvalley.model.objects;

import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.map.Actionable;

public class HouseObject extends DeployedObject implements Actionable {
    public static final int HOUSE_WIDTH = 6;
    public static final int HOUSE_HEIGHT = 6;

    public HouseObject(int tileX, int tileY) {
        super(tileX, tileY, HOUSE_WIDTH, HOUSE_HEIGHT, "House", true);
    }

    @Override
    public void onPlayerAction(Player player) {
        System.out.println("Player interacted with the House!");
        
    }
}
