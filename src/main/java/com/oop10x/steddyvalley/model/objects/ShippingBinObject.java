package com.oop10x.steddyvalley.model.objects; // Sesuaikan dengan paket Anda

import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.map.Actionable;
// Impor lain yang mungkin diperlukan, misalnya untuk item atau inventory UI

public class ShippingBinObject extends DeployedObject implements Actionable {

    // Definisikan ukuran standar untuk Shipping Bin
    public static final int BIN_WIDTH_IN_TILES = 3;
    public static final int BIN_HEIGHT_IN_TILES = 2;

    // Mungkin ada batasan berapa kali bisa digunakan per hari, atau kapasitas
    // private boolean usedToday = false;

    public ShippingBinObject(int tileX, int tileY) {
        super(tileX,         // Posisi X kiri atas (dalam tile)
              tileY,         // Posisi Y kiri atas (dalam tile)
              BIN_WIDTH_IN_TILES,
              BIN_HEIGHT_IN_TILES,
              "Shipping Bin",// Nama objek
              true);         // Shipping Bin biasanya solid (pemain tidak bisa melewatinya)
    }

    /**
     * Metode yang dipanggil ketika pemain berinteraksi dengan Shipping Bin.
     * Ini akan dipanggil oleh GameController.
     * @param player Pemain yang berinteraksi.
     */
    @Override
    public void onPlayerAction(Player player) {
        System.out.println("Player interacted with the Shipping Bin!");

        // Logika inti untuk Shipping Bin:
        // 1. Buka UI Penjualan (ini akan mengubah GameState).
        // 2. Pemain memilih item dari inventory untuk dijual.
        // 3. Item dipindahkan dari inventory pemain ke "slot" shipping bin.
        // 4. Di akhir hari (event NEWDAY), item di shipping bin "terjual" dan gold pemain bertambah.

        // Untuk implementasi sederhana saat ini, kita bisa:
        // - Mengubah GameState ke SHIPPING_BIN_STATE (perlu ditambahkan di GameState.java)
        // - Mencetak pesan bahwa bin digunakan.

        // Contoh jika Anda memiliki GameState dan ingin membuka UI khusus:
        // GameState gameState = GameController.getGameStateModel(); // Perlu cara untuk akses GameState
        // if (gameState != null) {
        //     gameState.setCurrentState(GameState.SHIPPING_UI_STATE); // Anda perlu state ini
        // }

        // Atau, jika interaksi langsung memproses sesuatu (kurang umum untuk bin):
        // if (!usedToday) {
        //     System.out.println("Opening shipping bin interface...");
        //     // Logika untuk menampilkan UI penjualan dan memproses item
        //     // Misalnya, ambil item pertama dari inventory pemain dan "jual"
        //     Item itemToSell = player.getInventory().getFirstSellableItem();
        //     if (itemToSell != null) {
        //         int sellPrice = calculateSellPrice(itemToSell); // Anda perlu logika ini
        //         player.getInventory().removeItem(itemToSell, 1);
        //         // Gold tidak langsung ditambahkan, tapi dicatat untuk akhir hari
        //         System.out.println("Placed " + itemToSell.getName() + " in shipping bin. Value: " + sellPrice + "g");
        //         // recordSale(itemToSell, sellPrice);
        //         this.usedToday = true; // Hanya bisa digunakan sekali sehari (contoh aturan)
        //     } else {
        //         System.out.println("Player has nothing to sell.");
        //     }
        // } else {
        //     System.out.println("Shipping bin has already been used today.");
        // }
    }

    /**
     * Dipanggil saat hari baru dimulai (misalnya, oleh TimeManager melalui FarmMap atau GameController).
     * Di sinilah item yang ada di bin akan "terjual".
     */
    public void processEndOfDaySales(Player player) {
        // TODO: Implementasikan logika untuk menyimpan item yang dimasukkan ke bin selama sehari.
        // Misal, ShippingBinObject punya List<Item> itemsInBin.
        // int totalSaleValue = 0;
        // for (Item item : itemsInBin) {
        //     totalSaleValue += calculateSellPrice(item);
        // }
        // player.addGold(totalSaleValue);
        // itemsInBin.clear();
        // this.usedToday = false; // Reset untuk hari berikutnya
        // System.out.println("End of day: Sold items from bin for " + totalSaleValue + "g.");
    }

    // Metode helper (contoh)
    // private int calculateSellPrice(Item item) {
    //     // Ambil harga jual dari item atau dari daftar harga global
    //     return item.getSellPrice(); // Asumsi Item punya metode getSellPrice()
    // }
}
