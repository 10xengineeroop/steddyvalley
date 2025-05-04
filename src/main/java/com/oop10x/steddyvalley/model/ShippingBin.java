package com.oop10x.steddyvalley.model;
import com.oop10x.steddyvalley.utils.Placeable;
import com.oop10x.steddyvalley.utils.Position;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.utils.Sellable;
import com.oop10x.steddyvalley.model.items.Item;
import java.util.HashMap;
import java.util.Map;

public class ShippingBin implements Placeable, Sellable {
    private final Position position = new Position(0, 0);
    private Map<Item, Integer> items = new HashMap<>(); // Item and jumlah

    public ShippingBin(int x, int y) {
        setX(x);
        setY(y);
    }

    @Override
    public void setX(int x) {
        position.setX(x);
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public void setY(int y) {
        position.setY(y);
    }

    @Override
    public int getY() {
        return position.getY();
    }

    public void putItem(Item item, int quantity) {
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    @Override
    public int getSellPrice() {
        int totalPrice = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }
        return totalPrice;
    }
    //masih perlu yang buat dijual di malam hari, keanya perlu observer buat nandain waktu
    //masih perlu update uang yang didapat ke player
    
}
