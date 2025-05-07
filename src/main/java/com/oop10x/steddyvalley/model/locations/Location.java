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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;
        return name.equals(location.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
