module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    // Add these for JUnit and TestFX
   // requires org.junit.jupiter.api;
   // requires org.testfx.junit5;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo.controller;
}
