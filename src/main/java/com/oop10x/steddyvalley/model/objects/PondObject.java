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
        // Logika saat pemain berinteraksi dengan rumah (misalnya, masuk rumah)
        // Ini akan dipanggil oleh GameController jika pemain berinteraksi di sebelah rumah
        System.out.println("Player interacted with the House!");
        // Contoh: gameStateModel.setCurrentState(GameState.INSIDE_HOUSE_STATE);
    }
}
