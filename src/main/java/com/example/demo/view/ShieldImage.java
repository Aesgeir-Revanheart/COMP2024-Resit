package com.example.demo.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;

public class ShieldImage extends ImageView {

	//IMAGE_NAME to be used later (maybe)
	private static final String IMAGE_NAME = "/images/shield.png";
	private static final int SHIELD_SIZE = 200;

	public ShieldImage(double xPosition, double yPosition) {
		this.setLayoutX(xPosition);
		this.setLayoutY(yPosition);
		//this.setImage(new Image(IMAGE_NAME));
		URL shieldImageURL = getClass().getResource("/com/example/demo/images/shield.png");
		if (shieldImageURL != null) {
			this.setImage(new Image(shieldImageURL.toExternalForm()));
		} else {
			System.err.println("Shield image resource not found!");
		}

		this.setVisible(false);
		this.setFitHeight(SHIELD_SIZE);
		this.setFitWidth(SHIELD_SIZE);
	}

	public void showShield() {
		this.setVisible(true);
	}

	public void hideShield() {
		this.setVisible(false);
	}

}
