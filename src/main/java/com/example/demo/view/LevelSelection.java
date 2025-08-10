package com.example.demo.view;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LevelSelection {

    private final Stage stage;
    private final Scene scene;

    public LevelSelection(Stage stage) {
        this.stage = stage;
        this.scene = initializeScene();
    }

    public Scene getScene() {
        return scene;
    }

    private Scene initializeScene() {
        Pane root = new Pane();

        // Set the background image
        ImageView background = new ImageView(new Image(getClass().getResource("/com/example/demo/images/mainmenu.png").toExternalForm()));
        background.setFitHeight(stage.getHeight());
        background.setFitWidth(stage.getWidth());
        root.getChildren().add(background);

        // Placeholder message
        // This is temporary until the thumbnails are added
        javafx.scene.text.Text placeholderText = new javafx.scene.text.Text("Level Selection Coming Soon...");
        placeholderText.setStyle("-fx-font-size: 24px; -fx-fill: white;");
        placeholderText.setX(stage.getWidth() / 2 - 150); // Center horizontally
        placeholderText.setY(stage.getHeight() / 2);      // Center vertically
        root.getChildren().add(placeholderText);

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }
}
