package com.example.demo;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainMenuTest extends ApplicationTest {

    private MainMenu mainMenu;

    @Override
    public void start(Stage stage) {
        mainMenu = new MainMenu(stage);
        Scene scene = mainMenu.getScene();
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testMainMenuSceneIsNotNull() {
        assertNotNull(mainMenu.getScene(), "MainMenu scene should not be null");
    }
}
