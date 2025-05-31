package com.oop10x.steddyvalley.model.objects;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.map.Actionable;
import com.oop10x.steddyvalley.model.ShippingBin;

public class ShippingBinObject extends DeployedObject implements Actionable {

    public static final int BIN_WIDTH_IN_TILES = 3;
    public static final int BIN_HEIGHT_IN_TILES = 2;
    private final ShippingBin logicalBin;

    public ShippingBinObject(int tileX, int tileY, ShippingBin logicalBin) {
        super(tileX,tileY,BIN_WIDTH_IN_TILES,BIN_HEIGHT_IN_TILES,"Shipping Bin",true);
        this.logicalBin = logicalBin;
    }
    public ShippingBin getLogicalBin() {
        return logicalBin;
    }

    @Override
    public void onPlayerAction(Player player) {
        System.out.println("Player interacted with the Shipping Bin!");

    }

}
