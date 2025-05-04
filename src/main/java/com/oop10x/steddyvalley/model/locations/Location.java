package com.oop10x.steddyvalley.model.locations;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.utils.Visitable;
public abstract class Location implements Visitable{
    protected String name ;
    public Location(String name) {
        this.name = name ;
    }
    public String getName() {
        return name ;
    }
    public abstract void handleVisit(Player player) ; 

    public void onVisit (Player player) {
        handleVisit(player);
    }
}
