module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens controllers to javafx.fxml;
    exports controllers;
    exports main;
    opens main to javafx.fxml;
}