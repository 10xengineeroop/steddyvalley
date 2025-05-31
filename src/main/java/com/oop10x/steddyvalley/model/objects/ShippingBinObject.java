package com.oop10x.steddyvalley.model.objects;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.map.Actionable;

public class ShippingBinObject extends DeployedObject implements Actionable {

    public static final int BIN_WIDTH_IN_TILES = 3;
    public static final int BIN_HEIGHT_IN_TILES = 2;

    public ShippingBinObject(int tileX, int tileY) {
        super(tileX,tileY,BIN_WIDTH_IN_TILES,BIN_HEIGHT_IN_TILES,"Shipping Bin",true);
    }

    @Override
    public void onPlayerAction(Player player) {
        System.out.println("Player interacted with the Shipping Bin!");

    }

}
