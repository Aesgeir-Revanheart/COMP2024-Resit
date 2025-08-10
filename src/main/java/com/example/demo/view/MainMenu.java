package com.example.demo.view;

import com.example.demo.controller.Controller;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainMenu {
    private final Scene scene;

    public MainMenu(Stage stage) {
        Pane root = new Pane();
        root.setPrefSize(stage.getWidth(), stage.getHeight());

        // Set Main Menu background
        ImageView background = new ImageView(new Image(getClass().getResource("/com/example/demo/images/mainmenu.png").toExternalForm()));
        background.setFitWidth(stage.getWidth());
        background.setFitHeight(stage.getHeight());

        // Title (Sky Battle)
        ImageView title = new ImageView(new Image(getClass().getResource("/com/example/demo/images/skybattle.png").toExternalForm()));
        title.setFitWidth(500); // Slightly smaller width
        title.setPreserveRatio(true);
        title.setLayoutX(stage.getWidth() / 2 - title.getFitWidth() / 2); // Centered horizontally
        title.setLayoutY(20); // Adjusted higher

        // Play Button
        ImageView playButton = new ImageView(new Image(getClass().getResource("/com/example/demo/images/play.png").toExternalForm()));
        playButton.setFitWidth(150);
        playButton.setFitHeight(150);
        playButton.setLayoutX(stage.getWidth() / 2 - playButton.getFitWidth() / 2 - 200); // Left position
        playButton.setLayoutY(500); // Lowered

        playButton.setOnMouseClicked(e -> {
            try {
                new Controller(stage).startLevelOne();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Level Selection Button
        ImageView levelSelectButton = new ImageView(new Image(getClass().getResource("/com/example/demo/images/levelselection.png").toExternalForm()));
        levelSelectButton.setFitWidth(150);
        levelSelectButton.setFitHeight(150);
        levelSelectButton.setLayoutX(stage.getWidth() / 2 - levelSelectButton.getFitWidth() / 2); // Centered
        levelSelectButton.setLayoutY(500); // Lowered

        levelSelectButton.setOnMouseClicked(e -> {
            LevelSelection levelSelection = new LevelSelection(stage);
            stage.setScene(levelSelection.getScene());
        });

        // Quit Button
        ImageView quitButton = new ImageView(new Image(getClass().getResource("/com/example/demo/images/quit.png").toExternalForm()));
        quitButton.setFitWidth(150);
        quitButton.setFitHeight(150);
        quitButton.setLayoutX(stage.getWidth() / 2 - quitButton.getFitWidth() / 2 + 200); // Right position
        quitButton.setLayoutY(500); // Lowered

        quitButton.setOnMouseClicked(e -> {
            stage.close();
        });

        // Add all elements to the root pane
        root.getChildren().addAll(background, title, playButton, levelSelectButton, quitButton);

        // Set up the scene
        this.scene = new Scene(root);
    }

    public Scene getScene() {
        return scene;
    }
}
