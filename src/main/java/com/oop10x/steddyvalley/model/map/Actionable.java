package com.oop10x.steddyvalley.model.map;

import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.GameState;

public interface Actionable {
    void onPlayerAction(Player player, GameState gameState);
}
