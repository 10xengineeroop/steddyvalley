module com.oop10x.steddyvalley {
    // requires javafx.controls;
    // requires javafx.fxml;
    requires java.desktop;


    opens com.oop10x.steddyvalley to javafx.fxml;
    exports com.oop10x.steddyvalley;
}