package com.oop10x.steddyvalley.view; // Atau paket 'utama' jika Anda masih menggunakan itu

import com.oop10x.steddyvalley.controller.KeyHandler; // Import KeyHandler

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;

public class GameWindow extends JFrame {

    private GamePanel gamePanel; // Referensi ke GamePanel

    public GameWindow(String title, GamePanel gamePanel, KeyHandler keyHandler) {
        super(title); // Set judul window dari constructor JFrame

        if (gamePanel == null) {
            throw new IllegalArgumentException("GamePanel cannot be null");
        }
        if (keyHandler == null) {
            throw new IllegalArgumentException("KeyHandler cannot be null");
        }

        this.gamePanel = gamePanel;

        // --- Konfigurasi JFrame ---
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Keluar aplikasi saat window ditutup
        this.setResizable(false); // Mencegah pengguna mengubah ukuran window

        // Tambahkan GamePanel ke JFrame
        this.add(gamePanel);

        // Tambahkan KeyHandler ke GamePanel
        // GamePanel harus focusable agar KeyListener berfungsi
        gamePanel.addKeyListener(keyHandler);
        // Pastikan GamePanel meminta fokus setelah window terlihat
        // Ini bisa dilakukan di sini atau setelah pack() dan setVisible()

        // Menyesuaikan ukuran window agar pas dengan preferred size dari GamePanel
        this.pack();

        this.setLocationRelativeTo(null); // Menempatkan window di tengah layar
        // this.setVisible(true); // Kita akan panggil ini dari Main setelah semua siap
    }

    /**
     * Metode untuk menampilkan window dan memulai game thread di GamePanel.
     * Ini juga memastikan GamePanel mendapatkan fokus untuk input keyboard.
     */
    public void displayAndFocus() {
        this.setVisible(true);
        // Penting: Minta fokus ke GamePanel SETELAH window visible
        // agar KeyListener dapat segera menangkap input.
        // Kadang perlu sedikit delay atau pemanggilan di Event Dispatch Thread.
        // Cara yang lebih robust:
        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
            if (!gamePanel.hasFocus()) {
                System.err.println("GamePanel could not gain focus. Keyboard input might not work.");
            }
        });
    }

    // Opsional: Tambahkan listener untuk menghentikan game thread saat window ditutup
    // Ini adalah praktik yang baik untuk membersihkan resource.
    public void addCleanShutdownHook() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (gamePanel != null) {
                    gamePanel.stopGameThread(); // Panggil metode stop di GamePanel
                }
                System.out.println("Game window closing, game thread stopped.");
            }
        });
    }
}
