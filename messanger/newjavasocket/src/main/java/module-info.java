module com.example.newjavasocket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    exports view;
    opens view to javafx.fxml;
    exports controller;
    opens controller to javafx.fxml;
    exports helper;
    opens helper to javafx.fxml;
}