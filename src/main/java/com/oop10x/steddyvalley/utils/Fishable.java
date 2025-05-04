package com.oop10x.steddyvalley.utils;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.items.Fish;

public interface Fishable {

    public void Fish (Player player)  ;
    public boolean isFishable(Player player);
}
