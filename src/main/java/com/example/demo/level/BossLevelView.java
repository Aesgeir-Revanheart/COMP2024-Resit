package com.example.demo.level;

import com.example.demo.ShieldImage;
import javafx.scene.Group;

public class BossLevelView extends LevelView {

	private static final int SHIELD_X_POSITION = 1150;
	private static final int SHIELD_Y_POSITION = 500;
	private final Group root;
	private final ShieldImage shieldImage;

	public BossLevelView(Group root, int heartsToDisplay) {
		super(root, heartsToDisplay);
		this.root = root;
		this.shieldImage = new ShieldImage(SHIELD_X_POSITION, SHIELD_Y_POSITION);
		addImagesToRoot();
	}

	private void addImagesToRoot() {
		root.getChildren().addAll(shieldImage); // Add the shield image to the scene
	}

	public void showShield() {
		shieldImage.setVisible(true);
		shieldImage.toFront(); // Ensure the shield is rendered in front of other elements
		System.out.println("Shield is now visible and brought to the front");
	}

	public void hideShield() {
		shieldImage.setVisible(false);
		System.out.println("Shield is now hidden");
	}
}
