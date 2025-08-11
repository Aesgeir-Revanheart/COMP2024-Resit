package com.example.demo.level;

import javafx.geometry.Bounds;
import com.example.demo.view.GameOverImage;
import com.example.demo.view.HeartDisplay;
import com.example.demo.view.WinImage;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;


public class LevelView {
	
	private static final double HEART_DISPLAY_X_POSITION = 5;
	private static final double HEART_DISPLAY_Y_POSITION = 25;
	private static final int WIN_IMAGE_X_POSITION = 355;
	private static final int WIN_IMAGE_Y_POSITION = 175;
	private static final int LOSS_SCREEN_X_POSITION = -0;
	private static final int LOSS_SCREEN_Y_POSITION = -0;
	private static final double LOSS_WIDTH_FRAC = 0.45;  // 55% of scene width
	private static final double LOSS_OFFSET_Y   = -0.08; // up 8% of scene height
	private static final double LOSS_OFFSET_X   = -0.11; // left 11% of scene width
	private final Group root;
	private final WinImage winImage;
	private final GameOverImage gameOverImage;
	private final HeartDisplay heartDisplay;
	private Group winWrapper;
	private Group loseWrapper;
	private javafx.scene.layout.StackPane lossOverlay;


	public LevelView(Group root, int heartsToDisplay) {
		this.root = root;
		this.heartDisplay = new HeartDisplay(HEART_DISPLAY_X_POSITION, HEART_DISPLAY_Y_POSITION, heartsToDisplay);
		this.winImage = new WinImage(WIN_IMAGE_X_POSITION, WIN_IMAGE_Y_POSITION);
		this.gameOverImage = new GameOverImage(LOSS_SCREEN_X_POSITION, LOSS_SCREEN_Y_POSITION);
	}
	
	public void showHeartDisplay() {
		root.getChildren().add(heartDisplay.getContainer());
	}

	public void showWinImage() {
		root.getChildren().add(winImage);
		winImage.showWinImage();
	}

	public void showGameOverImage() {
		// Make sure nothing old is showing
		root.getChildren().remove(gameOverImage);
		if (lossOverlay != null) root.getChildren().remove(lossOverlay);

		double sceneW = root.getScene().getWidth();
		double sceneH = root.getScene().getHeight();

		// Load the PNG directly
		Image raw = new Image(getClass()
				.getResource("/com/example/demo/images/gameover.png")
				.toExternalForm());

		// Trim transparent padding
		Image trimmed = trimTransparentBorder(raw, 8);

		// Full-screen overlay that centers its child
		lossOverlay = new StackPane();
		lossOverlay.setPickOnBounds(false);
		lossOverlay.setMouseTransparent(true);
		lossOverlay.setPrefSize(sceneW, sceneH);
		lossOverlay.setLayoutX(0);
		lossOverlay.setLayoutY(0);

		ImageView iv = new ImageView(trimmed);
		iv.setPreserveRatio(true);
		iv.setFitWidth(sceneW * LOSS_WIDTH_FRAC);
		iv.setTranslateY(sceneH * LOSS_OFFSET_Y);
		iv.setTranslateX(sceneW * LOSS_OFFSET_X);

		lossOverlay.getChildren().add(iv);
		StackPane.setAlignment(iv, Pos.CENTER);

		root.getChildren().add(lossOverlay);
	}


	//Scale the node to a fraction of the scene width and center it.
	private void centerAndScale(javafx.scene.Node node, double screenWidthFraction) {

		double sceneW = node.getScene().getWidth();
		double sceneH = node.getScene().getHeight();

		// Intrinsic bounds before scaling
		Bounds b = node.getBoundsInLocal();

		double targetW = sceneW * screenWidthFraction;
		double scale   = targetW / Math.max(1.0, b.getWidth());

		node.setScaleX(scale);
		node.setScaleY(scale);

		double w = b.getWidth()  * scale;
		double h = b.getHeight() * scale;

		// Offset for non-zero minX/minY so it truly centers
		double offsetX = -b.getMinX() * scale;
		double offsetY = -b.getMinY() * scale;

		node.setLayoutX((sceneW - w) / 2.0 + offsetX);
		node.setLayoutY((sceneH - h) / 2.0 + offsetY);

	}


	// ... end of centerAndScale(...)

	private Image trimTransparentBorder(Image src, int alphaThreshold) {
		PixelReader pr = src.getPixelReader();
		if (pr == null) return src;

		int w = (int)Math.round(src.getWidth());
		int h = (int)Math.round(src.getHeight());
		int minX = w, minY = h, maxX = -1, maxY = -1;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int a = (pr.getArgb(x, y) >>> 24) & 0xFF;
				if (a > alphaThreshold) {
					if (x < minX) minX = x;
					if (y < minY) minY = y;
					if (x > maxX) maxX = x;
					if (y > maxY) maxY = y;
				}
			}
		}
		if (maxX < minX || maxY < minY) return src;
		return new WritableImage(pr, minX, minY, (maxX - minX + 1), (maxY - minY + 1));
	}


	public void removeHearts(int heartsRemaining) {
		int currentNumberOfHearts = heartDisplay.getContainer().getChildren().size();
		for (int i = 0; i < currentNumberOfHearts - heartsRemaining; i++) {
			heartDisplay.removeHeart();
		}
	}

}
