package utama;

import Object.OBJ_Key;
public class AssetSetter {
    GamePanel gp ;
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }
    public void setObject() {
        gp.obj[0] = new OBJ_Key();
        gp.obj[0].worldX = 23 * gp.tileSize;
        gp.obj[0].worldY = 7 * gp.tileSize;
        
        gp.obj[1] = new OBJ_Key();
        gp.obj[1].worldX = 23 * gp.tileSize;
        gp.obj[1].worldY = 8 * gp.tileSize;
        
        gp.obj[2] = new OBJ_Key();
        gp.obj[2].worldX = 23 * gp.tileSize;
        gp.obj[2].worldY = 9 * gp.tileSize;
    }
}
