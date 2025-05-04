package com.oop10x.steddyvalley.model.locations;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.items.Item;
import java.util.HashMap;
import java.util.Map;

public class Shop extends Location {
    HashMap<String, Item> itemsForSale = new HashMap<>();
    public void addItemForSale(String name, Item item) {
        itemsForSale.put(name, item);
    }
    public Shop(String name) {
        super(name);
    }
    @Override
    public void handleVisit(Player player) {
        System.out.println("Welcome to the Shop!") ;
        System.out.println("You can buy items here.") ;
        System.out.println("Items for sale:") ;
        for (Map.Entry<String, Item> entry : itemsForSale.entrySet()) {
            String itemName = entry.getKey();
            Item item = entry.getValue();
            System.out.println(itemName + " - Price: " + item.getPrice());
        }
    }    
    public void buyItem(Player player, String itemName, int quantity) {
        if (quantity <= 0) {
            System.out.println("Invalid quantity. Please enter a positive number.");
            return;
        }
        Item item = itemsForSale.get(itemName);
        if (item == null) {
            System.out.println("Item not found in the shop.");
            return;
        }
        int totalPrice = item.getPrice() * quantity;
        if (player.getGold() < totalPrice) {
            System.out.println("You don't have enough money to buy " + quantity + " " + itemName + "(s).");
            return;
        }
        player.addItem(item, quantity);
    }
}
